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

	class S3Services extends ServicesBase {		
		protected function isValidPath($method, $path) {
			return count($path) == 1;
		}
		
		public function processGet() {
			if (($this->path[0] !== 'upload' and $this->path[0] !== 'success') or !$this->request->hasParam("id")) throw $this->invalidRequestException();
			
			if ($this->path[0] === 'success') {
				$this->env->response()->html("ok"); 
				return;
			}
			
			$s3 = $this->env->plugins()->getPlugin("S3")->getS3();
			$folder = $this->item($this->request->param("id"));
			if ($folder->filesystem()->type() != 'S3FS') throw $this->invalidRequestException();
			
			$bucket = $folder->filesystem()->getBucketKey();
			$success = $this->env->getServiceUrl("s3", array(), TRUE)."success?id=".$this->request->param("id");
			$policy = base64_encode('{"expiration": "2999-12-01T12:00:00.000Z", "conditions":[ {"bucket": "'.$bucket.'"}, ["starts-with", "$key", "'.$folder->path().'"], { "success_action_redirect": "'.$success.'" } ]}');
			$signature = $this->hex2b64($this->hmacsha1($s3->getSecretKey(), $policy));
			
			/*$html =
			"<form action='http://".$bucket.".s3.amazonaws.com/' method='post' enctype='multipart/form-data' target='s3-upload-frame' id='s3-upload-form'>".
				"<input type='hidden' name='AWSAccessKeyId' value='".$s3->getKey()."' />".
				"<input type='hidden' name='key' value='".$folder->path()."\${filename}' />".
				"<input type='hidden' name='success_action_status' value='200' />".
				"<input type='hidden' name='policy' value='".$policy."' />".
				"<input type='hidden' name='signature' value='".$signature."' />".
				"<input type='file' name='file' />".
			"</form>";*/
			$this->env->response()->success(array(
				"url" => "http://".$bucket.".s3.amazonaws.com/",
				"keys" => array(
					"AWSAccessKeyId" => $s3->getKey(),
					"key" => $folder->path()."\${filename}",
					"policy" => $policy,
					"signature" => $signature,
					"success_action_redirect" => $success
				)
			));
		}
		
		/*
		 * Calculate HMAC-SHA1 according to RFC2104
		 * See http://www.faqs.org/rfcs/rfc2104.html
		 */
		function hmacsha1($key, $data) {
			$blocksize=64;
			$hashfunc='sha1';
			if (strlen($key)>$blocksize)
				$key=pack('H*', $hashfunc($key));
			$key=str_pad($key,$blocksize,chr(0x00));
			$ipad=str_repeat(chr(0x36),$blocksize);
			$opad=str_repeat(chr(0x5c),$blocksize);
			$hmac = pack('H*',$hashfunc(
				($key^$opad).pack(
					'H*',$hashfunc(
						($key^$ipad).$data
					)
				)
			));
		    return bin2hex($hmac);
		}
 
		/*
		 * Used to encode a field for Amazon Auth
		 * (taken from the Amazon S3 PHP example library)
		 */
		function hex2b64($str) {
			$raw = '';
			for ($i=0; $i < strlen($str); $i+=2)
				$raw .= chr(hexdec(substr($str, $i, 2)));
		    return base64_encode($raw);
		}
	}