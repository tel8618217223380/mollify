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
	
	function authenticate() {
		global $USERS;
		
		if (!$USERS) {
			return TRUE;
		}
		
		if (!isset($_GET["username"]) || !isset($_GET["password"])) {
			return FALSE;
		}
		
		foreach($USERS as $id => $user) {
			if ($user["name"] != $_GET["username"])
				continue;
			if ($user["password"] != $_GET["password"])
			 	return FALSE;
			
			$_SESSION['user_id'] = $id;
			return array("name" => $user["name"]);
		}
		return FALSE;
	}
	
	function check_authentication() {
		global $USERS;

		// if no users are defined, always pass authentication
		if (count($USERS) === 0) return TRUE;
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
			// if no users are defined, return first directory set
			reset($PUBLISHED_DIRECTORIES);
			return current($PUBLISHED_DIRECTORIES);
		} else {
			return $PUBLISHED_DIRECTORIES[$_SESSION['user_id']];
		}
	}
	
	function get_account() {
		//TODO get user rights etc and add to result array
		return array("roots" => get_roots());
	}
?>