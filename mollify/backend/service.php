<?php	
	if (!isset($_GET["action"])) {
		return;
	}
	
	require "user.php";
	
	$account = get_account();
	if (!$account) {
		echo json_encode(array("error"=>"Unauthorized"));
		return;
	}

	require "files.php";
	
	switch ($_GET["action"]) {
		case "get":
			if (!isset($_GET["type"])) {
				break;
			}
			$ext = isset($_GET["callback"]);
			$result = array();
			
			switch ($_GET["type"]) {
				case "roots":
					foreach($account["roots"] as $dir) {
						$result[] = array("id" => base64_encode($dir["path"]), "name" => $dir["name"]);
					}
					break;
				case "files":
					$result = get_files($account);
					break;
				case "dirs":
					$result = get_directories($account);
					break;
			}
			
			// format result
			if ($ext) echo $_GET["callback"]."(";
			echo json_encode($result);
			if ($ext) echo ');';

			break;
		case "download":
			if (!isset($_GET["id"])) {
				break;
			}
			download($_GET["id"]);
			break;
	}

?>
