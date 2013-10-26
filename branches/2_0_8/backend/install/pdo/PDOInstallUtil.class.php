<?php

	/**
	 * PDOInstallUtil.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class PDOInstallUtil {
		private $db;
		
		public function __construct($db) {
			$this->db = $db;
		}
		
		public function db() {
			return $this->db;
		}
		
		public function checkPermissions() {
			$table = $this->db->table("mollify_install_test");

			// first cleanup, if test table was left
			try {
				$this->db->query('DROP TABLE '.$table, FALSE);
			} catch (ServiceException $e) {
				// ignore
			}
			
			$this->db->startTransaction();
			try {
				$tests = array("create table" => 'CREATE TABLE '.$table.' (id int NULL)',
					"insert data" => 'INSERT INTO '.$table.' (id) VALUES (1)',
					"update data" => 'UPDATE '.$table.' SET id = 2',
					"delete data" => 'DELETE FROM '.$table,
					"drop table" => 'DROP TABLE '.$table);
					
				foreach ($tests as $name => $query) {
					$phase = $name;
					$this->db->query($query, FALSE);
				}
			} catch (ServiceException $e) {
				throw new ServiceException("INVALID_CONFIGURATION", "Permission test failed, could not ".$phase." (".$e->details().")");
			}
			$this->db->commit();
		}
		
		public function execCreateTables() {
			$this->db->execSqlFile("include/".$this->db->type()."/sql/create_tables.sql");
		}
		
		public function execInsertParams() {
			$this->db->execSqlFile("include/".$this->db->type()."/sql/params.sql");
		}
		
		public function createAdminUser($name, $pw) {
			$a1pw = md5($name.":mollify:".$pw);	//TODO
			$this->db->query("INSERT INTO ".$this->db->table("user")." (name, password, a1password, permission_mode, is_group) VALUES ('".$this->db->string($name)."','".md5($pw)."','".$a1pw."','".Authentication::PERMISSION_VALUE_ADMIN."', 0)", FALSE);
		}
		
		public function updateVersionStep($from, $to) {
			$file = "include/".$this->db->type()."/sql/".$from."-".$to.".sql";
			$this->db->execSqlFile($file);
		}

		public function execPluginCreateTables($id) {
			$this->db->execSqlFile("plugin/".$id."/".$this->db->type()."/install.sql");
		}
		
		public function updatePluginVersionStep($id, $from, $to) {
			$file = "plugin/".$id."/".$this->db->type()."/".$from."-".$to.".sql";
			$this->db->execSqlFile($file);
		}
	}
?>