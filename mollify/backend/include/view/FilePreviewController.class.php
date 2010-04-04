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

	class FilePreviewController {
		static $defaultPreviewTypes = array("gif", "png", "jpg");
		static $defaultViewTypes = array("gif", "png", "jpg");
		
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
			}
			if ($this->previewEnabled) {
				$this->registerPreviewer(array("gif", "png", "jpg"), "ImagePreviewer");
			}
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
			$s = $this->env->settings()->setting("file_preview_options", TRUE);
			if (!isset($s["types"]) or count($s["types"]) == 0) return TRUE;
			return in_array($type, $this->getPreviewTypes());
		}
				
		private function isViewAllowed($type) {
			if (!array_key_exists($type, $this->viewers)) return false;
			$s = $this->env->settings()->setting("file_view_options", TRUE);
			if (!isset($s["types"]) or count($s["types"]) == 0) return TRUE;
			return in_array($type, $this->getViewTypes());
		}

		private function getPreviewer($type) {
			$previewer = $this->previewers[$type];
			require_once($previewer.".class.php");
			return new $previewer($this);
		}
				
		private function getViewer($type) {
			$viewer = $this->viewers[$type];
			require_once($viewer.".class.php");
			return new $viewer($this);
		}
		
		public function getPreview($item) {
			$type = strtolower($item->extension());
			$previewer = $this->getPreviewer($type);
			return $previewer->getPreview($item);
		}
		
		public function getView($item, $full) {
			$type = strtolower($item->extension());
			$viewer = $this->getViewer($type);
			return $viewer->getView($item, $full);
		}
		
		public function getDataUrl($item, $session = FALSE) {
			$url = $this->env->getServiceUrl("view", array($item->id(), "content"), TRUE);
			if ($session) {
				$s = $this->env->session()->getSessionInfo();
				$url .= '&session='.$s["session_id"];
			}
			return $url;
		}
		
		public function getServiceUrl($id, $path, $full = FALSE) {
			return $this->env->getServiceUrl($id, $path, $full);
		}

		private function getPreviewTypes() {
			$s = $this->env->settings()->setting("file_preview_options", TRUE);
			if (!isset($s["types"]) or count($s['types']) == 0) return self::$defaultPreviewTypes;
			
			$result = array();
			foreach (explode(",", $s["types"]) as $t)
				$result[] = strtolower(trim($t));
			return $result;
		}

		private function getViewTypes() {
			$s = $this->env->settings()->setting("file_view_options", TRUE);
			if (!isset($s["types"]) or count($s["types"]) == 0) return self::$defaultViewTypes;
			
			$result = array();
			foreach (explode(",", $s["types"]) as $t)
				$result[] = strtolower(trim($t));
			return $result;
		}
		
		private function isGoogleViewerEnabled() {
			$s = $this->env->settings()->setting("file_view_options", TRUE);
			return (isset($s["use_google_viewer"]) and $s["use_google_viewer"] === TRUE);
		}

		public function __toString() {
			return "FilePreview";
		}
	}
?>