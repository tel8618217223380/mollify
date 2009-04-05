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

	$DEFAULT_HOST = 'localhost';
	$DEFAULT_DB = 'mollify';
	$VERSION = "0_9_5";
	
	function get_installed_version($db) {
		$connection = @mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
		if (!$connection) return FALSE;
		if (!mysql_select_db($db['database'])) return FALSE;
		
		$result = mysql_query("SELECT value FROM parameter WHERE name='version'");
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
	
	function check_connection($db) {
		$connection = @mysql_connect($db['host'], $db['user'], $db['password'], TRUE, 2);
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
		mysqli_report(MYSQLI_REPORT_ALL);
				
		try {
			$connection = @mysqli_connect($db['host'], $db['user'], $db['password']);
			if (!$connection) return mysqli_connect_error();
			mysqli_query($connection, "CREATE DATABASE ".$db['database']."");		
			mysqli_select_db($connection, $db['database']);
		} catch (mysqli_sql_exception $e) {
			return $e->getMessage();
		}
		
		mysqli_close($connection);
		return FALSE;
	}
	
	function check_db_permissions($db) {
		mysqli_report(MYSQLI_REPORT_ERROR);
		
		$phase = "select database";
		try {
			$connection = @mysqli_connect($db['host'], $db['user'], $db['password'], $db['database']);
			if (!$connection) return array("phase" => $phase, "error" => mysqli_connect_error());
						
			$tests = array("create table" => 'CREATE TABLE mollify_install_test (id int NULL)',
				"insert data" => 'INSERT INTO mollify_install_test (id) VALUES (1)',
				"update data" => 'UPDATE mollify_install_test SET id = 2',
				"delete data" => 'DELETE FROM mollify_install_test',
				"drop table" => 'DROP TABLE mollify_install_test');
				
			foreach ($tests as $name => $query) {
				$phase = $name;
				mysqli_query($connection, $query);
			}
			
			mysqli_close($connection);
		} catch (mysqli_sql_exception $e) {
			return array("phase" => $phase, "error" => $e->getMessage());
		}
		
		return FALSE;
	}
	
	function _query($db, $query) {
		mysqli_report(MYSQLI_REPORT_ALL);
		
		try {
			$connection = @mysqli_connect($db['host'], $db['user'], $db['password'], $db['database']);
			if (!$connection) return mysqli_connect_error();
			mysqli_query($connection, $query);
			mysqli_commit($connection);
			mysqli_close($connection);
		} catch (mysqli_sql_exception $e) {
			return $e->getMessage();
		}

		return FALSE;
	}

	function _queries($db, $sql) {
		mysqli_report(MYSQLI_REPORT_ALL);
		
		try {
			$connection = @mysqli_connect($db['host'], $db['user'], $db['password'], $db['database']);
			if (!$connection) return mysqli_connect_error();
			mysqli_multi_query($connection, $sql);
			mysqli_commit($connection);
			mysqli_close($connection);
		} catch (mysqli_sql_exception $e) {
			return $e->getMessage();
		}
		
		return FALSE;
	}

	function _exec_sql_file($db, $file) {
		$sql = file_get_contents($file);
		if (!$sql) return "Could not open sql file: ".$file;
		return _queries($db, $sql);
	}
		
	function create_tables($db) {
		global $VERSION;
		return _exec_sql_file($db, "sql/create_tables_".$VERSION.".sql");
	}
	
	function insert_admin_user($db, $user, $pw) {
		$query = "INSERT INTO user (name, password, permission_mode) VALUES ('".mysql_escape_string($user)."','".$pw."','A')";
		return _query($db, $query);
	}
	
	function insert_params($db) {
		global $VERSION;
		return _exec_sql_file($db, "sql/params_".$VERSION.".sql");
	}
?>
