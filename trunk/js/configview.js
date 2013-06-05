!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.MainViewConfigView = function() {
		var that = this;
		this._views = [];

		this.init = function() {
			that.title = mollify.ui.texts.get('configviewMenuTitle');
		}

		this.onActivate = function(h) {
			mollify.templates.load("configview", mollify.templates.url("configview.html"), function() {
				mollify.dom.template("mollify-tmpl-configview").appendTo(h.content);

				var navBarItems = [];
				$.each(mollify.plugins.getConfigViewPlugins(), function(i, p) {
					if (!p.configViewHandler.views) return;

					var views = p.configViewHandler.views();
					if (!views) return;
					
					$.each(views, function(i, v) {
						that._views.push(v);
						navBarItems.push({title:v.title, obj: v, callback:function(){ that._activateView(v); }})
					});
				});

				that._userNav = h.addNavBar({
					title: mollify.ui.texts.get("configViewUserNavTitle"),
					items: navBarItems
				});

			});
		}

		this._activateView = function(v) {
			that._userNav.setActive(v);

			$("#mollify-configview-header").html(v.title);
			v.onActivate($("#mollify-configview-content").empty());
		}

		this.onDeactivate = function() {}
	}

	mollify.view.ConfigListView = function($e, o) {
		mollify.dom.template("mollify-tmpl-configlistview").appendTo($e);
		var $table = $e.find(".mollify-configview-table");
		var table = mollify.ui.controls.table($table, {
			key: o.table.key,
			columns: o.table.columns,
			onRowAction: function(id, obj) {
				if (o.onTableRowAction) o.onTableRowAction(table, id, obj);
			}
		});

		return {
			table: table
		};
	}
}(window.jQuery, window.mollify);
