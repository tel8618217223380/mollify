<?php
	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */
	 
	function check_configuration_provider() {
		global $CONFIGURATION_PROVIDER;
		
		if (!isset($CONFIGURATION_PROVIDER)) {
			error("Configuration provider not set in <code>configuration.php</code>, <code>\"mysql\"</code> expected.");
			return FALSE;
		}
		
		if (trim(strtolower($CONFIGURATION_PROVIDER)) != 'mysql') {
			error("Incorrect configuration provider set (<code>\"$CONFIGURATION_PROVIDER\"</code>) in <code>configuration.php</code>, <code>\"mysql\"</code> expected.");
			return FALSE;
		}
		
		return TRUE;
	}
	
	function check_requirements() {
		print "<h2>Checking requirements...</h2>";
		print "<span class='item'>Checking MySQL...</span>";
		if (!check_mysql()) return;
		print "<span class='item'>Checking Mollify configuration...</span>";
		if (!check_configuration_provider()) return;
		if (!check_db_configuration()) return;
?>

<span class="confirmation">Are you sure you want to install Mollify into MySQL database with information above?
	<form method="post">
		<input type="hidden" name="type" value="mysql">
		<input type="submit" name="install" value="Install">
	</form>
</span>

<?php
	}
	
	function install() {
		print "<h2>Installing...</h2>";
		if (!check_permissions()) return;
	}

	function on_page() {
		require_once("mysql.php");
		
		if (isset($_POST["start"])) {
			check_requirements();
		} else if (isset($_POST["install"])) {
			install();
		} else {
			error("Error in installer script.");
		}
	}
?>