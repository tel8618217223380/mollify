(function(){
	window.mollify = new function(){
		var t = this;
		this.plugins = [];

		this.getPlugins = function() {
			return t.plugins;
		}
		
		this.registerPlugin = function(p) {
			t.plugins.push(p);
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
		
		this.loadContent = function(id, url, t, cb) {
			$("#"+id).load(url, function() {
				$("#"+id+" text").each(function(){
					var key = $(this).attr('key');
					$(this).text(t(key));
				});
				if (cb) cb();
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