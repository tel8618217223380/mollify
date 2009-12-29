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
	
	require_once("install/MollifyInstaller.class.php");
	
	class FileInstaller extends MollifyInstaller {
		private $users;
		private $publishedDirectories;
		
		public function __construct($type, $settingsVar) {
			parent::__construct($type, $settingsVar);
			
			global $USERS, $PUBLISHED_DIRECTORIES;
			$this->users = $USERS;
			$this->publishedDirectories = $PUBLISHED_DIRECTORIES;
		}
		
		public function process($phase) {
			if (!$this->isConfigured()) {
				$this->showPage("file/instructions_configuration");
				return;
			}
			
			$this->createEnvironment();
			
			//never show installation information in single user mode since users cannot be identified
			if (!$this->authentication()->isAuthenticationRequired()) die();
			
			if ($this->action() != 'retry-configure') {
				// don't show installation information unless admin user is logged in
				if (!$this->authentication()->isAdmin()) die();
			}
			
			$this->showPage("file/instructions_installed");
		}
		
		public function isConfigured() {
			return isset($this->publishedDirectories);
		}
		
		public function users() {
			return $this->users;
		}

		public function publishedDirectories() {
			return $this->publishedDirectories;
		}
	}
?>