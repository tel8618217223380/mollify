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
	
	$baseUri = "mollify/backend/dav/index.php/";
	
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
			return "";
		}
	}
	
	class MollifyFolder extends Sabre_DAV_Directory {
		private $folder;

		function __construct($folder) {
			$this->folder = $folder;
 		}
 		
		function getChildren() {
			$children = array();

			return $children;
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
			return $this->folder->name();
		}

		function get() {
			return $this->folder->read();
		}

		function getSize() {
			return $this->folder->size();
		}
	}
	
	try {
		$backend = new MollifyBackend($SETTINGS, $CONFIGURATION_PROVIDER, new ConfigurationProviderFactory(), new VoidResponseHandler());
		$env = $backend->env();
		$root = new MollifyRootFolder($env->filesystem()->getRootFolders());
		
		$dav = new Sabre_DAV_Server($root);
		$dav->setBaseUri($baseUri);
		$dav->exec();
	} catch (Exception $e) {
		Logging::logException($e);
	}
?>