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
	global $CONFIGURATION;
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>
	
	<body id="install-instructions-type">
		<?php pageBody("Installation", "Welcome to Mollify Installer"); ?>
		<?php if (isset($CONFIGURATION["db"]) && isset($CONFIGURATION["db"]["type"])) { ?>
		<div class="error">
			<div class="title">	
				Database configuration is not valid.
			</div>
			<div class="details">
				Database type "<code><?php echo($CONFIGURATION["db"]["type"]); ?></code>" is invalid. For more information, see <a href="http://code.google.com/p/mollify/wiki/Installation" target="_blank">installation instructions</a>.
			</div>
		</div>
		<?php } ?>
		
		<div class="content">
			<?php if (!isset($CONFIGURATION) || !isset($CONFIGURATION["db"]) || !isset($CONFIGURATION["db"]["type"])) { ?>
			<p>
				To continue with Mollify installation, you have to setup the configuration.
			</p>
			<?php } ?>
	
			<p>
				Edit the configuration file <code>configuration.php</code> by adding the database type, for example:
				<div class="example code">
					&lt;?php<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;$CONFIGURATION = array(<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;db&quot; => array(<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&quot;type&quot; => &quot;<span class="value">[DATABASE TYPE HERE]</span>&quot;<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;)<br/>
					&nbsp;&nbsp;&nbsp;&nbsp;);<br/>
					?&gt;
				</div>
			</p>
			<p>
				Possible values are:
				<ul>
					<li>"<code>mysql</code>" for MySQL</li>
					<li>"<code>sqlite</code>" for SQLite</li>
					<li>"<code>pdo</code>" for PDO (supports MySQL and SQLite)</li>
				</ul>
				
				When this is added, click "Continue". For more information about the installation, see <a href="http://code.google.com/p/mollify/wiki/Installation" target="_blank">installation instructions</a>.

			</p>

			<p>
				<a id="button-continue" href="#" class="btn">Continue</a>
			</p>
		</div>
				
		<?php pageFooter(); ?>
	</body>
	
	<script type="text/javascript">
		function init() {
			$("#button-continue").click(function() {
				action("retry");
			});
		}
	</script>
</html>