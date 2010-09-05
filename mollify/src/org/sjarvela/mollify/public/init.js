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
	}
})();