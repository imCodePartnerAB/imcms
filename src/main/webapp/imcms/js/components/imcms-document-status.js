define("imcms-document-status",
    [
        'imcms-i18n-texts'
    ],
    function (texts) {
        texts = texts.status;

        module.exports = {
            getDocumentStatusTexts: (documentStatus, publishedDate) => {
                return {
                    PUBLISHED: {
                        title: texts.title.published,
                        tooltip: texts.tooltip.published + ' ' + publishedDate,
                    },
                    PUBLISHED_WAITING: {
                        title: texts.title.publishedWaiting,
                        tooltip: texts.tooltip.publishedWaiting,
                    },
                    IN_PROCESS: {
                        title: texts.title.inProcess,
                        tooltip: texts.tooltip.inProcess,
                    },
                    DISAPPROVED: {
                        title: texts.title.disapproved,
                        tooltip: texts.tooltip.disapproved,
                    },
                    ARCHIVED: {
                        title: texts.title.archived,
                        tooltip: texts.tooltip.archived,
                    },
                    PASSED: {
                        title: texts.title.passed,
                        tooltip: texts.tooltip.passed,
                    },
                    WASTE_BASKET: {
                        title: texts.title.wasteBasket,
                        tooltip: texts.tooltip.wasteBasket,
                    }
                }[documentStatus];
            }
        }
    });