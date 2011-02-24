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
	 	private $bucketCache = array();
	 	private $objectHeaderCache = array();
	 	
	 	public function __construct($settings) {
	 		$this->settings = $settings;
	 			 	
		 	define('AWS_KEY', $settings['AWS_KEY']);
			define('AWS_SECRET_KEY', $settings['AWS_SECRET_KEY']);
			define('AWS_CANONICAL_ID', $settings['AWS_CANONICAL_ID']);
			define('AWS_CANONICAL_NAME', $settings['AWS_CANONICAL_NAME']);
			include_once("sdk.class.php");
			
			$this->s3 = new AmazonS3();
			$this->s3->set_cache_config("./s3_cache");
		}
		
		public function getKey() {
			return $this->settings['AWS_KEY'];
		}

		public function getSecretKey() {
			return $this->settings['AWS_SECRET_KEY'];
		}

		public function bucketExists($b) {
			$bucket = $this->getBucketKey($b);
			
			if (array_key_exists($bucket, $this->bucketCache))
				return $this->bucketCache[$bucket];
			
			$e = $this->s3->if_bucket_exists($bucket);
			$this->bucketCache[$bucket] = $e;
			return $e;
		}

		public function createBucket($b) {
			$bucket = $this->getBucketKey($b);
			
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
		
		public function getObjects($bucket, $parent) {
			$filters = array();
			if (strlen($parent) > 0) $filters["prefix"] = $parent;
			$ret = $this->s3->get_object_list($this->getBucketKey($bucket), $filters);
			return $ret;
		}

		public function createEmptyObject($bucket, $obj) {
			$ret = $this->s3->create_object($this->getBucketKey($bucket), $obj, array("body" => ""));
			if (!$ret->isOK()) throw new ServiceException("REQUEST_FAILED", "Could not create empty object: ".$ret->status." ".Util::array2str($ret->header));
			return $ret;
		}
				
		public function getObjectSize($bucket, $obj) {
			$h = $this->getObjectHeaders($bucket, $obj);
			return $h["content-length"];
		}

		public function getObjectHeaders($b, $obj) {
			$bucket = $this->getBucketKey($b);
			if (is_array($obj)) {
				if (count($obj) == 0) return NULL;
				
				foreach($obj as $path)
					$this->s3->batch()->get_object_headers($bucket, $path);
				$ret = $this->s3->batch()->cache('5 minutes')->send();
				
				$i = 0;
				foreach($ret as $r) {
					if (!$r->isOK()) throw new ServiceException("REQUEST_FAILED", "Could not get multiple S3 headers: ".$ret->status." ".Util::array2str($ret->header));
					Logging::logDebug(Util::array2str($r));
					
					$p = $obj[$i++];
					$this->objectHeaderCache[$p] = $r->header;
				}
				return;
			}
			
			if (array_key_exists($obj, $this->objectHeaderCache)) return $this->objectHeaderCache[$obj];
			$ret = $this->s3->get_object_headers($bucket, $obj);
			if (!$ret->isOK()) throw new ServiceException("REQUEST_FAILED", "Could not get S3 headers: ".$ret->status." ".Util::array2str($ret->header));
			return $ret->header;
		}

		public function copyObject($bucket, $obj, $to) {
			$b = $this->getBucketKey($bucket);
			$ret = $this->s3->copy_object(array("bucket" => $b, "filename" => $obj), array("bucket" => $b, "filename" => $to));
			return $ret->isOK();
		}

		public function moveObject($bucket, $obj, $to) {
			$b = $this->getBucketKey($bucket);
			$ret = $this->s3->copy_object(array("bucket" => $b, "filename" => $obj), array("bucket" => $b, "filename" => $to));
			if (!$ret->isOK())
				throw new ServiceException("REQUEST_FAILED", "Could not rename: ".$ret->status." ".Util::array2str($ret->header));
			$ret = $this->s3->delete_object($this->getBucketKey($bucket), $obj);
			if (!$ret->isOK())
				throw new ServiceException("REQUEST_FAILED", "Could not rename: ".$ret->status." ".Util::array2str($ret->header));
		}
		
		public function deleteObject($bucket, $obj) {
			$ret = $this->s3->delete_object($this->getBucketKey($bucket), $obj);
			if (!$ret->isOK())
				throw new ServiceException("REQUEST_FAILED", "Could not delete: ".$ret->status." ".Util::array2str($ret->header));
		}

		public function getObjectUrl($bucket, $obj) {
			return $this->s3->get_object_url($this->getBucketKey($bucket), $obj, '5 minutes');
		}
				
		public function getBucketKey($b) {
			return strtolower($this->getKey())."-".$b;
		}
		
		public function __toString() {
			return "MollifyS3";
		}
	}
?>