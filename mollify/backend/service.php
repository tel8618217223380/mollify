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
		"UNAUTHORIZED" => array(100, "Unauthorized request"), 
		"INVALID_REQUEST" => array(101, "Invalid request"),
		"UNSUPPORTED_ACTION" => array(102, "Unsupported action"),
		"UNSUPPORTED_OPERATION" => array(103, "Unsupported operation"),
		
		"INVALID_PATH" => array(201, "Invalid path"), 
		"FILE_DOES_NOT_EXIST" => array(202, "File does not exist"), 
		"FILE_ALREADY_EXISTS" => array(203, "File already exists"), 
		"NOT_A_FILE" => array(204, "Target is not a file"), 
		"DELETE_FAILED" => array(205, "Could not delete"), 
		"NO_UPLOAD_DATA" => array(206, "No upload data available"), 
		"UPLOAD_FAILED" => array(207, "File upload failed"), 
		"SAVING_FAILED" => array(208, "Saving file failed")
	);
	
	function return_json($result_array) {
		$ext = isset($_GET["callback"]);
		if ($ext) echo $_GET["callback"]."(";
		echo json_encode($result_array);
		if ($ext) echo ');';
	}
	
	function get_success_message($result) {
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
	
	function handle_authentication() {
		$action = $_GET["action"];
		$result = FALSE;
		if ($action === "auth") {
			$result = authenticate();
		} else {
			$result = check_authentication();
			if ($result && ($action != "check_auth")) return TRUE;
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
	
	require "files.php";
	$result = FALSE;
	$error = "";
	$error_details = "";
	
	switch ($_GET["action"]) {
		case "get":
			if (!isset($_GET["type"])) {
				$error = "INVALID_REQUEST";
				break;
			}
			
			switch ($_GET["type"]) {
				case "roots":
					$result = array();
					foreach($account["roots"] as $id => $root) {
						$result[] = array(
							"id" => get_filesystem_id($id),
							"name" => $root["name"]
						);
					}
					break;
					
				case "files":
					$result = get_files($account);
					break;
					
				case "dirs":
					$result = get_directories($account);
					break;

				case "details":
					$file = get_fileitem_from_url("id");
					if (!$file) return;
					$result = get_file_details($file);
					break;
			}
			
			break;
		case "operate":
			if (!isset($_GET["type"])) {
				$error = "INVALID_REQUEST";
				break;
			}
			$operation = $_GET["type"];
			
			$file = get_fileitem_from_url("id");
			if (!$file) return;
			
			switch (strtolower($operation)) {
				case "download":
					// download writes the header and the content, just exit here
					if (download($file)) return;
					break;
				
				case "rename":
					if (!isset($_GET["to"])) return;
					if (rename_file($file, urldecode($_GET["to"])))
						$result = get_success_message();
					break;
					
				case "delete":
					if (delete_file($file))
						$result = get_success_message();
					break;
				
				case "upload":
					$dir = $file;
					if (upload_file($dir)) $result = get_success_message();
					header("Content-Type: text/html");
					header("HTTP/1.1 200 OK", true);
					break;

				default:
					$error = "UNSUPPORTED_OPERATION";
					$error_details = $operation;
					break;
			}
			break;
		default:
			$error = "UNSUPPORTED_ACTION";
			$error_details = $_GET["action"];
			break;	
	}

	// return JSON
	if ($result === FALSE) {
		$result = get_error_message($error, $error_details);
	} else {
		$result = get_success_message($result);
	}
	return_json($result);
?>
