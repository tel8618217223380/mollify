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
	 
	// NOTE! Modify this variable according to the "registration.php" location.
	$PATH = "";

	// DON'T CHANGE ANYTHING AFTER THIS
	// ********************************
	
	if (!file_exists($PATH."configuration.php")) die();
	$confirmMode = isset($_GET["confirm"]);
	$confirmEmail = $confirmMode ? $_GET["confirm"] : NULL;
	
	if ($confirmMode) {
		if ($confirmEmail == NULL or strlen($confirmEmail) == 0) {
			include($PATH."plugin/registration/resources/pages/InvalidConfirmation.php");
		} else {
			include($PATH."plugin/registration/resources/pages/Confirmation.php");
		}
		die();
	}
	include($PATH."plugin/registration/resources/pages/RegistrationForm.php");
?>