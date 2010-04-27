<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class RegistrationServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) == 1;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
				
		public function processPost() {
			if ($this->path[0] === 'create') $this->processRegister();
			else if ($this->path[0] === 'confirm') $this->processConfirm();
			else throw $this->invalidRequestException();
		}
		
		private function processRegister() {
			$registration = $this->request->data;
			if (!isset($registration['name']) or !isset($registration['password']) or !isset($registration['email'])) throw $this->invalidRequestException();
			
			$this->assertUniqueNameAndEmail($registration['name'], $registration['email']);
			
			$db = $this->env->configuration()->db();
			$name = $registration['name'];
			$password = $registration['password'];
			$email = $registration['email'];
			$time = date('YmdHis', time());
			$key = str_replace(".", "", uniqid("", TRUE));
			
			$db->update(sprintf("INSERT INTO ".$db->table("pending_registrations")." (`name`, `password`, `email`, `key`, `time`) VALUES (%s, %s, %s, %s, %s)", $db->string($name, TRUE), $db->string($password, TRUE), $db->string($email, TRUE), $db->string($key, TRUE), $time));
			
			$msg = $_SERVER["PHP_SELF"]."?confirm=".urlencode($email)."&key=".$key;
			$this->notify($email, "Mollify registration confirmation", $msg);
			
			$this->env->events()->onEvent(RegistrationEvent::registered($name, $email));
			$this->response()->success(array());
		}

		private function assertUniqueNameAndEmail($name, $email) {
			$db = $this->env->configuration()->db();
			$query = "select count(id) from ".$db->table("pending_registrations")." where name=".$db->string($name,TRUE)." or email=".$db->string($email,TRUE);
			$count = $db->query($query)->value(0);
			if ($count > 0) throw new ServiceException("User already registered with same name or email"); 
		}

		private function processConfirm() {
			$confirmation = $this->request->data;
			if (!isset($confirmation['email']) or !isset($confirmation['key'])) throw $this->invalidRequestException();
			
			$db = $this->env->configuration()->db();
			$query = "select `id`, `name`, `password`, `email` from ".$db->table("pending_registrations")." where `email`=".$db->string($confirmation['email'],TRUE)." and `key`=".$db->string($confirmation['key'],TRUE);
			$result = $db->query($query);
			if ($result->count() != 1) throw new ServiceException("Email and confirmation key don't match");

			$values = $result->firstRow();
			$this->env->configuration()->addUser($values['name'], $values['password'], $values['email'], Authentication::PERMISSION_VALUE_READONLY);
			
			$db->update("DELETE from ".$db->table("pending_registrations")." where `id`=".$db->string($values['id'],TRUE));
			
			$this->env->events()->onEvent(RegistrationEvent::confirmed($values['name']));
			$this->response()->success(array());
		}
		
		private function notify($email, $subject, $msg) {
			if (Logging::isDebug())
				Logging::logDebug("Registration confirmation to ".$email.": [".$msg."]");
			else
				mail($email, $subject, $msg);
		}
				
		public function __toString() {
			return "RegistrationServices";
		}
	}
?>