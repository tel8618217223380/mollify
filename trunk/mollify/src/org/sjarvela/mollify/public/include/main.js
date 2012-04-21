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
			mollify.dom.template("mollify-tmpl-main-rootfolder", that.roots, mollify).appendTo("#mainview-root-folders");
			$(".mollify-rootfolder").click(function() { that.onRootFolderSelected(that.rootsById[$(this).attr('id').substring(13)]); });
		}
		that.listener.onViewLoaded();
	}
	
	this.onResize = function() {
		$("#mainview-main").height($(window).height());
	}
	
	this.showAllRoots = function() {
		console.log("showAllRoots");
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
	
	this.onRootFolderSelected = function(r) {
		that.listener.onRootFolderSelected(r);
	} 
	
	this.folder = function(p) {
		mollify.dom.template("mollify-tmpl-main-folder", p.hierarchy[p.hierarchy.length-1]).appendTo($("#mainview-filelist").empty());
		mollify.dom.template("mollify-tmpl-main-folder-hierarchy", p.hierarchy).appendTo($("#mollify-folder-hierarchy").empty());
		$("#mollify-folderview-items").addClass("loading");
	}
	
	this.data = function(p) {
		$("#mollify-folderview-items").removeClass("loading");
		
		//TODO list/grid
		mollify.dom.template("mollify-tmpl-main-listitem", p.items).appendTo($("#mollify-folderview-items").empty());
	}
}