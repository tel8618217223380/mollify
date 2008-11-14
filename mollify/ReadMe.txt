Mollify

Copyright (c) 2008- Samuli Järvelä

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
this entire header must remain intact.

------------------------------------------------------------------------------------------------------

1. INSTALLATION
===============

1) Untar package contents

	tar -xzvf mollify_VER.tar.gz

	This will extract two directories:
	- "mollify": Application host page and backend service files
	- "org.sjarvela.mollify.App": Client application files

2) Copy extracted directories into your web server root directory

3) Create configuration file "configuration.php" under directory "mollify", for further instructions see chapter 2.

4) You are ready to go. Open address http://your.host.name/mollify to access your files.


2. CONFIGURATION
================

Backend file "configuration.php" contains all configuration properties needed by Mollify. Configuration depends
on whether it is for single-user or multi-user environment.

Installation package has a folder named "example", which contains example configuration file for both scenarios. Copy one of them
to directory "mollify" with name "configuration.php", and modify it to match your system.


2.1 USERS
---------

Mollify supports both single-user and multi-user environments. In single-user environment, no authentication is required and all access rules apply to everybody. In multi-user environment different user accounts are set up, where different users can have different published directories and different access permissions.

2.1.1 Single-user environment
-----------------------------

To set up a single-user environment, make sure variable $USERS in file "configuration.php" is empty or not defined.

For example:
	$USERS = array();


2.1.2 Multi-user environment
----------------------------

To set up a multi-user environment, you have to define user accounts with variable $USERS in file "configuration.php".

Syntax is:
	$USERS = array(
		[USER_ID] => array("name" => [USER NAME], "password" => [PASSWORD]),
		...
	);

For example:
	$USERS = array(
		"1" => array("name" => "User 1", "password" => "foo"),
		"2" => array("name" => "User 2", "password" => "bar")
	);

This configuration has two users, "User 1" and "User 2".

Rules for user configuration:
- User id's [USER_ID] can be freely chosen, but they must be unique (as all user related data is linked
  with user id)
- User names [USER NAME] can be freely chosen, but they must be unique (as user is identified with user
  name when logged in)


2.2 PUBLISHED DIRECTORIES
-------------------------

Mollify supports freely selectable published directories, which need not to have any relation with each other.
Also, in multi-user environment, each user can have different set of directories available.

2.2.1 Single-user environment
-----------------------------

In single-user environment (see chapter 2.1, Users), use following format to define published directories:

	$PUBLISHED_DIRECTORIES = array(
		array(
			[DIR_ID] => array("name" => [DIR_NAME], "path" => [DIR_PATH]),
			...
		)
	);

See rules below.

2.2.2 Multi-user environment
----------------------------

In multi-user environment (see chapter 2.1, Users), use following format to define published directories:

	$PUBLISHED_DIRECTORIES = array(
		[USER_ID] => array(
			[DIR_ID] => array("name" => [DIR_NAME], "path" => [DIR_PATH]),
			...
		),
		...
	);

For example:

	$PUBLISHED_DIRECTORIES = array(
		"1" => array(
			"r1" => array("name" => "Folder A", "path" => "/foo/bar"),
			"r2" => array("name" => "Folder B", "path" => "/foo/bay")
		),
		"2" => array(
			"r1" => array("name" => "Folder A", "path" => "/foo/bat"),
			"r2" => array("name" => "Folder C", "path" => "/foo/baz")
		)
	);

With this configuration
- User with id "1" (defined in user configuration) has two visible directories: "Folder A" and "Folder B"
- User with id "2" (defined in user configuration) has two visible directories: "Folder A" and "Folder C"
- Although "Folder A" exists in both users, they may point to different physical folder

Rules are:
- User id [USER_ID] must exist in user configuration (see chapter 2.1)
- Directory id's [DIR_ID] can be freely chosen, but they must be unique within user (two users can have same id's)
- Path [DIR_PATH] must be absolute path in local file system
- Actual file system folder name is not shown to user, but instead the name [DIR_NAME] defined in the configuration

NOTE! Currently all files and directories are recursively exposed, so carefully select your configuration.


2.3 FILE UPLOAD
---------------

2.3.1 Disabling file upload
---------------------------

By default, file upload feature is enabled. To disable this feature, use following setting in "configuration.php":

	$SETTINGS = array(
		"enable_file_upload" => FALSE
	);

2.3.2 Enabling file upload progress
-----------------------------------

By default, file upload progress is disabled. To enable this feature, use following setting in "configuration.php":

	$SETTINGS = array(
		"enable_file_upload_progress" => TRUE
	);

NOTE! File upload progress display requires APC (Alternative PHP Cache), see wiki page at
"http://code.google.com/p/mollify/wiki/HowToEnableUploadProgressDisplay" for more information



3. MODIFYING INSTALLATION
=========================

1) Moving host page

	Host page can be located anywhere, the only thing that matters is that backend interface files
	(*.php) are located in the same directory.

2) Moving client application files

	Client application files can be located anywhere, only the javascript reference in the host page
	must be updated. While the location and the name of the folder "org.sjarvela.mollify.App" can be
	changed, do not alter its contents.

3) Customizing Mollify host page, or integrating Mollify into existing web page

	Host page structure can be freely modified, as long as following rules are met:
	- Javascript file "org.sjarvela.mollify.App.nocache.js" must be linked
	- In the page, there must be a div with id "mollify". Application is injected inside this element.
	
	Optionally
	- If you wish to choose the language, include meta information in the head section
	- If you wish to support browser history, keep the iframe element with id "__gwt_historyFrame"

------------------------------------------------------------------------------------------------------

For more information and instructions, visit Mollify homepage at http://www.jaervelae.com/mollify
or Mollify Google Code page at http://code.google.com/p/mollify