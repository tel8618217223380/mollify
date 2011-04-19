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
		private $rootId;
		private $env;
		
		function __construct($s3, $id, $def, $env) {
			parent::__construct($id, $def['name'] != NULL ? $def['name'] : $def['default_name'], $env->filesystem());
			$this->s3 = $s3;
			$this->bucketId = $def["path"];
			$this->rootId = $id.":".DIRECTORY_SEPARATOR;
			$this->env = $env;
		}
		
		public function getBucketKey() {
			return $this->s3->getBucketKey($this->bucketId);
		}
		
		public function isDirectDownload() {
			return TRUE;
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
			
			if ($isFile) return new File($id, $this->rootId(), $path, $name, $this);
			return new Folder($id, $this->rootId(), $path, $name, $this);
		}
		
		public function itemExists($item) {
			return TRUE;	//TODO check item in bucket
		}
		
		public function pathExists($path) {
			return TRUE;	//TODO check item in bucket
		}
		
		public function internalPath($item) {
			return $item->path();
		}
				
		public function details($item) {
			$hdr = $this->s3->getObjectHeaders($this->bucketId, $item->path());

			$details = array("id" => $item->publicId());
			if ($item->isFile()) {
				$details["last_changed"] = date($this->env->filesystem()->datetimeFormat(), strtotime($hdr["last-modified"]));
			}
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
			
			Logging::logDebug("Retrieving objects under [".$parent->path()."]");
			$items = $this->s3->getObjects($this->bucketId, $parent->path());
			
			// retrieve object headers into cache
			if (count($items) > 0)
				$this->s3->getObjectHeaders($this->bucketId, $items);
			
			foreach($this->s3->getObjects($this->bucketId, $parent->path()) as $path) {
				$id = $this->rootId().$path;
				$result[] = $this->createItem($id, $path);
			}
			
			return $result;
		}
		
		public function parent($item) {
			if ($item->path() === '') return NULL;
			
			$pos = strrpos($item->path(), DIRECTORY_SEPARATOR);
			if ($pos === FALSE) return $this->root();
			
			$path = substr($item->path(), 0, $pos);
			$id = $this->rootId().$path;
			return $this->createItem($id, $path, self::basename($path));
		}

		public function rename($item, $name) {
			if (!$item->isFile()) throw new ServiceException("FEATURE_DISABLED", "Renaming folders in S3 is not supported");
			$new = $item->parent()->fileWithName($name);
			$this->s3->moveObject($this->bucketId, $item->path(), $new->path());
			return $new;
		}

		public function copy($item, $to) {
			if ($item->rootId() != $this->rootId or $to->rootId() != $this->rootId) throw new ServiceException("FEATURE_DISABLED", "Copying from/to outside S3 fs is not supported");
			if (!$item->isFile()) throw new ServiceException("FEATURE_DISABLED", "Copying folders in S3 is not supported");

			if (!$this->s3->copyObject($this->bucketId, $item->path(), $to->path()))
				throw new ServiceException("REQUEST_FAILED", "Failed to copy [".$item->id()." to .".$to->id()."]");
			
			return $to;
		}
				
		public function move($item, $to) {
			if (!$item->isFile()) throw new ServiceException("FEATURE_DISABLED", "Moving folders in S3 is not supported");
			$new = $to->fileWithName($item->name(), TRUE);
			$this->s3->moveObject($this->bucketId, $item->path(), $new->path());
			return $new;
		}
		
		public function delete($item) {
			if (!$item->isFile()) throw new ServiceException("FEATURE_DISABLED", "Deleting folders in S3 is not supported");
			$this->s3->deleteObject($this->bucketId, $item->path());
			return $item;
		}
				
		public function createFolder($folder, $name) {
			$new = $folder->folderWithName($name);
			
			if (!$this->s3->createEmptyObject($this->bucketId, $new->path()))
				throw new ServiceException("REQUEST_FAILED", "Failed to create folder [".$new->id()."]");

			return $new;
		}
		
		public function createFile($folder, $name) {
			return $this->fileWithName($folder, $name, TRUE);
		}

		public function fileWithName($folder, $name, $nonExisting = FALSE) {
			$path = ltrim(self::joinPath($folder->path(), $name), DIRECTORY_SEPARATOR);
			return $this->itemWithPath($path, $nonExisting);
		}

		public function folderWithName($folder, $name, $nonExisting = FALSE) {
			$path = ltrim(self::joinPath($folder->path(), $name.DIRECTORY_SEPARATOR), DIRECTORY_SEPARATOR);
			return $this->itemWithPath($path, $nonExisting);
		}
		
		public function size($file) {
			return sprintf("%d", $this->s3->getObjectSize($this->bucketId, $file->path()));
		}
		
		public function lastModified($item) {
			$hdr = $this->s3->getObjectHeaders($this->bucketId, $item->path());
			return strtotime($hdr["last-modified"]);
		}
		
		public function getDownloadUrl($item) {
			return $this->s3->getObjectUrl($this->bucketId, $item->path());
		}

		public function read($item, $range = NULL) {
			$f = fopen('php://temp', 'w+');
			$this->s3->getObject($this->bucketId, $item->path(), $f);
			rewind($f);
			return $f;
		}
		
		public function write($item, $s) {
			$this->s3->createObject($this->bucketId, $item->path(), $s);
		}
				
		public function put($item, $content) {
			$f = $this->write($item);
			fwrite($f, $content);
			rewind($f);
			$this->s3->createObject($this->bucketId, $item->path(), $f);
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
			$name = rtrim($path, DIRECTORY_SEPARATOR);
			$last = strrchr($name, DIRECTORY_SEPARATOR);
			if (!$last) return $name;
			return ltrim($last, DIRECTORY_SEPARATOR);
		}		
	}
?>