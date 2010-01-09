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

	class ConfigurationServices extends ServicesBase {
		private static $ITEMS = array("users", "usergroups", "folders", "userfolders");
		
		protected function isValidPath($method, $path) {
			if (count($path) == 0) return FALSE;
			if (!in_array($path[0], self::$ITEMS)) return FALSE;
			return TRUE;
		}
		
		public function processGet() {
			$this->env->authentication()->assertAdmin();
			
			switch($this->path[0]) {
				case 'users':
					$this->processGetUsers();
					break;
				case 'usergroups':
					$this->processGetUserGroups();
					break;
				case 'folders':
					$this->processGetFolders();
					break;
				case 'userfolders':
					$this->processGetUserFolders();
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		public function processPost() {
			$this->env->authentication()->assertAdmin();
			
			switch($this->path[0]) {
				case 'users':
					$this->processPostUsers();
					break;
				case 'usergroups':
					$this->processPostUserGroups();
					break;
				case 'folders':
					$this->processPostFolders();
					break;
				case 'userfolders':
					$this->processPostUserFolders();
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		public function processPut() {
			$this->env->authentication()->assertAdmin();
			
			switch($this->path[0]) {
				case 'users':
					$this->processPutUsers();
					break;
				case 'usergroups':
					$this->processPutUserGroups();
					break;
				case 'folders':
					$this->processPutFolders();
					break;
				case 'userfolders':
					$this->processPutUserFolders();
					break;
				default:
					throw $this->invalidRequestException();
			}
		}

		public function processDelete() {
			$this->env->authentication()->assertAdmin();
			
			switch($this->path[0]) {
				case 'users':
					$this->processDeleteUsers();
					break;
				case 'usergroups':
					$this->processDeleteUserGroups();
					break;
				case 'folders':
					$this->processDeleteFolders();
					break;
				case 'userfolders':
					$this->processDeleteUserFolders();
					break;
				default:
					throw $this->invalidRequestException();
			}
		}
		
		private function processGetUsers() {
			if (count($this->path) == 1) {
				$this->response()->success($this->env->configuration()->getAllUsers());
				return;
			}
			if (count($this->path) == 2) {
				$this->response()->success($this->env->configuration()->getUser($this->path[1]));
				return;
			}
			if (count($this->path) == 3 and $this->path[2] === 'groups') {
				$this->response()->success($this->env->configuration()->getUsersGroups($this->path[1]));
				return;
			}
			throw $this->invalidRequestException();
		}
		
		private function processPostUsers() {
			if (!$this->request->hasData()) throw $this->invalidRequestException();
			
			if (count($this->path) == 1) {
				$user = $this->request->data;
				if (!isset($user['name']) or !isset($user['password']) or !isset($user['permission_mode'])) throw $this->invalidRequestException();
				$user['permission_mode'] = strtoupper($user['permission_mode']);
				$this->env->authentication()->assertPermissionValue($user['permission_mode']);
				
				$this->env->configuration()->addUser($user['name'], $user['password'], $user['permission_mode']);
				$this->response()->success(TRUE);
				return;
			}
			if (count($this->path) == 3 and $this->path[2] === 'groups') {
				$groups = $this->request->data;
				$this->response()->success($this->env->configuration()->addUsersGroups($this->path[1], $groups));
				return;
			}
			throw $this->invalidRequestException();

		}

		private function processPutUsers() {
			if (count($this->path) != 2 or !$this->request->hasData()) throw $this->invalidRequestException();

			$id = $this->path[1];
			$user = $this->request->data;
			if (!isset($user['name']) or !isset($user['permission_mode'])) throw $this->invalidRequestException();
			$user['permission_mode'] = strtoupper($user['permission_mode']);
			$this->env->authentication()->assertPermissionValue($user['permission_mode']);
			
			$this->env->configuration()->updateUser($id, $user['name'], $user['permission_mode']);
			$this->response()->success(TRUE);			
		}

		private function processDeleteUsers() {
			if (count($this->path) != 2) throw $this->invalidRequestException();

			$id = $this->path[1];
			$this->env->configuration()->removeUser($id);
			$this->response()->success(TRUE);			
		}

		private function processGetUserGroups() {
			if (count($this->path) == 1) {
				$this->response()->success($this->env->configuration()->getAllUserGroups());
				return;
			}
			if (count($this->path) == 2) {
				$this->response()->success($this->env->configuration()->getUserGroup($this->path[1]));
				return;
			}
			if (count($this->path) == 3) {
				if ($this->path[2] != 'users') throw $this->invalidRequestException();
				$this->response()->success($this->env->configuration()->getGroupUsers($this->path[1]));
				return;
			}
			
			throw $this->invalidRequestException();
		}
		
		private function processPostUserGroups() {
			if ((count($this->path) != 1 and count($this->path) != 3) or !$this->request->hasData()) throw $this->invalidRequestException();
			
			if (count($this->path) == 1) {
				$group = $this->request->data;
				if (!isset($group['name']) or !isset($group['permission_mode'])) throw $this->invalidRequestException();
				$group['permission_mode'] = strtoupper($group['permission_mode']);
				$this->env->authentication()->assertPermissionValue($group['permission_mode']);
				
				$this->env->configuration()->addUserGroup($group['name'], $group['permission_mode']);
				$this->response()->success(TRUE);
			} else {
				$id = $this->path[1];		
				$users = $this->request->data;
				
				switch ($this->path[2]) {
					case 'users':
						$this->env->configuration()->addGroupUsers($id, $users);
						$this->response()->success(TRUE);
						break;
					case 'remove_users':
						$this->env->configuration()->removeGroupUsers($id, $users);
						$this->response()->success(TRUE);
						break;
					default:
						throw $this->invalidRequestException();	
				}
			}
		}

		private function processPutUserGroups() {
			if (count($this->path) != 2 or !$this->request->hasData()) throw $this->invalidRequestException();

			$id = $this->path[1];
			$group = $this->request->data;
			if (!isset($group['name']) or !isset($group['permission_mode'])) throw $this->invalidRequestException();
			$group['permission_mode'] = strtoupper($group['permission_mode']);
			$this->env->authentication()->assertPermissionValue($group['permission_mode']);

			$this->env->configuration()->updateUserGroup($id, $group['name'], $group['permission_mode']);
			$this->response()->success(TRUE);			
		}
		
		private function processDeleteUserGroups() {
			if (count($this->path) != 2) throw $this->invalidRequestException();

			$id = $this->path[1];
			$this->env->configuration()->removeUserGroup($id);
			$this->response()->success(TRUE);			
		}

		private function processGetFolders() {
			if (count($this->path) != 1) throw $this->invalidRequestException();
			
			$this->response()->success($this->env->configuration()->getFolders());
		}
		
		private function processPostFolders() {
			if (count($this->path) != 1 or !$this->request->hasData()) throw $this->invalidRequestException();
			
			$folder = $this->request->data;
			$this->env->configuration()->addFolder($folder['name'], $folder['path']);
			$this->response()->success(TRUE);	
		}
		
		private function processPutFolders() {
			if (count($this->path) != 2 or !$this->request->hasData()) throw $this->invalidRequestException();
			
			$id = $this->path[1];
			$folder = $this->request->data;
			$this->env->configuration()->updateFolder($id, $folder['name'], $folder['path']);
			$this->response()->success(TRUE);	
		}
		
		private function processDeleteFolders() {
			if (count($this->path) != 2) throw $this->invalidRequestException();

			$id = $this->path[1];
			$this->env->configuration()->removeFolder($id);
			//TODO remove descriptions, permissions etc
			$this->response()->success(TRUE);	
		}
		
		private function processGetUserFolders() {
			if (count($this->path) != 2) throw $this->invalidRequestException();
			$this->response()->success($this->env->configuration()->getUserFolders($this->path[1]));
		}
		
		private function processPostUserFolders() {
			if (count($this->path) != 2) throw $this->invalidRequestException();
			
			$userId = $this->path[1];
			$folder = $this->request->data;
			
			$this->env->configuration()->addUserFolder($userId, $folder['id'], isset($folder['name']) ? $folder['name'] : NULL);
			$this->response()->success(TRUE);	
		}
		
		private function processPutUserFolders() {
			if (count($this->path) != 3) throw $this->invalidRequestException();
			
			$userId = $this->path[1];
			$folderId = $this->path[2];
			$folder = $this->request->data;
			
			$this->env->configuration()->updateFolder($userId, $folderId, $folder['name']);
			$this->response()->success(TRUE);	
		}
		
		private function processDeleteUserFolders() {
			if (count($this->path) != 3) throw $this->invalidRequestException();
			
			$userId = $this->path[1];
			$folderId = $this->path[2];
			
			$this->env->configuration()->removeUserFolder($userId, $folderId);
			$this->response()->success(TRUE);	
		}

	}
?>