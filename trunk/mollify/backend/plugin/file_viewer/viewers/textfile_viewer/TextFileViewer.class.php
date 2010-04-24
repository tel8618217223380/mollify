<?php
	class TextFileViewer extends ViewerBase {
		static $scripts = array(
			"as3" => "shBrushAS3.js",
			"bash" => "shBrushBash.js",
			"cf" => "shBrushColdFusion.js",
			"csharp" =>	"shBrushCSharp.js",
			"c" => "shBrushCpp.js",
			"css" => "shBrushCss.js",
			"pascal" => "shBrushDelphi.js",
			"diff" => "shBrushDiff.js",
			"erl" => "shBrushErlang.js",
			"groovy" => "shBrushGroovy.js",
			"js" => "shBrushJScript.js",
			"java" => "shBrushJava.js",
			"jfx" => "shBrushJavaFX.js",
			"perl" => "shBrushPerl.js",
			"php" => "shBrushPhp.js",
			"plain" => "shBrushPlain.js",
			"ps" => "shBrushPowerShell.js",
			"py" =>	"shBrushPython.js",
			"rails" => "shBrushRuby.js",
			"scala" => "shBrushScala.js",
			"sql" => "shBrushSql.js",
			"vb" => "shBrushVb.js",
			"xml" => "shBrushXml.js"
		);
		
		public function getInfo($item) {
			return array(
				"embedded" => $this->getDataUrl($item, "embedded"),
				"full" => $this->getDataUrl($item, "view", TRUE)
			);
		}
		
		public function processDataRequest($item, $path) {
			if (count($path) != 1) throw $this->invalidRequestException();
			
			if ($path[0] === 'view')
				$this->processViewRequest($item);
			else if ($path[0] === 'embedded')
				$this->processEmbeddedViewRequest($item);
			else
				throw $this->invalidRequestException();
		}
		
		private function processEmbeddedViewRequest($item) {
			$html = '<iframe id="text-file-viewer" src="'.$this->getDataUrl($item, "view", TRUE).'" style="border: none;"></iframe>';
			
			$this->response()->success(array(
				"html" => $html,
				"resized_element_id" => "text-file-viewer",
				"size" => "600;400"
			));
		}
		
		private function processViewRequest($item) {
			$resourceUrl = $this->getResourceUrl();
			$syntax = $this->getSyntax($item);
			$settings = $this->getSettings();
			
			$theme = "shThemeDefault.css";
			if (isset($settings["style"])) $theme = $settings["style"];
			
			$head = '<script type="text/javascript" src="'.$resourceUrl.'shCore.js"></script>'.
					'<script type="text/javascript" src="'.$resourceUrl.self::$scripts[$syntax].'"></script>'.
					'<link type="text/css" rel="stylesheet" href="'.$resourceUrl.'/styles/shCore.css" />'.
					'<link type="text/css" rel="stylesheet" href="'.$resourceUrl.'/styles/'.$theme.'"/>';
					
			$html = '<script type="syntaxhighlighter" class="brush: '.$syntax.'"><![CDATA[';

			// read file			
			$stream = $item->read();
			while (!feof($stream))
				$html .= fread($stream, 1024);
			fclose($stream);

			$html .= ']]></script><script type="text/javascript">SyntaxHighlighter.all()</script>';
			
			$this->response()->html("<html><head><title>".$item->name()."</title>".$head."</head><body>".$html."</body></html>");
		}
		
		private function getSyntax($item) {
			$ext = $item->extension();
			
			if ($ext === 'as3') return "as3";
			if ($ext === 'js') return "js";
			if ($ext === 'php') return "php";
			if ($ext === 'css') return "css";
			if ($ext === 'sh') return "bash";
			if ($ext === 'cpp' or $ext === 'c') return "c";
			if ($ext === 'java') return "java";
			if ($ext === 'sql') return "sql";
			if ($ext === 'xml' or $ext === 'xhtml' or $ext === 'xslt' or $ext === 'html') return "xml";
			if ($ext === 'py') return "py";
			
			// fallback to plain format
			return "plain";
		}
	}
?>