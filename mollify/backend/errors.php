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
		"UNSUPPORTED_ACTION" => array(102, "Unsupported action"),
		"UNSUPPORTED_OPERATION" => array(103, "Unsupported operation"),
		"FEATURE_DISABLED" => array(104, "Feature disabled"),
	
		"INVALID_PATH" => array(201, "Invalid path"), 
		"FILE_DOES_NOT_EXIST" => array(202, "File does not exist"), 
		"FILE_ALREADY_EXISTS" => array(203, "File already exists"), 
		"NOT_A_FILE" => array(204, "Target is not a file"), 
		"DELETE_FAILED" => array(205, "Could not delete"), 
		"NO_UPLOAD_DATA" => array(206, "No upload data available"), 
		"UPLOAD_FAILED" => array(207, "File upload failed"), 
		"SAVING_FAILED" => array(208, "Saving file failed"),
		"NO_MODIFY_RIGHTS" => array(209, "User has no rights to modify file")
	);

?>