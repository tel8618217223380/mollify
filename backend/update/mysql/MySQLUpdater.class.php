<?php

	/**
	 * MySQLUpdater.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	
	require_once("install/mysql/MySQLInstaller.class.php");
	
	class MySQLUpdater extends MySQLInstaller {
		
		public function __construct($settings) {
			parent::__construct($settings, "update");
		}
		
		public function getVersionHistory() {
			return array("0_9_5", "1_0_0", "1_5_0", "1_5_4", "1_6_0", "1_7_8", "1_7_10", "1_8_1", "1_8_5", "1_8_7", "1_8_8", "2_0");
		}
		
		public function updateVersionStep($from, $to) {
			$this->util()->updateVersionStep($from, $to);
		}
		
		public function getConversion($versionTo) {
			if (strcmp("1_8_5", $versionTo) === 0) {
				require_once("update/conversion/1_8_5.php");
				return new Upd_1_8_5();
			}
			return NULL;
		}
		
		public function process() {}
				
		public function __toString() {
			return "MySQLUpdater";
		}
	}
?>