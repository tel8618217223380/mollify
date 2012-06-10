function MainView() {
	var that = this;
	this.viewStyle = 0;
	
	this.init = function(p) {
		that.roots = p.roots;
		that.listener = p.listener;
		
		that.rootsById = {};
		for (var i=0,j=p.roots.length; i<j; i++)
			that.rootsById[p.roots[i].id] = p.roots[i];
	}
	
	this.getDataRequest = function(folder) {
		return that.itemWidget.getDataRequest ? that.itemWidget.getDataRequest(folder) : {};
	}
	
	this.render = function(id) {
		mollify.dom.loadContent(id, mollify.templates.url("mainview.html"), that, ['localize', 'radio']);
	}
	
	this.onLoad = function() {
		$(window).resize(that.onResize);
		that.onResize();

		// TODO default view mode
		// TODO expose file urls
		var s = mollify.session.get();
		mollify.dom.template("mollify-tmpl-main-username", s, mollify).appendTo("#mainview-user");
		if (s.authenticated) mollify.ui.controls.hoverDropdown($('#mollify-username-dropdown'), that.sessionActions());
		
		that.controls["mainview-viewstyle-options"].set(that.viewStyle);
		
		that.listener.onViewLoaded();
	}
	
	this.onRadioChanged = function(groupId, valueId, i) {
		if (groupId == "mainview-viewstyle-options") that.onViewStyleChanged(valueId, i);
	}
	
	this.onViewStyleChanged = function(id, i) {
		that.viewStyle = i;
		that.initList();
		that.data(that.p);
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
		var $t = $("#mainview-content-area").empty();
		if (p) {
			mollify.dom.template("mollify-tmpl-main-folder", p.hierarchy[p.hierarchy.length-1]).appendTo($t);
			that.setupHierarchy(p.hierarchy);
			
			//TODO canWrite
			$("#mollify-folderview-items").addClass("loading");
		} else {
			mollify.dom.template("mollify-tmpl-main-rootfolders").appendTo($t);
			//TODO disable write
		}
		$("#mollify-folderview-items").css("top", $("#mollify-folderview-header").outerHeight()+"px");
		mollify.ui.process($t, ['localize']);
		that.initList();
	}
	
	this.setupHierarchy = function(h) {
		var p = $("#mollify-folder-hierarchy-items").empty();
		
		mollify.dom.template("mollify-tmpl-main-folder-hierarchy", h).appendTo(p);
		$("#mollify-folder-hierarchy-root").click(that.listener.onHomeSelected);
		$(".folder-hierarchy-item").click(function() {
			var index = p.find(".folder-hierarchy-item").index($(this));
			that.listener.onFolderSelected(index+1, h[index]);
		});
	}
	
	this.initList = function() {
		if (that.viewStyle == 0) {
			that.itemWidget = new FileList('mollify-folderview-items', 'main', mollify.settings["list-view-columns"]);
		} else {
			that.itemWidget = new IconView('mollify-folderview-items', 'main', that.viewStyle == 1 ? 'iconview-small' : 'iconview-large');
		}
		
		that.itemWidget.init({
			onFolderSelected : that.listener.onSubFolderSelected,
			canDrop : function(to, item) {
				if (to.id == to.root_id || to.is_file) return false;
				if (item.id == to.id) return false;
				return true;
			},
			onClick: function(item, t, e) {
				console.log(t);
				if (that.viewStyle == 0) {
					if (!item.is_file && t == 'name') {
						that.listener.onSubFolderSelected(item);
						return;
					}
				}
				that.openItemContext(item, that.itemWidget.getItemContextElement(item));
			},
			onDblClick: function(item) {
				if (that.viewStyle == 0 || item.is_file) return;
				that.listener.onSubFolderSelected(item);
			},
			onRightClick: function(item, t, e) {
				that.listener.getItemActions(item, function(a) { that.showActionMenu(item, a, e); });
			}
		});
	}
	
	this.data = function(p) {
		that.p = p;
		$("#mollify-folderview-items").removeClass("loading");
		that.itemWidget.content(p.items, p.data);
	}
	
	this.showActionMenu = function(item, actions, c) {
		if (!actions) return;
		c.addClass("open");
		mollify.ui.controls.popupmenu(actions, { control: c }, function() { c.removeClass("open"); that.itemWidget.removeHover(); });
	}
	
	this.openItemContext = function(item, e) {
		e.qtip({
			content: "<div>foo</div>",
			position: {
				my: that.viewStyle == 0 ? 'top left' : 'top center',
				at: that.viewStyle == 0 ? 'bottom left' : 'bottom center',
			},
			hide: {
				delay: 200,
				fixed: true,
				event: 'click mouseleave'
			},
			style: {
				tip: true,
				classes: 'ui-tooltip-light ui-tooltip-shadow ui-tooltip-rounded ui-tooltip-tipped'
			},
			events: {
				render: function(e, api) {

				},
				visible: function(e, api) {

				},
				hide: function(e, api) {
					api.destroy();
				}
			}
		}).qtip('api').show();
	}
}

