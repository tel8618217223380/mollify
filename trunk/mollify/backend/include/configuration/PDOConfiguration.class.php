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
	 
	require_once("DbConfiguration.class.php");

	class PDOConfiguration extends DbConfiguration {
		const VERSION = "1_8_8";
		
		public function __construct($settings) {
			global $PDO_STRING, $DB_USER, $DB_PASSWORD, $DB_TABLE_PREFIX, $DB_CHARSET;
			
			if (!isset($PDO_STRING)) throw new ServiceException("INVALID_CONFIGURATION", "No PDO string defined");
			if (!isset($DB_USER)) throw new ServiceException("INVALID_CONFIGURATION", "No db user defined");
			if (!isset($DB_PASSWORD)) throw new ServiceException("INVALID_CONFIGURATION", "No db password defined");
			
			require_once("include/pdo/PDODatabase.class.php");
			$this->db = new PDODatabase($PDO_STRING, $DB_USER, $DB_PASSWORD, isset($DB_TABLE_PREFIX) ? $DB_TABLE_PREFIX : "");
			
			$this->db->connect();
			if (isset($DB_CHARSET)) $this->db->setCharset($DB_CHARSET);
		}
		
		public function getType() {
			return $this->db->type();
		}
	}
?>