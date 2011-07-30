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
	
	require_once("dao/CommentDao.class.php");
	
	class CommentHandler {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function onEvent($e) {
			if (strcmp(FilesystemController::EVENT_TYPE_FILE, $e->type()) != 0) return;
			$type = $e->subType();
			if (!in_array($type, array(FileEvent::MOVE, FileEvent::RENAME, FileEvent::DELETE))) return;
			
			if ($type === FileEvent::DELETE)
				$this->getDao()->deleteComments($e->item());
			else if ($type === FileEvent::MOVE or $type === FileEvent::RENAME)
				$this->getDao()->moveComments($e->item(), $e->info());
		}
		
		public function getItemDetails($item, $details) {
			return array("comments" => array(
				"count" => $this->getDao()->getCommentCount($item)
			));
		}

		public function getComments($item) {
			return $this->getDao()->getComments($item);
		}
		
		public function addComment($user, $item, $comment) {
			$this->getDao()->addComment($user, $item, time(), $comment);
		}
		
		public function getRequestKey() {
			return "plugin-comment-count";
		}
		
		public function getRequestData($parent, $items, $result, $dataRequest) {
			$counts = $this->getDao()->getCommentCountForChildren($parent);
			$result = array();
			foreach($counts as $id=>$c) {
				$result[$this->env->filesystem()->item($id)->publicId()] = $c;
			} 
			return $result;
		}
		
		private function getDao() {
			return new CommentDao($this->env);
		}
				
		public function __toString() {
			return "CommentHandler";
		}
	}
?>