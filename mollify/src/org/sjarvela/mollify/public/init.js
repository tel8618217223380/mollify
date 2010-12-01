(function(){
	window.mollify = new function(){
		var t = this;
		this.plugins = [];
		this.locale_id = '';
		this.text_values = null;

		this.getPlugins = function() {
			return t.plugins;
		}
		
		this.registerPlugin = function(p) {
			t.plugins.push(p);
		}
		
		this.getTexts = function() {
			return t.text_values;
		}
		
		this.getLocale = function() {
			return t.locale_id;
		}
		
		this.setTexts = function(id, values) {
			t.locale_id = id;
			t.text_values = values;
		}
	}
})();