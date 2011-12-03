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

	class PublicUploader {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function showPage($folder) {
			include("upload.php");
		}
		
		public function uploadTo($folder) {
			$this->env->filesystem()->uploadTo($folder);
		}
	}
?>