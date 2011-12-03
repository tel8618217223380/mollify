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
	
	class ShareDao {
		private $env;

		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getShareCount($item, $userId) {
			$db = $this->env->configuration()->db();
			return $db->query("select count('id') from ".$db->table("share")." where item_id = ".$db->string($item->id(), TRUE)." and user_id = ".$db->string($userId, TRUE))->value(0);
		}

		public function getShare($id) {
			$db = $this->env->configuration()->db();
			return $db->query("select id, item_id from ".$db->table("share")." where active=1 and id = ".$db->string($id, TRUE))->firstRow();
		}

		public function getShares($item, $userId) {
			$db = $this->env->configuration()->db();
			return $db->query("select id from ".$db->table("share")." where item_id = ".$db->string($item->id(), TRUE)." and user_id = ".$db->string($userId, TRUE))->rows();
		}
		
		public function addShare($id, $item, $userId, $time) {
			$db = $this->env->configuration()->db();
			$db->update(sprintf("INSERT INTO ".$db->table("share")." (id, item_id, user_id, created, active) VALUES (%s, %s, %s, %s, 1)", $db->string($id, TRUE), $db->string($item->id(), TRUE), $db->string($userId, TRUE), $db->string(date('YmdHis', $time))));
		}
		
		public function deleteShares($item) {
			$db = $this->env->configuration()->db();
			if ($item->isFile())
				return $db->update("DELETE FROM ".$db->table("share")." WHERE item_id = ".$db->string($item->id(), TRUE));
			else
				return $db->update(sprintf("DELETE FROM ".$db->table("share")." WHERE item_id in (select id from ".$db->table("item_id")." where path like '%s%%')", $db->string($item->location())));
		}
						
		public function __toString() {
			return "ShareDao";
		}
	}
?>