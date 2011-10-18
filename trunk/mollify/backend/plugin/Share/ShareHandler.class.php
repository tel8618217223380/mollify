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
				"count" => $this->getDao()->getCommentCount($item)
			);
		}
		
		/*public function onEvent($e) {
			if (strcmp(FilesystemController::EVENT_TYPE_FILE, $e->type()) != 0) return;
			$type = $e->subType();
			if (!in_array($type, array(FileEvent::MOVE, FileEvent::RENAME, FileEvent::DELETE))) return;
			
			if ($type === FileEvent::DELETE)
				$this->getDao()->deleteComments($e->item());
			else if ($type === FileEvent::MOVE or $type === FileEvent::RENAME)
				$this->getDao()->moveComments($e->item(), $e->info());
		}*/
		
		private function getDao() {
			return new ShareDao($this->env);
		}
				
		public function __toString() {
			return "CommentHandler";
		}
	}
?>