<?php
	class Filesystem {
		private $env;
		private $allowedUploadTypes;
		
		function __construct($env) {
			require_once("FilesystemItem.class.php");
			
			$this->env = $env;
			$this->allowedUploadTypes = $env->settings()->getSetting('allowed_file_upload_types');
		}
		
		public function initialize($request) {}
		
		public function onSessionStarted() {
			$this->env->session()->param('roots', $this->validateRootDirectories());
		}

		public function getId($rootId, $path = "") {
			if (strlen($path) > 0)
				$path = substr($path, strlen($this->getRootPath($rootId)));
			return base64_encode($rootId.':'.DIRECTORY_SEPARATOR.$path);
		}
		
		public function getItemFromPath($rootId, $path) {
			$isFile = (strcasecmp(substr($path, -1), DIRECTORY_SEPARATOR) != 0);
			
			if ($isFile) return new File($this->getId($rootId, $path), $rootId, $path);
			return new Folder(self::getId($rootId, $path), $rootId, $path);
		}
		
		public function getItemFromId($id) {
			$parts = explode(":".DIRECTORY_SEPARATOR, base64_decode($id));
			$rootId = $parts[0];
			$filePath = $parts[1];
			$rootPath = $this->getRootPath($rootId);
			
			$path = $rootPath;
			$isFile = FALSE;
			
			if (strlen($filePath) > 0) {
				if (strpos("..", $filePath) != FALSE)
					throw new ServiceException("INVALID_REQUEST", "Illegal path requested: ".$filePath);
					
				$path = self::joinPath($path, $filePath);
			}
			
			return $this->getItemFromPath($rootId, $path);
		}
		
		public function getRootDirectories() {
			return $this->env->session()->param('roots');
		}
		
		private function getRootPath($rootId) {
			$roots = $this->getRootDirectories();
			if (!array_key_exists($rootId, $roots))
				throw new ServiceException("INVALID_CONFIGURATION", "Invalid root directory requested: ".$rootId);
			return self::dirPath($roots[$rootId]["path"]);
		}
				
		private function validateRootDirectories() {
			$roots = $this->env->configuration()->getUserRootDirectories($this->env->authentication()->getUserId());
			
			foreach($roots as $id => $root) {
				if (!isset($root["name"])) {
					$this->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Root directory definition does not have a name (".$id.")");
				}
				
				if (!file_exists($root["path"])) {
					$this->session->reset();
					throw new ServiceException("INVALID_CONFIGURATION", "Root directory does not exist (".$id.")");
				}
			}
			
			return $roots;
		}
		
		public function getSessionInfo() {
			$result = array();
			
			$result['filesystem'] = array(
				"max_upload_file_size" => Util::inBytes(ini_get("upload_max_filesize")),
				"max_upload_total_size" => Util::inBytes(ini_get("post_max_size")),
				"allowed_file_upload_types" => $this->getAllowedFileUploadTypes()
			);
			
			$result["roots"] = array();
			
			foreach($this->getRootDirectories() as $id => $root) {
				$result["roots"][] = array(
					"id" => FileSystem::getId($id),
					"name" => $root["name"]
				);
			}

			return $result;
		}
		
		private function getAllowedFileUploadTypes() {
			$types = array();
			foreach ($this->allowedUploadTypes as $type) {
				$pos = strrpos($type, ".");
				if ($pos === FALSE) $types[] = $type;
				else $types[] = substr($type, $pos+1);
			}
			return $types;
		}
		
		public function log() {
			Logging::logDebug("FILESYSTEM: allowed_file_upload_types=".Util::array2str($this->allowedUploadTypes));
		}
		
		static function joinPath($item1, $item2) {
			return self::dirPath($item1).$item2;
		}
		
		static function dirPath($path) {
			return rtrim($path, DIRECTORY_SEPARATOR).DIRECTORY_SEPARATOR;
		}
		
		static function basename($path) {
			$name = strrchr(rtrim($path, DIRECTORY_SEPARATOR), DIRECTORY_SEPARATOR);
			if (!$name) return "";
			return substr($name, 1);
		}
	}
?>