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

	$CONFIGURATION = array(
		"db" => array(
			"type" => "mysql",
			"database" => "mollify",
			"user" => "mollify",
			"password" => "mollify",
			"charset" => "utf8"
		),
		"timezone" => "Europe/Helsinki",	// change this to match your timezone
		
		"plugins" => array(
			"FileViewerEditor" => array(
				"viewers" => array(
					"Image" => array("gif", "png", "jpg")
				),
				"previewers" => array(
					"Image" => array("gif", "png", "jpg")
				)
			),
			"ItemDetails" => array()
		)
	);

?>