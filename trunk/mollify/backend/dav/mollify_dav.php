<?php
	// DON'T MODIFY THIS FILE
	set_include_path('lib/'.PATH_SEPARATOR.$MOLLIFY_BACKEND_ROOT.PATH_SEPARATOR.get_include_path());
	
	require_once("configuration.php");
	require_once("include/Logging.class.php");
		
	global $SETTINGS, $CONFIGURATION_PROVIDER;
	Logging::initialize($SETTINGS);

	require_once("include/MollifyBackend.class.php");
	require_once("include/ConfigurationProviderFactory.class.php");
	require_once("include/Settings.class.php");
	require_once("Sabre/autoload.php");
	
	class VoidResponseHandler {
		public function addListener($l) {}
	}
	
	class Mollify_DAV_Root extends Sabre_DAV_Directory {
		private $controller;
		private $roots;
		
		function __construct($controller) {
			$this->controller = $controller;
			$this->roots = $this->controller->getRootFolders();
 		}
 		
		function getChildren() {
			$children = array();
			foreach($this->roots as $id=>$root)
				$children[] = new Mollify_DAV_Folder($this->controller, $root);
			return $children;
		}
		
		function getName() {
			return "Mollify";
		}
	}
	
	class Mollify_DAV_Folder extends Sabre_DAV_Directory {
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
			if ($item->isFile()) return new Mollify_DAV_File($this->controller, $item);
			return new Mollify_DAV_Folder($this->controller, $item);
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
		
		public function getLastModified() {
			return $this->folder->lastModified();
		}
	}

	class Mollify_DAV_File extends Sabre_DAV_File {
		private $controller;
		private $file;

		function __construct($controller, $file) {
			$this->controller = $controller;
			$this->file = $file;
		}

		public function getName() {
			return $this->file->name();
		}
		
		public function setName($name) {
			$this->controller->assertRights($this->file, Authentication::RIGHTS_WRITE, "rename");
			$this->file->rename($name);
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
		
		public function getLastModified() {
			return $this->file->lastModified();
		}
		
		public function getETag() {
			return null;
		}
	}
	
	try {
		$settings = new Settings($SETTINGS);
		$factory = new ConfigurationProviderFactory();
		$conf = $factory->createConfigurationProvider($CONFIGURATION_PROVIDER, $settings);
		$backend = new MollifyBackend($settings, $conf, new VoidResponseHandler());
		
		$env = $backend->env();
		$env->initialize();
		
		if (isset($BASIC_AUTH) and !$BASIC_AUTH) {
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
		} else {
			$auth = new Sabre_HTTP_DigestAuth();
			$auth->setRealm($env->authentication()->realm());
			$auth->init();
			$username = $auth->getUserName();
			
			if (!$username) {
				Logging::logDebug("DAV digest authentication missing");
				$auth->requireLogin();
				echo "Authentication required\n";
				die();
			}
			
			$user = $env->configuration()->getUserByName($username);
			
			if (!$user or !$auth->validateA1($user["a1password"])) {
				Logging::logDebug("DAV digest authentication failure");
				$auth->requireLogin();
				echo "Authentication required\n";
				die();
			}
			$env->authentication()->doAuth($user);
		}

		$dav = new Sabre_DAV_Server(new Mollify_DAV_Root($env->filesystem()));
		$dav->setBaseUri($BASE_URI);
		if ($ENABLE_LOCKING) $dav->addPlugin(new Sabre_DAV_Locks_Plugin(new Sabre_DAV_Locks_Backend_FS('data')));
		if ($ENABLE_BROWSER) $dav->addPlugin(new Sabre_DAV_Browser_Plugin());
		if ($ENABLE_TEMPORARY_FILE_FILTER) $dav->addPlugin(new Sabre_DAV_TemporaryFileFilterPlugin('temp'));
		$dav->addPlugin(new Sabre_DAV_Mount_Plugin());
		$dav->exec();
	} catch (ServiceException $e) {
		Logging::logException($e);
	} catch (Exception $e) {
		Logging::logException($e);
		throw $e;
	}
?>