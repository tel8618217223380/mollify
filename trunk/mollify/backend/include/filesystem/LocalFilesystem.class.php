<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
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
		
		public function createItem($id, $path, $nonexisting = FALSE) {
			if (strlen($path) > 0 and strpos("..", $path) != FALSE)
				throw new ServiceException("INVALID_REQUEST", "Illegal path: ".$path);
			
			$fullPath = self::joinPath($this->rootPath, $path);
			$isFile = (strcasecmp(substr($fullPath, -1), DIRECTORY_SEPARATOR) != 0);
			
			if ($isFile) {
				if (!$nonexisting and !$this->exists($fullPath))
					throw new ServiceException("FILE_DOES_NOT_EXIST", $id);

				if ($nonexisting and $this->exists($fullPath))
					throw new ServiceException("FILE_ALREADY_EXISTS", $id);
				
				if (!$nonexisting and !is_file($fullPath))
					throw new ServiceException("NOT_A_FILE", $id);
			} else {
				if ($nonexisting) throw new ServiceException("REQUEST_FAILED", "Invalid folder request");
				
				if (!$this->exists($fullPath))
					throw new ServiceException("DIR_DOES_NOT_EXIST", $id);

				if (!is_dir($fullPath))
					throw new ServiceException("NOT_A_DIR", $id);
			}
				
			if ($isFile) return new File($id, $path, self::basename($fullPath), $this);
			return new Folder($id, $path, self::basename($fullPath), $this);
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
					if ($recursive) $result = array_merge($result, $this->visibleFiles($fullPath, TRUE));
					continue;
				}
				
				$result[] = $fullPath;
			}
			return $result;
		}
		
		public function parent($item) {
			if ($item->path() === '') return NULL;
			
			$path = $this->localPath($item);
			return $this->itemWithPath($this->publicPath(dirname($path)));
		}

		public function rename($item, $name) {
			$old = $this->localPath($item);
			$new = self::joinPath(dirname($old),$name);
			if (!$item->isFile()) $new = self::folderPath($new);

			if (file_exists($new))
				throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to rename [".$item->id()."], target already exists ".self::basename($new));

			if (!rename($old, $new)) throw new ServiceException("REQUEST_FAILED", "Failed to rename [".$item->id()."]");
			
			return $this->itemWithPath($this->publicPath($new));
		}

		public function copy($item, $to) {			
			$target = self::joinPath($this->localPath($to), $item->name());
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to copy [".$item->id()."] to [".$to->id()."], target already exists");
			Logging::logDebug($target);
			if (!copy($this->localPath($item), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to copy [".$item->id()."]");
			
			return $this->itemWithPath($this->publicPath($target));
		}
		
		public function move($item, $to) {			
			$target = self::joinPath($this->localPath($to), $item->name());
			if (!$item->isFile()) $target = self::folderPath($target);
			if (file_exists($target)) throw new ServiceException("FILE_ALREADY_EXISTS", "Failed to move [".$item->id()."] to [".$to->id()."], target already exists");
			if (!rename($this->localPath($item), $target)) throw new ServiceException("REQUEST_FAILED", "Failed to move [".$item->id()."]");
			
			return $this->itemWithPath($this->publicPath($target));
		}
		
		public function delete($item) {
			if ($item->isFile()) {
				if (!unlink($this->localPath($item)))
					throw new ServiceException("REQUEST_FAILED", "Cannot delete [".$item->id()."]");				
			} else {		
				$this->deleteFolderRecursively($this->localPath($item));
			}
		}
		
		private function deleteFolderRecursively($path) {
			$path = self::folderPath($path);
			$handle = opendir($path);
			
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open directory for traversal (recurse): ".$path);
		    
		    while (false !== ($item = readdir($handle))) {
				if ($item != "." and $item != ".." ) {
					$fullpath = $path.$item;
	
					if (is_dir($fullpath)) {
						$this->deleteFolderRecursively($fullpath);
					} else {
						if (!unlink($fullpath)) {
							closedir($handle);
							throw new ServiceException("REQUEST_FAILED", "Failed to remove file (recurse): ".$fullpath);
						}
					}
				}
			}
			
			closedir($handle);
			
			if (!rmdir($path))
				throw new ServiceException("REQUEST_FAILED", "Failed to remove directory (delete_directory_recurse): ".$path);
		}
		
		public function createFolder($folder, $name) {
			$path = self::folderPath(self::joinPath($this->localPath($folder), $name));
			if (file_exists($path)) throw new ServiceException("DIR_ALREADY_EXISTS", $folder->id()."/".$name);
			if (!mkdir($path, 0755)) throw new ServiceException("CANNOT_CREATE_FOLDER", $folder->id()."/".$name);
			return $this->itemWithPath($this->publicPath($path));
		}
		
		public function createEmptyItem($folder, $name) {
			return $this->itemWithPath($this->publicPath(self::joinPath($this->localPath($folder), $name)), TRUE);
		}
		
		public function size($file) {
			return filesize($this->localPath($file));
		}

		public function read($item) {
			$handle = @fopen($this->localPath($item), "rb");
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open file for reading: ".$item->id());
			return $handle;
		}
		
		public function write($item) {
			$handle = @fopen($this->localPath($item), "wb");
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open file for writing: ".$item->id());
			return $handle;
		}
		
		public function downloadAsZip($item) {
			require "zipstream.php";
			$zip = new ZipStream($item->name().".zip", $this->filesystemInfo->setting("zip_options"));
			
			if ($item->isFile()) {
				$zip->add_file_from_path($item->name(), $this->localPath($item));
			} else {
				$offset = strlen($this->localPath($item));
				$files = $this->visibleFiles($this->localPath($item), TRUE);
				foreach($files as $file)
					$zip->add_file_from_path(substr($file, $offset), $file);
			}
			$zip->finish();
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