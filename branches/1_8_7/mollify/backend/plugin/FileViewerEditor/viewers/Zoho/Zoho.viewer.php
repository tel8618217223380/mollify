<?php
	/**
	 * Zoho viewer uses web service provided by ZOHO Corp.
	 *
	 * Visit http://viewer.zoho.com/home.do for terms of use or other information about the service.
	 *
	 */
	 
	 class ZohoViewer extends ViewerBase {		
		public function getInfo($item) {
			return array(
				"embedded" => $this->getDataUrl($item, "embedded"),
				"full" => $this->getViewerUrl($item, TRUE)
			);
		}
		
		public function processDataRequest($item, $path) {
			if (count($path) != 1 and $path[0] != 'embedded') throw $this->invalidRequestException();
			
			$html = '<iframe id="zoho-viewer" src="'.$this->getViewerUrl($item, FALSE).'" style="border: none;"></iframe>';			
			$this->response()->success(array(
				"html" => $html,
				"resized_element_id" => "zoho-viewer",
				"size" => "600;400"
			));
		}
		
		private function getViewerUrl($item, $full = FALSE) {
			$url = $this->getContentUrl($item, TRUE)."&name=".$item->name()."&title=".$item->name();
			return 'http://viewer.zoho.com/api/urlview.do?url='.urlencode($url)."&cache=false";
		}
	}
?>