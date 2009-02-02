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
		return array("id" => $id, "root" => $root_id, "path" => $path, "public_path" => $file["path"]);
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

	function assert_dir($dirname) {
		global $error, $error_details;
		
		if (!file_exists($dirname["path"])) {
			$error = "DIR_DOES_NOT_EXIST";
			$error_details = basename($dirname["path"]);
			return FALSE;
		}
		if(!is_dir($dirname["path"])) {
			$error = "NOT_A_DIR";
			$error_details = basename($dirname["path"]);
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
		if (!$files) {
			$error = "INVALID_PATH";
			$error_details = $path;
			return FALSE;
		}
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
		$ignored = array('descript.ion', 'mollify.uac');
		
		$dir = get_fileitem_from_url("dir");
		if (!$dir) return FALSE;
		
		$root = $dir["root"];
		$path = $dir["path"];
		
		$files = get_visible_files_in_dir($path);
		if ($files === FALSE) return FALSE;
		
		$result = array();
		
		foreach($files as $full_path) {
			$name = basename($full_path);
			$ext_pos = strrpos($name, '.');
			if ($ext_pos > 0) {
				$extension = substr($name, $ext_pos + 1);
			} else {
				$extension = "";
			}
			
			$result[] = array(
				"id" => get_filesystem_id($root, $full_path),
				"root" => $root,
				"name" => $name,
				"extension" => $extension,
				"size" => filesize($full_path)
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
			"description" => get_file_description($file["path"]),
			"permissions" => get_file_permissions_value($file));
		return $result;
	}
	
	function get_directory_details($dir) {
		if (!assert_dir($dir)) return FALSE;

		$datetime_format = "YmdHis";
		$result = array(
			"id" => $dir["id"],
			"permissions" => get_directory_permissions_value($dir));
		return $result;
	}
	
	function get_file_permissions_value($file) {
		if (has_modify_rights($file)) return "rw";
		return "ro";
	}
	
	function get_directory_permissions_value($dir) {
		if (has_general_modify_rights()) return "rw";
		return "ro";
	}
	
	function rename_file($file, $new_name) {
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;
		if (!has_modify_rights($file)) {
			log_error("Insufficient file permissions (rename file): User=[".$_SESSION['user_id']."], file=[".$file."]");
			$error = "NO_MODIFY_RIGHTS";
			$error_details = basename($file);
			return FALSE;
		}
		
		$old = $file["path"];
		$new = dirname($old).DIRECTORY_SEPARATOR.$new_name;
		if (file_exists($new)) {
			$error = "FILE_ALREADY_EXISTS";
			$error_details = basename($new);
			return FALSE;
		}
		
		return rename($old, $new);
	}

	function rename_directory($dir, $new_name) {
		global $error, $error_details;
		
		if (!$_SESSION["settings"]["enable_folder_actions"]) {
			log_error("Cannot delete folder, feature disabled by settings");
			$error = "FEATURE_DISABLED";
			return FALSE;
		}
		if (!assert_dir($dir)) return FALSE;
		if (!has_general_modify_rights()) {
			log_error("Insufficient permissions (rename directory): User=[".$_SESSION['user_id']."], dir=[".$dir."]");
			$error = "NO_MODIFY_RIGHTS";
			return FALSE;
		}
		
		$old = $dir["path"];
		$new = dirname($old).DIRECTORY_SEPARATOR.$new_name;
		if (file_exists($new)) {
			$error = "DIR_ALREADY_EXISTS";
			$error_details = $new_name;
			return FALSE;
		}
		
		return rename($old, $new);
	}
	
	function delete_file($file) {
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;
		if (!has_modify_rights($file)) {
			log_error("Insufficient file permissions (delete): User=[".$_SESSION['user_id']."], file=[".$file."]");
			$error = "NO_MODIFY_RIGHTS";
			$error_details = basename($file);
			return FALSE;
		}
		
		if (!unlink($file["path"])) {
			$error = "CANNOT_DELETE";
			$error_details = basename($file["path"]);
			return FALSE;
		}
		return TRUE;
	}

	function delete_directory($dir) {
		global $error, $error_details;

		if (!$_SESSION["settings"]["enable_folder_actions"]) {
			log_error("Cannot delete folder, feature disabled by settings");
			$error = "FEATURE_DISABLED";
			return FALSE;
		}
		if (!assert_dir($dir)) return FALSE;
		if (!has_general_modify_rights()) {
			log_error("Insufficient permissions (delete directory): User=[".$_SESSION['user_id']."], dir=[".$dir."]");
			$error = "NO_MODIFY_RIGHTS";
			return FALSE;
		}
		
		if (!delete_directory_recurse($dir["path"])) {
			$error = "CANNOT_DELETE";
			return FALSE;
		}
		return TRUE;
	}
	
	function delete_directory_recurse($path) {
		global $error_details;
		
		$path = rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
		$handle = opendir($path);
		
		if (!$handle) {
			log_error("Could not open directory for traversal (delete_directory_recurse): ".$path);
			return FALSE;
		}
	    
	    while (false !== ($item = readdir($handle))) {
			if ($item != "." and $item != ".." ) {
				$fullpath = $path.$item;

				if (is_dir($fullpath)) {
					if (!delete_directory_recurse($fullpath)) {
						closedir($handle);
						return FALSE;
					}
				} else {
					if (!unlink($fullpath)) {
						log_error("Failed to remove file (delete_directory_recurse): ".$fullpath);
						closedir($handle);
						return FALSE;
					}
				}
			}
		}
		
		closedir($handle);
		if (!rmdir($path)) {
			log_error("Failed to remove directory (delete_directory_recurse): ".$path);
			return FALSE;
		}
	    return TRUE;
	}

	function upload_file($dir) {
		global $error, $error_details;
		
		if (!$_SESSION["settings"]["enable_file_upload"]) {
			log_error("Cannot upload file, feature disabled by settings");
			$error = "FEATURE_DISABLED";
			return FALSE;
		}
		
		if (!isset($_FILES['upload'])) {
			$error = "NO_UPLOAD_DATA";
			return FALSE;
		}
		
		$name = $_FILES['upload']['name'];
		$origin = $_FILES['upload']['tmp_name'];
		$target = $dir["path"].DIRECTORY_SEPARATOR.$name;
		
		if (isset($_FILES["file"]) && isset($_FILES["file"]["error"]) && $_FILES["file"]["error"] != UPLOAD_ERR_OK) {
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
			$_SESSION['upload_file'] = "";
			return TRUE;
		}
		
		$error = "SAVING_FAILED";
		$error_details = $name;
		return FALSE;
	}

	function create_folder($dir, $folder_name) {
		global $error, $error_details;
		
		if (!$_SESSION["settings"]["enable_folder_actions"]) {
			log_error("Cannot create folder, feature disabled by settings");
			$error = "FEATURE_DISABLED";
			return FALSE;
		}
		if (!assert_dir($dir)) return FALSE;
		if (!has_general_modify_rights()) {
			log_error("Insufficient file permissions (create folder): User=[".$_SESSION['user_id']."]");
			$error = "NO_MODIFY_RIGHTS";
			return FALSE;
		}
		
		$folder_path = $dir["path"].DIRECTORY_SEPARATOR.$folder_name;
		
		if (file_exists($folder_path)) {
			$error = "DIR_ALREADY_EXISTS";
			$error_details = dirname($folder_path);
			return FALSE;
		}
		
		if (!mkdir($folder_path, 0755)) {
			$error = "CANNOT_CREATE_FOLDER";
			$error_details = dirname($folder_path);
			return FALSE;
		}
		return TRUE;
	}
	
	function get_upload_status($id) {
		if (!$_SESSION["settings"]["enable_file_upload_progress"]) return FALSE;
		return apc_fetch('upload_'.$id);
	}
	
	function download($file) {
		global $error, $error_details;
		if (!assert_file($file)) return FALSE;
		
		$filename = $file["path"];
		header("Cache-Control: public, must-revalidate");
		header("Content-Type: application/force-download");
		header("Content-Type: application/octet-stream");
		header("Content-Type: application/download");
		header("Content-Disposition: attachment; filename=".basename($filename).";");
		header("Content-Transfer-Encoding: binary");
		header("Pragma: hack");
		header("Content-Length: ".filesize($filename));
		
		readfile($filename);
		return TRUE;
	}
	
	function download_file_as_zip($file) {
		require "zipstream.php";
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;
		$path = $file["path"];
		
		$zip = new ZipStream(basename($path).'.zip');
		$file_data = file_get_contents($path);
		if (!$file_data) {
			log_error("Could not add file to zip: ".$path);
			$error = "ZIP_FAILED";
			$error_details = basename($path);
			return FALSE;
		}
		$zip->add_file(basename($path), $file_data);
		$zip->finish();
		
		return TRUE;
	}
	
	function download_dir_as_zip($dir) {
		require "zipstream.php";
		global $error, $error_details;
		
		if (!assert_dir($dir)) return FALSE;
		$files = get_visible_files_in_dir($dir["path"]);
		if ($files === FALSE) return FALSE;
		
		$parent = dirname($dir["path"]);
		$zip_name = substr($dir["path"], strlen($parent)).'.zip';
		$zip = new ZipStream($zip_name);
		
		foreach($files as $file) {
			log_error("Zipping ".$file);
			$file_data = file_get_contents($file);
			if (!$file_data) {
				log_error("Could not add file to zip: ".$file);
				$error = "ZIP_FAILED";
				$error_details = basename($file);
				return FALSE;
			}
			$zip->add_file(basename($file), $file_data);
		}
		$zip->finish();
		
		return TRUE;
	}
	
	function get_visible_files_in_dir($path) {
		global $error, $error_details;
		$ignored = array('descript.ion', 'mollify.uac');
		
		$files = scandir($path);
		if (!$files) {
			$error = "INVALID_PATH";
			$error_details = $path;
			return FALSE;
		}
		$result = array();
		
		foreach($files as $i => $name) {
			if (substr($name, 0, 1) == '.' || in_array(strtolower($name), $ignored)) {
				continue;
			}
			$full_path = $path.DIRECTORY_SEPARATOR.$name;
			if (is_dir($full_path)) continue;
			
			$result[] = $full_path;
		}
		return $result;
	}
	
	function has_general_modify_rights() {
		global $FILE_PERMISSION_VALUE_ADMIN, $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY;
		$base = $_SESSION['default_file_permission'];
		return ($base === $FILE_PERMISSION_VALUE_ADMIN || $base === $FILE_PERMISSION_VALUE_READWRITE);
	}
	
	function has_modify_rights($item) {
		global $FILE_PERMISSION_VALUE_ADMIN, $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY;
		$base = $_SESSION['default_file_permission'];
		if ($base === $FILE_PERMISSION_VALUE_ADMIN) return TRUE;
		
		$path = $item["path"];
		if (!is_file($path)) return ($base === $FILE_PERMISSION_VALUE_READWRITE);
		
		$specific = get_file_permissions($path, $_SESSION['user_id']);
		return (get_applicable_permission($base, $specific) === $FILE_PERMISSION_VALUE_READWRITE);
	}
	
	function get_applicable_permission($base, $specific) {
		if (!$specific) return $base;
		return $specific;
	}
?>