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
			$type = $e->typeId();
			//$userId = $this->getUserId($e);
			
			//TODO handle remove, rename and move
		}
		
		public function getItemDetails($item, $details) {
			return array("comments" => array(
				"count" => $this->getDao()->getCommentCount($item)
			));
		}
		
		public function addComment($user, $item, $comment) {
			$this->getDao()->addComment($user, $item, time(), $comment);
		}
		
		private function getDao() {
			return new CommentDao($this->env);
		}
				
		public function __toString() {
			return "CommentHandler";
		}
	}
?>