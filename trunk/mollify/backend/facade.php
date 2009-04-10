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
						$file = get_fileitem_from_url("id");
						if (!$file) return;
						$item_type = strtolower(trim($_GET["item_type"]));
						
						if ($item_type === 'f')
							$result = get_file_details($file);
						else if ($item_type === 'd')
							$result = get_directory_details($file);
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
			
				$file = get_fileitem_from_url("id");
				if (!$file) return;
			
				switch (strtolower($action)) {
					case "download":
						// download writes the header and the content, just exit here
						if (download($file)) return;
						break;
	
					case "download_as_zip":
						if (!isset($_GET["item_type"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$item_type = strtolower(trim($_GET["item_type"]));
						
						// download writes the header and the content, just exit here
						if ($item_type === 'f') {
							if (download_file_as_zip($file)) return;
						} else if ($item_type === 'd') {
							if (download_dir_as_zip($file)) return;
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
						
					case "rename":
						if (!isset($_GET["item_type"]) or !isset($_GET["to"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$to = urldecode($_GET["to"]);
						$item_type = strtolower(trim($_GET["item_type"]));
						
						if ($item_type === 'f') {
							$result = rename_file($file, $to);
						} else if ($item_type === 'd') {
							$result = rename_directory($file, $to);
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
						
						$result = copy_file($file, $to);
						break;
	
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
						
						$result = move_file($file, $to);
						break;
										
					case "delete":
						if (!isset($_GET["item_type"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$item_type = strtolower(trim($_GET["item_type"]));
						
						if ($item_type === 'f') {
							$result = delete_file($file);
						} else if ($item_type === 'd') {
							$result = delete_directory($file);
						} else {
							$error = "INVALID_REQUEST";
						}
						break;
				
					case "upload":
						$dir = $file;
						if (upload_file($dir)) $result = get_success_message();
						header("Content-Type: text/html");
						header("HTTP/1.1 200 OK", true);
						break;
	
					case "create_folder":
						$dir = $file;
						if (!isset($_GET["name"])) {
							$error = "INVALID_REQUEST";
							break;
						}
						$result = create_folder($dir, $_GET["name"]);
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