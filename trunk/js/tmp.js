!function ($) {
window.mollify = function() {
	
	/*this.sessionManager = new function() {
		var smt = this;
		
		this.set = function(s) {
			
		};
		
		this.get = function() {
			
		};
	};*/
	

			


	/*this.parseDate = function(dateFmt, date, time) {
		if (!date || date.length == 0) return null;
		
		var t = $.datepicker.parseDate(dateFmt, date);
		if (!time || time.length < 5) {
			t.setHours("00");
			t.setMinutes("00");
			t.setSeconds("00");
		} else {
			//TODO timeFmt
			t.setHours(time.substring(0,2));
			t.setMinutes(time.substring(3,5));
			t.setSeconds(time.length > 6 ? time.substring(7,9) : "00");
		}
		return t;
	}*/


	
	/*this.texts = new function() {
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
	}*/
();
}(window.jQuery);

/**
/* Login view
/**/
$.extend(true, mollify, {
	view : {
		
	}
});



$.extend(true, mollify, {
	plugin : {
	}
});


/**
/* Comment plugin
/**/
$.extend(true, mollify, {
	plugin : {
		}
	}
});

/**
/* Permissions plugin
/**/
$.extend(true, mollify, {
	plugin : {
	}
});

/**
/* Dropbox plugin
/**/
$.extend(true, mollify, {
	plugin : {
	}
});

/**
/* Core plugin
/**/
$.extend(true, mollify, {
	plugin : {
	}
});


/**
/* Item collection plugin
/**/
$.extend(true, mollify, {
	plugin : {

	}
});


