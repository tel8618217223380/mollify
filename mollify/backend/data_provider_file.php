<?php
	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	function get_file_description($filename) {
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
	
	function get_file_permissions($filename, $user_id) {
		return get_permissions_from_file(dirname($filename).DIRECTORY_SEPARATOR."mollify.uac", $user_id, basename($filename));
	}
	
	function get_permissions_from_file($uac_file, $for_user_id, $for_file = FALSE) {
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
			
			$permissions = parse_permission_string($data);
			if (!$permissions) {
				log_error("Invalid file permission definition in file [".$uac_file."] at line ".$line_nr);
				continue;
			}
			
			$permission = get_active_permission($permissions, $for_user_id);
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
	
	function parse_permission_string($string) {
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
	
	function get_active_permission($permissions, $user_id) {
		if ($user_id != "" and isset($permissions[$user_id])) return $permissions[$user_id];
		if (isset($permissions["*"])) return $permissions["*"];
		return FALSE;
	}
?>