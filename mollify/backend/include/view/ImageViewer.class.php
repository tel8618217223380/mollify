<?php

	/**
	 * Copyright (c) 2008- Samuli JŠrvelŠ
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class ImageViewer {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function getInfo($item) {
			return array(
				"embedded" => $this->env->getServiceUrl("view", array($item->id(), "embedded"), FALSE),
				"full" => $this->env->getServiceUrl("view", array($item->id(), "full"), TRUE)
			);
		}
		
		public function getView($item, $full) {
			$html = '<img src="'.$this->env->getDataUrl($item).'">';
			if ($full) return "<html>".$html."</html>";
			return array(
				"html" => $html
			);
		}
	}
?>