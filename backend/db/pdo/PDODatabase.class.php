<?php

	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class PDODatabase {
		private $str;
		private $user;
		private $pw;
		private $type;
		private $tablePrefix;
		
		private $db = NULL;
		
		public static function createFromConf($conf) {
			if (!isset($conf["str"]) || !isset($conf["user"]) or !isset($conf["password"])) throw new ServiceException("INVALID_CONFIGURATION", "No PDO database information defined");

			if (isset($conf["table_prefix"])) $tablePrefix = $conf["table_prefix"];
			else $tablePrefix = "";
			
			$db = new PDODatabase($conf["str"], $conf["user"], $conf["password"], $tablePrefix);
			$db->connect();
			if (isset($conf["charset"])) $db->setCharset($conf["charset"]);
			return $db;
		}
		
		public function __construct($str, $user, $pw, $tablePrefix) {
			Logging::logDebug("PDO: ".$str);
			$this->str = $str;
			$this->user = $user;
			$this->pw = $pw;
			$this->tablePrefix = $tablePrefix;
			$this->type = substr($str, 0, strpos($str, ":"));
		}
		
		public function type() {
			return $this->type;
		}

		public function str() {
			return $this->str;
		}
		
		public function user() {
			return $this->user;
		}

		public function password() {
			return $this->password;
		}
		
		public function tablePrefix() {
			return $this->tablePrefix;
		}
		
		public function isConnected() {
			return $this->db != NULL;
		}
		
		public function port() {
			return $this->port;
		}
		
		public function socket() {
			return $this->socket;
		}
		
		public function connect($selectDb = TRUE) {
			try {
				$db = new PDO($this->str, $this->user, $this->pw);
			} catch (PDOException $e) {
			    throw new ServiceException("INVALID_CONFIGURATION", "Could not connect to database (".$this->str."), error: ".$e->getMessage());
			}

			$this->db = $db;
		}
		
		public function setCharset($charset) {
			$this->db->exec("SET CHARACTER SET ".$charset);
		}
		
		public function databaseExists() {
			return false;	//TODO mysql_select_db($this->database, $this->db);
		}

		public function table($name) {
			return $this->tablePrefix.$name;
		}
		
		public function update($query) {
			$result = $this->query($query);
			$affected = $result->affected();
			$result->free();
			return $affected;
		}

		public function query($query) {
			if (Logging::isDebug()) Logging::logDebug("DB: ".$query);
			
			$result = $this->db->query($query);
			if (!$result)
				throw new ServiceException("INVALID_CONFIGURATION", "Error executing query (".$query."): ".Util::array2str($this->db->errorInfo()));
			return new Result($this->db, $result);
		}
		
		public function queries($sql) {	
				/*@mysqli_multi_query($this->db, $sql);
			    do {
			        if ($result = @mysqli_store_result($this->db))
			        	mysqli_free_result($result);
			        
			        if (mysqli_error($this->db))
			        	throw new ServiceException("INVALID_CONFIGURATION", "Error executing queries (".(strlen($sql) > 40 ? substr($sql, 0, 40)."..." : $sql)."): ".mysqli_error($this->db));
			    } while (mysqli_next_result($this->db));
			} catch (mysqli_sql_exception $e) {
				if (Logging::isDebug()) Logging::logDebug("ERROR: ".$e);
				throw new ServiceException("INVALID_CONFIGURATION", "Error executing queries (".(strlen($sql) > 40 ? substr($sql, 0, 40)."..." : $sql)."...): ".mysqli_error($this->db));
			}*/
			try {
				$stmt = $this->db->prepare($sql);
				$stmt->execute();
			} catch(PDOException $e) {
				if (Logging::isDebug()) Logging::logDebug("ERROR: ".$e->getMessage());
				throw new ServiceException("INVALID_CONFIGURATION", "Error executing queries (".(strlen($sql) > 40 ? substr($sql, 0, 40)."..." : $sql)."...): ".$e->getMessage());
			}
		}
		
		public function execSqlFile($file) {
			$sql = file_get_contents($file);
			if (!$sql) throw new ServiceException("INVALID_REQUEST", "Error reading sql file (".$file.")");

			$sql = str_replace('{TABLE_PREFIX}', (isset($this->tablePrefix) and $this->tablePrefix != '') ? $this->tablePrefix : '', $sql);
			$this->queries($sql);
		}
		
		public function startTransaction() {
			if (!$this->db->beginTransaction())
				throw new ServiceException("INVALID_CONFIGURATION", "Error starting transaction: ".Util::array2str($this->db->errorInfo()));
		}

		public function commit() {
			if (!$this->db->commit())
				throw new ServiceException("INVALID_CONFIGURATION", "Error committing transaction: ".Util::array2str($this->db->errorInfo()));
		}
		
		public function rollback() {
			if (!$this->db->rollBack())
				throw new ServiceException("INVALID_CONFIGURATION", "Error rollbacking transaction: ".Util::array2str($this->db->errorInfo()));
		}
		
		public function string($s, $quote = FALSE) {
			if ($s === NULL) return 'NULL';
			$r = $this->db->quote($s);
			if (!$quote) return trim($r, "'");
			return $r;
		}
		
		public function arrayString($a, $quote = FALSE) {
			$result = '';
			$first = TRUE;
			foreach($a as $s) {
				if (!$first) $result .= ',';
				if ($quote) $result .= "'".$s."'";
				else $result .= $s;
				$first = FALSE;
			}
			return $result;
		}
		
		public function lastId() {
			return $this->db->lastInsertId();
		}
	}
	
	class Result {
		private $db;
		private $result;
		private $rows = NULL;
		
		public function __construct($db, $result) {
			$this->db = $db;
			$this->result = $result;
		}
		
		public function count() {
			$rows = $this->getRows();
			if (!$rows) return 0;
			return count($rows);
		}

		public function affected() {
			return $this->result->rowCount();
		}
		
		private function getRows() {
			if ($this->rows != NULL) return $this->rows;
			$this->rows = $this->result->fetchAll(PDO::FETCH_BOTH);
			return $this->rows;
		}
				
		public function rows() {
			return $this->getRows();
		}

		public function values($col) {
			$rows = $this->getRows();
			if (!$rows) return NULL;
			
			$list = array();
			foreach($rows as $row) {
				$list[] = $row[$col];
			}
			return $list;
		}
			
		public function valueMap($keyCol, $valueCol = NULL) {
			$rows = $this->getRows();
			if (!$rows) return NULL;
			
			$list = array();
			foreach($rows as $row) {
				if ($valueCol == NULL)
					$list[$row[$keyCol]] = $row;
				else
					$list[$row[$keyCol]] = $row[$valueCol];
			}
			return $list;
		}
			
		public function firstRow() {
			$rows = $this->getRows();
			if (!$rows) return NULL;
			if (count($rows) == 0) return NULL;
			return $rows[0];
		}
		
		public function firstValue($val) {
			$ret = $this->firstRow();
			if (!$ret) return NULL;
			return $ret[$val];
		}
		
		public function value($r=0, $f=0) {
			$rows = $this->getRows();
			if (!$rows) return NULL;
			if (count($rows) <= $r) return NULL;
			
			$row = $rows[$r];
			return $row[$f];
		}
		
		public function free() {
		}
	}
?>