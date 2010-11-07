<?php
	class JPlayerViewer extends FullDocumentViewer {
		protected function getEmbeddedSize() {
			return array("450", "150");
		}
		
		protected function getHtml($item, $full) {
			$resourceUrl = $this->getResourceUrl();
			
			$head =
				'<script type="text/javascript" src="'.$this->env->getCommonResourcesUrl().'jquery-1.4.2.min.js"></script>'.
				'<script type="text/javascript" src="'.$resourceUrl.'jquery.jplayer.min.js"></script>'.
				'<link href="'.$resourceUrl.'jplayer.blue.monday.css" rel="stylesheet" type="text/css" />'.
				'<script>
					$(document).ready( function() {
						$("#player").jPlayer( {
							swfPath: "'.$resourceUrl.'",
							nativeSupport: false,
							ready: function () { this.element.jPlayer("setFile", "'.$this->getContentUrl($item).'").jPlayer("play"); }
						});
					});
				</script>';

			$html =
				'<div class="jp-single-player">
					<div class="jp-interface">
						<ul class="jp-controls">
							<li id="jplayer_play" class="jp-play">play</li>
							<li id="jplayer_pause" class="jp-pause">pause</li>
							<li id="jplayer_stop" class="jp-stop">stop</li>
							<li id="jplayer_volume_min" class="jp-volume-min">min volume</li>
							<li id="jplayer_volume_max" class="jp-volume-max">max volume</li>
						</ul>
						<div class="jp-progress">
							<div id="jplayer_load_bar" class="jp-load-bar">
								<div id="jplayer_play_bar" class="jp-play-bar"></div>
							</div>
						</div>
						<div id="jplayer_volume_bar" class="jp-volume-bar">
							<div id="jplayer_volume_bar_value" class="jp-volume-bar-value"></div>
						</div>
						<div id="jplayer_play_time" class="jp-play-time"></div>
						<div id="jplayer_total_time" class="jp-total-time"></div>
					</div>
					<div id="jplayer_playlist" class="jp-playlist">
						<ul>
							<li>'.$item->name().'</li>
						</ul>
					</div>
				</div>
				<div id="player"></div>';

			return "<html><head><title>".$item->name()."</title>".$head."</head><body>".$html."</body></html>";
		}
	}
?>