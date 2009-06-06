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

	$ERRORS = array(
		"UNAUTHORIZED" => array(100, "Unauthorized request"), 
		"INVALID_REQUEST" => array(101, "Invalid request"),
		"FEATURE_DISABLED" => array(104, "Feature disabled"),
		"INVALID_CONFIGURATION" => array(105, "Invalid configuration"),
		"FEATURE_NOT_SUPPORTED" => array(106, "Feature not supported"),
	
		"INVALID_PATH" => array(201, "Invalid path"), 
		"FILE_DOES_NOT_EXIST" => array(202, "File does not exist"), 
		"DIR_DOES_NOT_EXIST" => array(203, "Directory does not exist"), 
		"FILE_ALREADY_EXISTS" => array(204, "File already exists"), 
		"DIR_ALREADY_EXISTS" => array(205, "Directory already exists"), 
		"NOT_A_FILE" => array(206, "Target is not a file"), 
		"NOT_A_DIR" => array(207, "Target is not a directory"), 
		"DELETE_FAILED" => array(208, "Could not delete"), 
		"NO_UPLOAD_DATA" => array(209, "No upload data available"), 
		"UPLOAD_FAILED" => array(210, "File upload failed"), 
		"SAVING_FAILED" => array(211, "Saving file failed"),
		"NO_MODIFY_RIGHTS" => array(212, "User has no rights to modify file"),
		"ZIP_FAILED" => array(213, "Creating a zip package failed"),
		"NO_GENERAL_WRITE_PERMISSION" => array(214, "User has no general read/write permission"),
		"NOT_AN_ADMIN" => array(215, "User is not an administrator")
	);

?>