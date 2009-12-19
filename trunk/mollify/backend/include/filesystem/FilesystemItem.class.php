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

	abstract class FilesystemItem {
		protected $id;
		protected $path;
		protected $filesystem;
		
		function __construct($id, $path, $name, $filesystem) {
			$this->id = $id;
			$this->path = $path;
			$this->name = $name;
			$this->filesystem = $filesystem;
			
			Logging::logDebug($this);
		}
		
		abstract function isFile();
		
		public function id() {
			return $this->id;
		}
		
		public function name() {
			return $this->name;
		}
		
		public function path() {
			return $this->path;
		}
		
		public function folderPath() {
			return $this->filesystem->folderPath($this);
		}
		
		public function details() {
			return $this->filesystem->details($this);
		}
		
		public function filesystem() {
			return $this->filesystem;
		}
		
		public function rename($name) {
			return $this->filesystem->rename($this, $name);
		}
		
		public function delete() {
			return $this->filesystem->delete($this);
		}
		
		public function downloadAsZip() {
			return $this->filesystem->downloadAsZip($this);
		}
		
		public function __toString() {
			return "FILESYSTEMITEM ".get_class($this)." (".get_class($this->filesystem)."): ".$this->id." = ".$this->name." (".$this->path.")";
		}
	}
	
	class File extends FilesystemItem {		
		public function isFile() { return TRUE; }
		
		public function size() {
			return $this->filesystem->size($this);
		}
		
		public function copy($to) {
			return $this->filesystem->copy($this, $to);
		}

		public function move($to) {
			return $this->filesystem->move($this, $to);
		}
		
		public function read() {
			return $this->filesystem->read($this);
		}
		
		public function write() {
			return $this->filesystem->write($this);
		}
	}
	
	class Folder extends FilesystemItem {
		public function isFile() { return FALSE; }
		
		public function folders() {
			return $this->filesystem->folders($this);
		}
		
		public function files() {			
			return $this->filesystem->files($this);
		}
		
		public function createFolder($name) {
			return $this->filesystem->createFolder($this, $name);
		}
		
		public function createEmptyItem($name) {
			return $this->filesystem->createEmptyItem($this, $name);
		}
	}
?>