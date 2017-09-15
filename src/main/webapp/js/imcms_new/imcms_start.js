/**
 * Init script for admin functionality
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 07.08.17.
 */
Imcms.require(["imcms-admin-panel-builder", "imcms"], function (panelBuilder, imcms) {
    panelBuilder.buildPanel({active: imcms.isEditMode ? 'edit' : 'public'});
});
