<?php

	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class S3Filesystem extends MollifyFilesystem {
		private $bucketId;
		
		function __construct($s3, $id, $def, $env) {
			parent::__construct($id, $def['name'] != NULL ? $def['name'] : $def['default_name'], $env->filesystem());
			$this->s3 = $s3;
			$this->bucketId = $def["path"];
		}
		
		public function assert() {
			if (!$this->exists())
				throw new NonExistingFolderException("INVALID_CONFIGURATION", "Invalid folder definition, bucket does not exist [".$this->id()."]");
		}
		
		public function exists() {
			Logging::logDebug("Checking bucket ".$this->bucketId);
			return $this->s3->bucketExists($this->bucketId);
		}
		
		public function create() {
			Logging::logDebug("Creating bucket ".$this->bucketId);
			return $this->s3->createBucket($this->bucketId);
		}
		
		public function type() {
			return "S3FS";
		}
		
		public function createItem($id, $path, $nonexisting = FALSE) {
			$isFile = (strcasecmp(substr($id, -1), DIRECTORY_SEPARATOR) != 0);
			$name = self::basename($path);
			Logging::logDebug("S3 item [".$id."] (".$name.") ".$isFile);
			
			if ($isFile) return new File($id, $this->rootId(), $path, $name, $this);
			return new Folder($id, $this->rootId(), $path, $name, $this);
		}
		
		public function pathExists($path) {
			return TRUE;	//TODO check item in bucket
		}
		
		public function internalPath($item) {
			return $item->path();
		}
				
		public function details($item) {
			$details = array("id" => $item->publicId());
			return $details;
		}

		public function extension($item) {
			if (!$item->isFile()) return NULL;
			
			$extPos = strrpos($item->name(), '.');
			if ($extPos > 0)
				return substr($item->name(), $extPos + 1);
			return "";
		}

		public function items($parent) {
			$result = array();
			foreach($this->s3->getObjects($this->bucketId) as $path) {
				Logging::logDebug($path);
				$id = $this->rootId().$path;
				$result[] = $this->createItem($id, $path);
			}
			
			return $result;
		}
		
		public function parent($item) {
			Logging::logDebug("Parent: ".$item->path());
			if ($item->path() === '') return NULL;
			
			$pos = strrpos($item->path(), DIRECTORY_SEPARATOR);
			if ($pos === FALSE) return $this->root();
			
			$path = substr($item->path, 0, $pos);
			$id = $this->rootId().$path;
			Logging::logDebug($id);
			return $this->createItem($id, $path, self::basename($path));
		}

		public function rename($item, $name) {
			return $item;	//TODO
		}

		public function copy($item, $to) {
			return $to;	//TODO (also folder copy)
		}
				
		public function move($item, $to) {
			return $item;	//TODO
		}
		
		public function delete($item) {
			//TODO
		}
				
		public function createFolder($folder, $name) {
			return $folder;	//TODO
		}
		
		public function createFile($folder, $name) {
			return $folder;	//TODO
		}

		public function fileWithName($folder, $name, $nonExisting = FALSE) {
			$path = self::joinPath($folder->path(), $name);
			return $this->itemWithPath($path, $nonExisting);
		}

		public function folderWithName($folder, $name, $nonExisting = FALSE) {
			$path = self::joinPath($folder->path(), $name.DIRECTORY_SEPARATOR);
			return $this->itemWithPath($path, $nonExisting);
		}
		
		public function size($file) {
			return "0";	//TODO
		}
		
		public function lastModified($item) {
			return 0;	//TODO
		}

		public function read($item, $range = NULL) {
			return NULL;
		}
		
		public function write($item) {
			return NULL;
		}
		
		public function put($item, $content) {
			//file_put_contents($this->localPath($item), $content);
		}
		
		public function __toString() {
			return "S3 (".$this->id.") ".$this->name."(".$this->rootPath.")";
		}
				
		static function joinPath($item1, $item2) {
			return self::folderPath($item1).$item2;
		}
		
		static function folderPath($path) {
			return rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
		}
		
		static function basename($path) {
			$name = strrchr(rtrim($path, DIRECTORY_SEPARATOR), DIRECTORY_SEPARATOR);
			if (!$name) return $path;
			return substr($name, 1);
		}		
	}
?>