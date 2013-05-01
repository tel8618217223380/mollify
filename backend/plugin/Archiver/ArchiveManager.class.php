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

	class ArchiveManager {
		private $env;
		private $compressor;
		
		function __construct($env, $compressor) {
			$this->env = $env;
			$this->compressor = $compressor;
		}
		
		public function storeArchive($items) {
			$id = uniqid();
			$zip = $this->createArchive($items);
			$this->env->session()->param("archive_".$id, $zip->filename());
			return $id;
		}
		
		private function createArchive($items) {
			$c = $this->getCompressor();
			
			if (is_array($items)) {
				$this->env->filesystem()->assertRights($items, Authentication::RIGHTS_READ, "add to package");
				
				foreach($items as $item) {
					$item->addTo($c);
					$this->env->events()->onEvent(FileEvent::download($item));
				}
			} else {
				$item = $items;
				$this->env->filesystem()->assertRights($item, Authentication::RIGHTS_READ, "add to package");
				$item->addTo($c);
				$this->env->events()->onEvent(FileEvent::download($item));
			}
			
			$c->finish();
			return $c;
		}
		
		public function extract($archive, $to) {
			$zip = new ZipArchive;
			if ($zip->open($archive) !== TRUE)
				throw new ServiceException("REQUEST_FAILED", "Could not open archive ".$archive);
			
			$zip->extractTo($to);
			$zip->close();
		}
		
		public function compress($items, $to) {
			$a = $this->createArchive($items);
			//$zip = $this->zipper();
			//$folder->addToZip($zip);
			//$zip->finish();
			
			rename($a->filename(), $to);
		}

		private function getCompressor() {
			require_once('MollifyCompressor.class.php');
			
			if ($this->compressor == NULL || strcasecmp($this->compressor, "ziparchive") === 0) {
				require_once('zip/MollifyZipArchive.class.php');
				return new MollifyZipArchive($this->env);
			} else if (strcasecmp($this->compressor, "native") === 0) {
				require_once('zip/MollifyZipNative.class.php');
				return new MollifyZipNative($this->env);
			} else if (strcasecmp($this->compressor, "raw") === 0) {
				require_once('zip/MollifyZipRaw.class.php');
				return new MollifyZipRaw($this->env);
			}
			
			throw new ServiceException("INVALID_CONFIGURATION", "Unsupported compressor configured: ".$this->compressor);
		}
		
		public function __toString() {
			return "ArchiverManager";
		}
	}
?>