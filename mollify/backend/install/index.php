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
		<div id="title">Mollify Installation</div>
		<div id="content">
		<?php
			function title($current, $total, $title) {
				print "<span class='title'><span class='nr'>$current/$total</span> $title</span>";
			}
			
			function error($error) {
				print "<span class='error'>$error</span>";
			}
			
			function info($info) {
				print "<span class='info'>$info</span>";
			}
			
			$CONFIGURATION = "../configuration.php";
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
				if (file_exists($CONFIGURATION)) include($CONFIGURATION);
				require("installation_".$type.".php");
			}
			process();
		?>
		</div>
	</body>
</html>
