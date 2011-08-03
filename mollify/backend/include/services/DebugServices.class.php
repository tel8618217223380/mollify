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

	class DebugServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			return TRUE;
		}
		
		protected function isAuthenticationRequired() {
			return TRUE;
		}
		
		protected function isAdminRequired() {
			return TRUE;
		}

		public function processGet() {
			if (!$this->env->session()->hasParam("debug_info"))
				$this->response()->html("<html><body><h1>Mollify Debug</h1><p>No debug info available</p></body></html>");
			else
				$this->response()->html($this->getDebugHtml());
		}
		
		private function getDebugHtml() {
			$html = "<html><body><h1>Mollify Debug</h1><p>";
			
			foreach($this->env->session()->param("debug_info") as $d)
				$html .= "<p><code>".htmlspecialchars(Util::toString($d), , ENT_QUOTES)."</code></p>";
			
			$html .= "</body></html>";
			return $html;
		}
		
		public function __toString() {
			return "DebugServices";
		}
	}
?>