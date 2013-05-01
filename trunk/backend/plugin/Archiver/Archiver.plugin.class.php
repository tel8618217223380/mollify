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

	require_once("ArchiveManager.class.php");
	
	class Archiver extends PluginBase {
		public function setup() {
			$this->addService("archiver", "ArchiverServices");
			$this->env->filesystem()->registerItemContextPlugin("plugin-archiver", $this);
			
			$compressor = $this->getSetting("compressor", NULL);
			$this->archiveManager = new ArchiveManager($this->env, $compressor);
		}
		
		public function getItemContextData($item, $details, $key, $data) {
			/*if ($item->isFile()) {
				$ext = $item->extension();
				// TODO tar etc

				if (strcasecmp("zip", $ext) != 0) return FALSE;
				
				return array(
					"action_extract" => "archive/".$item->id()."/extract"
				);
			} else {
				return array(
					"action_compress" => "archive/".$item->id()."/compress"
				);
			}*/
		}
		
		public function getArchiveManager() {
			return $this->archiveManager;
		}
		
		/*public function getClientPlugin() {
			return "client/plugin.js";
		}*/
		
		public function __toString() {
			return "ArchiverPlugin";
		}
	}
?>