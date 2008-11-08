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
	
	function initialize_session_data($user_id = "") {
		$_SESSION['user_id'] = $user_id;
		$_SESSION['default_file_permission'] = get_default_permission_mode($user_id);
		$_SESSION['settings'] = get_effective_settings();
	}
	
	function authenticate() {
		global $USERS;
		
		if (!isset($_GET["username"]) || !isset($_GET["password"])) {
			log_error("Invalid authentication request, no username or password provided");
			return FALSE;
		}
		
		foreach($USERS as $id => $user) {
			if ($user["name"] != $_GET["username"])
				continue;
			if ($user["password"] != $_GET["password"]) {
				log_error("Authentication failed for user [".$user["name"]."], invalid password");
			 	return FALSE;
			}
			
			initialize_session_data($id);
			return array("name" => $user["name"]);
		}
		
		log_error("Authentication failed, no user found with name [".$_GET["username"]."]");
		return FALSE;
	}
	
	function get_default_permission_mode($id = "") {
		global $USERS, $FILE_PERMISSION_VALUE_ADMIN, $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY, $FILE_PERMISSION_MODE;
		
		if ($id === "") {
			if (!isset($FILE_PERMISSION_MODE)) return $FILE_PERMISSION_VALUE_READONLY;
			$mode = strtoupper($FILE_PERMISSION_MODE);
		} else {
			if (!isset($USERS[$id]["file_permission_mode"])) return $FILE_PERMISSION_VALUE_READONLY;
			$mode = strtoupper($USERS[$id]["file_permission_mode"]);
		}

		if ($mode != $FILE_PERMISSION_VALUE_ADMIN and $mode != $FILE_PERMISSION_VALUE_READWRITE and $mode != $FILE_PERMISSION_VALUE_READONLY) {
			if ($id === "") log_error("Invalid file permission mode [".$mode."]. Falling back to default.");
			else log_error("Invalid file permission mode ".$mode." for user [".$id."]. Falling back to default.");
			return $FILE_PERMISSION_VALUE_READONLY;
		}
		return $mode;
	}
	
	function logout() {
		$_SESSION = array();

		if (isset($_COOKIE[session_name()])) {
		    setcookie(session_name(), '', time()-42000, '/');
		}
		session_destroy();
		
		return TRUE;
	}
	
	function authentication_required() {
		global $USERS;
		return ($USERS != FALSE and count($USERS) > 0);
	}
	
	function check_authentication() {
		global $USERS;

		// always pass authentication when it is not required
		if (!authentication_required()) {
			if (!isset($_SESSION['user_id'])) initialize_session_data();
			return TRUE;
		}
		// otherwise user must authenticate
		if (!isset($_SESSION['user_id']) or $_SESSION['user_id'] === "") return FALSE;
		return array("name" => $USERS[$_SESSION['user_id']]["name"]);
	}
	
	function get_root_path($id) {
		$roots = get_roots();
		if (!array_key_exists($id, $roots)) return FALSE;
		return $roots[$id]["path"];
	}
	
	function get_roots() {
		global $USERS, $PUBLISHED_DIRECTORIES;

		if (count($USERS) === 0) {
			return $PUBLISHED_DIRECTORIES;
		} else {
			return $PUBLISHED_DIRECTORIES[$_SESSION['user_id']];
		}
	}
	
	function get_account() {
		return array("roots" => get_roots());
	}
?>