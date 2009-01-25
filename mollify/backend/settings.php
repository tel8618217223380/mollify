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
	 
	function get_setting($setting_name, $default) {
		global $SETTINGS;
		if (!isset($SETTINGS) or !isset($SETTINGS[$setting_name])) return $default;
		return $SETTINGS[$setting_name];
	}
	
	function get_effective_settings() {
		return array(
			"enable_file_upload" => get_setting("enable_file_upload", TRUE),
			"enable_create_folder" => get_setting("enable_create_folder", TRUE),
			"enable_file_upload_progress" => get_setting("enable_file_upload_progress", FALSE)
		);
	}
?>