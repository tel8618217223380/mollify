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

	function get_db() {
		global $error, $DEFAULT_HOST, $DEFAULT_DB;
		$db = get_db_configuration();
		
		if ($db["user"] === NULL) {
			$error = "DB_USER_NOT_DEFINED";
			return FALSE;
		}
		
		if ($db["password"] === NULL) {
			$error = "DB_PW_NOT_DEFINED";
			return FALSE;
		}
		
		$db["host_defined"] = TRUE;
		if ($db["host"] === NULL) {
			$db["host"] = $DEFAULT_HOST;
			$db["host_defined"] = FALSE;
		}
		
		$db["database_defined"] = TRUE;
		if ($db["database"] === NULL) {
			$db["database"] = $DEFAULT_DB;
			$db["database_defined"] = FALSE;
		}
				
		return $db;
	}
	
	function convert_version($ver) {
		return str_replace("_", ".", $ver);
	}

	function get_error_value($error, $error_detail = NULL) {
		global $ERRORS;
		$desc = NULL;
		if (array_key_exists($error, $ERRORS)) $desc = $ERRORS[$error];
		return array("id" => $error, "details" => $error_detail, "desc" => $desc);
	}
	
	function success($result) {
		echo json_encode(array("success" => TRUE, "result" => $result));
	}
	
	function failure($error) {
		echo json_encode(array("success" => FALSE, "error" => $error));
	}
?>