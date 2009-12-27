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
	 
	 global $MAIN_PAGE;
	 if (!isset($MAIN_PAGE)) die();
	 include("installation_page.php");
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
	<?php pageHeader("Mollify Installation", "init"); ?>
	
	<body class="content" id="install-instructions-type">
		<h1>Configuration Type</h1>
		<p>
			blah blah
		</p>
		<button id="button-continue">Continue</button>
		
		<script type="text/javascript">
			function init() {
				createAction("continue", "#install-instructions-type", "button#button-continue");
			}
		</script>
	</body>
</html>