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
		private $env;
		private $db;

		public function __construct($env) {
			$this->env = $env;
			$this->db = $env->db();
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
				$subcategoryQuery = sprintf("(case when user_id = '%s' then 1 when user_id = '0' then 3 else 2 end)", $userId);
			}

			// item permissions
			$query = sprintf("SELECT value, user_id, (case when subject is null then 2 else 1 end) as category, %s as subcategory FROM ".$table." WHERE name='%s' AND (subject is null OR subject = '%s') AND %s", $subcategoryQuery, $name, $id, $userQuery);
					
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

		public function getGenericPermissions($name = NULL, $userId) {
			$criteria = ($name != NULL ? "name=".$this->db->string($name, TRUE) : "1=1");
			$criteria .= " AND subject is null";
			$criteria .= ($userId != NULL ? " AND user.id=".$this->db->string($userId) : "");
			return $this->doGetPermissions($criteria);
		}
		
		public function getPermissions($name = NULL, $subject = NULL, $userId) {
			$criteria = ($name != NULL ? "name=".$this->db->string($name, TRUE) : "1=1");
			$criteria .= ($subject != NULL ? " AND subject=".$this->db->string($subject, TRUE) : "");
			$criteria .= ($userId != NULL ? " AND user.id=".$this->db->string($userId) : "");
			return $this->doGetPermissions($criteria);
		}
		
		private function doGetPermissions($criteria) {
			$rows = $this->db->query("SELECT user.id as user_id, user.is_group as is_group, permission.value as value, permission.name as name, permission.subject as subject FROM ".$this->db->table("permission")." as permission LEFT OUTER JOIN ".$this->db->table("user")." as user ON user.id = permission.user_id WHERE ".$criteria)->rows();
			
			$list = array();
			foreach ($rows as $row) {
				if (!isset($row["user_id"]))
					$list[] = array("name" => $row["name"], "subject" => $row["subject"], "user_id" => '0', "is_group" => 0, "value" => $row["value"]);
				else
					$list[] = array("name" => $row["name"], "subject" => $row["subject"], "user_id" => $row["user_id"], "is_group" => $row["is_group"], "value" => $row["value"]);
			}
			return $list;
		}
			
		public function updatePermissions($updates) {
			$new = $updates['new'];
			$modified = $updates['modified'];
			$removed = $updates['removed'];
			
			$this->db->startTransaction();
			if (count($new) > 0) $this->addPermissionValues($new);
			if (count($modified) > 0) $this->updatePermissionValues($modified);
			if (count($removed) > 0) $this->removePermissionValues($removed);
			$this->db->commit();
							
			return TRUE;
		}

		private function addPermissionValues($list) {
			$query = "INSERT INTO ".$this->db->table("permission")." (name, subject, user_id, value) VALUES ";
			$first = TRUE;
			
			foreach($list as $item) {
				$name = $this->db->string(strtolower($item["name"]), TRUE);
				$value = $this->db->string(strtolower($item["value"]), TRUE);
				$subject = isset($item["subject"]) ? $this->db->string($item["subject"], TRUE) : "NULL";
				$user = "'0'";
				if ($item["user_id"] != NULL) $user = $this->db->string($item["user_id"], TRUE);
				
				if (!$first) $query .= ',';
				$query .= sprintf(" (%s, %s, %s, %s)", $name, $subject, $user, $value);
				$first = FALSE;
			}
			
			$this->db->update($query);							
			return TRUE;
		}
	
		private function updatePermissionValues($list) {
			foreach($list as $item) {
				$name = $this->db->string($item["name"], TRUE);
				$value = $this->db->string(strtolower($item["value"]), TRUE);
				$subject = $this->db->string($item["subject"], TRUE);
				$user = '0';
				if ($item["user_id"] != NULL) $user = $this->db->string($item["user_id"]);
			
				$this->db->update(sprintf("UPDATE ".$this->db->table("permission")." SET value=%s WHERE name=%s AND subject=%s AND user_id=%s", $value, $name, $subject, $user));
			}
							
			return TRUE;
		}
	
		private function removePermissionValues($list) {
			foreach($list as $item) {
				$name = $this->db->string($item["name"], TRUE);
				$subject = $this->db->string($item["subject"], TRUE);
				$user = "0";
				if ($item["user_id"] != NULL) $user = $this->db->string($item["user_id"]);
				$this->db->update(sprintf("DELETE FROM ".$this->db->table("permission")." WHERE name = %s AND subject=%s AND user_id = %s", $name, $subject, $user));
			}
							
			return TRUE;
		}

		public function removeFilesystemPermissions($name, $item) {
			$nameCriteria = ($name != NULL ? "name=".$this->db->string($name, TRUE)." AND " : "");
			
			if (!$item->isFile()) {
				$this->db->update(sprintf("DELETE FROM ".$this->db->table("permission")." WHERE ".$nameCriteria."subject in (select id from ".$this->db->table("item_id")." where path like '%s%%')", str_replace("'", "\'", $item->location())));
			} else {
				$this->db->update(sprintf("DELETE FROM ".$this->db->table("permission")." WHERE ".$nameCriteria."subject=%s", $this->db->string($item->id(), TRUE)));
			}
			return TRUE;
		}

		public function addPermission($name, $subject, $userId, $value = "1") {
			$query = sprintf("INSERT INTO ".$this->db->table("permission")." (name, subject, user_id, permission) VALUES (%s, %s, %s)", $this->db->string($name, TRUE), $this->db->string($subject, TRUE), $this->db->string($userId, TRUE), $this->db->string(strtolower($value), TRUE));
			$this->db->update($query);							
			return TRUE;
		}
		
		public function processQuery($data) {
			$criteria = ((isset($data["name"]) and $data["name"] != NULL) ? "name=".$this->db->string($data["name"], TRUE) : "1=1");
			
			if (isset($data["subject_type"]) and $data["subject_type"] != NULL and $data["subject_type"] != 'any') {
				if ($data["subject_type"] == "none")
					$criteria .= " AND subject is null";
					
				if (($data["subject_type"] == "filesystem_item" or $data["subject_type"] == "filesystem_child") and isset($data["subject_value"]) and $data["subject_value"] != NULL) {
					if ($data["subject_type"] == "filesystem_item")
						$criteria .= " AND subject = ".$this->db->string($data["subject_value"], TRUE);
					else {
						$item = $this->env->filesystem()->item($data["subject_value"]);
						$location = str_replace("'", "\'", $item->location());
						$criteria .= sprintf(" AND subject in (select id from ".$this->db->table("item_id")." where path like '%s%%')", $location);
						
						//TODO get items
					}
				}
			}
			
			$criteria .= ((isset($data["user_id"]) and $data["user_id"] != NULL) ? " AND user_id=".$this->db->string($data["user_id"]) : "");
			
			$count = $this->db->query("select count(name) FROM ".$this->db->table("permission")." WHERE ".$criteria)->value(0);
			$rows = isset($data["count"]) ? $data["count"] : 50;
			$start = isset($data["start"]) ? $data["start"] : 0;
			
			$rows = $this->db->query("SELECT user.id as user_id, user.is_group as is_group, permission.value as value, permission.name as name, permission.subject as subject FROM ".$this->db->table("permission")." as permission LEFT OUTER JOIN ".$this->db->table("user")." as user ON user.id = permission.user_id WHERE ".$criteria." limit ".$rows." offset ".$start)->rows();
			
			$list = array();
			foreach ($rows as $row) {
				if (!isset($row["user_id"]))
					$list[] = array("name" => $row["name"], "subject" => $row["subject"], "user_id" => '0', "is_group" => 0, "value" => $row["value"]);
				else
					$list[] = array("name" => $row["name"], "subject" => $row["subject"], "user_id" => $row["user_id"], "is_group" => $row["is_group"], "value" => $row["value"]);				
			}
			return array("start" => $start, "count" => count($rows), "total" => $count, "data" => $list);
		}
	}
?>