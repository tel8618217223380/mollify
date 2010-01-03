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
		
		<link rel="stylesheet" href="resources/jquery-ui-1.7.2.custom.css">
		<link rel="stylesheet" href="resources/ui.jqgrid.css">
		<link rel="stylesheet" href="configure/resources/style.css">
		
		<script type="text/javascript" src="resources/jquery-1.3.2.min.js"></script>
		<script type="text/javascript" src="resources/md5.js"></script>
		<script type="text/javascript" src="resources/json.js"></script>
		<script type="text/javascript" src="resources/jquery-ui-1.7.2.custom.min.js"></script>
		<script type="text/javascript" src="resources/jquery.jqGrid.min.js"></script>
		<script type="text/javascript" src="configure/resources/common.js"></script>
		<script type="text/javascript">
			var session = null;
			var loadedScripts = new Array();
			var controllers = {"menu-users": {"class" : "MollifyUsersConfigurationView", "script" : "configure/users/users.js", "title": "Users"}};
			var controller = null;
			
			$(document).ready(function() {
				preRequestCallback = function() { $("#request-indicator").addClass("active"); };
				postRequestCallback = function() { $("#request-indicator").removeClass("active"); }
				
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
					$("body").html("Current configuration type cannot be modified with the Mollify configuration utility. For more information, see <a href='http://code.google.com/p/mollify/wiki/Installation'>Installation instructions</a>");
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
						initView(controllers[id]);
					});
				} else {
					initView(controllers[id]);
				}				
			}
			
			function initView(controllerSpec) {
				setTitle(controllerSpec.title);
				
				controller = eval("new "+controllerSpec['class']+"()");
				if (controller.pageUrl) $("#page").load(controller.pageUrl, "", onLoadView);
			}
			
			function onLoadView() {
				initializeButtons();
				controller.onLoadView();
			}
			
			function onServerError(error) {
				$("body").html("<div class='error'><div class='title'>"+error+"</div></div>");
			}
			
			function onError(error) {
				setTitle("Error");
				$("#page").html("<div class='error'><div class='title'>"+error+"</div></div>");
			}
			
			function setTitle(title) {
				$("#page-title").html(title);
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
			<div id="page-area">
				<div id="page-header">
					<div id="page-title"></div>
					<div id="request-indicator"></div>
				</div>
				<div id="page"></div>
			</div>
		</div>
	</body>
</html>