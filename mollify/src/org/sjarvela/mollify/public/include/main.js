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
		var s = mollify.session.get();
		mollify.dom.template("mollify-tmpl-main-username", s, mollify).appendTo("#mainview-user");
		if (s.authenticated) mollify.ui.controls.hoverDropdown($('#mollify-username-dropdown'), that.sessionActions());
		
		that.listener.onViewLoaded();
	}
	
	this.onResize = function() {
		$("#mainview-main").height($(window).height());
	}
	
	this.sessionActions = function() {
		return [
			{'title-key': 'logout', callback: mollify.session.actions.logout}
		];
	}
	
	this.showAllRoots = function() {
		that.folder();
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
	
	this.folder = function(p) {
		var $t = $("#mainview-content").empty();
		if (p) {
			mollify.dom.template("mollify-tmpl-main-folder", p.hierarchy[p.hierarchy.length-1]).appendTo($t);
			that.setupHierarchy(p.hierarchy);
			
			//TODO canWrite
			$("#mollify-folderview-items").addClass("loading");
		} else {
			mollify.dom.template("mollify-tmpl-main-rootfolders").appendTo($t);
			//TODO disable write
		}
		mollify.ui.process($t, ['localize']);
	}
	
	this.setupHierarchy = function(h) {
		var p = $("#mollify-folder-hierarchy").empty();
		
		mollify.dom.template("mollify-tmpl-main-folder-hierarchy", h).appendTo(p);
		$(".folder-hierarchy-item").click(function() {
			var index = p.find(".folder-hierarchy-item").index($(this));
			that.listener.onFolderSelected(index+1, h[index]);
		});
	}
	
	this.data = function(p) {
		that.items = p.items;
		
		$("#mollify-folderview-items").removeClass("loading");
		
		//TODO list/grid
		that.itemWidget = new FileList('mollify-folderview-items', 'main', mollify.settings["list-view-columns"]);
		that.itemWidget.init(p.items, {
			onFolderSelected : that.listener.onSubFolderSelected,
			onMenuOpen : function(item, e) {
				that.listener.getItemActions(item, function(a) { that.showActionMenu(item, a, e); });
			}
		});
	}
	
	this.showActionMenu = function(item, actions, c) {
		if (!actions) return;
		mollify.ui.controls.popupmenu(actions, { control: c }, function() { that.itemWidget.removeHover(); });
	}
}

function FileList(container, id, columns) {
	var t = this;
	t.minColWidth = 75;
	t.$c = $("#"+container);
	t.listId = 'mollify-filelist-'+id;
	t.cols = [];
	for (var colId in columns) {
		var col = mollify.ui.filelist.columns[colId];
		if (!col) continue;
		t.cols.push(col);
	}
	
	this.init = function(items, p) {
		t.p = p;
		mollify.dom.template("mollify-tmpl-filelist", {listId: t.listId}).appendTo(t.$c.empty());
		t.$l = $("#"+t.listId);
		t.$h = $("#"+t.listId+"-header-cols");
		t.$i = $("#"+t.listId+"-items");
		
		mollify.dom.template("mollify-tmpl-filelist-headercol", t.cols, {
			title: function(c) {
				return mollify.ui.texts.get(c['title-key']);
			} 
		}).appendTo(t.$h);
		
		t.$h.find(".mollify-filelist-col-header").each(function(i) {
			if (i == (t.cols.length-1)) return;
			var $t = $(this);
			$t.css("min-width", t.minColWidth);
			var ind = $t.index(); 
			$t.resizable({
				handles: "e",
				minWidth: t.minColWidth,
				//autoHide: true,
				start: function(e, ui) {
					var max = t.$c.width() - (t.cols.length * t.minColWidth);
					$t.resizable("option", "maxWidth", max);
				},
				stop: function(e, ui) {
					var w = $t.width();
					$(".mollify-filelist-col-"+t.cols[ind].id).width(w);
				}
			});/*.draggable({
				axis: "x",
				helper: "clone",
				revert: "invalid",
				distance: 30
			});*/
		});
		
		t.items(items);
	}
	
	this.items = function(items) {
		t.items = items;
		
		mollify.dom.template("mollify-tmpl-filelist-item", items, {
			typeClass : function(item) {
				var c = item.is_file ? 'item-file' : 'item-folder';
				if (item.is_file && item.extension) c += ' item-type-'+item.extension;
				else if (!item.is_file && item.id == item.root_id) c += ' item-root-folder';
				return c;
			},
			cols : function(item) {
				var html = '';
				for (var i=0, j=t.cols.length; i<j; i++)
					html += t.columnContent(item, t.cols[i]);
				return html;
			}
		}).appendTo(t.$i.empty());
		
		t.$i.find(".mollify-filelist-item").hover(function() {
			$(this).addClass("hover");
		}, function() {
			$(this).removeClass("hover");
		});
		
		t.$i.find(".mollify-filelist-quickmenu").click(function(e) {
			e.preventDefault();
			var $t = $(this);
			t.p.onMenuOpen($t.tmplItem().data, $t);
		});
		
		t.$i.find(".item-folder .mollify-filelist-item-name-title").click(function(e) {
			e.preventDefault();
			t.p.onFolderSelected($(this).tmplItem().data);
		});

	}
	
	this.removeHover = function() {
		t.$i.find(".mollify-filelist-item.hover").removeClass('hover');
	}
	
	this.columnContent = function(item, col) {
		var content = col.content(item);
		return '<div class="mollify-filelist-col mollify-filelist-col-'+col.id+'">' + content + '</div>';
	}
	
	/*this.item = function(id) {
		for (var i=0, j=t.items.length; i<j; i++)
			if (t.items[i].id == id) return t.items[i];
		return false;
	}*/
}