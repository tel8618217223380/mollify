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
	 
	function get_setting($setting_name, $default) {
		global $SETTINGS;
		if (!isset($SETTINGS) or !isset($SETTINGS[$setting_name])) return $default;
		return $SETTINGS[$setting_name];
	}
	
	function get_effective_settings() {
		return array(
			"enable_file_upload" => get_setting("enable_file_upload", TRUE),
			"enable_folder_actions" => get_setting("enable_folder_actions", TRUE),
			"enable_file_upload_progress" => get_setting("enable_file_upload_progress", FALSE),
			"enable_zip_download" => get_setting("enable_zip_download", FALSE)
		);
	}
	
	function process_configuration_request() {
		global $result, $error, $error_details;
		
		if (!isset($_GET["action"]) or !is_configuration_update_supported()) {
			$error = "INVALID_REQUEST";
			return;
		}
		if (!is_admin()) {
			$error = "UNAUTHORIZED";
			return;
		}
		
		$action = $_GET["action"];
		switch ($action) {
			case "get_users":
				break;
			case "add_user":
				break;
			case "remove_user":
				break;
			case "update_user":
				break;
			default:
				$error = "UNSUPPORTED_OPERATION";
				$error_details = $operation;
				break;
		}
	}
?>