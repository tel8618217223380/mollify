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

	class ShareServices extends ServicesBase {		
		protected function isValidPath($method, $path) {
			return TRUE;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processGet() {
			if (count($this->path) > 2 or (strcmp($this->path[0], 'items') != 0 and strcmp($this->path[0], 'all') != 0)) throw $this->invalidRequestException();

			if (strcmp($this->path[0], 'all') == 0) {
				$shares = $this->handler()->getUserShares();
				$items = array();
				foreach($shares as $uk => $u) {
					foreach($u as $ik => $i) {
						if (in_array($ik, $items)) continue;
						$item = $this->item($ik);
						$items[$ik] = $item->data();
					}
				}
				$this->response()->success(array("shares" => $shares, "items" => $items));
				return;
			}
			
			$itemId = $this->path[1];
			if (strpos($itemId, "_") < 0) $this->item($itemId);

			$this->response()->success($this->handler()->getShares($itemId));
		}

		public function processDelete() {
			if (count($this->path) > 2) throw $this->invalidRequestException();
			
			if ($this->path[0] == "items") {
				if (count($this->path) != 2) throw $this->invalidRequestException();
				$id = $this->path[1];
				$this->handler()->deleteSharesForItem($id);
				$this->response()->success(array());
			} else {
				if (count($this->path) != 1) throw $this->invalidRequestException();
				$id = $this->path[0];
				$this->handler()->deleteShare($id);
				$this->response()->success(array());
			}
		}
				
		public function processPost() {
			if (count($this->path) > 0) throw $this->invalidRequestException();
			
			$data = $this->request->data;			
			if (!isset($data["item"]) or !isset($data["name"])) throw $this->invalidRequestException("No data");
					
			$itemId = $data["item"];
			if (strpos($itemId, "_") < 0) $this->item($itemId);
			
			if ($data["expiration"] and !is_int($data["expiration"])) throw $this->invalidRequestException("Invalid datatype: expiration");
			
			$this->handler()->addShare($itemId, $data["name"], $data["expiration"], isset($data["active"]) ? $data["active"] : TRUE);
			$this->response()->success($this->handler()->getShares($itemId));
		}
		
		public function processPut() {
			if (count($this->path) != 1) throw $this->invalidRequestException();
			
			$id = $this->path[0];
			$data = $this->request->data;
			if (!isset($data["name"])) throw $this->invalidRequestException("No data");
			if ($data["expiration"] and !is_int($data["expiration"])) throw $this->invalidRequestException("Invalid datatype: expiration");
			
			$this->handler()->editShare($id, $data["name"], $data["expiration"], isset($data["active"]) ? $data["active"] : TRUE);
			$this->response()->success(array());
		}
		
		private function handler() {
			return $this->env->plugins()->getPlugin("Share")->getHandler();
		}
	}
?>
