<?php
	function return_json($result_array) {
		$ext = isset($_GET["callback"]);
		if ($ext) echo $_GET["callback"]."(";
		echo json_encode($result_array);
		if ($ext) echo ');';
	}
	
	function get_success_message() {
		return array("success" => TRUE);
	}
	
	function get_error_message($error) {
		return array("success" => FALSE, "error"=>$error);
	}

	if (!isset($_GET["action"])) {
		return;
	}
	
	require "user.php";
	$account = get_account();
	if (!$account) {
		return_json(get_error_message("Unauthorized"));
		return;
	}
	
	require "files.php";
	$result = FALSE;
	$error = "";
	
	switch ($_GET["action"]) {
		case "get":
			if (!isset($_GET["type"])) {
				return;
			}
			
			switch ($_GET["type"]) {
				case "roots":
					foreach($account["roots"] as $root) {
						$result[] = array(
							"id" => get_file_id($root["path"]),
							"name" => $root["name"]
						);
					}
					break;
				case "files":
					$result = get_files($account);
					break;
				case "dirs":
					$result = get_directories($account);
					break;
			}
			
			break;
		case "operate":
			if (!isset($_GET["type"]) || !isset($_GET["id"])) {
				return;
			}
			$id = $_GET["id"];
			$filename = get_filename($id);
			
			switch (strtolower($_GET["type"])) {
				case "download":
					// download does not return JSON, it rewrites headers etc
					if (download($filename)) return;
					break;
				case "rename":
					if (!isset($_GET["to"]))
						return;
					if (rename_file($filename, urldecode($_GET["to"])))
						$result = get_success_message();
					break;
					
				case "delete":
					if (delete_file($filename))
						$result = get_success_message();
					break;

				default:
					$result = get_error_message("Unsupported operation");
					break;
			}
			
			break;
	}

	// return JSON
	if (!$result) {
		$result = get_error_message($error);
	}
	return_json($result);
?>
