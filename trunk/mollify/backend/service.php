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

	require_once("configuration.php");
	require_once("include/errors.php");
		
	function return_json($result_array) {
		log_message($result_array);
		$ext = isset($_GET["callback"]);
		if ($ext) echo $_GET["callback"]."(";
		echo json_encode($result_array);
		if ($ext) echo ');';
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
	
	function fatal_error($error = "") {
		if ($error === "") $error = "UNEXPECTED_ERROR";
		return_json(get_error_message($error));
		die();
	}
	
	function initialize_session() {
		if (check_authentication()) return TRUE;
		return_json(get_error_message("INVALID_REQUEST"));
		return FALSE;
	}
	
	require_once("include/system.php");	
	import_configuration_provider();
	initialize_logging();
	
	if (!isset($_REQUEST["type"])) exit(0);
	$request_type = trim(strtolower($_REQUEST["type"]));
	require_once("include/session.php");
	
	session_start();
	if ($request_type === "session") {
		process_session_request();
		exit(0);
	}
	if (!initialize_session()) return;
	require_once("include/files.php");
		
	$result = FALSE;
	$error = "";
	$error_details = "";
	
	if ($request_type === "configuration") {
		require_once("include/configuration_services.php");
		process_configuration_request();
	} else if ($request_type === "filesystem") {
		require_once("include/filesystem_services.php");
		process_filesystem_request();
	} else {
		$error = "INVALID_REQUEST";
		$error_details = "Unsupported request type: ".$request_type;
		log_error($error_details);
	}
	
	if ($result === FALSE) {
		$result = get_error_message($error, $error_details);
		log_error($result["error"].":".$result["details"]);
	} else {
		if ($result === TRUE) $result = array();
		$result = get_success_message($result);
	}
	return_json($result);
?>
