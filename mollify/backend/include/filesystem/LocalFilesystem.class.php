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

	class LocalFilesystem extends MollifyFilesystem {
		private $rootPath;
		
		function __construct($id, $def, $filesystemInfo) {
			parent::__construct($id, $def["name"], $filesystemInfo);
			
			if (!file_exists($def["path"]))
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid folder definition, path does not exist ".$id);
			$this->rootPath = self::folderPath($def["path"]);
		}
		
		public function type() {
			return MollifyFilesystem::TYPE_LOCAL;
		}
		
		public function createItem($id, $path) {
			if (strlen($path) > 0 and strpos("..", $path) != FALSE)
				throw new ServiceException("INVALID_REQUEST", "Illegal path: ".$path);
			
			$fullPath = self::joinPath($this->rootPath, $path);
			$isFile = (strcasecmp(substr($fullPath, -1), DIRECTORY_SEPARATOR) != 0);
			
			if ($isFile) {
				if (!$this->exists($fullPath))
					throw new ServiceException("FILE_DOES_NOT_EXIST", $id);
				
				if (!is_file($fullPath))
					throw new ServiceException("NOT_A_FILE", $id);
			} else {
				if (!$this->exists($fullPath))
					throw new ServiceException("DIR_DOES_NOT_EXIST", $id);

				if (!is_dir($fullPath))
					throw new ServiceException("NOT_A_DIR", $id);
			}
				
			if ($isFile) return new File($id, $path, self::basename($path), $this);
			return new Folder($id, $path, self::basename($path), $this);
		}
		
		public function exists($path) {
			return file_exists($path);
		}

		private function publicPath($path) {
			return substr($path, strlen($this->rootPath));
		}
		
		public function localPath($item) {
			return self::joinPath($this->rootPath, $item->path());
		}
		
		public function details($item) {
			$datetimeFormat = $this->filesystemInfo->datetimeFormat();
			
			$details = array("id" => $item->id());
			if ($item->isFile()) {
				$path = $this->localPath($item);
				$details["last_changed"] = date($datetimeFormat, filectime($path));
				$details["last_modified"] = date($datetimeFormat, filemtime($path));
				$details["last_accessed"] = date($datetimeFormat, fileatime($path));
			}
			return $details;
		}

		public function folders($parent) {
			$parentPath = $this->localPath($parent);
			$items = scandir($parentPath);
			if (!$items) throw new ServiceException("INVALID_PATH", $parent->id());
				
			$result = array();
			foreach($items as $i => $name) {
				if (substr($name, 0, 1) == '.') continue;
	
				$path = self::folderPath(self::joinPath($parentPath, $name));
				if (!is_dir($path)) continue;
		
				$result[] = array(
					"path" => $this->publicPath($path),
					"parent_id" => $parent->id(),
					"name" => $name
				);
			}
			
			return $result;
		}
		
		public function files($parent) {
			$result = array();
			
			foreach($this->visibleFiles($this->localPath($parent)) as $path) {
				$name = self::basename($path);
				$extPos = strrpos($name, '.');
				
				if ($extPos > 0) {
					$extension = substr($name, $extPos + 1);
				} else {
					$extension = "";
				}
				
				$result[] = array(
					"path" => $this->publicPath($path),
					"parent_id" => $parent->id(),
					"name" => $name,
					"extension" => $extension,
					"size" => filesize($path)
				);
			}
			
			return $result;
		}
		
		private function visibleFiles($path, $recursive = FALSE) {			
			$files = scandir($path);
			if (!$files) throw new ServiceException("INVALID_PATH", $this->path);
			
			$ignored = $this->ignoredItems($this->publicPath($path));
			$result = array();
			
			foreach($files as $i => $name) {
				if (substr($name, 0, 1) == '.' || in_array(strtolower($name), $ignored))
					continue;
	
				$fullPath = self::joinPath($path, $name);
				if (is_dir($fullPath)) {
					if ($recursive) $result = array_merge($result, $this->getVisibleFiles($fullPath, TRUE));
					continue;
				}
				
				$result[] = $fullPath;
			}
			return $result;
		}

		public function rename($item, $name) {
			$old = $item->path();
			$new = self::joinPath(dirname($old),$name);
			if (!$item->isFile()) $new = self::folderPath($new);

			if (file_exists($new))
				throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to rename [".$item->id()."], target already exists ".self::basename($new));
			if (!rename($old, $new)) throw new ServiceException("REQUEST_FAILED", "Failed to rename [".$item->id()."]");
			
			return $this->getItemFromPath($item->rootId(), $new);
		}

		public function copy($item, $to) {			
			$target = self::joinPath($to->path(), $item->name());
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to copy [".$item->id()."] to [".$to->id()."], target already exists");	
			if (!copy($item->path(), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to copy [".$item->id()."]");
			
			return $this->getItemFromPath($item->rootId(), $target);
		}
		
		public function move($item, $to) {			
			$target = self::joinPath($to->path(), $item->name());
			if (!$item->isFile()) $target = self::dirPath($target);
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to move [".$item->id()."] to [".$to->id()."], target already exists");
			if (!rename($item->path(), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to move [".$item->id()."]");
			
			return $this->getItemFromPath($item->rootId(), $target);
		}
		
		public function delete($item) {
			if ($item->isFile()) {
				if (!unlink($item->path()))
					throw new ServiceException("REQUEST_FAILED", "Cannot delete [".$item->id()."]");				
			} else {		
				$this->deleteFolderRecursively($item->path());
			}
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
				
		static function joinPath($item1, $item2) {
			return self::folderPath($item1).$item2;
		}
		
		static function folderPath($path) {
			return rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
		}
		
		static function basename($path) {
			$name = strrchr(rtrim($path, DIRECTORY_SEPARATOR), DIRECTORY_SEPARATOR);
			if (!$name) return "";
			return substr($name, 1);
		}
	}
?>