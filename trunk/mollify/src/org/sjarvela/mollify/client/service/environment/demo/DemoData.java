/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderDetails;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.session.FeatureInfo;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.session.file.FileSystemInfo;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserGroup;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;
import org.sjarvela.mollify.client.util.DateTime;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class DemoData {
	private static final List<User> users = Arrays.asList(User.create("1",
			"Test User", UserPermissionMode.Admin), User.create("2",
			"Another Test User", UserPermissionMode.ReadWrite), User.create(
			"3", "Third Test User", UserPermissionMode.ReadOnly));

	private static final List<UserGroup> groups = Arrays.asList(UserGroup
			.create("g1", "Group 1"), UserGroup.create("g2", "Group 2"));

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

	private static final String DESCRIPTION = "<b>Mollify demo</b><br/><br/>For downloads and instructions, visit <a href='http://code.google.com/p/mollify/' target='_new'>project page</a>";

	private JsArray<JsFolder> rootDirectories;
	private Map<String, List<Folder>> directories = new HashMap();
	private List<File> files;

	private final UserPermissionMode permissionMode = UserPermissionMode.Admin;
	private final FeatureInfo settings;
	private final FileSystemInfo fileSystemInfo;
	private final boolean multiUser;

	public DemoData(boolean multiUser) {
		this.multiUser = multiUser;
		this.settings = FeatureInfo.create(true, true, true, true, true, true,
				true, true, true, true, true);
		this.fileSystemInfo = FileSystemInfo.create(1024, 1024, Arrays.asList(
				"txt", "gif"));

		createDirectoriesAndFiles();
	}

	public List<User> getUsers() {
		return users;
	}

	public List<UserGroup> getUserGroups() {
		return groups;
	}

	private void createDirectoriesAndFiles() {
		rootDirectories = JavaScriptObject.createArray().cast();
		rootDirectories.set(0, JsFolder.create(ROOT_1, "", "Folder A", ""));
		rootDirectories.set(1, JsFolder.create(ROOT_2, "", "Folder B", ""));

		List<Folder> subDirs = new ArrayList();
		directories.put(ROOT_1, subDirs);

		subDirs.add(new Folder(DIR_1A, ROOT_1, "Sub folder A", "", ROOT_1));
		subDirs.add(new Folder(DIR_1B, ROOT_1, "Sub folder B", "", ROOT_1));

		subDirs = new ArrayList();
		directories.put(ROOT_2, subDirs);
		subDirs.add(new Folder(DIR_2A, ROOT_2, "Sub folder A", "", ROOT_2));

		files = new ArrayList();
		files.add(new File(FILE_1A1, ROOT_1, "Example.txt", "", "path", "txt",
				128));
		files.add(new File(FILE_1A2, ROOT_1, "Picture.gif", "", "path", "gif",
				2228));
		files.add(new File(FILE_1A3, ROOT_1, "Picture.png", "", "path", "png",
				64434));
		files.add(new File(FILE_1A4, ROOT_1, "Portable Document Format.pdf",
				"", "path", "pdf", 113428));
		files.add(new File(FILE_1A5, ROOT_1, "Word Document.doc", "", "path",
				"doc", 5634347));
		files.add(new File(FILE_1A6, ROOT_1, "Web page.html", "", "path",
				"html", 23433231));
	}

	public SessionInfo getSessionInfo() {
		return getSessionInfo(null);
	}

	public SessionInfo getSessionInfo(String user) {
		if (!multiUser) {
			return SessionInfo.create(false, false, "", "", "", "",
					permissionMode, settings, fileSystemInfo, rootDirectories);
		}

		if (user != null && user.length() > 0)
			return SessionInfo.create(true, true, "", "", user, user,
					permissionMode, settings, fileSystemInfo, rootDirectories);
		return SessionInfo.create(true, false, "", "", "", "", permissionMode,
				settings, fileSystemInfo, rootDirectories);
	}

	public List<Folder> getDirectories(Folder dir) {
		if (!directories.containsKey(dir.getId()))
			return new ArrayList();
		return directories.get(dir.getId());
	}

	public List<File> getFiles(Folder dir) {
		return files;
	}

	public FolderDetails getDirectoryDetails(Folder directory) {
		return FolderDetails.create(FilePermission.ReadWrite, DESCRIPTION);
	}

	public FileDetails getFileDetails(File file) {
		Date now = DateTime.getInstance().currentTime();
		return FileDetails.create(now, now, now, DESCRIPTION,
				FilePermission.ReadWrite);
	}

}
