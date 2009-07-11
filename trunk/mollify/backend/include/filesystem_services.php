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
	 
	function process_filesystem_request() {
		global $result, $error, $error_details;

		if (!isset($_REQUEST["action"])) {
			$error = "INVALID_REQUEST";
			return;
		}
		$DATA_ACTIONS = array("get_upload_status", "update_item_permissions");
		$ITEM_ACTIONS = array("get_files", "get_directories", "get_contents", "get_item_details", "download", "download_as_zip", "rename", "copy", "move", "delete", "upload", "create_folder", "set_description", "remove_description", "get_item_permissions", "get_item_permission", "set_item_permission", "remove_item_permission");
				
		$action = strtolower($_REQUEST["action"]);
		
		if (in_array($action, $DATA_ACTIONS)) {
			$result = process_data_request($action);
		} else if (in_array($action, $ITEM_ACTIONS)) {
			$result = process_item_request($action);
		} else {
			$error = "INVALID_REQUEST";
			$error_details = "Unsupported action: ".$action;
		}
	}
	
	function process_item_request($action) {
		global $result, $error, $error_details;

		$item = get_fileitem_from_url("id");
		if (!$item) return FALSE;
		
		switch ($action) {
			case "get_contents":
				return array("directories" => get_directories($item),
					"files" => get_files($item));
								
			case "get_files":
				return get_files($item);
			
			case "get_directories":
				return get_directories($item);
					
			case "get_item_details":				
				if ($item["is_file"])
					return get_file_details($item);
				else
					return get_directory_details($item);
				
			case "download":
				// download writes the header and the content, just exit here
				if (download($item)) return TRUE;
				break;

			case "download_as_zip":
				// download writes the header and the content, just exit here
				if ($item["is_file"]) {
					if (download_file_as_zip($item)) return TRUE;
				} else {
					if (download_dir_as_zip($item)) return TRUE;
				}
				break;
				
			case "rename":
				if (!isset($_GET["to"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$to = urldecode($_GET["to"]);
				
				if ($item["is_file"]) {
					return rename_file($item, $to);
				} else {
					return rename_directory($item, $to);
				} 

			case "copy":
				if (!isset($_GET["to"]) or !$item["is_file"]) {
					$error = "INVALID_REQUEST";
					break;
				}
				
				$to = get_fileitem_from_url("to");
				if (!$to) {
					$error = "INVALID_REQUEST";
					break;
				}
				
				return copy_file($item, $to);

			case "move":
				if (!isset($_GET["to"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				
				$to = get_fileitem_from_url("to");
				if (!$to) {
					$error = "INVALID_REQUEST";
					break;
				}
				
				if ($item["is_file"]) {
					return move_file($item, $to);
				} else {
					return move_directory($item, $to);
				}
								
			case "delete":
				if ($item["is_file"]) {
					return delete_file($item);
				} else {
					return delete_directory($item);
				}
				break;
		
			case "upload":
				if (upload_file($item)) return TRUE;
				
				header("Content-Type: text/html");
				header("HTTP/1.1 200 OK", true);
				break;

			case "create_folder":
				if (!isset($_GET["name"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				return create_folder($item, $_GET["name"]);

			case "set_description":
				if (!isset($_GET["description"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				if (!$_SESSION["settings"]["enable_description_update"]) {
					log_error("Cannot edit descriptions, feature disabled by settings");
					$error = "FEATURE_DISABLED";
					break;
				}
				if (!is_admin()) {
					log_error("Insufficient permissions (set description): User=[".$_SESSION['user_id']."]");
					$error = "NOT_AN_ADMIN";
					break;
				}
				return set_item_description($item, urldecode($_GET["description"]));

			case "remove_description":
				if (!$_SESSION["settings"]["enable_description_update"]) {
					log_error("Cannot edit descriptions, feature disabled by settings");
					$error = "FEATURE_DISABLED";
					break;
				}
				if (!is_admin()) {
					log_error("Insufficient permissions (remove description): User=[".$_SESSION['user_id']."]");
					$error = "NOT_AN_ADMIN";
					break;
				}
				
				return remove_item_description($item);

			case "get_item_permissions":
				if (!$_SESSION["settings"]["enable_permission_update"]) {
					log_error("Cannot edit permissions, feature disabled by settings");
					$error = "FEATURE_DISABLED";
					break;
				}
				if (!is_admin()) {
					log_error("Insufficient permissions (set permission): User=[".$_SESSION['user_id']."]");
					$error = "NOT_AN_ADMIN";
					break;
				}
														
				return get_item_permissions($item);
				
			case "set_item_permission":
				if (!isset($_GET["permission"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				if (!$_SESSION["settings"]["enable_permission_update"]) {
					log_error("Cannot edit permissions, feature disabled by settings");
					$error = "FEATURE_DISABLED";
					break;
				}
				if (!is_admin()) {
					log_error("Insufficient permissions (set permission): User=[".$_SESSION['user_id']."]");
					$error = "NOT_AN_ADMIN";
					break;
				}
				$permission = $_GET["permission"];
				$user_id = NULL;
				if (isset($_GET["user_id"])) $user_id = $_GET["user_id"];
				
				return set_item_permission($item, $user_id, $permission);

			case "remove_item_permission":
				if (!$_SESSION["settings"]["enable_permission_update"]) {
					log_error("Cannot edit permissions, feature disabled by settings");
					$error = "FEATURE_DISABLED";
					break;
				}
				if (!is_admin()) {
					log_error("Insufficient permissions (remove permission): User=[".$_SESSION['user_id']."]");
					$error = "NOT_AN_ADMIN";
					break;
				}
				$user_id = NULL;
				if (isset($_GET["user_id"])) $user_id = $_GET["user_id"];
				
				return remove_item_permission($item, $user_id);
		}
		
		return FALSE;
	}
	
	function process_data_request($action) {
		switch ($action) {
			case "update_item_permissions":
				print_r($_REQUEST);
				
				if (!$_SESSION["settings"]["enable_permission_update"]) {
					log_error("Cannot edit permissions, feature disabled by settings");
					$error = "FEATURE_DISABLED";
					break;
				}
				if (!is_admin()) {
					log_error("Insufficient permissions (update permissions): User=[".$_SESSION['user_id']."]");
					$error = "NOT_AN_ADMIN";
					break;
				}
//				print_r($_POST);
//				$data = json_decode($_GET["data"]);
//				log_message($data);
				return TRUE;	
			case "get_upload_status":
				if (!isset($_GET["id"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				return get_upload_status($_GET["id"]);
		}

		return FALSE;
	}
?>