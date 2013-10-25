<?php

	/**
	 * SQLiteInstallUtil.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class SQLiteInstallUtil {
		private $db;
		
		public function __construct($db) {
			$this->db = $db;
		}
		
		public function db() {
			return $this->db;
		}
		
		public function execCreateTables() {
			$this->db->execSqlFile("db/sqlite/sql/install/create_tables.sql");
		}
		
		public function execInsertParams() {
			$this->db->execSqlFile("db/sqlite/sql/install/params.sql");
		}
		
		public function createAdminUser($name, $pw) {
			//$a1pw = md5($name.":mollify:".$pw);	//TODO
			//$this->db->query("INSERT INTO ".$this->db->table("user")." (name, password, a1password, permission_mode, is_group) VALUES ('".$this->db->string($name)."','".md5($pw)."','".$a1pw."','".Authentication::PERMISSION_VALUE_ADMIN."', 0)", FALSE);
			require_once("include/configuration/ConfigurationDao.class.php");
			$conf = new ConfigurationDao($this->db)
			$id = $conf->addUser($name, NULL, Authentication::PERMISSION_VALUE_ADMIN, FALSE);
			$conf->storeUserAuth($id, $name, 'pw', $pw);
		}
		
		public function updateVersionStep($from, $to) {
			$file = "db/sqlite/sql/update/".$from."-".$to.".sql";
			$this->db->execSqlFile($file);
		}

		public function execPluginCreateTables($id) {
			$this->db->execSqlFile("plugin/".$id."/sqlite/install.sql");
		}
		
		public function updatePluginVersionStep($id, $from, $to) {
			$file = "plugin/".$id."/sqlite/".$from."-".$to.".sql";
			$this->db->execSqlFile($file);
		}
	}
?>