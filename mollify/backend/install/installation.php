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

	function process() {
?>
	<span class="info">Mollify Installer supports only MySQL configuration, file based installation must be configured manually. <span class="details">See <a href="http://code.google.com/p/mollify/wiki/Installation">Installation instructions</a> for more information.</span></span>
	
	<span class="confirmation">Do you want to continue installing Mollify into MySQL database?
		<form method="post">
			<input type="hidden" name="type" value="mysql">
			<input type="submit" name="start" value="Continue">
		</form>
	</span>
<?php
	}
?>