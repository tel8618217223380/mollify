(function(){
	window.mollify = new function(){
		var t = this;
		this.settings = {};
		this.plugins = [];

		this.init = function(s) {
			t.settings = s;
			if (s.plugins) {
				for (var i=0; i < s.plugins.length; i++)
					t.registerPlugin(s.plugins[i]);
			}
		}
		
		this.setup = function(e) {
			t.env = e;
		}
		
		this.getSettings = function() {
			return t.settings;
		}

		this.getPlugins = function() {
			return t.plugins;
		}
		
		this.registerPlugin = function(p) {
			t.plugins.push(p);
		}

		this.importScript = function(url) {
			$.getScript(url);
		}
		
		this.importCss = function(url) {
			var link = $("<link>");
			link.attr({
		    	type: 'text/css',
		    	rel: 'stylesheet',
		    	href: url
			});
			$("head").append(link);
		}
		
		this.loadContent = function(id, url, cb) {
			$("#"+id).load(url, function() {
				t.localize(id);
				if (cb) cb();
			});
		}
		
		this.localize = function(id) {
			$("#"+id+" text").each(function(){
				var key = $(this).attr('key');
				$(this).text(t.env.texts().get(key));
			});
		}
		
		this.texts = new function(){
			var tt = this;
			this.locale = '';
			this.values = null;
			
			this.set = function(id, values) {
				tt.locale = id;
				tt.values = values;
			}
			
			this.add = function(id, values) {
				//TODO handle different locale
				for (v in values) {
					tt.values[v] = values[v];
				}
			}
		}
	}
})();