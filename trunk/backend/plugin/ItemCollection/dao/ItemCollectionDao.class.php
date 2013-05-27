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
	
	class ItemCollectionDao {
		private $env;

		public function __construct($env) {
			$this->env = $env;
		}

		public function getItemCollection($id) {
			$db = $this->env->db();
			$list = $db->query("select ic.id as id, ic.name as name, ici.item_id as item_id from ".$db->table("itemcollection")." ic,".$db->table("itemcollection_item")." ici where ic.id = ".$db->string($id, TRUE)." and ici.collection_id = ic.id order by ici.item_index asc")->rows();
			if (count($list) == 0) return FALSE;
			
			$items = array();
			foreach($list as $c) {
				$items[] = $c["item_id"];
			}
			return array("id" => $list[0]["id"], "name" => $list[0]["name"], "items" => $this->items($items));
		}
		
		public function getUserItemCollections($userId) {
			$db = $this->env->db();
			$list = $db->query("select ic.id as id, ic.name as name, ici.item_id as item_id from ".$db->table("itemcollection")." ic,".$db->table("itemcollection_item")." ici where ic.user_id = ".$db->string($userId, TRUE)." and ici.collection_id = ic.id order by ic.created asc, ici.item_index asc")->rows();
			
			$res = array();
			$id = FALSE;
			$items = array();
			$collection = FALSE;
			$prev = NULL;
			foreach($list as $c) {
				if ($prev == NULL) $prev = $c;
				
				if (strcmp($prev["id"], $c["id"]) != 0) {
					$res[] = array("id" => $prev["id"], "name" => $prev["name"], "items" => $this->items($items));
					$items = array();
				}
				$items[] = $c["item_id"];
				$prev = $c;
			}
			if (count($items) > 0) $res[] = array("id" => $prev["id"], "name" => $prev["name"], "items" => $this->items($items));
			return $res;
		}
		
		public function addUserItemCollection($userId, $name, $items, $time) {
			$db = $this->env->db();
			$db->startTransaction();
			$db->update(sprintf("INSERT INTO ".$db->table("itemcollection")." (name, user_id, created) VALUES (%s, %s, %s)", $db->string($name, TRUE), $db->string($userId, TRUE), $db->string($time)));
			$cid = $db->lastId();
			
			$this->addCollectionItemRows($db, $cid, $items);
			$db->commit();
		}
		
		private function addCollectionItemRows($db, $cid, $items) {
			$itemIds = $db->query("select item_id from ".$db->table("itemcollection_item")." where collection_id = ".$db->string($cid, TRUE))->values("item_id");
			$ind = count($itemIds);
		
			foreach($items as $i) {
				if (in_array($i["id"], $itemIds)) continue;
				$db->update(sprintf("INSERT INTO ".$db->table("itemcollection_item")." (collection_id, item_id, item_index) VALUES (%s, %s, %s)", $db->string($cid, TRUE), $db->string($i["id"], TRUE), $db->string($ind++)));
				$itemIds[] = $i["id"];
			}
		}
		
		public function addCollectionItems($id, $userId, $items) {
			$db = $this->env->db();
			$list = $db->query("select id from ".$db->table("itemcollection")." where user_id = ".$db->string($userId, TRUE)." and id = ".$db->string($id, TRUE))->rows();
			if (count($list) == 0) return FALSE;
			
			$db->startTransaction();
			$this->addCollectionItemRows($db, $id, $items);
			$db->commit();
		}

		public function deleteUserItemCollection($id, $userId) {
			$db = $this->env->db();
			
			$list = $db->query("select id from ".$db->table("itemcollection")." where user_id = ".$db->string($userId, TRUE)." and id = ".$db->string($id, TRUE))->rows();
			if (count($list) == 0) return FALSE;
			
			$db->startTransaction();
			$db->update("DELETE FROM ".$db->table("itemcollection")." WHERE id = ".$db->string($id, TRUE));
			$db->update("DELETE FROM ".$db->table("itemcollection_item")." WHERE collection_id = ".$db->string($id, TRUE));
			$db->commit();
			return TRUE;
		}
		
		public function deleteUserItemCollections($userId) {
			$db = $this->env->db();
			
			$list = $db->query("select id from ".$db->table("itemcollection")." where user_id = ".$db->string($userId, TRUE))->values("id");
			if (count($list) == 0) return $list;
			
			$db->startTransaction();
			$db->update("DELETE FROM ".$db->table("itemcollection")." WHERE id in (".$db->arrayString($list, TRUE).")");
			$db->update("DELETE FROM ".$db->table("itemcollection_item")." WHERE collection_id in (".$db->arrayString($list, TRUE).")");
			$db->commit();
			return $list;
		}

		public function deleteCollectionItems($item) {
			$db = $this->env->db();
			return $db->update("DELETE FROM ".$db->table("itemcollection_item")." WHERE item_id = ".$db->string($item->id(), TRUE));
		}
		
		private function items($list) {
			$result = array();
			foreach($list as $id) {
				$result[] = $this->env->filesystem()->item($id);
			}
			return $result;
		}
		
		public function __toString() {
			return "ItemCollectionDao";
		}
	}
?>
