<?php
	class EventHandler {
		private $listeners = array();
				
		public function register($type, $listener) {
			if (!array_key_exists($type, $this->listeners)) $this->listeners[$type] = array();
			$list = $this->listeners[$type];
			$list[] = $listener;
		}
		
		public function onEvent($e) {
			if (!array_key_exists($e->type())) return;
			
			foreach($this->listeners as $listener)
				$listener->onEvent($e);
		}
	}
	
	class Event {
		private $type;
		private $data;
		
		public function __construct($type, $data) {
			$this->type = $type;
			$this->data = $data;
		}
		
		public function type() {
			return $this->type;
		}
		
		public function data() {
			return $this->data;
		}
	}
?>