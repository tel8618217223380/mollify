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
	
	$FILE_PERMISSION_VALUE_ADMIN = "A";
	$FILE_PERMISSION_VALUE_READWRITE = "RW";
	$FILE_PERMISSION_VALUE_READONLY = "RO";
	
	function process_session_request() {
		if (!isset($_GET["action"])) {
			return_json(get_error_message("INVALID_REQUEST"));
			exit(0);
		}
		global $error, $error_details;
		$action = $_GET["action"];
		$result = FALSE;
		
		switch (strtolower($action)) {
			case "authenticate":
				if (authenticate()) $result = get_session_info();
				break;
			case "session_info":
				$result = get_session_info();
				break;
			case "logout":
				if (logout()) $result = get_session_info();
				break;
			case "change_pw":
				if (!get_configuration_setting("configuration_update")) {
					log_error("Cannot change password, feature not supported");
					$error = "FEATURE_NOT_SUPPORTED";
					break;
				}
				if (!isset($_GET["old"]) or !isset($_GET["new"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = change_password($_SESSION['user_id'], $_GET["old"], $_GET["new"]);
				break;
			case "reset_pw":
				if (!is_admin()) break;
				if (!get_configuration_setting("configuration_update")) {
					log_error("Cannot reset password, feature not supported");
					$error = "FEATURE_NOT_SUPPORTED";
					break;
				}
				if (!isset($_GET["id"]) or !isset($_GET["new"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = reset_password($_GET['id'], $_GET["new"]);
				break;
			default:
				$error = "INVALID_REQUEST";
		}
		
		if (!$result) {
			if (!$error or $error === "")
				return_json(get_error_message("UNAUTHORIZED"));
			else
				return_json(get_error_message($error, $error_details));
		} else {
			return_json(get_success_message($result));
		}
		exit(0);
	}
	
	function get_session_info() {
		$info = array("authentication_required" => authentication_required(), "authenticated" => FALSE);
		$auth = check_authentication();
		
		if ($auth) {
			$info["authenticated"] = TRUE;
			$info["username"] = $_SESSION['username'];
			$info["user_id"] = $_SESSION['user_id'];
			$info["settings"] = $_SESSION['settings'];
			$info["default_permission_mode"] = $_SESSION['default_file_permission'];
			$info["filesystem"] = get_filesystem_session_info();
			$info["configuration"] = get_configuration_info();
			$info["roots"] = get_root_directory_info();
		}
		return $info;
	}
	
	function get_root_directory_info() {
		$result = array();
		foreach($_SESSION["roots"] as $id => $root) {
			$result[] = array(
				"id" => get_filesystem_id($id),
				"name" => $root["name"]
			);
		}
		return $result;
	}
	
	function initialize_session_data($user_id = "", $username = "") {
		global $error, $error_details;
		if (!on_session_start($user_id, $username)) return FALSE;
		
		$_SESSION['user_id'] = $user_id;
		$_SESSION['username'] = $username;
		$_SESSION['default_file_permission'] = get_default_user_permission_mode($user_id);
		$_SESSION['settings'] = get_effective_settings();
		$_SESSION['roots'] = get_user_root_directories($user_id);
		
		foreach($_SESSION["roots"] as $id => $root) {
			if (!isset($root["name"])) {
				log_error("Invalid published directory definition for id ".$id);
				$error = "INVALID_CONFIGURATION";
				$error_details = "Root directory definition does not have a name (".$id.")";
				session_destroy();
				return FALSE;
			}
			if (!file_exists($root["path"])) {
				log_error("Root directory does not exist, id ".$id);
				$error = "INVALID_CONFIGURATION";
				$error_details = "Root directory does not exist (".$id.")";
				session_destroy();
				return FALSE;
			}
		}
		return TRUE;
	}
	
	function authenticate() {
		if (!isset($_GET["username"]) || !isset($_GET["password"])) {
			log_error("Invalid authentication request, no username or password provided");
			return FALSE;
		}
		
		$user = find_user(base64_decode($_GET["username"]), $_GET["password"]);
		if (!$user) {
			log_error("Authentication failed");
			return FALSE;
		}
		
		return initialize_session_data($user["id"], $user["name"]);
	}
	
	function logout() {
		$_SESSION = array();

		if (isset($_COOKIE[session_name()])) {
		    setcookie(session_name(), '', time()-42000, '/');
		}
		session_destroy();
		
		return TRUE;
	}
	
	function check_authentication() {
		// always pass authentication when it is not required
		if (!authentication_required()) {
			if (!isset($_SESSION['user_id'])) initialize_session_data();
			return TRUE;
		}
		// otherwise user must authenticate
		if (!isset($_SESSION['user_id']) or $_SESSION['user_id'] === "") return FALSE;
		return TRUE;
	}
	
	function has_general_modify_rights() {
		global $FILE_PERMISSION_VALUE_ADMIN, $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY;
		$base = $_SESSION['default_file_permission'];
		return ($base === $FILE_PERMISSION_VALUE_ADMIN || $base === $FILE_PERMISSION_VALUE_READWRITE);
	}
	
	function is_admin() {
		global $FILE_PERMISSION_VALUE_ADMIN;
		return ($_SESSION['default_file_permission'] === $FILE_PERMISSION_VALUE_ADMIN);
	}
?>