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

	class S3 extends PluginBase {
		private $s3;
				
		public function setup() {
			$this->env()->filesystem()->registerFilesystem("S3FS", $this);
	 		$this->addService("s3", "S3Services");
		}
		
		public function initialize() {
	 		require_once("S3Filesystem.class.php");
	 		require_once("MollifyS3.class.php");
	 		$this->s3 = new MollifyS3($this->getSettings());
		}
		
		public function getS3() {
			return $this->s3;
		}
		
		//public function getClientPlugin() {
		//	return "client/plugin.js";
		//}
		
		public function createFilesystem($id, $folderDef) {
			return new S3Filesystem($this->s3, $id, $folderDef, $this->env);
		}
		
		public function __toString() {
			return "S3Plugin";
		}
	}
?>