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
		
	require_once("S3Filesystem.class.php");
	
	class MollifyS3 {
	 	private $s3;
	 	private $settings;
	 	
	 	public function __construct($settings) {
	 		$this->settings = $settings;
	 			 	
		 	define('AWS_KEY', $settings['AWS_KEY']);
			define('AWS_SECRET_KEY', $settings['AWS_SECRET_KEY']);
			define('AWS_CANONICAL_ID', $settings['AWS_CANONICAL_ID']);
			define('AWS_CANONICAL_NAME', $settings['AWS_CANONICAL_NAME']);
			include_once("sdk.class.php");
			
			$this->s3 = new AmazonS3();
			$this->s3->enable_debug_mode(true);
		}

		public function bucketExists($bucket) {
			return $this->s3->if_bucket_exists($this->getBucketId($bucket));
		}

		public function createBucket($b) {
			$bucket = $this->getBucketId($b);
			
			$ret = $this->s3->create_bucket($bucket, isset($settings["REGION"]) ? $settings["REGION"] : AmazonS3::REGION_EU_W1);
			if ($ret->isOK()) {
				$exists = $this->s3->if_bucket_exists($bucket);
				while (!$exists) {
					sleep(1);
					$exists = $this->s3->if_bucket_exists($bucket);
				}
			} else {
				throw new ServiceException("REQUEST_FAILED", "Could not create S3 bucket: ".$ret->status." ".Util::array2str($ret->header));
			}
		}
		
		public function getObjects($bucket) {
			return $this->s3->get_object_list($this->getBucketId($bucket));
		}
		
		private function getBucketId($b) {
			return strtolower($this->settings['AWS_KEY'])."-".$b;
		}
		
		public function __toString() {
			return "MollifyS3";
		}
	}
?>