<?php
	/**
	 * PermissionsDao.class.php
	 *
	 * Copyright 2008- Samuli Järvelä
	 * Released under GPL License.
	 *
	 * License: http://www.mollify.org/license.php
	 */

	class Mollify_PermissionsDao {
		private $db;

		public function __construct($db) {
			$this->db = $db;
		}
		
		public function getFilesystemPermission($name, $item, $userId, $groupIds = NULL) {
			$mysql = (strcmp("mysql", $this->db->type()) == 0);
			$table = $this->db->table("permission");
			$id = $item->id();
			
			$userIds = array(0, $userId);
			if ($groupIds != NULL)
				foreach($groupIds as $g)
					$userIds[] = $g;
			$userQuery = sprintf("(user_id in (%s))", $this->db->arrayString($userIds));

			// order within category into 1) user specific 2) group 3) item default
			if ($mysql) {
				$subcategoryQuery = sprintf("(IF(user_id = '%s', 1, IF(user_id = '0', 3, 2)))", $userId);
			} else {
				$subcategoryQuery = sprintf("case when user_id = '%s' then 1 when user_id = '0' then 3 else 2 end", $userId);
			}

			// item permissions
			$query = sprintf("SELECT value, user_id, case when subject is null then 2 else 1 AS 'category', %s AS 'subcategory' FROM ".$table." WHERE name='%s' AND (subject is null OR subject = '%s') AND %s", $name, $subcategoryQuery, $id, $userQuery);
					
			if ($item->isFile() or !$item->isRoot()) {
				$parentLocation = $item->parent()->location();
				$rootLocation = $item->root()->location();
				
				if ($mysql)
					$hierarchyQuery = "(i.path REGEXP '^".str_replace("'", "\'", str_replace("\\", "\\\\", $rootLocation));
				else
					$hierarchyQuery = "REGEX(i.path, '#^".str_replace("'", "\'", str_replace("\\", "\\\\", $rootLocation));
				
				$hierarchyQueryEnd = "";
				$parts = preg_split("/[\/\\\\]+/", substr($parentLocation, strlen($rootLocation)), -1, PREG_SPLIT_NO_EMPTY);
				//Logging::logDebug(Util::array2str($parts));
				foreach($parts as $part) {
					$hierarchyQuery .= "(".str_replace("'", "\'", $part).DIRECTORY_SEPARATOR;
					$hierarchyQueryEnd .= ")*";
				}
				if ($mysql)
					$hierarchyQuery .= $hierarchyQueryEnd."$')";
				else
					$hierarchyQuery .= $hierarchyQueryEnd."$#')";
			
				$categoryQuery = "case when p.subject is null then 3 when i.id = '".$id."' then 1 else 2 end";
				
				if ($mysql) {
					$subcategoryQuery = sprintf("(((%s - CHAR_LENGTH(i.path)) * 10) + IF(user_id = '%s', 0, IF(user_id = '0', 2, 1)))", strlen($parentLocation), $userId);
				} else {
					$subcategoryQuery = sprintf("((%s - LENGTH(i.path)) * 10) + (case when user_id = '%s' then 0 when user_id = '0' then 2 else 1 end)", strlen($parentLocation), $userId);
				}
				$query = sprintf("SELECT value, user_id, %s AS category, %s AS subcategory FROM ".$table." p, ".$this->db->table("item_id")." i WHERE p.name = '%s' AND (p.subject is null OR (p.subject = i.id AND (i.id = '%s' OR %s))) AND %s", $categoryQuery, $subcategoryQuery, $name, $id, $hierarchyQuery, $userQuery);
			}
			
			$query = "SELECT value FROM (".$query.") as u ORDER BY u.category ASC, u.subcategory ASC, u.value DESC";
			
			$result = $this->db->query($query);
			if ($result->count() < 1) return NULL;
			return $result->value();
		}
		
		public function getFilesystemPermissionsForChildren($name, $parent, $userId, $groupIds = NULL) {
			$parentLocation = str_replace("'", "\'", str_replace("\\", "\\\\", $parent->location()));
			$table = $this->db->table("permission");
			$mysql = (strcmp("mysql", $this->db->type()) == 0);
			
			$userIds = array(0, $userId);
			if ($groupIds != NULL)
				foreach($groupIds as $g)
					$userIds[] = $g;
			$userQuery = sprintf("(user_id in (%s))", $this->db->arrayString($userIds));

			//TODO subject asc? -> join p.subject = item.id & item.location asc
			if ($mysql) {
				$itemFilter = "SELECT distinct item_id from ".$table." p, ".$this->db->table("item_id")." i where p.subject = i.id and ".$userQuery." and i.path REGEXP '^".$parentLocation."[^/\\\\]+[/\\\\]?$'";
				$query = sprintf("SELECT subject, value, (IF(user_id = '%s', 1, IF(user_id = '0', 3, 2))) as ind from %s where %s and subject in (%s) order by subject asc, ind asc, value desc", $userId, $table, $userQuery, $itemFilter);
			} else {
				$itemFilter = "SELECT distinct item_id from ".$table." p, ".$this->db->table("item_id")." i where p.subject = i.id and ".$userQuery." and REGEX(i.path, \"#^".$parentLocation."[^/\\\\]+[/\\\\]?$#\")";
				$query = sprintf("SELECT subject, value, case when user_id = '%s' then 1 when user_id = '0' then 3 else 2 end as ind from %s where %s and subject in (%s) order by subject asc, ind asc, value desc", $userId, $table, $userQuery, $itemFilter);
			}			
			
			$all = $this->db->query($query)->rows();
			$all[] = array(
				"item_id" => $parent->id(),
				"permission" => $this->getItemPermission($parent, $userId)
			);
			$k = array();
			$prev = NULL;
			foreach($all as $p) {
				$id = $p["item_id"];
				if ($id != $prev) $k[$id] = strtoupper($p["permission"]);
				$prev = $id;
			}
			return $k;
		}
	
		/*function getItemPermissions($item) {
			$id = $this->itemId($item);
			$rows = $this->db->query(sprintf("SELECT user.id as user_id, user.is_group as is_group, item_permission.permission as permission FROM ".$this->db->table("item_permission")." as item_permission LEFT OUTER JOIN ".$this->db->table("user")." as user ON user.id = item_permission.user_id WHERE item_permission.item_id = '%s'", $id))->rows();
			
			$list = array();
			foreach ($rows as $row) {
				if (!isset($row["user_id"]))
					$list[] = array("item_id" => $item->id(), "user_id" => '0', "is_group" => 0, "permission" => $row["permission"]);
				else
					$list[] = array("item_id" => $item->id(), "user_id" => $row["user_id"], "is_group" => $row["is_group"], "permission" => $row["permission"]);
			}
			return $list;
		}
			
		function updateItemPermissions($updates) {
			$new = $updates['new'];
			$modified = $updates['modified'];
			$removed = $updates['removed'];
			$mysql = (strcmp("mysql", $this->db->type()) == 0);
			
			if ($mysql) $this->db->startTransaction();
			if (count($new) > 0) $this->addItemPermissionValues($new);
			if (count($modified) > 0) $this->updateItemPermissionValues($modified);
			if (count($removed) > 0) $this->removeItemPermissionValues($removed);
			if ($mysql) $this->db->commit();
							
			return TRUE;
		}

		private function addItemPermissionValues($list) {
			$query = "INSERT INTO ".$this->db->table("item_permission")." (item_id, user_id, permission) VALUES ";
			$first = TRUE;
			
			foreach($list as $item) {
				$permission = $this->db->string(strtolower($item["permission"]));
				$id = $this->db->string($item["item_id"]);
				$user = '0';
				if ($item["user_id"] != NULL) $user = $this->db->string($item["user_id"]);
				
				if (!$first) $query .= ',';
				$query .= sprintf(" ('%s', '%s', '%s')", $id, $user, $permission);
				$first = FALSE;
			}
			
			$this->db->update($query);							
			return TRUE;
		}
		
		public function addItemPermission($id, $permission, $userId) {
			$permission = $this->db->string(strtolower($permission));
			$id = $this->db->string($id);
			$user = $this->db->string($userId);

			$query = sprintf("INSERT INTO ".$this->db->table("item_permission")." (item_id, user_id, permission) VALUES ('%s', '%s', '%s')", $id, $user, $permission);
			$this->db->update($query);							
			return TRUE;
		}
	
		private function updateItemPermissionValues($list) {
			foreach($list as $item) {
				$permission = $this->db->string(strtolower($item["permission"]));
				$id = $this->db->string($item["item_id"]);
				$user = '0';
				if ($item["user_id"] != NULL) $user = $this->db->string($item["user_id"]);
			
				$this->db->update(sprintf("UPDATE ".$this->db->table("item_permission")." SET permission='%s' WHERE item_id='%s' and user_id='%s'", $permission, $id, $user));
			}
							
			return TRUE;
		}
	
		private function removeItemPermissionValues($list) {
			foreach($list as $item) {
				$id = $this->db->string($item["item_id"]);
				$user = "user_id = '0'";
				if ($item["user_id"] != NULL) $user = sprintf("user_id = '%s'", $this->db->string($item["user_id"]));
				$this->db->update(sprintf("DELETE FROM ".$this->db->table("item_permission")." WHERE item_id='%s' AND %s", $id, $user));
			}
							
			return TRUE;
		}

		function removeItemPermissions($item) {
			if (!$item->isFile()) {
				$this->db->update(sprintf("DELETE FROM ".$this->db->table("item_permission")." WHERE item_id in (select id from ".$this->db->table("item_id")." where path like '%s%%')", str_replace("'", "\'", $item->location())));
			} else {
				$this->db->update(sprintf("DELETE FROM ".$this->db->table("item_permission")." WHERE item_id='%s'", $this->itemId($item)));
			}
			return TRUE;
		}*/

	}
?>