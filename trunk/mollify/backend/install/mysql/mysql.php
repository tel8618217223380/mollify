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

	function get_connection($db, $connect_db = TRUE) {
		global $error, $error_detail;
		
		try {
			if ($connect_db) $connection = @mysqli_connect($db['host'], $db['user'], $db['password'], $db['database']);
			else $connection = @mysqli_connect($db['host'], $db['user'], $db['password']);
		} catch (mysqli_sql_exception $e) {
			$error = "COULD_NOT_CONNECT_TO_DB";
			$error_detail = $e->getMessage();
			return FALSE;
		}
		if ($connection) return $connection;
		$error = "COULD_NOT_CONNECT_TO_DB";
		$error_detail = mysqli_connect_error();
		return FALSE;
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
		global $error, $error_detail;
		mysqli_report(MYSQLI_REPORT_ALL);

		$connection = get_connection($db, FALSE);
		if (!$connection) return FALSE;
								
		try {
			mysqli_query($connection, "CREATE DATABASE ".$db['database']."");		
			mysqli_select_db($connection, $db['database']);
		} catch (mysqli_sql_exception $e) {
			$error = "COULD_NOT_CREATE_DB";
			$error_detail = $e->getMessage();
			return FALSE;
		}
		
		mysqli_close($connection);
		return TRUE;
	}
	
	function check_db_permissions($db) {
		global $error, $error_detail;
		mysqli_report(MYSQLI_REPORT_ERROR);
		
		$connection = get_connection($db);
		if (!$connection) return FALSE;
		
		try {						
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
			$error = "DB_PERMISSION_TEST_FAILED";
			$error_detail = "Could not ".$phase." (".$e->getMessage().")";
			return FALSE;
		}
		
		return TRUE;
	}

	function _query($connection, $query, $err, $has_result = TRUE) {
		global $error, $error_detail;
		mysqli_report(MYSQLI_REPORT_ALL);
		
		try {
			$result = mysqli_query($connection, $query);
			if (!$result) {
				$error = $err;
				$error_detail = mysqli_error($connection)." (".mysqli_errno($connection).")";
				return FALSE;
			}
			if ($has_result) mysqli_free_result($result);
		} catch (mysqli_sql_exception $e) {
			$error = $err;
			$error_detail = $e->getMessage();
			return FALSE;
		}

		return TRUE;
	}
	
	function _queries($connection, $sql, $err) {
		global $error, $error_detail;
		mysqli_report(MYSQLI_REPORT_ALL);
		
		try {
			mysqli_multi_query($connection, $sql);
		    do {
		        if ($result = mysqli_store_result($connection))
		        	mysqli_free_result($result);
		    } while (mysqli_next_result($connection));
		} catch (mysqli_sql_exception $e) {
			$error = $err;
			$error_detail = $e->getMessage();
			return FALSE;
		}
		
		return TRUE;
	}

	function _exec_sql_file($connection, $file, $err = "COULD_NOT_EXEC_SQL") {
		global $error, $error_detail;
		
		$sql = file_get_contents($file);
		if (!$sql) {
			$error = $err;
			$error_detail = $file;
			return FALSE;
		}
		return _queries($connection, $sql, $err);
	}

	function create_tables($connection) {
		global $VERSION;
		return _exec_sql_file($connection, "sql/create_tables_".$VERSION.".sql", "COULD_NOT_CREATE_TABLES");
	}
	
	function insert_admin_user($connection, $user, $pw) {
		$query = "INSERT INTO user (name, password, permission_mode) VALUES ('".mysql_escape_string($user)."','".$pw."','A')";
		return _query($connection, $query, "COULD_NOT_CREATE_ADMIN", FALSE);
	}
	
	function insert_params($connection) {
		global $VERSION;
		return _exec_sql_file($connection, "sql/params_".$VERSION.".sql", "COULD_NOT_INSERT_PARAMS");
	}
	
	function close_connection($connection, $err) {
		global $error, $error_detail;
		mysqli_report(MYSQLI_REPORT_ALL);
		
		try {
			mysqli_commit($connection);
			mysqli_close($connection);
		} catch (mysqli_sql_exception $e) {
			$error = $err;
			$error_detail = $e->getMessage();
			return FALSE;
		}
		return TRUE;
	}

?>
