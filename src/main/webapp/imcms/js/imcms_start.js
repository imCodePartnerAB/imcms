import '../css/imcms-imports_files.css';

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

imcms.$ = $;

events.on('imcms-version-modified', () => {
    imcms.document.hasNewerVersion = true;
});

events.on('imcms-publish-new-version-current-doc', () => {
    window.location.href = imcms.contextPath + '/api/publish-document/' + imcms.document.id;
    events.trigger('imcms-alert-publish-new-version');
});

events.on('imcms-alert-publish-new-version', () => {
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

$(() => {
    panelBuilder.buildPanel({
        active: detectActivePanelButton()
    });

    if (imcms.isEditMode) {
        const editorsInit = require('imcms-editors-initializer');
        $(editorsInit.initEditors);
    }
});

