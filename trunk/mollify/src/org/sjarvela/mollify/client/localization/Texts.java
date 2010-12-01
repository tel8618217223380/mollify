/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.localization;

import org.sjarvela.mollify.client.ResourceId;

public enum Texts implements ResourceId {

	pleaseWait,

	shortDateTimeFormat,

	permissionModeNone,

	permissionModeAdmin,

	permissionModeReadWrite,

	permissionModeReadOnly,

	loginDialogTitle,

	loginDialogUsername,

	loginDialogPassword,

	loginDialogResetPassword,

	loginDialogLoginButton,

	loginDialogLoginFailedMessage,

	mainViewParentDirButtonTitle,

	mainViewRefreshButtonTitle,

	mainViewAdministrationTitle,

	mainViewEditPermissionsTitle,

	mainViewLogoutButtonTitle,

	mainViewChangePasswordTitle,

	mainViewAddButtonTitle,

	mainViewAddFileMenuItem,

	mainViewAddDirectoryMenuItem,

	mainViewRetrieveUrlMenuItem,

	fileDetailsLabelLastAccessed,

	fileDetailsLabelLastModified,

	fileDetailsLabelLastChanged,

	fileActionDetailsTitle,

	fileActionDownloadTitle,

	fileActionDownloadZippedTitle,

	fileActionRenameTitle,

	fileActionCopyTitle,

	fileActionCopyHereTitle,

	fileActionMoveTitle,

	fileActionDeleteTitle,

	fileActionViewTitle,

	fileActionPublicLinkTitle,

	filePreviewTitle,

	fileDetailsActionsTitle,

	dirActionDownloadTitle,

	dirActionRenameTitle,

	dirActionDeleteTitle,

	fileListColumnTitleSelect,

	fileListColumnTitleName,

	fileListColumnTitleType,

	fileListColumnTitleSize,

	fileListDirectoryType,

	errorMessageRequestFailed,

	errorMessageInvalidRequest,

	errorMessageNoResponse,

	errorMessageInvalidResponse,

	errorMessageDataTypeMismatch,

	errorMessageOperationFailed,

	errorMessageAuthenticationFailed,

	errorMessageInvalidConfiguration,

	errorMessageUnknown,

	directorySelectorSeparatorLabel,

	directorySelectorMenuPleaseWait,

	directorySelectorMenuNoItemsText,

	infoDialogInfoTitle,

	infoDialogErrorTitle,

	confirmationDialogYesButton,

	confirmationDialogNoButton,

	dialogOkButton,

	dialogCancelButton,

	dialogCloseButton,

	renameDialogTitleFile,

	renameDialogTitleDirectory,

	renameDialogOriginalName,

	renameDialogNewName,

	renameDialogRenameButton,

	deleteFileConfirmationDialogTitle,

	deleteDirectoryConfirmationDialogTitle,

	fileUploadDialogTitle,

	fileUploadDialogMessage,

	fileUploadDialogUploadButton,

	fileUploadDialogAddFileButton,

	fileUploadDialogAddFilesButton,

	fileUploadDialogRemoveFileButton,

	fileUploadDialogInfoTitle,

	fileUploadProgressTitle,

	fileUploadProgressPleaseWait,

	fileUploadDialogMessageFileCompleted,

	fileUploadDialogMessageFileCancelled,

	fileUploadTotalProgressTitle,

	fileUploadDialogSelectFileTypesDescription,

	createFolderDialogTitle,

	createFolderDialogName,

	createFolderDialogCreateButton,

	errorMessageFileAlreadyExists,

	errorMessageDirectoryAlreadyExists,

	errorMessageDirectoryDoesNotExist,

	errorMessageInsufficientRights,

	selectFolderDialogSelectButton,

	selectFolderDialogFoldersRoot,

	selectFolderDialogRetrievingFolders,

	copyFileDialogTitle,

	copyFileDialogAction,

	moveFileDialogTitle,

	moveFileDialogAction,

	passwordDialogTitle,

	passwordDialogOriginalPassword,

	passwordDialogNewPassword,

	passwordDialogConfirmNewPassword,

	passwordDialogChangeButton,

	passwordDialogPasswordChangedSuccessfully,

	passwordDialogOldPasswordIncorrect,

	configurationDialogTitle,

	configurationDialogCloseButton,

	configurationDialogSettingUsers,

	configurationDialogSettingFolders,

	configurationDialogSettingUserFolders,

	configurationDialogSettingUsersViewTitle,

	configurationDialogSettingUsersAdd,

	configurationDialogSettingUsersEdit,

	configurationDialogSettingUsersRemove,

	configurationDialogSettingUsersResetPassword,

