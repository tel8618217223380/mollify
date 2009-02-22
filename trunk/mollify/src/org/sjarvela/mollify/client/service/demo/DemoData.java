/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.DateTime;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.DirectoryDetails;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileDetails;
import org.sjarvela.mollify.client.data.FilePermission;
import org.sjarvela.mollify.client.data.JsDirectory;
import org.sjarvela.mollify.client.data.JsFile;
import org.sjarvela.mollify.client.data.SessionInfo;
import org.sjarvela.mollify.client.data.SessionSettings;
import org.sjarvela.mollify.client.data.SessionInfo.PermissionMode;

import com.google.gwt.core.client.JsArray;

public class DemoData {
	private static final String ROOT_1 = "r1";
	private static final String ROOT_2 = "r2";

	private static final String DIR_1A = "r1a";
	private static final String DIR_1B = "r1b";
	private static final String DIR_2A = "r2a";

	private static final String FILE_1A1 = "1a1";
	private static final String FILE_1A2 = "1a2";
	private static final String FILE_1A3 = "1a3";
	private static final String FILE_1A4 = "1a4";
	private static final String FILE_1A5 = "1a5";
	private static final String FILE_1A6 = "1a6";

	private JsArray<JsDirectory> rootDirectories;
	private Map<String, JsArray<JsDirectory>> directories = new HashMap();
	private JsArray<JsFile> files;

	private PermissionMode permissionMode = PermissionMode.ReadWrite;
	private SessionSettings settings;

	public DemoData() {
		settings = SessionSettings.create(true, true, true, true);
		createDirectoriesAndFiles();
	}

	private void createDirectoriesAndFiles() {
		rootDirectories = JsArray.createArray().cast();
		rootDirectories.set(0, JsDirectory.create(ROOT_1, "Folder A"));
		rootDirectories.set(1, JsDirectory.create(ROOT_2, "Folder B"));

		JsArray<JsDirectory> subDirs = JsArray.createArray().cast();
		directories.put(ROOT_1, subDirs);

		subDirs.set(0, JsDirectory.create(DIR_1A, "Sub folder A"));
		subDirs.set(1, JsDirectory.create(DIR_1B, "Sub folder B"));

		subDirs = JsArray.createArray().cast();
		directories.put(ROOT_2, subDirs);
		subDirs.set(0, JsDirectory.create(DIR_2A, "Sub folder A"));

		files = JsArray.createArray().cast();
		files.set(0, JsFile.create(FILE_1A1, "Example.txt", "txt", 128));
		files.set(1, JsFile.create(FILE_1A2, "Example.gif", "gif", 2228));
		files.set(2, JsFile.create(FILE_1A3, "Example.png", "png", 64434));
		files.set(3, JsFile.create(FILE_1A4, "Example.pdf", "pdf", 113428));
		files.set(4, JsFile.create(FILE_1A5, "Example.doc", "doc", 5634347));
		files.set(5, JsFile.create(FILE_1A6, "Example.html", "html", 23433231));
	}

	public SessionInfo getSessionInfo(String user) {
		if (user != null && user.length() > 0)
			return SessionInfo.create(true, true, user, permissionMode,
					settings);
		return SessionInfo.create(true, false, "", permissionMode, settings);
	}

	public JsArray<JsDirectory> getRootDirectories() {
		return rootDirectories;
	}

	public JsArray<JsDirectory> getDirectories(String dir) {
		if (!directories.containsKey(dir))
			return JsArray.createArray().cast();
		return directories.get(dir);
	}

	public JsArray<JsFile> getFiles(String dir) {
		return files;
	}

	public DirectoryDetails getDirectoryDetails(Directory directory) {
		return DirectoryDetails.create(FilePermission.ReadWrite);
	}

	public FileDetails getFileDetails(File file) {
		Date now = DateTime.getInstance().currentTime();
		return FileDetails.create(now, now, now, "Mollify demo",
				FilePermission.ReadWrite);
	}
}
