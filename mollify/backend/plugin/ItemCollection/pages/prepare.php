<html>
	<head>
		<script type="text/javascript" language="javascript" src="<?php print $resourcesUrl ?>/jquery-1.4.2.min.js"></script>
		<style>
			#itemcollection-preparing, #itemcollection-downloading {
				font-size: 31.5px;
				margin: 20px;
				font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
				font-weight: bold;
				line-height: 40px;
				text-rendering: optimizelegibility;
			}
		</style>
		<script>
			$(document).ready(function() {
				var loc = window.location;
				var mobile = (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent));
				$.get(loc+"?ac=prepare", function(r) {
					$("#itemcollection-preparing").hide();
					$("#itemcollection-downloading").show();
					var url = loc + "?ac=download&id="+encodeURIComponent(r.result.id);
					if (mobile) url = url + "&m=1";
					window.location = url;
				});
			});
		</script>
	</head>
	<body>
		<div id="itemcollection-preparing">
		Preparing...
		</div>
		<div id="itemcollection-downloading" style="display:none">
		Downloading...
		</div>
	</body>
</html>