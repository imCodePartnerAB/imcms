const $ = require('jquery');
const editableArea = require('imcms-editable-area');
const previewImageArea = require('imcms-preview-image-area');

module.exports = {
    build: function () {
        return $("<div>").append(
            editableArea.getEditableImageArea(),
            previewImageArea.getPreviewImageArea()
        );
    }
};
