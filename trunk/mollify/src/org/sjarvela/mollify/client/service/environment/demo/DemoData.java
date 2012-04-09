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

/*import java.util.ArrayList;
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
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
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
	private static final List<User> users = Arrays
			.asList(User.create("1", "Test User", UserPermissionMode.Admin),
					User.create("2", "Another Test User",
							UserPermissionMode.ReadWrite), User.create("3",
							"Third Test User", UserPermissionMode.ReadOnly));

	private static final List<UserGroup> groups = Arrays.asList(
			UserGroup.create("g1", "Group 1"),
			UserGroup.create("g2", "Group 2"));

	public static final String ROOT_1 = "r1";
	public static final String ROOT_2 = "r2";

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

	private JsArray<JsFolder> rootFolders;
	public Map<String, List<Folder>> folders = new HashMap();
	public List<File> files;

	private final UserPermissionMode permissionMode = UserPermissionMode.Admin;
	private final FeatureInfo settings;
	private final FileSystemInfo fileSystemInfo;

	public DemoData() {
		this.settings = FeatureInfo.create(true, true, true, true, true, true,
				true, true, true, true);
		this.fileSystemInfo = FileSystemInfo.create("/", 1024, 1024,
				Arrays.asList("txt", "gif"));

		createFoldersAndFiles();
	}

	public List<User> getUsers() {
		return users;
	}

	public List<UserGroup> getUserGroups() {
		return groups;
	}

	private void createFoldersAndFiles() {
		rootFolders = JavaScriptObject.createArray().cast();
		rootFolders.set(0, JsFolder.create(ROOT_1, "", "Folder A", ""));
		rootFolders.set(1, JsFolder.create(ROOT_2, "", "Folder B", ""));

		List<Folder> subDirs = new ArrayList();
		folders.put(ROOT_1, subDirs);

		subDirs.add(new Folder(DIR_1A, ROOT_1, "Sub folder A", "Sub folder A/",
				ROOT_1));
		subDirs.add(new Folder(DIR_1B, ROOT_1, "Sub folder B", "Sub folder B/",
				ROOT_1));

		subDirs = new ArrayList();
		folders.put(ROOT_2, subDirs);
		subDirs.add(new Folder(DIR_2A, ROOT_2, "Sub folder A", "Sub folder A/",
				ROOT_2));

		files = new ArrayList();
		files.add(new File(FILE_1A1, ROOT_1, "Example.txt", "Example.txt",
				"path", "txt", 128));
		files.add(new File(FILE_1A2, ROOT_1, "Picture.gif", "Picture.gif",
				"path", "gif", 2228));
		files.add(new File(FILE_1A3, ROOT_1, "Picture.png", "Picture.png",
				"path", "png", 64434));
		files.add(new File(FILE_1A4, ROOT_1, "Portable Document Format.pdf",
				"Portable Document Format.pdf", "path", "pdf", 113428));
		files.add(new File(FILE_1A5, ROOT_1, "Word Document.doc",
				"Word Document.doc", "path", "doc", 5634347));
		files.add(new File(FILE_1A6, ROOT_1, "Web page.html", "Web page.html",
				"path", "html", 23433231));
	}

	public SessionInfo getSessionInfo() {
		return getSessionInfo(null);
	}

	public SessionInfo getSessionInfo(String user) {
		if (user != null && user.length() > 0)
			return SessionInfo.create(true, true, "", "", user, user,
					permissionMode, settings, fileSystemInfo, rootFolders);
		return SessionInfo.create(true, false, "", "", "", "", permissionMode,
				settings, fileSystemInfo, rootFolders);
	}

	public List<Folder> getFolders(Folder dir) {
		if (!folders.containsKey(dir.getId()))
			return new ArrayList();
		return folders.get(dir.getId());
	}

	public List<File> getFiles(Folder dir) {
		return files;
	}

	public FolderDetails getFolderDetails(Folder directory) {
		return FolderDetails.create(FilePermission.ReadWrite, DESCRIPTION);
	}

	public FileDetails getFileDetails(File file) {
		String preview = "preview";
		JsObj view = new JsObjBuilder().string("embedded", "embedded-view")
				.string("full", "file-view.html").create();

		Date now = DateTime.getInstance().currentTime();
		return FileDetails.create(now, now, now, DESCRIPTION,
				FilePermission.ReadWrite, preview, view);
	}

}*/