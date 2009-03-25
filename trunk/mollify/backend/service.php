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

	require_once("errors.php");
	require_once("configuration.php");
	
	function return_json($result_array) {
		$ext = isset($_GET["callback"]);
		if ($ext) echo $_GET["callback"]."(";
		echo json_encode($result_array);
		if ($ext) echo ');';
	}

	function log_error($message) {
		error_log("MOLLIFY: ".$message);
	}
	
	function get_success_message($result = array()) {
		return array("success" => TRUE, "result" => $result);
	}
	
	function get_error_message($error, $details = "") {
		global $ERRORS;
		
		if (!isset($ERRORS[$error])) {
			return array("success" => FALSE, "code" => 0, "error" => "Unknown error: " + $error, "details" => $details);
		}
		$err = $ERRORS[$error];
		return array("success" => FALSE, "code" => $err[0], "error" => $err[1], "details" => $details);
	}
	
	function get_configuration_info() {
		return array("supports_configuration_update" => is_configuration_update_supported());
	}
	
	function get_session_info() {
		$info = array("authentication_required" => authentication_required(), "authenticated" => FALSE);
		$auth = check_authentication();
		
		if ($auth) {
			$info["authenticated"] = TRUE;
			$info["user"] = $_SESSION['username'];
			$info["settings"] = $_SESSION['settings'];
			$info["default_permission_mode"] = $_SESSION['default_file_permission'];
			$info["filesystem"] = get_filesystem_session_info();
			$info["configuration"] = get_configuration_info();
		}
		return $info;
	}
	
	function handle_authentication() {
		$action = $_GET["action"];
		$result = FALSE;
		
		if ($action === "auth") {
			if (authenticate()) $result = get_session_info();
		} else if ($action === "session_info") {
			$result = get_session_info();
		} else if ($action === "logout") {
			if (logout()) $result = get_session_info();
		} else if ($action === "change_pw") {
			if (!is_configuration_update_supported()) {
				log_error("Cannot change password, feature not supported");
				return_json(get_error_message("FEATURE_NOT_SUPPORTED"));
				return FALSE;
			}
			if (!isset($_GET["old"]) or !isset($_GET["new"])) {
				return_json(get_error_message("INVALID_REQUEST"));
				return FALSE;
			}
			$result = change_password($_SESSION['user_id'], $_GET["old"], $_GET["new"]);
		} else {
			if (check_authentication()) return TRUE;
		}
		if (!$result) {
			return_json(get_error_message("UNAUTHORIZED"));
		} else {
			return_json(get_success_message($result));
		}
		return FALSE;
	}
	
	function import_configuration_provider() {
		global $CONFIGURATION_PROVIDER;
		
		if (isset($CONFIGURATION_PROVIDER)) {
			$provider = trim(strtolower($CONFIGURATION_PROVIDER));
			
			if ($provider === 'file') {
				require_once "configuration_provider_file.php";
			} else if ($provider === 'mysql') {
				require_once "configuration_provider_mysql.php";
			} else {
				log_error("Unsupported data provider: ".$CONFIGURATION_PROVIDER);
				die("Unsupported data provider: ".$CONFIGURATION_PROVIDER);
			}
		} else {
			require_once "data_provider_file.php";
		}
		
		init_configuration_provider();
	}
	
	import_configuration_provider();
	
	if (!isset($_GET["action"])) {
		return;
	}
	
	require_once("settings.php");
	require_once("user.php");
	require_once("files.php");
	
	session_start();
	if (!handle_authentication()) return;
	
	$result = FALSE;
	$error = "";
	$error_details = "";
	
	// handle actual request
	require_once("facade.php");
	process_request();
	
	// return JSON
	if ($result === FALSE) {
		$result = get_error_message($error, $error_details);
	} else {
		$result = get_success_message($result);
	}
	return_json($result);
?>
