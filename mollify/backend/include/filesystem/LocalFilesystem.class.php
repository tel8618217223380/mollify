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
		
		function __construct($id, $def) {
			parent::__construct($id, $def["name"]);
			
			if (!file_exists($def["path"]))
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid folder definition, path does not exist ".$id);
			$this->rootPath = $def["path"];
		}
		
		public function createItem($id, $path) {
			$path = $rootPath;
			$isFile = FALSE;
			
			if (strlen($path) == 0) throw new ServiceException("INVALID_REQUEST", "Illegal path: ".$path);
			if (strpos("..", $filePath) != FALSE) throw new ServiceException("INVALID_REQUEST", "Illegal path requested: ".$path);
			
			$isFile = (strcasecmp(substr($path, -1), DIRECTORY_SEPARATOR) != 0);
			if ($isFile) return new File($this, $path, self::basename($path));
			return new Folder($this, $path, self::basename($path));
		}
		
		public function exists($path) {
			return file_exists($path);
		}
		
		public function folders($parent) {
			$items = scandir($parent->path());
			if (!$items) throw new ServiceException("INVALID_PATH", $parent->id());
				
			$result = array();
			foreach($items as $i => $name) {
				if (substr($name, 0, 1) == '.') continue;
	
				$fullPath = self::dirPath(self::joinPath($this->path, $name));
				if (!is_dir($fullPath)) continue;
		
				$result[] = array(
					"path" => $this->relativePath($fullPath),
					"name" => $name,
					"parent" => $this->id
				);
			}
			
			return $result;
		}
		
		public function files($parent) {
			$result = array();
			
			foreach($this->getVisibleFiles($this->path) as $fullPath) {
				$name = Filesystem::basename($fullPath);
				$extPos = strrpos($name, '.');
				
				if ($extPos > 0) {
					$extension = substr($name, $extPos + 1);
				} else {
					$extension = "";
				}
				
				$result[] = array(
					"id" => $this->filesystem->getId($this->rootId, $fullPath),
					"parent_id" => $this->id,
					"name" => $name,
					"extension" => $extension,
					"size" => filesize($fullPath)
				);
			}
			
			return $result;
		}
		
		function getVisibleFiles($path, $recursive = FALSE) {			
			$files = scandir($path);
			if (!$files) throw new ServiceException("INVALID_PATH", $this->path);
			
			$ignored = $this->filesystem->getIgnoredItems($this);
			$result = array();
			
			foreach($files as $i => $name) {
				if (substr($name, 0, 1) == '.' || in_array(strtolower($name), $ignored))
					continue;
	
				$fullPath = Filesystem::joinPath($path, $name);
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
			if (!$item->isFile()) $new = self::dirPath($new);

			if (file_exists($new))
				throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to rename [".$item->id()."], target already exists ".self::basename($new));
			if (!rename($old, $new)) throw new ServiceException("REQUEST_FAILED", "Failed to rename [".$item->id()."]");
			
			return $this->getItemFromPath($item->rootId(), $new);
		}

		public function copy($item, $to) {			
			$target = Filesystem::joinPath($to->path(), $item->name());
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to copy [".$item->id()."] to [".$to->id()."], target already exists");	
			if (!copy($item->path(), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to copy [".$item->id()."]");
			
			return $this->getItemFromPath($item->rootId(), $target);
		}
		
		public function move($item, $to) {			
			$target = Filesystem::joinPath($to->path(), $item->name());
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
			return self::dirPath($item1).$item2;
		}
		
		public function folderPath($path) {
			return rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
		}
		
		static function basename($path) {
			$name = strrchr(rtrim($path, DIRECTORY_SEPARATOR), DIRECTORY_SEPARATOR);
			if (!$name) return "";
			return substr($name, 1);
		}
	}
?>