function IconView(container, id, cls) {
	var t = this;
	t.$c = $("#"+container);
	t.viewId = 'mollify-iconview-'+id;
	
	this.init = function(p) {
		t.p = p;
		
		mollify.dom.template("mollify-tmpl-iconview", {viewId: t.viewId}).appendTo(t.$c.empty());
		t.$l = $("#"+t.viewId);
		if (cls) t.$l.addClass(cls);
	}
	
	this.content = function(items, data) {
		t.items = items;
		t.data = data;
		
		mollify.dom.template("mollify-tmpl-iconview-item", items, {
			typeClass : function(item) {
				var c = item.is_file ? 'item-file' : 'item-folder';
				if (item.is_file && item.extension) c += ' item-type-'+item.extension;
				else if (!item.is_file && item.id == item.root_id) c += ' item-root-folder';
				return c;
			}
		}).appendTo(t.$l.empty());
		
		t.$l.find(".mollify-iconview-item").hover(function() {
			$(this).addClass("hover");
		}, function() {
			$(this).removeClass("hover");
		}).draggable({
			revert: "invalid",
			distance: 10,
			addClasses: false,
			zIndex: 2700
		}).droppable({
			hoverClass: "drophover",
			accept: function(i) { return t.p.canDrop ? t.p.canDrop($(this).tmplItem().data, $(i).tmplItem().data) : false; }
		}).single_double_click(function() {
			var $t = $(this);
			t.p.onClick($t.tmplItem().data, "", $t);
		},function() {
			t.p.onDblClick($(this).tmplItem().data);
		}).attr('unselectable', 'on').css({
		   '-moz-user-select':'none',
		   '-webkit-user-select':'none',
		   'user-select':'none',
		   '-ms-user-select':'none'
		});
	}
	
	this.getItemContextElement = function(item) {
		return t.$l.find("#mollify-iconview-item-"+item.id);
	}
}

