define(
    'imcms-image-active-tab',
    ['jquery'],
    function ($) {

        function getCurrentActiveTabData() {
            return $('.imcms-editable-img-control-tabs__tab--active').data('tab');
        }

        return {
            currentActiveTab: getCurrentActiveTabData

        };
    }
);