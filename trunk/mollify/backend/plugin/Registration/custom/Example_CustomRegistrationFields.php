<!--
	Add here your own fields without HTML head or body, this is injected directly into the form.

	Example:
-->
			<div class="registration-form-field">
				<div class="registration-field-title">My field1:</div>
				<input type="text" id="my-field1" class="registration-field custom-field"></input>
			</div>

<!--
	Define here javascript code that validates and provides the saved data. Remember to keep the function names as "onValidateCustomFields" and 
	"getCustomRegistrationData". The function "getCustomRegistrationData" has to return JSON object with saved data.
-->
<script>
	function onValidateCustomFields() {
		// validate custom fields, and show your notification on validation errors here
	}
	
	function getCustomRegistrationData() {
		return {
			"my-field1": $("#my-field1").val()
		};
	}
</script>