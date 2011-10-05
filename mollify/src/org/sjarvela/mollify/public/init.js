(function(){
	window.mollify = new function(){
		var t = this;
		this.settings = {};
		this.plugins = [];
		this.pluginsById = {};

		this.init = function(s) {
			t.settings = s;
			if (s.plugins) {
				for (var i=0; i < s.plugins.length; i++)
					t.registerPlugin(s.plugins[i]);
			}
		}
		
		this.setup = function(e) {
			t.env = e;
			
			t.env.addListColumnSpec({
				"id": "file-modified",
				"request-id": "core-file-modified",
				"default-title-key": "fileListColumnTitleLastModified",
				"sort": function(i1, i2, sort, data) {
					if (!i1.is_file && !i2.is_file) return 0;
					if (!data || !data["core-file-modified"]) return 0;
					
					var ts1 = data["core-file-modified"][i1.id] ? data["core-file-modified"][i1.id] * 1 : 0;
					var ts2 = data["core-file-modified"][i2.id] ? data["core-file-modified"][i2.id] * 1 : 0;
					return ((ts1 > ts2) ? 1 : -1) * sort;
				},
				"content": function(item, data) {
					if (!item.id || !item.is_file || !data || !data["core-file-modified"] || !data["core-file-modified"][item.id]) return "";
					return t.env.texts().formatInternalTime(data["core-file-modified"][item.id]);
				}
			});
		}
		
		this.getSettings = function() {
			return t.settings;
		}

		this.getPlugins = function() {
			return t.plugins;
		}
		
		this.getPlugin = function(id) {
			return t.pluginsById[id];
		}

		this.hasPlugin = function(id) {
			return !!t.pluginsById[id];
		}
		
		this.registerPlugin = function(p) {
			t.plugins.push(p);
			var id = p.getPluginInfo().id;
			if (id) t.pluginsById[id] = p;
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

function CommentPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin-comment" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addItemContextProvider(that.getItemContext);
		
		mollify.importCss(that.url("style.css"));
		mollify.importScript(that.url("texts_" + that.env.texts().locale + ".js"));
		
		env.addListColumnSpec({
			"id": "comment-count",
			"request-id": "plugin-comment-count",
			"default-title-key": "",
			"content": that.getListCellContent,
			"request": function(parent) { return {}; },
			"on-render": function() {
				$(".filelist-item-comment-count").click(function(e) {
					var id = e.target.id.substring(19);
					var item = that.env.fileview().item(id);
					that.openComments(item);
				});
			}
		});
		
		if (mollify.hasPlugin("plugin-itemdetails"))
			mollify.getPlugin("plugin-itemdetails").addDetailsSpec({
				key: "comments-count",
				title: "commentsDetailsCount"
			});
	}
	
	this.getListCellContent = function(item, data) {
		if (item.id == null || item.id.length == 0 || !data || !data["plugin-comment-count"]) return "";
		var counts = data["plugin-comment-count"];
		if (!counts[item.id]) return "";
		
		return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count'>"+counts[item.id]+"</div>";
	}
	
	this.getItemContext = function(item) {
		return {
			components : [{
				type: "custom",
				html: "",
				on_init: that.onInit
			}]
		};
	}
	
	this.onInit = function(id, c, item, details) {
		if (!details.comments) return;
		
		$("#"+id).html("<div id='details-comments'><div id='details-comments-content'><div id='details-comments-icon'/><div id='details-comment-count'>"+details.comments.count+"</div></div></div>");
		
		$("#details-comments-content").hover(
			function () { $(this).addClass("hover"); }, 
			function () { $(this).removeClass("hover"); }
		);
		$("#details-comments-content").click(function() {
			c.close();
			that.openComments(item);
		});
	}
	
	this.openComments = function(item) {
		that.env.dialog().showDialog({
			title: that.t("commentsDialogTitle"),
			html: "<div id='comments-dialog-content' class='loading' />",
			on_show: function(d) { that.onShowCommentsDialog(d, item); }
		});
	}

	this.onShowCommentsDialog = function(d, item) {
		mollify.loadContent("comments-dialog-content", that.url("content.html"), function() {
			d.setMinimumSizeToCurrent();
			d.center();
			
			$("#comments-item").html(item.name);
			$("#comments-dialog-content").removeClass("loading");
			$("#comments-dialog-content .mollify-actionlink").hover(
				function () { $(this).addClass("mollify-actionlink-hover"); }, 
				function () { $(this).removeClass("mollify-actionlink-hover"); }
			);

			$("#comments-dialog-add").click(function() { that.onAddComment(d, item); } );
			$("#comments-dialog-close").click(function() { d.close(); } );
			
			that.env.service().get("comment/"+item.id, function(result) {
				that.onShowComments(item, result);
			},	function(code, error) {
				alert(error);
			});
		});
	}
	
	this.onAddComment = function(d, item) {
		var comment = $("#comments-dialog-add-text").val();
		if (!comment || comment.length == 0) return;
		
		that.env.service().post("comment/"+item.id, { comment: comment }, function(result) {
			d.close();
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.onShowComments = function(item, comments) {
		if (comments.length == 0) {
			$("#comments-list").html("<message>"+that.t("commentsDialogNoComments")+"</message>");
			return;
		}
		
		for (var i=0; i<comments.length; i++) {
			comments[i].time = that.env.texts().formatInternalTime(comments[i].time);
			comments[i].comment = comments[i].comment.replace(new RegExp('\n', 'g'), '<br/>');
		}

		$("#comment-template").tmpl(comments).appendTo("#comments-list");
	}
	
	this.url = function(p) {
		return that.env.service().getPluginUrl("Comment")+"client/"+p;
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}	
}

function ItemDetailsPlugin(detailsSpec) {
	var that = this;
	that.specs = {};
	
	this.getPluginInfo = function() { return { id: "plugin-itemdetails" }; }
	
	this.initialize = function(env) {
		that.env = env;
		
		that.env.addItemContextProvider(function(item) {
			if (!detailsSpec || !that.getApplicableSpec(item)) return null;
			
			return {
				components : [{
					type: "section",
					title: that.t("fileActionDetailsTitle"),
					html: "<div id='file-item-details'></div>",
					on_init: that.onInit,
					index: 5
				}]
			}
		}, function(item) {
			if (!detailsSpec) return null;
			var spec = that.getApplicableSpec(item);
			if (!spec) return null;
			
			var result = { itemdetails: [] };
			for (var k in spec)
				result.itemdetails.push(k);
			return result;
		});
	}
	
	this.addDetailsSpec = function(s) {
		if (!s || !s.key) return;
		that.specs[s.key] = s;
	}
	
	this.getApplicableSpec = function(item) {
		var ext = item.is_file ? item.extension.toLowerCase().trim() : "[folder]";
		if (ext.length == 0 || !detailsSpec[ext])
			return detailsSpec["*"];
		return detailsSpec[ext];
	}
	
	this.onInit = function(id, c, item, details) {
		if (!detailsSpec || !details.itemdetails) return false;
		
		var s = that.getApplicableSpec(item);
		var html = "<div class='mollify-file-context-details-content'>";
		for (k in s)
			html += that.getItemRow(k, s[k], details.itemdetails[k]);
		$("#file-item-details").html(html+"</div>");
	}

	this.onOpen = function() {
	}
	
	this.getItemRow = function(dataKey, rowSpec, rowData) {
		if (!rowData) return "";
		var title = that.getTitle(dataKey, rowSpec);
		var value = that.formatData(dataKey, rowData);
		return "<div id='mollify-file-context-details-row-"+dataKey+"' class='mollify-file-context-details-row'><div class='mollify-file-context-details-row-label'>"+title+"</div><div class='mollify-file-context-details-row-value-container'><div class='mollify-file-context-details-row-value'>"+value+"</div></div></div>";
	}
	
	this.getTitle = function(dataKey, rowSpec) {
		if (rowSpec.title) return that.t(rowSpec.title);
		
		if (dataKey == 'size') return that.t('fileItemContextDataSize');
		if (dataKey == 'last-modified') return that.t('fileItemContextDataLastModified');
		
		if (that.specs[dataKey]) {
			var spec = that.specs[dataKey];
			if (spec.title) return that.t(spec.title);
		}
		return dataKey;
	}
	
	this.formatData = function(key, data) {
		if (key == 'size') return that.env.texts().formatSize(data);
		if (key == 'last-modified') return that.env.texts().formatInternalTime(data);
		
		if (that.specs[key]) {
			var spec = that.specs[key];
			if (spec.formatter) return spec.formatter(data);
		}

		return data;
	}
		
	this.url = function(p) {
		return that.env.service().getPluginUrl("Comment")+"client/"+p;
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}	
}