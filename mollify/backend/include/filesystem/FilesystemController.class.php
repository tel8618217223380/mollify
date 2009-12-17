<?php

	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */
	 
	 class FilesystemController {
	 	const TYPE_LOCAL = "local";
	 	const EVENT_TYPE_FILE = "event_file";
		
		private $env;
		private $allowedUploadTypes;

		function __construct($env) {
			require_once("MollifyFilesystem.class.php");
			require_once("LocalFilesystem.class.php");
			require_once("FilesystemItem.class.php");
			
			$this->env = $env;
			$this->allowedUploadTypes = $env->settings()->setting('allowed_file_upload_types');
		}
		
		public function initialize($request) {}

		public function onSessionStarted() {
			$this->env->session()->param('filesystems', $this->createFilesystems());
		}

		private function createFilesystems() {
			$filesystems = array();
			$folderDefs = $this->env->configuration()->getUserFolders($this->env->authentication()->getUserId());
			
			foreach($folderDefs as $id => $folderDef) {
				if (!isset($folderDef["name"])) {
					$this->env->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Folder definition does not have a name (".$root['id'].")");
				}
				if (!isset($folderDef["path"])) {
					$this->env->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Folder definition does not have a path (".$root['id'].")");
				}
				
				$filesystems[$id] = $this->createFilesystem($id, $folderDef);
			}
			
			return $filesystems;
		}

		private function createFilesystem($id, $folderDef) {
			switch ($this->filesystemType($folderDef)) {
				case self::TYPE_LOCAL:
					return new LocalFilesystem($id, $folderDef);
				default:
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid folder definition (".$id.")");
			}
		}
		
		private function filesystemType($folderDef) {
			return self::TYPE_LOCAL;
		}
		
		public function getSessionInfo() {
			$result = array();
			
			$result['filesystem'] = array(
				"max_upload_file_size" => Util::inBytes(ini_get("upload_max_filesize")),
				"max_upload_total_size" => Util::inBytes(ini_get("post_max_size")),
				"allowed_file_upload_types" => $this->getAllowedFileUploadTypes()
			);
			
			$result["roots"] = array();
			
			foreach($this->filesystems() as $id => $filesystem) {
				$result["roots"][] = array(
					"id" => $this->publicId($filesystem),
					"name" => $filesystem->name()
				);
			}

			return $result;
		}
		
		public function filesystems() {
			return $this->env->session()->param('filesystems');
		}
		
		public function filesystem($id) {
			return $this[$id];
		}
		
		public function item($id) {
			$plainId = base64_decode($id);
			$parts = explode(":".DIRECTORY_SEPARATOR, $plainId);
			if (count($parts) != 2) throw new ServiceException("INVALID_CONFIGURATION", "Invalid item id: ".$id);
			
			$filesystemId = $parts[0];
			$path = $parts[1];
			
			return $this->filesystem($filesystemId)->createItem($path);			
		}
		
		public function publicId($filesystem, $path = "") {
			return base64_encode($filesystem->id().":".DIRECTORY_SEPARATOR.$path);
		}
		
/*		public function getItemFromPath($rootId, $path) {
			$isFile = (strcasecmp(substr($path, -1), DIRECTORY_SEPARATOR) != 0);
			
			if ($isFile) return new File($this, $this->getId($rootId, $path), $rootId, $path);
			return new Folder($this, $this->getId($rootId, $path), $rootId, $path);
		}
		
				public function getId($rootId, $path = "") {
			if (strlen($path) > 0)
				$path = substr($path, strlen($this->getRootPath($rootId)));
			return base64_encode($rootId.':'.DIRECTORY_SEPARATOR.$path);
		}
		*/

		public function assertRights($item, $required, $desc = "Unknown action") {
			$this->env->authentication()->assertRights($item->permission(), $required, "filesystemitem ".$item->path()."/".$desc);
		}
				
/*		private function getRootPath($rootId) {
			$roots = $this->getRootDirectories();
			if (!array_key_exists($rootId, $roots))
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid root directory requested: ".$rootId);
			return $this->filesystem->folderPath($roots[$rootId]["path"]);
		}*/

		public function getIgnoredItems($folder) {
			return array('mollify.dsc', 'mollify.uac');
		}
		
		public function getDatetimeFormat() {
			return "YmdHis";
		}

		public function details($item) {
			$this->assertRights($item, Authentication::RIGHTS_READ, "details");
			
			if (!$item->isFile())
				return array(
					"id" => $this->id,
					"description" => $this->description(),
					"permission" => $this->permission());

			$datetime_format = $this->filesystem->getDatetimeFormat();
			
			return array(
				"id" => $this->id,
				"last_changed" => date($datetime_format, filectime($this->path)),
				"last_modified" => date($datetime_format, filemtime($this->path)),
				"last_accessed" => date($datetime_format, fileatime($this->path)),
				"description" => $this->description(),
				"permission" => $this->permission());
		}
		
		public function description($item) {
			return $this->env->configuration()->getItemDescription($item);
		}

		public function setDescription($item, $desc) {
			$this->assertRights(Authentication::RIGHTS_WRITE, "set description");
			return $this->env->configuration()->setItemDescription($item, $desc);
		}
		
		public function permission($item) {
			if ($this->env->authentication()->isAdmin()) return Authentication::$PERMISSION_VALUE_READWRITE;
			return $this->env->configuration()->getItemPermission($item, $this->env->authentication()->getUserId());
		}

		public function allPermissions($item) {
			return $this->env->configuration()->getItemPermissions($item);
		}
		
		private function getAllowedFileUploadTypes() {
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
			$this->assertRights(Authentication::RIGHTS_WRITE, "rename");
			$to = $this->filesystem($item)->rename($item, $name);

			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->moveItemDescription($item, $to);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->moveItemPermissions($item, $to);
			
			$this->env->events()->onEvent(FileEvent::rename($item, $name));
		}

		public function copy($item, $to) {
			Logging::logDebug('copying '.$item->id()."[".$item->path().'] to ['.$to.']');
			
			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights(Authentication::RIGHTS_READ, "copy");
			$to->assertRights(Authentication::RIGHTS_WRITE, "copy");

			$to = $this->filesystem->copy($item, $to);
			$this->env->events()->onEvent(FileEvent::copy($item, $this->getItemFromPath($item->rootId(), $target)));			
		}
		
		public function move($item, $to) {
			Logging::logDebug('moving '.$item->id()."[".$item->path().'] to ['.$to.']');

			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights(Authentication::RIGHTS_READ, "move");
			$to->assertRights(Authentication::RIGHTS_WRITE, "move");

			$to = $this->filesystem->move($item, $to);
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->moveItemDescription($item, $to);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->moveItemPermissions($item, $to);
			
			$this->env->events()->onEvent(FileEvent::move($item, $to));			
		}
		
		public function delete($item) {
			Logging::logDebug('deleting ['.$item->id().']');
			
			if (!$item->isFile()) $this->env->features()->assertFeature("folder_actions");
			$this->assertRights(Authentication::RIGHTS_WRITE, "delete");
			$this->filesystem->delete($item);
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->removeItemDescription($item);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->removeItemPermissions($item);
			
			$this->env->events()->onEvent(FileEvent::delete($item));
		}
		
		public function createFolder($parent, $name) {
			$this->assertRights(Authentication::RIGHTS_WRITE, "create folder");
			$this->filesystem->createFolder($parent, $name);
		}

		public function download($file) {
			$this->assertRights(Authentication::RIGHTS_READ, "download");
			Logging::logDebug('download ['.$this->path.']');
			
			header("Cache-Control: public, must-revalidate");
			header("Content-Type: application/force-download");
			header("Content-Type: application/octet-stream");
			header("Content-Type: application/download");
			header("Content-Disposition: attachment; filename=\"".Filesystem::basename($this->path)."\";");
			header("Content-Transfer-Encoding: binary");
			header("Pragma: hack");
			header("Content-Length: ".filesize($this->path));
			
			readfile($this->path);
		}

		public function uploadToFolder($folder) {
			$this->env->features()->assertFeature("file_upload");
			$this->assertRights(Authentication::RIGHTS_WRITE, "upload");
			$this->filesystem->uploadToFolder($folder);
		}
		
		public function zip($name) {
			$this->env->features()->assertFeature("zip_download");
			require "zipstream.php";
			return new ZipStream($name, $this->env->settings()->setting("zip_options"));
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
		
		private $item;
		private $subType;
		
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
		
		function __construct($item, $type, $data = NULL) {
			parent::__construct(FileSystem::EVENT_TYPE_FILE, $data);
			$this->item = $item;
			$this->subType = $type;
		}

		public function item() {
			return $this->item;
		}
		
		public function subType() {
			return $this->subType;
		}
	}

?>