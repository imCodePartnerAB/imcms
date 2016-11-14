$.ajaxSetup({cache: false});
CKEDITOR.disableAutoInline = true;

$(document).ready(function () {
    new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);
});
