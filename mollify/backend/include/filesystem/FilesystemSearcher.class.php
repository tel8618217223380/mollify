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
		protected function getMatch($item, $text) {
			if (stripos($item->name(), $text) !== FALSE)
				return "name";
			return NULL;
		}
	}
?>