/*function ItemDetailsPlugin(conf, sp) {
	var that = this;
	that.specs = {};
	that.typeConfs = false;
	
	this.getPluginInfo = function() { return { id: "plugin-itemdetails" }; }
	
	this.initialize = function(env) {
		that.env = env;
		
		if (sp) {
			for (var i=0; i<sp.length;i++)
				that.addDetailsSpec(sp[i]);
		}
		if (conf) {
			that.typeConfs = {};
			
			for (var t in conf) {
				var parts = t.split(",");
				var c = conf[t];
				for (var i=0; i < parts.length; i++) {
					var p = parts[i].trim();
					if (p.length > 0)
						that.typeConfs[p] = c;
				}
			}
		}
		
		that.env.addItemContextProvider(function(item) {
			if (!that.typeConfs || !that.getApplicableSpec(item)) return null;
			
			return {
				components : [{
					title: that.t("fileActionDetailsTitle"),
					html: "<div id='file-item-details' class='loading'></div>",
					on_init: that.onInit,
					on_open: that.onOpen,
					on_dispose: function() { that.loaded = false; },
					index: 5
				}]
			}
		}, function(item) {
			if (!that.typeConfs) return null;
			var spec = that.getApplicableSpec(item);
			if (!spec) return null;
			
			var result = { itemdetails: [] };
			for (var k in spec)
				result.itemdetails.push(k);
			return result;
		});
	}
	
	this.onOpen = function(item, details) {
		if (that.loaded) return;
		that.loaded = true;
		
		mollify.loadContent("file-item-details", that.url("content.html"), function() {
			$("#file-item-details").removeClass("loading");
			var s = that.getApplicableSpec(item);
			var data = [];
			for (var k in s) {
				var rowSpec = s[k];
				var rowData = details.itemdetails[k];
				if (!rowData) continue;
				
				data.push({key:k, title:that.getTitle(k, rowSpec), value: that.formatData(k, rowData)});
			}
			$("#item-details-template").tmpl(data).appendTo("#mollify-file-item-details-content");
		});
	}
	
	this.addDetailsSpec = function(s) {
		if (!s || !s.key) return;
		that.specs[s.key] = s;
	}
	
	this.getApplicableSpec = function(item) {
		var ext = item.is_file ? item.extension.toLowerCase().trim() : "";
		if (ext.length == 0 || !that.typeConfs[ext]) {
			ext = item.is_file ? "[file]" : "[folder]";
			if (!that.typeConfs[ext])
				return that.typeConfs["*"];
		}
		return that.typeConfs[ext];
	}
	
	this.onInit = function(id, c, item, details) {
		if (!that.typeConfs || !details.itemdetails) return false;
		that.loaded = false;
	}
	
	this.getTitle = function(dataKey, rowSpec) {
		if (rowSpec.title) return rowSpec.title;
		if (rowSpec["title-key"]) return that.t(rowSpec["title-key"]);

		if (dataKey == 'name') return that.t('fileItemContextDataName');
		if (dataKey == 'size') return that.t('fileItemContextDataSize');
		if (dataKey == 'path') return that.t('fileItemContextDataPath');
		if (dataKey == 'extension') return that.t('fileItemContextDataExtension');
		if (dataKey == 'last-modified') return that.t('fileItemContextDataLastModified');
		if (dataKey == 'image-size') return that.t('fileItemContextDataImageSize');
		
		if (that.specs[dataKey]) {
			var spec = that.specs[dataKey];
			if (spec.title) return spec.title;
			if (spec["title-key"]) return that.t(spec["title-key"]);
		}
		return dataKey;
	}
	
	this.formatData = function(key, data) {
		if (key == 'size') return that.env.texts().formatSize(data);
		if (key == 'last-modified') return that.env.texts().formatInternalTime(data);
		if (key == 'image-size') return that.t('fileItemContextDataImageSizePixels', [data]);
		
		if (that.specs[key]) {
			var spec = that.specs[key];
			if (spec.formatter) return spec.formatter(data);
		}

		return data;
	}
		
	this.url = function(p) {
		return that.env.service().getPluginUrl("ItemDetails")+"client/"+p;
	}
	
	this.t = function(s, p) {
		return that.env.texts().get(s, p);
	}
}

function ExifDetails() {
	var t = this;
	
	this.formatExif = function(d) {
		var html = "<div id='item-details-exif'><table id='item-details-exif-values'>";
		for (var s in d) {
			var first = true;
			for (var k in d[s]) {
				var v = t.formatValue(s, k, d[s][k]);
				if (!v) continue;
				
				html += '<tr id="exif-row-'+s+'-'+k+'" class="'+(first?'exif-row-section-first':'exif-row')+'"><td class="exif-section">'+(first?s:'')+'</td><td class="exif-key">'+k+'</td><td class="exif-value">'+v+'</td></tr>';
				first = false;
			}
		}
		return html + "</table></div>";
	}
	
	this.formatValue = function(section, key, value) {
		if (section == 'FILE' && key == 'SectionsFound') return false;
		//TODO format values?
		return value;
	}
	
	return {
		key: "exif",
		"title-key": "fileItemDetailsExif",
		formatter: t.formatExif
	}
}

function SharePlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_share" }; }
	
	this.initialize = function(env) {
		that.env = env;
		
		if (window.ZeroClipboard) {
			that.testclip = new ZeroClipboard.Client();
			that.testclip.addEventListener('load', function(client) {
				//console.log("Clipboard support detected");
				that.testclip.hide();
				
				that.clip = new ZeroClipboard.Client();
				that.clip.setHandCursor(true);
				that.clip.setCSSEffects(false);
				that.clip.addEventListener('onMouseOver', function() { that.onClipMouse(true); });
				that.clip.addEventListener('onMouseOut', function() { that.onClipMouse(false); });
				that.clip.addEventListener('onComplete', that.onClipClick);
				
				if (that.shares.length > 0) $(".share-link-copy-container").removeClass("hidden");
			});
		}

		that.clip = false;
		that.hoverId = false;
		
		mollify.importCss(that.url("style.css"));
		mollify.importScript(that.url("texts_" + that.env.texts().locale + ".js"));
		
		that.env.addItemContextProvider(function(item) {
			return {
				actions: {
					secondary: [
						{ title: "-" },
						{
							title: that.t('itemContextShareMenuTitle'),
							callback: function(item) {
								that.openShares(item);
							}
						}
					]
				}
			};
		}, function(item) {
			return {"plugin-share":[]};
		});
	}
	
	this.openShares = function(item) {
		that.env.dialog().showDialog({
			modal: false,
			title: that.t("shareDialogTitle"),
			html: "<div id='share-dialog-content' class='loading'></div>",
			on_show: function(d) { that.onShowSharesDialog(d, item); }
		});
	}

	this.onShowSharesDialog = function(d, item) {
		mollify.loadContent("share-dialog-content", that.url("content.html"), function() {
			$("#share-item-title").html(that.t(item.is_file ? 'shareDialogShareFileTitle' : 'shareDialogShareFolderTitle'));
			$("#share-item-name").html(item.name);
			$("#share-dialog-content").removeClass("loading");

			$("#add-share-btn").click(function() { that.onAddShare(item); } );
			$("#share-dialog-close").click(function() { d.close(); } );
			if (that.testclip) that.testclip.glue("clip-test");
			
			that.env.service().get("share/items/"+item.id, function(result) {
				that.refreshShares(item, result);
				that.updateShareList(item);
				d.setMinimumSizeToCurrent();
				d.center();
			},	function(code, error) {
				alert(error);
			});
		});
	}
	
	this.refreshShares = function(item, shares) {
		that.shares = shares;
		that.shareIds = [];
		
		for (var i=0, j=shares.length; i<j; i++)
			that.shareIds.push(shares[i].id);
	}
	
	this.getShare = function(id) {
		return that.shares[that.shareIds.indexOf(id)];
	}
	
	this.updateShareList = function(item) {
		$("#share-items").empty();
		
		if (that.shares.length == 0) {
			$("#share-items").html('<div class="no-share-items">'+that.t("shareDialogNoShares")+'</div>');
			return;
		}
		
		var opt = {
			name : function() {
				if (!this.data.name || this.data.name.length == 0)
					return '<text key="shareDialogUnnamedShareTitle" />';
				return this.data.name;
			},
			itemClass : function() {
				var c = "item-share";
				if (!this.data.active)
					c = c + " item-share-inactive";
				if (!this.data.name || this.data.name.length == 0)
					c = c + " item-share-unnamed";
				return c;
			},
			link : function() {
				return that.env.service().getUrl("public/"+this.data.id);
			}
		};
		
		$("#share-template").tmpl(that.shares, opt).appendTo("#share-items");
		mollify.localize("share-list");
		
		if (that.clip) $(".share-link-copy-container").removeClass("hidden");
		//else console.log("Clipboard support not detected");
		
		var initClipboard = function(id) {
			if (!that.clip) return;
			if (!that.getShare(id).active) {
				that.clip.setText("");
				that.clip.hide();
				return;
			}
			
			that.clip.show();
			that.clip.setText(that.env.service().getUrl("public/"+id));
			var el = $("#share-copy-"+id);
			if (that.clip.div) {
				that.clip.reposition(el[0]);
			} else {
				that.clip.glue(el[0]);
			}
		}

		$(".item-share").hover(
			function() {
				$(".item-share").removeClass("item-share-hover");
				
				var el = $(this);
				var id = el.attr('id').substring(6);
				el.addClass("item-share-hover");
				that.hoverId = id;
				
				initClipboard(id);
			},
			function() {
			}
		);
		//initClipboard(that.shares[0].id);
		
		var idFunction = function(i, f) {
			var p = $(i).hasClass('item-share') ? i : $(i).parentsUntil(".item-share").parent()[0];
			var id = p.id.substring(6);
			f(item, id);
		};
		$(".share-link-toggle-title").click(function() {
			var id = $(this).parent()[0].id.substring(6);
			if (!that.getShare(id).active) return;
			
			var linkContainer = $(this).next();
			var open = linkContainer.hasClass("open");
			if (!open) $(".share-link-content").removeClass("open");
			linkContainer.toggleClass("open");
			return false;
		}).hover(
			function() { $(this).addClass("hover"); },
			function() { $(this).removeClass("hover"); }
		);
		
		$(".share-edit").click(function(e) {
			idFunction(this, that.onEditShare);
			return false;
		});
		$(".share-remove").click(function(e) {
			idFunction(this, that.removeShare);
			return false;
		});
	}
	
	this.onClipMouse = function(over) {
		if (!that.hoverId) return;
		
		if (over) $("#share-copy-"+that.hoverId).addClass("hover");
		else $("#share-copy-"+that.hoverId).removeClass("hover");
	}

	this.onClipClick = function() {
		if (!that.hoverId) return;
		
		var el = $("#share-copy-"+that.hoverId);
		el.addClass("click");
		window.setTimeout(function() { el.removeClass("click"); }, 200);
	}
	
	this.closeAddEdit = function() {
		$("#share-items").removeClass("minimized");
		$("#share-context").addClass("minimized");
		$(".share-context-toolbar-option").hide();
		$("#add-share-btn").show();
	}
	
	this.openContext = function(toolbarId, contentTemplateId) {
		$("#share-items").addClass("minimized");
		$("#share-context").removeClass("minimized");
		$(".share-context-toolbar-option").hide();
		$("#"+toolbarId).show();
		$("#"+contentTemplateId).tmpl({}).appendTo($("#share-context-content").empty());
		mollify.localize("share-context-content");
		//$("#share-validity-expirationdate-value").datepicker();
	}
	
	this.onAddShare = function(item) {
		that.openContext('add-share-title', 'share-context-addedit-template');
		
		$("#share-general-name").val('');
		$('#share-general-active').attr('checked', true);

		$("#share-addedit-btn-ok").click(function() {
			var name = $("#share-general-name").val();
			var active = $("#share-general-active").is(":checked");
			var expiration = null;//mollify.parseDate(that.t('shortDateFormat'), $("#share-validity-expirationdate-value").val(), $("#share-validity-expirationtime-value").val());
			
			$("#share-items").empty().append('<div class="loading"/>');
			that.closeAddEdit();
			that.addShare(item, name || '', expiration, active);
		});
		
		$("#share-addedit-btn-cancel").click(function() {
			that.closeAddEdit();
		});
	}
	
	this.onEditShare = function(item, id) {
		that.openContext('edit-share-title', 'share-context-addedit-template');
		
		var share = that.getShare(id);
		
		$("#share-general-name").val(share.name);
		$("#share-general-active").attr("checked", share.active);
		
		$("#share-addedit-btn-ok").click(function() {
			var name = $("#share-general-name").val();
			var active = $("#share-general-active").is(":checked");
			var expiration = mollify.parseDate(that.t('shortDateFormat'), $("#share-validity-expirationdate-value").val(), $("#share-validity-expirationtime-value").val());
			
			$("#share-items").empty().append('<div class="loading"/>')
			that.closeAddEdit();
			that.editShare(item, share.id, name || '', expiration, active);
		});
		
		$("#share-addedit-btn-cancel").click(function() {
			that.closeAddEdit();
		});
	}
	
	this.addShare = function(item, name, expiration, active) {
		that.env.service().post("share/items/"+item.id, { item: item.id, name: name, expiration: mollify.formatInternalTime(expiration), active: active }, function(result) {
			that.refreshShares(item, result);
			that.updateShareList(item);
		},	function(code, error) {
			alert(error);
		});
	}

	this.editShare = function(item, id, name, expiration, active) {
		that.env.service().put("share/"+id, { id: id, name: name, expiration: mollify.formatInternalTime(expiration), active: active }, function(result) {
			var share = that.getShare(id);
			share.name = name;
			share.active = active;
			share.expiration = expiration;
			that.updateShareList(item);
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.removeShare = function(item, id) {
		that.env.service().del("share/"+id, function(result) {
			var i = that.shareIds.indexOf(id);
			that.shareIds.splice(i, 1);
			that.shares.splice(i, 1);
			that.updateShareList(item);
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.url = function(p) {
		return that.env.service().getPluginUrl("Share")+"client/"+p;
	}
	
	this.t = function(s, p) {
		return that.env.texts().get(s, p);
	}
}*/

