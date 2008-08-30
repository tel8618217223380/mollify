<!--
  Copyright (c) 2008- Samuli Järvelä
 
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
  this entire header must remain intact.
 -->

<?php
	//TODO implement user authentication
	
	function get_account() {
		$result = array();
		$result["roots"] = array(
			array("name" => "Root folder 1", "path" => "/foo/bar/"),
			array("name" => "Root folder 2", "path" => "/foo/bay/")
		);
		return $result;
	}
?>