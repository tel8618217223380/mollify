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

	class FilePreview {
		static $defaultPreviewTypes = array("gif", "png", "jpg");
		static $defaultViewTypes = array("gif", "png", "jpg");
		
		static $googleViewerTypes = array("pdf", "doc", "xls");
		static $imageTypes = array("gif", "png", "jpg");
		
		private $view;
		private $preview;
		
		public function __construct($serviceEnvironment, $view, $preview) {
			$this->env = $serviceEnvironment;
			$this->view = $view;
			$this->preview = $preview;
		}
		
		public function getItemDetails($item) {
			if (!$item->isFile()) return FALSE;
			$type = strtolower($item->extension());
			
			$result = array();
			if ($this->preview and in_array($type, $this->getPreviewTypes())) {
				$result["preview"] = array(
					"url" => $this->env->getServiceUrl("preview", array($item->id(), "embedded"))
				);
			}
			if ($this->view and in_array($type, $this->getViewTypes())) {
				if (in_array($type, self::$imageTypes))
					$result["view"] = $this->getImageViewData($item);
				else if (in_array($type, self::$googleViewerTypes) and $this->isGoogleViewerEnabled())
					$result["view"] = $this->getGoogleViewData($item);
			}
			
			return $result;
		}

		private function getPreviewTypes() {
			$s = $this->env->settings()->setting("file_preview_options", TRUE);
			if (isset($s["types"])) return $s["types"];
			return self::$defaultPreviewTypes;
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
		
		public function getPreview($item) {
			$dataUrl = $this->env->getServiceUrl("preview", array($item->id(), "content"), TRUE);
			return array("html" => '<div id="file-preview-container" style="overflow:auto; max-height:300px"><img src="'.$dataUrl.'" style="max-width:400px"></div>');
		}
		
		public function getView($item, $full) {
			$type = strtolower($item->extension());
			
			if (in_array($type, self::$imageTypes))
				return $this->getImageView($item, $full);
			else if (in_array($type, self::$googleViewerTypes) and $this->isGoogleViewerEnabled())
				return $this->getGoogleView($item, $full);
		}

		private function getImageViewData($item) {
			return array(
				"embedded" => array(
					"url" => $this->env->getServiceUrl("view", array($item->id(), "embedded"), TRUE)
				),
				"full" => $this->env->getServiceUrl("view", array($item->id(), "full"), TRUE)
			);
		}
				
		private function getImageView($item, $full) {
			$html = $full ? "<html>" : "";
			$html .= '<img src="'.$this->getDataUrl($item).'">';
			if ($full) $html .= "</html>";
			return $html;
		}

		private function getGoogleView($item, $full) {
			$html = $full ? "<html>" : "";
			$html .= '<iframe id="google-viewer" src="'.$this->getGoogleViewerUrl($item, FALSE).'" width="600" height="780" style="border: none;"></iframe>';
			if ($full) $html .= "</html>";
			return $html;
		}

		private function getGoogleViewData($item) {
			return array(
				"embedded" => array(
					"url" => $this->env->getServiceUrl("view", array($item->id(), "embedded"), TRUE),
					"element_id" => "google-viewer",
					"size" => "600;780"
				),
				"full" => $this->getGoogleViewerUrl($item, TRUE)
			);
		}
		
		private function getGoogleViewerUrl($item, $full) {
			$url = 'http://docs.google.com/viewer?url='.urlencode($this->getDataUrl($item, TRUE));
			if (!$full) $url .= '&embedded=true';
			return $url;
		}
		
		private function getDataUrl($item, $session = FALSE) {
			$url = $this->env->getServiceUrl("view", array($item->id(), "content"), TRUE);
			if ($session) {
				$s = $this->env->session()->getSessionInfo();
				$url .= '&session='.$s["session_id"];
			}
			return $url;
		}
		
		public function __toString() {
			return "FilePreview";
		}
	}
?>