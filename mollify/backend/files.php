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
	
	function get_filesystem_session_info() {
		return array(
			"max_upload_file_size" => in_bytes(ini_get("upload_max_filesize")),
			"max_upload_total_size" => in_bytes(ini_get("post_max_size"))
		);
	}
	
	function in_bytes($amount) {
	    $amount = trim($amount);
	    $last = strtolower($amount[strlen($amount)-1]);
	    
	    switch ($last) {
	        case 'g':
	            $amount *= 1024;
	        case 'm':
	            $amount *= 1024;
	        case 'k':
	            $amount *= 1024;
	    }
	
	    return (int)$amount;
	}
	
	function join_path($item1, $item2) {
		return dir_path($item1).$item2;
	}
	
	function dir_path($path) {
		return rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
	}
	
	function get_fileitem($root_id, $path) {
		return array("id" => get_filesystem_id($root_id, $path), "root" => $root_id, "path" => $path);
	}
	
	function get_filesystem_id($root_id, $path = "") {
		if (strlen($path) > 0) {
			$root_path = get_root_path($root_id);
			$path = substr($path, strlen($root_path) + 1);
		}
		return base64_encode($root_id.':'.$path);
	}

	function get_path_info_from_id($id) {
		$parts = explode(":", base64_decode($id));
		return array("root" => $parts[0], "path" => $parts[1]);
	}

	function get_root_path($id) {
		$roots = $_SESSION["roots"];
		if (!array_key_exists($id, $roots)) return FALSE;
		return $roots[$id]["path"];
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
		if (strlen($file["path"]) > 0) $path = join_path($path, $file["path"]);
		
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
	
	function assert_item_type($item_type) {
		return ($item_type === 'd' or $item_type === 'f');
	}
		
	function get_directories($dir = NULL) {
		global $error, $error_details;

		if ($dir === NULL) {
			$dir = get_fileitem_from_url("dir");
			if (!$dir) return FALSE;
		}
		
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

			$full_path = dir_path(join_path($path, $name));
			if (!is_dir($full_path)) continue;
	
			$result[] = array(
				"id" => get_filesystem_id($root, $full_path),
				"root" => $root,
				"name" => $name
			);
		}
		
		return $result;
	}
	
	function get_directory_list() {
		global $error, $error_details;
		$result = array();
		
		if (isset($_GET["dir"])) {
			$item = get_fileitem_from_url("dir");
			if (!assert_dir($item)) return FALSE;
		} else if (isset($_GET["file"])) {
			$item = get_fileitem_from_url("file");
			if (!assert_file($item)) return FALSE;
		}
		
		$path = $item["path"];
		$root_id = $item["root"];
		$root_path = get_root_path($root_id);
		
		while (true) {
			$path = dirname($path);
			$result[] = array(
				"id" => get_filesystem_id($root_id, dir_path($path)),
				"root" => $root,
				"name" => $name
			);
			if ($path === '' or $path === $root_path) break;
		}
			
		return $result;
	}
	
	function get_files($dir = NULL) {
		global $error, $error_details;
		$ignored = array('descript.ion', 'mollify.uac');
		
		if ($dir === NULL) {
			$dir = get_fileitem_from_url("dir");
			if (!$dir) return FALSE;
		}
		
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
			"description" => get_file_description($file),
			"permissions" => get_file_permissions_value($file));
		return $result;
	}
	
	function get_directory_details($dir) {
		if (!assert_dir($dir)) return FALSE;

		$datetime_format = "YmdHis";
		$result = array(
			"id" => $dir["id"],
			"description" => get_dir_description($dir),
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

	function set_file_description($file, $description) {		
		if (!assert_file($file)) return FALSE;
		return set_item_description($file, $description);
	}

	function set_directory_description($dir, $description) {		
		if (!assert_dir($dir)) return FALSE;
		return set_item_description($dir, $description);
	}

	function remove_file_description($file) {
		if (!assert_file($file)) return FALSE;
		return remove_item_description($file);
	}

	function remove_directory_description($dir) {
		if (!assert_dir($dir)) return FALSE;
		return remove_item_description($dir);
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
		$new = join_path(dirname($old),$new_name);
		
		if (file_exists($new)) {
			$error = "FILE_ALREADY_EXISTS";
			$error_details = basename($new);
			return FALSE;
		}
		
		if (!rename($old, $new)) return FALSE;
		move_item_description($file, get_fileitem($file["root"], $new));
		return TRUE;
	}

	function copy_file($file, $to) {
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;
		if (!assert_dir($to)) return FALSE;
		
		if (!has_general_modify_rights()) {
			log_error("Insufficient permissions (copy file): User=[".$_SESSION['user_id']."]. General read/write permission is required.");
			$error = "NO_GENERAL_WRITE_PERMISSION";
			return FALSE;
		}
		
		$origin = $file["path"];
		$target = join_path($to["path"], basename($origin));
		
		if (file_exists($target)) {
			$error = "FILE_ALREADY_EXISTS";
			$error_details = basename($target);
			return FALSE;
		}
		
		return copy($origin, $target);
	}

	function move_file($file, $to) {
		global $error, $error_details;
		
		if (!assert_file($file)) return FALSE;
		if (!assert_dir($to)) return FALSE;
		
		if (!has_general_modify_rights()) {
			log_error("Insufficient permissions (move file): User=[".$_SESSION['user_id']."]. General read/write permission is required.");
			$error = "NO_GENERAL_WRITE_PERMISSION";
			return FALSE;
		}
		
		$origin = $file["path"];
		$target = join_path($to["path"], basename($origin));
		
		if (file_exists($target)) {
			$error = "FILE_ALREADY_EXISTS";
			$error_details = basename($target);
			return FALSE;
		}
		
		if (!rename($origin, $target)) return FALSE;
		
		if ($_SESSION["settings"]["enable_description_update"])
			move_item_description($file, get_fileitem($to["root"], $target));

		return TRUE;
	}

	function move_directory($dir, $to) {
		global $error, $error_details;
		
		if (!assert_dir($dir)) return FALSE;
		if (!assert_dir($to)) return FALSE;
		
		if (!has_general_modify_rights()) {
			log_error("Insufficient permissions (move dir): User=[".$_SESSION['user_id']."]. General read/write permission is required.");
			$error = "NO_GENERAL_WRITE_PERMISSION";
			return FALSE;
		}
		
		$origin = $dir["path"];
		$target = dir_path(join_path($to["path"], basename($origin)));
		
		if (file_exists($target)) {
			$error = "DIR_ALREADY_EXISTS";
			$error_details = basename($target);
			return FALSE;
		}
		
		if (!rename($origin, $target)) return FALSE;
		
		if ($_SESSION["settings"]["enable_description_update"])
			move_item_description($dir, get_fileitem($to["root"], $target), TRUE);

		return TRUE;
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
		$new = dir_path(join_path(dirname($old), $new_name));
		
		if (file_exists($new)) {
			$error = "DIR_ALREADY_EXISTS";
			$error_details = $new_name;
			return FALSE;
		}
		
		if (!rename($old, $new)) return FALSE;
		
		if ($_SESSION["settings"]["enable_description_update"])
			move_item_description($dir, get_fileitem($dir["root"], $new), TRUE);

		return TRUE;
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
		
		if ($_SESSION["settings"]["enable_description_update"])
			remove_item_description($file);

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
		
		if ($_SESSION["settings"]["enable_description_update"])
			remove_item_description($dir, TRUE);

		return TRUE;
	}
	
	function delete_directory_recurse($path) {
		global $error_details;
		
		$path = dir_path($path);
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

		if (isset($_FILES["file"]) && isset($_FILES["file"]["error"]) && $_FILES["file"]["error"] != UPLOAD_ERR_OK) {
			$error = "UPLOAD_FAILED";
			$error_details = $_FILES["file"]["error"];
			return FALSE;
		}
				
		foreach ($_FILES[upload][name] as $key => $value) { 
			$name = $_FILES['upload']['name'][$key];
			$origin = $_FILES['upload']['tmp_name'][$key];
			$target = join_path($dir["path"], $name);
				
			if (file_exists($target)) {
				$error = "FILE_ALREADY_EXISTS";
				$error_details = basename($target);
				return FALSE;
			}
			
			if (!move_uploaded_file($origin, $target)) {
				$error = "SAVING_FAILED";
				$error_details = $name;
				return FALSE;
			}
		}

		$_SESSION['upload_file'] = "";
		return TRUE;
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
		
		$folder_path = dir_path(join_path($dir["path"], $folder_name));
		
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
		header("Content-Disposition: attachment; filename=\"".basename($filename)."\";");
		header("Content-Transfer-Encoding: binary");
		header("Pragma: hack");
		header("Content-Length: ".filesize($filename));
		
		readfile($filename);
		return TRUE;
	}
	
	function download_file_as_zip($file) {
		require "zipstream.php";
		global $error, $error_details, $ZIP_OPTIONS;

		if (!$_SESSION["settings"]["enable_zip_download"]) {
			log_error("Cannot download files zipped, feature disabled by settings");
			$error = "FEATURE_DISABLED";
			return FALSE;
		}
		
		if (!assert_file($file)) return FALSE;
		$path = $file["path"];
		$zip_name = basename($path).'.zip';
		
		if (isset($ZIP_OPTIONS)) $zip = new ZipStream($zip_name, $ZIP_OPTIONS);
		else $zip = new ZipStream($zip_name);

		$zip->add_file_from_path(basename($path), $path);
		$zip->finish();
		
		return TRUE;
	}
	
	function download_dir_as_zip($dir) {
		require "zipstream.php";
		global $error, $error_details, $ZIP_OPTIONS;
		
		if (!$_SESSION["settings"]["enable_zip_download"]) {
			log_error("Cannot download files zipped, feature disabled by settings");
			$error = "FEATURE_DISABLED";
			return FALSE;
		}
		
		if (!assert_dir($dir)) return FALSE;
		$offset = strlen($dir["path"]) + 1;
		$files = get_visible_files_in_dir($dir["path"], TRUE);
		if ($files === FALSE) return FALSE;
		
		$parent = dirname($dir["path"]);
		$zip_name = substr($dir["path"], strlen($parent) + 1).'.zip';
		
		if (isset($ZIP_OPTIONS)) $zip = new ZipStream($zip_name, $ZIP_OPTIONS);
		else $zip = new ZipStream($zip_name);
		
		foreach($files as $file) {
			$zip->add_file_from_path(substr($file, $offset), $file);
		}
		$zip->finish();
		
		return TRUE;
	}
	
	function get_visible_files_in_dir($path, $recursive = FALSE) {
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
			$full_path = join_path($path, $name);
			
			if (is_dir($full_path)) {
				if (!$recursive) continue;
				
				$sub = get_visible_files_in_dir($full_path);
				if ($sub != FALSE) $result = array_merge($result, $sub);
				continue;
			}
			
			$result[] = $full_path;
		}
		return $result;
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