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

public interface LanguageConstants extends com.google.gwt.i18n.client.Constants {
	public String shortDateTimeFormat();

	public String permissionModeNone();

	public String permissionModeAdmin();

	public String permissionModeReadWrite();

	public String permissionModeReadOnly();

	public String loginDialogTitle();

	public String loginDialogUsername();

	public String loginDialogPassword();

	public String loginDialogLoginButton();

	public String loginDialogLoginFailedMessage();

	public String mainViewParentDirButtonTitle();

	public String mainViewRefreshButtonTitle();

	public String mainViewConfigurationTitle();

	public String mainViewEditPermissionsTitle();

	public String mainViewLogoutButtonTitle();

	public String mainViewChangePasswordTitle();

	public String mainViewAddButtonTitle();

	public String mainViewAddFileMenuItem();

	public String mainViewAddDirectoryMenuItem();

	public String fileDetailsLabelLastAccessed();

	public String fileDetailsLabelLastModified();

	public String fileDetailsLabelLastChanged();

	public String fileActionDetailsTitle();

	public String fileActionDownloadTitle();

	public String fileActionDownloadZippedTitle();

	public String fileActionRenameTitle();

	public String fileActionCopyTitle();

	public String fileActionMoveTitle();

	public String fileActionDeleteTitle();

	public String dirActionDownloadTitle();

	public String dirActionRenameTitle();

	public String dirActionDeleteTitle();

	public String fileListColumnTitleSelect();

	public String fileListColumnTitleName();

	public String fileListColumnTitleType();

	public String fileListColumnTitleSize();

	public String fileListDirectoryType();

	public String errorMessageRequestFailed();

	public String errorMessageInvalidRequest();

	public String errorMessageNoResponse();

	public String errorMessageInvalidResponse();

	public String errorMessageDataTypeMismatch();

	public String errorMessageOperationFailed();

	public String errorMessageAuthenticationFailed();

	public String errorMessageInvalidConfiguration();

	public String errorMessageUnknown();

	public String directorySelectorSeparatorLabel();

	public String directorySelectorMenuPleaseWait();

	public String directorySelectorMenuNoItemsText();

	public String infoDialogInfoTitle();

	public String infoDialogErrorTitle();

	public String confirmationDialogYesButton();

	public String confirmationDialogNoButton();

	public String dialogOkButton();

	public String dialogCancelButton();

	public String dialogCloseButton();

	public String renameDialogTitleFile();

	public String renameDialogTitleDirectory();

	public String renameDialogOriginalName();

	public String renameDialogNewName();

	public String renameDialogRenameButton();

	public String deleteFileConfirmationDialogTitle();

	public String deleteDirectoryConfirmationDialogTitle();

	public String fileUploadDialogTitle();

	public String fileUploadDialogMessage();

	public String fileUploadDialogUploadButton();

	public String fileUploadDialogAddFileButton();

	public String fileUploadDialogRemoveFileButton();

	public String fileUploadDialogInfoTitle();

	public String fileUploadProgressTitle();

	public String fileUploadProgressPleaseWait();
	
	public String fileUploadDialogMessageFileCompleted();

	public String createFolderDialogTitle();

	public String createFolderDialogName();

	public String createFolderDialogCreateButton();

	public String errorMessageFileAlreadyExists();

	public String errorMessageDirectoryAlreadyExists();

	public String errorMessageDirectoryDoesNotExist();

	public String selectFolderDialogSelectButton();

	public String selectFolderDialogFoldersRoot();

	public String selectFolderDialogRetrievingFolders();

	public String copyFileDialogTitle();

	public String copyFileDialogAction();

	public String moveFileDialogTitle();

	public String moveFileDialogAction();

	public String passwordDialogTitle();

	public String passwordDialogOriginalPassword();

	public String passwordDialogNewPassword();

	public String passwordDialogConfirmNewPassword();

	public String passwordDialogChangeButton();

	public String passwordDialogPasswordChangedSuccessfully();

	public String passwordDialogOldPasswordIncorrect();

