/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;

public interface SelectDirectoryListener {

	void onSelect(Directory selected);

	boolean isDirectoryAllowed(Directory directory, List<Directory> path);

}
