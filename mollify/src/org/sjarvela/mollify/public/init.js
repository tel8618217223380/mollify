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
	}
	
	this.getListCellContent = function(item, data) {
		if (item.id == null || item.id.length == 0 || !data || !data["plugin-comment-count"]) return "";
		var counts = data["plugin-comment-count"];
		if (!counts[item.id]) return "";
		
		return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count'>"+counts[item.id]+"</div>";
	}
	
	this.getItemContext = function(item, details) {
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

function ItemDetailsPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin-itemdetails" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addItemContextProvider(that.getItemContext);
	}
		
	this.getItemContext = function(item, details) {
		return {
			components : [{
				type: "section",
				title: that.t("fileActionDetailsTitle"),
				html: "<div id='file-item-details'></div>",
				on_init: that.onInit,
				index: 5
			}]
		};
	}
	
	this.onInit = function(id, c, item, details) {
		//if (!details.itemdetails) return;
		
		$("#file-item-details").html("<div class='mollify-file-context-details-content'>foo</div>");
	}
		
	this.url = function(p) {
		return that.env.service().getPluginUrl("Comment")+"client/"+p;
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}	
}