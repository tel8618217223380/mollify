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
	 
	 global $MAIN_PAGE, $USERS, $PUBLISHED_DIRECTORIES;
	 if (!isset($MAIN_PAGE)) die();
	 include("../installation_page.php");
	 
	 $multiUser = (isset($USERS) and count($USERS) > 0);
	 if (!$multiUser) die();	//never show installation information in single user mode since users cannot be identified

	 function getPermissionMode($mode) {
	 	switch (strtolower(trim($mode))) {
	 		case "ro": return "Read-Only";
	 		case "rw": return "Read and Write";
	 		case "a": return "Admin";
	 		default: return "Unknown";
	 	}
	 }
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation"); ?>
	
	<body class="content" id="install-instructions-type">
		<h1>Installation Summary</h1>
		<p>
			<h2>Configured users:</h2>
			<ol>
			<?php foreach ($USERS as $id => $user) {
				echo "<li>".$user['name']." (".getPermissionMode($user['file_permission_mode']).")</li>";
			}?>
			</ol>
			
			<h2>Published directories:</h2>
			<ol>
			<?php foreach ($PUBLISHED_DIRECTORIES as $userId => $dirs) {
				echo "<li>".$USERS[$userId]['name']."<ul>";
				foreach ($dirs as $id => $dir) {
					echo "<li>".$dir['name']." (".$dir['path'].")</li>";
				}
				echo "</ul></li>";
			}?>
			</ol>

		</p>
	</body>
</html>