	configurationDialogSettingUsersCannotDeleteYourself,

	configurationDialogSettingFoldersViewTitle,

	configurationDialogSettingFoldersAdd,

	configurationDialogSettingFoldersEdit,

	configurationDialogSettingFoldersRemove,

	configurationDialogSettingUserFoldersViewTitle,

	configurationDialogSettingUserFoldersSelectUser,

	configurationDialogSettingUserFoldersAdd,

	configurationDialogSettingUserFoldersEdit,

	configurationDialogSettingUserFoldersRemove,

	configurationDialogSettingUserFoldersNoFoldersAvailable,

	userListColumnTitleName,

	userListColumnTitleType,

	userDialogAddTitle,

	userDialogEditTitle,

	userDialogUserName,

	userDialogUserType,

	userDialogPassword,

	userDialogGeneratePassword,

	userDialogAddButton,

	userDialogEditButton,

	folderListColumnTitleName,

	folderListColumnTitleLocation,

	folderDialogAddTitle,

	folderDialogEditTitle,

	folderDialogName,

	folderDialogPath,

	folderDialogAddButton,

	folderDialogEditButton,

	resetPasswordDialogTitle,

	resetPasswordDialogPassword,

	resetPasswordDialogGeneratePassword,

	resetPasswordDialogResetButton,

	userFolderDialogAddTitle,

	userFolderDialogEditTitle,

	userFolderDialogDirectoriesTitle,

	userFolderDialogUseDefaultName,

	userFolderDialogName,

	userFolderDialogAddButton,

	userFolderDialogEditButton,

	userFolderDialogSelectFolder,

	userFolderDialogDefaultNameTitle,

	fileDetailsAddDescription,

	fileDetailsEditDescription,

	fileDetailsApplyDescription,

	fileDetailsCancelEditDescription,

	fileDetailsRemoveDescription,

	fileDetailsEditPermissions,

	copyDirectoryDialogTitle,

	copyDirectoryDialogAction,

	moveDirectoryDialogTitle,

	moveDirectoryDialogAction,

	invalidDescriptionUnsafeTags,

	itemPermissionEditorDialogTitle,

	itemPermissionEditorItemTitle,

	itemPermissionEditorDefaultPermissionTitle,

	itemPermissionEditorNoPermission,

	itemPermissionListColumnTitleName,

	itemPermissionListColumnTitlePermission,

	itemPermissionEditorSelectItemMessage,

	itemPermissionEditorButtonSelectItem,

	itemPermissionEditorButtonAddUserPermission,

	itemPermissionEditorButtonAddUserGroupPermission,

	itemPermissionEditorButtonEditPermission,

	itemPermissionEditorButtonRemovePermission,

	itemPermissionEditorConfirmItemChange,

	fileItemUserPermissionDialogAddTitle,

	fileItemUserPermissionDialogAddGroupTitle,

	fileItemUserPermissionDialogEditTitle,

	fileItemUserPermissionDialogEditGroupTitle,

	fileItemUserPermissionDialogName,

	fileItemUserPermissionDialogPermission,

	fileItemUserPermissionDialogAddButton,

	fileItemUserPermissionDialogEditButton,

	selectItemDialogTitle,

	selectPermissionItemDialogMessage,

	selectPermissionItemDialogAction,

	mainViewAddButtonTooltip,

	mainViewRefreshButtonTooltip,

	mainViewParentDirButtonTooltip,

	mainViewHomeButtonTooltip,

	copyMultipleItemsTitle,

	cannotCopyAllItemsMessage,

	moveMultipleItemsTitle,

	cannotMoveAllItemsMessage,

	dropBoxTitle,

	dropBoxActions,

	dropBoxActionClear,

	dropBoxActionCopy,

	dropBoxActionCopyHere,

	dropBoxActionMove,

	dropBoxActionMoveHere,

	mainViewSelectButton,

	mainViewSelectAll,

	mainViewSelectNone,

	mainViewSelectActions,

	mainViewSelectActionAddToDropbox,

	mainViewDropBoxButton,

	mainViewSearchHint,

	fileViewerOpenInNewWindowTitle,

	filePublicLinkTitle,

	copyHereDialogTitle,

	resetPasswordPopupMessage,

	resetPasswordPopupButton,

	resetPasswordPopupTitle,

	resetPasswordPopupInvalidEmail,

	resetPasswordPopupResetFailed,

	resetPasswordPopupResetSuccess,

	retrieveUrlTitle,

	retrieveUrlMessage,

	retrieveUrlFailed,

	searchResultsDialogTitle,

	searchResultListColumnTitlePath,

	searchResultsNoMatchesFound,

}
