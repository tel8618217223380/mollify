<?php
	function check_installation() {
		$conf = get_db_configuration();
		$connection = @mysql_connect($conf['host'], $conf['user'], $conf['password'], TRUE, 2);
		if (!$connection) return TRUE;
		if (!mysql_select_db($conf['db'])) return TRUE;
		
		$result = mysql_query("SELECT value FROM param WHERE name='version'");
		if (mysql_error() or mysql_num_rows($result) === 0) return TRUE;
		
		$version = mysql_result($result);
		if (trim($version) === "") return TRUE;

		error("Mollify already installed (version ".$version.")");
		return FALSE;
	}
	
	function get_db_configuration() {
		global $DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE;
		
		if (isset($DB_HOST)) $host = $DB_HOST;
		else $host = "localhost";
		
		if (isset($DB_DATABASE)) $database = $DB_DATABASE;
		else $database = "mollify";
		
		return array("host" => $host, "db" => $database, "user" => $DB_USER, "password" => $DB_PASSWORD);		
	}
	
	function check_permissions() {
		$conf = get_db_configuration();
		$connection = @mysql_connect($conf['host'], $conf['user'], $conf['password'], TRUE, 2);
		if (!$connection) {
			error("Could not connect to database, error: <code>".mysql_error()."</code>");
			return FALSE;
		}

		if (!mysql_select_db($conf['db'])) {
			if (!mysql_query("CREATE DATABASE ".$conf['db']."")) {
				error("Could not create database, error: <code>".mysql_error()."</code>");
				return FALSE;
			}
			
			if (!mysql_select_db($conf['db'])) { 
				error("Could not select database, error: <code>".mysql_error()."</code>");
				return FALSE;
			}
		}

		$query = 'CREATE TABLE mollify_install_test (id int NULL)';
		$result = mysql_query($query);
		if ($error = mysql_error()) {
			error("Could not create database table, error: <code>".$error."</code>");
			return FALSE;
		}

		$query = 'INSERT INTO mollify_install_test (id) VALUES (1)';
		$result = mysql_query($query);
		if ($error = mysql_error()) {
			error("Could not insert data into database table, error: <code>".$error."</code>");
			return FALSE;
		}

		$query = 'UPDATE mollify_install_test SET id = 2';
		$result = mysql_query($query);
		if ($error = mysql_error()) {
			error("Could not update data into database table, error: <code>".$error."</code>");
			return FALSE;
		}

		$query = 'DELETE FROM mollify_install_test';
		$result = mysql_query($query);
		if ($error = mysql_error()) {
			error("Could not delete data from database table, error: <code>".$error."</code>");
			return FALSE;
		}
		
		$query = 'DROP TABLE mollify_install_test';
		$result = mysql_query($query);
		if ($error = mysql_error()) {
			error("Could not drop database table, error: <code>".$error."</code>");
			return FALSE;
		}

		mysql_close($connection);
		return TRUE;
	}
?>