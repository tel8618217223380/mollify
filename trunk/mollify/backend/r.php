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

	require_once("include/Logging.class.php");
	require_once("include/Request.class.php");
	require_once("include/ResponseHandler.class.php");
	require_once("include/OutputHandler.class.php");
	
	$responseHandler = NULL;
	
	function globalErrorHandler($errno, $errstr, $errfile, $errline) {
		global $responseHandler;
		$info = "PHP error #".$errno.", ".$errstr." (".$errfile.":".$errline.")";
		Logging::logError($info);
		if ($responseHandler == NULL) $responseHandler = new ResponseHandler(new OutputHandler());
		$responseHandler->unknownServerError($info);
		die();
	}
	set_error_handler('globalErrorHandler');
	
	function globalExceptionHandler($e) {
		global $responseHandler;
		Logging::logException($e);
		if ($responseHandler == NULL) $responseHandler = new ResponseHandler(new OutputHandler());
		$responseHandler->unknownServerError($e->getMessage());
		die();
	}
	set_exception_handler('globalExceptionHandler');
	
	require_once("configuration.php");
	
	global $SETTINGS, $CONFIGURATION_PROVIDER;
	Logging::initialize($SETTINGS);

	require_once("include/MollifyBackend.class.php");
	require_once("include/ConfigurationProviderFactory.class.php");
	
	$responseHandler = new ResponseHandler(new OutputHandler(getSetting($SETTINGS, 'mime-types', array()), isSetting($SETTINGS, 'support_output_buffer')));
	try {
		$backend = new MollifyBackend($SETTINGS, $CONFIGURATION_PROVIDER, new ConfigurationProviderFactory(), $responseHandler);
		$request = new Request(isSetting($SETTINGS, 'enable_limited_http_methods'));
		$backend->processRequest($request);
	} catch (ServiceException $e) {
		Logging::logException($e);
		$responseHandler->error($e->type(), $e->details());
	} catch (Exception $e) {
		Logging::logException($e);
		$responseHandler->unknownServerError($e->getMessage());
	}

	function getSetting($settings, $name, $def) {
		if (!isset($settings) or !isset($settings[$name])) return $def;
		return $settings[$name];
	}
		
	function isSetting($settings, $name) {
		return isset($settings) and isset($settings[$name]) and $settings[$name] == TRUE;
	}
?>