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

	abstract class MollifyFilesystem {
		const TYPE_LOCAL = "local";
		
		protected $id;
		protected $name;
		protected $filesystemInfo;
		
		function __construct($id, $name, $filesystemInfo) {
			$this->id = $id;
			$this->name = $name;
			$this->filesystemInfo = $filesystemInfo;
		}
		
		abstract function type();

		public function id() {
			return $this->id;
		}
		
		public function name() {
			return $this->name;
		}
		
		public abstract function createItem($id, $path);
				
		//public abstract function exists($path);
		
		public function details($item) {
			return array();
		}
		
		public abstract function folders($parent);
		
		public abstract function files($parent);
		
		public abstract function rename($item, $name);
		
		public abstract function copy($item, $to);
		
		public abstract function move($item, $to);
		
		public abstract function delete($item);
				
		public abstract function createFolder($folder, $name);
	
		public abstract function uploadToFolder($folder);
		
		protected function ignoredItems($path) {
			return $this->filesystemInfo->ignoredItems($this, $path);
		}
		
		protected function itemPublicId($path) {
			return $this->filesystemInfo->publicId($this->id(), $path);
		}
		
		protected function itemWithPath($path) {
			return $this->createItem($this->itemPublicId($path), $path);
		}

		public function __toString() {
			return get_class($this)." (".$this->id.") ".$this->name;
		}
	}
?>