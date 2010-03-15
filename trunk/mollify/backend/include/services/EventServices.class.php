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

	class EventServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			if (count($path) != 1)
				return FALSE;
			return TRUE;
		}
		
		public function processPost() {
			if ($this->path[0] === 'query') {
				$this->env->authentication()->assertAdmin();
				$this->response()->success($this->processQuery());
				return;
			}
			throw $this->invalidRequestException();
		}
		
		private function processQuery() {
			$data = $this->request->data;
			if (!isset($data)) throw $this->invalidRequestException();
			
			$db = $this->env->configuration()->db();
			$query = "from ".$db->table("event_log")." where type='filesystem/download'";
			if (isset($data['start_time'])) {
				$query .= ' and time >= '.$data['start_time'];
			}
			if (isset($data['end_time'])) {
				$query .= ' and time < '.$data['end_time'];
			}
			
			$count = $db->query("select count(id) ".$query)->value(0);
			$rows = isset($data["rows"]) ? $data["rows"] : 50;
			$start = isset($data["start"]) ? $data["start"] : 0;
			$result = $db->query("select id, time, user, type, item, description ".$query." limit ".$rows." offset ".$start)->rows();
			
			return array("start" => $start, "count" => count($result), "total" => $count, "events" => $result);
		}
		
		public function __toString() {
			return "EventServices";
		}
	}
?>