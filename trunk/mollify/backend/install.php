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
	
	if (!file_exists("configuration.php"))
		showInstructions("configuration_create");
	
	require_once("configuration.php");
	global $SETTINGS, $CONFIGURATION_PROVIDER;
	
	if (!isset($CONFIGURATION_PROVIDER) or !isValidConfigurationType($CONFIGURATION_PROVIDER))
		showInstructions("configuration_type");

	$installer = createInstaller($CONFIGURATION_PROVIDER, $SETTINGS);
	try {
		$installer->process();
	} catch (Exception $e) {
		$installer->onError($e);
	}

	function isValidConfigurationType($type) {
		$TYPES = array("file","mysql");
		return in_array(strtolower($type), $TYPES);
	}
	
	function showInstructions($page, $type = '') {
		global $installer;
		require_once("install/DefaultInstaller.class.php");
		$installer = new DefaultInstaller();
		require("install/".($type === '' ? '' : $type."/")."page_instructions_".$page.".php");
		die();
	}
	
	function createInstaller($type, $settings) {
		switch (strtolower($type)) {
			case 'file':
				require_once("install/file/FileInstaller.class.php");
				return new FileInstaller($type, $settings);
			case 'mysql':
				require_once("install/mysql/MySQLInstaller.class.php");
				return new MySQLInstaller($type, $settings);
			default:
				die("Unsupported installer type: ".$type);
		}
	}
?>