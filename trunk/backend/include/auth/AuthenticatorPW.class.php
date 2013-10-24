<?php
	/**
	 * AuthenticatorPW.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class Mollify_Authenticator_PW extends Mollify_Authenticator {
		public function __construct($settings) {
		}
		
		public function createHash($pw, $salt) {
			$hash = $this->hasher->HashPassword($this->serverSalt.$pw.$salt);
			if (strlen($hash) < 20)
				throw new ServiceException("REQUEST_FAILED");
			return $hash;
		}
		
		public function isEqual($pw, $hash, $salt) {
			$ret = $this->hasher->CheckPassword($this->serverSalt.$pw.$salt, $hash);
			return ($ret === TRUE or $ret == 1);
		}
	}
?>