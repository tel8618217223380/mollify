<?php
	function get_file_id($file) {
		return base64_encode($file);
	}

	function get_filename($id) {
		return base64_decode($id);
	}

	function get_dir_from_url() {
		if (!isset($_GET["dir"])) return FALSE;
		return get_filename($_GET["dir"]);
	}
	
	function get_directories($account) {
		global $error;
		$path = get_dir_from_url();
		if (!$path) {
			$error = "Invalid path";
			return FALSE;
		}
		
		$files = scandir($path);
		$result = array();
		
		foreach($files as $i => $name) {
			if (substr($name, 0, 1) == '.') {
				continue;
			}
			$fullPath = $path.DIRECTORY_SEPARATOR.$name;
			if (!is_dir($fullPath)) {
				continue;
			}
			$result[] = array("id" => get_file_id($fullPath), "name" => $name, "path" => $fullPath);
		}
		
		return $result;
	}
	
	function get_files($account) {
		global $error;
		$path = get_dir_from_url();
		if (!$path) {
			$error = "Invalid path";
			return FALSE;
		}

		$files = scandir($path);
		$result = array();
		
		foreach($files as $i => $name) {
			if (substr($name, 0, 1) == '.') {
				continue;
			}
			$fullPath = $path.DIRECTORY_SEPARATOR.$name;
			if (is_dir($fullPath)) {
				continue;
			}
			
			$ext_pos = strrpos($name, '.');
			if ($ext_pos > 0) {
				$extension = substr($name, strrpos($name, '.') + 1);
			} else {
				$extension = "";
			}
			
			$result[] = array("id" => get_file_id($fullPath), "name" => $name, "extension" => $extension, "size" => filesize($fullPath));
		}
		
		return $result;
	}
	
	function rename_file($filename, $new_name) {
		global $error;
		
		if (!file_exists($filename)) {
			$error = "Source file does not exist: ".basename($filename);
			return FALSE;
		}
		if(!is_file($filename)) {
			$error = "Source is not a file: ".basename($filename);
			return FALSE;
		}
		
		$new = dirname($filename).DIRECTORY_SEPARATOR.$new_name;
		if (file_exists($new)) {
			$error = "Target file already exists: ".basename($new);
			return FALSE;
		}
		
		return rename($filename, $new);
	}

	function delete_file($filename) {
		global $error;
		if (!file_exists($filename)) {
			$error = "Target file does not exist: ".basename($filename);
			return FALSE;
		}
		if(!is_file($filename)) {
			$error = "Target is not a file: ".basename($filename);
			return FALSE;
		}
		if (!unlink($filename)) {
			$error = "Failed to delete file: ".basename($filename);
			return FALSE;
		}
		return TRUE;
	}
	
	function download($filename) {
		global $error;
		if (!file_exists($filename)) {
			$error = "Source file does not exist: ".basename($filename);
			return FALSE;
		}
		if(!is_file($filename)) {
			$error = "Source is not a file: ".basename($filename);
			return FALSE;
		}
		
		header("Content-Type: application/force-download");
		header("Content-Type: application/octet-stream");
		header("Content-Type: application/download");
		header("Content-Disposition: attachment; filename=".basename($filename).";");
		header("Content-Transfer-Encoding: binary");
		header("Content-Length: ".filesize($filename));
		
		readfile($filename);
		return TRUE;
	}
?>