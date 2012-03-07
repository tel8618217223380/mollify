(function(){ mollify.texts.set('en', {

decimalSeparator: ".",
groupingSeparator: ",",
zeroDigit: "0",
plusSign: "+",
minusSign: "-",

fileSizeFormat: "#.#",
sizeInBytes: "{0} bytes",
sizeOneByte: "1 byte",
sizeOneKilobyte: "1 kB",
sizeInKilobytes: "{0} kB",
sizeInMegabytes: "{0} MB",
sizeInGigabytes: "{0} GB",

confirmFileDeleteMessage: "Are you sure you want to delete file \"{0}\"?",
confirmDirectoryDeleteMessage: "Are you sure you want to delete directory \"{0}\" and all its files and subdirectories?",

uploadingNFilesInfo: "Uploading {0} files",
uploadMaxSizeHtml: "<span class=\"mollify-upload-info\">Maximum uploaded file size is <span class=\"mollify-upload-info-size\">{0}</span>, and maximum size of all files is <span class=\"mollify-upload-info-size\">{1}</span>.</span>",
fileUploadDialogUnallowedFileType: "Uploading files of type \"{0}\" is not allowed.",
fileUploadSizeTooBig: "File \"{0}\" size {1} exceeds the maximum allowed upload size of {2}.",
fileUploadTotalSizeTooBig: "Selected files exceed the maximum allowed total upload size of {0}.",  

copyFileMessage: "<span class=\"mollify-copy-file-message\">Select the folder in which the file <span class=\"mollify-copy-file-message-file\">\"{0}\"</span> is copied to:</span>",
moveFileMessage: "<span class=\"mollify-move-file-message\">Select the folder in which the file <span class=\"mollify-move-file-message-file\">\"{0}\"</span> is moved to:</span>",
copyDirectoryMessage: "<span class=\"mollify-copy-directory-message\">Select the folder in which the folder <span class=\"mollify-copy-directory-message-directory\">\"{0}\"</span> is copied to:</span>",
moveDirectoryMessage: "<span class=\"mollify-move-directory-message\">Select the folder in which the folder <span class=\"mollify-move-directory-message-directory\">\"{0}\"</span> is moved to:</span>",

userDirectoryListDefaultName: "{0} (Default)",

confirmMultipleItemDeleteMessage: "Are you sure you want to delete {0} items?",
copyMultipleItemsMessage: "<span class=\"mollify-copy-items-message\">Select the folder in which the {0} items are copied to:</span>",
moveMultipleItemsMessage: "<span class=\"mollify-move-items-message\">Select the folder in which the {0} items are moved to:</span>",

dragMultipleItems: "{0} items",

publicLinkMessage: "Public link for \"{0}\":",
copyHereDialogMessage: "<span class=\"mollify-copy-here-message\">Enter the name for the copy of <span class=\"mollify-copy-file-message-file\">\"{0}\"</span>:</span>",

searchResultsInfo: "Following matches ({1}) found with \"{0}\":",

retrieveUrlNotFound: "Resource not found: {0}",
retrieveUrlNotAuthorized: "Unauthorized resource: {0}",

shortDateTimeFormat: "M/d/yyyy h:mm:ss a",
shortDateFormat: "M/d/yyyy",
timeFormat: "h:mm:ss a",

pleaseWait: "Please wait...",

permissionModeAdmin: "Administrator",
permissionModeReadWrite: "Read and write",
permissionModeReadOnly: "Read only",
permissionModeNone: "No rights",

loginDialogTitle: "Login",
loginDialogUsername: "Username:",
loginDialogPassword: "Password:",
loginDialogRememberMe: "Remember me",
loginDialogResetPassword: "Forgot password?",
loginDialogLoginButton: "Log in",
loginDialogLoginFailedMessage: "Login failed, user name or password was incorrect.",

resetPasswordPopupMessage: "To reset your password, enter your email address:",
resetPasswordPopupButton: "Reset",
resetPasswordPopupTitle: "Reset password",
resetPasswordPopupInvalidEmail: "No user was found with given email address",
resetPasswordPopupResetFailed: "Reset password failed",
resetPasswordPopupResetSuccess: "Your password has been reset. Check your email for details.",

mainViewParentDirButtonTitle: "..",
mainViewRefreshButtonTitle: "Refresh",
mainViewChangePasswordTitle: "Change password...",
mainViewAdministrationTitle: "Administration...",
mainViewEditPermissionsTitle: "File permissions...",
mainViewLogoutButtonTitle: "Logout",
mainViewAddButtonTitle: "Add",
mainViewAddFileMenuItem: "Add files...",
mainViewAddDirectoryMenuItem: "Add folder...",
mainViewRetrieveUrlMenuItem: "Retrieve from URL...",
mainViewAddButtonTooltip: "Add files or folders",
mainViewRefreshButtonTooltip: "Refresh",
mainViewParentDirButtonTooltip: "Up one level",
mainViewHomeButtonTooltip: "Root folders",
mainViewSearchHint: "Search",
mainViewSlideBarTitleSelect: "Select Mode",
mainViewOptionsListTooltip: "List",
mainViewOptionsGridSmallTooltip: "Small Icons",
mainViewOptionsGridLargeTooltip: "Large Icons",

fileDetailsAddDescription: "Add description",
fileDetailsEditDescription: "Edit",
fileDetailsApplyDescription: "Apply",
fileDetailsCancelEditDescription: "Cancel",
fileDetailsRemoveDescription: "Remove",
fileDetailsEditPermissions: "Edit permissions",
fileDetailsActionsTitle: "Actions",

filePreviewTitle: "Preview",

fileActionDetailsTitle: "Details",
fileActionDownloadTitle: "Download",
fileActionDownloadZippedTitle: "Download zipped",
fileActionRenameTitle: "Rename...",
fileActionCopyTitle: "Copy...",
fileActionCopyHereTitle: "Copy Here...",
fileActionMoveTitle: "Move...",
fileActionDeleteTitle: "Delete",
fileActionViewTitle: "View",
fileActionEditTitle: "Edit",
dirActionDownloadTitle: "Download zipped",
dirActionRenameTitle: "Rename...",
dirActionDeleteTitle: "Delete",
fileActionPublicLinkTitle: "Get public link...",

fileListColumnTitleSelect: "",
fileListColumnTitleName: "Name",
fileListColumnTitleType: "Type",
fileListColumnTitleSize: "Size",
fileListColumnTitleLastModified: "Last Modified",
fileListColumnTitleDescription: "Description",
fileListDirectoryType: "Folder",

errorMessageRequestFailed: "Request failed",
errorMessageInvalidRequest: "Server could not process the request.",
errorMessageNoResponse: "Failed to get response from server.",
errorMessageInvalidResponse: "Server returned invalid response.",
errorMessageDataTypeMismatch: "Server returned unexpected result.",
errorMessageOperationFailed: "Operation failed.",
errorMessageAuthenticationFailed: "Authentication failed.",
errorMessageInvalidConfiguration: "Application configuration is invalid.",
errorMessageDirectoryAlreadyExists: "Folder already exists.",
errorMessageFileAlreadyExists: "File already exists.",
errorMessageDirectoryDoesNotExist: "Folder does not exist.",
errorMessageInsufficientRights: "Insufficient rights to perform the action.",
errorMessageUnknown: "Unknown error occurred.",

directorySelectorSeparatorLabel: "/",
directorySelectorMenuPleaseWait: "Please wait...",
directorySelectorMenuNoItemsText: "No folders",

infoDialogInfoTitle: "Information",
infoDialogErrorTitle: "Error",

confirmationDialogYesButton: "Yes",
confirmationDialogNoButton: "No",

dialogOkButton: "OK",
dialogCancelButton: "Cancel",
dialogCloseButton: "Close",

deleteFileConfirmationDialogTitle: "Delete file",
deleteDirectoryConfirmationDialogTitle: "Delete folder",

renameDialogTitleFile: "Rename file",
renameDialogTitleDirectory: "Rename folder",
renameDialogOriginalName: "Original name:",
renameDialogNewName: "New name:",
renameDialogRenameButton: "Rename",

fileUploadDialogTitle: "Upload file",
fileUploadDialogMessage: "Select the file(s) to be uploaded:",
fileUploadDialogUploadButton: "Upload",
fileUploadDialogAddFileButton: "+",
fileUploadDialogAddFilesButton: "Add file(s)...",
fileUploadDialogRemoveFileButton: "-",
fileUploadDialogInfoTitle: "Details",
fileUploadFileExists: "Following files already exist in the folder: {0}",

fileUploadProgressTitle: "Uploading file",
fileUploadProgressPleaseWait: "Please wait...",
fileUploadDialogMessageFileCompleted: "Completed",
fileUploadDialogMessageFileCancelled: "Cancelled",
fileUploadTotalProgressTitle: "Total:",
fileUploadDialogSelectFileTypesDescription: "Allowed files",

createFolderDialogTitle: "Create new folder",
createFolderDialogName: "Folder name:",
createFolderDialogCreateButton: "Create",

selectFolderDialogSelectButton: "Select",
selectFolderDialogFoldersRoot: "Folders",
selectFolderDialogRetrievingFolders: "Please wait...",

selectItemDialogTitle: "Select file or folder",
selectPermissionItemDialogMessage: "Select the file or folder for which permissions are edited:",
selectPermissionItemDialogAction: "Select",

copyFileDialogTitle: "Copy file",
copyFileDialogAction: "Copy",

moveFileDialogTitle: "Move file",
moveFileDialogAction: "Move",

moveDirectoryDialogTitle: "Move folder",
moveDirectoryDialogAction: "Move",

copyDirectoryDialogTitle: "Copy folder",
copyDirectoryDialogAction: "Copy",

copyMultipleItemsTitle: "Copy items",
cannotCopyAllItemsMessage: "Cannot copy all items to the selected folder.",

moveMultipleItemsTitle: "Move items",
cannotMoveAllItemsMessage: "Cannot move all items to the selected folder.",
	
passwordDialogTitle: "Change password",
passwordDialogOriginalPassword: "Current password:",
passwordDialogNewPassword: "New password:",
passwordDialogConfirmNewPassword: "Confirm new password:",
passwordDialogChangeButton: "Change",
passwordDialogPasswordChangedSuccessfully: "Password has been changed successfully",
passwordDialogOldPasswordIncorrect: "Current password was incorrect, password is not changed",

configurationDialogTitle: "Configuration",
configurationDialogCloseButton: "Close",
configurationDialogSettingUsers: "Users",
configurationDialogSettingFolders: "Published folders",
configurationDialogSettingUserFolders: "User folders",
configurationDialogSettingUsersViewTitle: "Users",
configurationDialogSettingUsersAdd: "Add...",
configurationDialogSettingUsersEdit: "Edit...",
configurationDialogSettingUsersRemove: "Remove",
configurationDialogSettingUsersResetPassword: "Reset password...",
configurationDialogSettingUsersCannotDeleteYourself: "You cannot remove your own user account.",

configurationDialogSettingFoldersViewTitle: "Published folders",
configurationDialogSettingFoldersAdd: "Add...",
configurationDialogSettingFoldersEdit: "Edit...",
configurationDialogSettingFoldersRemove: "Remove",

configurationDialogSettingUserFoldersViewTitle: "User folders",
configurationDialogSettingUserFoldersSelectUser: "Select user",
configurationDialogSettingUserFoldersAdd: "Add...",
configurationDialogSettingUserFoldersEdit: "Edit...",
configurationDialogSettingUserFoldersRemove: "Remove",
configurationDialogSettingUserFoldersNoFoldersAvailable: "There are no folders to add for current user.",

userListColumnTitleName: "Name",
userListColumnTitleType: "Type",
folderListColumnTitleName: "Name",
folderListColumnTitleLocation: "Location",

userDialogAddTitle: "Add user",
userDialogEditTitle: "Edit user",
userDialogUserName: "Name:",
userDialogUserType: "Type:",
userDialogPassword: "Password:",
userDialogGeneratePassword: "New",
userDialogAddButton: "Add",
userDialogEditButton: "Edit",

folderDialogAddTitle: "Add folder",
folderDialogEditTitle: "Edit folder",
folderDialogName: "Folder public name:",
folderDialogPath: "Folder path (not visible to users):",
folderDialogAddButton: "Add",
folderDialogEditButton: "Edit",

resetPasswordDialogTitle: "Reset password",
resetPasswordDialogPassword: "New password:",
resetPasswordDialogGeneratePassword: "New",
resetPasswordDialogResetButton: "Reset",

userFolderDialogAddTitle: "Add user folder",
userFolderDialogEditTitle: "Edit user folder",
userFolderDialogDirectoriesTitle: "Folder:",
userFolderDialogUseDefaultName: "Use default folder name",
userFolderDialogName: "Folder public name:",
userFolderDialogDefaultNameTitle: "Default folder name:",
userFolderDialogAddButton: "Add",
userFolderDialogEditButton: "Edit",
userFolderDialogSelectFolder: "Select folder",

invalidDescriptionUnsafeTags: "Illegal HTML tags used in description, only \"safe HTML\" is allowed.",

itemPermissionEditorDialogTitle: "Edit permissions",
itemPermissionEditorItemTitle: "Name:",
itemPermissionEditorDefaultPermissionTitle: "Default permission:",
itemPermissionEditorNoPermission: "-",
itemPermissionListColumnTitleName: "Name",
itemPermissionListColumnTitlePermission: "Permission",
itemPermissionEditorSelectItemMessage: "Select item",
itemPermissionEditorButtonSelectItem: "...",
itemPermissionEditorButtonAddUserPermission: "Add User",
itemPermissionEditorButtonAddUserGroupPermission: "Add Group",
itemPermissionEditorButtonEditPermission: "Edit",
itemPermissionEditorButtonRemovePermission: "Remove",
itemPermissionEditorConfirmItemChange: "There are unsaved changes, are you sure you want to continue?",

fileItemUserPermissionDialogAddTitle: "Add user permission",
fileItemUserPermissionDialogAddGroupTitle: "Add group permission",
fileItemUserPermissionDialogEditTitle: "Edit user permission",
fileItemUserPermissionDialogEditGroupTitle: "Edit group permission",
fileItemUserPermissionDialogName: "Name:",
fileItemUserPermissionDialogPermission: "Permission:",
fileItemUserPermissionDialogAddButton: "Add",
fileItemUserPermissionDialogEditButton: "Edit",

dropBoxTitle: "Dropbox",
dropBoxActions: "Actions",
dropBoxActionClear: "Clear",
dropBoxActionCopy: "Copy...",
dropBoxActionCopyHere: "Copy Here",
dropBoxActionMove: "Move...",
dropBoxActionMoveHere: "Move Here",
dropBoxNoItems: "Add items here",

mainViewSelectButton: "Select",
mainViewSelectAll: "Select All",
mainViewSelectNone: "Select None",
mainViewSelectActions: "Actions",
mainViewSelectActionAddToDropbox: "Add to Dropbox",
mainViewDropBoxButton: "Dropbox",

fileViewerOpenInNewWindowTitle: "Open in New Window",
fileEditorSave: "Save",

filePublicLinkTitle: "Public Link",

copyHereDialogTitle: "Copy Here",

retrieveUrlTitle: "Retrieve from URL",
retrieveUrlMessage: "Enter the URL to retrieve the file from:",
retrieveUrlFailed: "Could not retrieve the file.",

searchResultsDialogTitle: "Search Results",
searchResultsNoMatchesFound: "No matches found.",
searchResultListColumnTitlePath: "Path",
searchResultsTooltipMatches: "Matches:",
searchResultsTooltipMatchName: "Name",
searchResultsTooltipMatchDescription: "Description",

fileItemContextDataName: "Name",
fileItemContextDataPath: "Path",
fileItemContextDataSize: "File size",
fileItemContextDataExtension: "Extension",
fileItemContextDataLastModified: "Last modified",
fileItemDetailsExif: "Image information",
fileItemContextDataImageSize: "Image size",
fileItemContextDataImageSizePixels: "{0} pixels"
})})();