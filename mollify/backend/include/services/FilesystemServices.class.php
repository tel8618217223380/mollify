<?php
	class FilesystemServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			if (count($path) < 1 or count($path) > 2) return FALSE;
			return TRUE;
		}
		
		public function processGet() {
			$item = $this->env->filesystem()->getItemFromId(strtr($this->path[0], '-_,', '+/='));
			$this->env->filesystem()->assertRights($item, Authentication::RIGHTS_READ, Util::array2str($this->path));
			
			if ($item->isFile()) $this->processGetFile($item);
			else $this->processGetFolder($item);
		}
		
		private function processGetFile($item) {
			if (count($this->path) == 1) {
				$item->download();
				return;
			}
						
			switch (strtolower($this->path[1])) {
				case 'items':
					$this->response()->success(array("directories" => $item->folders(), "files" => $item->files()));
					break;
				case 'files':
					$this->response()->success($item->files());
					break;
				case 'directories':
					$this->response()->success($item->folders());
					break;
				case 'details':
					$this->response()->success($item->details());
					break;
				default:
					throw new ServiceException("INVALID_REQUEST", "Invalid folder request: ".strtoupper($this->request->method())." ".$this->request->URI());
			}
		}
		
		private function processGetFolder($item) {
			if (count($this->path) != 2) throw new ServiceException("INVALID_REQUEST", "Invalid folder request: ".strtoupper($this->request->method())." ".$this->request->URI());
			
			switch (strtolower($this->path[1])) {
				case 'items':
					$this->response()->success(array("directories" => $item->folders(), "files" => $item->files()));
					break;
				case 'files':
					$this->response()->success($item->files());
					break;
				case 'directories':
					$this->response()->success($item->folders());
					break;
				case 'details':
					$this->response()->success($item->details());
					break;
				default:
					throw new ServiceException("INVALID_REQUEST", "Invalid folder request: ".strtoupper($this->request->method())." ".$this->request->URI());
			}
		}
	}
?>