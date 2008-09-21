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
		
		foreach($USERS as $user) {
			if ($user["name"] === $_GET["username"]) {
				if ($user["password"] === $_GET["password"]) {
					$_SESSION['user_id'] = $user["id"];
					return $user;
				} else {
					return FALSE;
				}
			}
		}
		return FALSE;
	}
	
	function get_account() {
		global $USERS, $PUBLISHED_DIRECTORIES;


		if (count($USERS) === 0) {
			// if no users are defined, return first directory set
			reset($PUBLISHED_DIRECTORIES);
			$roots = current($PUBLISHED_DIRECTORIES);
		} else {
			// when users are defined, user must have been authenticated
			if (!isset($_SESSION['user_id'])) return FALSE;
			$roots = $PUBLISHED_DIRECTORIES[$_SESSION['user_id']];
		}
		
		//TODO get user rights etc and add to result array
		return array("roots" => $roots);
	}
?>