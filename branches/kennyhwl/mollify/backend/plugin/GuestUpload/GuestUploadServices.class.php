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

	class GuestUploadServices extends ServicesBase {
		const SUBJECT = "Upload link";
		const MESSAGE = "Open following link to upload files: %link%";
		
		protected function isValidPath($method, $path) {
			return TRUE;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processGet() {
			if (count($this->path) != 1 or $this->path[0] != 'info') throw $this->invalidRequestException();
			$user = $this->env->configuration()->getUser($this->env->authentication()->getUserId());
			
			$this->response()->success(array("subject" => self::SUBJECT, "message" => self::MESSAGE));
		}
		
		public function processPost() {
			if (count($this->path) != 1) throw $this->invalidRequestException();
			
			if ($this->path[0] === 'send') $this->doSend();
			else if ($this->path[0] === 'upload') $this->doUpload();
			else throw $this->invalidRequestException();
		}

		private function doSend() {			
			if (!$this->request->hasData()) throw $this->invalidRequestException();
			
			$data = $this->request->data;
			if (!isset($data["to"]) or !isset($data["subject"]) or !isset($data["message"])) throw $this->invalidRequestException();
			
			$user = $this->env->configuration()->getUser($this->env->authentication()->getUserId());
			if ($user["email"] == NULL or strlen($user["email"]) == 0) $this->response()->fail("1", "User does not have email defined");
			
			$link = $this->env->getPluginUrl("GuestUpload")."?id=".$this->env->authentication()->getUserId();
			$values = array("link" => $link);
			
			$msg = Util::replaceParams($data["message"], $values);
			$recipient = array(array("email" => $data["to"]));
			
			$this->env->notificator()->send($recipient, $data["subject"], $msg);
			$this->response()->success(TRUE);
		}
		
		private function doUpload() {
			if (!$this->request->hasParam("id")) throw $this->invalidRequestException();
			
			$folder = $this->getInboxFolder($this->request->param("id"));
			require_once("include/filesystem/plupload.php");
			plupload($folder, $this->env->events());
		}
		
		private function getInboxFolder($id) {
			$folder = $this->env->customizations()->getUserFolder($id);
			$target = $this->env->filesystem()->filesystemFromId($folder["id"]);
			$root = $target->root();
			return $root->folderWithName(KennyHWLCustomizations::$INBOX_NAME);
		}
	}