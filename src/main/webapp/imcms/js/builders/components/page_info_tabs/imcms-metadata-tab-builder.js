define('imcms-metadata-tab-builder',
	[
		'imcms-bem-builder', 'imcms-components-builder', 'imcms', 'imcms-i18n-texts',
		'imcms-page-info-tab', 'imcms-document-types', 'imcms-field-wrapper', 'jquery',
		'imcms-meta-tag-rest-api'
	],
	function (BEM, components, imcms, i18Texts, PageInfoTab, docTypes, fieldWrapper, $,
	          metaTagsRestApi) {
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
				});
		}

		function buildMetaTagSelect(metadata) {
			const $metaTagSelect = components.selects.imcmsSelect('<div>', {
				name: "meta-tag-select",
				class: 'imcms-flex--w-25 imcms-field',
			}, tabData.$metaTags);

			metadata ? $metaTagSelect.selectValue(metadata.metaTag.id) : $metaTagSelect.selectFirst();

			return $metaTagSelect
		}

		function buildMetaTagTextArea(meta) {
			return components.texts.textAreaField("<div>", {
				name: "data",
				html: meta ? meta.content : '',
				class: 'imcms-flex--w-70 imcms-flex--ml-auto',
				resizable: true
			});
		}

		function buildMetadataRowRemove($metadataRow, metadataElement) {
			return components.controls.remove(() => {
				$metadataRow.remove();
				tabData.documentMetadataList.splice(tabData.documentMetadataList.indexOf(metadataElement), 1);
			}).addClass("imcms-flex--ml-auto");
		}

		function buildMetadataRowAdd(commonContent, $body) {
			return components.buttons.neutralButton({
				text: texts.add,
				click: () => onMetadataRowAddClick(commonContent, $body),
				class: "imcms-flex--ml-auto"
			})
		}

		function onMetadataRowAddClick(commonContent, $body) {
			$body.append(buildMetadataRow(commonContent.language.name))
				.find('.info-text')
				.remove();
		}

		function buildMetadataContainerHead(commonContent, $metadataBody) {
			const $languageTitle = components.texts.titleText("<div>", commonContent.language.name),
				$addRow = buildMetadataRowAdd(commonContent, $metadataBody);

			if(!(imcms.availableLanguages.length > 1)){
				$languageTitle.hide();
			}

			return new BEM({
				block: 'imcms-metadata-head'
			}).buildBlock("<div>", [
				{"language-title": $languageTitle},
				{"add-row": $addRow}
			], {
				class: 'imcms-flex--d-flex imcms-flex--p-0',
			});
		}

		function buildMetadata(commonContent) {
			const $metadataBody = $("<div class='imcms-metadata-body'>");

			const $head = buildMetadataContainerHead(commonContent, $metadataBody),
				$rows = buildMetadataRows(commonContent);

			if (!$rows || !$rows.length) {
				$metadataBody.append(buildTextInfo(texts.noData));
			} else {
				$metadataBody.append($rows);
			}

			return new BEM({
				block: 'imcms-metadata'
			}).buildBlock($("<div>"), [
				{'head': $head},
				{'body': $metadataBody},
				{'hr': $("<hr/>")}
			]).addClass("imcms-field imcms-metadata");
		}

		function buildTextInfo(value) {
			return components.texts.infoText('<div>', value).addClass('info-text')
		}

		function buildMetadataRows(commonContent) {
			if (commonContent.docId)
				return commonContent.documentMetadataList
					.map(metadata => buildMetadataRow(commonContent.language.name, metadata));
		}

		function buildMetadataRow(languageName, metadata) {
			const $metaTagContent = buildMetaTagTextArea(metadata),
				$metaTagSelect = buildMetaTagSelect(metadata);

			const $metadataRow = new BEM({
				block: 'imcms-metadata-row'
			}).buildBlock("<div>",
				[
					{'meta-tag': $metaTagSelect},
					{'content': $metaTagContent},
				], {
					class: ('imcms-flex--d-flex imcms-flex--p-0')
				}
			);

			const metadataElement = {
				name: languageName,
				metaTagContent: $metaTagContent,
				metaTagSelect: $metaTagSelect
			};

			const $remove = buildMetadataRowRemove($metadataRow, metadataElement);

			tabData.documentMetadataList.push(metadataElement);
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
					const metaTagContent = meta.metaTagContent.getValue();

					if (metaTagContent) {
						docCommonContent.documentMetadataList.push({
							metaTag: tabData.metaTags[meta.metaTagSelect.getSelectedValue() - 1],
							content: metaTagContent
						})
					}
				})
			})
			return document;
		}

		MetadataTab.prototype.clearTabData = () => {
			tabData.$metadataContainer.empty();
		};

		MetadataTab.prototype.getDocLink = () => texts.documentationLink;

		return new MetadataTab(texts.name, docTypes.TEXT);
	});
