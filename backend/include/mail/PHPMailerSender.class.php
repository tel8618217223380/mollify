<?php

	/**
	 * MailNotificator.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class Mollify_MailSender {
		private $env;
		private $enabled;
		
		public function __construct($env) {
			$this->env = $env;
			$this->enabled = $env->features()->isFeatureEnabled("mail_notification");
		}
		
		public function send($to, $subject, $message, $from = NULL, $attachments = NULL) {
			if (Logging::isDebug())
				Logging::logDebug("Sending mail to [".Util::array2str($to)."]: [".$message."]");
			
			if (!$this->enabled) return;
			
			$isHtml = (stripos($message, "<html>") !== FALSE);
			$f = ($from != NULL ? $from : $this->env->settings()->setting("mail_notification_from"));
			
			$validRecipients = $this->getValidRecipients($to);
			if (count($validRecipients) === 0) {
				Logging::logDebug("No valid recipient email addresses, no mail sent");
				return;
			}
			
			set_include_path("PHPMailer".DIRECTORY_SEPARATOR.PATH_SEPARATOR.get_include_path()); 
			require 'class.phpmailer.php';
			
			$mailer = new PHPMailer;
			
			$smtp = $this->settings()->getSetting("mail_smtp");
			if ($smtp != NULL and isset($smtp["host"])) {
				$mail->isSMTP();
				$mail->Host = $smtp["host"];
				
				if (isset($smtp["username"]) and isset($smtp["password"])) {
					$mail->SMTPAuth = true;
					$mail->Username = $smtp["username"];
					$mail->Password = $smtp["password"];
				}
				if (isset($smtp["secure"]))
					$mail->SMTPSecure = $smtp["secure"];
			}
			
			$mail->From = $f;
			foreach ($recipients as $recipient) {
				$mail->addBCC($recipient["name"], $recipient["email"]);
			}

			if (!$isHtml)
				$mail->WordWrap = 50;
			else
				$mail->isHTML(true);
			
			if ($attachments != NULL) {
				//TODO use stream
				foreach ($attachments as $attachment)
					$mail->addAttachment($attachment);
			}
			
			$mail->Subject = $subject;
			$mail->Body    = $message;
			
			if(!$mail->send()) {
				Logging::logError('Message could not be sent: '.$mail->ErrorInfo);
				return FALSE;
			}
			return TRUE;
		}
		
		private function getValidRecipients($recipients) {
			$valid = array();
			foreach ($recipients as $recipient) {
				if ($recipient["email"] === NULL or strlen($recipient["email"]) == 0) continue;
				$valid[] = $recipient;
			}
			return $valid;
		}
				
		public function __toString() {
			return "Mollify_MailSender_PHPMailer";
		}
	}
?>