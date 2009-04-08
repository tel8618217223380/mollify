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
	
	function initialize_session() {
		if (check_authentication()) return TRUE;
		return_json(get_error_message("UNAUTHORIZED"));
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
	
	if (!isset($_GET["type"])) {
		exit(0);
	}
	
	require_once("settings.php");
	require_once("user.php");
	require_once("files.php");
	
	session_start();
	if (strtolower($_GET["type"]) === "session") {
		process_session_request();
		exit(0);
	}
	if (!initialize_session()) return;
	
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
