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

	set_include_path('lib/' . PATH_SEPARATOR . '../' . PATH_SEPARATOR . get_include_path());
	
	require_once("../configuration.php"); 
	require_once("include/Logging.class.php");
		
	global $SETTINGS, $CONFIGURATION_PROVIDER;
	Logging::initialize($SETTINGS);

	require_once("include/MollifyBackend.class.php");
	require_once("include/ConfigurationProviderFactory.class.php");
	require_once("Sabre/autoload.php");
	
	$baseUri = "/mollify/dev/backend/dav/index.php/";
	
	class VoidResponseHandler {}
	
	class MollifyRootFolder extends Sabre_DAV_Directory {
		private $filesystem;
		private $roots;
		
		function __construct($filesystem) {
			$this->filesystem = $filesystem;
			$this->roots = $filesystem->getRootFolders();
 		}
 		
		function getChildren() {
			$children = array();
			foreach($this->roots as $root)
				$children[] = new MollifyFolder($this->filesystem, $root);
			return $children;
		}
		
		function getName() {
			return "Mollify";
		}
	}
	
	class MollifyFolder extends Sabre_DAV_Directory {
		private $filesystem;
		private $folder;

		function __construct($filesystem, $folder) {
			$this->filesystem = $filesystem;
			$this->folder = $folder;
 		}
 		
		function getChildren() {
			$children = array();
			foreach($this->filesystem->items($this->folder) as $i)
				$children[] = $this->createItem($i);
			return $children;
		}
		
		function createItem($item) {
			if ($item->isFile()) return new MollifyFile($item);
			return new MollifyFolder($this->filesystem, $item);
		}

		function getName() {
			return $this->folder->name();
		}
	}

	class MollifyFile extends Sabre_DAV_File {
		private $file;

		function __construct($file) {
			$this->file = $file;
		}

		function getName() {
			return $this->file->name();
		}

		function get() {
			return $this->file->read();
		}

		function getSize() {
			return $this->file->size();
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

		$root = new MollifyRootFolder($env->filesystem());
		$dav = new Sabre_DAV_Server($root);
		$dav->setBaseUri($baseUri);
		$dav->exec();
	} catch (ServiceException $e) {
		Logging::logException($e);
	}
?>