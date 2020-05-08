const $ = require('jquery');
const originalArea = require('imcms-originally-area');
const previewImageArea = require('imcms-preview-image-area');
const croppingArea = require('imcms-cropping-area');

module.exports = {
    build: () => $("<div>").append(
        previewImageArea.getPreviewImageArea(),
        originalArea.getOriginalImageArea(),
        croppingArea.getCroppingBlock(),
    )
};
