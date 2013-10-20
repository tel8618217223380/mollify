<?php
	require_once("vendor/phpass/PasswordHash.php");
	/**
	 * FilesystemServices.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class Mollify_PasswordHash {
		private static $hash_cost_log2 = 8;
		private static $hash_portable = FALSE;
		
		private $serverSalt;

		public function __construct($serverSalt="") {
			$this->serverSalt = $serverSalt;
			$this->hasher = new PasswordHash(self::$hash_cost_log2, self::$hash_portable);
		}
		
		public function createHash($pw, $salt) {
			$hash = $this->hasher->HashPassword($this->serverSalt.$pw.$salt);
			if (strlen($hash) < 20)
				throw new ServiceException("REQUEST_FAILED");
			return $hash;
		}
		
		public function isEqual($pw, $hash, $salt) {
			return $this->hasher->CheckPassword($this->serverSalt.$pw.$salt, $hash);
		}
	}
?>