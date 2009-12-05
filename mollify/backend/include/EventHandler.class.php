<?php
	class EventHandler {
		private $listeners = array();
				
		public function register($type, $listener) {
			if (Logging::isDebug()) Logging::logDebug("EVENT: registering '".$type."': ".get_class($listener));
			
			if (!array_key_exists($type, $this->listeners)) $this->listeners[$type] = array();
			$list = $this->listeners[$type];
			$list[] = $listener;
			$this->listeners[$type] = $list;
		}
		
		public function onEvent($e) {
			if (Logging::isDebug()) Logging::logDebug("EVENT: onEvent: '".$e->type()."'");
			if (!array_key_exists($e->type(), $this->listeners)) return;
			
			foreach($this->listeners[$e->type()] as $listener)
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