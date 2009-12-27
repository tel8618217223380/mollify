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
	 
	 global $MAIN_PAGE;
	 if (!isset($MAIN_PAGE)) die();
	 include("../installation_page.php");
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>
	
	<body class="content" id="install-instructions-file-configuration">
		<h1>File Configuration</h1>
		
		<?php if (getAction() == 'continue') { ?>
		<div class="error">Configuration was not found in "configuration.php"</div>
		<?php }?>
		
		<p>
			Mollify installer does not support file configuration, and therefore configuration must be done by manually editing the configuration file.
		</p>
		<p>
			File configuration supports both, single user and multi user configurations. In single user configuration, no authentication is required and all access rules apply to everybody. In multi user configuration different user accounts are set up, where different users can have different published directories and different access permissions.
		</p>
		<h2>Installation</h2>
		<p>
			<ol>
				<li>Choose the preferred operation mode (described above)</li>
				<li>Modify "configuration.php" by following instructions based on the selected mode
				<ul>
					<li><a href="http://code.google.com/p/mollify/wiki/ConfigurationSingleUserMode" target="_blank">Single user</a></li>
					<li><a href="http://code.google.com/p/mollify/wiki/ConfigurationMultiUserMode" target="_blank">Multi user</a></li>
				</ul>
				</li>
				<li>Click "Continue"</li>
			</ol>
		</p>
		<button id="button-continue">Continue</button>
		
		<script type="text/javascript">
			function init() {
				createAction("continue", "#install-instructions-file-configuration", "button#button-continue");
			}
		</script>
	</body>
</html>