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
	 			
	 class ArchiveExtractor {
		public function getItemDetails($item) {
			if (!$item->isFile()) return FALSE;
			
			$ext = $item->extension();
			if (strncasecmp("zip", $ext) != 0) return FALSE;
			
			return array("extract_archive" => TRUE);
		}
	}
?>