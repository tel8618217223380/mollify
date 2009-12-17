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

	class Database {
		private $host;
		private $user;
		private $pw;
		private $database;
		
		private $db = NULL;
		
		public __construct($host, $user, $pw, $database, $tablePrefix) {
			Logging::logDebug("MySQL DB: ".$user."@".$host.":".$database."(".$tablePrefix.")");
			$this->host = $host;
		}
		
		public function isConnected() {
			return $this->db != NULL;
		}
		
		public function connect() {
			$db = @mysql_connect($host, $user, $pw);
			
			if (!db) throw new ServiceException("INVALID_CONFIGURATION", "Could not connect to database (host=".$host.", user=".$user.", password=".$DB_PASSWORD."), error: ".mysql_error());

			if (!mysql_select_db($database, $db)) throw new ServiceException("INVALID_CONFIGURATION", "Could not connect select database (".$database.") error: ".mysql_error($db));
			
			$this->db = $db;
		}
		
		public function query($query) {
			$result = @mysql_query($query, $this->db);
			if (!$result) throw new ServiceException("INVALID_CONFIGURATION", "Error executing query (".$query."): ".mysql_error($db));
				log_error("Error executing query (".$query."): ".mysql_error($this->db));
			return new Result($result);
		}
		
		public function string($s) {
			return mysql_real_escape_string($s, $this->db);
		}
	}
	
	class Result {
		private $result;
		
		public __construct($result) {
			$this->result = $result;
		}
		
		public function count() (
			return mysql_num_rows($this->result);
		)
		
		public function rows() {
			$list = array();
			while ($row = mysql_fetch_assoc($this->result)) {
				$list[] = $row;
			}
			mysql_free_result($this->result);
			return $list;
		}
		
		public function firstRow() {
			$ret = mysql_fetch_assoc($this->result);
			mysql_free_result($this->result);
			return $ret;
		}
		
		public function value($i) {
			$ret = mysql_result($this->result, $i);
			mysql_free_result($this->result);
			return $ret;
		}
	}
?>