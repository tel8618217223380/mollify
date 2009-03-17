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
	
	function check_configuration() {
		if (!file_exists("../configuration.php")) {
			error("Configuration does not exist");
			require("instruction_configuration_mysql.php");
			return FALSE;
		}
		
		global $CONFIGURATION_PROVIDER;
		
		if (!isset($CONFIGURATION_PROVIDER)) {
			error("Configuration provider is not defined");
			require("instruction_configuration_mysql.php");
			return FALSE;
		}
		
		if (trim(strtolower($CONFIGURATION_PROVIDER)) != 'mysql') {
			error("Incorrect configuration provider defined (<code>\"$CONFIGURATION_PROVIDER\"</code>)");
			require("instruction_configuration_mysql.php");
			return FALSE;
		}
		return TRUE;
	}
	
	function get_db() {
		$DEFAULT_HOST = 'localhost';
		$DEFAULT_DB = 'mollify';
		$db = get_db_configuration();
		
		if ($db["user"] === NULL) {
			error("Database user not defined");
			require("instruction_configuration_mysql.php");
			return FALSE;
		}
		if ($db["password"] === NULL) {
			error("Database password not defined");
			require("instruction_configuration_mysql.php");
			return FALSE;
		}
		
		$db["host_defined"] = TRUE;
		if ($db["host"] === NULL) {
			$db["host"] = $DEFAULT_HOST;
			$db["host_defined"] = FALSE;
		}
		
		$db["database_defined"] = TRUE;
		if ($db["database"] === NULL) {
			$db["database"] = $DEFAULT_DB;
			$db["database_defined"] = FALSE;
		}
				
		return $db;
	}
	
	function check_installed($db) {
		$version = get_installed_version($db);
		if (!$version) return TRUE;
		
		error("Mollify already installed");
		return FALSE;
	}

	function print_db($db) {		
		print "<div class='list'><div class='title'>Database information</div>";
		
		if (!$db["host_defined"]) {
			list_item("Database host:", "<code>\"".$db["host"]."\"</code> (default)");
		} else {
			list_item("Database host:", "<code>\"".$db["host"]."\"</code>");
		}
		
		if (!$db["database_defined"]) {
			list_item("Database name:", "<code>\"".$db["database"]."\"</code> (default)");
		} else {
			list_item("Database name:", "<code>\"".$db["database"]."\"</code>");
		}
		
		list_item("Database user:", "<code>\"".$db['user']."\"</code>");
		
		print "</div>";
	}
	
	function list_item($name, $value) {
		print("<div class='list-item'><div class='name'>$name</div><div class='value'>$value</div></div>");
	}
	
	function step_database() {
		print "<h2>1/3 Database Configuration</h2>";

		if (!check_configuration()) return;
		if (!check_mysql()) return;
		if (!($db = get_db())) return;
		if (!check_installed($db)) return;
		
		print_db($db);
		
		if (!check_connection($db)) {
			error("Cannot connect to database");
			return FALSE;
		}
		if (!($db_exists = check_database($db))) {
			print("<span class='note'>Database \"".$db["database"]."\" does not exist. If you continue with installation, it will be created.</span>");
		} 

		print "<span class='confirmation'>";
		print "<form method='post'>";
		print "<span class='message'>Continue with this configuration?</span>";
		print "<input type='hidden' name='type' value='mysql'>";
		print "<input type='hidden' name='step' value='configuration'>";
		if (!$db_exists) print "<input type='hidden' name='create_db' value='1'>";
		print "<input type='submit' name='action' value='Continue'>";
		print "</form>";
		print "</span>";
	}

	function step_configuration() {
		if (!($db = get_db())) return;
		
		if (isset($_POST["create_db"]) and !create_database($db)) {
			print "<h2>1/3 Database Configuration</h2>";
			error("Could not create database. Try creating database manually and restarting installation.");
			print_db_error();
			print_db($db);
			return FALSE;
		}
				
		if (($failed = check_db_permissions($db)) != NULL) {
			print "<h2>1/3 Database Configuration</h2>";
			error("Database permission test failed, could not ".$failed.". Check database user settings and try again.");
			print_db_error();
			print_db($db);
			return FALSE;
		}
		
		print "<h2>2/3 Mollify Configuration</h2>";
		print "<span class='user'>";
		print "<form method='post'>";
		print "<span class='title'>Enter Mollify admin user information:</span>";
		print "<div class='user_data' id='user'><div class='title'>User name:</div><input class='value' type='text' name='user' value=''></div>";
		print "<div class='user_data' id='password'><div class='title'>Password:</div><input class='value' type='password' name='password' value=''></div>";
		print "</span>";
		print "<span class='confirmation'>";
		print "<span class='message'>Continue to installation?</span>";
		print "<input type='hidden' name='type' value='mysql'>";
		print "<input type='hidden' name='step' value='install'>";
		print "<input type='submit' name='action' value='Install Mollify'>";
		print "</span>";
		print "</form>";
	}
	
	function print_db_error($error) {
		print "<span class='db-error'>".$error["no"].": ".$error["error"]."</span>";
	}
	
	function step_install() {
		if (!($db = get_db())) return;

		if (!isset($_POST["user"]) or strlen($_POST["user"]) === 0 or !isset($_POST["password"]) or strlen($_POST["password"]) === 0) {
			print "<h2>2/3 Mollify Configuration</h2>";
			error("Missing admin user information.");
			return FALSE;
		}
		if ($error = create_tables($db)) {
			print "<h2>2/3 Mollify Configuration</h2>";
			error("Could not create Mollify tables.");
			print_db_error($error);
			print_db($db);
			return FALSE;
		}
		if ($error = insert_admin_user($db, $_POST["user"], $_POST["password"])) {
			print "<h2>2/3 Mollify Configuration</h2>";
			error("Could not create Mollify user.");
			print_db_error($error);
			print_db($db);
			return FALSE;
		}
		if ($error = insert_param($db, "version", "100")) {
			print "<h2>2/3 Mollify Configuration</h2>";
			error("Failed to update Mollify version data.");
			print_db_error($error);
			print_db($db);
			return FALSE;
		}
		
		print "<h2>3/3 Installation</h2>";
		info("Mollify successfully installed!");
	}

	function on_page() {
		require("mysql.php");
				
		if (!isset($_POST["step"])) {
			step_database();
		} else if ($_POST["step"] === 'configuration') {
			step_configuration();
		} else if ($_POST["step"] === 'install') {
			step_install();
		} else {
			error("Error in installer script.");
		}
	}
?>