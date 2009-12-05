<?php
	class FilePermissionDao {
		private $uacName;
		
		public function __construct($uacName) {
			$this->$uacName = $uacName;
		}
		
		public function getItemPermission($item, $userId) {
			$permissions = $this->readPermissionsFromFile($this->getUacFilename($item));
			
			$match = FALSE;
			$id = $this->getPermissionId($item);
			
			if (array_key_exists($id, $permissions)) $match = $permissions[$id];
			if (!$match and $item->isFile() and array_key_exists(".", $permissions)) $match = $permissions["."];
			if (!$match) return FALSE;
			
			return $this->getEffectivePermission($match, $userId);
		}
		
		public function getItemPermissions($item) {
			$permissions = $this->readPermissionsFromFile($this->getUacFilename($item));
			if ($permissions === FALSE) return FALSE;
			$id = $this->getPermissionId($item);
			
			$result = array();
			if (array_key_exists($id, $permissions)) {
				foreach ($permissions[$id] as $id => $permission) {
					if ($id === "*") $permission_id = "0";
					else $permission_id = "".$id;
					
					$result[] = array("item_id" => $item["id"], "user_id" => $permission_id, "permission" => $permission);
				}
			}
			return $result;
		}

		public function moveItemPermissions($from, $to, $recursively = FALSE) {
			if ($recursively) return TRUE;	// permission file is moved along the folder
			
			$fromPath = dirname($from->path());
			$fromName = Filesystem::basename($from->path());
			$fromId = $this->getPermissionId($from);
	
			$toPath = dirname($to->path());
			$toName = Filesystem::basename($to->path());
			$toId = $this->getPermissionId($to);
	
			$fromUac = $this->getUacFilename($from);
			$fromPermissions = $this->readPermissionsFromFile($fromUac);
			if (!$fromPermissions or !array_key_exists($fromId, $fromPermissions)) return TRUE;
			
			$itemPermissions = $fromPermissions[$fromId];
			unset($fromPermissions[$fromId]);
			
			if ($toPath === $fromPath) $fromPermissions[$toId] = $itemPermissions;
			if (!$this->writePermissionsToFile($fromUac, $fromPermissions)) return FALSE;
			
			if ($toPath != $fromPath) {
				$toUac = $this->getUacFilename($to);
				$toPermissions = $this->readPermissionsFromFile($toUac);
				if (!$toPermissions) $toPermissions = array();
				
				$toPermissions[$toId] = $itemPermissions;
				return $this->writePermissionsToFile($toUac, $toPermissions);
			}
			return TRUE;
		}
		
		private function getEffectivePermission($permissions, $userId) {
			if ($userId != "" and isset($permissions[$userId])) return $permissions[$userId];
			if (isset($permissions["*"])) return $permissions["*"];
			return FALSE;
		}
		
		private function getPermissionId($item) {
			if (!$item->isFile()) return ".";
			return Filesystem::basename($item->path());
		}
		
		private function getUacFilename($item) {
			$path = $item->path();
			if (!is_dir($path))
				$path = dirname($path);
	
			return $path.DIRECTORY_SEPARATOR.$this->uacName;
		}
		
		private function readPermissionsFromFile($uacFile) {
			$result = array();
			if (!file_exists($uacFile)) return $result;
		
			$handle = @fopen($uacFile, "r");
			if (!$handle) return $result;
			
			$i = 0;
		    while (!feof($handle)) {
		        $line = fgets($handle, 4096);
				$i = $i + 1;
				
				$parts = explode(chr(9), $line);
				if (count($parts) < 2) continue;
				
				$file = trim($parts[0], '" ');
				$data = trim($parts[count($parts) - 1]);
				
				$permissions = $this->parsePermissionString($data);
				if (!$permissions) {
					Logging.logError("Invalid file permission definition in file [".$uacFile."] at line ".$i);
					continue;
				}
							
				$result[$file] = $permissions;			
		    }
		    fclose($handle);
			
			return $result;
		}
		
		private function writePermissionsToFile($uacFile, $permissionTable) {
			if (file_exists($uacFile)) {
				if (!is_writable($uacFile)) {
					Logging::logError("Permission file (".$uacFile.") is not writable");
					return FALSE;
				}
			} else {
				$dir = dirname($uacFile);
				if (!is_writable($dir)) {
					Logging::logError("Directory for permission file (".$dir.") is not writable");
					return FALSE;
				}
			}
		
			$handle = @fopen($uacFile, "w");
			if (!$handle) {
				Logging::logError("Could not open permission file for writing: ".$dir);
				return FALSE;
			}
			
			foreach($permissionTable as $file => $permissions) {
				$value = $this->formatPermissionString($permissions);
				fwrite($handle, sprintf("\"%s\"\t%s\n", $file, $value));
			}
	
			fclose($handle);
			
			return TRUE;
		}
		
		private function parsePermissionString($string) {
			$result = array();
			if (strlen($string) < 1) return $result;
			
			$parts = explode(',', $string);
			if (count($parts) < 1) return $result;
			
			foreach($parts as $part) {
				$valueParts = explode('=', $part);
				if (count($valueParts) != 2) return FALSE;
	
				$id = trim($valueParts[0]);
				$permission = strtolower(trim($valueParts[1]));
				if (strlen($id) == 0 or strlen($permission) == 0) return FALSE;
	
				$result[$id] = $permission;
			}
			return $result;
		}
		
		private function formatPermissionString($permissions) {
			$result = "";
			if (count($permissions) < 1) return $result;
			
			$first = TRUE;
			foreach($permissions as $id => $permission) {
				if (!$first) $result .= ',';
				$result .= sprintf("%s=%s", $id, strtolower($permission));
				$first = FALSE;
			}
			return $result;
		}
	}
?>