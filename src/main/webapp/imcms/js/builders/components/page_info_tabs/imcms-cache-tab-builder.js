define('imcms-cache-tab-builder',
    ["imcms-bem-builder", "imcms-components-builder", "imcms-i18n-texts", "jquery",
        "imcms-page-info-tab", 'imcms', 'imcms-cache-document-rest-api', 'imcms-modal-window-builder'],
    function (BEM, components, texts, $, PageInfoTab, imcms, documentCacheRest, modal) {

        texts = texts.pageInfo.cache;

        function buildCacheData($title, $button) {
            return new BEM({
                block: 'init-data',
                elements: {
                    'title': $title,
                    'button': $button,
                },
            }).buildBlockStructure('<div>');
        }

        function clearCacheRequest(request, $loading, $success) {
            $success.hide();
            $loading.show();
            request().done(() => {
	            $loading.hide();
	            $success.show();
            }).fail(() => modal.buildErrorWindow(texts.error.failedClear));
        }

	    const tabData = {};

		function buildCacheSettingsBlock(){
			const $title = components.texts.titleText('<div>', texts.cacheSettings);

			const $cacheForUnauthorizedUsers = components.checkboxes.imcmsCheckbox("<div>", {
				name: "cacheForUnauthorizedUsers",
				text: texts.cacheForUnauthorizedUsers
			})
			tabData.$checkboxCacheForUnauthorizedUsers = $cacheForUnauthorizedUsers;

			const $cacheForAuthorizedUsers = components.checkboxes.imcmsCheckbox("<div>", {
				name: "cacheForAuthorizedUsers",
				text: texts.cacheForAuthorizedUsers
			})
			tabData.$checkboxCacheForAuthorizedUsers = $cacheForAuthorizedUsers;

			return new BEM({
				block: 'cache-settings',
				elements: {
					'title': $title,
					'unauthorized-users': $cacheForUnauthorizedUsers,
					'authorized-users': $cacheForAuthorizedUsers,
				},
			}).buildBlockStructure('<div>');
		}

		function fillCacheSettings(document){
			tabData.$checkboxCacheForUnauthorizedUsers.setChecked(document.cacheForUnauthorizedUsers);
			tabData.$checkboxCacheForAuthorizedUsers.setChecked(document.cacheForAuthorizedUsers);
		}

	    function buildDeleteDocsBlock() {
		    return tabData.$deleteDocsBlock = $('<div>');
	    }

	    function fillDeleteDocsBlock(document) {
		    const $loading = $('<div>', {
			    class: 'loading-animation',
			    style: 'display: none',
		    });
		    const $success = $('<div>', {
			    class: 'success-animation',
			    style: 'display: none',
		    });

		    function buildCacheActions() {
			    const aliases = document.commonContents.map(commonContent => commonContent.alias);
			    const dataParam = {
				    docId: imcms.document.id,
				    aliases: aliases.join(",")
			    };
			    const $cacheTitle = $('<div>', {
				    text: texts.invalidateTitle,
				    class: 'imcms-title',
			    });

			    const request = () => documentCacheRest.invalidate(dataParam);

			    const $button = components.buttons.positiveButton({
				    text: texts.invalidateButton,
				    click: () => clearCacheRequest(request, $loading, $success)
			    });

			    return buildCacheData($cacheTitle, $button)
		    }

		    return new BEM({
			    block: 'cache-remove-document-row',
			    elements: {
				    'build-data': buildCacheActions(),
				    'loading': $loading,
				    'success': $success,
			    }
		    }).buildBlockStructure('<div>')
	    }

        const CacheTab = function (name) {
            PageInfoTab.call(this, name);
        };

	    CacheTab.prototype = Object.create(PageInfoTab.prototype);

	    CacheTab.prototype.isDocumentTypeSupported = () => {
		    return true; // all supported
	    };

	    CacheTab.prototype.tabElementsFactory = () => [
			buildCacheSettingsBlock(),
			$('<hr/>'),
		    buildDeleteDocsBlock()
	    ];

	    CacheTab.prototype.fillTabDataFromDocument = document => {
		    tabData.$deleteDocsBlock.prepend(fillDeleteDocsBlock(document));
			fillCacheSettings(document);
	    }

		CacheTab.prototype.saveData = function (documentDTO) {
			documentDTO.cacheForUnauthorizedUsers = tabData.$checkboxCacheForUnauthorizedUsers.isChecked()
			documentDTO.cacheForAuthorizedUsers = tabData.$checkboxCacheForAuthorizedUsers.isChecked();
			return documentDTO;
		};

		CacheTab.prototype.clearTabData = () => {
			tabData.$checkboxCacheForUnauthorizedUsers.setChecked(false);
			tabData.$checkboxCacheForAuthorizedUsers.setChecked(false);
		};

		CacheTab.prototype.getDocLink = () => texts.documentationLink;

	    return new CacheTab(texts.name);
    }
);