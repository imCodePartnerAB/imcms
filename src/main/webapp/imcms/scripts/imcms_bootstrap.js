(function (Imcms) {
    Imcms.Bootstrapper = function () {
    };

    Imcms.Bootstrapper.prototype = {
        bootstrap: function (editMode) {
            var $body = $("body");
            if (editMode) {
                $body.css({paddingLeft: 150, width: $(window).width() - 150});
            } else {
                if ($body.css('paddingLeft').length > 0) {
                    $body.removeAttr('style');
                }
            }

            //Init of internationalization plugin
            $.i18n.properties({
                name: 'imcms_jquery_i18n',
                path: Imcms.Linker.get('admin.localization.config'),
                mode: 'both'
            });

            Imcms.Editors.init();
            Imcms.Admin.Panel.init();
        }
    };

    return Imcms;
})(Imcms);
