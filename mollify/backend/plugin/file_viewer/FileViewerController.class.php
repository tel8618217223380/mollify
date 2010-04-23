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

	class FileViewerController {	
		private $previewers = array();
		private $viewers = array();
		
		private $viewEnabled;
		private $previewEnabled;
		
		public function __construct($serviceEnvironment, $view, $preview) {
			$this->env = $serviceEnvironment;
			$this->viewEnabled = $view;
			$this->previewEnabled = $preview;
			
			if ($this->viewEnabled) {
				$this->registerViewer(array("gif", "png", "jpg"), "ImageViewer");
				if ($this->isGoogleViewerEnabled())
					$this->registerViewer(array("pdf", "doc", "xls"), "GoogleViewer");
				
				$customViewers = $this->getSetting(TRUE, "custom-viewers");
				if ($customViewers != NULL and is_array($customViewers)) {
					foreach($customViewers as $t => $list)
						$this->registerViewer($list, $t);
				}
			}
			if ($this->previewEnabled)
				$this->registerPreviewer(array("gif", "png", "jpg"), "ImagePreviewer");
		}

		private function registerPreviewer($types, $cls) {
			foreach($types as $t)
				$this->previewers[$t] = $cls;
		}
				
		private function registerViewer($types, $cls) {
			foreach($types as $t)
				$this->viewers[$t] = $cls;
		}
		
		public function getItemDetails($item) {
			if (!$item->isFile()) return FALSE;
			$type = strtolower($item->extension());
			
			$result = array();
			if ($this->previewEnabled and $this->isPreviewAllowed($type)) {
				$previewer = $this->getPreviewer($type);
				$result["preview"] = $previewer->getUrl($item);
			}
			if ($this->viewEnabled and $this->isViewAllowed($type)) {
				$viewer = $this->getViewer($type);
				$result["view"] = $viewer->getInfo($item);
			}
			return $result;
		}

		private function isPreviewAllowed($type) {
			if (!array_key_exists($type, $this->previewers)) return false;
			$types = $this->getSetting(FALSE, "types");
			if ($types == NULL or count($types) == 0) return TRUE;
			return in_array($type, $this->splitTypes($types));
		}
				
		private function isViewAllowed($type) {
			if (!array_key_exists($type, $this->viewers)) return false;
			$types = $this->getSetting(TRUE, "types");
			if ($types == NULL or count($types) == 0) return TRUE;
			return in_array($type, $this->splitTypes($types));
		}
		
		private function getPreviewer($type) {
			$previewer = $this->previewers[$type];
			require_once($previewer.".class.php");
			return new $previewer($this);
		}
				
		private function getViewer($type) {
			$viewer = $this->viewers[$type];
			require_once("ViewerBase.class.php");
			require_once($viewer.".class.php");
			return new $viewer($this);
		}
		
		public function getPreview($item) {
			$type = strtolower($item->extension());
			$previewer = $this->getPreviewer($type);
			return $previewer->getPreview($item);
		}
		
		public function processDataRequest($item, $path) {
			$type = strtolower($item->extension());
			$viewer = $this->getViewer($type);
			$viewer->processDataRequest($item, $path);
		}
		
		public function getContentUrl($item, $session = FALSE) {
			$url = $this->env->getServiceUrl("view", array($item->id(), "content"), TRUE);
			if ($session and $this->env->session()->isActive()) {
				$s = $this->env->session()->getSessionInfo();
				$url .= '/?session='.$s["session_id"];
			}
			return $url;
		}

		public function response() {
			return $this->env->response();
		}

		public function request() {
			return $this->env->request();
		}
		
		public function getViewServiceUrl($item, $p, $fullUrl = FALSE) {
			$path = array($item->id());
			if ($p != NULL) $path = array_merge($path, $p);
			return $this->getServiceUrl("view", $path, $fullUrl);
		}
				
		public function getServiceUrl($id, $path, $fullUrl = FALSE) {
			return $this->env->getServiceUrl($id, $path, $fullUrl);
		}

		public function getResourceUrl($id) {
			return $this->env->getPluginResourceUrl(FileViewer::ID, $id);
		}

		public function getCommonResourcesUrl() {
			return $this->env->getCommonResourcesUrl();
		}
		
		private function splitTypes($list) {
			$result = array();
			foreach (explode(",", $list) as $t)
				$result[] = strtolower(trim($t));
			return $result;
		}
		
		private function isGoogleViewerEnabled() {
			$s = $this->getSetting(TRUE, "use_google_viewer");
			return ($s === TRUE);
		}

		private function getSetting($view, $name) {
			$s = $this->env->settings()->setting($view ? "file_view_options" : "file_preview_options", TRUE);
			if (!isset($s[$name])) return NULL;
			return $s[$name];
		}

		public function __toString() {
			return "FileViewerController";
		}
	}
?>