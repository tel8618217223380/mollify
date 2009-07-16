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
		"UNAUTHENTICATED" => "Authentication failed.",
		"NON_ADMIN" => "Logged in user is not an administrator.",
		"INSTALLED_VERSION_UNKNOWN" => "Installed version unknown.",
		"UPDATE_FAILED" => "Could not update Mollify database"
	);
	
	$CONFIGURATION = "../../configuration.php";
	$SYSTEM = "../../include/system.php";
	$SESSION = "../../include/session.php";
	
	header("Content-type: text/plain");	
	if (!$_POST or !isset($_POST["action"]) or $_POST["action"] === "") return;

	require("../../include/mysql/common.php");
	require("common.php");	
		
	if (!file_exists($CONFIGURATION)) {
		failure(get_error_value("CONFIGURATION_DOES_NOT_EXIST"));
		return;
	}
	require($CONFIGURATION);
	
	if (!file_exists($SYSTEM) or !file_exists($SESSION)) {
		failure(get_error_value("INVALID_INSTALLATION"));
		return;
	}
	require($SYSTEM);
	require($SESSION);
	
	initialize_logging();
	session_start();
	import_configuration_provider();

	$action = $_POST["action"];
	if (!check_authentication()) {
		if ($action === "check_auth") {
			if (!do_auth()) {
				failure(get_error_value("UNAUTHENTICATED"));
				return;
			}
		} else {
			failure(get_error_value("UNAUTHENTICATED"));
			return;
		}
	}
	if (!is_admin()) {
		failure(get_error_value("NON_ADMIN"));
		return;
	}
	
	$result = "";
	$error = NULL;
	$error_detail = "";
	
	switch($action) {
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
	
	function do_auth() {
		$user = find_user($_POST["username"], $_POST["password"]);
		if (!$user) return FALSE;
		
		$_SESSION['user_id'] = $user["id"];
		$_SESSION['default_file_permission'] = get_default_user_permission_mode($user["id"]);
		return TRUE;
	}
?>