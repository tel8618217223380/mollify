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
	
	function pageHeader($title, $onLoad = NULL) { ?>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<title><?php echo($title);?></title>
			<link rel="stylesheet" href="resources/style.css">
			<script type="text/javascript" src="resources/jquery.js"></script>
			<script type="text/javascript" src="install/common.js"></script>
			<script type="text/javascript">
			<?php if ($onLoad != NULL) {?>
				$(document).ready(function() {
					<?php echo($onLoad);?>();
				 });
			<?php }?>
			</script>
		</head><?php
	}
	
	function pageData() { ?>
		<form id="page-data" method="post">
		<?php if (isset($_POST)) foreach ($_POST as $key => $val) if ($key != 'action') echo '<input type="hidden" name="'.$key.'" value="'.$val.'">';?>
		</form><?php
 	}
?>