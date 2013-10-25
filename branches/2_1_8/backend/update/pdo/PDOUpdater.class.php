<?php

	/**
	 * PDOUpdater.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	
	require_once("install/pdo/PDOInstaller.class.php");
	
	class PDOUpdater extends PDOInstaller {
		
		public function __construct($settings) {
			parent::__construct($settings, "update");
		}
		
		public function getVersionHistory() {
			return array("1_8_8", "2_0");
		}
		
		public function updateVersionStep($from, $to) {
			$this->util()->updateVersionStep($from, $to);
		}
		
		public function getConversion($versionTo) {
			return NULL;
		}
		
		public function process() {}
				
		public function __toString() {
			return "PDOUpdater";
		}
	}
?>