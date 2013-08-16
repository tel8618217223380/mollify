<?php

	/**
	 * index.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	 
	$PATH = "../../";
	
	if (!file_exists($PATH."configuration.php")) die("Mollify not configured");
	$confirmMode = isset($_GET["confirm"]);
	$confirmEmail = $confirmMode ? $_GET["confirm"] : NULL;
	
	if ($confirmMode) {
		if ($confirmEmail == NULL or strlen($confirmEmail) == 0) {
			include("pages/InvalidConfirmation.php");
		} else {
			include("pages/Confirmation.php");
		}
		die();
	}
	include("pages/RegistrationForm.php");
?>