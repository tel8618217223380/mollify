/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.localization;

import com.google.gwt.i18n.client.Messages;

public interface MessageConstants extends Messages {
	String sizeOneByte();

	String sizeInBytes(long bytes);

	String sizeOneKilobyte();

	String sizeInKilobytes(double kilobytes);

	String sizeInMegabytes(double megabytes);

	String confirmFileDeleteMessage(String name);

	String confirmDirectoryDeleteMessage(String name);

	String uploadingNFilesInfo(int amount);

	String uploadMaxSizeHtml(String fileMax, String totalMax);

	String copyFileMessage(String name);

	String moveFileMessage(String name);

	String moveDirectoryMessage(String name);
	
	String userDirectoryListDefaultName(String defaultName);

	String fileUploadDialogUnallowedFileType(String extension);

}
