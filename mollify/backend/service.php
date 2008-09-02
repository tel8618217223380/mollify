<?php
	function format_json($result_array) {
		$ext = isset($_GET["callback"]);
		if ($ext) echo $_GET["callback"]."(";
		echo json_encode($result_array);
		if ($ext) echo ');';
	}

	if (!isset($_GET["action"])) {
		return;
	}
	
	require "user.php";
	$account = get_account();
	if (!$account) {
		format_json(array("error"=>"Unauthorized"));
		return;
	}
	
	require "files.php";
	$result = array();
	
	switch ($_GET["action"]) {
		case "get":
			if (!isset($_GET["type"])) {
				return;
			}
			
			switch ($_GET["type"]) {
				case "roots":
					foreach($account["roots"] as $root) {
						$result[] = array(
							"id" => base64_encode($root["path"]),
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
			switch (strtolower($_GET["type"])) {
				case "download":
					download($id);
					return;	// download does not return JSON, it rewrites headers etc
				case "rename":
					if (!isset($_GET["to"]))
						return;
					$result = rename($id, $_GET["to"]);
				default:
					$result = array("error"=>"Unsupported operation");
					return;
			}
			
			break;
	}
	
	// return JSON
	format_json($result);
?>
