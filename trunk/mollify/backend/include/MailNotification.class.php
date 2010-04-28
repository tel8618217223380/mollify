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

	class MailNotification {
		private $env;
		private $enabled;
		
		public function __construct($env) {
			$this->env = $env;
			$this->enabled = $env->features()->isFeatureEnabled("mail_notification");
		}
		
		public function send($to, $subject, $message) {
			if (Logging::isDebug())
				Logging::logDebug("Sending mail to ".$to.": [".$message."]");
			
			if ($this->enabled)
				mail($email, $subject, $msg);
		}
				
		public function __toString() {
			return "MailNotification";
		}
	}
?>