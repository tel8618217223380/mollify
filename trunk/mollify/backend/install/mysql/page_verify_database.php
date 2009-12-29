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
	
	<body class="content" id="verify-mysql-configuration">
		<?php pageData($installer->data()); ?>
		
		<h1>Database Configuration 2/3</h1>

		<p>
			Mollify will be installed in following database:
			<ul>
				<li>Host name: <?php echo $installer->db()->host(); ?></li>
				<li>Database name: <?php echo $installer->db()->database(); ?></li>
				<li>User: <?php echo $installer->db()->user(); ?></li>
				<?php if ($installer->db()->tablePrefix() != '') { ?><li>Table prefix: <?php echo $installer->db()->tablePrefix(); ?></li><?php } ?>
			</ul>

			<?php if (!$installer->db()->databaseExists()) echo '<span class="note">Note! Database "'.$installer->db()->database().'" does not exist. If you continue installation, it will be created.</span>'; ?>
		</p>
	
		<p>
			Are you sure you want to install Mollify to this database?
		</p>
		
		<p>
			<button id="button-install">Yes, Continue</button>
			<button id="button-refresh">No, Refresh configuration</button>
		</p>
		
		<script type="text/javascript">
			function init() {
				$("button#button-refresh").click(function() {
					action("refresh-configuration");
				});
				$("button#button-install").click(function() {
					phase("db");
				});
			}
		</script>
	</body>
</html>