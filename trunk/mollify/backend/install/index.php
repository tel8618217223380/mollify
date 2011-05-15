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

	$MAIN_PAGE = "install";
	$installer = NULL;
	
	set_include_path(realpath('../').PATH_SEPARATOR.get_include_path());
	chdir("..");

	if (!file_exists("configuration.php")) {
		require_once("install/DefaultInstaller.class.php");
		$installer = new DefaultInstaller();
		showInstructions("configuration_create");
	}

	@include("configuration.php");
	global $SETTINGS, $CONFIGURATION_TYPE;
	if (!isset($CONFIGURATION_TYPE) or !isValidConfigurationType($CONFIGURATION_TYPE))
		showInstructions("configuration_type");

	$installer = createInstaller($CONFIGURATION_TYPE, $SETTINGS);
	try {
		$installer->process();
	} catch (Exception $e) {
		$installer->onError($e);
	}

	function isValidConfigurationType($type) {
		$TYPES = array("mysql","sqlite");
		return in_array(strtolower($type), $TYPES);
	}
	
	function showInstructions($page, $type = '') {
		require("install/".($type === '' ? '' : $type."/")."page_instructions_".$page.".php");
		die();
	}
	
	function createInstaller($type, $settings) {
		switch (strtolower($type)) {
			case 'mysql':
				require_once("install/mysql/MySQLInstaller.class.php");
				return new MySQLInstaller($type, $settings);
			case 'sqlite':
				require_once("install/sqlite/SQLiteInstaller.class.php");
				return new SQLiteInstaller($type, $settings);
			default:
				die("Invalid configuration type");
		}
	}
?>