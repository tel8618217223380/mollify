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
	 
	$PATH = "../../../";
	
	if (!file_exists($PATH."configuration.php")) die("Mollify not configured");
	if (!isset($_GET["id"])) die();
	
	$id = $_GET["id"];
?>
<html>
	<head>
		<title>Upload</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<style type="text/css">@import url(resources/plupload.queue.css);</style>
		<link rel="stylesheet" href="resources/style.css">
		
		<script type="text/javascript" src="<?php echo $PATH ?>resources/jquery-1.4.2.min.js"></script>
		<script type="text/javascript" src="http://www.google.com/jsapi"></script>
		<script type="text/javascript" src="resources/gears_init.js"></script>
		<script type="text/javascript" src="http://bp.yahooapis.com/2.4.21/browserplus-min.js"></script>
		<script type="text/javascript" src="resources/plupload.full.min.js"></script>
		<script type="text/javascript" src="/plupload/js/jquery.plupload.queue.min.js"></script>
		<script type="text/javascript">
			$(function() {
			    $("#uploader").pluploadQueue({
			        runtimes : 'gears,flash,silverlight,browserplus,html5',
			        url : '<?php echo $PATH ?>r.php/guest/upload/?id=<?php echo $id ?>',
			        chunk_size : '1mb',
			        flash_swf_url : 'resources/plupload.flash.swf',
			        silverlight_xap_url : 'resources/plupload.silverlight.xap'
			    });
			
			    $('form').submit(function(e) {
					e.preventDefault();
					
					var uploader = $('#uploader').pluploadQueue();
			        if (uploader.total.uploaded != 0) return;
			        
			        if (uploader.files.length == 0) {
			        	alert('You must select at least one file.');
			        	return;
			        }
					
					uploader.bind('UploadProgress', function() {
						if (uploader.total.uploaded == uploader.files.length)
							$('body').html("<div class='complete'>Upload complete</div>");
					});
					uploader.start();
			    });
			});
		</script>
	</head>

	<body>
		<form>
		    <div id="uploader">
		        <p>You browser doesn't have Flash, Silverlight, Gears, BrowserPlus or HTML5 support.</p>
		    </div>
		    <input type="submit" value="Start" />
		</form>
	</body>
</html>