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

	date_default_timezone_set("Europe/Helsinki");
	require_once("include/Logging.class.php");
	require_once("include/Request.class.php");
	require_once("include/ResponseHandler.class.php");
	require_once("include/OutputHandler.class.php");
	require_once("include/Version.info.php");
	
	$responseHandler = NULL;
	
	function globalErrorHandler($errno, $errstr, $errfile, $errline) {
		global $responseHandler;
		$info = "PHP error #".$errno.", ".$errstr." (".$errfile.":".$errline.")";
		Logging::logError($info."\n".Util::array2str(debug_backtrace()));
		if ($responseHandler == NULL) $responseHandler = new ResponseHandler(new OutputHandler());
		$responseHandler->unknownServerError($info);
		die();
	}
	set_error_handler('globalErrorHandler');
	
	function globalExceptionHandler($e) {
		global $responseHandler;
		Logging::logException($e);
		Logging::logDebug(Util::array2str(debug_backtrace()));
		if ($responseHandler == NULL) $responseHandler = new ResponseHandler(new OutputHandler());
		$responseHandler->unknownServerError($e->getMessage());
		die();
	}
	set_exception_handler('globalExceptionHandler');
	
	require_once("configuration.php");
	
	global $SETTINGS, $VERSION;
	Logging::initialize($SETTINGS, $VERSION);

	require_once("include/MollifyBackend.class.php");
	require_once("include/Settings.class.php");
		
	$responseHandler = new ResponseHandler(new OutputHandler(getSetting($SETTINGS, 'mime_types', array()), isSetting($SETTINGS, 'support_output_buffer')));
	try {
		$settings = new Settings($SETTINGS);
		$backend = new MollifyBackend($settings, getDB($settings), $responseHandler);
		$request = new Request(isSetting($SETTINGS, 'enable_limited_http_methods'));
		$backend->processRequest($request);
	} catch (ServiceException $e) {
		Logging::logException($e);
		$responseHandler->error($e->type(), $e->details(), $e->data());
	} catch (Exception $e) {
		Logging::logException($e);
		$responseHandler->unknownServerError($e->getMessage());
	}
	
	function getDB($settings) {
		require_once("db/DBConnectionFactory.class.php");
		$f = new DBConnectionFactory();
		return $f->createConnection($settings);
	}

	function getSetting($settings, $name, $def) {
		if (!isset($settings) or !isset($settings[$name])) return $def;
		return $settings[$name];
	}
		
	function isSetting($settings, $name) {
		return isset($settings) and isset($settings[$name]) and $settings[$name] == TRUE;
	}
?>