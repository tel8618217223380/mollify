<?php
	class FilesystemServices extends ServicesBase {
		protected function isValidPath($path) {
			return TRUE;
		}
		
		public function processGet() {
			$this->response()->success("jee");
		}

	}
?>