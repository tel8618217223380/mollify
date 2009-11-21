<?php
	class FileConfigurationProvider extends ConfigurationProvider {
		function getSupportedFeatures() {
			$features = array('description_update');
			if ($this->isAuthenticationRequired()) $features[] = 'permission_update';
			return $features;
		}
		
	}
?>