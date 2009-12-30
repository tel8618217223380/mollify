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
	 
	 include("install/installation_page.php");
	 	 
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
	
	<body class="content" id="page-instructions-file-installed">

	<h1>Configuration Summary</h1>
	<?php if ($installer->action() != 'retry-configure') {?>
		<p>
			Mollify has been configured with following users and published directories. To modify this configuration, edit the "configuration.php". For more information about the configuration, see <a href="http://code.google.com/p/mollify/wiki/ConfigurationMultiUserMode" target="_blank">instructions</a>.
		</p>
	<?php } else { ?>
		<p>
			Mollify has been configured with following users and published directories. To view this list later, log into Mollify as admin user and open this installer.
		</p>
	<?php } ?>
		<p>
			<h2>Configured users</h2>
			<ol>
			<?php foreach ($installer->users() as $id => $user) {
				echo "<li>".$user['name']." (".getPermissionMode($user['file_permission_mode']).")</li>";
			}?>
			</ol>
			
			<h2>Published directories</h2>
			<ol>
			<?php foreach ($installer->publishedDirectories() as $userId => $dirs) {
				$users = $installer->users();
				
				echo "<li>".$users[$userId]['name']."<ul>";
				foreach ($dirs as $id => $dir) {
					echo "<li>".$dir['name']." (".$dir['path'].")</li>";
				}
				echo "</ul></li>";
			}?>
			</ol>
		</p>
	</body>
</html>