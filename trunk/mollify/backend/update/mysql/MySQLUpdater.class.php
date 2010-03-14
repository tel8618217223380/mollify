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
	
	require_once("install/mysql/MySQLInstaller.class.php");
	
	class MySQLUpdater extends MySQLInstaller {
		private static $versionHistory = array("0_9_5", "1_0_0", "1_5_0", "1_5_4");
		
		public function __construct($type, $settingsVar) {
			parent::__construct($type, $settingsVar, "update");
		}
		
		public function process() {
			if (!$this->isInstalled()) die();

			$this->createEnvironment();
			if (!$this->authentication()->isAdmin()) die("Mollify Updater requires administrator user");
			
			if ($this->isCurrentVersionInstalled()) $this->showPage("current_installed");
			
			if ($this->action() === 'update') $this->update();
			$this->showPage("update");
		}
		
		 public function versionString($ver) {
		 	return str_replace("_", ".", $ver);
		 }

		private function update() {
			$installed = $this->installedVersion();
			$current = $this->currentVersion();
			
			if (!in_array($installed, self::$versionHistory)) {
				$this->setError("Unknown version", "Installed version (".$this->versionString($installed).") is unknown, and updater cannot continue.");
				$this->showPage("update_error");
			}

			if (!in_array($current, self::$versionHistory)) {
				$this->setError("Updater error", "Mollify updater does not contain the update required to update to current version, report a new updater issue at <a href='http://code.google.com/p/mollify/issues/list'>issue list</a>");
				$this->showPage("update_error");
			}
			
			$indexFrom = array_search($installed, self::$versionHistory) + 1;
			$indexTo = array_search($current, self::$versionHistory);
			$stepFrom = $installed;
			
			try {
				$this->db->startTransaction();
				for ($i = $indexFrom; $i <= $indexTo; $i++) {
					$stepTo = self::$versionHistory[$i];
					$this->util()->updateVersionStep($stepFrom, $stepTo);
					$stepFrom = $stepTo;
				}
				$this->db->commit();
			} catch (ServiceException $e) {
				$this->setError("Update failed", "<code>".$e->details()."</code>");
				$this->showPage("update_error");
			}
			
			$this->session()->reset();
			$this->showPage("success");
		}
	}
?>