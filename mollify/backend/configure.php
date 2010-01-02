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

	if (!file_exists("configuration.php")) die();
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Mollify Configuration</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" href="configure/resources/style.css">
		<script type="text/javascript" src="resources/jquery.js"></script>
		<script type="text/javascript" src="resources/md5.js"></script>
		<script type="text/javascript" src="resources/json.js"></script>
		<!--script type="text/javascript" src="resources/configure/flexigrid.pack.js"></script-->
		<script type="text/javascript" src="configure/resources/common.js"></script>
		<script type="text/javascript">
			var session = null;
			var loadedScripts = new Array();
			var controllers = {"menu-users": {"class" : "MollifyUsersConfigurationView", "script" : "configure/users/users.js"}};
			var controller = null;
			
			$(document).ready(function() {
				initializeButtons();
				
				$(".main-menu-item").click(function() {
					$(".main-menu-item").removeClass("active");
					$(this).addClass("active");
					onSelectMenu($(this).attr("id"));
				});

				getSessionInfo(onSession, onServerError);				
			});
			
			function onSession(session) {
				if (!session["authentication_required"] || !session["authenticated"] || session["default_permission"] != 'A') {
					$("body").html("Mollify configuration utility requires admin user");
					return;
				}
				if (!session.features["configuration_update"]) {
					$("body").html("Mollify configuration is read only");
					return;
				}
				this.session = session;
			}
						
			function onSelectMenu(id) {
				if (!controllers[id]) {
					onError("Configuration view not defined: "+id);
					return;
				}
				
				var script = controllers[id]['script'];
				if (script && $.inArray(script, loadedScripts) < 0) {
					$.getScript(script, function() {
						loadedScripts.push(script);
						initView(controllers[id]['class']);
					});
				} else {
					initView(controllers[id]['class']);
				}				
			}
			
			function initView(cls) {
				controller = eval("new "+cls+"()");
				controller.load($("#page"));
			}
			
			function onServerError(error) {
				$("#page").html("<div class='error'><div class='title'>"+error+"</div></div>");
			}
			
			function onError(error) {
				$("#page").html("<div class='error'><div class='title'>"+error+"</div></div>");
			}

		</script>
	</head>	
	
	<body id="page-configure">
		<header>
			<h1>Configuration</h1>
		</header>

		<div class="content">
			<div id="main-menu">
				<ul>
					<li id="menu-users" class="main-menu-item">Users</li>
					<li id="menu-groups" class="main-menu-item">Groups</li>
					<li id="menu-published-folders" class="main-menu-item">Published Folders</li>
				</ul>
			</div>
			<div id="page">
			</div>
		</div>
	</body>
</html>