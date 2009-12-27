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
	 include("installation_page.php");
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>

	<body class="content" id="install-instructions-create">
	<h1>Mollify Installation</h1>

	<?php if (getAction() != 'continue') { ?>
		<p>
			To begin with Mollify installation process, you have to decide the configuration mode suitable for your installation. Alternatives are file based configuration (static) or database configuration. For more information about the alternatives, see <a href="http://code.google.com/p/mollify/wiki/Installation" target="_blank">installation instructions</a>.
		</p>
		<p>
			Create file "configuration.php" in your Mollify directory as instructed, and continue.
		</p>
	<?php } else { ?>
		<p>
			Configuration file cannot be found. Make sure
			<ul>
				<li>check that the file is located at Mollify folder</li>
				<li>that it is accessible to PHP</li>
			</ul>
		</p>
	<?php }?>

		<button id="button-continue">Continue</button>
		
		<script type="text/javascript">
			function init() {
				createAction("continue", "#install-instructions-create", "button#button-continue");
			}
		</script>
	</body>
</html>