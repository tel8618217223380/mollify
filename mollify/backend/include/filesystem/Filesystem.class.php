<?php
	class Filesystem {
		private $env;
		private $allowedUploadTypes;
		
		function __construct($env) {
			$this->env = $env;
			$this->allowedUploadTypes = $env->settings()->getSetting('allowed_file_upload_types');
		}
		
		public function initialize($request) {}
		
		public function onSessionStarted() {
			$this->env->session()->param('roots', $this->validateRootDirectories());
		}

		public static function getId($rootId, $path = "") {
			if (strlen($path) > 0) {
				$rootPath = getRootPath($rootId);
				$path = substr($path, strlen($rootPath));
			}
			return base64_encode($rootId.':'.DIRECTORY_SEPARATOR.$path);
		}
		
		public function getRootDirectories() {
			return $this->env->session()->param('roots');
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
	}
?>