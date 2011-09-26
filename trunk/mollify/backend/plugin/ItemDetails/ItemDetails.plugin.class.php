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
	
	class ItemDetails extends PluginBase {
		public function setup() {
			$this->env->filesystem()->registerDetailsPlugin("itemdetails", $this);
		}
		
		public function getItemDetails($item, $details, $data) {
			if ($data == NULL) return FALSE;
			return array(
				"size" => $item->size(),
				"last-modified" => $this->env->formatTimestampInternal($item->lastModified())
			);
		}
				
		public function __toString() {
			return "ItemDetailsPlugin";
		}
	}
?>
