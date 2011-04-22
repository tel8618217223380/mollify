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
	 			
	 class FilesystemSearcher extends BaseSearcher {
	 	private $env;
	 	private $searchDescriptions;
	 	
		function __construct($env) {
			$this->env = $env;
			$this->searchDescriptions = $env->features()->isFeatureEnabled("description_update");
		}
		
		protected function getMatch($data, $item, $text) {
			$result = array();
			if (stripos($item->name(), $text) !== FALSE)
				$result[] = "name";
			if ($this->searchDescriptions and in_array($item->id(), $data))
				$result[] = "description";
			return $result;
		}
		
		public function preData($parent, $text) {
			if (!$this->searchDescriptions) return NULL;
			$descMatches = $this->env->configuration()->findItemsWithDescription($parent, $text);
			return $descMatches;
		}
	}
?>