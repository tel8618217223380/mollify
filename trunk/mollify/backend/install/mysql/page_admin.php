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
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>
	
	<body class="content" id="page-admin">
		<?php pageBody(); ?>
		<h1>Database Configuration 3/3</h1>
		<p>
			Mollify requires an administrator user to finish the installation:
		</p>
		<p>
			<form id="admin-user">
				<div class='user-data' id='admin-username'>
					<div class='title'>User name:</div>
					<input id='username' class='value' type='text' name='user' value=''>
				</div>
				<div class='user-data' id='admin-password'>
					<div class='title'>Password:</div>
					<input id='password' class='value' type='password' name='password' value=''>
				</div>
			</form>
		</p>
		<p class="info">
			Use this user account to log into Mollify and configure users and published folders.
		</p>
		<p>
			<button id="button-create">Create</button>
		</p>
		
		<script type="text/javascript">
			function validate() {
				$(".user-data").removeClass("invalid");
			
				var result = true;
				if ($("#username").val().length == 0) {
					$("#admin-username").addClass("invalid");
					result = false;
				}
				if ($("#password").val().length == 0) {
					$("#admin-password").addClass("invalid");
					result = false;
				}
				return result;
			}
			
			function init() {
				$("button#button-create").click(function() {
					if (!validate()) return;
					
					setData("name", $("#username").val());
					setData("password", generate_md5($("#password").val()));
					action("create");
				});
			}
		</script>
	</body>
</html>