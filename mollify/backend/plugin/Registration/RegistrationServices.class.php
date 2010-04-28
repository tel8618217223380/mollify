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

	class RegistrationServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return count($path) >= 1;
		}
		
		public function isAuthenticationRequired() {
			return FALSE;
		}
		
		public function processGet() {
			if (count($this->path) != 1 or $this->path[0] != 'list') throw $this->invalidRequestException();
			$this->env->authentication()->assertAdmin();
			
			$db = $this->env->configuration()->db();
			$result = $db->query("select `id`, `name`, `email`, `key`, `time` from ".$db->table("pending_registrations")." order by id asc")->rows();
			$this->response()->success($result);
		}

		public function processDelete() {
			if (count($this->path) != 2 or $this->path[0] != 'list') throw $this->invalidRequestException();
			$this->env->authentication()->assertAdmin();
			
			$id = $this->path[1];
			$db = $this->env->configuration()->db();
			$result = $db->update("delete from ".$db->table("pending_registrations")." where id=".$db->string($id, TRUE));
			$this->response()->success(array());
		}
		
		public function processPost() {
			if (count($this->path) == 1 and $this->path[0] === 'create') {
				$this->processRegister();
			} else if (count($this->path) >= 1 and $this->path[0] === 'confirm') {
				if (count($this->path) == 1) $this->processConfirm();
				else $this->processConfirmById($this->path[1]);
			}
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
			
			$msg = $_SERVER["REQUEST_URI"]."?confirm=".urlencode($email)."&key=".$key;
			$this->notify($email, "Mollify registration confirmation", $msg);
			
			$this->env->events()->onEvent(RegistrationEvent::registered($name, $email));
			$this->response()->success(array());
		}

		private function assertUniqueNameAndEmail($name, $email) {
			$db = $this->env->configuration()->db();
			$query = "select count(id) from ".$db->table("pending_registrations")." where name=".$db->string($name,TRUE)." or email=".$db->string($email,TRUE);
			$count = $db->query($query)->value(0);
			if ($count > 0) throw new ServiceException("REQUEST_FAILED", "User already registered with same name or email"); 
		}

		private function processConfirm() {
			$confirmation = $this->request->data;
			if (!isset($confirmation['email']) or !isset($confirmation['key'])) throw $this->invalidRequestException();
			
			$db = $this->env->configuration()->db();
			$query = "select `id`, `name`, `password`, `email` from ".$db->table("pending_registrations")." where `email`=".$db->string($confirmation['email'],TRUE)." and `key`=".$db->string($confirmation['key'],TRUE);
			$result = $db->query($query);
			
			if ($result->count() != 1) throw new ServiceException("REQUEST_FAILED", "Email and confirmation key don't match");
			$this->confirm($result->firstRow());
		}
		
		private function processConfirmById($id) {
			$this->env->authentication()->assertAdmin();
			
			$db = $this->env->configuration()->db();
			$query = "select `id`, `name`, `password`, `email` from ".$db->table("pending_registrations")." where `id`=".$db->string($id,TRUE);
			$result = $db->query($query);
			
			if ($result->count() != 1) throw new ServiceException("Registration not found");
			$this->confirm($result->firstRow());
		}
		
		private function confirm($registration) {
			$db = $this->env->configuration()->db();
			
			$this->env->configuration()->addUser($registration['name'], $registration['password'], $registration['email'], Authentication::PERMISSION_VALUE_READONLY);
			$db->update("DELETE from ".$db->table("pending_registrations")." where `id`=".$db->string($registration['id'],TRUE));
			
			$this->env->events()->onEvent(RegistrationEvent::confirmed($registration['name']));
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