<?php
	class FilesystemItem {
		protected $id;
		protected $rootId;
		protected $path;
		
		function __construct($id, $rootId, $path) {
			$this->id = $id;
			$this->rootId = $rootId;
			$this->path = $path;
			
			$this->assert();
		}
		
		public function isFile() { throw new Exception("Not implemented"); }
		
		public function getId() {
			return $this->id;
		}
		
		public function getRootId() {
			return $this->rootId;
		}
		
		public function getPath() {
			return $this->path;
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
	}
	
	class Folder extends FilesystemItem {
		function assert() {
			if (!file_exists($this->path))
				throw new ServiceException("DIR_DOES_NOT_EXIST", Filesystem::basename($this->path));
				
			if(!is_dir($this->path))
				throw new ServiceException("NOT_A_DIR", Filesystem::basename($this->path));
		}
		
		public function isFile() { return FALSE; }
	}
?>