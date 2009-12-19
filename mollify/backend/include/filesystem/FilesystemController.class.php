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
			$this->env->session()->param('roots', $this->getRootFolders());
		}

		private function getRootFolders() {
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
			}
			
			return $folderDefs;
		}

		private function createFilesystem($id, $folderDef) {
			switch ($this->filesystemType($folderDef)) {
				case MollifyFilesystem::TYPE_LOCAL:
					return new LocalFilesystem($id, $folderDef, $this);
				default:
					throw new ServiceException("INVALID_CONFIGURATION", "Invalid folder definition (".$id.")");
			}
		}
		
		private function filesystemType($folderDef) {
			return MollifyFilesystem::TYPE_LOCAL;
		}
		
		public function getSessionInfo() {
			$result = array();
			
			$result['filesystem'] = array(
				"max_upload_file_size" => Util::inBytes(ini_get("upload_max_filesize")),
				"max_upload_total_size" => Util::inBytes(ini_get("post_max_size")),
				"allowed_file_upload_types" => $this->getAllowedFileUploadTypes()
			);
			
			$result["roots"] = array();
			
			foreach($this->env->session()->param('roots') as $id => $folderDef) {
				$result["roots"][] = array(
					"id" => $this->publicId($id),
					"name" => $folderDef['name']
				);
			}

			return $result;
		}
		
		public function filesystem($id) {
			$folderDefs = $this->env->session()->param('roots');
			return $this->createFilesystem($id, $folderDefs[$id]);
		}
		
		public function item($id) {
			$plainId = base64_decode($id);
			$parts = explode(":".DIRECTORY_SEPARATOR, $plainId);
			if (count($parts) != 2) throw new ServiceException("INVALID_CONFIGURATION", "Invalid item id: ".$id);
			
			$filesystemId = $parts[0];
			$path = $parts[1];
			
			return $this->filesystem($filesystemId)->createItem($id, $path);
		}
		
		public function publicId($filesystemId, $path = "") {
			return base64_encode($filesystemId.":".DIRECTORY_SEPARATOR.$path);
		}

		public function assertRights($item, $required, $desc = "Unknown action") {
			$this->env->authentication()->assertRights($this->permission($item), $required, "filesystemitem ".$item->id()."/".$desc);
		}

		public function ignoredItems($filesystem, $path) {
			return array('mollify.dsc', 'mollify.uac');	//TODO settings etc
		}
		
		public function folders($parent) {
			$this->assertRights($parent, Authentication::RIGHTS_READ, "folders");
			
			$result = array();
			$folders = $parent->folders();
			foreach($folders as $folder) {
				$folder["id"] = $this->publicId($parent->filesystem()->id(), $folder["path"]);
				$result[] = $folder;
			}
			return $result;
		}
		
		public function files($parent) {
			$this->assertRights($parent, Authentication::RIGHTS_READ, "files");
			
			$result = array();
			$files = $parent->files();
			foreach($files as $file) {
				$file["id"] = $this->publicId($parent->filesystem()->id(), $file["path"]);
				$result[] = $file;
			}
			return $result;
		}

		public function details($item) {
			$this->assertRights($item, Authentication::RIGHTS_READ, "details");
			
			$details = $item->details();
			$details["description"] = $this->description($item);
			$details["permission"] = $this->permission($item);
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
			$this->assertRights($item, Authentication::RIGHTS_READ, "copy");
			$this->assertRights($to, Authentication::RIGHTS_WRITE, "copy");

			$to = $this->filesystem->copy($item, $to);
			$this->env->events()->onEvent(FileEvent::copy($item, $this->getItemFromPath($item->rootId(), $target)));			
		}
		
		public function move($item, $to) {
			Logging::logDebug('moving '.$item->id()."[".$item->path().'] to ['.$to.']');

			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights($item, Authentication::RIGHTS_READ, "move");
			$this->assertRights($to, Authentication::RIGHTS_WRITE, "move");

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
			$this->assertRights($item, Authentication::RIGHTS_WRITE, "delete");
			$this->filesystem->delete($item);
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->removeItemDescription($item);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->removeItemPermissions($item);
			
			$this->env->events()->onEvent(FileEvent::delete($item));
		}
		
		public function createFolder($parent, $name) {
			$this->assertRights($parent, Authentication::RIGHTS_WRITE, "create folder");
			$this->filesystem->createFolder($parent, $name);
		}

		public function download($file) {
			Logging::logDebug('download ['.$this->path.']');
			$this->assertRights($file, Authentication::RIGHTS_READ, "download");
			
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