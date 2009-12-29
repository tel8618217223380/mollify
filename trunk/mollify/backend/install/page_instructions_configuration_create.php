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
	 
	include("installation_page.php");
	global $installer;
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>

	<body class="content" id="install-instructions-create">
	<?php pageData(); ?>
	
	<h1>Mollify Installation</h1>

	<?php if ($installer->action() == 'retry-configuration-file') { ?>
		<div class="error">
			Configuration file cannot be found. Make sure that the file "<code>configuration.php</code>"
			<ul>
				<li>is located in the Mollify folder (where, for example, this install script is located)</li>
				<li>is accessible to PHP</li>
			</ul>
		</div>
	<?php }?>
		<p>
			To begin with installation process, first create configuration file called "<code>configuration.php</code>" in your Mollify directory, and click "Continue".
		</p>

		<button id="button-continue">Continue</button>
		
		<script type="text/javascript">
			function init() {
				$("button#button-continue").click(function() {
					action("retry-configuration-file");
				});
			}
		</script>
	</body>
</html>