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
		private $tablePrefix;
		
		private $db = NULL;
		
		public function __construct($host, $user, $pw, $database, $tablePrefix) {
			Logging::logDebug("MySQL DB: ".$user."@".$host.":".$database."(".$tablePrefix.")");
			$this->host = $host;
			$this->user = $user;
			$this->pw = $pw;
			$this->database = $database;
			$this->tablePrefix = $tablePrefix;
		}
		
		public function isConnected() {
			return $this->db != NULL;
		}
		
		public function connect() {
			$db = @mysql_connect($this->host, $this->user, $this->pw);
			
			if (!$db) throw new ServiceException("INVALID_CONFIGURATION", "Could not connect to database (host=".$this->host.", user=".$this->user.", password=".$this->pw."), error: ".mysql_error());

			if (!mysql_select_db($this->database, $db)) throw new ServiceException("INVALID_CONFIGURATION", "Could not connect select database (".$database.") error: ".mysql_error($db));
			
			$this->db = $db;
		}

		public function table($name) {
			return $this->tablePrefix.$name;
		}

		public function query($query) {
			if (Logging::isDebug()) Logging::logDebug("DB: ".$query);
			
			$result = @mysql_query($query, $this->db);
			if (!$result)
				throw new ServiceException("INVALID_CONFIGURATION", "Error executing query (".$query."): ".mysql_error($db));
			return new Result($this->db, $result);
		}
		
		public function string($s) {
			return mysql_real_escape_string($s, $this->db);
		}
	}
	
	class Result {
		private $db;
		private $result;
		
		public function __construct($db, $result) {
			$this->db = $db;
			$this->result = $result;
		}
		
		public function count() {
			return mysql_num_rows($this->result);
		}

		public function affected() {
			return mysql_affected_rows($this->db);
		}
				
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
		
		public function free() {
			if ($this->result === TRUE or $this->result === FALSE) return;
			mysql_free_result($this->result);
		}
	}
?>