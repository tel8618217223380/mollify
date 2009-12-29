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

	class MySQLInstallUtil {
		private $db;
		
		public function __construct($db) {
			$this->db = $db;
		}
		
		public function db() {
			return $this->db;
		}
		
		public function checkPermissions() {
			mysqli_report(MYSQLI_REPORT_ERROR);
			$table = $this->db->table("mollify_install_test");
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
		}
	}
?>