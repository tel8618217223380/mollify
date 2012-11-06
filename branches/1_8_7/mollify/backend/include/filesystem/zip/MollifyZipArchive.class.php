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

	class MollifyZipArchive implements MollifyZip {
		private $env;
		private $name;
		private $path;
		private $zip;
		
		function __construct($env, $name = FALSE) {
			if (!class_exists('ZipArchive'))
				throw new ServiceException("INVALID_CONFIGURATION", "ZipArchive lib not installed");
				
			$this->env = $env;
			$this->name = $name ? $name : uniqid('Mollify', true);
			$this->path = sys_get_temp_dir().DIRECTORY_SEPARATOR.$name.'zip';
			$this->zip = new ZipArchive();
			if ($this->zip->open($this->path, ZIPARCHIVE::CREATE) !== TRUE)
				throw new ServiceException("REQUEST_FAILED", "Could not create zip ".$this->path);
		}
		
		public function acceptFolders() {
			return FALSE;
		}

		public function add($name, $path, $size = 0) {
			$this->zip->addFile($path, $name);
		}
		
		public function finish() {
			$this->zip->close();
		}
		
		public function stream() {
			$handle = @fopen($this->path, "rb");
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open zip for reading: ".$this->name);
			return $handle;
		}

		public function name() {
			return $this->name;
		}
				
		public function filename() {
			return $this->path;
		}
	}
?>