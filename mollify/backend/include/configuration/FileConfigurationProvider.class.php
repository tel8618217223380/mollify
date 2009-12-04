<?php
	class FileConfigurationProvider extends ConfigurationProvider {
		function initialize($request, $env) {
			$env->events()->register(FileSystem::EVENT_TYPE_FILE, $this);
		}
		
		function onEvent($e) {
			Logging::logDebug("FileConfigurationProvider Event: ".$e->subType()." (".$e->item()->id().")");
			
			if ($e->subType() === FileEvent::RENAME) {
//			if ($_SESSION["features"]["description_update"])
//				move_item_description($file, get_fileitem($file["root"], $new));
//			if ($_SESSION["features"]["permission_update"])
//				move_item_permissions($file, get_fileitem($file["root"], $new));
			}
		}
		
		function getSupportedFeatures() {
			$features = array('description_update');
			if ($this->isAuthenticationRequired()) $features[] = 'permission_update';
			return $features;
		}
		
		function findUser($username, $password) {
			global $USERS, $PASSWORDS_HASHED;
				
			foreach($USERS as $id => $user) {
				if ($user["name"] != $username)
					continue;
					
				$pw = $user["password"];
				if (!isset($PASSWORDS_HASHED) or $PASSWORDS_HASHED != TRUE) {
					$pw = md5($pw);
				}
	
				if ($pw != $password) {
					Logging::logError("Invalid password for user [".$user["name"]."]");
					return NULL;
				}
				
				return array("id" => $id, "name" => $user["name"]);
			}
			
			Logging::logError("No user found with name [".$username."]");
			return NULL;
		}
		
		function getAllUsers() {
			global $USERS;
			$result = array();
			foreach($USERS as $id => $user)
				$result[] = array("id" => "".$id, "name" => $user["name"], "permission_mode" => $user["file_permission_mode"]);
			return $result;
		}

		function getUser($id) {
			if ($id === "") return FALSE;
			global $USERS;
			return $USERS[$id];
		}
		
		function getDefaultPermissionMode($userId = "") {
			global $USERS, $FILE_PERMISSION_MODE;
			
			if ($userId === "") {
				if (!isset($FILE_PERMISSION_MODE)) return Authentication::$PERMISSION_VALUE_READONLY;
				$mode = strtoupper($FILE_PERMISSION_MODE);
			} else {
				if (!isset($USERS[$userId]["file_permission_mode"])) return Authentication::$PERMISSION_VALUE_READONLY;
				$mode = strtoupper($USERS[$userId]["file_permission_mode"]);
			}
	
			if ($mode != Authentication::$PERMISSION_VALUE_ADMIN and $mode != Authentication::$PERMISSION_VALUE_READWRITE and $mode != Authentication::$PERMISSION_VALUE_READONLY) {
				if ($user_id === "") throw new ServiceException("INVALID_CONFIGURATION", "Invalid file permission mode [".$mode."]");
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid file permission mode ".$mode." for user [".$user_id."]");
			}
			return $mode;
		}
	
		function isAuthenticationRequired() {
			global $USERS;
			return ($USERS != FALSE and count($USERS) > 0);
		}
		
		public function getUserRootDirectories($userId) {
			global $USERS, $PUBLISHED_DIRECTORIES;
	
			if (!isset($PUBLISHED_DIRECTORIES)) return array();
			
			if (count($USERS) === 0) {
				return $PUBLISHED_DIRECTORIES;
			} else {
				if (!array_key_exists($userId, $PUBLISHED_DIRECTORIES)) throw new ServiceException("INVALID_CONFIGURATION", "Missing root directory configuration for user ".$userId);
				return $PUBLISHED_DIRECTORIES[$userId];
			}
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
	
			return $path.DIRECTORY_SEPARATOR."mollify.uac";
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
	}
?>