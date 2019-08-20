/**
 * @author Pavlenko Victor from Ubrainians for imCode
 * 19.08.19
 */
define(
    'imcms-data-version-admin-tab-builder',
    ['imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-version-data-rest-api',
        'imcms-modal-window-builder', 'imcms-bem-builder', 'jquery'],
    function (SuperAdminTab, texts, versionDataApi, modal, BEM, $) {

        texts = texts.superAdmin.dataVersion;


        function getDataVersion() {
            let $versionDataContainer = $('<div>', {
                'class': 'versions-container'
            });
            versionDataApi.read().done(dataVersion => {
                    $versionDataContainer.append(buildVersionDataContainer(dataVersion))
                }
            ).fail(() => modal.buildErrorWindow('Error!'));

            return $versionDataContainer;
        }

        function buildVersionDataContainer(dataVersion) {
            return new BEM({
                block: 'data-version',
                elements: {
                    'imcms-version': $('<div>', {
                        text: dataVersion.imcmsVersion
                    }),
                    'java-version': $('<div>', {
                        text: dataVersion.javaVersion
                    }),
                    'server-info': $('<div>', {
                        text: dataVersion.serverInfo
                    }),
                    'db-version': $('<div>', {
                        text: dataVersion.dbVersion
                    }),
                    'db-name-version': $('<div>', {
                        text: dataVersion.dbNameVersion
                    }),
                }
            }).buildBlockStructure('<div>');
        }


        return new SuperAdminTab(texts.name, [
            getDataVersion()
        ]);
    }
);
