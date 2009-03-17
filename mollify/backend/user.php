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
	
	function initialize_session_data($user_id = "", $username = "") {
		$_SESSION['user_id'] = $user_id;
		$_SESSION['username'] = $username;
		$_SESSION['default_file_permission'] = get_default_user_permission_mode($user_id);
		$_SESSION['settings'] = get_effective_settings();
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
?>