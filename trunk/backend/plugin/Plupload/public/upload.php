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
?>
<html>
	<head>
		<title>Upload</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<link rel="stylesheet" href="<?php echo $RESOURCE_URL ?>plupload.queue.css">
		<link rel="stylesheet" href="<?php echo $RESOURCE_URL ?>style.css">
		
		<script type="text/javascript" src="<?php echo $COMMON_RESOURCE_URL ?>jquery-1.4.2.min.js"></script>
		<script type="text/javascript" src="<?php echo $RESOURCE_URL ?>gears_init.js"></script>
		<script type="text/javascript" src="<?php echo $PLUPLOAD_RESOURCE_URL ?>plupload.full.js"></script>
		<script type="text/javascript" src="<?php echo $RESOURCE_URL ?>jquery.plupload.queue.min.js"></script>
		<script type="text/javascript" src="http://www.google.com/jsapi"></script>
		<script type="text/javascript" src="http://bp.yahooapis.com/2.4.21/browserplus-min.js"></script>
		<script type="text/javascript">
			$(function() {
			    $("#uploader").pluploadQueue({
			        runtimes : 'gears,flash,silverlight,browserplus,html5',
			        url : '<?php echo $UPLOAD_URL ?>',
			        chunk_size : '1mb',
			        flash_swf_url : '<?php echo $PLUPLOAD_RESOURCE_URL ?>plupload.flash.swf',
			        silverlight_xap_url : '<?php echo $PLUPLOAD_RESOURCE_URL ?>plupload.silverlight.xap'
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
							$('body').load("complete.html");
					});
					uploader.start();
			    });
			});
		</script>
	</head>

	<body>
		<div id="title">Upload file(s) to "<?php echo $FOLDER_NAME;?>":</div>
		<form>
		    <div id="uploader">
		        <p>Your browser does not support Flash, Silverlight, Gears, BrowserPlus or HTML5.</p>
		    </div>
		</form>
	</body>
</html>