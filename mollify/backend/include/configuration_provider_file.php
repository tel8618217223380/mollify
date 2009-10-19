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

	function init_configuration_provider() {}
	
	function verify_version($ver) { return TRUE; }
	
	function get_configuration_settings() {
		$permission = FALSE;
		if (authentication_required()) $permission = get_setting("enable_permission_update", FALSE);
		
		return array(
			'permission_update' => $permission,
			'description_update' => get_setting("enable_description_update", FALSE)
		);
	}
	
	function on_session_start($user_id, $username) {
		return TRUE;
	}

	function find_user($username, $password) {
		global $USERS, $PASSWORDS_HASHED;
			
		foreach($USERS as $id => $user) {
			if ($user["name"] != $username)
				continue;
				
			$pw = $user["password"];
			if (!isset($PASSWORDS_HASHED) or $PASSWORDS_HASHED != TRUE) {
				$pw = md5($pw);
			}

			if ($pw != $password) {
				log_error("Invalid password for user [".$user["name"]."]");
			 	return FALSE;
			}
			
			return array("id" => $id, "name" => $user["name"]);
		}
		
		log_error("No user found with name [".$username."]");
		return FALSE;
	}
	
	function get_user($id) {
		if ($id === "") return FALSE;
		global $USERS;
		return $USERS[$id];
	}
	
	function get_default_user_permission_mode($user_id = "") {
		global $USERS, $FILE_PERMISSION_VALUE_ADMIN, $FILE_PERMISSION_VALUE_READWRITE, $FILE_PERMISSION_VALUE_READONLY, $FILE_PERMISSION_MODE;
		
		if ($user_id === "") {
			if (!isset($FILE_PERMISSION_MODE)) return $FILE_PERMISSION_VALUE_READONLY;
			$mode = strtoupper($FILE_PERMISSION_MODE);
		} else {
			if (!isset($USERS[$user_id]["file_permission_mode"])) return $FILE_PERMISSION_VALUE_READONLY;
			$mode = strtoupper($USERS[$user_id]["file_permission_mode"]);
		}

		if ($mode != $FILE_PERMISSION_VALUE_ADMIN and $mode != $FILE_PERMISSION_VALUE_READWRITE and $mode != $FILE_PERMISSION_VALUE_READONLY) {
			if ($user_id === "") log_error("Invalid file permission mode [".$mode."]. Falling back to default.");
			else log_error("Invalid file permission mode ".$mode." for user [".$user_id."]. Falling back to default.");
			return $FILE_PERMISSION_VALUE_READONLY;
		}
		return $mode;
	}

	function authentication_required() {
		global $USERS;
		return ($USERS != FALSE and count($USERS) > 0);
	}
	
	function get_user_root_directories($user_id) {
		global $USERS, $PUBLISHED_DIRECTORIES;

		if (count($USERS) === 0) {
			return $PUBLISHED_DIRECTORIES;
		} else {
			return $PUBLISHED_DIRECTORIES[$user_id];
		}
	}
	
	function get_file_description($file) {
		$path = dirname($file["path"]);
		$name = basename($file["path"]);
		$descriptions = _get_descriptions($path);
		
		if (!isset($descriptions[$name])) return NULL;
		return $descriptions[$name];
	}
	
	function get_dir_description($dir) {
		$path = dirname($dir["path"]);
		$name = basename($dir["path"]);
		$descriptions = _get_descriptions($path);
		
		if (!isset($descriptions[$name])) return NULL;
		return $descriptions[$name];
	}
	
	function set_item_description($item, $description) {
		$path = dirname($item["path"]);
		$name = basename($item["path"]);
	
		$descriptions = _get_descriptions($path);
		$descriptions[$name] = $description;
		return _write_descriptions_to_file(_get_description_filename($path), $descriptions);
	}

	function remove_item_description($item, $recursively = FALSE) {
		# we can ignore recursive flag, file "descript.ion" will be removed automatically when folder is removed		
		$path = dirname($item["path"]);
		$name = basename($item["path"]);
		
		$descriptions = _get_descriptions($path);
		if (!isset($descriptions[$name])) return TRUE;
		
		unset($descriptions[basename($name)]);
		return _write_descriptions_to_file(_get_description_filename($path), $descriptions);
	}
	
	function move_item_description($from, $to, $recursively = FALSE) {
		$from_path = dirname($from["path"]);
		$from_name = basename($from["path"]);

		$to_path = dirname($to["path"]);
		$to_name = basename($to["path"]);
		
		$from_descriptions = _get_descriptions($from_path);
		if (!isset($from_descriptions[$from_name])) return TRUE;
		
		$description = $from_descriptions[$from_name];
		unset($from_descriptions[$from_name]);
		
		if ($to_path === $from_path) $from_descriptions[$to_name] = $description;
		if (!_write_descriptions_to_file(_get_description_filename($from_path), $from_descriptions)) return FALSE;
		
		if ($to_path != $from_path) {
			$to_descriptions = _get_descriptions($to_path);
			$to_descriptions[$to_name] = $description;
			return _write_descriptions_to_file(_get_description_filename($to_path), $to_descriptions);
		}
		return TRUE;
	}
	
	function _get_description_filename($path) {
		return $path.DIRECTORY_SEPARATOR."descript.ion";
	}
	
	function _get_descriptions($path) {
		return _get_descriptions_from_file(_get_description_filename($path));
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
			$desc = str_replace('\n', "\n", trim(substr($line, $split + 1)));
			$result[$name] = $desc;
	    }
	    fclose($handle);
		
		return $result;
	}

	function _write_descriptions_to_file($descript_ion, $descriptions) {
		if (file_exists($descript_ion)) {
			if (!is_writable($descript_ion)) {
				log_error("Description file (".$descript_ion.") is not writable");
				return FALSE;
			}
		} else {
			$dir = dirname($descript_ion);
			if (!is_writable($dir)) {
				log_error("Directory for description file (".$dir.") is not writable");
				return FALSE;
			}
		}
	
		$handle = @fopen($descript_ion, "w");
		if (!$handle) return FALSE;
		
		foreach($descriptions as $name => $description)
			fwrite($handle, sprintf('"%s" %s', $name, str_replace("\n", '\n', $description))."\n");

		fclose($handle);
		
		return TRUE;
	}
		
	function get_item_permission($item, $user_id) {		
		$permissions = _get_permissions(_get_permission_filename($item));
		
		$match = FALSE;
		$id = _get_permission_id($item);
		
		if (array_key_exists($id, $permissions)) $match = $permissions[$id];
		if (!$match and $item["is_file"] and array_key_exists(".", $permissions)) $match = $permissions["."];
		if (!$match) return FALSE;
		
		return _get_effective_permission($match, $user_id);
	}

	function get_item_permissions($item) {
		$permissions = _get_permissions(_get_permission_filename($item));
		if ($permissions === FALSE) return FALSE;
		$id = _get_permission_id($item);
		
		$result = array();
		if (array_key_exists($id, $permissions)) {
			foreach ($permissions[$id] as $id => $permission) {
				if ($id === "*") $permission_id = "0";
				else $permission_id = "".$id;
				
				$result[] = array("item_id" => $item["id"], "user_id" => $permission_id, "permission" => $permission);
			}
		}
		return $result;
	}
	
	function _get_permission_filename($item) {
		$path = $item["path"];
		if (!is_dir($path))
			$path = dirname($path);

		return $path.DIRECTORY_SEPARATOR."mollify.uac";
	}

	function _get_permission_id($item) {
		if (!$item["is_file"]) return ".";		
		return basename($item["path"]);
	}

	function update_item_permissions($new, $modified, $removed) {
		// find item id (assumes that all are for the same item)
		$id = NULL;
		if (count($new) > 0) $id = $new[0]["item_id"];
		else if (count($modified) > 0) $id = $modified[0]["item_id"];
		else if (count($removed) > 0) $id = $removed[0]["item_id"];
		else return TRUE;
		
		$item = get_fileitem_from_id($id);
		if (!$item) {
			global $error;
			log_error("Could determine file item for permission (".$id.")");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		return _update_item_permissions($item, $new, $modified, $removed);
	}

	function _update_item_permissions($item, $new, $modified, $removed) {
		$uac_file = _get_permission_filename($item);
		$permissions = _get_permissions($uac_file);
		if (!$permissions) $permissions = array();

		$id = _get_permission_id($item);
		if (!array_key_exists($id, $permissions)) $permissions[$id] = array();
		$list = $permissions[$id];
		
		foreach(array_merge($new, $modified) as $permission) {
			if (!_check_item($item, $permission)) return FALSE;
			$user_id = "*";
			if ($permission["user_id"] != NULL) $user_id = $permission["user_id"];
			
			$list[$user_id] = $permission["permission"];
		}
		
		foreach($removed as $permission) {
			if (!_check_item($item, $permission)) return FALSE;			
			$user_id = "*";
			if ($permission["user_id"] != NULL) $user_id = $permission["user_id"];
			
			unset($list[$user_id]);
		}
		
		if (count($list) === 0) unset($permissions[$id]);
		else $permissions[$id] = $list;
		
		log_message($permissions);
		return _write_permissions($uac_file, $permissions);
	}
	
	function _check_item($item, $permission) {
		if ($permission["item_id"] != $item["id"]) {
			global $error, $error_details;
			$error = "INVALID_REQUEST";
			$error_details = "Permission update request for multiple items is not supported";
			log_error($error_details);
			return FALSE;
		}
		return TRUE;
	}

	function move_item_permissions($from, $to, $recursively = FALSE) {
		if ($recursively) return TRUE;	// permission file is moved along the folder
		
		$from_path = dirname($from["path"]);
		$from_name = basename($from["path"]);
		$from_id = _get_permission_id($from);

		$to_path = dirname($to["path"]);
		$to_name = basename($to["path"]);
		$to_id = _get_permission_id($to);

		$from_uac_file = _get_permission_filename($from);
		$from_permissions = _get_permissions($from_uac_file);
		if (!$from_permissions or !array_key_exists($from_id, $from_permissions)) return TRUE;
		
		$item_permissions = $from_permissions[$from_id];
		unset($from_permissions[$from_id]);
		
		if ($to_path === $from_path) $from_permissions[$to_id] = $item_permissions;
		if (!_write_permissions($from_uac_file, $from_permissions)) return FALSE;
		
		if ($to_path != $from_path) {
			$to_uac_file = _get_permission_filename($to);
			$to_permissions = _get_permissions($to_uac_file);
			if (!$to_permissions) $to_permissions = array();
			
			$to_permissions[$to_id] = $item_permissions;
			return _write_permissions($to_uac_file, $to_permissions);
		}
		return TRUE;
	}

	function _get_permissions($uac_file) {
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
			if (count($parts) < 2) continue;
			
			$file = trim($parts[0], '" ');
			$data = trim($parts[count($parts) - 1]);
			
			$permissions = _parse_permission_string($data);
			if (!$permissions) {
				log_error("Invalid file permission definition in file [".$uac_file."] at line ".$line_nr);
				continue;
			}
						
			$result[$file] = $permissions;			
	    }
	    fclose($handle);
		
		return $result;
	}

	function _write_permissions($uac_file, $permission_table) {
		if (file_exists($uac_file)) {
			if (!is_writable($uac_file)) {
				log_error("Permission file (".$uac_file.") is not writable");
				return FALSE;
			}
		} else {
			$dir = dirname($uac_file);
			if (!is_writable($dir)) {
				log_error("Directory for permission file (".$dir.") is not writable");
				return FALSE;
			}
		}
	
		$handle = @fopen($uac_file, "w");
		if (!$handle) return FALSE;
		
		foreach($permission_table as $file => $permissions) {
			$value = _format_permission_string($permissions);
			fwrite($handle, sprintf("\"%s\"\t%s\n", $file, $value));
		}

		fclose($handle);
		
		return TRUE;
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
			$permission = strtolower(trim($value_parts[1]));
			if (strlen($id) == 0 or strlen($permission) == 0) return FALSE;

			$result[$id] = $permission;
		}
		return $result;
	}

	function _format_permission_string($permissions) {
		$result = "";
		if (count($permissions) < 1) return $result;
		
		$first = TRUE;
		foreach($permissions as $id => $permission) {
			if (!$first) $result .= ',';
			$result .= sprintf("%s=%s", $id, strtolower($permission));
			$first = FALSE;
		}
		return $result;
	}
	
	function _get_effective_permission($permissions, $user_id) {
		if ($user_id != "" and isset($permissions[$user_id])) return $permissions[$user_id];
		if (isset($permissions["*"])) return $permissions["*"];
		return FALSE;
	}
	
	function get_all_users() {
		global $USERS;

		$result = array();
		foreach($USERS as $id => $user)
			$result[] = array("id" => "".$id, "name" => $user["name"], "permission_mode" => $user["file_permission_mode"]);
		return $result;
	}

?>