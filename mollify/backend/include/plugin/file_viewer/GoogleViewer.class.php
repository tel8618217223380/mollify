<?php

	/**
	 * Copyright (c) 2008- Samuli Järvelä
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class GoogleViewer extends ViewerBase {		
		public function getInfo($item) {
			return array(
				"embedded" => $this->env->getServiceUrl("view", array($item->id(), "embedded"), FALSE),
				"full" => $this->getGoogleViewerUrl($item, TRUE)
			);
		}
		
		public function getView($item, $full) {
			$html = '<iframe id="google-viewer" src="'.$this->getGoogleViewerUrl($item).'" style="border: none;"></iframe>';
			return array(
				"html" => $html,
				"resized_element_id" => "google-viewer",
				"size" => "600;400"
			);
		}
		
		private function getGoogleViewerUrl($item, $full = FALSE) {
			$url = $this->env->getDataUrl($item, TRUE);
			return 'http://docs.google.com/viewer?url='.urlencode($url).($full ? '' : '&embedded=true');
		}
	}
?>