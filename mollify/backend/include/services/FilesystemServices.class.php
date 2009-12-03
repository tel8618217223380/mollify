<?php
	class FilesystemServices extends ServicesBase {
		protected function isValidPath($method, $path) {
			if (count($path) < 1 or count($path) > 2) return FALSE;
			return TRUE;
		}
		
		public function processGet() {
			$item = $this->env->filesystem()->getItemFromId($this->convertItemID($this->path[0]));
			$this->env->filesystem()->assertRights($item, Authentication::RIGHTS_READ, Util::array2str($this->path));
			
			if ($item->isFile()) $this->processGetFile($item);
			else $this->processGetFolder($item);
		}

		public function processPost() {
			$item = $this->env->filesystem()->getItemFromId($this->convertItemID($this->path[0]));
			$this->env->filesystem()->assertRights($item, Authentication::RIGHTS_WRITE, Util::array2str($this->path));
			
			if ($item->isFile()) $this->processPostFile($item);
			else $this->processPostFolder($item);
		}
		
		private function convertItemId($id) {
			return strtr($id, '-_,', '+/=');
		}
				
		private function processGetFile($item) {
			if (count($this->path) == 1) {
				$item->download();
				return;
			}
						
			switch (strtolower($this->path[1])) {
				case 'details':
					$this->response()->success($item->details());
					break;
				default:
					throw new ServiceException("INVALID_REQUEST", "Invalid folder request: ".strtoupper($this->request->method())." ".$this->request->URI());
			}
		}
		
		private function processPostFile($item) {
			if (count($this->path) < 2)
				throw new ServiceException("INVALID_REQUEST", "Invalid file request: ".strtoupper($this->request->method())." ".$this->request->URI());
						
			switch (strtolower($this->path[1])) {
				case 'name':
					$this->response()->success($item->rename($this->request->data));
					break;
				case 'details':
					$this->response()->success($item->setDetails($this->request->data));
					break;
				case 'description':
					$this->response()->success($item->setDescription($this->request->data));
					break;
				default:
					throw new ServiceException("INVALID_REQUEST", "Invalid file request: ".strtoupper($this->request->method())." ".$this->request->URI());
			}
		}
		
		private function processGetFolder($item) {
			if (count($this->path) != 2) throw new ServiceException("INVALID_REQUEST", "Invalid folder request: ".strtoupper($this->request->method())." ".$this->request->URI());
			
			switch (strtolower($this->path[1])) {
				case 'items':
					$this->response()->success(array("folders" => $item->folders(), "files" => $item->files()));
					break;
				case 'files':
					$this->response()->success($item->files());
					break;
				case 'folders':
					$this->response()->success($item->folders());
					break;
				case 'details':
					$this->response()->success($item->details());
					break;
				default:
					throw new ServiceException("INVALID_REQUEST", "Invalid folder request: ".strtoupper($this->request->method())." ".$this->request->URI());
			}
		}
		
		private function processPostFolder($item) {}
	}
?>