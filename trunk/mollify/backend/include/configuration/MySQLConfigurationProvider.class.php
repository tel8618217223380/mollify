<?php
	class MySQLConfigurationProvider extends ConfigurationProvider {
		private $db;
		
		public function __construct($settings) {
			global $DB_HOST, $DB_USER, $DB_PASSWORD, $DB_DATABASE;
			
			if (!isset($DB_USER) or !isset($DB_PASSWORD)) throw new ServiceException("INVALID_CONFIGURATION", "No database information defined");
			
			if (isset($DB_HOST)) $host = $DB_HOST;
			else $host = "localhost";
			
			if (isset($DB_DATABASE)) $database = $DB_DATABASE;
			else $database = "mollify";

			if (isset($DB_TABLE_PREFIX)) $tablePrefix = $DB_TABLE_PREFIX;
			else $tablePrefix = "";
			
			require_once("../mysql/Database.class.php");
			$this->db = new Database($host, $DB_USER, $DB_PASSWORD, $database, $tablePrefix);
			$this->db->connect();
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}

		public function getSupportedFeatures() {
			$features = array('description_update', 'configuration_update');
			if ($this->isAuthenticationRequired()) $features[] = 'permission_update';
			return $features;
		}
		
		public function checkProtocolVersion($version) {}
	
		public function findUser($username, $password) {
			$result = $this->db->query(sprintf("SELECT id, name FROM user WHERE name='%s' AND password='%s'", $this->db->string($username), $this->db->string($password)));
			$matches = $result->count();
			
			if ($matches === 0) {
				Logging::logError("No user found with name [".$username."], or password was invalid");
				return NULL;
			}
			
			if ($matches > 1) {
				Logging::logError("Duplicate user found with name [".$username."] and password");
				return FALSE;
			}
			
			return $result->firstRow();
		}
		
		public function getAllUsers() {
			return $this->db->query("SELECT id, name, permission_mode FROM user ORDER BY id ASC")->rows();
		}

		public function getUser($id) {
			return $this->db->query(sprintf("SELECT id, name FROM user WHERE id='%s'", $this->db->string($id)))->firstRow();
		}
		
		public function getDefaultPermission($userId = "") {
			$mode = strtoupper($this->db->query(sprintf("SELECT permission_mode FROM user WHERE id='%s'", $this->db->string($userId)))->value(0));
			$this->env->authentication()->assertPermissionValue($mode);
			return $mode;
		}
			
		public function getUserFolders($userId) {
			$rows = $this->db->query(sprintf("SELECT folder.id, user_folder.name, folder.name as folder_name, folder.path FROM user_folder, folder WHERE user_id='%s' AND folder.id = user_folder.folder_id", $this->db->string($user_id)))->rows();

			$roots = array();
			foreach ($rows as $row) {
				if ($row["name"] != NULL) $name = $row["name"];
				else $name = $row["folder_name"];
			
				$roots[$row["id"]] = array("id" => $row["id"], "name" => $name, "path" => $row["path"]);
			}
			return $roots;
		}
		
		function getItemDescription($item) {
			return NULL;
		}
				
		function setItemDescription($item, $description) {
			return FALSE;
		}
	
		function removeItemDescription($item) {
			return FALSE;
		}
		
		function moveItemDescription($from, $to) {
			return FALSE;
		}
					
		function getItemPermission($item, $userId) {
			return FALSE;
		}
	
		function getItemPermissions($item) {
			return FALSE;
		}
			
		function updateItemPermissions($updates) {
			return FALSE;
		}

		function removeItemPermissions($item) {
			return FALSE;
		}
		
		function moveItemPermissions($from, $to) {
			return FALSE;
		}

	}
?>