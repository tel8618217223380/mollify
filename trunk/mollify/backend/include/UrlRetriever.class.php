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

	class UrlRetriever {
		private $env;
		
		public function __construct($env) {
			$this->env = $env;
		}
		
		public function retrieve($url) {
			if (Logging::isDebug())
				Logging::logDebug("Retrieving [$url]");
			
			$h = curl_init();
			if (!$h)
				throw new ServiceException("REQUEST_FAILED", "Failed to initialize curl: ".curl_errno()." ".curl_error());
			
			if (!curl_setopt($h, CURLOPT_URL, $url)) {
				curl_close($h);
				throw new ServiceException("REQUEST_FAILED", "Failed to initialize curl: ".curl_errno()." ".curl_error());
			}
			
			$tempFile = sys_get_temp_dir().DIRECTORY_SEPARATOR.uniqid('Mollify', true);
			$fh = @fopen($tempFile, "wb");
			if (!$fh) {
				curl_close($h);
				throw new ServiceException("REQUEST_FAILED", "Could not open temporary file for writing: ".$tempFile);
			}
			
			if (!curl_setopt($h, CURLOPT_FILE, $fh) or !curl_setopt($h, CURLOPT_HEADER, 0)) {
				fclose($fh);
				curl_close($h);
				throw new ServiceException("REQUEST_FAILED", "Failed to initialize curl: ".curl_errno()." ".curl_error());
			}
			
			$success = curl_exec($h);
			fclose($fh);
			curl_close($h);
			if (!$success) throw new ServiceException("REQUEST_FAILED", "Failed to get url: ".curl_errno()." ".curl_error());
			
			return array("file" => $tempFile, "stream" => @fopen($tempFile, "rb"), "name" => $this->getName($url));
		}
		
		private function getName($url) {
			return "test";
		}
				
		public function __toString() {
			return "UrlRetriever";
		}
	}
?>