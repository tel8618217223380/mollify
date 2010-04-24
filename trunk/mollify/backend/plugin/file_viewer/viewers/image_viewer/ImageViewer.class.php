<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class ImageViewer extends ViewerBase {		
		public function getInfo($item) {
			return array(
				"embedded" => $this->getDataUrl($item, "embedded"),
				"full" => $this->getDataUrl($item, "full", TRUE)
			);
		}
		
		public function processDataRequest($item, $path) {
			if (count($path) != 1) throw $this->invalidRequestException();
			$html = '<img src="'.$this->getContentUrl($item).'">';

			if ($path[0] === 'full')
				$this->response()->html("<html><head><title>".$item->name()."</title></head><body>".$html."</body></html>");
			else if ($path[0] === 'embedded')
				$this->response()->success(array("html" => $html));
			else
				throw $this->invalidRequestException();
		}
	}
?>