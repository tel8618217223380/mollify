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
	
	/* For configuration instructions, see ReadMe.txt or wiki page at http://code.google.com/p/mollify/wiki/Installation */
	
	/* User types: "A" = Admin, "RW" = Read/write, "RO" = Read-only (default if omitted) */
	$USERS = array(
		"1" => array("name" => "User 1", "password" => "foo", "file_permission_mode" => "A"),
		"2" => array("name" => "User 2", "password" => "bar", "file_permission_mode" => "ro")
	);
	
	$PUBLISHED_DIRECTORIES = array(
		"1" => array(
			"r1" => array("name" => "Folder A", "path" => "/foo/bar"),
			"r2" => array("name" => "Folder B", "path" => "/foo/bay")
		),
		"2" => array(
			"r1" => array("name" => "Folder A", "path" => "/foo/bat"),
			"r2" => array("name" => "Folder C", "path" => "/foo/baz")
		)
	);
	
	/*$FILE_PERMISSION_MODE = "RO";
	
	$PUBLISHED_DIRECTORIES = array(
		"r1" => array("name" => "Folder A", "path" => "/foo/bar"),
		"r2" => array("name" => "Folder B", "path" => "/foo/bay")
	);*/
	
	$SETTINGS = array(
		"enable_file_upload" => TRUE,
		"enable_file_upload_progress" => TRUE
	);
	
?>