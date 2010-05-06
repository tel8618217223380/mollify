<?php
	class QuicktimeViewer extends EmbeddedContentViewer {
		protected function getEmbeddedSize() {
			return array("450", "300");
		}
		
		protected function getResizedElementId() {
			return "movie";
		}
		
		protected function getHtml($item, $full) {
			return '<embed id="movie" width="400" height="240" src="'.$this->getContentUrl($item).'" autoplay="true" pluginspace="http://www.apple.com/quicktime/">';
		}		
	}
?>