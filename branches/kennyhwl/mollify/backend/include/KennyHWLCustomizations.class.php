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

	class KennyHWLCustomizations {
		static $FOLDER_PATHS = "/foo/kennyhwl/users/";
		static $INBOX_NAME = "inbox";
		
		protected $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function onUserAdded($id, $user) {
			$folderName = $user["name"];
			$folderPath = KennyHWLCustomizations::$FOLDER_PATHS.$folderName;
			
			mkdir($folderPath);
			mkdir($folderPath.DIRECTORY_SEPARATOR.KennyHWLCustomizations::$INBOX_NAME);
			
			$folderId = $this->env->configuration()->addFolder($folderName, $folderPath);
			$this->env->configuration()->addUserFolder($id, $folderId, NULL);
			
			$fs = $this->env->filesystem()->filesystem(array("id" => $folderId, "path" => $folderPath, "name" => $folderName), FALSE);
			$this->env->configuration()->addItemPermission($fs->root()->id(), Authentication::PERMISSION_VALUE_READWRITE, $id);
		}
		
		public function __toString() {
			return "KennyHWLCustomizations";
		}
	}
?>