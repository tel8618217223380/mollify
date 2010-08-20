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

	class MollifyZipStream {
		private $env;
		private $name;
		private $zip;
		
		function __construct($env) {
			$this->env = $env;
			$this->name = sys_get_temp_dir().DIRECTORY_SEPARATOR.uniqid('Mollify', true).'zip';
			$this->zip = new ZipArchive();
			if ($this->zip->open($this->name, ZIPARCHIVE::CREATE) !== TRUE)
				throw new ServiceException("REQUEST_FAILED", "Could not create zip ".$this->name);
		}
		
		public function add($name, $size, $path) {
			$this->zip->addFile($path, $name);
		}
		
		public function finish() {
			$this->zip->close();
		}
		
		public function stream() {
			$handle = @fopen($this->name, "rb");
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open zip for reading: ".$this->name);
			return $handle;
		}
	}
?>