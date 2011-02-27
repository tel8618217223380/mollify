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
	
	class CommentDao {
		private $env;

		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getCommentCount($item) {
			$db = $this->env->configuration()->db();
			return $db->query("select count(`id`) from ".$db->table("comment")." where `item_id` = ".$db->string($item->id(), TRUE))->value(0);
		}
		
		public function addComment($userId, $item, $time, $comment) {
			$db = $this->env->configuration()->db();
			$db->update(sprintf("INSERT INTO ".$db->table("comment")." (item_id, user_id, time, comment) VALUES (%s, %s, %s, %s)", $db->string($userId, TRUE), $db->string($item->id(), TRUE), $db->string(date('YmdHis', $time)), $db->string($comment, TRUE)));
			return $db->lastId();
		}
						
		public function __toString() {
			return "CommentDao";
		}
	}
?>