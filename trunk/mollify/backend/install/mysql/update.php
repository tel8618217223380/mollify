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

	$ERRORS = array(
		"CONFIGURATION_DOES_NOT_EXIST" => "Configuration does not exist",
		"INVALID_INSTALLATION" => "Invalid installation, service files don't exist or are misplaced.",
		"UNAUTHENTICATED" => "No authentication has been done.",
		"NON_ADMIN" => "Logged in user is not an administrator.",
		"INSTALLED_VERSION_UNKNOWN" => "Installed version unknown.",
		"UPDATE_FAILED" => "Could not update Mollify database"
	);
	
	$CONFIGURATION = "../../configuration.php";
	$SYSTEM = "../../include/system.php";
	$USER = "../../include/user.php";
	
	header("Content-type: text/plain");	
	if (!$_POST or !isset($_POST["action"]) or $_POST["action"] === "") return;

	require("common.php");	
		
	if (!file_exists($CONFIGURATION)) {
		failure(get_error_value("CONFIGURATION_DOES_NOT_EXIST"));
		return;
	}
	require($CONFIGURATION);
	
	if (!file_exists($SYSTEM) or !file_exists($USER)) {
		failure(get_error_value("INVALID_INSTALLATION"));
		return;
	}
	require($SYSTEM);
	require($USER);
	session_start();
	import_configuration_provider();

	if (!check_authentication()) {
		failure(get_error_value("UNAUTHENTICATED"));
		return;
	}
	if (!is_admin()) {
		failure(get_error_value("NON_ADMIN"));
		return;
	}
	require("mysql.php");
	
	$result = "";
	$error = NULL;
	$error_detail = "";
	
	switch($_POST["action"]) {
		case "check_auth":
			$result = true;
			break;

		case "update_info":
			$db = get_db();
			if (!$db) break;
			
			$installed = get_installed_version($db);
			
			if (!is_version_in_history($installed)) {
				$error = "INSTALLED_VERSION_UNKNOWN";
				$error_details = $installed;
			} else {
				$result = array("installed" => convert_version($installed), "current" => convert_version(get_current_version()));
			}
			
			break;
		
		case "update":
			$db = get_db();
			if (!$db) break;
			
			$installed = get_installed_version($db);
			$connection = get_connection($db);
			
			if (!$connection) break;
			if (!update_db($connection, $installed)) break;
			if (!close_connection($connection, "UPDATE_FAILED")) break;

			logout();
			break;
			
		default:
			return;
	}
	
	if ($error != NULL) failure(get_error_value($error, $error_detail));
	else success($result);
?>