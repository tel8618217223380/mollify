<?php

	/**
	 * Copyright (c) 2008- Samuli J�rvel�
	 *
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	 * this entire header must remain intact.
	 */

	class TextFileViewer extends ViewerBase {
	
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
			$resourceUrl = $this->getResourceUrl("syntaxhilighter");
			$syntax = $this->getSyntax($item);
			
			$head = '<script type="text/javascript" src="'.$resourceUrl.'shCore.js"></script>'.
					'<script type="text/javascript" src="'.$resourceUrl.$this->getScript($syntax).'"></script>'.
					'<link href="'.$resourceUrl.'/styles/shCore.css" rel="stylesheet" type="text/css" />'.
					'<link type="text/css" rel="Stylesheet" href="'.$resourceUrl.'/styles/shThemeDefault.css"/>';
					
			$html = '<script type="syntaxhighlighter" class="brush: '.$syntax.'"><![CDATA[';

			// read file			
			$stream = $item->read();
			while (!feof($stream))
				$html .= fread($stream, 1024);
			fclose($stream);
			
			$html .= ']]></script><script type="text/javascript">SyntaxHighlighter.all()</script>';
;			
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
			
			return "plain";
		}
		
		private function getScript($syntax) {
			$scripts = array(
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
			return $scripts[$syntax];
		}	
	}
?>