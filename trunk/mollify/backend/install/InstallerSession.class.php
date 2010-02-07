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

	require_once("include/Session.class.php");
	
	class InstallerSession extends Session {
		private $settings;
		
		private $sessionVersions = array("Pre1_5", "1_5");
		private $version;
		
		public function __construct($settings) {
			$this->settings = $settings;	
		}

		public function initPre1_5() {
			$this->name = "MOLLIFY_SESSION";
					
			if ($this->settings->hasSetting("session_name")) {
				$n = $this->settings->setting("session_name");
				if (strlen($n) > 0) $this->name .= "_".$n;
			}
			parent::initialize(NULL);
		}
				
		public function init1_5() {
			$this->name = "MOLLIFY-SESSION";
					
			if ($this->settings->hasSetting("session_name")) {
				$n = $this->settings->setting("session_name");
				if (strlen($n) > 0) $this->name .= "_".$n;
			}
			parent::initialize(NULL);
		}
		
		public function initialize($env) {
			foreach($this->sessionVersions as $ver) {
				eval("\$this->init$ver();");

				if ($env->authentication()->isAdmin()) {
					$this->version = $ver;
					return;
				}
				session_destroy();
			}
		}
	}
?>