<?php
	class ResponseHandler {
		private static $ERRORS = array(
			"UNAUTHORIZED" => array(100, "Unauthorized request", 403), 
			"INVALID_REQUEST" => array(101, "Invalid request", 200),
			"FEATURE_DISABLED" => array(104, "Feature disabled", 200),
			"INVALID_CONFIGURATION" => array(105, "Invalid configuration", 200),
			"FEATURE_NOT_SUPPORTED" => array(106, "Feature not supported", 200),
			"AUTHENTICATION_FAILED" => array(107, "Authentication failed", 200),
		
			"INVALID_PATH" => array(201, "Invalid path", 200), 
			"FILE_DOES_NOT_EXIST" => array(202, "File does not exist", 200), 
			"DIR_DOES_NOT_EXIST" => array(203, "Directory does not exist", 200), 
			"FILE_ALREADY_EXISTS" => array(204, "File already exists", 200), 
			"DIR_ALREADY_EXISTS" => array(205, "Directory already exists", 200), 
			"NOT_A_FILE" => array(206, "Target is not a file", 200), 
			"NOT_A_DIR" => array(207, "Target is not a directory", 200), 
			"DELETE_FAILED" => array(208, "Could not delete", 200), 
			"NO_UPLOAD_DATA" => array(209, "No upload data available", 200), 
			"UPLOAD_FAILED" => array(210, "File upload failed", 200), 
			"SAVING_FAILED" => array(211, "Saving file failed", 200),
			"NO_MODIFY_RIGHTS" => array(212, "User has no rights to modify file", 200),
			"ZIP_FAILED" => array(213, "Creating a zip package failed", 200),
			"NO_GENERAL_WRITE_PERMISSION" => array(214, "User has no general read/write permission", 200),
			"NOT_AN_ADMIN" => array(215, "User is not an administrator", 200),
			
			"UNEXPECTED_ERROR" => array(999, "Unexpected error occurred", 200),
		);
		private $output;
		
		function __construct($output) {
			$this->output = $output;
		}
		
		public function success($data) {
			$this->output->sendResponse(new Response(200, "json", $this->getSuccessResponse($data)));
		}
		
		public function error($type, $details) {
			$error = $this->getError($type);
			$this->output->sendResponse(new Response($error[2], "json", $this->getErrorResponse($error, $details)));
		}
		
		public function unknownServerError($msg) {
			$this->output->sendResponse(new Response(500, "plain", $msg));
		}
		
		private function getSuccessResponse($data) {
			if (Logging::isDebug()) return array("success" => TRUE, "result" => $data, "trace" => Logging::getTrace());
			return array("success" => TRUE, "result" => $data);
		}
		
		private function getError($error) {
			if (array_key_exists($error, self::$ERRORS)) {
				return self::$ERRORS[$error];
			} else {
				return array(0, "Unknown error: ".$error);
			}			
		}
		
		private function getErrorResponse($err, $details) {
			if (Logging::isDebug()) return array("success" => FALSE, "code" => $err[0], "error" => $err[1], "details" => $details, "trace" => Logging::getTrace());
			return array("success" => FALSE, "code" => $err[0], "error" => $err[1], "details" => $details);
		}

	}
?>