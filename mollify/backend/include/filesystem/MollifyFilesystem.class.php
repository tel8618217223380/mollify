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
		private $id;
		private $name;
		
		function __construct($id, $name) {
			$this->id = $id;
			$this->name = $name;
		}

		public function id() {
			return $this->id;
		}
		
		public function name() {
			return $this->name;
		}
		
		public abstract function createItem($id, $path);
				
		//public abstract function exists($path);
		
		public abstract function folders($parent);
		
		public abstract function files($parent);
		
		public abstract function rename($item, $name);
		
		public abstract function copy($item, $to);
		
		public abstract function move($item, $to);
		
		public abstract function delete($item);
				
		public abstract function createFolder($folder, $name);
	
		public abstract function uploadToFolder($folder);
				
		//public function folderPath($path) {
	}
?>