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
		private $roots;
		
		function __construct($roots) {
			$this->roots = $roots;
 		}
 		
		function getChildren() {
			$children = array();
			foreach($this->roots as $root)
				$children[] = new MollifyFolder($root);
			return $children;
		}
		
		function getName() {
			return "Mollify";
		}
	}
	
	class MollifyFolder extends Sabre_DAV_Directory {
		private $folder;

		function __construct($folder) {
			$this->folder = $folder;
 		}
 		
		function getChildren() {
			$children = array();
			foreach($this->folder->items() as $i)
				$children[] = $this->createItem($i);
			return $children;
		}
		
		function createItem($item) {
			if ($item->isFile()) return new MollifyFile($item);
			return new MollifyFolder($item);
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
		
		$auth = new Sabre_HTTP_DigestAuth();
		$auth->init();
		
		if (!$auth->getUsername()) {
			Logging::logDebug("DAV authentication missing");
			$auth->requireLogin();
			echo "Authentication required\n";
			die();
		}
		
		$user = $env->configuration()->getUserByName($auth->getUsername());
		if (!$user) {
			Logging::logDebug("DAV authentication failure");
			$auth->requireLogin();
			echo "Authentication required\n";
			die();
		}
		$env->authentication()->doAuth($user);

		$root = new MollifyRootFolder($env->filesystem()->getRootFolders());
		$dav = new Sabre_DAV_Server($root);
		$dav->setBaseUri($baseUri);
		$dav->exec();
	} catch (ServiceException $e) {
		Logging::logException($e);
	}
?>