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
	
	require_once("dao/ShareDao.class.php");
	
	class ShareHandler {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;			
		}
				
		public function getItemContextData($item, $details, $key, $data) {
			return array(
				"count" => $this->dao()->getShareCount($item, $this->env->authentication()->getUserId())
			);
		}
		
		public function getShares($item) {
			return $this->dao()->getShares($item, $this->env->authentication()->getUserId());
		}

		public function addShare($item) {
			$this->dao()->addShare($this->GUID(), $item, $this->env->authentication()->getUserId(), time());
		}
				
		public function onEvent($e) {
			if (strcmp(FilesystemController::EVENT_TYPE_FILE, $e->type()) != 0) return;
			$type = $e->subType();
			
			if ($type === FileEvent::DELETE)
				$this->dao()->deleteShares($e->item());
		}
		
		private function dao() {
			return new ShareDao($this->env);
		}
		
		private function GUID() {
			if (function_exists('com_create_guid') === true)
				return str_replace('-', '', trim(com_create_guid(), '{}'));
			return sprintf('%04X%04X%04X%04X%04X%04X%04X%04X', mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(16384, 20479), mt_rand(32768, 49151), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535));
		}
				
		public function __toString() {
			return "CommentHandler";
		}
	}
?>