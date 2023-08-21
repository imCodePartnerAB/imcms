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

        const ImagesAdminTab = function (name, tabElements) {
            SuperAdminTab.call(this, name, tabElements);
        };

        ImagesAdminTab.prototype = Object.create(SuperAdminTab.prototype);

        ImagesAdminTab.prototype.getDocLink = () => texts.documentationLink;

        return new ImagesAdminTab(texts.title, [
            getImageLibraryButton()
        ]);
    }
);