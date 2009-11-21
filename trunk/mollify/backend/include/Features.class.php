<?php
	class Features {
		private $features = array(
			"file_upload" => FALSE,
			"folder_actions" => FALSE,
			"file_upload_progress" => FALSE,
			"zip_download" => FALSE,
			"change_password" => FALSE,
			"description_update" => FALSE,
			"permission_update" => FALSE,
			"configuration_update" => FALSE
		);
		
		private static $featuresControlledByConfigurationProvider = array("description_update", "permission_update", "configuration_update");
		
		function __construct($configurationProvider, $settings) {
			$configurationFeatures = $configurationProvider->getSupportedFeatures();
			
			foreach ($this->features as $f=>$k) {
				$enabled = FALSE;
				if (!in_array($f, self::$featuresControlledByConfigurationProvider) or in_array($f, $configurationFeatures)) {
					$enabled = $settings->getSetting("enable_".$f);
				}
				$this->features[$f] = $enabled;
			}
		}
		
		public function getFeatures() {
			return $this->features;
		}
		
		function log() {
			Logging::logDebug("FEATURES: ".Util::array2str($this->features));
		}

	}
?>