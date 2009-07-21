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

1) Unzip Mollify package (for example with command "unzip mollify_[VER].zip")

2) Copy extracted directory "mollify" into your web server root directory (or any other place of your choice)

3) Create configuration file called "configuration.php" under directory "mollify/backend", examples can be found in
directory "backend/example". For further instructions see wiki pages at Mollify project page (http://code.google.com/p/mollify).

4) You are ready to go. Open address "http://your.host.name/mollify" (if installed into root) to access your files.


2. UPGRADING
============

Upgrading Mollify version 0.9 to version 1.0 or later requires following steps:

1) Remove "org.sjarvela.mollify.App"
2) Backup "configuration.php" and "index.php" (if exists) under directory "mollify"
3) Remove directory "mollify"
4) Copy directory "mollify" from the latest package
5) Restore "configuration.php" into directory "mollify/backend", and "index.php" into directory "mollify"


Upgrading Mollify versions later than 1.0 requires following steps:

1) Backup "configuration.php" from directory "mollify/backend"
2) Remove directories "backend" and "client" under directory "mollify"
3) Copy directories "backend" and "client" from the latest package into directory "mollify"
4) Restore "configuration.php" into "mollify/backend"


With MySQL installations, also database may require an upgrade. Check this by opening installation script with at
"http://your.host.name/mollify/backend/install", and it will start upgrade if required.
 
------------------------------------------------------------------------------------------------------

For more information and instructions, visit Mollify homepage at http://www.jaervelae.com/mollify
or Mollify Google Code page at http://code.google.com/p/mollify