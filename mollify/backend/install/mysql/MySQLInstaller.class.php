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
			require_once("install/mysql/MySQLInstallUtil.class.php");
			return new MySQLInstallUtil($this->db);
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
			
			try {
				$ver = $this->dbUtil->installedVersion();
			} catch (ServiceException $e) {
				return FALSE;
			}

			if ($ver != NULL)
				Logging::logDebug('Mollify installed version: '.$ver);
			else
				Logging::logDebug('Mollify not installed');

			return $ver != NULL;
		}
		
		public function isCurrentVersionInstalled() {
			return ($this->installedVersion() === $this->currentVersion());
		}
		
		public function installedVersion() {
			return $this->dbUtil->installedVersion();
		}

		public function currentVersion() {
			return MySQLConfigurationProvider::VERSION;
		}
		
		public function db() {
			return $this->db;
		}		

		public function process() {
			$this->checkSystem();
			$this->checkInstalled();
			$this->checkConfiguration();

			$phase = $this->phase();
			if ($phase == NULL) $phase = 'db';
			Logging::logDebug("Installer phase: [".$phase."]");	
			
			$this->onPhase($phase);
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
			if (!$this->authentication()->isAdmin()) die("Mollify Installer requires administrator user");
			
			$this->showPage("installed");
		}
		
		private function checkConfiguration() {
			if (!$this->isConfigured())
				$this->showPage("configuration");

			try {
				$this->db->connect(FALSE);
			} catch (ServiceException $e) {
				if ($e->type() === 'INVALID_CONFIGURATION') {
					$this->setError("Could not connect to database", $e->details());
					$this->showPage("configuration");
					die();
				}
				throw $e;
			}
		}
		
		private function onPhase($phase) {
			$this->setPhase($phase);
			
			switch ($phase) {
				case 'db':
					$this->onPhaseDatabase();
					break;
				case 'install':
					$this->onPhaseInstall();
					break;
				case 'admin':
					$this->onPhaseAdmin();
					break;
				case 'success':
					$this->showPage("success");
					break;
				default:
					Logging::logError("Invalid installer phase: ".$phase);
					die();
			}
		}
		
		// PHASES
				
		private function onPhaseDatabase() {
			if ($this->action() === 'install') {
				$this->clearAction();
				$this->onPhase('install');
			}
			
			$this->showPage("database");
		}
		
		private function onPhaseInstall() {
			try {
				if (!$this->db->databaseExists()) $this->dbUtil->createDatabase();
				$this->util()->checkPermissions();
				$this->util()->execCreateTables();
				$this->util()->execInsertParams();
			} catch (ServiceException $e) {
				$this->setError("Could not install", $e->details());
				$this->onPhase('db');
			}
			$this->onPhase('admin');
		}
		
		private function onPhaseAdmin() {
			if ($this->action() === 'create') {
				try {
					$this->db->selectDb();
					$this->util()->createAdminUser($this->data("name"), $this->data("password"));
				} catch (ServiceException $e) {
					$this->setError("Could not create user", $e->details());
				}
				$this->onPhase('success');
			}
			$this->showPage("admin");
		}
	}
?>