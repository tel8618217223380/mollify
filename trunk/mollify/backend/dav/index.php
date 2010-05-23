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

	// NOTE! Modify these according to the location of the script
	
	$MOLLIFY_BACKEND_ROOT = "../";
	$BASE_URI = "/mollify/dev/backend/dav/";
	$ENABLE_LOCKING = TRUE;
	
	// DON'T MODIFY ANYTHING AFTER THIS LINE
	
	set_include_path('lib/'.PATH_SEPARATOR.$MOLLIFY_BACKEND_ROOT.PATH_SEPARATOR.get_include_path());
	
	require_once("configuration.php"); 
	require_once("include/Logging.class.php");
		
	global $SETTINGS, $CONFIGURATION_PROVIDER;
	Logging::initialize($SETTINGS);

	require_once("include/MollifyBackend.class.php");
	require_once("include/ConfigurationProviderFactory.class.php");
	require_once("Sabre/autoload.php");
	
	class VoidResponseHandler {}
	
	class MollifyRootFolder extends Sabre_DAV_Directory {
		private $controller;
		private $roots;
		
		function __construct($controller) {
			$this->controller = $controller;
			$this->roots = $this->controller->getRootFolders();
 		}
 		
		function getChildren() {
			$children = array();
			foreach($this->roots as $root)
				$children[] = new MollifyFolder($this->controller, $root);
			return $children;
		}
		
		function getName() {
			return "Mollify";
		}
	}
	
	class MollifyFolder extends Sabre_DAV_Directory {
		private $controller;
		private $folder;

		function __construct($controller, $folder) {
			$this->controller = $controller;
			$this->folder = $folder;
 		}
 		
		public function getChildren() {
			$children = array();
			foreach($this->controller->items($this->folder) as $i)
				$children[] = $this->createItem($i);
			return $children;
		}
		
		private function createItem($item) {
			if ($item->isFile()) return new MollifyFile($this->controller, $item);
			return new MollifyFolder($this->controller, $item);
		}

		public function createFile($name, $data = null) {
			$this->controller->assertRights($this->folder, Authentication::RIGHTS_WRITE, "create file");
			$file = $this->folder->createFile($name);
			if ($data != NULL) $file->put($data);
			return $file;
		}

		public function createDirectory($name) {
			$this->controller->assertRights($this->folder, Authentication::RIGHTS_WRITE, "create folder");
			return $this->folder->createFolder($name);
		}

		public function delete() {
			$this->controller->assertRights($this->folder, Authentication::RIGHTS_WRITE, "delete");
	        $this->folder->delete();
		}

		public function getName() {
			return $this->folder->name();
		}
	}

	class MollifyFile extends Sabre_DAV_File {
		private $controller;
		private $file;

		function __construct($controller, $file) {
			$this->controller = $controller;
			$this->file = $file;
		}

		public function getName() {
			return $this->file->name();
		}

		public function get() {
			$this->controller->assertRights($this->file, Authentication::RIGHTS_READ, "get");
			return $this->file->read();
		}
		
		public function put($data) {
			$this->controller->assertRights($this->file, Authentication::RIGHTS_WRITE, "put");
	        $this->file->put($data);
		}
		
		public function delete() {
			$this->controller->assertRights($this->file, Authentication::RIGHTS_WRITE, "delete");
	        $this->file->delete();
		}

		public function getSize() {
			return $this->file->size();
		}
		
		public function getETag() {
			return null;
		}
	}
	
	try {
		$backend = new MollifyBackend($SETTINGS, $CONFIGURATION_PROVIDER, new ConfigurationProviderFactory(), new VoidResponseHandler());
		$env = $backend->env();
		$env->initialize();
		
		$auth = new Sabre_HTTP_BasicAuth();
		$result = $auth->getUserPass();
		
		if (!$result) {
			Logging::logDebug("DAV authentication missing");
			$auth->requireLogin();
			echo "Authentication required\n";
			die();
		}
		
		$user = $env->configuration()->getUserByName($result[0]);
		if (!$user or strcmp($user["password"], md5($result[1])) != 0) {
			Logging::logDebug("DAV authentication failure");
			$auth->requireLogin();
			echo "Authentication required\n";
			die();
		}

		$env->authentication()->doAuth($user);

		$dav = new Sabre_DAV_Server(new MollifyRootFolder($env->filesystem()));
		$dav->setBaseUri($BASE_URI);
		if ($ENABLE_LOCKING) $dav->addPlugin(new Sabre_DAV_Locks_Plugin(new Sabre_DAV_Locks_Backend_FS('data')));
		$dav->exec();
	} catch (ServiceException $e) {
		Logging::logException($e);
	} catch (Exception $e) {
		Logging::logException($e);
		throw $e;
	}
?>