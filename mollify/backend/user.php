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
	
	function handle_session_request() {
		if (!isset($_GET["action"])) {
			return_json(get_error_message("INVALID_REQUEST"));
			exit(0);
		}
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
				if (!is_configuration_update_supported()) {
					log_error("Cannot change password, feature not supported");
					return_json(get_error_message("FEATURE_NOT_SUPPORTED"));
					return;
				}
				if (!isset($_GET["old"]) or !isset($_GET["new"])) {
					return_json(get_error_message("INVALID_REQUEST"));
					return;
				}
				$result = change_password($_SESSION['user_id'], $_GET["old"], $_GET["new"]);
				break;
			default:
				return_json(get_error_message("INVALID_REQUEST"));
				exit(0);
		}
		
		if (!$result) {
			return_json(get_error_message("UNAUTHORIZED"));
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
			$info["user"] = $_SESSION['username'];
			$info["settings"] = $_SESSION['settings'];
			$info["default_permission_mode"] = $_SESSION['default_file_permission'];
			$info["filesystem"] = get_filesystem_session_info();
			$info["configuration"] = get_configuration_info();
		}
		return $info;
	}
	
	function initialize_session_data($user_id = "", $username = "") {
		$_SESSION['user_id'] = $user_id;
		$_SESSION['username'] = $username;
		$_SESSION['default_file_permission'] = get_default_user_permission_mode($user_id);
		$_SESSION['settings'] = get_effective_settings();
		$_SESSION['roots'] = get_roots($user_id);
	}
	
	function authenticate() {
		if (!isset($_GET["username"]) || !isset($_GET["password"])) {
			log_error("Invalid authentication request, no username or password provided");
			return FALSE;
		}
		
		$user = find_user($_GET["username"], $_GET["password"]);
		if (!$user) {
			log_error("Authentication failed");
			return FALSE;
		}
		
		initialize_session_data($user["id"], $user["name"]);
		return TRUE;
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