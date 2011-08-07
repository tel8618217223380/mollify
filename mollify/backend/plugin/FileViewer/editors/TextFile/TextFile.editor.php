<?php
	class TextFileEditor extends EmbeddedEditor {
		protected function getHtml($item) {
			$html = '<textarea id="text-editor" style="width:100%;height:100%">';
			
			// read file			
			$stream = $item->read();
			while (!feof($stream))
				$html .= fread($stream, 1024);
			fclose($stream);
			
			return $html.'</textarea>';
		}
		
		protected function getEmbeddedSize() {
			return array("640", "480");
		}
	}
?>