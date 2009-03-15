<html>
	<head>
		<title>Mollify Installation</title>
	</head>
	<body>
		<h1>Mollify Installation</h1>
		<?php
			function error($error) {
				print "<span class='error'>$error <span class='info'>For installation instructions, see <a href='http://code.google.com/p/mollify/wiki/Installation'>Installation instructions</a>.</span></span>";
			}

			function info($info) {
				print "<span class='info'>$info</span>";
			}
						
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
	
			function check_mysql() {
				if (!function_exists('mysql_connect')) {
					error("MySQL not detected in the system, check system configuration.");
					return FALSE;
				}
				return TRUE;
			}
			
			function check_db_configuration() {
				global $DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE;
				
				if (!isset($DB_USER)) {
					error("Database user not found in configuration.");
					return FALSE;
				}
				info("DB user: ".$DB_USER);
				if (!isset($DB_PASSWORD)) {
					error("Database password not found in configuration.");
					return FALSE;
				}
												
				if (!isset($DB_HOST)) info("No MySQL host found in configuration, assuming localhost.");
				else info("DB host: ".$DB_HOST);
				
				if (!isset($DB_DATABASE)) info("No database name found in configuration, using default (\"mollify\").");
				else info("DB name: ".$DB_DATABASE);
				
				return TRUE;
			}
			require_once("../configuration.php");
			require_once("installation.php");
						
			print "<h2>Checking prerequisities...</h2>";

			if (!check_configuration_provider()) return;
			if (!check_mysql()) return;
			if (!check_db_configuration()) return;
			if (!check_installation()) return;
			
			print "<h2>Installing...</h2>";
			
			if (!check_permissions()) return;
		?>
	</body>
</html>
