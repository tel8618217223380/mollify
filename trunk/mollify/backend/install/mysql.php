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

	function check_mysql() {
		if (!function_exists('mysql_connect')) {
			error("MySQL not detected in the system, check system configuration.");
			return FALSE;
		}
		if (!function_exists('mysqli_multi_query')) {
			error("MySQLI extension not found. This is required for running SQL files.");
			return FALSE;
		}		
		return TRUE;
	}

	function get_installed_version($db) {
		$connection = @mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return FALSE;
		if (!mysql_select_db($db['database'])) return FALSE;
		
		$result = mysql_query("SELECT value FROM param WHERE name='version'");
		if (mysql_error() or mysql_num_rows($result) === 0) return FALSE;
		
		$version = mysql_result($result, 0);
		if (trim($version) === "") return FALSE;

		return $version;
	}
	
	function get_db_configuration() {
		global $DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE;
		
		$result = array();

		$result["host"] = NULL;
		if (isset($DB_HOST)) $result["host"] = $DB_HOST;
		
		$result["database"] = NULL;
		if (isset($DB_DATABASE)) $result["database"] = $DB_DATABASE;

		$result["user"] = NULL;
		if (isset($DB_USER)) $result["user"] = $DB_USER;

		$result["password"] = NULL;
		if (isset($DB_PASSWORD)) $result["password"] = $DB_PASSWORD;
				
		return $result;
	}
	
	function db_error($msg, $error) {
		error("<span class='error'>$msg<span class='details'>$error</span></span>");
	}
	
	function check_connection($db) {
		$connection = mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return FALSE;
		mysql_close($connection);
		return TRUE;
	}
	
	function check_database($db) {
		$connection = mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return FALSE;

		$result = mysql_select_db($db['database']);
		mysql_close($connection);
		return $result;		
	}
		
	function create_database($db) {
		$connection = mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return FALSE;
			
		$result = mysql_query("CREATE DATABASE ".$db['database']."");		
		if ($result) $result = mysql_select_db($db['database']);
		
		mysql_close($connection);
		return $result;
	}
	
	function check_db_permissions($db) {
		$connection = mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return "connect";
		
		if (!mysql_select_db($db['database'])) {
			mysql_close($connection);
			return "select database";
		}
		
		$tests = array("create table" => 'CREATE TABLE mollify_install_test (id int NULL)',
			"insert data" => 'INSERT INTO mollify_install_test (id) VALUES (1)',
			"update data" => 'UPDATE mollify_install_test SET id = 2',
			"delete data" => 'DELETE FROM mollify_install_test',
			"drop table" => 'DROP TABLE mollify_install_test');
			
		foreach ($tests as $name => $query) {
			if (!mysql_query($query)) {
				mysql_close($connection);
				return $name;
			}
		}
		
		mysql_close($connection);
		return NULL;
	}
	
	function _query($db, $query) {
		$connection = mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return array("no" => mysql_errno(), "error" => mysql_error());
		
		$result = mysql_select_db($db['database']);
		if (!$result) {
			$result = array("no" => mysql_errno(), "error" => mysql_error());
		} else {	
			$result = mysql_query($query);		
			if (!$result) $result = array("no" => mysql_errno(), "error" => mysql_error());
			else $result = NULL;
		}
		
		mysql_close($connection);
		return $result;
	}

	function _queries($db, $queries) {
		$connection = mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return array("no" => mysql_errno(), "error" => mysql_error());
		
		$result = mysql_select_db($db['database']);
		if (!$result) {
			$result = array("no" => mysql_errno(), "error" => mysql_error());
		} else {
			foreach($queries as $query) {
				$result = mysql_query($query);
				if (!$result) break;
			}
			
			if (!$result) $result = array("no" => mysql_errno(), "error" => mysql_error());
			else $result = NULL;
		}
		
		mysql_close($connection);
		return $result;
	}
		
	function create_tables($db) {
		$tables = array("
CREATE TABLE `folder` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`)
);
","
CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  `password` varchar(128) NOT NULL,
  `permission_mode` char(2) character set latin1 NOT NULL,
  PRIMARY KEY  (`id`)
);
","
CREATE TABLE `user_folder` (
  `user_id` int(11) NOT NULL,
  `folder_id` int(11) NOT NULL,
  PRIMARY KEY  (`user_id`,`folder_id`),
  KEY `fk_pf_folder` (`folder_id`)
);
","
CREATE TABLE `param` (
  `name` char(255) character set latin1 NOT NULL,
  `value` char(255) character set latin1 NOT NULL,
  PRIMARY KEY  (`name`)
);
");
		return _queries($db, $tables);
	}
	
	function insert_admin_user($db, $user, $pw) {
		$query = "INSERT INTO user (name, password, permission_mode) VALUES ('".mysql_escape_string($user)."','".md5($pw)."','A')";
		return _query($db, $query);
	}
	
	function insert_param($db, $name, $value) {
		$query = "INSERT INTO param (name, value) VALUES ('".mysql_escape_string($name)."','".mysql_escape_string($value)."')";
		return _query($db, $query);
	}
?>
