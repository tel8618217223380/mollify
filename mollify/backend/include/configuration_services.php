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
	
	function process_configuration_request() {
		global $result, $error, $error_details;
		
		if (!isset($_REQUEST["action"])) {
			$error = "INVALID_REQUEST";
			return;
		}
		if (!is_admin()) {
			$error = "UNAUTHORIZED";
			return;
		}
		
		$action = strtolower($_REQUEST["action"]);
		$NON_UPDATE_ACTIONS = array("get_users", "get_folders", "get_user_folders");
		if (!in_array($action, $NON_UPDATE_ACTIONS) and !get_configuration_setting("configuration_update")) {
			$error = "INVALID_REQUEST";
			return;
		}

		switch ($action) {
			case "get_users":
				$result = get_all_users();
				break;
			case "add_user":
				if (!isset($_GET["name"]) or !isset($_GET["password"]) or !isset($_GET["permission_mode"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = add_user(base64_decode($_GET["name"]), $_GET["password"], $_GET["permission_mode"]);
				break;
			case "remove_user":
				if (!isset($_GET["id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = remove_user($_GET["id"]);
				break;
			case "update_user":
				if (!isset($_GET["id"]) or !isset($_GET["name"]) or !isset($_GET["permission_mode"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = update_user($_GET["id"], base64_decode($_GET["name"]), $_GET["permission_mode"]);
				break;
			case "get_folders":
				$result = get_all_folders();
				break;
			case "add_folder":
				if (!isset($_GET["name"]) or !isset($_GET["path"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$path = base64_decode($_GET["path"]);
				if (!assert_dir(array("path" => $path))) break;
				
				$result = add_folder(base64_decode($_GET["name"]), $path);
				break;
			case "remove_folder":
				if (!isset($_GET["id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = remove_folder($_GET["id"]);
				break;
			case "update_folder":
				if (!isset($_GET["id"]) or !isset($_GET["name"]) or !isset($_GET["path"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$path = base64_decode($_GET["path"]);
				if (!assert_dir(array("path" => $path))) break;

				$result = update_folder($_GET["id"], base64_decode($_GET["name"]), $path);
				break;
			case "get_user_folders":
				if (!isset($_GET["user_id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = get_user_folders($_GET["user_id"]);
				break;
			case "add_user_folder":
				if (!isset($_GET["user_id"]) or !isset($_GET["id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				
				$name = NULL;
				if (isset($_GET["name"]))
					$name = base64_decode($_GET["name"]);
				
				$result = add_user_folder($_GET["user_id"], $_GET["id"], $name);
				break;
			case "update_user_folder":
				if (!isset($_GET["user_id"]) or !isset($_GET["id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				
				$name = NULL;
				if (isset($_GET["name"])) $name = base64_decode($_GET["name"]);
				
				$result = update_user_folder($_GET["user_id"], $_GET["id"], $name);
				break;
			case "remove_user_folder":
				if (!isset($_GET["user_id"]) or !isset($_GET["id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$result = remove_user_folder($_GET["user_id"], $_GET["id"]);
				break;

			default:
				$error = "UNSUPPORTED_OPERATION";
				$error_details = $action;
				break;
		}
	}
?>