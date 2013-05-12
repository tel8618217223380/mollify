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
	 
	class DBConnectionFactory {
		public function createConnection($settings) {
			if (!$settings->hasSetting("db")) throw new ServiceException("INVALID_CONFIGURATION", "No database settings defined");
			$db = $settings->setting("db");
			
			if (!isset($db["type"])) throw new ServiceException("INVALID_CONFIGURATION", "No database type defined");
			$type = $db["type"];
			
			if (strcasecmp($type, 'pdo') == 0) {
				require_once("db/pdo/PDODatabase.class.php");
				return PDODatabase::createFromConf($db);
			} else if (strcasecmp($type, 'mysql') == 0) {
				require_once("db/mysql/MySQLIDatabase.class.php");
				return MySQLIDatabase::createFromConf($db);
			} else if (strcasecmp($type, 'postgresql') == 0) {
				require_once("db/postgresql/PostgresqlDatabase.class.php");
				return PostgresqlDatabase::createFromConf($db);
			} else if (strcasecmp($type, 'sqlite') == 0) {
				require_once("db/sqlite/SQLiteDatabase.class.php");
				return MollifySQLiteDatabase::createFromConf($db);
			} else {
				throw new ServiceException("INVALID_CONFIGURATION", "Unsupported database type: [".$type."]");
			}
		}
		
		public function __toString() {
			return "DBConnectionFactory";
		}
	}
?>