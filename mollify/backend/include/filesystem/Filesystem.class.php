<?php
	class Filesystem {
		private $allowedUploadTypes;
		
		function __construct($settings) {
			$this->allowedUploadTypes = $settings->getSetting('allowed_file_upload_types');
		}
		
		public function initialize($request) {}
		
		public function getId($root_id, $path = "") {
			if (strlen($path) > 0) {
				$root_path = get_root_path($root_id);
				$path = substr($path, strlen($root_path));
			}
			return base64_encode($root_id.':'.DIRECTORY_SEPARATOR.$path);
		}
		
		public function getSessionInfo() {
			$result = array();
			
			$result['filesystem'] = array(
				"max_upload_file_size" => Util::inBytes(ini_get("upload_max_filesize")),
				"max_upload_total_size" => Util::inBytes(ini_get("post_max_size")),
				"allowed_file_upload_types" => $this->getAllowedFileUploadTypes()
			);
			
//			$result["roots"] = get_root_directory_info();
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