function($, mollify){
mollify.MollifyHTML5DragAndDrop = function() {
	var t = this;
	t.dragObj = false;
	t.dragEl = false;
	t.dragListener = false;
	
	var endDrag = function() {
		if (t.dragEl) {
			t.dragEl.removeClass("dragged");
			if (t.dragListener && t.dragListener.onDragEnd) t.dragListener.onDragEnd(t.dragEl, e);
			t.dragEl = false;
		}
		t.dragObj = false;
		t.dragListener = false;
	};
	
	$("body").bind('dragover', function(e) {
		if (e.preventDefault) e.preventDefault();
		e.originalEvent.dataTransfer.dropEffect = "none";
		return false;
	});
	
	return {
		enableDrag : function($e, l) {
			$e.attr("draggable","true").bind('dragstart', function(e) {
				t.dragObj = false;
				e.originalEvent.dataTransfer.effectAllowed = "none";
				if (l.onDragStart) {
					t.dragObj = l.onDragStart($(this), e);
					if (t.dragObj) {
						t.dragEl = $(this);
						t.dragListener = l;
						t.dragEl.addClass("dragged");
						e.originalEvent.dataTransfer.effectAllowed = "copyMove";
						return;
					}
				}
				return false;
			}).bind('dragend', function(e) {	
				endDrag();
			});
		},
		enableDrop : function($e, l) {
			$e.addClass("droppable").bind('drop', function(e) {
				if (e.stopPropagation) e.stopPropagation();
				if (!l.canDrop || !l.onDrop || !t.dragObj) return;
				var $t = $(this);
				if (l.canDrop($t, e, t.dragObj)) {
					l.onDrop($t, e, t.dragObj);
					$t.removeClass("dragover");
				}
				endDrag();
			}).bind('dragenter', function(e) {
				if (!l.canDrop || !t.dragObj) return false;
				var $t = $(this);
				if (l.canDrop($t, e, t.dragObj)) {
					$t.addClass("dragover");
				}
			}).bind('dragover', function(e) {
				if (e.preventDefault) e.preventDefault();
				
				var fx = "none";
				if (l.canDrop && l.dropType && t.dragObj) {
					var $t = $(this);
					if (l.canDrop($t, e, t.dragObj)) {
						var tp = l.dropType($t, e, t.dragObj);
						if (tp) fx = tp;
					}
				}
				
				e.originalEvent.dataTransfer.dropEffect = fx;
				return false;
			}).bind('dragleave', function(e) {
				$(this).removeClass("dragover");
			});
		}
	};
};

mollify.MollifyJQueryDragAndDrop = function() {
	return {
		enableDrag : function($e, l) {
			$e.draggable({
				revert: "invalid",
				distance: 10,
				addClasses: false,
				zIndex: 2700,
				start: function(e) {
					if (l.onDragStart) l.onDragStart($(this), e);
				}
			});
		}
	};
};
}(window.jQuery, window.mollify);

