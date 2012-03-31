(function(){ mollify.texts.set('fr', {

decimalSeparator: ",",
groupingSeparator: " ",
zeroDigit: "0",
plusSign: "+",
minusSign: "-",

fileSizeFormat: "#.#",
sizeInBytes: "{0} octets",
sizeOneByte: "1 octet",
sizeOneKilobyte: "1 ko",
sizeInKilobytes: "{0} ko",
sizeInMegabytes: "{0} Mo",
sizeInGigabytes: "{0} Go",

confirmFileDeleteMessage: "Êtes-vous certain de vouloir suprimer le fichier \"{0}\" ?",
confirmDirectoryDeleteMessage: "Êtes-vous certain de vouloir supprimer le répertoire \"{0}\" et tous les fichiers et sous-répertoires qu'il contient ?",

uploadingNFilesInfo: "Envoi de {0} fichiers",
uploadMaxSizeHtml: "<span class=\"mollify-upload-info\">La taille maximale d'un fichier envoyé est <span class=\"mollify-upload-info-size\">{0}</span>, et la taille maximale de tous les fichiers est <span class=\"mollify-upload-info-size\">{1}</span>.</span>",
fileUploadDialogUnallowedFileType: "L'envoi de fichiers de type \"{0}\" n'est pas autorisé.",
fileUploadSizeTooBig: "Le fichier \"{0}\" de taille {1} dépasse la taille maximale d'envoi autorisée : {2}.",
fileUploadTotalSizeTooBig: "Les fichiers sélectionnés dépassent la taille totale maximale d'envoi autorisée : {0}.",  

copyFileMessage: "<span class=\"mollify-copy-file-message\">Sélectionnez le dossier vers lequel ce fichier <span class=\"mollify-copy-file-message-file\">\"{0}\"</span> sera copié :</span>",
moveFileMessage: "<span class=\"mollify-move-file-message\">Sélectionnez le dossier vers lequel ce fichier <span class=\"mollify-move-file-message-file\">\"{0}\"</span> sera déplacé :</span>",
copyDirectoryMessage: "<span class=\"mollify-copy-directory-message\">Sélectionnez le dossier vers lequel ce dossier <span class=\"mollify-copy-directory-message-directory\">\"{0}\"</span> sera copié :</span>",
moveDirectoryMessage: "<span class=\"mollify-move-directory-message\">Sélectionnez le dossier vers lequel ce dossier <span class=\"mollify-move-directory-message-directory\">\"{0}\"</span> sera déplacé :</span>",

userDirectoryListDefaultName: "{0} (Default)",

confirmMultipleItemDeleteMessage: "Êtes-vous certain de vouloir détruire {0} items ?",
copyMultipleItemsMessage: "<span class=\"mollify-copy-items-message\">Sélectionnez le dossier dans lequel les {0} items seront copiés :</span>",
moveMultipleItemsMessage: "<span class=\"mollify-move-items-message\">Sélectionnez le dossier dans lequel les {0} items seront déplacés :</span>",

dragMultipleItems: "{0} items",

publicLinkMessage: "Lien public pour \"{0}\" :",
copyHereDialogMessage: "<span class=\"mollify-copy-here-message\">Entrez le nom du fichier pour la copie de <span class=\"mollify-copy-file-message-file\">\"{0}\"</span>:</span>",

searchResultsInfo: "Trouvé {1} résultat(s) pour \"{0}\" :",

retrieveUrlNotFound: "Ressource introuvable: {0}",
retrieveUrlNotAuthorized: "Ressource non-autorisée: {0}",

shortDateTimeFormat: "d/M/yyyy hh:mm:ss a",
shortDateFormat: "d/M/yyyy",
timeFormat: "hh:mm:ss a",

pleaseWait: "Merci de patienter...",

permissionModeAdmin: "Administrateur",
permissionModeReadWrite: "Lecture et écriture",
permissionModeReadOnly: "Lecture seule",
permissionModeNone: "Aucun droits",

loginDialogTitle: "Identification",
loginDialogUsername: "Utilisateur :",
loginDialogPassword: "Mot de passe :",
loginDialogRememberMe: "Se souvenir de moi",
loginDialogResetPassword: "Mot de passe perdu?",
loginDialogLoginButton: "S'identifier",
loginDialogLoginFailedMessage: "Identification échouée, le nom d'utilisateur ou le mot de passe sont erronés.",

resetPasswordPopupMessage: "Pour réinitialiser votre mot de passe, entrez votre adresse courriel :",
resetPasswordPopupButton: "Réinitialier",
resetPasswordPopupTitle: "Réinitialiser le mot de passe",
resetPasswordPopupInvalidEmail: "Aucun utilisateur trouvé avec l'adresse courriel fournie",
resetPasswordPopupResetFailed: "Echec de la réinitialisation du mot de passe",
resetPasswordPopupResetSuccess: "Votre mot de passe a été réinitialisé. Vérifiez votre courriel pour les détails.",

mainViewParentDirButtonTitle: "..",
mainViewRefreshButtonTitle: "Rafraichir",
mainViewChangePasswordTitle: "Changer le mot de passe...",
mainViewAdministrationTitle: "Administration...",
mainViewEditPermissionsTitle: "Permissions Fichiers...",
mainViewLogoutButtonTitle: "Déconnexion",
mainViewAddButtonTitle: "Ajouter",
mainViewAddFileMenuItem: "Ajouter fichiers...",
mainViewAddDirectoryMenuItem: "Ajouter dossier...",
mainViewRetrieveUrlMenuItem: "Récupérer d'une URL...",
mainViewAddButtonTooltip: "Ajouter fichiers ou dossiers",
mainViewRefreshButtonTooltip: "Rafraichir",
mainViewParentDirButtonTooltip: "Remonter d'un niveau",
mainViewHomeButtonTooltip: "Dossiers racine",
mainViewSearchHint: "Rechercher",
mainViewSlideBarTitleSelect: "Sélection",
mainViewOptionsListTooltip: "Liste",
mainViewOptionsGridSmallTooltip: "Petites icônes",
mainViewOptionsGridLargeTooltip: "Grandes icônes",

fileDetailsAddDescription: "Ajouter une description",
fileDetailsEditDescription: "Editer",
fileDetailsApplyDescription: "Appliquer",
fileDetailsCancelEditDescription: "Annuler",
fileDetailsRemoveDescription: "Enlever",
fileDetailsEditPermissions: "Editer les permissions",
fileDetailsActionsTitle: "Actions",

filePreviewTitle: "Prévisualier",

fileActionDetailsTitle: "Détails",
fileActionDownloadTitle: "Télécharger",
fileActionDownloadZippedTitle: "Télécharger en format ZIP",
fileActionRenameTitle: "Renommer...",
fileActionCopyTitle: "Copier...",
fileActionCopyHereTitle: "Copier ici…",
fileActionMoveTitle: "Déplacer...",
fileActionDeleteTitle: "Détruire",
fileActionViewTitle: "Voir",
fileActionEditTitle: "Editer",
dirActionDownloadTitle: "Télécharger en format ZIP",
dirActionRenameTitle: "Renommer...",
dirActionDeleteTitle: "Détruire",
fileActionPublicLinkTitle: "Obtenir le lien public...",

fileListColumnTitleSelect: "",
fileListColumnTitleName: "Nom",
fileListColumnTitleType: "Type",
fileListColumnTitleSize: "Taille",
fileListColumnTitleLastModified: "Dernière modification",
fileListColumnTitleDescription: "Description",
fileListDirectoryType: "Dossier",

errorMessageRequestFailed: "Requête échouée",
errorMessageInvalidRequest: "Le serveur ne peut pas traiter la requête.",
errorMessageNoResponse: "Echec de la réponse du serveur.",
errorMessageInvalidResponse: "Le serveur a retourné une réponse invalide.",
errorMessageDataTypeMismatch: "Le serveur a retourné des résultats inattendus.",
errorMessageOperationFailed: "Operation échouée.",
errorMessageAuthenticationFailed: "Authentification échouée.",
errorMessageInvalidConfiguration: "La configuration de l'application est invalide.",
errorMessageDirectoryAlreadyExists: "Le dossier existe déjà.",
errorMessageFileAlreadyExists: "Le fichier existe déjà.",
errorMessageDirectoryDoesNotExist: "Le dossier n'existe pas.",
errorMessageInsufficientRights: "Droits insuffisants pour effectuer l'opération.",
errorMessageUnknown: "Une erreur inconnue est survenue.",

directorySelectorSeparatorLabel: "/",
directorySelectorMenuPleaseWait: "Merci de patienter...",
directorySelectorMenuNoItemsText: "Pas de dossiers",

infoDialogInfoTitle: "Information",
infoDialogErrorTitle: "Erreur",

confirmationDialogYesButton: "Oui",
confirmationDialogNoButton: "Non",

dialogOkButton: "OK",
dialogCancelButton: "Annuler",
dialogCloseButton: "Fermer",

deleteFileConfirmationDialogTitle: "Supprimer le fichier",
deleteDirectoryConfirmationDialogTitle: "Supprimer le dossier",

renameDialogTitleFile: "Renommer le fichier",
renameDialogTitleDirectory: "Renommer le dossier",
renameDialogOriginalName: "Nom original :",
renameDialogNewName: "Nouveau nom :",
renameDialogRenameButton: "Renommer",

fileUploadDialogTitle: "Envoyer fichier",
fileUploadDialogMessage: "Sélectionner ls(s) fichier(s) à envoyer :",
fileUploadDialogUploadButton: "Envoyer",
fileUploadDialogAddFileButton: "+",
fileUploadDialogAddFilesButton: "Ajouter fichier(s)...",
fileUploadDialogRemoveFileButton: "-",
fileUploadDialogInfoTitle: "Détails",
fileUploadFileExists: "Les fichier(s) suivants existent déjà dans ce répertoire : {0}",

fileUploadProgressTitle: "Envoi du fichier",
fileUploadProgressPleaseWait: "Merci de patienter...",
fileUploadDialogMessageFileCompleted: "Complété",
fileUploadDialogMessageFileCancelled: "Annulé",
fileUploadTotalProgressTitle: "Total :",
fileUploadDialogSelectFileTypesDescription: "Fichiers autorisés",

createFolderDialogTitle: "Créer un nouveau dossier",
createFolderDialogName: "Nom du dossier :",
createFolderDialogCreateButton: "Créer",

selectFolderDialogSelectButton: "Sélectionner",
selectFolderDialogFoldersRoot: "Dossiers",
selectFolderDialogRetrievingFolders: "Merci de patienter...",

selectItemDialogTitle: "Sélectionner un fichier ou un dossier",
selectPermissionItemDialogMessage: "Sélectionnez un fichier ou un dossier duquel éditer les permissions :",
selectPermissionItemDialogAction: "Sélectionner",

copyFileDialogTitle: "Copier fichier",
copyFileDialogAction: "Copier",

moveFileDialogTitle: "Déplacer fichier",
moveFileDialogAction: "Déplacer",

moveDirectoryDialogTitle: "Déplacer dossier",
moveDirectoryDialogAction: "Déplacer",

copyDirectoryDialogTitle: "Copier dossier",
copyDirectoryDialogAction: "Copier",

copyMultipleItemsTitle: "Copier items",
cannotCopyAllItemsMessage: "Impossible de copier tous les items vers le dossier sélectionné.",

moveMultipleItemsTitle: "Déplacer items",
cannotMoveAllItemsMessage: "Impossible de déplacer tous les items vers le dossier sélectionné.",
	
passwordDialogTitle: "Changer le mot de passe",
passwordDialogOriginalPassword: "Mot de passe actuel :",
passwordDialogNewPassword: "Nouveau mot de passe :",
passwordDialogConfirmNewPassword: "Confirmer nouveau mot de passe :",
passwordDialogChangeButton: "Changer",
passwordDialogPasswordChangedSuccessfully: "Le mot de passe a bien été changé.",
passwordDialogOldPasswordIncorrect: "Le mot de passe actuel est erroné. Le mot de passe n'a pas été changé.",

configurationDialogTitle: "Configuration",
configurationDialogCloseButton: "Fermer",
configurationDialogSettingUsers: "Utilisateurs",
configurationDialogSettingFolders: "Dossiers publiés",
configurationDialogSettingUserFolders: "Dossier utilisateurs",
configurationDialogSettingUsersViewTitle: "Utilisateurs",
configurationDialogSettingUsersAdd: "Ajouter...",
configurationDialogSettingUsersEdit: "Éditer…",
configurationDialogSettingUsersRemove: "Enlever",
configurationDialogSettingUsersResetPassword: "Réinitialiser le mot de passe...",
configurationDialogSettingUsersCannotDeleteYourself: "Vous ne pouvez pas supprimer votre propre compte utilisateur.",

configurationDialogSettingFoldersViewTitle: "Dossiers publiés",
configurationDialogSettingFoldersAdd: "Ajouter...",
configurationDialogSettingFoldersEdit: "Éditer...",
configurationDialogSettingFoldersRemove: "Enlever",

configurationDialogSettingUserFoldersViewTitle: "Dossiers utilisateur",
configurationDialogSettingUserFoldersSelectUser: "Sélectionner utilisateur",
configurationDialogSettingUserFoldersAdd: "Ajouter...",
configurationDialogSettingUserFoldersEdit: "Éditer...",
configurationDialogSettingUserFoldersRemove: "Enlever",
configurationDialogSettingUserFoldersNoFoldersAvailable: "Il n'y a pas de dossiers à ajouter à l'utilisateur actuel.",

userListColumnTitleName: "Nom",
userListColumnTitleType: "Type",
folderListColumnTitleName: "Nom",
folderListColumnTitleLocation: "Emplacement",

userDialogAddTitle: "Ajouter un utilisateur",
userDialogEditTitle: "Éditer un utilisateur",
userDialogUserName: "Nom:",
userDialogUserType: "Type:",
userDialogPassword: "Mot de passe:",
userDialogGeneratePassword: "Nouveau",
userDialogAddButton: "Ajouter",
userDialogEditButton: "Éditer",

folderDialogAddTitle: "Ajouter un dossier",
folderDialogEditTitle: "Éditer un dossier",
folderDialogName: "Nom public du dossier:",
folderDialogPath: "Chemin du dossier (non visible aux utilisateurs) :",
folderDialogAddButton: "Ajouter",
folderDialogEditButton: "Editer",

resetPasswordDialogTitle: "Réinitialiser le mot de passe",
resetPasswordDialogPassword: "Nouveau mot de passe :",
resetPasswordDialogGeneratePassword: "Nouveau",
resetPasswordDialogResetButton: "Réinitialiser",

userFolderDialogAddTitle: "Ajouter un dossier utilisateur",
userFolderDialogEditTitle: "Éditer un dossier utilisateur",
userFolderDialogDirectoriesTitle: "Dossier:",
userFolderDialogUseDefaultName: "Utiliser le nom de dossier par défaut",
userFolderDialogName: "Nom public du dossier :",
userFolderDialogDefaultNameTitle: "Nom de dossier par défaut :",
userFolderDialogAddButton: "Ajouter",
userFolderDialogEditButton: "Éditer",
userFolderDialogSelectFolder: "Sélectionner dossier",

invalidDescriptionUnsafeTags: "Balises HTML illégales utilisées dans la description, seul du \"HTML sain\" est permis.",

itemPermissionEditorDialogTitle: "Éditer permissions",
itemPermissionEditorItemTitle: "Nom :",
itemPermissionEditorDefaultPermissionTitle: "Permissions par défaut :",
itemPermissionEditorNoPermission: "-",
itemPermissionListColumnTitleName: "Nom",
itemPermissionListColumnTitlePermission: "Permission",
itemPermissionEditorSelectItemMessage: "Sélectionner item",
itemPermissionEditorButtonSelectItem: "...",
itemPermissionEditorButtonAddUserPermission: "Ajouter un utilisateur",
itemPermissionEditorButtonAddUserGroupPermission: "Ajouter un Groupe",
itemPermissionEditorButtonEditPermission: "Éditer",
itemPermissionEditorButtonRemovePermission: "Enlever",
itemPermissionEditorConfirmItemChange: "Il y a des changements non sauvegardés. Souhaitez-vous continuer ?",

fileItemUserPermissionDialogAddTitle: "Ajouter des permissions utilisateur",
fileItemUserPermissionDialogAddGroupTitle: "Ajouter des permissions de groupe",
fileItemUserPermissionDialogEditTitle: "Éditer des permissions utilisateur",
fileItemUserPermissionDialogEditGroupTitle: "Éditer des permissions de groupe",
fileItemUserPermissionDialogName: "Nom :",
fileItemUserPermissionDialogPermission: "Permission :",
fileItemUserPermissionDialogAddButton: "Ajouter",
fileItemUserPermissionDialogEditButton: "Editer",

dropBoxTitle: "Dropbox",
dropBoxActions: "Actions",
dropBoxActionClear: "Effacer",
dropBoxActionCopy: "Copier...",
dropBoxActionCopyHere: "Copier ici",
dropBoxActionMove: "Déplacer...",
dropBoxActionMoveHere: "Déplacer ici",
dropBoxNoItems: "Ajouter ici",

mainViewSelectButton: "Sélectionner",
mainViewSelectAll: "Tout sélectionner",
mainViewSelectNone: "Tout désélectionner",
mainViewSelectActions: "Actions",
mainViewSelectActionAddToDropbox: "Ajouter à Dropbox",
mainViewDropBoxButton: "Dropbox",

fileViewerOpenInNewWindowTitle: "Ouvrir dans une nouvelle fenêtre",
fileEditorSave: "Save",

filePublicLinkTitle: "Lien public",

copyHereDialogTitle: "Copier ici",

retrieveUrlTitle: "Récupérer d'une URL",
retrieveUrlMessage: "Entrez l'URL à partir de laquelle récupérer le fichier :",
retrieveUrlFailed: "Impossible de récupérer le fichier.",

searchResultsDialogTitle: "Résultats de recherche",
searchResultsNoMatchesFound: "Aucun résultat trouvé.",
searchResultListColumnTitlePath: "Chemin",
searchResultsTooltipMatches: "Résultats :",
searchResultsTooltipMatchName: "Name",
searchResultsTooltipMatchDescription: "Description",

fileItemContextDataName: "Nom",
fileItemContextDataPath: "Chemin",
fileItemContextDataSize: "Taille",
fileItemContextDataExtension: "Extension",
fileItemContextDataLastModified: "Dernière modification",
fileItemDetailsExif: "Propriétés de l'image",
fileItemContextDataImageSize: "Taille de l'image",
fileItemContextDataImageSizePixels: "{0} pixels"
})})();