function FileList(container, id, columns) {
	var t = this;
	t.minColWidth = 75;
	t.$c = $("#"+container);
	t.listId = 'mollify-filelist-'+id;
	t.cols = [];
	t.sortCol = false;
	t.sortOrderAsc = true;
	
	for (var colId in columns) {
		var col = mollify.ui.filelist.columns[colId];
		if (!col) continue;
		t.cols.push(col);
	}
	
	this.init = function(p) {
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
			var $t = $(this);
			var ind = $t.index();
			var col = t.cols[ind];
			
			$t.css("min-width", t.minColWidth);
			if (col.width) $t.css("width", col.width);
			
			$t.find(".mollify-filelist-col-header-title").click(function() {
				t.onSortClick(col);
			});
			
			if (i != (t.cols.length-1)) {
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
						$(".mollify-filelist-col-"+col.id).width(w);
					}
				});/*.draggable({
					axis: "x",
					helper: "clone",
					revert: "invalid",
					distance: 30
				});*/
			}
		});
		t.items = [];
		t.data = {};
		t.onSortClick(t.cols[0]);
	}
	
	this.onSortClick = function(col) {
		if (col.id != t.sortCol.id) {
			t.sortCol = col;
			t.sortOrderAsc = true;
		} else {
			t.sortOrderAsc = !t.sortOrderAsc;
		}
		t.refreshSortIndicator();
		t.content(t.items, t.data);
	}
	
	this.sortItems = function() {
		var s = t.sortCol.sort;
		t.items.sort(function(a, b) {
			return s(a, b, t.sortOrderAsc ? 1 : -1, t.data);
		});
	}
	
	this.refreshSortIndicator = function() {
		t.$h.find(".mollify-filelist-col-header").removeClass("sort-asc").removeClass("sort-desc");
		$("#mollify-filelist-col-header-"+t.sortCol.id).addClass("sort-" + (t.sortOrderAsc ? "asc" : "desc"));
	}
	
	this.getDataRequest = function(item) {
		var rq = {};
		for (var i=0, j=t.cols.length; i<j; i++) {
			var c = t.cols[i];
			if (c['request-id']) rq[c['request-id']] = {};
		}
		return rq;
	}
	
	this.content = function(items, data) {
		t.items = items;
		t.data = data;
		t.sortItems();
		
		mollify.dom.template("mollify-tmpl-filelist-item", items, {
			cols: t.cols,
			typeClass : function(item) {
				var c = item.is_file ? 'item-file' : 'item-folder';
				if (item.is_file && item.extension) c += ' item-type-'+item.extension;
				else if (!item.is_file && item.id == item.root_id) c += ' item-root-folder';
				return c;
			},
			col: function(item, col) {
				return col.content(item, t.data);
			},
			itemColStyle: function(item, col) {
				var style="min-width:"+t.minColWidth+"px";
				if (col.width) style = style+";width:"+col.width+"px";
				return style;
			}
		}).appendTo(t.$i.empty());
		
		var $items = t.$i.find(".mollify-filelist-item");
		$items.hover(function() {
			$(this).addClass("hover");
		}, function() {
			$(this).removeClass("hover");
		}).draggable({
			revert: "invalid",
			distance: 10,
			addClasses: false,
			zIndex: 2700
		}).droppable({
			hoverClass: "drophover",
			accept: function(i) { return t.p.canDrop ? t.p.canDrop($(this).tmplItem().data, $(i).tmplItem().data) : false; }
		}).click(function(e) {
			e.preventDefault();
			t.onItemClick($(this), $(e.srcElement), true);
			return false;
		}).bind("contextmenu",function(e){
			e.preventDefault();
			t.onItemClick($(this), $(e.srcElement), false);
			return false;
		}).attr('unselectable', 'on').css({
		   '-moz-user-select':'none',
		   '-webkit-user-select':'none',
		   'user-select':'none',
		   '-ms-user-select':'none'
		});

		/*t.$i.find(".mollify-filelist-quickmenu").click(function(e) {
			e.preventDefault();
			var $t = $(this);
			t.p.onMenuOpen($t.tmplItem().data, $t);
		});*/

		/*t.$i.find(".mollify-filelist-item-name-title").click(function(e) {
			e.preventDefault();
			t.p.onClick($(this).tmplItem().data, "name");
		});*/
		/*t.$i.find(".item-folder .mollify-filelist-item-name-title").click(function(e) {
			e.preventDefault();
			t.p.onFolderSelected($(this).tmplItem().data);
		});*/
	}
	
	this.onItemClick = function($item, $el, left) {
		var i = $item.find(".mollify-filelist-col").index($el.closest(".mollify-filelist-col"));
		var colId = (i == 0 ? "icon" : t.cols[i-1].id);
		if (left)
			t.p.onClick($item.tmplItem().data, colId, $item);
		else
			t.p.onRightClick($item.tmplItem().data, colId, $item);
	}
	
	this.getItemContextElement = function(item) {
		return t.$i.find("#mollify-filelist-item-"+item.id+" .mollify-filelist-col-name");
	}
	
	this.removeHover = function() {
		t.$i.find(".mollify-filelist-item.hover").removeClass('hover');
	}
}