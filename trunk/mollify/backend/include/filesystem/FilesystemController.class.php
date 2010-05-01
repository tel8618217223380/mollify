<?php

	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */
	 
	 require_once("include/event/EventHandler.class.php");
	 			
	 class FilesystemController {	 	
	 	const EVENT_TYPE_FILE = "filesystem";
		
		private $env;
		private $allowedUploadTypes;
		private $permissionCache = array();
		private $detailsPlugins = array();

		function __construct($env) {
			require_once("MollifyFilesystem.class.php");
			require_once("LocalFilesystem.class.php");
			require_once("FilesystemItem.class.php");
			
			$this->env = $env;
			$this->allowedUploadTypes = $env->settings()->setting('allowed_file_upload_types', TRUE);
			
			FileEvent::register($this->env->events());
		}
		
		public function initialize($request) {}

		public function registerDetailsPlugin($plugin) {
			$this->detailsPlugins[] = $plugin;
		}

		public function onSessionStarted() {
		}
		
		private function getFolders() {
			if ($this->env->configuration()->isAuthenticationRequired())
				$folderDefs = $this->env->configuration()->getUserFolders($this->env->authentication()->getUserId());
			else
				$folderDefs = $this->env->configuration()->getFolders();

			$list = array();
			
			foreach($folderDefs as $folderDef) {
				if (!isset($folderDef["name"]) and !isset($folderDef["default_name"])) {
					$this->env->session()->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Folder definition does not have a name (".$folderDef['id'].")");
				}
				if (!isset($folderDef["path"])) {
					$this->env->session()->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Folder definition does not have a path (".$folderDef['id'].")");
				}
				
				$list[$folderDef['id']] = $folderDef;
			}
			
			return $list;
		}
		
		private function createFilesystem($folderDef) {
			$id = isset($folderDef['id']) ? $folderDef['id'] : '';
			
			switch ($this->filesystemType($folderDef)) {
				case MollifyFilesystem::TYPE_LOCAL:
					return new LocalFilesystem($id, $folderDef, $this);
				default:
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid root folder definition (".$id."), type unknown");
			}
		}
		
		private function filesystemType($folderDef) {
			return MollifyFilesystem::TYPE_LOCAL;	// include type in definition when more types are supported
		}
		
		public function getSessionInfo() {
			$result = array();
			
			$result['filesystem'] = array(
				"folder_separator" => DIRECTORY_SEPARATOR,
				"max_upload_file_size" => Util::inBytes(ini_get("upload_max_filesize")),
				"max_upload_total_size" => Util::inBytes(ini_get("post_max_size")),
				"allowed_file_upload_types" => $this->allowedFileUploadTypes()
			);
			
			$result["folders"] = array();
			foreach($this->getFolders() as $id => $folderDef) {
				$result["folders"][] = array(
					"id" => $this->publicId($id),
					"name" => $folderDef['name'] != NULL ? $folderDef['name'] : $folderDef['default_name']
				);
			}

			return $result;
		}

		public function filesystemFromId($id, $assert=TRUE) {
			return $this->filesystem($this->env->configuration()->getFolder($id), $assert);
		}
		
		public function filesystem($def, $assert=TRUE) {
			$fs = $this->createFilesystem($def);
			if ($assert) $fs->assert();
			return $fs;
		}
		
		public function item($id, $nonexisting = FALSE) {
			$internalId = $this->internalId($id);
			$parts = explode(":".DIRECTORY_SEPARATOR, $internalId);
			if (count($parts) != 2) throw new ServiceException("INVALID_CONFIGURATION", "Invalid item id: ".$id);
			
			$filesystemId = $parts[0];
			$path = $parts[1];
			$folderDef = $this->env->configuration()->getFolder($filesystemId);
			
			return $this->filesystem($folderDef)->createItem($id, $path, $nonexisting);
		}
		
		public function publicId($filesystemId, $path = "") {
			return base64_encode($filesystemId.":".DIRECTORY_SEPARATOR.$path);
		}

		public function internalId($itemId) {
			return base64_decode($itemId);
		}
		
		public function assertFilesystem($folderDef) {
			$this->filesystem($folderDef, TRUE);
		}

		public function assertRights($item, $required, $desc = "Unknown action") {
			if (is_array($item)) {
				foreach($item as $i)
					$this->env->authentication()->assertRights($this->permission($i), $required, "filesystemitem ".$i->id()."/".$desc);
			} else {
				$this->env->authentication()->assertRights($this->permission($item), $required, "filesystemitem ".$item->id()."/".$desc);
			}
		}

		public function ignoredItems($filesystem, $path) {
			return array('mollify.dsc', 'mollify.uac');	//TODO get from settings and/or configuration etc
		}
		
		public function folders($parent) {
			$this->assertRights($parent, Authentication::RIGHTS_READ, "folders");
			return $parent->folders();
		}
		
		public function files($parent) {
			$this->assertRights($parent, Authentication::RIGHTS_READ, "files");
			return $parent->files();
		}

		public function details($item) {
			$this->assertRights($item, Authentication::RIGHTS_READ, "details");
			
			$details = $item->details();
			$details["description"] = $this->description($item);
			$details["permission"] = $this->permission($item);
			
			foreach($this->detailsPlugins as $p) {
				$l = $p->getItemDetails($item, $details);
				if (!$l) continue;
				
				foreach($l as $k=>$v)
					$details[$k] = $v;
			}
			return $details;
		}

		public function datetimeFormat() {
			return "YmdHis";
		}

		public function description($item) {
			return $this->env->configuration()->getItemDescription($item);
		}

		public function setDescription($item, $desc) {
			$this->assertRights($item, Authentication::RIGHTS_WRITE, "set description");
			return $this->env->configuration()->setItemDescription($item, $desc);
		}

		public function removeDescription($item) {
			$this->assertRights($item, Authentication::RIGHTS_WRITE, "remove description");
			return $this->env->configuration()->removeItemDescription($item);
		}
		
		public function permission($item) {
			if ($this->env->authentication()->isAdmin()) return Authentication::PERMISSION_VALUE_READWRITE;
			
			$permission = $this->getItemUserPermission($item);
			if (!$permission) return $this->env->authentication()->getDefaultPermission();
			return $permission;
		}
		
		public function temporaryItemPermission($item, $permission) {
			$this->permissionCache[$item->id()] = $permission;
		}
		
		private function getItemUserPermission($item) {
			if (array_key_exists($item->id(), $this->permissionCache)) {
				$permission = $this->permissionCache[$item->id()];
				Logging::logDebug("Permission cache get [".$item->id()."]=".$permission);
			} else {
				$permission = $this->env->configuration()->getItemPermission($item, $this->env->authentication()->getUserId());
				$this->permissionCache[$item->id()] = $permission;
				Logging::logDebug("Permission cache put [".$item->id()."]=".$permission);
			}
			return $permission;
		}

		public function allPermissions($item) {
			return $this->env->configuration()->getItemPermissions($item);
		}
		
		private function allowedFileUploadTypes() {
			$types = array();
			foreach ($this->allowedUploadTypes as $type) {
				$pos = strrpos($type, ".");
				if ($pos === FALSE) $types[] = $type;
				else $types[] = substr($type, $pos+1);
			}
			return $types;
		}
		
		public function rename($item, $name) {
			Logging::logDebug('rename from ['.$item->path().'] to ['.$name.']');
			$this->assertRights($item, Authentication::RIGHTS_WRITE, "rename");
			
			$to = $item->rename($name);
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->moveItemDescription($item, $to);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->moveItemPermissions($item, $to);
			
			$this->env->events()->onEvent(FileEvent::rename($item, $name));
		}

		public function copy($item, $to) {
			Logging::logDebug('copying '.$item->id()."[".$item->path().'] to ['.$to.']');
			
			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights($item, Authentication::RIGHTS_READ, "copy");
			$this->assertRights($to, Authentication::RIGHTS_WRITE, "copy");

			$to = $item->copy($to);
			$this->env->events()->onEvent(FileEvent::copy($item, $to));
		}
		
		public function copyItems($items, $to) {
			Logging::logDebug('copying '.count($items).' items');
			$this->assertRights($items, Authentication::RIGHTS_WRITE, "copy");
			
			foreach($items as $item)
				$this->copy($item, $to);
		}
		
		public function move($item, $to) {
			Logging::logDebug('moving '.$item->id()."[".$item->path().'] to ['.$to.']');

			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights($item, Authentication::RIGHTS_READ, "move");
			$this->assertRights($to, Authentication::RIGHTS_WRITE, "move");

			$to = $item->move($to);
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->moveItemDescription($item, $to);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->moveItemPermissions($item, $to);
			
			$this->env->events()->onEvent(FileEvent::move($item, $to));			
		}
		
		public function moveItems($items, $to) {
			Logging::logDebug('moving '.count($items).' items');
			$this->assertRights($items, Authentication::RIGHTS_WRITE, "move");
			
			foreach($items as $item)
				$this->move($item, $to);
		}
		
		public function delete($item) {
			Logging::logDebug('deleting ['.$item->id().']');
			
			if (!$item->isFile()) $this->env->features()->assertFeature("folder_actions");
			$this->assertRights($item, Authentication::RIGHTS_WRITE, "delete");
			
			$item->delete();
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->removeItemDescription($item);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->removeItemPermissions($item);
			
			$this->env->events()->onEvent(FileEvent::delete($item));
		}
		
		public function deleteItems($items) {
			Logging::logDebug('deleting '.count($items).' items');
			$this->assertRights($items, Authentication::RIGHTS_WRITE, "delete");
			
			foreach($items as $item)
				$this->delete($item);
		}
		
		public function createFolder($parent, $name) {
			Logging::logDebug('creating folder ['.$parent->id().'/'.$name.']');
			$this->env->features()->assertFeature("folder_actions");
			$this->assertRights($parent, Authentication::RIGHTS_WRITE, "create folder");

			$new = $parent->createFolder($name);
			$this->env->events()->onEvent(FileEvent::createFolder($new));
		}

		public function download($file, $range = NULL) {
			if (!$range)
				Logging::logDebug('download ['.$file->id().']');
			$this->assertRights($file, Authentication::RIGHTS_READ, "download");
			
			$name = $file->name();
			$size = $file->size();
			
			if ($range != NULL) {
				list($unit, $range) = explode('=', $range, 2);
				
				if ($unit == 'bytes') {
					$pos = strpos(",", $range);
					if ($pos != false) {
						if ($pos === 0) $range = NULL;
						else if ($pos >= 0) $range = substr($range, 0, $pos);
					}
				} else {
					$range = NULL;
				}
			}
			
			if ($range != NULL) {
				list($start, $end) = explode('-', $range, 2);

				$end = (empty($end)) ? ($size - 1) : min(abs(intval($end)),($size - 1));
				$start = (empty($start) || $end < abs(intval($start))) ? 0 : max(abs(intval($start)),0);
				$range = array($start, $end, $size);
				Logging::logDebug("Download range ".$start."-".$end);
			}

			if (!$range)
				$this->env->events()->onEvent(FileEvent::download($file));

			$this->env->response()->download($name, $file->extension(), $file->read($range), $size, $range);							
		}
		
		public function uploadTo($folder) {
			$this->env->features()->assertFeature("file_upload");
			$this->assertRights($folder, Authentication::RIGHTS_WRITE, "upload");
			
			if ($this->env->request()->hasParam('uploader') and $this->env->request()->param('uploader') === 'plupload') {
				require_once("plupload.php");
				plupload($folder);
				return;
			}

			if (!isset($_FILES['uploader-http']) and !isset($_FILES['uploader-flash']))
				throw new ServiceException("NO_UPLOAD_DATA");
			
			if (Logging::isDebug()) Logging::logDebug("Upload to ".$folder->id().", FILES=".Util::array2str($_FILES));
			
			// flash uploader (uploads one file at a time)
			if (isset($_FILES['uploader-flash'])) {
				$this->upload($folder, $_FILES['uploader-flash']['name'], $_FILES['uploader-flash']['tmp_name']);
				return;
			}
	
			// http
			if (isset($_FILES["file"]) && isset($_FILES["file"]["error"]) && $_FILES["file"]["error"] != UPLOAD_ERR_OK)
				throw new ServiceException("UPLOAD_FAILED", $_FILES["file"]["error"]);
					
			foreach ($_FILES['uploader-http']['name'] as $key => $value) { 
				$name = $_FILES['uploader-http']['name'][$key];
				$origin = $_FILES['uploader-http']['tmp_name'][$key];
				$this->upload($folder, $name, $origin);
			}
		}
		
		private function upload($folder, $name, $origin) {
			$target = $folder->createEmptyItem($name);
			Logging::logDebug('uploading to ['.$target.']');
			
			$src = @fopen($origin, "r");
			if (!$src)
				throw new ServiceException("SAVING_FAILED", "Failed to read uploaded data");			
			$dst = $target->write();
			
			while (!feof($src))
				fwrite($dst, fread($src, 4096));

			fclose($dst);
			fclose($src);
			unlink($origin);
			
			$this->env->events()->onEvent(FileEvent::upload($target));
		}
		
		public function downloadAsZip($items) {
			$this->env->features()->assertFeature("zip_download");
			
			if (is_array($items)) {
				$this->assertRights($items, Authentication::RIGHTS_READ, "download as zip");
				
				$zip = $this->zipper("items.zip");
				foreach($items as $item) {
					$item->addToZip($zip);
					$this->env->events()->onEvent(FileEvent::download($item));
				}
				$zip->finish();
			} else {
				$item = $items;
				$this->assertRights($item, Authentication::RIGHTS_READ, "download as zip");
				
				$zip = $this->zipper($item->name().".zip");
				$item->addToZip($zip);
				$zip->finish();
				
				$this->env->events()->onEvent(FileEvent::download($item));
			}
		}
		
		public function zipper($name) {
			require_once('MollifyZipStream.class.php');
			return new MollifyZipStream($this->env, $name, $this->setting("zip_options"));
		}
		
		public function setting($setting) {
			return $this->env->settings()->setting($setting);
		}

		public function log() {
			Logging::logDebug("FILESYSTEM: allowed_file_upload_types=".Util::array2str($this->allowedUploadTypes));
		}

	 }
	 
	 class FileEvent extends Event {
		const COPY = "copy";
		const RENAME = "rename";
		const MOVE = "move";
		const DELETE = "delete";
		const CREATE_FOLDER = "create_folder";
		const DOWNLOAD = "download";
		const UPLOAD = "upload";
		
		private $item;
		private $info;
		
		static function register($eventHandler) {
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::COPY, "Copy file");
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::RENAME, "Rename file");
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::MOVE, "Move file");
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::DELETE, "Delete file");
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::CREATE_FOLDER, "Create folder");
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::DOWNLOAD, "Download file");
			$eventHandler->registerEventType(FilesystemController::EVENT_TYPE_FILE, self::UPLOAD, "Upload file");
		}
		
		static function rename($item, $name) {
			return new FileEvent($item, self::RENAME, $name);
		}

		static function copy($item, $to) {
			return new FileEvent($item, self::COPY, $to);
		}

		static function move($item, $to) {
			return new FileEvent($item, self::MOVE, $to);
		}

		static function delete($item) {
			return new FileEvent($item, self::DELETE);
		}

		static function createFolder($folder) {
			return new FileEvent($folder, self::CREATE_FOLDER);
		}

		static function download($item) {
			return new FileEvent($item, self::DOWNLOAD);
		}

		static function upload($item) {
			return new FileEvent($item, self::UPLOAD);
		}
		
		function __construct($item, $type, $info = NULL) {
			parent::__construct(time(), FileSystemController::EVENT_TYPE_FILE, $type);
			$this->item = $item;
			$this->info = $info;
		}

		public function item() {
			return $this->item;
		}

		public function itemToStr() {
			return $this->item->internalPath();
		}
				
		public function details() {
			$f = $this->item->internalId()." (".$this->item->filesystem()->name().")";
			
			if ($this->subType() === self::RENAME)
				return 'item id='.$f.';to='.$this->info;
			if ($this->subType() === self::COPY or $this->subType() === self::MOVE)
				return 'item id='.$f.';to='.$this->info->internalId()." (".$this->info->filesystem()->name().")";
			return 'item id='.$f;
		}
	}

?>