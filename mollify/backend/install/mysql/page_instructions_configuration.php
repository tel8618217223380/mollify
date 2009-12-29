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
	 global $installer;
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>
	
	<body class="content" id="install-instructions-mysql-configuration">
		<?php pageData(); ?>
		<h1>Database Configuration 1/3</h1>

		<?php if ($installer->hasError()) { ?>
		<div class="error">
			<p>Could not connect to database.</p>
			<p><code><?php echo $installer->error(); ?></code></p>
		</div>	
		<?php } ?>
		
		<p>
			Installer needs the database connection information set in the configuration file "<code>configuration.php</code>":
			<ul>
				<li>Host name (optional)</li>
				<li>Database name (optional)</li>
				<li>User</li>
				<li>Password</li>
				<li>Table prefix (optional)</li>
			</ul>
			
			For more information, see <a href="http://code.google.com/p/mollify/wiki/Installation">Installation instructions</a>.
		</p>
	
		<p>	
			An example configuration:
			<div class="example">
				&lt;php?<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;$CONFIGURATION_PROVIDER = &quot;<span class="value">mysql</span>&quot;;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;$DB_HOST = &quot;<span class="value">localhost</span>&quot;;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;$DB_DATABASE = &quot;<span class="value">mollify</span>&quot;;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;$DB_USER = &quot;<span class="value">[MYSQL_USERNAME]</span>&quot;;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;$DB_PASSWORD = &quot;<span class="value">[MYSQL_PASSWORD]</span>&quot;;<br/>
				&nbsp;&nbsp;&nbsp;&nbsp;$DB_TABLE_PREFIX = &quot;<span class="value">mollify_</span>&quot;;<br/>
				?&gt;
			</div>
		</p>
	
		<p>
			<button id="button-continue">Continue</button>
		</p>
		
		<script type="text/javascript">
			function init() {
				$("button#button-continue").click(function() {
					action("retry-configure");
				});
			}
		</script>
	</body>
</html>