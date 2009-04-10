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
	
	$CONFIGURATION = "../../configuration.php";
	
	$ERRORS = array(
		"CONFIGURATION_DOES_NOT_EXIST" => "Configuration does not exist",
		"CONFIGURATION_PROVIDER_NOT_DEFINED" => "Configuration provider not defined",
		"CONFIGURATION_PROVIDER_INCORRECT" => "Configuration provider is not correct",
		"MYSQL_NOT_DETECTED" => "MySQL not detected",
		"MYSQLI_NOT_DETECTED" => "MySQLI extension not detected",
		"DB_USER_NOT_DEFINED" => "MySQL user not defined",
		"DB_PW_NOT_DEFINED" => "MySQL password not defined",
		"MOLLIFY_ALREADY_INSTALLED" => "Mollify already installed",
		"COULD_NOT_CREATE_DB" => "Could not create database",
		"DB_PERMISSION_TEST_FAILED" => "Insufficient database permissions",
		"COULD_NOT_CONNECT_DB" => "Could not connect to database",
		"COULD_NOT_OPEN_SQL_FILE" => "Could not open SQL file",
		"COULD_NOT_EXEC_SQL" => "Could not execute SQL query",
		"COULD_NOT_CREATE_TABLES" => "Could not create database tables",
		"COULD_NOT_CREATE_ADMIN" => "Could not create admin user",
		"COULD_NOT_INSERT_PARAMS" => "Could not insert parameters",
		"INSTALLATION_FAILED" => "Installation failed"
	);
		
	function check_configuration() {
		global $CONFIGURATION, $error, $error_detail;
		
		if (!file_exists($CONFIGURATION)) {
			$error = "CONFIGURATION_DOES_NOT_EXIST";
			return FALSE;
		}
		
		global $CONFIGURATION_PROVIDER;
		
		if (!isset($CONFIGURATION_PROVIDER)) {
			$error = "CONFIGURATION_PROVIDER_NOT_DEFINED";
			return FALSE;
		}
		
		if (trim(strtolower($CONFIGURATION_PROVIDER)) != 'mysql') {
			$error = "CONFIGURATION_PROVIDER_INCORRECT";
			$error_detail = $CONFIGURATION_PROVIDER;
			return FALSE;
		}

		return TRUE;
	}
	
	function check_mysql() {
		global $error;
		
		if (!function_exists('mysql_connect')) {
			$error = "MYSQL_NOT_DETECTED";
			return FALSE;
		}
		if (!function_exists('mysqli_multi_query')) {
			$error = "MYSQLI_NOT_DETECTED";
			return FALSE;
		}		
		return TRUE;
	}
	
	function get_db() {
		global $error, $DEFAULT_HOST, $DEFAULT_DB;
		$db = get_db_configuration();
		
		if ($db["user"] === NULL) {
			$error = "DB_USER_NOT_DEFINED";
			return FALSE;
		}
		
		if ($db["password"] === NULL) {
			$error = "DB_PW_NOT_DEFINED";
			return FALSE;
		}
		
		$db["host_defined"] = TRUE;
		if ($db["host"] === NULL) {
			$db["host"] = $DEFAULT_HOST;
			$db["host_defined"] = FALSE;
		}
		
		$db["database_defined"] = TRUE;
		if ($db["database"] === NULL) {
			$db["database"] = $DEFAULT_DB;
			$db["database_defined"] = FALSE;
		}
				
		return $db;
	}
	
	function check_installed($db) {
		global $error, $error_detail;
		
		$version = get_installed_version($db);
		if (!$version) return TRUE;
		
		$error = "MOLLIFY_ALREADY_INSTALLED";
		$error_detail = $version;
		return FALSE;
	}
	
	function success($result) {
		echo json_encode(array("success" => TRUE, "result" => $result));
	}
	
	function failure($error) {
		echo json_encode(array("success" => FALSE, "error" => $error));
	}
	
	function get_error_value($error, $error_detail) {
		global $ERRORS;
		$desc = NULL;
		if (array_key_exists($error, $ERRORS)) $desc = $ERRORS[$error];
		return array("id" => $error, "details" => $error_detail, "desc" => $desc);
	}

	header("Content-type: text/plain");	

	if (!$_POST or !isset($_POST["action"]) or $_POST["action"] === "") return;
	require("mysql.php");
	if (file_exists($CONFIGURATION)) include($CONFIGURATION);
	
	$result = "";
	$error = NULL;
	$error_detail = "";
	
	switch($_POST["action"]) {
		case "check_db_conf":
			if (!check_configuration()) break;
			if (!check_mysql()) break;
			
			$db = get_db();
			if (!$db) break;
			if (!check_installed($db)) break;
			
			$connection = check_connection($db);
			$db_exists = FALSE;
			if ($connection) $db_exists = check_database($db);
			
			unset($db["password"]);
			$result = array("db" => $db, "connection_success" => $connection, "database_exists" => $db_exists);
			break;

		case "create_and_check_db":
			$db = get_db();
			if (!$db) break;

			$db_exists = check_database($db);
					
			if (isset($_POST["create"]) and $_POST["create"] === "true") {
				if (!$db_exists and !create_database($db)) break;
			} else {
				if (!$db_exists) {
					$error = "COULD_NOT_CONNECT_DB";
					$error_detail = "Database does not exist";
					break;
				}
			}
				
			check_db_permissions($db);
			break;
		
		case "install":
			if (!isset($_POST["username"]) or strlen($_POST["username"]) === 0 or !isset($_POST["password"]) or strlen($_POST["password"]) === 0) {
				$error = "MISSING_USER_DATA";
				break;
			}
			
			$db = get_db();
			if (!$db) break;
			$connection = get_connection($db);
			
			if (!$connection) break;
			if (!create_tables($connection)) break;
			if (!insert_admin_user($connection, $_POST["username"], $_POST["password"])) break;
			if (!insert_params($connection)) break;			
			if (!close_connection($connection, "INSTALLATION_FAILED")) break;

			break;

		default:
			return;
	}
	
	if ($error != NULL) failure(get_error_value($error, $error_detail));
	else success($result);
?>