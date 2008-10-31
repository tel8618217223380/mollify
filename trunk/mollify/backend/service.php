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

	include "errors.php";
	
	function return_json($result_array) {
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
	
	function get_session_info() {
		$user = check_authentication();
		$authenticated = FALSE;
		$name = "";
		if ($user) {
			$authenticated = TRUE;
			if (isset($user["name"])) $name = $user["name"];
		}
		return array("authentication_required" => authentication_required(), "authenticated" => $authenticated, "user" => $name);
	}
	
	function handle_authentication() {
		$action = $_GET["action"];
		$result = FALSE;
		if ($action === "auth") {
			if (authenticate()) $result = get_session_info();
		} else if ($action === "session_info") {
			$result = get_session_info();
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

	if (!isset($_GET["action"])) {
		return;
	}
	
	include "configuration.php";
	require "user.php";
	
	session_start();
	if (!handle_authentication()) return;
	
	$account = get_account();
	if (!$account) {
		return_json(get_error_message("UNAUTHORIZED"));
		return;
	}
	
	$result = FALSE;
	$error = "";
	$error_details = "";
	
	// handle actual request
	include "facade.php";

	// return JSON
	if ($result === FALSE) {
		$result = get_error_message($error, $error_details);
	} else {
		$result = get_success_message($result);
	}
	return_json($result);
?>
