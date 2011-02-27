<?php
/**
 * Based on upload.php by Moxiecode
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under GPL License.
 *
 * License: http://www.plupload.com/license
 * Contributing: http://www.plupload.com/contributing
 */

function plupload($folder, $uploadHandler) {
	// Settings
	$targetDir = ini_get("upload_tmp_dir").DIRECTORY_SEPARATOR."mollify".DIRECTORY_SEPARATOR;
	$cleanupTargetDir = false; // Remove old files
	$maxFileAge = 60 * 60; // Temp file age in seconds

	// 5 minutes execution time
	@set_time_limit(5 * 60);
	// usleep(5000);

	// Get parameters
	$chunk = isset($_REQUEST["chunk"]) ? $_REQUEST["chunk"] : 0;
	$chunks = isset($_REQUEST["chunks"]) ? $_REQUEST["chunks"] : 0;
	$fileName = isset($_REQUEST["name"]) ? $_REQUEST["name"] : '';

	// Clean the fileName for security reasons
	$fileName = preg_replace('/[^\w\._]+/', '', $fileName);
	
	// Make sure the fileName is unique but only if chunking is disabled
	if ($chunks < 2 && file_exists($targetDir . DIRECTORY_SEPARATOR . $fileName)) {
		$ext = strrpos($fileName, '.');
		$fileName_a = substr($fileName, 0, $ext);
		$fileName_b = substr($fileName, $ext);

		$count = 1;
		while (file_exists($targetDir . DIRECTORY_SEPARATOR . $fileName_a . '_' . $count . $fileName_b))
			$count++;

		$fileName = $fileName_a . '_' . $count . $fileName_b;
	}
	
	// Create target dir
	if (!file_exists($targetDir))
		@mkdir($targetDir);

	// Remove old temp files
	if (is_dir($targetDir) && ($dir = opendir($targetDir))) {
		while (($file = readdir($dir)) !== false) {
			$filePath = $targetDir . DIRECTORY_SEPARATOR . $file;

			// Remove temp files if they are older than the max age
			if (preg_match('/\\.tmp$/', $file) && (filemtime($filePath) < time() - $maxFileAge))
				@unlink($filePath);
		}

		closedir($dir);
	} else
		throw new ServiceException("UPLOAD_FAILED", "Failed to open temp directory.");

	// Look for the content type header
	if (isset($_SERVER["HTTP_CONTENT_TYPE"]))
		$contentType = $_SERVER["HTTP_CONTENT_TYPE"];

	if (isset($_SERVER["CONTENT_TYPE"]))
		$contentType = $_SERVER["CONTENT_TYPE"];
	
	$file = $targetDir.$fileName;
	Logging::logDebug("Uploading to ".$file." (".$chunk."/".$chunks.")");
	Logging::logDebug("Content type: ".$contentType);
	
	if (strpos($contentType, "multipart") !== false) {
		if (isset($_FILES['file']['tmp_name']) and is_uploaded_file($_FILES['file']['tmp_name'])) {
			$from = $_FILES['file']['tmp_name'];
			$in = fopen($from, "rb");
			if (!$in) throw new ServiceException("UPLOAD_FAILED", "Failed to open input stream");
			
			Logging::logDebug("Reading from file ".$from);						
			$uploadHandler->uploadFrom($folder, $fileName, $in);
			fclose($in);
			
			Logging::logDebug("Upload finished, removing temp files");
			@unlink($from);
			@unlink($file);
		} else {
			throw new ServiceException("UPLOAD_FAILED", "Failed to move uploaded file.");
		}
	} else {
		Logging::logDebug("Reading from chunk ".$chunk."/".$chunks);
		
		$out = fopen($file, $chunk == 0 ? "wb" : "ab");
		if (!$out) throw new ServiceException("UPLOAD_FAILED", "Failed to open output stream.");
		
		$in = fopen("php://input", "rb");
		if (!$in) throw new ServiceException("UPLOAD_FAILED", "Failed to open input stream.");
		
		while ($buff = fread($in, 4096))
			fwrite($out, $buff);
		
		fclose($out);
		fclose($in);
		
		if ($chunks == 0 or $chunk == ($chunks-1)) {
			$f = fopen($file, "rb");
			$uploadHandler->uploadFrom($folder, $fileName, $f);
			fclose($f);
			Logging::logDebug("Upload finished");
		}
	}
}
?>