/*
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

var create_db = false;

function add_retry_button(action) {
	add_button('#db-footer', 'db-retry', 'Retry', action);
}

function add_continue_button(action) {
	add_button('#db-footer', 'db-continue', 'Continue', action);
}

function on_conf_error(error) {	
	if (error.id == 'MOLLIFY_ALREADY_INSTALLED') {
		show_error(error, true);
		$("#title").hide();
		$("#db-content").hide();
	} else {
		show_error(error);
		$("#db-content").load("mysql/instructions-configuration.html");
		add_retry_button(check_configuration);
	}
}
	
function on_conf_success(result) {
	create_db = !result.database_exists;
	
	var row = $.template("<div class='list-item'><div class='name'>${name}:</div><div class='value'><code>${value}</code> ${option}</div></div>");
	
	$("#db-content").html('<span class="info">Verify database information, and click "Continue".</span>');
	$("#db-content").append('<div class="list" id="db-conf">');
	$("#db-content #db-conf").append(row, { name: 'Host', value: result.db.host, option: result.db.host_defined ? '' : ' (default)'});
	$("#db-content #db-conf").append(row, { name: 'Database', value: result.db.database, option: result.db.database_defined ? '' : ' (default)'});
	$("#db-content #db-conf").append(row, { name: 'Username', value: result.db.user});

	if (!result.connection_success) {
		show_error_msg("Could not connect to database, check database configuration and retry");
		add_retry_button(check_configuration);
	} else {		
		if (!result.database_exists) {
			$("#db-content").append('<span class="note">Database "' + result.db.database + '" does not exist. If you continue with installation, it will be created.</span>');
		}
		
		$("#db-content").append('<span class="confirmation">Are you sure you want to continue installation with this database?</span>');
		
		add_continue_button(function() { create_and_check_db(); });
	}
}

function check_configuration() {
	empty_errors();
	$("#db-footer").html("<div class='progress'>Checking configuration...</div>");
	
	request("mysql/installation.php", "action=check_db_conf",
		function(result) {
			$("#db-footer").empty();
			
			if (!result.success) on_conf_error(result.error);
			else on_conf_success(result.result);
		}
	);
}

function on_db_error(error) {
	show_error(error);
	if (error.id == 'COULD_NOT_CREATE_DB') {
		$("#error .error-msg").append("<div class='details'>Try creating database manually, and retry</div>");
	}
	
	add_retry_button(create_and_check_db);
}

function create_and_check_db() {
	empty_errors();
	$("#db-footer").html("<div class='progress'>Checking database...</div>");
	
	request("mysql/installation.php", "action=create_and_check_db&create="+create_db,
		function(result) {
			$("#db-buttons").empty();
			
			if (!result.success) on_db_error(result.error);
			else open_page("mysql", "configuration");
		}
	);
}

function init_page_database() {
	set_multipage_title(1, 3, "Database Configuration");
	check_configuration();
}