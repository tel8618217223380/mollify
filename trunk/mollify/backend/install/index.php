<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!--
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
-->
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Mollify Installation</title>
		<link rel="stylesheet" href="style.css">
	</head>
	<body>
		<h1>Mollify Installation</h1>
		<div id="content">
		<?php
			function error($error) {
				print "<span class='error'>$error <span class='details'>See <a href='http://code.google.com/p/mollify/wiki/Installation'>Installation instructions</a> for more information.</span></span>";
			}
			
			function info($info) {
				print "<span class='info'>$info</span>";
			}
			
			$VALID_TYPES = array("mysql");
			$type = NULL;
			
			if ($_POST and isset($_POST["type"])) $type = $_POST["type"];
			
			if ($type === NULL) {
				require("installation.php");
			} else {
				if (!in_array($type, $VALID_TYPES)) {
					error("Invalid installation request");
					return;
				}
				require("../configuration.php");
				require("installation_".$type.".php");
				on_page();
			}
		?>
		</div>
	</body>
</html>
