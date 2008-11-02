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
	
	$USER_TYPE_ADMIN = "A";
	$USER_TYPE_READWRITE = "RW";
	$USER_TYPE_READONLY = "RO";
	
	function authenticate() {
		global $USERS;
		
		if (!authentication_required()) {
			$_SESSION['user_id'] = "";
			$_SESSION['user_type'] = get_user_type();
			return array("name" => "", "type" => $_SESSION['user_type']);
		}
		
		if (!isset($_GET["username"]) || !isset($_GET["password"])) {
			error_log("MOLLIFY: Invalid authentication request, no username or password provided");
			return FALSE;
		}
		
		foreach($USERS as $id => $user) {
			if ($user["name"] != $_GET["username"])
				continue;
			if ($user["password"] != $_GET["password"]) {
				error_log("MOLLIFY: Authentication failed for user [".$user["name"]."], invalid password");
			 	return FALSE;
			}
			
			$_SESSION['user_id'] = $id;
			$_SESSION['user_type'] = get_user_type($id);
			return array("name" => $user["name"], "type" => $_SESSION['user_type']);
		}
		return FALSE;
	}
	
	function get_user_type($id = "") {
		global $USERS, $USER_TYPE_ADMIN, $USER_TYPE_READWRITE, $USER_TYPE_READONLY, $PERMISSION_MODE;
		
		if ($id === "") {
			if (!isset($PERMISSION_MODE)) return $USER_TYPE_READONLY;
			$type = strtoupper($PERMISSION_MODE);
		} else {
			if (!isset($USERS[$id]["type"])) return $USER_TYPE_READONLY;
			$type = strtoupper($USERS[$id]["type"]);
		}

		if ($type != $USER_TYPE_ADMIN and $type != $USER_TYPE_READWRITE and $type != $USER_TYPE_READONLY) {
			if ($id === "") error_log("MOLLIFY: Invalid permission mode defined [".$type."]. Fallback to default.");
			else error_log("MOLLIFY: User ".$id." has invalid type defined [".$type."]. Fallback to default.");
			return $USER_TYPE_READONLY;
		}
		return $type;
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
		if (!authentication_required()) return TRUE;
		// otherwise user must authenticate
		if (!isset($_SESSION['user_id'])) return FALSE;
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