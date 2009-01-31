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
	
	require_once "files.php";
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

				case "file_details":
					$file = get_fileitem_from_url("id");
					if (!$file) return;
					$result = get_file_details($file);
					break;

				case "directory_details":
					$dir = get_fileitem_from_url("id");
					if (!$dir) return;
					$result = get_directory_details($dir);
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
			
				case "rename":
					if (!isset($_GET["to"])) return;
					if (rename_file($file, urldecode($_GET["to"])))
						$result = get_success_message();
					break;
				
				case "delete":
					if (delete_file($file))
						$result = get_success_message();
					break;
			
				case "upload":
					$dir = $file;
					if (upload_file($dir)) $result = get_success_message();
					header("Content-Type: text/html");
					header("HTTP/1.1 200 OK", true);
					break;

				case "create_folder":
					$dir = $file;
					if (!isset($_GET["name"])) return;
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