	public String configurationDialogTitle();

	public String configurationDialogCloseButton();

	public String configurationDialogSettingUsers();

	public String configurationDialogSettingFolders();

	public String configurationDialogSettingUserFolders();

	public String configurationDialogSettingUsersViewTitle();

	public String configurationDialogSettingUsersAdd();

	public String configurationDialogSettingUsersEdit();

	public String configurationDialogSettingUsersRemove();

	public String configurationDialogSettingUsersResetPassword();

	public String configurationDialogSettingUsersCannotDeleteYourself();

	public String configurationDialogSettingFoldersViewTitle();

	public String configurationDialogSettingFoldersAdd();

	public String configurationDialogSettingFoldersEdit();

	public String configurationDialogSettingFoldersRemove();

	public String configurationDialogSettingUserFoldersViewTitle();

	public String configurationDialogSettingUserFoldersSelectUser();

	public String configurationDialogSettingUserFoldersAdd();

	public String configurationDialogSettingUserFoldersEdit();

	public String configurationDialogSettingUserFoldersRemove();

	public String configurationDialogSettingUserFoldersNoFoldersAvailable();

	public String userListColumnTitleName();

	public String userListColumnTitleType();

	public String userDialogAddTitle();

	public String userDialogEditTitle();

	public String userDialogUserName();

	public String userDialogUserType();

	public String userDialogPassword();

	public String userDialogGeneratePassword();

	public String userDialogAddButton();

	public String userDialogEditButton();

	public String folderListColumnTitleName();

	public String folderListColumnTitleLocation();

	public String folderDialogAddTitle();

	public String folderDialogEditTitle();

	public String folderDialogName();

	public String folderDialogPath();

	public String folderDialogAddButton();

	public String folderDialogEditButton();

	public String resetPasswordDialogTitle();

	public String resetPasswordDialogPassword();

	public String resetPasswordDialogGeneratePassword();

	public String resetPasswordDialogResetButton();

	public String userFolderDialogAddTitle();

	public String userFolderDialogEditTitle();

	public String userFolderDialogDirectoriesTitle();

	public String userFolderDialogUseDefaultName();

	public String userFolderDialogName();

	public String userFolderDialogAddButton();

	public String userFolderDialogEditButton();

	public String userFolderDialogSelectFolder();

	public String userFolderDialogDefaultNameTitle();

	public String fileDetailsAddDescription();

	public String fileDetailsEditDescription();

	public String fileDetailsApplyDescription();

	public String fileDetailsCancelEditDescription();

	public String fileDetailsRemoveDescription();

	public String fileDetailsEditPermissions();

	public String moveDirectoryDialogTitle();

	public String moveDirectoryDialogAction();

	public String invalidDescriptionUnsafeTags();

	public String itemPermissionEditorDialogTitle();

	public String itemPermissionEditorItemTitle();

	public String itemPermissionEditorDefaultPermissionTitle();

	public String itemPermissionListColumnTitleUser();

	public String itemPermissionListColumnTitlePermission();

	public String itemPermissionEditorSelectItemMessage();

	public String itemPermissionEditorButtonSelectItem();

	public String itemPermissionEditorButtonAddPermission();

	public String itemPermissionEditorButtonEditPermission();

	public String itemPermissionEditorButtonRemovePermission();

	public String itemPermissionEditorConfirmItemChange();

	public String fileItemUserPermissionDialogAddTitle();

	public String fileItemUserPermissionDialogEditTitle();

	public String fileItemUserPermissionDialogUser();

	public String fileItemUserPermissionDialogPermission();

	public String fileItemUserPermissionDialogAddButton();

	public String fileItemUserPermissionDialogEditButton();

	public String selectItemDialogTitle();

	public String selectPermissionItemDialogMessage();

	public String selectPermissionItemDialogAction();

	public String mainViewAddButtonTooltip();

	public String mainViewRefreshButtonTooltip();

	public String mainViewParentDirButtonTooltip();

	public String mainViewHomeButtonTooltip();

}
