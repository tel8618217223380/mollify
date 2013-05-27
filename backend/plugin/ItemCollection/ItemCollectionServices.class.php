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
			$this->response()->success($this->convert($this->handler()->getUserItemCollections()));
		}
		
		private function convert($collections) {
			$result = array();
			foreach($collections as $c)
				$result[] = array("id" => $c["id"], "name" => $c["name"], "items" => $this->convertItems($c["items"]));
			return $result;
		}

		private function convertItems($items) {
			$result = array();
			foreach($items as $i)
				$result[] = $i->data();
			return $result;
		}

		public function processDelete() {
			if (count($this->path) != 1) throw $this->invalidRequestException();
			
			$id = $this->path[0];
			$this->handler()->deleteUserItemCollection($id);
			$this->response()->success($this->convert($this->handler()->getUserItemCollections()));
		}
				
		public function processPost() {
			if (count($this->path) > 1) throw $this->invalidRequestException();
			$id = FALSE;
			if (count($this->path) == 1) $id = $this->path[0];
			
			$data = $this->request->data;
			if (!isset($data["name"]) or !isset($data["items"])) throw $this->invalidRequestException("No data");
			
			$name = $data["name"];
			$items = $data["items"];
			if ((!$id && strlen($name) == 0) or !is_array($items) or count($items) == 0) throw $this->invalidRequestException("Missing data");
			
			if (!$id) {
				$this->handler()->addUserItemCollection($name, $items);
				$this->response()->success($this->convert($this->handler()->getUserItemCollections()));
			} else {
				$this->handler()->addCollectionItems($id, $items);
				$this->response()->success(array());
			}
		}
		
		private function handler() {
			return $this->env->plugins()->getPlugin("ItemCollection")->getHandler();
		}
	}
?>
