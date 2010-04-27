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
			return count($path) <= 1;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
		
		public function processGet() {
			if (count($this->path) == 1 and $this->path[0] === 'confirm') {
				//TODO check key
				//TODO if correct, activate account
			}
			throw $this->invalidRequestException();
		}
		
		public function processPost() {
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
			
			//TODO send confirm email with link
			$this->env->events()->onEvent(RegistrationEvent::registered($name, $email));
			$this->response()->success(array());
		}
		
		private function assertUniqueNameAndEmail($name, $email) {
			$db = $this->env->configuration()->db();
			$query = "select count(id) from ".$db->table("pending_registrations")." where name=".$db->string($name,TRUE)." or email=".$db->string($email,TRUE);
			$count = $db->query($query)->value(0);
			if ($count > 0) throw new ServiceException("User already registered with same name or email"); 
		}
		
		public function __toString() {
			return "RegistrationServices";
		}
	}
?>