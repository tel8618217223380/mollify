<?php
	class FileDescriptionDao {
		private $fileName;
		
		public function __construct($fileName) {
			$this->fileName = $fileName;
		}
		
		public function getItemDescription($item) {
			$descriptions = $this->readDescriptionsFromFile($this->getDescriptionFilename($item));
			
			if (!isset($descriptions[$item->name()])) return NULL;
			return $descriptions[$item->name()];
		}

		public function setItemDescription($item, $description) {
			$file = $this->getDescriptionFilename($item);
			$descriptions = $this->readDescriptionsFromFile($file);
			$descriptions[$item->name()] = $description;
			$this->writeDescriptionsToFile($file, $descriptions);
			return TRUE;
		}
	
		private function getDescriptionFilename($item) {
			return $item->dirName().DIRECTORY_SEPARATOR.$this->fileName;
		}
		
		private function readDescriptionsFromFile($descriptionFile) {
			$result = array();
			if (!file_exists($descriptionFile)) return $result;
		
			$handle = @fopen($descriptionFile, "r");
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open description file for reading: ".$descriptionFile);
			
		    while (!feof($handle)) {
		        $line = fgets($handle, 4096);
	
				// check for quote marks (")
				if (ord(substr($line, 0, 1)) === 34) {
					$line = substr($line, 1);
					$split = strpos($line, chr(34));
				} else {
		        	$split = strpos($line, ' ');
				}
				if ($split <= 0) continue;
	
				$name = trim(substr($line, 0, $split));
				$desc = str_replace('\n', "\n", trim(substr($line, $split + 1)));
				$result[$name] = $desc;
		    }
		    fclose($handle);
			
			return $result;
		}
		
		function writeDescriptionsToFile($file, $descriptions) {
			if (file_exists($file)) {
				if (!is_writable($file))
					throw new ServiceException("REQUEST_FAILED", "Could not open description file for writing: ".$file);
			} else {
				$dir = dirname($file);
				if (!is_writable($dir))
					throw new ServiceException("REQUEST_FAILED", "Could not write to the folder for description file: ".$file);
			}
		
			$handle = @fopen($file, "w");
			if (!$handle)
				throw new ServiceException("REQUEST_FAILED", "Could not open description file for writing: ".$file);
			
			foreach($descriptions as $name => $description)
				fwrite($handle, sprintf('"%s" %s', $name, str_replace("\n", '\n', $description))."\n");
	
			fclose($handle);
		}
	}
?>