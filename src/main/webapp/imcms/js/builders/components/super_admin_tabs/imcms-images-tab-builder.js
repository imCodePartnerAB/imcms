define(
    'imcms-images-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms', 'imcms-image-editor-body-head-builder'],
    function (SuperAdminTab, texts, imcms, imageEditor) {

        texts = texts.superAdmin.imagesTab;
        imcms.disableContentManagerSaveButton = true;
        let $imageLibraryButton;

        function getImageLibraryButton(){
            return $imageLibraryButton || ($imageLibraryButton = $imageLibraryButton = imageEditor.buildSelectImageBtnContainer())
        }

        return new SuperAdminTab(texts.title, [
            getImageLibraryButton()
        ]);
    }
);