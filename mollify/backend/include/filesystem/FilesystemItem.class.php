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

	class FilesystemItem {
		protected $filesystem;
		protected $id;
		protected $rootId;
		protected $path;
		protected $storedPermission = NULL;
		
		function __construct($filesystem, $id, $rootId, $path) {
			$this->filesystem = $filesystem;
			$this->id = $id;
			$this->rootId = $rootId;
			$this->path = $path;
		
			Logging::logDebug("FilesystemItem: ".$id."=".$path." (".$rootId.")");	
			$this->assert();
		}
		
		public function isFile() { throw new Exception("Not implemented"); }
		
		public function id() {
			return $this->id;
		}
		
		public function name() {
			return Filesystem::basename($this->path);
		}

		public function rename($name) {
			$this->assertRights(Authentication::RIGHTS_WRITE, "rename");
			return $this->filesystem->rename($this, $name);
		}

		public function move($to) {
			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights(Authentication::RIGHTS_READ, "move");
			$to->assertRights(Authentication::RIGHTS_WRITE, "move");

			return $this->filesystem->move($this, $to);
		}

		public function copy($to) {
			if ($to->isFile()) throw new ServiceException("NOT_A_DIR", $to->path());
			$this->assertRights(Authentication::RIGHTS_READ, "copy");
			$to->assertRights(Authentication::RIGHTS_WRITE, "copy");

			return $this->filesystem->copy($this, $to);
		}
		
		public function delete() {
			$this->assertRights(Authentication::RIGHTS_WRITE, "delete");
			return $this->filesystem->delete($this);
		}

		public function description() {
			return $this->filesystem->description($this);
		}

		public function setDescription($desc) {
			$this->assertRights(Authentication::RIGHTS_WRITE, "set description");
			return $this->filesystem->setDescription($this, $desc);
		}
				
		public function permission() {
			if ($this->storedPermission != NULL) return $this->storedPermission;
			$this->storedPermission = $this->filesystem->permission($this);
			return $this->storedPermission;
		}

		public function allPermissions() {
			return $this->filesystem->allPermissions($this);
		}
		
		public function rootId() {
			return $this->rootId;
		}
		
		public function path() {
			return $this->path;
		}
		
		public function dirName() {
			return dirname($this->path);
		}
		
		public function assertRights($rights, $action = "Unknown action") {
			$this->filesystem->assertRights($this, $rights, $action);
		}

	}
	
	class File extends FilesystemItem {
		function assert() {
			if (!file_exists($this->path))
				throw new ServiceException("FILE_DOES_NOT_EXIST", Filesystem::basename($this->path));
				
			if(!is_file($this->path))
				throw new ServiceException("NOT_A_FILE", Filesystem::basename($this->path));
		}
		
		public function isFile() { return TRUE; }
				
		public function details() {
			$this->assertRights(Authentication::RIGHTS_READ, "details");
			$datetime_format = $this->filesystem->getDatetimeFormat();
			
			return array(
				"id" => $this->id,
				"last_changed" => date($datetime_format, filectime($this->path)),
				"last_modified" => date($datetime_format, filemtime($this->path)),
				"last_accessed" => date($datetime_format, fileatime($this->path)),
				"description" => $this->description(),
				"permission" => $this->permission());
		}
		
		public function download() {
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
		
		public function downloadAsZip() {
			$this->assertRights(Authentication::RIGHTS_READ, "download zip");
			Logging::logDebug('download zip ['.$this->path.']');
			
			$zip = $this->filesystem->zip($this->name().".zip");
			$zip->add_file_from_path($this->name(), $this->path);
			$zip->finish();
		}
	}
	
	class Folder extends FilesystemItem {
		function assert() {
			if (!file_exists($this->path))
				throw new ServiceException("DIR_DOES_NOT_EXIST", Filesystem::basename($this->path));
				
			if(!is_dir($this->path))
				throw new ServiceException("NOT_A_DIR", Filesystem::basename($this->path));
		}
				
		public function isFile() { return FALSE; }
		
		public function details() {
			$this->assertRights(Authentication::RIGHTS_READ, "details");
			
			return array(
				"id" => $this->id,
				"description" => $this->description(),
				"permission" => $this->permission());
		}
		
		public function folders() {
			$items = scandir($this->path);
			if (!$items) throw new ServiceException("INVALID_PATH", $this->path);
				
			$result = array();
			
			foreach($items as $i => $name) {
				if (substr($name, 0, 1) == '.') continue;
	
				$fullPath = Filesystem::dirPath(Filesystem::joinPath($this->path, $name));
				if (!is_dir($fullPath)) continue;
		
				$result[] = array(
					"id" => $this->filesystem->getId($this->rootId, $fullPath),
					"name" => $name,
					"parent_id" => $this->id
				);
			}
			
			return $result;
		}
		
		public function files() {
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

		public function createFolder($name) {
			$this->assertRights(Authentication::RIGHTS_WRITE, "create folder");
			$this->filesystem->createFolder($this, $name);
		}
		
		public function uploadTo() {
			$this->assertRights(Authentication::RIGHTS_WRITE, "upload");
			$this->filesystem->uploadToFolder($this);
		}
		
		public function pathFor($name) {
			return Filesystem::joinPath($this->path, $name);
		}
		
		public function downloadAsZip() {
			$this->assertRights(Authentication::RIGHTS_READ, "download zip");
			Logging::logDebug('download zip ['.$this->path.']');
			
			$offset = strlen($this->path());
			$zip = $this->filesystem->zip($this->name().'.zip');
			foreach($this->getVisibleFiles($this->path, TRUE) as $file)
				$zip->add_file_from_path(substr($file, $offset), $file);
			$zip->finish();
		}
	}
?>