<?php
	function get_params() {
		if (!isset($_GET["params"])) return FALSE;
		$value = $_GET["params"];
		if ($value === '0') return FALSE;
		$list = explode(';', base64_decode($value));
		$result = array();
		foreach ($list as $item) {
			$parts = explode('=', $item);
			if (count($parts) < 2) continue;
			$result[$parts[0]] = $parts[1];
		}
		return $result;
	}
?>