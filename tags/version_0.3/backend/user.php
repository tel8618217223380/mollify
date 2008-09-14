<?php
	//TODO implement user authentication
	
	function get_account() {
		$result = array();
		$result["roots"] = array(
			array("name" => "Folder A", "path" => "/foo/bar"),
			array("name" => "Folder B", "path" => "/foo/bay")
		);
		return $result;
	}
?>