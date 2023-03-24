define(
	'imcms-documents-import-tab-builder',
	["imcms-window-builder", 'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-modal-window-builder',
		'imcms-field-wrapper', 'jquery', 'imcms-bem-builder', 'imcms-import-documents-rest-api', 'imcms-import-entity-reference-rest-api',
		'imcms-templates-rest-api', "imcms-roles-rest-api", 'imcms-categories-rest-api', "imcms-category-types-rest-api",
		"imcms-basic-import-documents-info-rest-api"],
	function (WindowBuilder, SuperAdminTab, texts, components, modal, fieldWrapper, $, BEM,
	          importDocumentsRestApi, importEntityReferenceRestApi, templatesRestApi, rolesRestApi, categoriesRestApi,
	          categoryTypesRestApi, basicImportDocumentsInfoRestApi) {
		texts = texts.superAdmin.documentsImport;

		let tab = {};

		const DEFAULT_HIDE_TIMEOUT = 2000;

		const ImportEntityType = {
			ROLE: 'ROLE',
			TEMPLATE: 'TEMPLATE',
			CATEGORY: 'CATEGORY',
			CATEGORY_TYPE: 'CATEGORY_TYPE'
		};

		const ImportDocumentStatus = {
			IMPORT: "IMPORT",
			UPDATE: "UPDATE",
			IMPORTED: "IMPORTED",
			SKIP: "SKIP",
			SKIPPED: "SKIPPED",
			FAILED: "FAILED"
		};

		const Events = {
			EMITTER_ID_EVENT: "EMITTER_ID_EVENT",
			TOTAL_SIZE_EVENT: "TOTAL_SIZE_EVENT",
			PROGRESS_EVENT: "PROGRESS_EVENT",
			UPLOAD_FILENAME_EVENT: "UPLOAD_FILENAME_EVENT"
		}

		function loadResources() {
			rolesRestApi.read().done(roles => tab.$roleOptions = roles.map(mapToOption));
			templatesRestApi.read().done(templates => tab.$templateOptions = templates.map(mapToOption));
			categoriesRestApi.read().done(categories => tab.$categoryOptions = categories.map(mapToOption));
			categoryTypesRestApi.read().done(categoryTypes => tab.$categoryTypeOptions = categoryTypes.map(mapToOption));
		}

		function mapToOption(entity) {
			return {
				'data-value': entity.id ? entity.id : entity,
				text: entity.name ? entity.name : entity
			}
		}

		function buildProgressBar() {
			tab.$progressBar = $("<progress id='progress-bar' style='width: 100%; visibility:hidden;'>");
			return tab.$progressBar;
		}

		function makeProgressBarVisible(totalSize) {
			tab.$progressBar.attr("max", totalSize).css("visibility", "visible");
		}

		function hideProgressBar() {
			tab.$progressBar.css("visibility", "hidden");
		}

		function changeProgressBarProgress(progress) {
			tab.$progressBar.attr("value", progress);
		}

		function buildImportEntityReferenceButtons() {
			tab.$importEntityReferenceButtons = new BEM({
				block: "imcms-import-entity-reference-buttons",
				elements: {
					'role': buildButton("role references", ImportEntityType.ROLE),
					'template': buildButton("template references", ImportEntityType.TEMPLATE),
					'category': buildButton("category references", ImportEntityType.CATEGORY),
					'category-type': buildButton("category type references", ImportEntityType.CATEGORY_TYPE),
				}
			}).buildBlockStructure('<div>', {})
				.css({});

			return tab.$importEntityReferenceButtons;
		}

		function buildButton(name, importEntityType) {
			return components.buttons.neutralButton({
				text: name,
				click: () => {
					importEntityReferenceWindowBuilder
						.buildWindowWithShadow
						.apply(importEntityReferenceWindowBuilder, [importEntityType])
				}
			});
		}

		function buildImportDocumentsContainer() {
			tab.$resultContainer = $('<div>', {'class': 'table-import-documents'});
			tab.$listButton.click();
			return tab.$resultContainer;
		}

		const ImportDocumentListBuilder = function () {
			this.$containerResult = tab.$resultContainer;
			this.appender = this.append.bind(this);
		}

		ImportDocumentListBuilder.prototype = {
			toRow: basicImportDocument => {
				return new BEM({
					block: "basic-import-document-info-row",
					elements: {
						'id': $("<div>", {text: basicImportDocument.id}),
						'metaId': buildMetaIdLink(basicImportDocument),
						"status": buildStatusColumn(basicImportDocument)
					}
				}).buildBlockStructure("<div>", {});
			},
			buildTitles: function () {
				const $titles = new BEM({
					block: "basic-import-document-titles",
					elements: {
						'id': $("<div>", {text: texts.tableTitles.id}),
						"metaId": $("<div>", {text: texts.tableTitles.metaId}),
						"status": $("<div>", {text: texts.tableTitles.status})
					}
				}).buildBlockStructure("<div>", {})

				this.$containerResult.append($titles);
				return this;
			},
			addToList: function (rows) {
				this.$containerResult.css('display', 'inline-block').append(rows)
				return this;
			},
			append: function (basicImportDocuments) {
				if (!tab.$resultContainer.children().length) {
					this.buildTitles()
				}
				this.addToList(basicImportDocuments.map(this.toRow));
			}
		}

		function buildMetaIdLink(basicImportDocument) {
			const metaId = basicImportDocument.metaId;
			const $metaIdLink = $("<a>");

			if (metaId) {
				$metaIdLink.attr({
					href: '/' + metaId,
					target: "_blank"
				})
				$metaIdLink.text(metaId);
			}

			return $metaIdLink;
		}

		function buildStatusColumn(basicImportDocument) {
			const status = basicImportDocument.status;

			switch (status) {
				case ImportDocumentStatus.IMPORT:
				case ImportDocumentStatus.SKIP:
					return buildImportDocumentStatusSelect(basicImportDocument, optionsForNonImportedDocument()).selectValue(status);
				case ImportDocumentStatus.IMPORTED:
				case ImportDocumentStatus.UPDATE:
					return buildImportDocumentStatusSelect(basicImportDocument, optionsForImportedDocument()).selectValue(status)

				default: {
					return components.texts.textBox("<div>", {
						value: status,
						readonly: "readonly"
					});
				}
			}
		}

		function buildImportDocumentStatusSelect(basicImportDocument, options) {
			const $responseMessageDiv = $("<div>")
				.css({
					"width": "fit-content",
					"padding-left": "5px",
					"position": "absolute",
					"font-size": "smaller",
					"display": "inline-block",
					"line-height": "40px",
				});
			const $importDocumentStatusSelect = components.selects.imcmsSelect("<div>", {
				emptySelect: false,
				onSelected: () => onStatusSelect.call($importDocumentStatusSelect, $responseMessageDiv, basicImportDocument)
			}, options);

			$importDocumentStatusSelect.append($responseMessageDiv);
			return $importDocumentStatusSelect;
		}

		function onStatusSelect($responseMessageDiv, basicImportDocument) {
			basicImportDocument.status = this.getSelectedValue();
			basicImportDocumentsInfoRestApi.update(basicImportDocument)
				.done(response => $responseMessageDiv.text(texts.statusResponses.success).css("color", "green").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT))
				.fail(response => $responseMessageDiv.text(texts.statusResponses.failed).css("color", "red").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT));
		}

		function optionsForNonImportedDocument() {
			return [
				mapToOption(ImportDocumentStatus.IMPORT),
				mapToOption(ImportDocumentStatus.SKIP),
			];
		}

		function optionsForImportedDocument() {
			return [
				mapToOption(ImportDocumentStatus.IMPORTED),
				mapToOption(ImportDocumentStatus.UPDATE),
			];
		}

		function buildUploadButton() {
			const $fileInput = $('<input>', {
				type: 'file',
				accept: ".zip",
				change: () => {
					const formData = new FormData();
					const files = $fileInput.prop("files");

					tab.$resultContainer.off("scroll");
					formData.append("file", files[0]);

					tab.$resultContainer.children().not(":first").remove();
					importDocumentsRestApi.upload(formData)
						.done(zipFilename => {
							tab.$uploadFilename.text(zipFilename);
							trackUploadProgress();

							$fileInput.val('');
						})
						.fail(() => modal.buildErrorWindow(texts.uploadFailure));
				}
			});

			return components.buttons.positiveButton({
				text: texts.uploadButton,
				click: () => {
					$fileInput.click();
				}
			}).css('display', 'inline-block');
		}

		function trackUploadProgress() {
			const uploadProgressInterval = setInterval(function () {
				importDocumentsRestApi.getUploadProgress()
					.done((progress) => {
						makeProgressBarVisible(progress.totalSize);
						changeProgressBarProgress(progress.progress);

						if (progress.totalSize === -1 && progress.progress === -1) {
							clearInterval(uploadProgressInterval);
							hideProgressBar();
							tab.$listButton.click();
						}
					})
			}, 2000);
		}

		function trackImportProgress() {
			const importProgressInterval = setInterval(function () {
				importDocumentsRestApi.getImportProgress()
					.done((progress) => {
						makeProgressBarVisible(progress.totalSize);
						changeProgressBarProgress(progress.progress);

						if (progress.totalSize === -1 && progress.progress === -1) {
							clearInterval(importProgressInterval);
							hideProgressBar();
							tab.$listButton.click();
						}
					})
			}, 2000);
		}

		function buildUploadButtonContainer() {
			const $uploadButton = buildUploadButton();
			const $uploadFilename = components.texts.infoText("<div>", '');
			tab.$uploadFilename = $uploadFilename;

			return new BEM({
				block: "imcms-upload-button-container",
				elements: {
					'upload-button': $uploadButton,
					"upload-filename": $uploadFilename
				}
			}).buildBlockStructure("<div>", {});
		}

		function buildTabContainer() {
			loadResources();

			const $fieldStart = tab.$startIdInput = components.texts.textNumber('<div>', {
				placeholder: texts.fieldStart,
				min: 0
			});
			const $fieldEnd = tab.$endIdInput = components.texts.textNumber('<div>', {
				placeholder: texts.fieldEnd,
				min: 0
			});

			//remove empty space
			$fieldStart.find("label").remove();
			$fieldEnd.find("label").remove();

			const $basicImportDocumentsInfoFilter = buildBasicImportDocumentsInfoFilter();
			const $listButton = components.buttons.positiveButton({
				text: "List",
				click: onListButtonClick
			});

			tab.$listButton = $listButton;
			const $importButton = components.buttons.saveButton({
				text: texts.importButton,
				click: onImportButtonClick
			});

			return new BEM({
				block: "imcms-import-documents-tab",
				elements: {
					"field-start": $fieldStart,
					"field-end": $fieldEnd,
					"basic-import-documents-info-filter": $basicImportDocumentsInfoFilter,
					"list-button": $listButton,
					"import-button": $importButton,
				}
			}).buildBlockStructure("<div>", {});
		}

		function buildBasicImportDocumentsInfoFilter() {
			const filterOptions = [
				{
					text: texts.filter.excludeImported,
					value: "excludeImported",
				},
				{
					text: texts.filter.excludeSkip,
					value: "excludeSkip",
				}
			];

			const $basicImportDocumentsInfoFilter = components.selects.multipleSelect('<div>', {
				id: 'basic-import-documents-info-filter',
				name: 'basic-import-documents-info-filter',
			}, filterOptions);

			tab.$basicImportDocumentsInfoFilter = $basicImportDocumentsInfoFilter;
			$basicImportDocumentsInfoFilter.find(".imcms-drop-down-list__select-item-value").text(texts.filter.name);

			return $basicImportDocumentsInfoFilter;
		}

		function onListButtonClick() {
			const startId = tab.$startIdInput.getInput().val();
			const endId = tab.$endIdInput.getInput().val();
			const selectedFilters = tab.$basicImportDocumentsInfoFilter.getSelectedValues();
			const pageable = {
				size: 100,
				page: 0
			};
			const $builder = new ImportDocumentListBuilder();

			let totalPages;

			tab.$resultContainer.children().not(":first").remove();
			basicImportDocumentsInfoRestApi.getAll(pageable, startId, endId, selectedFilters)
				.done(page => {
					totalPages = page.totalPages;
					if (!page.content.length) return;

					$builder.appender(page.content)
				})
				.fail(() => {
					modal.buildErrorWindow(texts.listFailure);
				})

			tab.$resultContainer.off("scroll").on("scroll", function () {
				const $this = tab.$resultContainer;
				const resultBodyHeight = $this.prop("scrollHeight");
				const currentUserScrollPosition = $this.scrollTop();

				if ((currentUserScrollPosition > resultBodyHeight - 2000) && pageable.page !== totalPages) {
					pageable.page += 1;
					basicImportDocumentsInfoRestApi.getAll(pageable, startId, endId, selectedFilters)
						.done(page => {
							if (!page.content.length) return;

							$builder.appender(page.content)
						})
						.fail(() => {
							modal.buildErrorWindow(texts.listFailure);
						})
				}
			})

		}

		function onImportButtonClick() {
			const startId = tab.$startIdInput.getInput().val();
			const endId = tab.$endIdInput.getInput().val();

			if (!startId || !endId) {
				modal.buildWarningWindow("Provide import range!");
				return;
			}

			const params = {
				start: startId,
				end: endId
			}

			tab.$resultContainer.children().not(":first").remove();
			importDocumentsRestApi.importDocuments(params)
				.done(() => {
					trackImportProgress();
				}).fail(response => {
					modal.buildErrorWindow(texts.importFailure)
				}
			);
		}

		function clearData() {
			//
		}

		function loadData(type) {
			importEntityReferenceRestApi.getAllReferences(type)
				.done(references => {
					references.forEach(reference => {
						const $importEntityText = components.texts.textBox("<div>", {
							text: texts.importEntityName,
							value: reference.name,
						}).css({display: "inline-block"});
						$importEntityText.find("input[type=text]").prop("readonly", true);

						const $select = buildReferencesSelect(reference);
						const $row = new BEM({
							block: "imcms-import-entity-reference-manager-row",
							elements: {
								"text": $importEntityText,
								"select": $select,
							}
						}).buildBlockStructure("<div>", {});

						tab.$importEntityReferenceManagerBodyContainer.append($row)
						$select.selectValue(reference.linkedEntityId);
					})
				})
		}

		function buildReferencesSelect(reference) {
			let text;
			let name;
			let options;
			switch (reference.type) {
				case ImportEntityType.ROLE: {
					text = texts.references.roles;
					name = "roles";
					options = tab.$roleOptions;
					break;
				}
				case ImportEntityType.TEMPLATE: {
					text = texts.references.templates;
					name = "templates";
					options = tab.$templateOptions;
					break;
				}
				case ImportEntityType.CATEGORY: {
					text = texts.references.categories;
					name = "categories";
					options = tab.$categoryOptions;
					break;
				}
				case ImportEntityType.CATEGORY_TYPE: {
					text = texts.references.categoryTypes;
					name = "category types";
					options = tab.$categoryTypeOptions;
					break;
				}
			}
			const $select = components.selects.imcmsSelect("<div>", {
				text: text,
				name: name,
				emptySelect: true,
			});

			const $responseMessageDiv = $("<div style='width: fit-content; padding-left: 5%; font-size: smaller;'>");

			components.selects.addOptionsToSelect(options, $select, onReferenceOptionClick(reference, $responseMessageDiv))
			if (options.length)
				$select.find('.imcms-drop-down-list__items').children().first().on('click', () => onNoneReferenceOptionClick(reference, $responseMessageDiv));
			$select.append($responseMessageDiv)

			return $select;
		}

		function onReferenceOptionClick(reference, $responseMessageDiv) {
			return id => {
				reference.linkedEntityId = id;
				importEntityReferenceRestApi.replace(reference)
					.done(() => $responseMessageDiv.text(texts.statusResponses.success).css("color", "green").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT))
					.fail(() => $responseMessageDiv.text(texts.statusResponses.failed).css("color", "red").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT));
			}
		}

		function onNoneReferenceOptionClick(reference, $responseMessageDiv) {
			reference.linkedEntityId = null;
			importEntityReferenceRestApi.replace(reference)
				.done(() => $responseMessageDiv.text(texts.statusResponses.success).css("color", "green").show().hide(DEFAULT_HIDE_TIMEOUT))
				.fail(() => $responseMessageDiv.text(texts.statusResponses.failed).css("color", "red").show().hide(DEFAULT_HIDE_TIMEOUT));
		}

		function buildImportEntityReferenceManager() {
			return new BEM({
				block: "imcms-pop-up-modal",
				elements: {
					"head": importEntityReferenceWindowBuilder.buildHead(texts.importEntityReferenceManagerHead),
					"body": buildBody(),
					"footer": buildFooter()
				}
			}).buildBlockStructure("<div>", {class: "imcms-import-entity-reference-manager"});
		}

		function buildBody() {
			return new BEM({
				block: "imcms-import-entity-reference-manager-body",
				elements: {
					"container": tab.$importEntityReferenceManagerBodyContainer = $("<div>")
				}
			}).buildBlockStructure("<div>");
		}

		function buildFooter() {
			return WindowBuilder.buildFooter([
				components.buttons.negativeButton({
					text: texts.footer.closeButton,
					"class": "",
					click: () => importEntityReferenceWindowBuilder.closeWindow()
				}),
			]);
		}

		const importEntityReferenceWindowBuilder = new WindowBuilder({
			factory: buildImportEntityReferenceManager,
			loadDataStrategy: loadData,
			clearDataStrategy: clearData,
			onEscKeyPressed: "close",
			onEnterKeyPressed: () => importEntityReferenceWindowBuilder.closeWindow()
		});

		return new SuperAdminTab(texts.name, [
			buildUploadButtonContainer(),
			buildTabContainer(),
			buildProgressBar(),
			buildImportDocumentsContainer(),
			buildImportEntityReferenceButtons(),
		]);
	}
)
