function MainView() {
	var that = this;
	
	this.init = function(p) {
		that.roots = p.roots;
		that.listener = p.listener;
		
		that.rootsById = {};
		for (var i=0,j=p.roots.length; i<j; i++)
			that.rootsById[p.roots[i].id] = p.roots[i];
	}
	
	this.render = function(id) {
		mollify.dom.loadContent(id, mollify.templates.url("mainview.html"), that.onLoad, ['localize']);
	}
	
	this.onLoad = function() {
		$(window).resize(that.onResize);
		that.onResize();
		
		// TODO default view mode
		// TODO expose file urls
		mollify.dom.template("mollify-tmpl-main-username", mollify.env.session(), mollify).appendTo("#mainview-user");
		
		//setup roots
		if (that.roots) {
			//mollify.dom.template("mollify-tmpl-main-rootfolder", that.roots, mollify).appendTo("#mainview-root-folders");
			//$(".mollify-rootfolder").click(function() { that.onRootFolderSelected(that.rootsById[$(this).attr('id').substring(13)]); });
		}
		that.listener.onViewLoaded();
	}
	
	this.onResize = function() {
		$("#mainview-main").height($(window).height());
	}
	
	this.showAllRoots = function() {
		that.folder({ hierarchy: [], canWrite: false });
		that.data({ items: that.roots });
	}

	this.showNoRoots = function() {
		console.log("showNoRoots");
	}
		
	this.showProgress = function() {
		console.log("showProgress");
	}

	this.hideProgress = function() {
		console.log("hideProgress");
	}

	this.onFolderSelected = function(f) {
		that.listener.onSubFolderSelected(f);
	}
	
	this.getItem = function(id) {
		for (var i=0, j=that.items.length; i<j; i++)
			if (that.items[i].id == id) return that.items[i];
		return null;
	}
		
	this.folder = function(p) {
		mollify.dom.template("mollify-tmpl-main-folder", p.hierarchy[p.hierarchy.length-1]).appendTo($("#mainview-filelist").empty());
		that.setupHierarchy(p.hierarchy);
		
		//TODO canWrite
		$("#mollify-folderview-items").addClass("loading");
	}
	
	this.setupHierarchy = function(h) {
		var p = $("#mollify-folder-hierarchy").empty();
		
		mollify.dom.template("mollify-tmpl-main-folder-hierarchy", h).appendTo(p);
		$(".folder-hierarchy-item").click(function() {
			var $t = $(this);
			var id = $t.attr('id').substring(22);
			var index = p.find(".folder-hierarchy-item").index($t);
			
			/*var f = false;
			for (var i=0, j=h.length; i<j; i++) {
				if (h[i].id == id) {
					f = h[i];
					break;
				}
			}*/
			that.listener.onFolderSelected(index+1, h[index]);
		});
	}
	
	this.data = function(p) {
		that.items = p.items;
		
		$("#mollify-folderview-items").removeClass("loading");
		
		//TODO list/grid
		mollify.dom.template("mollify-tmpl-main-listitem", p.items).appendTo($("#mollify-folderview-items").empty());
		$(".item-folder").click(function() {
			var item = that.getItem($(this).attr('id').substring(22));
			that.listener.onSubFolderSelected(item);
		});
	}
}