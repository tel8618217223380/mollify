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

	class Filesystem {
		const EVENT_TYPE_FILE = "event_file";
		
		private $env;
		private $allowedUploadTypes;
		
		function __construct($env) {
			require_once("FilesystemItem.class.php");
			
			$this->env = $env;
			$this->allowedUploadTypes = $env->settings()->setting('allowed_file_upload_types');
		}
		
		public function initialize($request) {}
		
		public function onSessionStarted() {
			$this->env->session()->param('roots', $this->validateRootDirectories());
		}

		public function getId($rootId, $path = "") {
			if (strlen($path) > 0)
				$path = substr($path, strlen($this->getRootPath($rootId)));
			return base64_encode($rootId.':'.DIRECTORY_SEPARATOR.$path);
		}
		
		public function getItemFromPath($rootId, $path) {
			$isFile = (strcasecmp(substr($path, -1), DIRECTORY_SEPARATOR) != 0);
			
			if ($isFile) return new File($this, $this->getId($rootId, $path), $rootId, $path);
			return new Folder($this, $this->getId($rootId, $path), $rootId, $path);
		}
		
		public function getItemFromId($id) {
			$plainId = base64_decode($id);
			$parts = explode(":".DIRECTORY_SEPARATOR, $plainId);
			if (count($parts) != 2) throw new ServiceException("INVALID_CONFIGURATION", "Invalid file item id: ".$plainId);
			
			$rootId = $parts[0];
			$filePath = $parts[1];
			$rootPath = $this->getRootPath($rootId);
			
			$path = $rootPath;
			$isFile = FALSE;
			
			if (strlen($filePath) > 0) {
				if (strpos("..", $filePath) != FALSE)
					throw new ServiceException("INVALID_REQUEST", "Illegal path requested: ".$filePath);
					
				$path = self::joinPath($path, $filePath);
			}
			
			return $this->getItemFromPath($rootId, $path);
		}
		
		public function assertRights($item, $required, $desc = "Unknown action") {
			$this->env->authentication()->assertRights($item->permission(), $required, "filesystemitem ".$item->path()."/".$desc);
		}
		
		public function getRootDirectories() {
			return $this->env->session()->param('roots');
		}
		
		private function getRootPath($rootId) {
			$roots = $this->getRootDirectories();
			if (!array_key_exists($rootId, $roots))
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid root directory requested: ".$rootId);
			return self::dirPath($roots[$rootId]["path"]);
		}
				
		private function validateRootDirectories() {
			$roots = $this->env->configuration()->getUserRootDirectories($this->env->authentication()->getUserId());
			
			foreach($roots as $id => $root) {
				if (!isset($root["name"])) {
					$this->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Root directory definition does not have a name (".$id.")");
				}
				
				if (!file_exists($root["path"])) {
					$this->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Root directory does not exist (".$id.")");
				}
			}
			
			return $roots;
		}
		
		public function rename($item, $name) {
			$old = $item->path();
			$new = self::joinPath(dirname($old),$name);
			if (!$item->isFile()) $new = self::dirPath($new);

			if (file_exists($new))
				throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to rename [".$item->id()."], target already exists ".self::basename($new));
			
			Logging::logDebug('rename from ['.$old.'] to ['.$new.']');
			
			if (!rename($old, $new)) throw new ServiceException("REQUEST_FAILED", "Failed to rename [".$item->id()."]");
			
			$to = $this->getItemFromPath($item->rootId(), $new);
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->moveItemDescription($item, $to);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->moveItemPermissions($item, $to);
			
			$this->env->events()->onEvent(FileEvent::rename($item, $name));
		}

		public function copy($item, $to) {			
			$target = Filesystem::joinPath($to->path(), $item->name());
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to copy [".$item->id()."] to [".$to->id()."], target already exists");
			Logging::logDebug('copying '.$item->id()."[".$item->path().'] to ['.$target.']');
					
			if (!copy($item->path(), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to copy [".$item->id()."]");
						
			$this->env->events()->onEvent(FileEvent::copy($item, $this->getItemFromPath($item->rootId(), $target)));			
		}
		
		public function move($item, $to) {			
			$target = Filesystem::joinPath($to->path(), $item->name());
			if (!$item->isFile()) $target = self::dirPath($target);
			
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to move [".$item->id()."] to [".$to->id()."], target already exists");
			Logging::logDebug('moving '.$item->id()."[".$item->path().'] to ['.$target.']');
					
			if (!rename($item->path(), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to move [".$item->id()."]");
			$to = $this->getItemFromPath($item->rootId(), $target);
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->moveItemDescription($item, $to);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->moveItemPermissions($item, $to);
			
			$this->env->events()->onEvent(FileEvent::move($item, $to));			
		}
		
		public function delete($item) {
			Logging::logDebug('deleting ['.$item->id().']');
			
			if ($item->isFile()) {
				if (!unlink($item->path()))
					throw new ServiceException("REQUEST_FAILED", "Cannot delete [".$item->id()."]");				
			} else {		
				$this->env->features()->assertFeature("folder_actions");
				$this->deleteFolderRecursively($item->path());
			}
			
			if ($this->env->features()->isFeatureEnabled("description_update"))
				$this->env->configuration()->removeItemDescription($item);
				
			if ($this->env->features()->isFeatureEnabled("permission_update"))
				$this->env->configuration()->removeItemPermissions($item);
			
			$this->env->events()->onEvent(FileEvent::delete($item));
		}
		
		private function deleteFolderRecursively($path) {
			$path = self::dirPath($path);
			$handle = opendir($path);
			
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open directory for traversal (delete_directory_recurse): ".$path);
		    
		    while (false !== ($item = readdir($handle))) {
				if ($item != "." and $item != ".." ) {
					$fullpath = $path.$item;
	
					if (is_dir($fullpath)) {
						$this->deleteFolderRecursively($fullpath);
					} else {
						if (!unlink($fullpath)) {
							closedir($handle);
							throw new ServiceException("REQUEST_FAILED", "Failed to remove file (delete_directory_recurse): ".$fullpath);
						}
					}
				}
			}
			
			closedir($handle);
			
			if (!rmdir($path))
				throw new ServiceException("REQUEST_FAILED", "Failed to remove directory (delete_directory_recurse): ".$path);
		}
		
		public function createFolder($folder, $name) {
			$this->env->features()->assertFeature("folder_actions");
						
			$path = self::dirPath(self::joinPath($folder->path(), $name));
			Logging::logDebug('create folder ['.$path.']');
			
			if (file_exists($path)) throw new ServiceException("DIR_ALREADY_EXISTS", $folder->id()."/".$name);
			if (!mkdir($path, 0755)) throw new ServiceException("CANNOT_CREATE_FOLDER", $folder->id()."/".$name);
		}
	
		public function uploadToFolder($folder) {
			$this->env->features()->assertFeature("file_upload");
						
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
			$target = $folder->pathFor($name);
			Logging::logDebug('uploading ['.$target.']');
					
			if (file_exists($target))
				throw new ServiceException("FILE_ALREADY_EXISTS", Filesystem::basename($target));
				
			if (!move_uploaded_file($origin, $target))
				throw new ServiceException("SAVING_FAILED", Filesystem::basename($target));
		}

		public function zip($name) {
			$this->env->features()->assertFeature("zip_download");
			require "zipstream.php";
			return new ZipStream($name, $this->env->settings()->setting("zip_options"));
		}

		public function getIgnoredItems($folder) {
			return array('mollify.dsc', 'mollify.uac');
		}
		
		public function getDatetimeFormat() {
			return "YmdHis";
		}
		
		public function description($item) {
			return $this->env->configuration()->getItemDescription($item);
		}

		public function setDescription($item, $desc) {
			return $this->env->configuration()->setItemDescription($item, $desc);
		}
		
		public function permission($item) {
			if ($this->env->authentication()->isAdmin()) return Authentication::$PERMISSION_VALUE_READWRITE;
			return $this->env->configuration()->getItemPermission($item, $this->env->authentication()->getUserId());
		}

		public function allPermissions($item) {
			return $this->env->configuration()->getItemPermissions($item);
		}
		
		public function getSessionInfo() {
			$result = array();
			
			$result['filesystem'] = array(
				"max_upload_file_size" => Util::inBytes(ini_get("upload_max_filesize")),
				"max_upload_total_size" => Util::inBytes(ini_get("post_max_size")),
				"allowed_file_upload_types" => $this->getAllowedFileUploadTypes()
			);
			
			$result["roots"] = array();
			
			foreach($this->getRootDirectories() as $id => $root) {
				$result["roots"][] = array(
					"id" => FileSystem::getId($id),
					"name" => $root["name"]
				);
			}

			return $result;
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
		
		public function log() {
			Logging::logDebug("FILESYSTEM: allowed_file_upload_types=".Util::array2str($this->allowedUploadTypes));
		}
		
		static function joinPath($item1, $item2) {
			return self::dirPath($item1).$item2;
		}
		
		static function dirPath($path) {
			return rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
		}
		
		static function basename($path) {
			$name = strrchr(rtrim($path, DIRECTORY_SEPARATOR), DIRECTORY_SEPARATOR);
			if (!$name) return "";
			return substr($name, 1);
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