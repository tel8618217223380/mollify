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
	 
	function log_error($message) {
		error_log("MOLLIFY: ".$message);
	}
	
	function import_configuration_provider() {
		global $CONFIGURATION_PROVIDER;
		
		if (isset($CONFIGURATION_PROVIDER)) {
			$provider = trim(strtolower($CONFIGURATION_PROVIDER));
			
			if ($provider === 'file') {
				require_once "configuration_provider_file.php";
			} else if ($provider === 'mysql') {
				require_once "configuration_provider_mysql.php";
			} else {
				log_error("Unsupported data provider: ".$CONFIGURATION_PROVIDER);
				die("Unsupported data provider: ".$CONFIGURATION_PROVIDER);
			}
		} else {
			require_once "configuration_provider_file.php";
		}
		
		init_configuration_provider();
	}
	
	function get_configuration_setting($name) {
		$settings = get_configuration_settings();
		if (array_key_exists($name, $settings)) return $settings[$name];
		return FALSE;
	}
?>