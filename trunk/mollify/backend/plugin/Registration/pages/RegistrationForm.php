<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
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
?>
<html>
	<head>
		<title>User registration</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" href="<?php echo $PATH ?>resources/jquery-ui-1.7.2.custom.css">
		<link rel="stylesheet" href="resources/style.css">

		<script type="text/javascript" src="<?php echo $PATH ?>resources/jquery-1.3.2.min.js"></script>
		<script type="text/javascript" src="<?php echo $PATH ?>resources/json.js"></script>
		<script type="text/javascript" src="<?php echo $PATH ?>resources/md5.js"></script>
		<script type="text/javascript" src="<?php echo $PATH ?>resources/template.js"></script>
		<script type="text/javascript" src="<?php echo $PATH ?>resources/jquery-ui-1.7.2.custom.min.js"></script>
		<script type="text/javascript" src="js/registration.js"></script>
		<script type="text/javascript" src="js/registration_form.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
				init('<?php echo $PATH ?>');
			});
		</script>
	</head>

	<body>
		<div id="registration-form" style="display:none">
			<div class="registration-form-field">
				<div class="registration-field-title">Name:</div>
				<input type="text" id="username-field" class="registration-field"></input>
			</div>
			<div class="registration-form-field">
				<div class="registration-field-title">Password:</div>
				<input type="password" id="password-field" class="registration-field"></input>
			</div>
			<div class="registration-form-field">
				<div class="registration-field-title">Confirm password:</div>
				<input type="password" id="confirm-password-field" class="registration-field"></input>
			</div>
			<div class="registration-form-field">
				<div class="registration-field-title">E-mail:</div>
				<input type="text" id="email-field" class="registration-field"></input>
			</div>
			
			<button id="register-button">Register</button>
		</div>
	</body>
</html>