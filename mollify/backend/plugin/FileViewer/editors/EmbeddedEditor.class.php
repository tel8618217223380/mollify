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

	abstract class EmbeddedEditor extends EditorBase {
		public function getInfo($item) {
			return array(
				"embedded" => $this->getUrl($item, "embedded", TRUE),
				"full" => $this->getUrl($item, "full", TRUE)
			);
		}
		
		public function processRequest($item, $path) {
			if ($path[0] === 'embedded')
				$this->processEmbeddedEditorRequest($item);
			else if ($path[0] === 'full')
				$this->processFullEditorRequest($item);
			else
				throw $this->invalidRequestException();
		}

		protected function processEmbeddedEditorRequest($item) {
			$html = '<html>
				<head>
					<title>'.$item->name().'</title>
					<script type="text/javascript" src="'.$this->getResourceUrl().'/jquery-1.4.2.min.js"></script>
					<script>
						function onEditorSave() {
							var data = getSaveContent();
							$.ajax({
								type: "POST",
								url: "'.$this->getServiceUrl("filesystem", array($item->publicId(), 
"content"), TRUE).'",
								data: data,
								dataType: "json",
								success: function(result) {
								},
								error: function (xhr, desc, exc) {
								}
							});
						}
					</script>
				</head>
				<body>'.$this->getHtml($item).'
				</body>
			</html>';
			
			$this->response()->html($html);
		}
		
		protected abstract function getHtml($item);
		
	}
?>