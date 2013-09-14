<?php

	/**
	 * ShareDao.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */
	
	class ShareDao {
		private $env;

		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getShareCount($item, $userId) {
			$db = $this->env->db();
			return $db->query("select count('id') from ".$db->table("share")." where item_id = ".$db->string($item->id(), TRUE)." and user_id = ".$db->string($userId, TRUE))->value(0);
		}

		public function getShare($id, $mustBeValidAfter = NULL) {
			$db = $this->env->db();
			$query = "select id, name, item_id, active from ".$db->table("share")." where active=1 and id = ".$db->string($id, TRUE);
			if ($mustBeValidAfter)
				$query .= ' and (expiration is null or expiration >= '.$db->string($mustBeValidAfter).')';
			return $db->query($query)->firstRow();
		}

		public function getUserShares($userId) {
			$db = $this->env->db();
			$list = $db->query("select id, user_id, item_id, name, expiration, active from ".$db->table("share")." where user_id = ".$db->string($userId, TRUE)." order by item_id asc, created asc")->rows();
			
			$res = array();
			$itemId = FALSE;
			$userId = FALSE;
			foreach($list as $s) {
				//Logging::logDebug(Util::array2str($s));
				if ($s["user_id"] != $userId) {
					$res[$s["user_id"]] = array();
				}
				$userId = $s["user_id"];

				if ($s["item_id"] != $itemId) {
					$res[$userId][$s["item_id"]] = array();
				}
				$itemId = $s["item_id"];

				$res[$userId][$itemId][] = array("id" => $s["id"], "name" => $s["name"], "expiration" => $s["expiration"],"active" => ($s["active"] == 1));
			}
			return $res;
		}

		public function getShares($itemId, $userId) {
			$db = $this->env->db();
			$list = $db->query("select id, name, expiration, active from ".$db->table("share")." where item_id = ".$db->string($itemId, TRUE)." and user_id = ".$db->string($userId, TRUE)." order by created asc")->rows();
			
			$res = array();
			foreach($list as $s)
				$res[] = array("id" => $s["id"], "name" => $s["name"], "expiration" => $s["expiration"],"active" => ($s["active"] == 1));
			return $res;
		}
		
		public function getShareUsers($i) {
			$db = $this->env->db();
			if (is_array($i)) {
				$itemIds = array();
				foreach($i as $item)
					$itemIds[] = $item->id();
				$itemIds = sprintf("item_id in (%s)", $db->arrayString($itemIds, TRUE));
				
//				return $db->query("select distinct user_id from ".$db->table("share")." where ".$itemIds)->values("user_id")." group by item_id";
			} else {
				$itemIds = "item_id = ".$db->string($i->id(), TRUE);
			}

			return $db->query("select distinct item_id, user_id from ".$db->table("share")." where ".$itemIds." group by item_id")->rows();
//			return $db->query("select distinct user_id from ".$db->table("share")." where ".$itemId)->values("user_id");
		}
		
		public function getUserSharesForChildren($p, $currentUser) {
			$db = $this->env->db();
			$parentLocation = $db->string(str_replace("\\", "\\\\", $p->location()));
			
			if (strcasecmp("mysql", $this->env->db()->type()) == 0) {
				$itemFilter = "select id from ".$db->table("item_id")." where path REGEXP '^".$parentLocation."[^/\\\\]+[/\\\\]?$'";
			} else {
				$itemFilter = "select id from ".$db->table("item_id")." where REGEX(path, \"#^".$parentLocation."[^/\\\\]+[/\\\\]?$#\")";
			}
			return $db->query("select item_id, sum(case when user_id = ".$db->string($currentUser, TRUE)." then 1 else 0 end) as own, sum(case when user_id <> ".$db->string($currentUser, TRUE)." then 1 else 0 end) as other from ".$db->table("share")." where item_id in (".$itemFilter.") group by item_id")->valueMap("item_id", "own", "other");
		}

		public function getUserSharesForItems($items, $currentUser) {
			$ids = array();
			foreach($items as $i)
				$ids[] = $i->id();

			$db = $this->env->db();		
			return $db->query("select item_id, sum(case when user_id = ".$db->string($currentUser, TRUE)." then 1 else 0 end) as own, sum(case when user_id <> ".$db->string($currentUser, TRUE)." then 1 else 0 end) as other from ".$db->table("share")." where item_id in (".$db->arrayString($ids, TRUE).") group by item_id")->valueMap("item_id", "own", "other");
		}
				
		public function addShare($id, $itemId, $name, $userId, $expirationTime, $time, $active = TRUE) {
			$db = $this->env->db();
			$db->update(sprintf("INSERT INTO ".$db->table("share")." (id, name, item_id, user_id, expiration, created, active) VALUES (%s, %s, %s, %s, %s, %s, %s)", $db->string($id, TRUE), $db->string($name, TRUE), $db->string($itemId, TRUE), $db->string($userId, TRUE), $db->string($expirationTime), $db->string($time), ($active ? "1" : "0")));
		}
		
		public function editShare($id, $name, $expirationTime, $active) {
			$db = $this->env->db();
			$db->update(sprintf("UPDATE ".$db->table("share")." SET name = %s, active = %s, expiration = %s WHERE id=%s", $db->string($name, TRUE), ($active ? "1" : "0"), $db->string($expirationTime), $db->string($id, TRUE)));
		}

		public function deleteShare($id) {
			$db = $this->env->db();
			return $db->update("DELETE FROM ".$db->table("share")." WHERE id = ".$db->string($id, TRUE));
		}
		
		public function deleteShares($item) {
			$db = $this->env->db();
			if ($item->isFile())
				return $db->update("DELETE FROM ".$db->table("share")." WHERE item_id = ".$db->string($item->id(), TRUE));
			else
				return $db->update(sprintf("DELETE FROM ".$db->table("share")." WHERE item_id in (select id from ".$db->table("item_id")." where path like '%s%%')", str_replace("'", "\'", $db->string($item->location()))));
		}

		public function deleteSharesForItem($itemId) {
			$db = $this->env->db();
			return $db->update("DELETE FROM ".$db->table("share")." WHERE item_id = ".$db->string($itemId, TRUE));
		}
						
		public function __toString() {
			return "ShareDao";
		}
	}
?>
