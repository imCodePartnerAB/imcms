define('imcms-metadata-tab-builder',
	[
		'imcms-bem-builder', 'imcms-components-builder', 'imcms', 'imcms-i18n-texts',
		'imcms-page-info-tab', 'imcms-document-types', 'imcms-field-wrapper', 'jquery',
		'imcms-meta-tag-rest-api', 'imcms-modal-window-builder'
	],
	function (BEM, components, imcms, i18Texts, PageInfoTab, docTypes, fieldWrapper, $,
	          metaTagsRestApi, modal) {
		const texts = i18Texts.pageInfo.metadata;

		const MetadataTab = function (name, docType) {
			PageInfoTab.apply(this, arguments);
		};

		const tabData = {};

		function buildMetadataContainer() {
			fetchMetaTags();
			tabData.documentMetadataList = [];
			return tabData.$metadataContainer = $('<div>');
		}

		function fetchMetaTags() {
			metaTagsRestApi.readAll()
				.done(metaTags => {
					tabData.metaTags = metaTags;
					tabData.$metaTags = metaTags.map(metaTag => ({
						'data-value': metaTag.id,
						text: metaTag.name.toUpperCase(),
					}));
				})
		}

		function buildMetaTagSelect() {
			return components.selects.imcmsSelect('<div>', {
				name: "meta-tag-select",
				text: texts.select,
				class: 'imcms-flex--w-25 imcms-field',
			}, tabData.$metaTags);
		}

		function onMetadataContainerButtonClick(commonContent, $body) {
			$body.append(buildMetadataRow(commonContent.language.name));
			if ($body.children().length !== 0)
				$body.show();
		}

		function buildMetaTagTextArea(meta) {
			return components.texts.textAreaField("<div>", {
				text: texts.content,
				name: "data",
				html: meta ? meta.content : '',
				class: 'imcms-flex--w-70 imcms-flex--ml-auto',
				resizable: true
			});
		}

		function buildMetadata(commonContent) {
			const $body = $("<div class='imcms-metadata-body'>"),
				$language = components.texts.titleText("<div>", commonContent.language.name),
				$addRow = components.buttons.neutralButton({
					text: 'ADD+',
					click: () => onMetadataContainerButtonClick(commonContent, $body),
					class: "imcms-flex--ml-auto"
				}),
				$head = new BEM({block: 'imcms-metadata-head'}).buildBlock("<div>", [
					{"language-title": $language},
					{"add-row": $addRow}
				], {
					class: 'imcms-flex--d-flex imcms-flex--p-0',
				}),
				$hr = $("<hr/>"),
				$rows = buildMetadataRows(commonContent);

			$body.append($rows);
			$body.children().length > 0 ? $body.show() : $body.hide();

			return new BEM({
				block: 'imcms-metadata'
			}).buildBlock($("<div>"), [
				{'head': $head},
				{'body': $body},
				{'hr': $hr}
			]).addClass("imcms-field imcms-metadata");
		}

		function buildMetadataRows(commonContent) {
			if (commonContent.docId)
				return commonContent.documentMetadataList
					.map(metadata => buildMetadataRow(commonContent.language.name, metadata));
		}

		function buildMetadataRow(languageName, metadata) {
			const $metaTagContent = buildMetaTagTextArea(metadata),
				$metaTagSelect = buildMetaTagSelect(),
				$metadataRow = new BEM({
					block: 'imcms-metadata-row'
				}).buildBlock("<div>",
					[
						{'meta-tag': $metaTagSelect},
						{'content': $metaTagContent},
					], {
						class: ('imcms-flex--d-flex imcms-flex--p-0')
					}
				),
				metadataListElement = {
					name: languageName,
					metaTagContent: $metaTagContent,
					metaTagSelect: $metaTagSelect
				},
				$remove = components.controls.remove(() => {
					$metadataRow.remove();
					tabData.documentMetadataList.splice(tabData.documentMetadataList.indexOf(metadataListElement), 1);
				}).addClass("imcms-flex--ml-auto");

			metadata ? $metaTagSelect.selectValue(metadata.metaTag.id) : $metaTagSelect.selectFirst();
			tabData.documentMetadataList.push(metadataListElement);

			$metadataRow.append($remove);

			return $metadataRow;
		}

		MetadataTab.prototype = Object.create(PageInfoTab.prototype);

		MetadataTab.prototype.tabElementsFactory = () => [
			buildMetadataContainer()
		];

		MetadataTab.prototype.fillTabDataFromDocument = (document) => {
			tabData.document = document;
			tabData.$metadataContainer.append(document.commonContents.map(buildMetadata));
		};

		MetadataTab.prototype.saveData = (document) => {
			document.commonContents.forEach(docCommonContent => {
				docCommonContent.documentMetadataList = [];

				const metadata = tabData.documentMetadataList.filter(meta => meta.name === docCommonContent.language.name);
				metadata.forEach((meta, index) => {
					docCommonContent.documentMetadataList.push({
						metaTag: tabData.metaTags[meta.metaTagSelect.getSelectedValue() - 1],
						content: meta.metaTagContent.getValue()
					})
				})
			})
			return document;
		}

		MetadataTab.prototype.clearTabData = () => {
			tabData.$metadataContainer.empty();
		};

		return new MetadataTab(texts.name, docTypes.TEXT);
	});