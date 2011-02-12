<?php
	class QuicktimeViewer extends EmbeddedContentViewer {
		protected function getEmbeddedSize() {
			return array("640", "480");
		}
		
		protected function getResizedElementId() {
			return "quicktime-player";
		}
		
		protected function getHtml($item, $full) {
			return '<embed id="quicktime-player" width="580" height="380" src="'.$this->getContentUrl($item).'" autoplay="true" controller="true" pluginspace="/quicktime/download/">';
		}		
	}
?>