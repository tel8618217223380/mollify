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
	
	require_once "data_provider_file.php";	// TODO create db provider, and require one of them

	switch ($_GET["action"]) {
		case "get":
			if (!isset($_GET["type"])) {
				$error = "INVALID_REQUEST";
				break;
			}
		
			switch ($_GET["type"]) {
				case "roots":
					$result = array();
					foreach($account["roots"] as $id => $root) {
						if (!isset($root["name"])) {
							$error = "INVALID_CONFIGURATION";
							break;
						}
						
						$result[] = array(
							"id" => get_filesystem_id($id),
							"name" => $root["name"]
						);
					}
					break;

				case "dirs_and_files":
					$result = array("directories" => get_directories($account),
						"files" => get_files($account));
					break;
									
				case "files":
					$result = get_files($account);
					break;
				
				case "dirs":
					$result = get_directories($account);
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
		case "operate":
			if (!isset($_GET["type"])) {
				$error = "INVALID_REQUEST";
				break;
			}
			$operation = $_GET["type"];
		
			$file = get_fileitem_from_url("id");
			if (!$file) return;
		
			switch (strtolower($operation)) {
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
						if (rename_file($file, $to))
							$result = get_success_message();
					} else if ($item_type === 'd') {
						if (rename_directory($file, $to))
							$result = get_success_message();
					} else {
						$error = "INVALID_REQUEST";
					}
					break;
				
				case "delete":
					if (!isset($_GET["item_type"])) {
						$error = "INVALID_REQUEST";
						break;
					}
					$item_type = strtolower(trim($_GET["item_type"]));
					
					if ($item_type === 'f') {
						if (delete_file($file))
							$result = get_success_message();
					} else if ($item_type === 'd') {
						if (delete_directory($file))
							$result = get_success_message();
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
					if (create_folder($dir, $_GET["name"]))
						$result = get_success_message();
					break;
					
				default:
					$error = "UNSUPPORTED_OPERATION";
					$error_details = $operation;
					break;
			}
			break;
		default:
			$error = "UNSUPPORTED_ACTION";
			$error_details = $_GET["action"];
			break;	
	}
?>