/* Common */

function isArray(o) {
	return Object.prototype.toString.call(o) === '[object Array]';
}

if(typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, ''); 
	}
}

if(typeof String.prototype.startsWith !== 'function') {
	String.prototype.startsWith = function(s) {
		if (!s || s.length === 0) return false;
		return this.substring(0, s.length) == s; 
	}
}

function def(o) {
	return (typeof(o) != 'undefined');
}

if (!Array.prototype.indexOf) { 
    Array.prototype.indexOf = function(obj, start) {
         for (var i = (start || 0), j = this.length; i < j; i++) {
             if (this[i] === obj) { return i; }
         }
         return -1;
    }
}

if (!Array.prototype.remove) { 
	Array.prototype.remove = function(from, to) {
		if (typeof(to) == 'undefined' && typeof(from) == 'object')
			from = this.indexOf(from);
		var rest = this.slice((to || from) + 1 || this.length);
		this.length = from < 0 ? this.length + from : from;
		return this.push.apply(this, rest);
	};
}

function strpos(haystack, needle, offset) {
    // Finds position of first occurrence of a string within another  
    // 
    // version: 1109.2015
    // discuss at: http://phpjs.org/functions/strpos
    // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: Onno Marsman    
    // +   bugfixed by: Daniel Esteban
    // +   improved by: Brett Zamir (http://brett-zamir.me)
    var i = (haystack + '').indexOf(needle, (offset || 0));
    return i === -1 ? false : i;
}

/**
*
*  Base64 encode / decode
*  http://www.webtoolkit.info/
*
**/
 
var Base64 = {
 
	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
 
	// public method for encoding
	encode : function (input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = Base64._utf8_encode(input);
 
		while (i < input.length) {
 
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
 
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
 
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
 
			output = output +
			this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
			this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
 
		}
 
		return output;
	},
 
	// public method for decoding
	decode : function (input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
		while (i < input.length) {
 
			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));
 
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
 
			output = output + String.fromCharCode(chr1);
 
			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
 
		}
 
		output = Base64._utf8_decode(output);
 
		return output;
 
	},
 
	// private method for UTF-8 encoding
	_utf8_encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
 
		for (var n = 0; n < string.length; n++) {
 
			var c = string.charCodeAt(n);
 
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
 
		}
 
		return utftext;
	},
 
	// private method for UTF-8 decoding
	_utf8_decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = 0, c1 = 0, c2 = 0;
 
		while ( i < utftext.length ) {
 
			c = utftext.charCodeAt(i);
 
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
 
		}
 
		return string;
	}
 
}