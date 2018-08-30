import '../../css/imcms-imports_files.css';

/**
 * Init script for admin functionality
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
const panelBuilder = require('imcms-admin-panel-builder');
const events = require('imcms-events');
const sessionTimeoutManagement = require('imcms-session-timeout-management');
const dateFormatter = require('date-format');
const imcms = require('imcms');
const $ = require('jquery');

events.on('imcms-version-modified', function () {
    imcms.document.hasNewerVersion = true;
});

events.on('imcms-publish-new-version-current-doc', function () {
    window.location.href = imcms.contextPath + '/api/publish-document/' + imcms.document.id;
    alert('The version is published with status APPROVED ' + dateFormatter.format(new Date(), 'yyyy-mm-dd HH:MM'));
});

sessionTimeoutManagement.initOrUpdateSessionTimeout();

function detectActivePanelButton() {
    if (imcms.isEditMode) {
        return 'edit';
    }

    if (imcms.isPreviewMode) {
        return 'preview'
    }

    return 'public';
}

$(function () {
    panelBuilder.buildPanel({
        active: detectActivePanelButton()
    });

    if (imcms.isEditMode) {
        var editorsInit = require('imcms-editors-initializer');
        $(editorsInit.initEditors);
    }
});

