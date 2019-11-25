import '../../css/imcms-imports_files.css';
import '../../../imcms/css/imcms_admin.css';
import '../../css/admin/imcms-super-admin.css';

const $ = require('jquery');
const superAdminPageBuilder = require('imcms-super-admin-page-builder');

$(function () {
    superAdminPageBuilder.build();
});
