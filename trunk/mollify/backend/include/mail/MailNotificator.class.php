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

	class MailNotificator {
		private $env;
		private $enabled;
		
		public function __construct($env) {
			$this->env = $env;
			$this->enabled = $env->features()->isFeatureEnabled("mail_notification");
		}
		
		public function send($to, $subject, $message, $from = NULL) {
			if (Logging::isDebug())
				Logging::logDebug("Sending mail to [".Util::array2str($to)."]: [".$message."]");
			
			if ($this->enabled) {
				$f = ($from != NULL ? $from : $this->env->settings()->setting("mail_notification_from"));
				
				$headers = 'From:'.$f;
				$count = 0;
				foreach ($to as $recipient) {
					if ($recipient["email"] === NULL or strlen($recipient["email"]) == 0) continue;
					
					if ($count == 0) {
						$headers .= PHP_EOL.'Bcc:'.$recipient["name"].'<'.$recipient["email"].'>';
					} else {
						$headers .= ','.$recipient["name"].'<'.$recipient["email"].'>';
					}
					
					$count .= 1;
				}
				if ($count === 0) {
					Logging::logDebug("No valid recipient email addresses, no mail sent");
					return;
				}
				mail('', $subject, wordwrap($message), $headers);
			}
		}
				
		public function __toString() {
			return "MailNotificator";
		}
	}
?>