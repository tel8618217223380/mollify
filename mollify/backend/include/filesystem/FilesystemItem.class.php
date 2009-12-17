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
		protected $id;
		protected $path;
		
		function __construct($id, $path, $name) {
			$this->id = $id;
			$this->path = $path;
			$this->name = $name;
			Logging::logDebug("FilesystemItem: ".$id."=".$path);
		}
		
		public function isFile() { throw new Exception("Not implemented"); }
		
		public function id() {
			return $this->id;
		}
		
		public function name() {
			return $this->name;
		}

/*		public function rename($name) {
			return $this->filesystem->rename($this, $name);
		}

		public function move($to) {
			return $this->filesystem->move($this, $to);
		}

		public function copy($to) {
			return $this->filesystem->copy($this, $to);
		}
		
		public function delete() {
			return $this->filesystem->delete($this);
		}

		public function description() {
			return $this->filesystem->description($this);
		}

		public function setDescription($desc) {
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
		}*/
		
		public function path() {
			return $this->path;
		}
		
/*		public function dirName() {
			return $this->filesystem->dirname($this);
		}*/
	}
	
	class File extends FilesystemItem {
		function assert() {
			if (!$this->filesystem->exists($this))
				throw new ServiceException("FILE_DOES_NOT_EXIST", $this->id());
				
			if(!$this->filesystem->isFile($this))
				throw new ServiceException("NOT_A_FILE", $this->id());
		}
		
		public function isFile() { return TRUE; }
						
/*		public function download() {
			$this->filesystem->download($this);
		}
		
		public function downloadAsZip() {
			$this->assertRights(Authentication::RIGHTS_READ, "download zip");
			Logging::logDebug('download zip ['.$this->path.']');
			
			$zip = $this->filesystem->zip($this->name().".zip");
			$zip->add_file_from_path($this->name(), $this->path);
			$zip->finish();
		}*/
	}
	
	class Folder extends FilesystemItem {
/*		function assert() {
			if (!file_exists($this->path))
				throw new ServiceException("DIR_DOES_NOT_EXIST", $this->name()));
				
			if(!is_dir($this->path))
				throw new ServiceException("NOT_A_DIR", Filesystem::basename($this->path));
		}*/
				
		public function isFile() { return FALSE; }
		
/*		public function folders() {
			return $result;
		}
		
		public function files() {			
			return $result;
		}
		
		public function createFolder($name) {
			$this->filesystem->createFolder($this, $name);
		}
		
		public function uploadTo() {
			$this->filesystem->uploadToFolder($this);
		}
		
		public function pathFor($name) {
			return $this->filesystem->joinPath($this->path, $name);
		}
		
		public function downloadAsZip() {
			$this->assertRights(Authentication::RIGHTS_READ, "download zip");
			Logging::logDebug('download zip ['.$this->path.']');
			
			$offset = strlen($this->path());
			$zip = $this->filesystem->zip($this->name().'.zip');
			foreach($this->getVisibleFiles($this->path, TRUE) as $file)
				$zip->add_file_from_path(substr($file, $offset), $file);
			$zip->finish();
		}*/
	}
?>