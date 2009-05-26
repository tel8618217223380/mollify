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
	
	function get_configuration_settings() {
		return array(
			'configuration_update' => TRUE,
			'permission_update' => TRUE,
			'description_update_default' => TRUE,
			'permission_update_default' => TRUE
		);
	}
	
	function on_session_start($user_id, $username) {
		require_once("common.php");
		
		if (!isset($_GET["version"])) {
			log_error("Invalid authentication request, no client version provided");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		$installed = get_installed_version_from_db(init_db());
		if (!$installed) {
			log_error("Could not resolve installed version");
			global $error;
			$error = "INVALID_CONFIGURATION";
			return FALSE;
		}
		
		$current = get_current_version();
		if ($current != $installed) {
			log_error("Database version does not match the current version (database=".$installed.", current=".$current."), update is required");
			global $error, $error_details;
			$error = "INVALID_CONFIGURATION";
			$error_details = "Database version does not match the current version";
			return FALSE;
		}
		if ($_GET["version"] != $current) {
			log_error("Client version does not match the backend version (client=".$_GET["version"].", backend=".$current.")");
			global $error, $error_details;
			$error = "INVALID_CONFIGURATION";
			$error_details = "Client version does not match the backend version";
			return FALSE;
		}
		
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
		if (!mysql_select_db($database, $db)) {
			log_error("Could not find database: ".$database);
			die();
		}
		return $db;
	}
	
	function _query($query, $db) {
		$result = mysql_query($query, $db);
		if (!$result) {
			log_error("Error executing query (".$query."): ".mysql_error($db));
			die();
		}
		return $result;
	}
	
	function find_user($username, $password) {
		$db = init_db();
		
		$result = _query(sprintf("SELECT id, name FROM user WHERE name='%s' AND password='%s'", mysql_real_escape_string($username, $db), mysql_real_escape_string($password, $db)), $db);
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
		$db = init_db();
		$result = _query("SELECT id, name, permission_mode FROM user ORDER BY id ASC", $db);
		$list = array();
		while ($row = mysql_fetch_assoc($result)) {
			$list[] = $row;
		}
		mysql_free_result($result);
		return $list;
	}

	function get_user($id) {
		$db = init_db();
		$result = _query(sprintf("SELECT id, name FROM user WHERE id='%s'", mysql_real_escape_string($id, $db)), $db);
		return mysql_fetch_assoc($result);
	}

	function add_user($name, $pw, $permission) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("INSERT INTO user (name, password, permission_mode) VALUES ('%s', '%s', '%s')", mysql_real_escape_string($name, $db), mysql_real_escape_string($pw, $db), mysql_real_escape_string($permission, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to add user (".$error_details.")");
			return FALSE;
		}
		
		return TRUE;
	}

	function update_user($id, $name, $permission) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("UPDATE user SET name='%s', permission_mode='%s' WHERE id='%s'", mysql_real_escape_string($name, $db), mysql_real_escape_string($permission, $db), mysql_real_escape_string($id, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to update user (".$error_details.")");
			return FALSE;
		}
		
		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid update user request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_user($id) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("DELETE FROM user_folder WHERE user_id='%s'", mysql_real_escape_string($id, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to delete user published folders for user id ".$id." (".$error_details.")");
			return FALSE;
		}
		
		if (!_query(sprintf("DELETE FROM user WHERE id='%s'", mysql_real_escape_string($id, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to remove user (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid delete user request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
		
	function _get_password($id, $db) {
		$result = _query(sprintf("SELECT password FROM user WHERE id='%s'", mysql_real_escape_string($id, $db)), $db);
		return mysql_result($result, 0);
	}

	function change_password($id, $old, $new) {
		global $error, $error_details;

		$db = init_db();
		if ($old != _get_password($id, $db)) {
			$error = "UNAUTHORIZED";
			return FALSE;
		}
		
		if (!_query(sprintf("UPDATE user SET password='%s' WHERE id='%s'", mysql_real_escape_string($new, $db), mysql_real_escape_string($id, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to change password (".$error_details.")");
			return FALSE;
		}
		
		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid change password request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		return TRUE;
	}

	function reset_password($id, $pw) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("UPDATE user SET password='%s' WHERE id='%s'", mysql_real_escape_string($pw, $db), mysql_real_escape_string($id, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to reset password (".$error_details.")");
			return FALSE;
		}
		
		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid reset password request, user ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		return TRUE;
	}

	function get_all_folders() {
		$db = init_db();
		$result = _query("SELECT id, name, path FROM folder ORDER BY id ASC", $db);
		
		$list = array();
		while ($row = mysql_fetch_assoc($result)) {
			$list[] = $row;
		}
		mysql_free_result($result);
		return $list;
	}

	function add_folder($name, $path) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("INSERT INTO folder (name, path) VALUES ('%s', '%s')", mysql_real_escape_string($name, $db), mysql_real_escape_string($path, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to add folder (".$error_details.")");
			return FALSE;
		}
		
		return TRUE;
	}

	function update_folder($id, $name, $path) {
		global $error, $error_details;

		$db = init_db();
		_query(sprintf("UPDATE folder SET name='%s', path='%s' WHERE id='%s'", mysql_real_escape_string($name, $db), mysql_real_escape_string($path, $db), mysql_real_escape_string($id, $db)), $db);
		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid update folder request, folder ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_folder($id) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("DELETE FROM user_folder WHERE folder_id='%s'", mysql_real_escape_string($id)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to delete user published folders with id ".$id." (".$error_details.")");
			return FALSE;
		}
		
		if (!_query(sprintf("DELETE FROM folder WHERE id='%s'", mysql_real_escape_string($id)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to delete folders with id ".$id." (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid delete folder request, folder ".$id." not found");
			$error = "INVALID_REQUEST";
			return FALSE;
		}
		
		remove_item_descriptions_recursively(array("id" => $id.":"), TRUE, TRUE);
		return TRUE;
	}
	
	function get_user_folders($user_id) {
		$db = init_db();
		$result = _query(sprintf("SELECT folder.id, user_folder.name, folder.name as folder_name, folder.path FROM user_folder, folder WHERE user_id='%s' AND folder.id = user_folder.folder_id", mysql_real_escape_string($user_id, $db)), $db);
		$list = array();
		while ($row = mysql_fetch_assoc($result)) {
			$list[] = $row;
		}
		mysql_free_result($result);
		return $list;
	}
	
	function add_user_folder($user_id, $folder_id, $name) {
		global $error, $error_details;

		$db = init_db();
		if ($name != NULL) {
			$query = sprintf("INSERT INTO user_folder (user_id, folder_id, name) VALUES ('%s', '%s', '%s')", mysql_real_escape_string($user_id, $db), mysql_real_escape_string($folder_id, $db), mysql_real_escape_string($name, $db));
		} else {
			$query = sprintf("INSERT INTO user_folder (user_id, folder_id, name) VALUES ('%s', '%s', NULL)", mysql_real_escape_string($user_id, $db), mysql_real_escape_string($folder_id, $db));
		}
		
		if (!_query($query, $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error();
			log_error("Failed to add user folder (".$error_details.")");
			return FALSE;
		}
		
		return TRUE;
	}

	function update_user_folder($user_id, $folder_id, $name) {
		global $error, $error_details;

		$db = init_db();
		if ($name != NULL) {
			$query = sprintf("UPDATE user_folder SET name='%s' WHERE user_id='%s' AND folder_id='%s'", mysql_real_escape_string($name, $db), mysql_real_escape_string($user_id, $db), mysql_real_escape_string($folder_id, $db));
		} else {
			$query = sprintf("UPDATE user_folder SET name = NULL WHERE user_id='%s' AND folder_id='%s'", mysql_real_escape_string($user_id, $db), mysql_real_escape_string($folder_id, $db));
		}
		
		if (!_query($query, $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to update user folder (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows($db) == 0) {
			log_error("Invalid update user folder request, folder ".$folder_id." not found for user ".$user_id);
			$error = "INVALID_REQUEST";
			return FALSE;
		}
				
		return TRUE;
	}
	
	function remove_user_folder($user_id, $folder_id) {
		global $error, $error_details;

		$db = init_db();
		if (!_query(sprintf("DELETE FROM user_folder WHERE folder_id='%s' AND user_id='%s'", mysql_real_escape_string($folder_id, $db), mysql_real_escape_string($user_id, $db)), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to delete user (".$user_id.") published folder with id ".$folder_id." (".$error_details.")");
			return FALSE;
		}
						
		return TRUE;
	}
	
	function get_user_root_directories($user_id) {
		$db = init_db();
		$result = _query(sprintf("SELECT folder.id, user_folder.name, folder.name as folder_name, folder.path FROM user_folder, folder WHERE user_id='%s' AND folder.id = user_folder.folder_id", mysql_real_escape_string($user_id, $db)), $db);

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

		$db = init_db();
		$mode = strtoupper(mysql_result(_query(sprintf("SELECT permission_mode FROM user WHERE id='%s'", mysql_real_escape_string($user_id, $db)), $db), 0));

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
		$db = init_db();
		$result = _query(sprintf("SELECT description FROM item_description WHERE item_id='%s'", mysql_real_escape_string(base64_decode($file["id"]), $db)), $db);
		if (!$result or mysql_num_rows($result) < 1) return NULL;
		return mysql_result($result, 0);
	}

	function get_dir_description($dir) {
		$db = init_db();
		$result = _query(sprintf("SELECT description FROM item_description WHERE item_id='%s'", mysql_real_escape_string(base64_decode($dir["id"]), $db)), $db);
		if (!$result or mysql_num_rows($result) < 1) return NULL;
		return mysql_result($result, 0);
	}

	function set_item_description($item, $description) {
		global $error, $error_details;

		$db = init_db();
		$sql_id = mysql_real_escape_string(base64_decode($item["id"]), $db);
		$sql_desc = mysql_real_escape_string($description, $db);
		
		if (!_query(sprintf("UPDATE item_description SET description='%s' WHERE item_id='%s'", $sql_desc, $sql_id), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to update description (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows($db) == 0) {
			if (!_query(sprintf("INSERT INTO item_description (item_id, description) VALUES ('%s','%s')", $sql_id, $sql_desc), $db)) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error($db);
				log_error("Failed to insert description (".$error_details.")");
				return FALSE;
			}
		}
				
		return TRUE;
	}

	function remove_item_description($item, $recursively = FALSE, $unencoded = FALSE) {
		global $error, $error_details;

		$db = init_db();
		$id = $item["id"];
		if (!$unencoded) $id = base64_decode($id);
		
		if ($recursively) {
			$query = sprintf("DELETE FROM item_description WHERE item_id like '%s%%'", mysql_real_escape_string($id, $db));
			if (!_query($query, $db)) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error($db);
				log_error("Failed to remove descriptions (".$error_details.")");
				return FALSE;
			}
		} else {
			if (!_query(sprintf("DELETE FROM item_description WHERE item_id='%s'", mysql_real_escape_string($id, $db)), $db)) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error($db);
				log_error("Failed to remove description (".$error_details.")");
				return FALSE;
			}
		}
				
		return TRUE;
	}
	
	function move_item_description($from, $to, $recursively = FALSE) {
		global $error, $error_details;

		$db = init_db();
		$from_id = base64_decode($from["id"]);
		$to_id = base64_decode($to["id"]);
		
		if ($recursively) {
			$query = sprintf("UPDATE item_description SET item_id=CONCAT('%s', SUBSTR(item_id, %d)) WHERE item_id like '%s%%'", mysql_real_escape_string($to_id, $db), strlen($from_id)+1, mysql_real_escape_string($from_id, $db));
		} else {
			$query = sprintf("UPDATE item_description SET item_id='%s' WHERE item_id='%s'", mysql_real_escape_string($to_id, $db), mysql_real_escape_string($from_id, $db));
		}
		
		if (!_query($query, $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to move description (".$error_details.")");
			return FALSE;
		}
				
		return TRUE;
	}
	
	function get_item_permission($item, $user_id) {
		$db = init_db();
		$id = mysql_real_escape_string(base64_decode($item["id"]), $db);
		$user_query = sprintf("(user_id = '%s' or user_id = '0')", $user_id);
		$query = NULL;

		if (!is_dir($item["path"])) {
			$dir = get_parent_item($item);
			
			if ($dir != NULL) {
				$dir_id = mysql_real_escape_string(base64_decode($dir["id"]), $db);
				$query = sprintf("SELECT permission FROM ((SELECT permission, user_id, 1 AS 'index' FROM `item_permission` WHERE item_id = '%s' AND %s) UNION ALL (SELECT permission, user_id, 2 AS 'index' FROM `item_permission` WHERE item_id = '%s' AND %s)) AS u ORDER BY u.user_id DESC, u.index ASC", $id, $user_query, $dir_id, $user_query);
			}
		}
		if ($query === NULL) $query = sprintf("SELECT permission FROM item_permission WHERE item_id = '%s' AND %s ORDER BY user_id DESC", $id, $user_query);
		
		$result = _query($query, $db);
		
		if (!$result) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to get item permission (".$error_details.")");
			return NULL;
		}
		if (mysql_num_rows($result) < 1) return NULL;
		
		return mysql_result($result, 0);
	}
	
	function set_item_permission($item, $user_id, $permission) {
		global $error, $error_details;

		$db = init_db();
		$sql_permission = mysql_real_escape_string(strtolower($permission), $db);
		$sql_id = mysql_real_escape_string(base64_decode($item["id"]), $db);
		if ($user_id != NULL) $sql_user = sprintf("user_id = '%s'", mysql_real_escape_string($user_id, $db));
		else $sql_user = "user_id is null"; 
		
		if (!_query(sprintf("UPDATE item_permission SET permission='%s' WHERE item_id='%s' and %s", $sql_permission, $sql_id, $sql_user), $db)) {
			$error = "INVALID_REQUEST";
			$error_details = mysql_error($db);
			log_error("Failed to update permission (".$error_details.")");
			return FALSE;
		}

		if (mysql_affected_rows($db) == 0) {
			if ($user_id != NULL) $query = sprintf("INSERT INTO item_permission (item_id, user_id, permission) VALUES ('%s','%s','%s')", $sql_id, mysql_real_escape_string($user_id, $db), $sql_permission);
			else $query = sprintf("INSERT INTO item_permission (item_id, permission) VALUES ('%s','%s')", $sql_id, $sql_permission);
			
			if (!_query($query, $db)) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error($db);
				log_error("Failed to insert permission (".$error_details.")");
				return FALSE;
			}
		}
				
		return TRUE;
	}

	function remove_item_permission($item, $user_id, $recursively = FALSE, $unencoded = FALSE) {
		global $error, $error_details;

		$db = init_db();
		$id = $item["id"];
		if (!$unencoded) $id = base64_decode($id);
		if ($user_id != NULL) $sql_user = sprintf("user_id = '%s'", mysql_real_escape_string($user_id, $db));
		else $sql_user = "user_id is null"; 
		
		if ($recursively) {
			$query = sprintf("DELETE FROM item_permission WHERE item_id like '%s%%' AND %s", mysql_real_escape_string($id, $db), $sql_user);
			if (!_query($query, $db)) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error($db);
				log_error("Failed to remove permission (".$error_details.")");
				return FALSE;
			}
		} else {
			if (!_query(sprintf("DELETE FROM item_permission WHERE item_id='%s' AND %s", mysql_real_escape_string($id, $db), $sql_user), $db)) {
				$error = "INVALID_REQUEST";
				$error_details = mysql_error($db);
				log_error("Failed to remove permission (".$error_details.")");
				return FALSE;
			}
		}
				
		return TRUE;
	}
?>