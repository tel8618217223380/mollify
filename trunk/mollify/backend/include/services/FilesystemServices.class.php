<?php
	class FilesystemServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			if (count($path) < 1 or count($path) > 2) return FALSE;
			return TRUE;
		}
		
		public function processGet() {
			$id = $this->path[0];
			$item = $this->env->filesystem()->getItemFromId($id);
			
			$this->response()->success($item->getPath());
		}

	}
?>