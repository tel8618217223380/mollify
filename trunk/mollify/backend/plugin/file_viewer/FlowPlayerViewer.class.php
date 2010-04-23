<?php
	class FlowPlayerViewer extends ViewerBase {
		public function getInfo($item) {
			return array(
				"embedded" => $this->getDataUrl($item, "embedded"),
				"full" => $this->getDataUrl($item, "view", TRUE)
			);
		}
		
		public function processDataRequest($item, $path) {
			if (count($path) != 1) throw $this->invalidRequestException();
			
			if ($path[0] === 'view')
				$this->processViewRequest($item);
			else if ($path[0] === 'embedded')
				$this->processEmbeddedViewRequest($item);
			else
				throw $this->invalidRequestException();
		}
		
		private function processEmbeddedViewRequest($item) {
			$html = '<iframe id="flow-player-viewer" src="'.$this->getDataUrl($item, "view", TRUE).'" style="border: none;"></iframe>';
			
			$this->response()->success(array(
				"html" => $html,
				"resized_element_id" => "flow-player-viewer",
				"size" => "600;400"
			));
		}
		
		private function processViewRequest($item) {
			$resourceUrl = $this->getResourceUrl("flowplayer");
			
			$head = '<script type="text/javascript" src="'.$resourceUrl.'flowplayer-3.1.4.min.js"></script>';					
			$html =
				'<a href="'.$this->getContentUrl($item).'" style="display:block;width:580px;height:380px" id="player"></a>'.
				'<script>flowplayer("player", "'.$resourceUrl.'flowplayer-3.1.5.swf");</script>';

			$this->response()->html("<html><head><title>".$item->name()."</title>".$head."</head><body>".$html."</body></html>");
		}
	}
?>