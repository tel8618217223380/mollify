$(function() {
	var saveCmd = {
		modes : { wysiwyg:1, source:1 },
		exec : function( editor ) {
			alert("on save");
		}
    }
  
	var pluginName = 'custom_save';
	CKEDITOR.plugins.add( pluginName, {
		init : function( editor ) {
	        var command = editor.addCommand( pluginName, saveCmd );
	        
			editor.ui.addButton( 'CustomSave', {
				label : editor.lang.save,
				command : pluginName,
				icon: "/img/save.png"
			});
		}
	});
	
	var editor = $('#ckeditor').ckeditor({
		extraPlugins : 'autogrow',
		removePlugins : 'resize',
		toolbar: [
			['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink'],
			['UIColor']
		]
	});

	$(editor).bind('saved.ckeditor', function() {
		alert('SAVE');
	});
});