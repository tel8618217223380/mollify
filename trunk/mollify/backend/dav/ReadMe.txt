Mollify DAV plugin is based on SabreDAV framework from Rooftop Solutions (http://code.google.com/p/sabredav/), which is released under New BSD License (http://www.opensource.org/licenses/bsd-license.php).

INSTALLATION
============

	Copy folder "dav" under Mollify backend folder, and continue with configuration.

CONFIGURATION
=============

1) WebDAV client URL

	Default URL for accessing files via WebDAV client is "http[s]://[host_ip]/[mollify_backend_path]/dav/".
	In the installation package, there is a rewrite rule in file ".htaccess" which applies only to Apache web servers.
	If this does not apply your server, or it is disabled, the url becomes "http[s]://[host_ip]/[mollify_backend_path]/dav/index.php/" (Note that the url always ends with "/").

	Modify "index.php" and update variable $BASE_URI according to your configuration. The value is the access url without protocol or host ip.

	For example, if mollify backend is located at "http://host/mollify/backend", the base uri should be "/mollify/backend/dav/" (assuming rewrite applies, "/mollify/backend/dav/index.php/" if not).

2) Script location

	If dav folder needs to be located somewhere else than under Mollify backend, modify "index.php" and update variables $MOLLIFY_BACKEND_ROOT and $BASE_URI (see previous chapter) accordingly.

3) Locking

	By default, dav is set up with locking support. Some clients don't require this (see http://code.google.com/p/sabredav/), and can be disabled by setting "$ENABLE_LOCKING = FALSE;" in index.php