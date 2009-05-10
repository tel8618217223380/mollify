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
	 
	function process_request() {
		global $result, $error, $error_details;
		
		switch (strtolower($_GET["type"])) {
			case "file_data":
				if (!isset($_GET["action"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$action = $_GET["action"];
			
				switch (strtolower($action)) {
					case "contents":
						$dir = get_fileitem_from_url("dir");
						if (!$dir) break;
						
						$result = array("directories" => get_directories($dir),
							"files" => get_files($dir));
						break;
										
					case "files":
						$result = get_files();
						break;
					
					case "directories":
						$result = get_directories();
						break;

					case "dir_list":
						if (!isset($_GET["file"]) and !isset($_GET["dir"])) break;
						$result = get_directory_list();
						break;
							
					case "details":
						if (!isset($_GET["item_type"])) return;
						$item = get_fileitem_from_url("id");
						if (!$item) return;
						$item_type = strtolower(trim($_GET["item_type"]));
						
						if ($item_type === 'f')
							$result = get_file_details($item);
						else if ($item_type === 'd')
							$result = get_directory_details($item);
						else
							$error = "INVALID_REQUEST";
						break;
						
					case "upload_status":
						if (!isset($_GET["id"])) break;
						$result = get_upload_status($_GET["id"]);
						break;
				}
				break;
				
			case "file_action":
				if (!isset($_GET["action"])) {
					$error = "INVALID_REQUEST";
					break;
				}
				$action = $_GET["action"];
			
				$item = get_fileitem_from_url("id");
				if (!$item) return;
				
				$item_type = FALSE;
				if (isset($_GET["item_type"])) {
					$item_type = strtolower(trim($_GET["item_type"]));
					assert_item_type($item_type);
				}
				
				switch (strtolower($action)) {
					case "download":
						// download writes the header and the content, just exit here
						if (download($item)) return;
						break;
	
					case "download_as_zip":
						if (!$item_type) {
							$error = "INVALID_REQUEST";
							break;
						}
						
						// download writes the header and the content, just exit here
						if ($item_type === 'f') {
							if (download_file_as_zip($item)) return;
						} else if ($item_type === 'd') {
							if (download_dir_as_zip($item)) return;
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
						
					case "rename":
						if (!$item_type or !isset($_GET["to"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$to = urldecode($_GET["to"]);
						
						if ($item_type === 'f') {
							$result = rename_file($item, $to);
						} else if ($item_type === 'd') {
							$result = rename_directory($item, $to);
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
	
					case "copy":
						if (!isset($_GET["to"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$to = get_fileitem_from_url("to");
						if (!$to) {
							$error = "INVALID_REQUEST";
							break;
						}
						
						$result = copy_file($item, $to);
						break;
	
					case "move":
						if (!isset($_GET["to"]) or !$item_type) {
							$error = "INVALID_REQUEST";
							break;
						}
						$to = get_fileitem_from_url("to");
						if (!$to) {
							$error = "INVALID_REQUEST";
							break;
						}
						
						if ($item_type === 'f') {
							$result = move_file($item, $to);
						} else if ($item_type === 'd') {
							$result = move_directory($item, $to);
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
										
					case "delete":
						if (!$item_type) {
							$error = "INVALID_REQUEST";
							break;
						}
						
						if ($item_type === 'f') {
							$result = delete_file($item);
						} else if ($item_type === 'd') {
							$result = delete_directory($item);
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
				
					case "upload":
						if (upload_file($item)) $result = get_success_message();
						header("Content-Type: text/html");
						header("HTTP/1.1 200 OK", true);
						break;
	
					case "create_folder":
						if (!isset($_GET["name"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$result = create_folder($item, $_GET["name"]);
						break;

					case "set_description":
						if (!isset($_GET["description"]) or !$item_type) {
							$error = "INVALID_REQUEST";
							break;
						}
						if (!$_SESSION["settings"]["enable_description_update"]) {
							log_error("Cannot edit descriptions, feature disabled by settings");
							$error = "FEATURE_DISABLED";
							return FALSE;
						}
						if (!is_admin()) {
							log_error("Insufficient permissions (set description): User=[".$_SESSION['user_id']."]");
							$error = "NOT_AN_ADMIN";
							break;
						}
						$description = urldecode($_GET["description"]);
						log_error($description);
						
						if ($item_type === 'f') {
							if (!assert_file($item)) break;
						} else if ($item_type === 'd') {
							if (!assert_dir($item)) break;
						} else {
							$error = "INVALID_REQUEST";
						}
						$result = set_item_description($item, $description);
						break;

					case "remove_description":
						if (!isset($_GET["item_type"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						if (!$_SESSION["settings"]["enable_description_update"]) {
							log_error("Cannot edit descriptions, feature disabled by settings");
							$error = "FEATURE_DISABLED";
							return FALSE;
						}
						if (!is_admin()) {
							log_error("Insufficient permissions (remove description): User=[".$_SESSION['user_id']."]");
							$error = "NOT_AN_ADMIN";
							break;
						}
						
						if ($item_type === 'f') {
							$result = remove_file_description($item);
						} else if ($item_type === 'd') {
							$result = remove_directory_description($item);
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
																		
					default:
						$error = "UNSUPPORTED_OPERATION";
						$error_details = $operation;
						break;
				}
				break;

			case "configuration":
				process_configuration_request();
				break;

			default:
				$error = "UNSUPPORTED_ACTION";
				$error_details = $_GET["type"];
				break;	
		}
	}
?>