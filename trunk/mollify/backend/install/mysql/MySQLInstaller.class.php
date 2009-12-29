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
	require_once("include/ServiceEnvironment.class.php");
	require_once("include/mysql/DatabaseUtil.class.php");
	require_once("install/mysql/MySQLInstallUtil.class.php");
	
	class MySQLInstaller extends MollifyInstaller {
		private $configured;
		private $db;

		public function __construct($type, $settingsVar) {
			parent::__construct($type, $settingsVar);
			
			global $DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE, $DB_TABLE_PREFIX;
			$this->configured = isset($DB_USER, $DB_PASSWORD);
			$this->db = $this->createDB($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE, $DB_TABLE_PREFIX);
			$this->dbUtil = new DatabaseUtil($this->db);
		}

		private function createDB($host, $user, $password, $database, $tablePrefix) {
			if (!isset($host)) $host = "localhost";
			if (!isset($database)) $database = "mollify";
			if (!isset($tablePrefix)) $tablePrefix = "";
			
			require_once("include/mysql/MySQLIDatabase.class.php");
			return new MySQLIDatabase($host, $user, $password, $database, $tablePrefix);
		}
		
		private function util() {
			return new MySQLInstallUtil($this->db);
		}
		
		public function process() {
			$this->checkSystem();
			$this->checkInstalled();
			$this->checkConfiguration();

			$phase = $this->phase();
			if ($phase == NULL) $phase = '';
			Logging::logDebug("Installer phase: [".$phase."]");
						
			switch ($phase) {
				case '':
					$this->showPage("verify_database");
				case 'db':
					$this->onCreateDatabaseAndVerifyPermissions();
				case 'admin':
					$this->showPage("create_admin");
				default:
					Logging::logError("Invalid phase: ".$phase);
					die();
			}
		}
		
		private function checkSystem() {
			if (!function_exists('mysql_connect'))
				$this->showPage("mysql/install_error", "MySQL not installed");
		
			if (!function_exists('mysqli_multi_query'))
				$this->showPage("mysql/install_error", "MySQLI not installed");
		}
		
		private function checkInstalled() {
			if (!$this->isInstalled()) return;
			
			$this->createEnvironment();
			if (!$this->authentication()->isAdmin()) die();
			
			$this->showPage("instructions_installed");
		}
		
		private function checkConfiguration() {
			if (!$this->isConfigured())
				$this->showPage("instructions_configuration");

			try {
				$this->db->connect(FALSE);
			} catch (ServiceException $e) {
				if ($e->type() === 'INVALID_CONFIGURATION') {
					$this->showPage("instructions_configuration", $e->details());
					die();
				}
				throw $e;
			}
		}
		
		private function onCreateDatabaseAndVerifyPermissions() {
			try {
				if (!$this->db->databaseExists()) $this->dbUtil->createDatabase();
				$this->util()->checkPermissions();
			} catch (ServiceException $e) {
				$this->showPage("verify_database", $e->details());
			}
			//$this->moveToPhase("admin");
		}		
		
		public function isConfigured() {
			return $this->configured;
		}

		public function isInstalled() {
			if (!$this->isConfigured())
				return FALSE;
			
			try {
				if (!$this->db->isConnected()) $this->db->connect(FALSE);
			} catch (ServiceException $e) {
				return FALSE;
			}

			if (!$this->db->databaseExists())
				return FALSE;
			
			try {
				$this->db->selectDb();
			} catch (ServiceException $e) {
				return FALSE;
			}
			
			$ver = $this->dbUtil->installedVersion();
			if ($ver != NULL)
				Logging::logDebug('Mollify installed version: '.$ver);
			else
				Logging::logDebug('Mollify not installed');

			return $ver != NULL;
		}
		
		public function db() {
			return $this->db;
		}		
	}
?>