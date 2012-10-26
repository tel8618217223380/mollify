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

	class ItemCollectionServices extends ServicesBase {		
		protected function isValidPath($method, $path) {
			return TRUE;
		}
		
		public function isAuthenticationRequired() {
			return TRUE;
		}
		
		public function processGet() {
			if (count($this->path) != 0) throw $this->invalidRequestException();
			$this->response()->success($this->handler()->getUserItemCollections());
		}

		public function processDelete() {
			if (count($this->path) != 1) throw $this->invalidRequestException();
			
			$id = $this->path[0];
			$this->handler()->deleteUserItemCollection($id);
			$this->response()->success(array());
		}
				
		public function processPost() {
			if (count($this->path) != 0) throw $this->invalidRequestException();
			
			$data = $this->request->data;
			
			if (!isset($data["name"]) or !isset($data["items"])) throw $this->invalidRequestException("No data");
			$name = $data["name"];
			$items = $data["items"];
			if (strlen($name) == 0 or !is_array($items) or count($items) == 0) throw $this->invalidRequestException("Missing data");
			
			$this->handler()->addUserItemCollection($name, $items);
			$this->response()->success(TRUE);
		}
		
		private function handler() {
			return $this->env->plugins()->getPlugin("ItemCollection")->getHandler();
		}
	}
?>