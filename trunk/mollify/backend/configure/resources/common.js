/**
	Copyright (c) 2008- Samuli JŠrvelŠ

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

jQuery.fn.exists = function() { return ($(this).length > 0); }

function initializeButtons() {
	$('.btn').each(function() {
		var b = $(this);
		var tt = b.text() || b.val();
		
		if ($(':submit,:button',this)) {
			b = $('<a>').insertAfter(this).addClass(this.className).attr('id',this.id);
			$(this).remove();
		}
		b.text('').css({cursor:'pointer'}).prepend('<i></i>').append($('<span>').
		text(tt).append('<i></i><span></span>'));
	});
}

function getSessionInfo(success, fail) {
	$.ajax({
		type: "GET",
		url: 'r.php/session/info/1_5_0',
		dataType: "json",
		success: function(result) {
			success(result.result);
		},
		error: function (xhr, desc, exc) {
			fail(xhr.responseText);
		}
	});

}
