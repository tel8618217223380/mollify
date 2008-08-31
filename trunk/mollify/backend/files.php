<?php
	function get_dir_from_url() {
		if (!isset($_GET["dir"])) return FALSE;
		$dir = base64_decode($_GET["dir"]);
		return $dir;
	}
	
	function get_directories($account) {
		$path = get_dir_from_url();
		if (!$path) return array();
		
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
			$result[] = array("id" => base64_encode($fullPath), "name" => $name, "path" => $fullPath);
		}
		
		return $result;
	}
	
	function get_files($account) {
		$path = get_dir_from_url();
		if (!$path) return array();

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
			
			$result[] = array("id" => base64_encode($fullPath), "name" => $name, "extension" => $extension, "size" => filesize($fullPath));
		}
		
		return $result;
	}
	
	function download($id) {
		$filename = base64_decode($id);
		
		header("Content-Type: application/force-download");
		header("Content-Type: application/octet-stream");
		header("Content-Type: application/download");
		header("Content-Disposition: attachment; filename=".basename($filename).";");
		header("Content-Transfer-Encoding: binary");
		header("Content-Length: ".filesize($filename));
		
		readfile($filename); 
	}
?>