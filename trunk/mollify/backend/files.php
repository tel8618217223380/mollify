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
	
	function get_filesystem_id($root_id, $path = "") {
		if (strlen($path) > 0) {
			$root_path = get_root_path($root_id);
			$path = substr($path, strlen($root_path) + 1);
		}
		return base64_encode($root_id.'|'.$path);
	}

	function get_path_info_from_id($id) {
		$parts = explode("|", base64_decode($id));
		return array("root" => $parts[0], "path" => $parts[1]);
	}

	function get_fileitem_from_url($id_param) {
		if (!isset($_GET[$id_param])) return FALSE;
		
		$id = $_GET[$id_param];
		$file = get_path_info_from_id($id);
		$root_id = $file["root"];
		$root_path = get_root_path($root_id);
		if (!$root_path) {
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		$path = $root_path;
		if (strlen($file["path"]) > 0) $path .= DIRECTORY_SEPARATOR.$file["path"];
		
		if (strpos("..", $path) != FALSE) {
			$error = "INVALID_PATH";
			return FALSE;
		}
		return array("id" => $id, "root" => $root_id, "path" => $path, "public_path" => $dir["path"]);
	}
	
	function assert_file($filename) {
		global $error, $error_details;
		
		if (!file_exists($filename["path"])) {
			$error = "FILE_DOES_NOT_EXIST";
			$error_details = basename($filename["path"]);
			return FALSE;
		}
		if(!is_file($filename["path"])) {
			$error = "NOT_A_FILE";
			$error_details = basename($filename["path"]);
			return FALSE;
		}
		return TRUE;
	}
	
	function get_directories($account) {
		global $error, $error_details;

		$dir = get_fileitem_from_url("dir");
		if (!$dir) return FALSE;
		
		$root = $dir["root"];
		$path = $dir["path"];
		$files = scandir($path);
		$result = array();
		
		foreach($files as $i => $name) {
			if (substr($name, 0, 1) == '.') continue;

			$fullPath = $path.DIRECTORY_SEPARATOR.$name;
			if (!is_dir($fullPath)) continue;
	
			$result[] = array(
				"id" => get_filesystem_id($root, $fullPath),
				"root" => $root,
				"name" => $name
			);
		}
		
		return $result;
	}
	
	function get_files($account) {
		global $error, $error_details;
		$ignored = array('descript.ion');
		
		$dir = get_fileitem_from_url("dir");
		if (!$dir) return FALSE;
		
		$root = $dir["root"];
		$path = $dir["path"];
		$files = scandir($path);
		$result = array();
		
		foreach($files as $i => $name) {
			if (substr($name, 0, 1) == '.' || in_array(strtolower($name), $ignored)) {
				continue;
			}
			$fullPath = $path.DIRECTORY_SEPARATOR.$name;
			if (is_dir($fullPath)) continue;
			
			$ext_pos = strrpos($name, '.');
			if ($ext_pos > 0) {
				$extension = substr($name, strrpos($name, '.') + 1);
			} else {
				$extension = "";
			}
			
			$result[] = array(
				"id" => get_filesystem_id($root, $fullPath),
				"root" => $root,
				"name" => $name,
				"extension" => $extension,
				"size" => filesize($fullPath)
			);
		}
		
		return $result;
	}
	
	function get_file_details($file) {
		if (!assert_file($file)) return FALSE;

		$datetime_format = "YmdHis";
		$result = array(
			"id" => $file["id"],
			"last_changed" => date($datetime_format, filectime($file["path"])),
			"last_modified" => date($datetime_format, filemtime($file["path"])),
			"last_accessed" => date($datetime_format, fileatime($file["path"])),
			"description" => get_description($file["path"]));
		return $result;
	}
	
	function get_description($filename) {
		$path = dirname($filename);
		$file = basename($filename);
		$descriptions = get_descriptions_from_file($path.DIRECTORY_SEPARATOR."descript.ion");

		if (!isset($descriptions[$file])) return "";
		return $descriptions[$file];
	}
		
	function get_descriptions_from_file($descript_ion) {
		$result = array();
		if (!file_exists($descript_ion)) return $result;
	
		$handle = @fopen($descript_ion, "r");
		if (!$handle) return $result;
		
	    while (!feof($handle)) {
	        $line = fgets($handle, 4096);

			// check for quote marks (")
			if (ord(substr($line, 0, 1)) === 34) {
				$line = substr($line, 1);
				$split = strpos($line, chr(34));
			} else {
	        	$split = strpos($line, ' ');
			}
			if ($split <= 0) continue;

			$name = trim(substr($line, 0, $split));
			$desc = trim(substr($line, $split + 1));
			$result[$name] = $desc;
	    }
	    fclose($handle);
		
		return $result;
	}
	
	function rename_file($file, $new_name) {
		if (!assert_file($file)) return FALSE;
		
		$old = $file["path"];
		$new = dirname($old).DIRECTORY_SEPARATOR.$new_name;
		if (file_exists($new)) {
			$error = "FILE_ALREADY_EXISTS";
			$error_details = basename($new);
			return FALSE;
		}
		
		return rename($old, $new);
	}

	function delete_file($file) {
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;

		if (!unlink($file["path"])) {
			$error = "CANNOT_DELETE";
			$error_details = basename($file["path"]);
			return FALSE;
		}
		return TRUE;
	}

	function upload_file($dir) {
		global $error, $error_details;
		
		if (!isset($_FILES['upload'])) {
			$error = "NO_UPLOAD_DATA";
			return FALSE;
		}
		
		$name = $_FILES['upload']['name'];
		$origin = $_FILES['upload']['tmp_name'];
		$target = $dir["path"].DIRECTORY_SEPARATOR.$name;
		
		if ($_FILES["file"]["error"] != UPLOAD_ERR_OK) {
			$error = "UPLOAD_FAILED";
			$error_details = $_FILES["file"]["error"];
			return FALSE;
		}
		
		if (file_exists($target)) {
			$error = "FILE_ALREADY_EXISTS";
			$error_details = basename($target);
			return FALSE;
		}

		if (move_uploaded_file($origin, $target)) {
			return TRUE;
		}
		
		$error = "SAVING_FAILED";
		$error_details = $name;
		return FALSE;
	}
	
	function download($file) {
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;
		
		$filename = $file["path"];
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