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
				
		_query(sprintf("INSERT INTO user (name, password, permission_mode) VALUES ('%s', '%s', '%s')", mysql_real_escape_string($name), mysql_real_escape_string($pw), mysql_real_escape_string($permission)));
		
		return TRUE;
	}

	function update_user($id, $name, $permission) {
		global $error, $error_details;
				
		_query(sprintf("UPDATE user SET name='%s', permission_mode='%s' WHERE id='%s'", mysql_real_escape_string($name), mysql_real_escape_string($permission), mysql_real_escape_string($id)));
		if (mysql_affected_rows() == 0) {
			log_error("Invalid update user request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_user($id) {
		global $error, $error_details;

		_query(sprintf("DELETE FROM user WHERE id='%s'", mysql_real_escape_string($id)));

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
		
		_query(sprintf("UPDATE user SET password='%s' WHERE id='%s'", mysql_real_escape_string($new), mysql_real_escape_string($id)));
		
		if (mysql_affected_rows() == 0) {
			log_error("Invalid change password request, user ".$id." not found");
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
	
	function get_user_root_directories($user_id) {
		$result = _query(sprintf("SELECT folder.id, folder.name, folder.path FROM user_folder, folder WHERE user_id='%s' AND folder.id = user_folder.folder_id", mysql_real_escape_string($user_id)));

		$roots = array();
		while ($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
			$roots[$row["id"]] = array("id" => $row["id"], "name" => $row["name"], "path" => $row["path"]);
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
	
	function get_file_description($filename) {
		$path = dirname($filename);
		$file = basename($filename);
		$descriptions = _get_descriptions_from_file($path.DIRECTORY_SEPARATOR."descript.ion");

		if (!isset($descriptions[$file])) return "";
		return $descriptions[$file];
	}
	
	function _get_descriptions_from_file($descript_ion) {
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