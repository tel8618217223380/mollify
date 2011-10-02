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
	
	class ItemDetails extends PluginBase {
		private $detailProviders = array();
		
		public function setup() {
			$this->env->filesystem()->registerDetailsPlugin("itemdetails", $this);
		}
		
		public function registerDetailsProvider($keys, $p) {
			if (is_array($keys))
				foreach($keys as $k)
					$this->detailProviders[$k] = $p;
			else
				$this->detailProviders[$keys] = $p;
		}
		
		public function getItemDetails($item, $details, $data) {
			if (!$data) return FALSE;
			
			$result = array();
			foreach($data as $key)
				$result[$key] = $this->getData($item, $key);
			return $result;
		}
		
		private function getData($item, $key) {
			if (strcmp($key, "size") === 0)
				return $item->size();
			if (strcmp($key, "last-modified") === 0)
				return $this->env->formatTimestampInternal($item->lastModified());
			if (array_key_exists($key, $this->detailProviders)) {
				$provider = $this->detailProviders[$key];
				return $provider->getDetail($item, $key);
			}
			return NULL;
		}
				
		public function __toString() {
			return "ItemDetailsPlugin";
		}
	}
?>
