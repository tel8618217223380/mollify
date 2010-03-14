/**
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/
var dateFormat = "dd.mm.yy";
var dateTimeFormat = "dd.MM.yyyy HH:mm:ss";

var session = null;
var loadedScripts = new Array();
var controllers = {
	"menu-published-folders": {"class" : "MollifyPublishedFoldersConfigurationView", "script" : "folders/published_folders.js", "title": "Published Folders"},
	"menu-users": {"class" : "MollifyUsersConfigurationView", "script" : "users/users.js", "title": "Users"},
	"menu-usergroups": {"class" : "MollifyUserGroupsConfigurationView", "script" : "users/groups.js", "title": "Groups"},
	"menu-events": {"class" : "MollifyEventsView", "script" : "events/events.js", "title": "Events"}
};
var controller = null;

$(document).ready(function() {
	preRequestCallback = function() { $("#request-indicator").addClass("active"); };
	postRequestCallback = function() { $("#request-indicator").removeClass("active"); }
	$.datepicker.setDefaults( { dateFormat: dateFormat } );
	
	$(".main-menu-item").click(function() {
		$(".main-menu-item").removeClass("active");
		$(this).addClass("active");
		onSelectMenu($(this).attr("id"));
	});

	getSessionInfo(onSession, onServerError);				
});

function onSession(session) {
	if (!session["authentication_required"] || !session["authenticated"] || session["default_permission"] != 'A') {
		onUnauthorized();
		return;
	}
	if (!session.features["administration"]) {
		$("body").html("Current configuration type is not supported by the Mollify administration utility. For more information, see <a href='http://code.google.com/p/mollify/wiki/Installation'>Installation instructions</a>");
		return;
	}
	this.session = session;
	
	$("#content").show();
}
			
function onSelectMenu(id) {
	if (!controllers[id]) {
		onError("Configuration view not defined: "+id);
		return;
	}
	
	loadScript(controllers[id]['script'], function() { initView(controllers[id]); });
}

function loadScript(script, cb) {
	if (!script || $.inArray(script, loadedScripts) >= 0) {
		if (cb) cb();
		return;
	}
	$.getScript(script, function() {
		loadedScripts.push(script);
		if (cb) cb();
	});
}

function initView(controllerSpec) {
	setTitle(controllerSpec.title);
	
	controller = eval("new "+controllerSpec['class']+"()");
	if (controller.pageUrl) $("#page").load(controller.pageUrl, "", onLoadView);
}

function onLoadView() {
	initWidgets();
	controller.onLoadView();
}

function notify(msg) {
	alert(msg);	//TODO some other notification that doesn't require user dismissal
}

function onUnauthorized() {
	$("body").load("unauthorized.html", "", initWidgets);
}

function onServerError(error) {
	if (error.code == 100) {
		onUnauthorized();
		return;
	}
	var errorHtml = $.template("<div class='error'><div class='title'>${title}</div><div class='details'>${details}</div><div id='error-info'><div id='error-info-title'>Details</div><div id='error-info-content'>${info}</div></div></div>");
	$("body").html(errorHtml, {title: error.error, details: error.details, info: (error.trace ? error.trace : '' ) });
	
	if (!error.trace) {
		$('#error-info').hide();
	} else {
		$('#error-info-content').hide();
		$('#error-info-title').click(function(){ $('#error-info-title').toggleClass("open"); $('#error-info-content').slideToggle(); });
	}
}

function onError(error) {
	setTitle("Error");
	$("#page").html("<div class='error'><div class='title'>"+error+"</div></div>");
}

function setTitle(title) {
	$("#page-title").html(title);
}

function enableButton(id, enabled) {
	if (!enabled) $("#"+id).addClass("ui-state-disabled");
	else $("#"+id).removeClass("ui-state-disabled");
}

function getDateFormat() {
	return dateFormat;
}

function formatDate(d) {
	return $.datepicker.formatDate(dateFormat, d);
}

function formatDateTime(time) {
	return time.format(dateTimeFormat);
}

function parseDate(d) {
	var t = $.datepicker.parseDate(dateFormat, d);
	t.setHours("00");
	t.setMinutes("00");
	t.setSeconds("00");
	return t;
}

function parseInternalTime(time) {
	var ts = new Date();
	ts.setYear(time.substring(0,4));
	ts.setMonth(time.substring(4,6) - 1);
	ts.setDate(time.substring(6,8));
	ts.setHours(time.substring(8,10));
	ts.setMinutes(time.substring(10,12));
	ts.setSeconds(time.substring(12,14));
	return ts;
}

function formatInternalTime(time) {
	return time.format('yyyyMMddHHmmss', time);
}

function initWidgets() {
	$('button').each(function() {
		$(this).hover(
			function(){ 
				$(this).addClass("ui-state-hover"); 
			},
			function(){ 
				$(this).removeClass("ui-state-hover"); 
			}
		);
	});
	
	$('.toggle-panel').each(function() {
		$(this).children('.toggle-panel-content').hide();
		$(this).children('.toggle-panel-title').click(function(){
			$(this).toggleClass("open"); $(this).parent().children('.toggle-panel-content').slideToggle();
		});
	});
}

Date.prototype.format = function(format) {
	var date = this;
	if (!format) format="MM/dd/yyyy";               
 
	var month = date.getMonth() + 1;
	var year = date.getFullYear();    
 
	format = format.replace("MM",month.toString().padL(2,"0"));        
 
	if (format.indexOf("yyyy") > -1) format = format.replace("yyyy",year.toString());
	else if (format.indexOf("yy") > -1) format = format.replace("yy",year.toString().substr(2,2));
 
    format = format.replace("dd",date.getDate().toString().padL(2,"0"));
 
	var hours = date.getHours();
	if (format.indexOf("t") > -1) {
		if (hours > 11) format = format.replace("t","pm")
		else format = format.replace("t","am")
	}
	
	if (format.indexOf("HH") > -1) format = format.replace("HH",hours.toString().padL(2,"0"));
	if (format.indexOf("hh") > -1) {
		if (hours > 12) hours - 12;
		if (hours == 0) hours = 12;
		format = format.replace("hh",hours.toString().padL(2,"0"));        
    }

    if (format.indexOf("mm") > -1) format = format.replace("mm",date.getMinutes().toString().padL(2,"0"));
    if (format.indexOf("ss") > -1) format = format.replace("ss",date.getSeconds().toString().padL(2,"0"));

    return format;
}

String.repeat = function(chr,count) {    
    var str = ""; 
    for(var x=0;x<count;x++) {str += chr}; 
    return str;
}

String.prototype.padL = function(width,pad) {
	if (!width ||width < 1) return this;
	if (!pad) pad = " ";
    
	var length = width - this.length
	if (length < 1) return this.substr(0, width);
    
	return (String.repeat(pad,length) + this).substr(0,width);    
}
 
String.prototype.padR = function(width,pad) {
    if (!width || width<1) return this;
	if (!pad) pad = " ";

	var length = width - this.length
	if (length < 1) this.substr(0, width);
	return (this + String.repeat(pad,length)).substr(0,width);
}