$.ajaxSetup({cache: false});
CKEDITOR.disableAutoInline = true;

$(document).ready(function () {
    new Imcms.Bootstrapper().bootstrap(Imcms.isEditMode);
});

$(document).ajaxError(function (event, jqxhr) {
    Imcms.BackgroundWorker.closeProcessWindow();
    console.log(jqxhr);
});
