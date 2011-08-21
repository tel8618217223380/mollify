<?php
	class CKEditorEditor extends FullEditor {
		protected function getHead($item) {
			$resourceUrl = $this->getResourceUrl();
			return '
				<script type="text/javascript" src="'.$resourceUrl.'ckeditor.js"></script>
				<script type="text/javascript" src="'.$resourceUrl.'adapters/jquery.js"></script>
				<script type="text/javascript" src="'.$resourceUrl.'mollify.js"></script>
			';
		}
		
		protected function getHtml($item) {
			$html = '<textarea id="ckeditor" style="width:100%;height:100%">';
			
			// read file			
			$stream = $item->read();
			while (!feof($stream))
				$html .= htmlspecialchars(fread($stream, 1024));
			fclose($stream);
			
			return $html.'</textarea>';
		}

		protected function getDataJs() {
			return "return $('#ckeditor').val();";
		}		
	}
?>