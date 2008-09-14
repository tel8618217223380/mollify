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

2) Modify "user.php" under directory "mollify"

	Modify PHP array "roots" to return all the directories you wish to expose.
	- Name is the directory name visible to users, this can be freely named.
	- Path is the absolute path to the directory.
	
	NOTE! Currently all files and directories are recursively exposed, so carefully select your configuration.

3) You are ready to go. Open address http://your.host.name/mollify to see your files.



2. MODIFYING INSTALLATION
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