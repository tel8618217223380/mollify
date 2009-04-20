<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */
	
	function init_configuration_provider() {}

	function is_configuration_update_supported() {
		return TRUE;
	}

	function init_db() {
		global $DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE;
		
		if (!isset($DB_USER) or !isset($DB_PASSWORD)) {
			log_error("No database information defined");
			die();
		}
		
		if (isset($DB_HOST)) $host = $DB_HOST;
		else $host = "localhost";
		
		if (isset($DB_DATABASE)) $database = $DB_DATABASE;
		else $database = "mollify";
		
		$db = mysql_connect($host, $DB_USER, $DB_PASSWORD);
		if (!$db) {
			log_error("Could not connect to database (host=".$host.", user=".$DB_USER.", password=".$DB_PASSWORD."), error: ".mysql_error());
			die();
		}
		if (!mysql_select_db($database)) {
			log_error("Could not find database: ".$database);
			die();
		}
	}
	
	function _query($query, $init = TRUE) {
		if ($init) init_db();
		
		$result = mysql_query($query);
		if (!$result) {
			log_error("Error executing query (".$query."): ".mysql_error());
			die();
		}
		return $result;
	}
	
	function find_user($username, $password) {
		$result = _query(sprintf("SELECT id, name FROM user WHERE name='%s' AND password='%s'", mysql_real_escape_string($username), mysql_real_escape_string($password)));
		$matches = mysql_num_rows($result);
		
		if ($matches === 0) {
			log_error("No user found with name [".$username."], or password was invalid");
			return FALSE;
		} else if ($matches > 1) {
			log_error("Duplicate user found with name [".$username."] and password");
			die();
		}
		
		return mysql_fetch_assoc($result);
	}

	function get_all_users() {
		$result = _query("SELECT id, name, permission_mode FROM user ORDER BY id ASC");
		$list = array();
		while ($row = mysql_fetch_assoc($result)) {
			$list[] = $row;
		}
		mysql_free_result($result);
		return $list;
	}

	function get_user($id) {
		$result = _query(sprintf("SELECT id, name FROM user WHERE id='%s'", mysql_real_escape_string($id)));
		return mysql_fetch_assoc($result);
	}

	function add_user($name, $pw, $permission) {
		global $error, $error_details;
				
		if (!_query(sprintf("INSERT INTO user (name, password, permission_mode) VALUES ('%s', '%s', '%s')", mysql_real_escape_string($name), mysql_real_escape_string($pw), mysql_real_escape_string($permission)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to add user (".$error_details.")");
			return FALSE;
		}
		
		return TRUE;
	}

	function update_user($id, $name, $permission) {
		global $error, $error_details;
				
		if (!_query(sprintf("UPDATE user SET name='%s', permission_mode='%s' WHERE id='%s'", mysql_real_escape_string($name), mysql_real_escape_string($permission), mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to update user (".$error_details.")");
			return FALSE;
		}
		
		if (mysql_affected_rows() == 0) {
			log_error("Invalid update user request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_user($id) {
		global $error, $error_details;

		if (!_query(sprintf("DELETE FROM user_folder WHERE user_id='%s'", mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to delete user published folders for user id ".$id." (".$error_details.")");
			return FALSE;
		}
		
		if (!_query(sprintf("DELETE FROM user WHERE id='%s'", mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to remove user (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows() == 0) {
			log_error("Invalid delete user request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
		
	function _get_password($id) {
		$result = _query(sprintf("SELECT password FROM user WHERE id='%s'", mysql_real_escape_string($id)));
		return mysql_result($result, 0);
	}

	function change_password($id, $old, $new) {
		global $error, $error_details;
		
		if ($old != _get_password($id)) {
			$error = "UNAUTHORIZED";
			return FALSE;
		}
		
		if (!_query(sprintf("UPDATE user SET password='%s' WHERE id='%s'", mysql_real_escape_string($new), mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to change password (".$error_details.")");
			return FALSE;
		}
		
		if (mysql_affected_rows() == 0) {
			log_error("Invalid change password request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		return TRUE;
	}

	function reset_password($id, $pw) {
		global $error, $error_details;
				
		if (!_query(sprintf("UPDATE user SET password='%s' WHERE id='%s'", mysql_real_escape_string($pw), mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to reset password (".$error_details.")");
			return FALSE;
		}
		
		if (mysql_affected_rows() == 0) {
			log_error("Invalid reset password request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		return TRUE;
	}

	function get_all_folders() {
		$result = _query("SELECT id, name, path FROM folder ORDER BY id ASC");
		$list = array();
		while ($row = mysql_fetch_assoc($result)) {
			$list[] = $row;
		}
		mysql_free_result($result);
		return $list;
	}

	function add_folder($name, $path) {
		global $error, $error_details;
				
		if (!_query(sprintf("INSERT INTO folder (name, path) VALUES ('%s', '%s')", mysql_real_escape_string($name), mysql_real_escape_string($path)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to add folder (".$error_details.")");
			return FALSE;
		}
		
		return TRUE;
	}

	function update_folder($id, $name, $path) {
		global $error, $error_details;
				
		_query(sprintf("UPDATE folder SET name='%s', path='%s' WHERE id='%s'", mysql_real_escape_string($name), mysql_real_escape_string($path), mysql_real_escape_string($id)));
		if (mysql_affected_rows() == 0) {
			log_error("Invalid update folder request, folder ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_folder($id) {
		global $error, $error_details;

		if (!_query(sprintf("DELETE FROM user_folder WHERE folder_id='%s'", mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to delete user published folders with id ".$id." (".$error_details.")");
			return FALSE;
		}
		
		if (!_query(sprintf("DELETE FROM folder WHERE id='%s'", mysql_real_escape_string($id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to delete folders with id ".$id." (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows() == 0) {
			log_error("Invalid delete folder request, folder ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		remove_item_descriptions_recursively($id.":", TRUE);
		return TRUE;
	}
	
	function get_user_folders($user_id) {
		$result = _query(sprintf("SELECT folder.id, user_folder.name, folder.name as folder_name, folder.path FROM user_folder, folder WHERE user_id='%s' AND folder.id = user_folder.folder_id", mysql_real_escape_string($user_id)));
		$list = array();
		while ($row = mysql_fetch_assoc($result)) {
			$list[] = $row;
		}
		mysql_free_result($result);
		return $list;
	}
	
	function add_user_folder($user_id, $folder_id, $name) {
		global $error, $error_details;
		
		if ($name != NULL) {
			$query = sprintf("INSERT INTO user_folder (user_id, folder_id, name) VALUES ('%s', '%s', '%s')", mysql_real_escape_string($user_id), mysql_real_escape_string($folder_id), mysql_real_escape_string($name));
		} else {
			$query = sprintf("INSERT INTO user_folder (user_id, folder_id, name) VALUES ('%s', '%s', NULL)", mysql_real_escape_string($user_id), mysql_real_escape_string($folder_id));
		}
		
		if (!_query($query)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to add user folder (".$error_details.")");
			return FALSE;
		}
		
		return TRUE;
	}

	function update_user_folder($user_id, $folder_id, $name) {
		global $error, $error_details;
		
		if ($name != NULL) {
			$query = sprintf("UPDATE user_folder SET name='%s' WHERE user_id='%s' AND folder_id='%s'", mysql_real_escape_string($name), mysql_real_escape_string($user_id), mysql_real_escape_string($folder_id));
		} else {
			$query = sprintf("UPDATE user_folder SET name = NULL WHERE user_id='%s' AND folder_id='%s'", mysql_real_escape_string($user_id), mysql_real_escape_string($folder_id));
		}
		
		if (!_query($query)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to update user folder (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows() == 0) {
			log_error("Invalid update user folder request, folder ".$folder_id." not found for user ".$user_id);
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_user_folder($user_id, $folder_id) {
		global $error, $error_details;

		if (!_query(sprintf("DELETE FROM user_folder WHERE folder_id='%s' AND user_id='%s'", mysql_real_escape_string($folder_id), mysql_real_escape_string($user_id)))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to delete user (".$user_id.") published folder with id ".$folder_id." (".$error_details.")");
			return FALSE;
		}
						
		return TRUE;
	}
	
	function get_user_root_directories($user_id) {
		$result = _query(sprintf("SELECT folder.id, user_folder.name, folder.name as folder_name, folder.path FROM user_folder, folder WHERE user_id='%s' AND folder.id = user_folder.folder_id", mysql_real_escape_string($user_id)));

		$roots = array();
		while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
			if ($row["name"] != NULL) $name = $row["name"];
			else $name = $row["folder_name"];
			
			$roots[$row["id"]] = array("id" => $row["id"], "name" => $name, "path" => $row["path"]);
		}
		return $roots;
	}
	
	function get_default_user_permission_mode($user_id) {
		global $FILE_PERMISSION_VALUE_ADMIN, $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY, $FILE_PERMISSION_MODE;
		$mode = strtoupper(mysql_result(_query(sprintf("SELECT permission_mode FROM user WHERE id='%s'", mysql_real_escape_string($user_id))), 0));

		if ($mode != $FILE_PERMISSION_VALUE_ADMIN and $mode != $FILE_PERMISSION_VALUE_READWRITE and $mode != $FILE_PERMISSION_VALUE_READONLY) {
			log_error("Invalid file permission mode ".$mode." for user [".$user_id."]. Falling back to default.");
			return $FILE_PERMISSION_VALUE_READONLY;
		}
		return $mode;
	}

	function authentication_required() {
		return TRUE;
	}
	
	function get_file_description($file) {
		$result = _query(sprintf("SELECT description FROM item_description WHERE item_id='%s'", mysql_real_escape_string(base64_decode($file["id"]))));
		if (!$result or mysql_num_rows($result) < 1) return NULL;
		return mysql_result($result, 0);
	}

	function get_dir_description($dir) {
		$result = _query(sprintf("SELECT description FROM item_description WHERE item_id='%s'", mysql_real_escape_string(base64_decode($dir["id"]))));
		if (!$result or mysql_num_rows($result) < 1) return NULL;
		return mysql_result($result, 0);
	}

	function set_item_description($item, $description) {
		global $error, $error_details;
		
		if (!_query(sprintf("UPDATE item_description SET description='%s' WHERE item_id='%s'", mysql_real_escape_string($description), mysql_real_escape_string(base64_decode($item["id"]))))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to update description (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows() == 0) {
			if (!_query(sprintf("INSERT INTO item_description (item_id, description) VALUES ('%s','%s')", mysql_real_escape_string(base64_decode($item["id"])), mysql_real_escape_string($description)))) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error();
				log_error("Failed to insert description (".$error_details.")");
				return FALSE;
			}
		}
				
		return TRUE;
	}

	function remove_item_description($item) {
		global $error, $error_details;
		
		if (!_query(sprintf("DELETE FROM item_description WHERE item_id='%s'", mysql_real_escape_string(base64_decode($item["id"]))))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to remove description (".$error_details.")");
			return FALSE;
		}
				
		return TRUE;
	}

	function remove_item_descriptions_recursively($item_id, $unencoded = FALSE) {
		global $error, $error_details;
		
		$id = $item_id
		if (!$unencoded) $id = base64_decode($id);
		#if ($id{strlen($id)-1} != DIRECTORY_SEPARATOR) $id .= DIRECTORY_SEPARATOR;
		
		$query = sprintf("DELETE FROM item_description WHERE item_id like '%s%%'", mysql_real_escape_string($id));
		log_error($query);
		
		if (!_query($query)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to remove descriptions (".$error_details.")");
			return FALSE;
		}
				
		return TRUE;
	}
	
	function move_item_description($from, $to) {
		global $error, $error_details;
		
		if (!_query(sprintf("UPDATE item_description SET item_id='%s' WHERE item_id='%s'", mysql_real_escape_string(base64_decode($to["id"])), mysql_real_escape_string(base64_decode($from["id"]))))) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to move description (".$error_details.")");
			return FALSE;
		}
				
		return TRUE;
	}
	
	function get_file_permissions($filename, $user_id) {
		return _get_permissions_from_file(dirname($filename).DIRECTORY_SEPARATOR."mollify.uac", $user_id, basename($filename));
	}
	
	function _get_permissions_from_file($uac_file, $for_user_id, $for_file = FALSE) {
		$result = array();
		if (!file_exists($uac_file)) return $result;
	
		$handle = @fopen($uac_file, "r");
		if (!$handle) return $result;
		
		global $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY;
		$line_nr = 0;
	    while (!feof($handle)) {
	        $line = fgets($handle, 4096);
			$line_nr = $line_nr + 1;
			
			$parts = explode(chr(9), $line);
			if (count($parts) < 2) return $result;
			
			// results
			$file = trim($parts[0]);
			// if requested only for a single file, skip if not the correct one
			if ($for_file and $for_file != $file) continue;
			
			$data = trim($parts[count($parts) - 1]);
			
			$permissions = _parse_permission_string($data);
			if (!$permissions) {
				log_error("Invalid file permission definition in file [".$uac_file."] at line ".$line_nr);
				continue;
			}
			
			$permission = _get_active_permission($permissions, $for_user_id);
			// ignore lines that don't apply to current user
			if (!$permission) continue;
			
			// ignore invalid permissions
			if ($permission != $FILE_PERMISSION_VALUE_READWRITE and $permission != $FILE_PERMISSION_VALUE_READONLY) {
				log_error("Invalid file permission definition [".$permission."] in file [".$uac_file."] at line ".$line_nr);
				continue;
			}
			
			if ($for_file) {
				$result = $permission;
				break;
			}
			$result[$file] = $permission;
	    }
	    fclose($handle);
		
		return $result;
	}
	
	function _parse_permission_string($string) {
		$result = array();
		if (strlen($string) < 1) return $result;
		
		$parts = explode(',', $string);
		if (count($parts) < 1) return $result;
		
		foreach($parts as $part) {
			$value_parts = explode('=', $part);
			if (count($value_parts) != 2) return FALSE;

			$id = trim($value_parts[0]);
			$permission = strtoupper(trim($value_parts[1]));
			if (strlen($id) == 0 or strlen($permission) == 0) return FALSE;

			$result[$id] = $permission;
		}
		return $result;
	}
	
	function _get_active_permission($permissions, $user_id) {
		if ($user_id != "" and isset($permissions[$user_id])) return $permissions[$user_id];
		if (isset($permissions["*"])) return $permissions["*"];
		return FALSE;
	}
?>