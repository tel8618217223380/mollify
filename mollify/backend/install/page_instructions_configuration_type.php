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
	global $CONFIGURATION_PROVIDER;
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>
	
	<body class="content" id="install-instructions-type">
		<?php pageData(); ?>
		<h1>Configuration Type</h1>

		<?php if (!isset($CONFIGURATION_PROVIDER)) { ?>
		<p>
			To continue with Mollify installation, you have to choose the configuration type suitable for your installation.
		</p>
		<p>
			Options are:
			<ul>
				<li>File based configuration</li>
				<li>Database configuration (MySQL)</li>
			</ul>
			For more information about the alternatives, see <a href="http://code.google.com/p/mollify/wiki/Installation" target="_blank">installation instructions</a>.
		</p>
		<?php } else { ?>
		<div class="error">
			Configuration type value ("<code><?php echo($CONFIGURATION_PROVIDER); ?></code>") is invalid. For more information, see <a href="http://code.google.com/p/mollify/wiki/Installation" target="_blank">installation instructions</a>.
		</div>
		<?php } ?>

		<p>
			Edit the configuration file <code>configuration.php</code> by adding the configuration provider variable, for example:
			<div class="example">
				<code>
					&lt;?php<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;$CONFIGURATION_PROVIDER = 'file';<br/>
					?&gt;<br/>
				</code>
			</div>
		</p>
		<p>
			Possible values are:
			<ul>
				<li>"<code>file</code>" for file based configuration</li>
				<li>"<code>mysql</code>" for database configuration</li>
			</ul>
		</p>
		<button id="button-continue">Continue</button>
		
		<script type="text/javascript">
			function init() {
				$("button#button-continue").click(function() {
					action("button#button-continue", "retry-configuration-type");
				});
			}
		</script>
	</body>
</html>