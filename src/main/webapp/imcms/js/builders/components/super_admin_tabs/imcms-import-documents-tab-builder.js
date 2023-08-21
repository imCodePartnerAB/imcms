define(
	'imcms-import-documents-tab-builder',
	["imcms-window-builder", "imcms-super-admin-tab", "imcms-bem-builder", "jquery", 'imcms-components-builder',
		'imcms-import-documents-rest-api', 'imcms-modal-window-builder', 'imcms-basic-import-documents-info-rest-api',
		'imcms-import-entity-reference-rest-api', 'imcms-roles-rest-api', 'imcms-templates-rest-api',
		'imcms-categories-rest-api', 'imcms-category-types-rest-api', 'imcms-i18n-texts'],
	function (WindowBuilder, SuperAdminTab, BEM, $, components,
			  importDocumentsRestApi, modal, basicImportDocumentsInfoRestApi,
			  importEntityReferenceRestApi, rolesRestApi, templatesRestApi,
			  categoriesRestApi, categoryTypesRestApi, texts) {
		texts = texts.superAdmin.documentsImport;

		let tab = {};
		const DEFAULT_HIDE_TIMEOUT = 2000;

		const ImportEntityType = {
			ROLE: 'ROLE',
			TEMPLATE: 'TEMPLATE',
			CATEGORY: 'CATEGORY',
			CATEGORY_TYPE: 'CATEGORY_TYPE'
		};

		function mapToOption(entity) {
			return {
				'data-value': entity.id ? entity.id : entity,
				text: entity.name ? entity.name : entity
			}
		}

		function buildProgressBar() {
			const $progressBar = $("<progress id='progress-bar' style='width: 100%; visibility:hidden;'>");
			$.extend($progressBar, {
				show: function () {
					this.reset();
					$progressBar.css("visibility", "visible");
				},
				hide: function () {
					$progressBar.css("visibility", "hidden");
				},
				setTotalSize: function (totalSize) {
					$progressBar.attr("max", totalSize);
				},
				updateProgress: function updateProgress(progress) {
					$progressBar.attr("value", progress);
				},
				reset: function () {
					$progressBar.removeAttr("value");
					$progressBar.removeAttr("max");
				},
				finish: function () {
					this.setTotalSize(0);
					this.updateProgress(1);
				}
			});

			return $progressBar;
		}

		function trackUploadProgress($progressBar) {
			$progressBar.show();
			tab.tabbedContent.disableControls();
			tab.$backBtn.disable();
			const uploadProgressInterval = setInterval(function () {
				importDocumentsRestApi.getUploadProgress()
					.done(progress => {
						if (progress.totalSize !== 1) {
							$progressBar.setTotalSize(progress.totalSize);
							$progressBar.updateProgress(progress.progress);
						}

						if (progress.error) {
							clearInterval(uploadProgressInterval);
							tab.tabbedContent.enableControls();
							tab.$backBtn.enable();
							modal.buildErrorWindow("Error");
							return;
						}

						if (progress.totalSize === progress.progress) {
							clearInterval(uploadProgressInterval);
							tab.tabbedContent.enableControls();
							tab.$backBtn.enable();
						}
					})
			}, DEFAULT_HIDE_TIMEOUT);
		}

		function trackImportProgress($progressBar, $listBtn) {
			$progressBar.show();
			tab.tabbedContent.disableControls();
			tab.$backBtn.disable();
			const importProgressInterval = setInterval(function () {
				importDocumentsRestApi.getImportProgress()
					.done((progress) => {
						if (progress.totalSize !== 1) {
							$progressBar.setTotalSize(progress.totalSize);
							$progressBar.updateProgress(progress.progress);
						}

						if (progress.error) {
							clearInterval(importProgressInterval);
							tab.tabbedContent.enableControls();
							tab.$backBtn.enable();
							modal.buildErrorWindow("Error");
							return;
						}

						if (progress.totalSize === progress.progress) {
							clearInterval(importProgressInterval);
							$listBtn.click();
							$progressBar.finish();
							tab.tabbedContent.enableControls();
							tab.$backBtn.enable();
						}
					})
			}, DEFAULT_HIDE_TIMEOUT);
		}

		function buildListDocumentsBtn() {
			return components.buttons.positiveButton({
				text: texts.controls.listButton.name,
			});
		}

		function buildStartIdInput() {
			const $startIdInput = components.texts.textNumber('<div>', {
				placeholder: texts.controls.rangeInput.startId,
				min: 0
			});
			$startIdInput.find("label").remove();
			return $startIdInput;
		}

		function buildEndIdInput() {
			const $endIdInput = components.texts.textNumber('<div>', {
				placeholder: texts.controls.rangeInput.endId,
				min: 0
			});
			$endIdInput.find("label").remove();
			return $endIdInput;
		}

		function buildMultipleEmptySelect() {
			const $select = $("<select multiple>");

			$.extend($select, {
				getImportDocIds: () => $select.find("option").map((index, option) => option.value).toArray()
			});
			return $select;
		}

		function buildMetaIdInput() {
			return components.texts.textInput({
				placeholder: texts.controls.listInput.docIdInput
			});
		}

		function buildAddMetaIdBtn($metaIdInput, $metaIdListSelect) {
			return components.buttons.neutralButton({
				text: texts.controls.listInput.addButton.name,
				click: () => {
					const metaId = $metaIdInput.val();

					if (isNaN(parseInt(metaId))) {
						alert(texts.controls.listInput.addButton.warning);
						return;
					}

					const $metaIdOption = $("<option>", {
						value: metaId,
						text: metaId
					});

					$metaIdListSelect.append($metaIdOption);
					$metaIdInput.val('');
				}
			});
		}

		function buildRemoveMetaIdBtn($metaIdListSelect) {
			return components.buttons.neutralButton({
				text: texts.controls.listInput.removeButton.name,
				click: () => {
					const $selectedOptions = $metaIdListSelect.find(":selected");

					if (!$selectedOptions.length) {
						alert(texts.controls.listInput.removeButton.warning)
					}

					$selectedOptions.each((index, option) => {
						$(option).remove();
					})
				}
			});
		}

		function handleImportBtnClick($progressBar, $listBtn, importDocIdRange, importDocIdList, $autoImportMenusCheckbox) {
			const body = {
				importDocIds: importDocIdList,
				startId: importDocIdRange?.startId,
				endId: importDocIdRange?.endId,
				autoImportMenus: $autoImportMenusCheckbox.isChecked()
			}

			importDocumentsRestApi.importDocuments(body)
				.done(() => {
					trackImportProgress($progressBar, $listBtn);
				}).fail(response => {
					$progressBar.hide();
					modal.buildErrorWindow(texts.importSection.importFail)
				}
			);
		}

		function handleRemoveAliasesBtnClick($progressBar, importDocIdRange, importDocIdList) {
			const body = {
				importDocIdList: importDocIdList,
				startId: importDocIdRange?.startId,
				endId: importDocIdRange?.endId
			}

			$progressBar.show();
			importDocumentsRestApi.removeAliases(body)
				.done(() => {
					$progressBar.finish();
				}).fail(response => {
					$progressBar.hide();
					modal.buildErrorWindow(texts.controlAliasSection.removeFail)
				}
			);
		}

		function handleReplaceAliasesBtnClick($progressBar, importDocIdRange, importDocIdList) {
			const body = {
				importDocIdList: importDocIdList,
				startId: importDocIdRange?.startId,
				endId: importDocIdRange?.endId
			}

			$progressBar.show();
			importDocumentsRestApi.replaceAliases(body)
				.done(() => {
					$progressBar.finish();
				}).fail(response => {
					$progressBar.hide();
					modal.buildErrorWindow(texts.controlAliasSection.replaceFail)
				}
			);
		}

		function buildRangeInputContainer($resultContainer, $builder, $progressBar,$autoImportMenusCheckbox, $listBtn) {
			const $startIdInput = buildStartIdInput();
			const $endIdInput = buildEndIdInput();
			const $basicImportDocumentsInfoFilter = buildBasicImportDocumentsInfoFilter();

			const $rangeInputContainer = new BEM({
				block: "range-input",
				elements: {
					"title": components.texts.titleText("<div>", texts.controls.rangeInput.name),
					"start-id": $startIdInput,
					"end-id": $endIdInput,
					"filters": $basicImportDocumentsInfoFilter,
				}
			}).buildBlockStructure("<div>", {});

			function onListBtnClick() {
				const startId = $startIdInput.getInput().val();
				const endId = $endIdInput.getInput().val();
				const selectedFilters = $basicImportDocumentsInfoFilter.getSelectedValues();
				const pageable = {
					size: 100,
					page: 0
				};

				let totalPages;
				$builder.empty();
				basicImportDocumentsInfoRestApi.getAll(pageable, startId, endId, null, selectedFilters)
					.done(page => {
						totalPages = page.totalPages;
						if (!page.content.length) return;

						$builder.append(page.content)
					})
					.fail(() => {
						modal.buildErrorWindow(texts.controls.listButton.fail);
					})

				$resultContainer.off("scroll").on("scroll", function () {
					const resultBodyHeight = $resultContainer.prop("scrollHeight");
					const currentUserScrollPosition = $resultContainer.scrollTop();

					if ((currentUserScrollPosition > resultBodyHeight - 2000) && pageable.page !== totalPages) {
						pageable.page += 1;
						basicImportDocumentsInfoRestApi.getAll(pageable, startId, endId, null, selectedFilters)
							.done(page => {
								if (!page.content.length) return;
								$builder.append(page.content)
							})
							.fail(() => {
								modal.buildErrorWindow(texts.controls.listButton.fail);
							})
					}
				})
			}

			function onImportBtnClick() {
				const startId = $startIdInput.getInput().val();
				const endId = $endIdInput.getInput().val();

				if (!startId || !endId) {
					modal.buildWarningWindow(texts.controls.rangeInput.warning);
					return;
				}

				const importDocIdRange = {
					startId: startId,
					endId: endId
				}

				handleImportBtnClick($progressBar, $listBtn, importDocIdRange, null,$autoImportMenusCheckbox);
			}

			function onRemoveAliasesBtnClick() {
				const startId = $startIdInput.getInput().val();
				const endId = $endIdInput.getInput().val();

				if (!startId || !endId) {
					modal.buildWarningWindow(texts.controls.rangeInput.warning);
					return;
				}

				const importDocIdRange = {
					startId: startId,
					endId: endId
				}

				handleRemoveAliasesBtnClick($progressBar, importDocIdRange, null);
			}

			function onReplaceAliasesBtnClick() {
				const startId = $startIdInput.getInput().val();
				const endId = $endIdInput.getInput().val();

				if (!startId || !endId) {
					modal.buildWarningWindow(texts.controls.rangeInput.warning);
					return;
				}

				const importDocIdRange = {
					startId: startId,
					endId: endId
				}

				handleReplaceAliasesBtnClick($progressBar, importDocIdRange, null);
			}

			$.extend($rangeInputContainer, {
				onListBtnClick: onListBtnClick,
				onImportBtnClick: onImportBtnClick,
				onRemoveAliasesBtnClick: onRemoveAliasesBtnClick,
				onReplaceAliasesBtnClick: onReplaceAliasesBtnClick
			});

			return $rangeInputContainer;
		}

		function buildListInputContainer($resultContainer, $builder, $progressBar, $autoImportMenusCheckbox,$listBtn) {
			const $metaIdListSelect = buildMultipleEmptySelect();
			const $metaIdInput = buildMetaIdInput();
			const $addButton = buildAddMetaIdBtn($metaIdInput, $metaIdListSelect);
			const $removeButton = buildRemoveMetaIdBtn($metaIdListSelect);

			const $listInputContainer = new BEM({
				block: "list-input",
				elements: {
					title: components.texts.titleText("<div>", texts.controls.listInput.name),
					"meta-id-list-select": $metaIdListSelect,
					"meta-id-input": new BEM({
						block: "meta-id-input",
						elements: {
							"meta-id-input": $metaIdInput,
							"controls": new BEM({
								block: "controls",
								elements: {
									"add-button": $addButton,
									"remove-button": $removeButton
								}
							}).buildBlockStructure("<div>", {})
						}
					}).buildBlockStructure("<div>", {}),
				}
			}).buildBlockStructure("<div>", {});

			function onListBtnClick() {
				const pageable = {
					size: 100,
					page: 0
				};
				const importDocIds = $metaIdListSelect.getImportDocIds();

				let totalPages;
				$builder.empty();
				basicImportDocumentsInfoRestApi.getAll(pageable, null, null, importDocIds)
					.done(page => {
						totalPages = page.totalPages;
						if (!page.content.length) return;

						$builder.append(page.content)
					})
					.fail(() => {
						modal.buildErrorWindow(texts.controls.listButton.fail);
					})

				$resultContainer.off("scroll").on("scroll", function () {
					const resultBodyHeight = $resultContainer.prop("scrollHeight");
					const currentUserScrollPosition = $resultContainer.scrollTop();

					if ((currentUserScrollPosition > resultBodyHeight - 2000) && pageable.page !== totalPages) {
						pageable.page += 1;
						basicImportDocumentsInfoRestApi.getAll(pageable, null, null, importDocIds)
							.done(page => {
								if (!page.content.length) return;

								$builder.append(page.content)
							})
							.fail(() => {
								modal.buildErrorWindow(texts.controls.listButton.fail);
							})
					}
				})
			}

			function onImportBtnClick() {
				handleImportBtnClick($progressBar, $listBtn, null, $metaIdListSelect.getImportDocIds(),$autoImportMenusCheckbox);
			}

			function onRemoveAliasesBtnClick() {
				handleRemoveAliasesBtnClick($progressBar, null, $metaIdListSelect.getImportDocIds());
			}

			function onReplaceAliasesBtnClick() {
				handleReplaceAliasesBtnClick($progressBar, null, $metaIdListSelect.getImportDocIds());
			}

			$.extend($listInputContainer, {
				onListBtnClick: onListBtnClick,
				onImportBtnClick: onImportBtnClick,
				onRemoveAliasesBtnClick: onRemoveAliasesBtnClick,
				onReplaceAliasesBtnClick: onReplaceAliasesBtnClick
			});

			return $listInputContainer;
		}

		function buildBasicImportDocumentsInfoFilter() {
			const filterOptions = [
				{text: texts.controls.filter.excludeImported, value: "excludeImported",},
				{text: texts.controls.filter.excludeSkipped, value: "excludeSkip",}
			];

			const $basicImportDocumentsInfoFilter = components.selects.multipleSelect('<div>', {
				id: 'basic-import-documents-info-filter',
				name: 'basic-import-documents-info-filter',
			}, filterOptions);

			tab.$basicImportDocumentsInfoFilter = $basicImportDocumentsInfoFilter;
			$basicImportDocumentsInfoFilter.find(".imcms-drop-down-list__select-item-value").text(texts.controls.filter.name);

			return $basicImportDocumentsInfoFilter;
		}

		function buildSelectionWindowContainer() {
			const sectionTypes = {
				INCLUDE_UPLOAD_DOCUMENTS_SECTION: "INCLUDE_UPLOAD_DOCUMENTS_SECTION",
				DEFAULT: "DEFAULT",
				ALIAS_CONTROL: "ALIAS_CONTROL"
			};

			function buildSections(sectionType) {
				let $sections;
				switch (sectionType) {
					case sectionTypes.INCLUDE_UPLOAD_DOCUMENTS_SECTION: {
						$sections = [
							buildUploadDocumentsSection(),
							buildEditImportedDocumentsSection(),
							buildImportEntityReferencesSection(),
							buildImportSection()];
						break;
					}
					case sectionTypes.ALIAS_CONTROL: {
						$sections = [buildControlAliasSection()];
						tab.tabbedContent.disableControls();
						break;
					}
					default: {
						$sections = [
							buildEditImportedDocumentsSection(),
							buildImportEntityReferencesSection(),
							buildImportSection(),
						];
						break;
					}
				}

				for (const [index, $section] of $sections.entries()) {
					$section
						.attr("id", index)
						.addClass("section")
						.css("display", index === 0 ? "block" : "none")

					tab.$sections.append($section);
				}
				tab.$sectionsContainer.show();
			}

			const $uploadDocumentsCard = new BEM({
				block: "upload-documents-card",
				elements: {
					"icon": $("<img src='/imcms/images/import_documents/upload_icon.svg'>"),
					"title": components.texts.titleText("<div>", texts.selectionWindowContainer.uploadDocumentsCard)
				}
			}).buildBlockStructure("<div>", {});

			const $editDocumentsCard = new BEM({
				block: "edit-documents-card",
				elements: {
					"icon": $("<img src='/imcms/images/import_documents/edit_icon.svg'>"),
					"title": components.texts.titleText("<div>", texts.selectionWindowContainer.editDocumentsCard)
				}
			}).buildBlockStructure("<div>", {});

			const $aliasControlCard = new BEM({
				block: "alias-control-card",
				elements: {
					"icon": $("<img src='/imcms/images/import_documents/document_edit_icon.svg'>"),
					"title": components.texts.titleText("<div>", texts.selectionWindowContainer.aliasControlCard)
				}
			}).buildBlockStructure("<div>", {});

			tab.$selectionWindowContainer = new BEM({
				block: "selection-window-container",
				elements: {
					'upload-documents-card': $uploadDocumentsCard,
					'edit-documents-card': $editDocumentsCard,
					'alias-control': $aliasControlCard
				}
			}).buildBlockStructure("<div>", {});

			$uploadDocumentsCard.on('click', () => {
				buildSections(sectionTypes.INCLUDE_UPLOAD_DOCUMENTS_SECTION);
				tab.$selectionWindowContainer.hide();
				tab.$sectionsContainer.show();
			});
			$editDocumentsCard.on('click', () => {
				buildSections(sectionTypes.DEFAULT);
				tab.$selectionWindowContainer.hide();
				tab.$sectionsContainer.show();
			});
			$aliasControlCard.on("click", () => {
				buildSections(sectionTypes.ALIAS_CONTROL)
				tab.$selectionWindowContainer.hide();
				tab.$sectionsContainer.show();
			})

			return tab.$selectionWindowContainer;
		}

		function buildUploadDocumentsSection() {
			const $filename = components.texts.infoText("<div>");
			const $progressBar = buildProgressBar();

			function handleFileUpload(formData) {
				importDocumentsRestApi.upload(formData)
					.done(zipFilename => {
						$filename.text(zipFilename);
						trackUploadProgress($progressBar);

						$fileInput.val('');
					})
					.fail(() => modal.buildErrorWindow(texts.uploadDocumentsSection.uploadFail));
			}

			const $fileInput = $('<input>', {
				type: 'file',
				accept: "application/zip",
				change: () => {
					const formData = new FormData();
					const file = $fileInput.prop("files")[0];

					formData.append("file", file);
					handleFileUpload(formData);
				}
			});

			const $uploadButton = components.buttons.saveButton({
				text: texts.uploadDocumentsSection.uploadButton,
				click: () => $fileInput.click()
			})

			const $uploadArea = new BEM({
				block: "upload-area",
				elements: {
					"upload-button": $uploadButton,
					"filename": $filename,
					"progress-bar": $progressBar
				}
			}).buildBlockStructure("<div>", {});

			$(document.body).on("dragenter dragstart dragend dragleave dragover drag drop", function (e) {
				e.preventDefault();
				e.stopPropagation();
			});
			$uploadArea.on("dragenter dragstart dragend dragleave dragover drag drop", function (e) {
				e.preventDefault();
				e.stopPropagation();
			});

			$uploadArea.on({
				dragenter: function (e) {
					$uploadArea.addClass("highlight");
				},
				dragover: function (e) {
					$uploadArea.addClass("highlight");
				},
				dragleave: function (e) {
					$uploadArea.removeClass("highlight");
				},
				drop: function (e) {
					$uploadArea.removeClass("highlight");
					const file = e.originalEvent.dataTransfer.files[0];
					const formData = new FormData();

					formData.append("file", file);
					handleFileUpload(formData);
				}
			})

			return new BEM({
				block: "upload-documents-section",
				elements: {
					"upload-area": $uploadArea
				}
			}).buildBlockStructure("<section>", {});
		}

		function buildEditImportedDocumentsSection() {
			const $resultContainer = $('<div>', {'class': 'table-import-documents'});
			const $builder = new ImportDocumentListBuilder($resultContainer);
			const $listBtn = buildListDocumentsBtn();

			const $rangeInputContainer = buildRangeInputContainer($resultContainer, $builder);
			const $listInputContainer = buildListInputContainer($resultContainer, $builder);

			const $switchInputTypeButton = components.buttons.neutralButton({
				text: texts.controls.switchInputType,
				click: () => {
					if ($listInputContainer.css("display") === "none") {
						$listBtn.off('click').on('click', $listInputContainer.onListBtnClick);
						$listInputContainer.show();
						$rangeInputContainer.hide();
						$resultContainer.css("height", "530");
					} else {
						$listBtn.off('click').on('click', $rangeInputContainer.onListBtnClick);
						$rangeInputContainer.css("display", "inline");
						$listInputContainer.hide()
						$resultContainer.css("height", "635px");
					}
				}
			});

			$listBtn.click($listInputContainer.onListBtnClick);
			return new BEM({
				block: "edit-imported-documents-section",
				elements: {
					"switch-input-type-button": $switchInputTypeButton,
					"range-input": $rangeInputContainer,
					"list-input": $listInputContainer,
					"list-button": $listBtn,
					"result-container": $resultContainer
				}
			}).buildBlockStructure("<section>", {});
		}

		function buildImportEntityReferencesSection() {
			function buildReferencesSelect(reference) {
				function onNoneReferenceOptionClick($responseMessageDiv) {
					reference.linkedEntityId = null;
					importEntityReferenceRestApi.replace(reference)
						.done(() => $responseMessageDiv.text(texts.importEntityReferenceSection.success).css("color", "green").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT))
						.fail(() => $responseMessageDiv.text(texts.importEntityReferenceSection.fail).css("color", "red").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT));
				}

				function onReferenceOptionClick($responseMessageDiv) {
					return id => {
						reference.linkedEntityId = id;
						importEntityReferenceRestApi.replace(reference)
							.done(() => $responseMessageDiv.text(texts.importEntityReferenceSection.success).css("color", "green").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT))
							.fail(() => $responseMessageDiv.text(texts.importEntityReferenceSection.fail).css("color", "red").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT));
					}
				}

				let options;
				if (reference.type === ImportEntityType.CATEGORY_TYPE)
					options = tab.$categoryTypeOptions;
				if (reference.type === ImportEntityType.CATEGORY)
					options = tab.$categoryOptions;
				if (reference.type === ImportEntityType.ROLE)
					options = tab.$roleOptions;
				if (reference.type === ImportEntityType.TEMPLATE)
					options = tab.$templateOptions;

				const $select = components.selects.imcmsSelect("<div>", {
					emptySelect: true,
				});

				const $responseMessageDiv = $("<div style='width: fit-content; padding-left: 5%; font-size: smaller;'>");

				components.selects.addOptionsToSelect(options, $select, onReferenceOptionClick($responseMessageDiv))

				if (options.length)
					$select.find('.imcms-drop-down-list__items').children().first().on('click', () => onNoneReferenceOptionClick($responseMessageDiv));

				$select.append($responseMessageDiv)

				return $select;
			}

			function buildText(importEntityType, isRB4, isPlural) {
				let text = isRB4 ? "RB4 " : "RB6 ";
				if (importEntityType === ImportEntityType.CATEGORY_TYPE)
					text += isPlural ? texts.importEntityReferenceSection.categoryTypeReferenceTitlePlural : texts.importEntityReferenceSection.categoryTypeReferenceTitle;
				if (importEntityType === ImportEntityType.CATEGORY)
					text += isPlural ? texts.importEntityReferenceSection.categoryReferenceTitlePlural : texts.importEntityReferenceSection.categoryTypeReferenceTitle;
				if (importEntityType === ImportEntityType.ROLE)
					text += isPlural ? texts.importEntityReferenceSection.roleReferenceTitlePlural : texts.importEntityReferenceSection.roleReferenceTitle;
				if (importEntityType === ImportEntityType.TEMPLATE)
					text += isPlural ? texts.importEntityReferenceSection.templateReferenceTitlePlural : texts.importEntityReferenceSection.templateReferenceTitle;
				return components.texts.titleText("<div>", text);
			}

			function loadResources(importEntityType) {
				if (importEntityType === ImportEntityType.CATEGORY_TYPE)
					categoryTypesRestApi.read().done(categoryTypes => tab.$categoryTypeOptions = categoryTypes.map(mapToOption));
				if (importEntityType === ImportEntityType.CATEGORY)
					categoriesRestApi.read().done(categories => tab.$categoryOptions = categories.map(mapToOption));
				if (importEntityType === ImportEntityType.ROLE)
					rolesRestApi.read().done(roles => tab.$roleOptions = roles.map(mapToOption));
				if (importEntityType === ImportEntityType.TEMPLATE)
					templatesRestApi.read().done(templates => tab.$templateOptions = templates.map(mapToOption));
			}

			function buildButton(name, importEntityType) {
				return components.buttons.neutralButton({
					text: name,
					click: () => {
						loadResources(importEntityType);
						$importEntityReferenceContainer.empty();
						importEntityReferenceRestApi.getAllReferences(importEntityType)
							.done(references => {
								const $titles = new BEM({
									block: "titles",
									elements: {
										"imcms-rb4-entity": buildText(importEntityType, true, false),
										"imcms-rb6-entities": buildText(importEntityType, false, true)
									}
								}).buildBlockStructure("<div>", {});

								$importEntityReferenceContainer.append($titles);
								references.forEach(reference => {
									const $importEntityText = components.texts.textBox("<div>", {
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

									$importEntityReferenceContainer.append($row);
									$select.selectValue(reference.linkedEntityId);
								})
							})
					}
				});
			}

			const $importEntityReferenceButtons = new BEM({
				block: "imcms-import-entity-reference-buttons",
				elements: {
					'role': buildButton(texts.importEntityReferenceSection.roleReferences, ImportEntityType.ROLE),
					'template': buildButton(texts.importEntityReferenceSection.templateReferences, ImportEntityType.TEMPLATE),
					'category': buildButton(texts.importEntityReferenceSection.categoryReferences, ImportEntityType.CATEGORY),
					'category-type': buildButton(texts.importEntityReferenceSection.categoryTypeReferences, ImportEntityType.CATEGORY_TYPE),
				}
			}).buildBlockStructure('<div>', {});
			const $importEntityReferenceContainer = $("<div>");

			return new BEM({
				block: "import-entity-references-section",
				elements: {
					"import-entity-reference-buttons": $importEntityReferenceButtons,
					"import-entity-reference-container": $importEntityReferenceContainer
				}
			}).buildBlockStructure("<section>", {});
		}

		function buildImportSection() {
			const $resultContainer = $('<div>', {'class': 'table-import-documents', 'style': 'height:520px'});
			const $progressBar = buildProgressBar();
			const $builder = new ImportDocumentListBuilder($resultContainer);
			const $listBtn = buildListDocumentsBtn();
			const $importBtn = components.buttons.saveButton({
				text: texts.importSection.importButton,
			});
			const $autoImportMenusCheckbox = components.checkboxes.imcmsCheckbox("<div>", {text: "Auto import menus"});

			const $rangeInputContainer = buildRangeInputContainer($resultContainer, $builder, $progressBar,$autoImportMenusCheckbox, $listBtn);
			const $listInputContainer = buildListInputContainer($resultContainer, $builder, $progressBar,$autoImportMenusCheckbox, $listBtn);

			$listBtn.off('click').on('click', $listInputContainer.onListBtnClick);
			$importBtn.off('click').on('click', $listInputContainer.onImportBtnClick);
			const $switchInputTypeButton = components.buttons.neutralButton({
				text: texts.controls.switchInputType,
				click: () => {
					if ($listInputContainer.css("display") === "none") {
						$listBtn.off('click').on('click', $listInputContainer.onListBtnClick);
						$importBtn.off('click').on('click', $listInputContainer.onImportBtnClick);
						$listInputContainer.show();
						$rangeInputContainer.hide();
						$resultContainer.css("height", "520px");

					} else {
						$listBtn.off('click').on('click', $rangeInputContainer.onListBtnClick);
						$importBtn.off('click').on('click', $rangeInputContainer.onImportBtnClick);
						$rangeInputContainer.css("display", "inline");
						$listInputContainer.hide()
						$resultContainer.css("height", "605px");
					}
				}
			});

			return new BEM({
				block: "import-section",
				elements: {
					"switch-input-type-button": $switchInputTypeButton,
					"range-input": $rangeInputContainer,
					"list-input": $listInputContainer,
					"container":$("<div>").append($importBtn).append($autoImportMenusCheckbox),
						"progress-bar": $progressBar,
					"result-container": $resultContainer
				}
			}).buildBlockStructure("<section>", {});
		}

		function buildControlAliasSection() {
			const $progressBar = buildProgressBar();

			const $removeAliasesBtn = components.buttons.saveButton({
				text: texts.controlAliasSection.removeAliases
			});
			const $replaceAliasesBtn = components.buttons.saveButton({
				text: texts.controlAliasSection.replaceAliases
			});

			const $rangeInputContainer = buildRangeInputContainer(null, null, $progressBar, null);
			const $listInputContainer = buildListInputContainer(null, null, $progressBar, null);

			$removeAliasesBtn.click($listInputContainer.onRemoveAliasesBtnClick);
			$replaceAliasesBtn.click($listInputContainer.onReplaceAliasesBtnClick);
			const $switchInputTypeButton = components.buttons.neutralButton({
				text: texts.controls.switchInputType,
				click: () => {
					if ($listInputContainer.css("display") === "none") {
						$listInputContainer.show();
						$rangeInputContainer.hide();
						$removeAliasesBtn.off('click').on('click', $listInputContainer.onRemoveAliasesBtnClick);
						$replaceAliasesBtn.off('click').on('click', $listInputContainer.onReplaceAliasesBtnClick);
					} else {
						$rangeInputContainer.css("display", "inline");
						$listInputContainer.hide()
						$removeAliasesBtn.off('click').on('click', $rangeInputContainer.onRemoveAliasesBtnClick);
						$replaceAliasesBtn.off('click').on('click', $rangeInputContainer.onReplaceAliasesBtnClick);
					}
				}
			});

			return new BEM({
				block: "alias-control-section",
				elements: {
					"switch-input-type-button": $switchInputTypeButton,
					"range-input": $rangeInputContainer,
					"list-input": $listInputContainer,
					"remove-aliases-button": $removeAliasesBtn,
					"replace-aliases-button": $replaceAliasesBtn,
					"progress-bar": $progressBar,
				}
			}).buildBlockStructure("<section>", {});
		}

		function buildSectionsContainer() {
			const $backBtn = components.buttons.closeButton({});

			tab.$backBtn = $.extend($backBtn, {
				disable: function () {
					$backBtn.attr("disabled", true);
				},
				enable: function () {
					$backBtn.removeAttr("disabled")
				}
			});

			const $prevBtn = components.buttons.positiveButton({
				id: "previousBtn",
				text: texts.controls.previous,
				"class": "disable"
			});

			const $nextBtn = components.buttons.positiveButton({
				id: "nextBtn",
				text: texts.controls.next,
			});

			const $controlButtons = new BEM({
				block: "control-buttons",
				elements: {
					"previous-button": $prevBtn,
					"next-button": $nextBtn
				}
			}).buildBlockStructure("<div>", {});

			tab.$sectionsContainer = new BEM({
				block: "sections-container",
				elements: {
					"back-button": tab.$backBtn,
					"sections": tab.$sections = $("<div>"),
					"control-buttons": $controlButtons
				}
			})
				.buildBlockStructure("<div>", {})
				.css("display", "none");

			tab.tabbedContent = new TabbedContent(tab.$sections, $prevBtn, $nextBtn);

			$prevBtn.on('click', () => {
				tab.tabbedContent.prev();
			})
			$nextBtn.on('click', () => {
				tab.tabbedContent.next();
			})

			tab.$backBtn.on('click', () => {
				tab.$sectionsContainer.hide();
				tab.$sections.empty();
				tab.$selectionWindowContainer.show();
				tab.tabbedContent.reset();
			})

			return tab.$sectionsContainer;
		}

		class TabbedContent {
			constructor($sections, $prevButton, $nextButton) {
				this.$sections = $sections;
				this.$prevButton = $prevButton;
				this.$nextButton = $nextButton;
				this.current = 0;
			}

			disableControls() {
				this.$prevButton.attr("disabled", true).addClass("disable");
				this.$nextButton.attr("disabled", true).addClass("disable");
			}

			enableControls() {
				this.#togglePrev();
				this.#toggleNext();
			}

			reset() {
				this.current = 0;
				this.#toggleNext();
				this.#togglePrev();
			}

			#toggleSections() {
				this.$sections.children().each((index, section) => {
					//display: none
					$(section).hide();
				});
				$(this.$sections.children()[this.current]).show();
			}

			#togglePrev() {
				if (this.current === 0) {
					this.$prevButton.attr("disabled", true).addClass("disable")
				} else {
					this.$prevButton.removeAttr("disabled").removeClass("disable");
				}
			}

			#toggleNext() {
				if (this.current === this.$sections.children().length - 1) {
					this.$nextButton.attr("disabled", true).addClass("disable")
				} else {
					this.$nextButton.removeAttr("disabled").removeClass("disable");
				}
			}

			next() {
				if (this.current < this.$sections.children().length - 1) {
					this.current++
				}
				// this.toggleTabs();
				this.#toggleSections();
				this.#toggleNext();
				this.#togglePrev();
			}

			prev() {
				if (this.current > 0) {
					this.current--
				}
				// this.toggleTabs();
				this.#toggleSections();
				this.#toggleNext();
				this.#togglePrev();
			}
		}

		const ImportDocumentStatus = {
			IMPORT: "IMPORT",
			UPDATE: "UPDATE",
			IMPORTED: "IMPORTED",
			SKIP: "SKIP",
			SKIPPED: "SKIPPED",
			FAILED: "FAILED"
		};

		class ImportDocumentListBuilder {
			constructor($containerResult) {
				this.$containerResult = $containerResult;
			}

			append(basicImportDocuments) {
				if (!this.$containerResult.children().length) {
					this.#buildTitles()
				}
				this.#addToList(basicImportDocuments.map(this.#toRow));
			}

			empty() {
				this.$containerResult.children().not(":first").remove();
			}

			#buildTitles = () => {
				const $titles = new BEM({
					block: "basic-import-document-titles",
					elements: {
						'id': $("<div>", {text: texts.importDocumentListBuilder.titles.id}),
						"metaId": $("<div>", {text: texts.importDocumentListBuilder.titles.metaId}),
						"status": $("<div>", {text: texts.importDocumentListBuilder.titles.status})
					}
				}).buildBlockStructure("<div>", {})

				this.$containerResult.append($titles);
				return this;
			}

			#addToList = (rows) => {
				this.$containerResult.css('display', 'inline-block').append(rows)
				return this;
			}

			#toRow = (basicImportDocument) => {
				return new BEM({
					block: "basic-import-document-info-row",
					elements: {
						'id': $("<div>", {text: basicImportDocument.id}),
						'metaId': this.#buildMetaIdLink(basicImportDocument),
						"status": this.#buildStatusColumn(basicImportDocument)
					}
				}).buildBlockStructure("<div>", {});
			}

			#buildMetaIdLink = (basicImportDocument) => {
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

			#buildStatusColumn = (basicImportDocument) => {
				const status = basicImportDocument.status;

				switch (status) {
					case ImportDocumentStatus.IMPORT:
					case ImportDocumentStatus.SKIP:
						return this.#buildImportDocumentStatusSelect(basicImportDocument, this.#optionsForNonImportedDocument()).selectValue(status);
					case ImportDocumentStatus.IMPORTED:
					case ImportDocumentStatus.UPDATE:
						return this.#buildImportDocumentStatusSelect(basicImportDocument, this.#optionsForImportedDocument()).selectValue(status)

					default: {
						return components.texts.textBox("<div>", {
							value: status,
							readonly: "readonly"
						});
					}
				}
			}

			#buildImportDocumentStatusSelect = (basicImportDocument, options) => {
				const $responseMessageDiv = $("<div>")
					.css({
						"padding-left": "5px",
						"font-size": "smaller",
					});
				const $importDocumentStatusSelect = components.selects.imcmsSelect("<div>", {
					emptySelect: false,
					onSelected: () => this.#onStatusSelect($importDocumentStatusSelect, $responseMessageDiv, basicImportDocument)
				}, options);

				$importDocumentStatusSelect.append($responseMessageDiv);
				return $importDocumentStatusSelect;
			}

			#onStatusSelect = ($importDocumentStatusSelect, $responseMessageDiv, basicImportDocument) => {
				basicImportDocument.status = $importDocumentStatusSelect.getSelectedValue();
				basicImportDocumentsInfoRestApi.update(basicImportDocument)
					.done(response => $responseMessageDiv.text(texts.importEntityReferenceSection.success).css("color", "green").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT))
					.fail(response => $responseMessageDiv.text(texts.importEntityReferenceSection.fail).css("color", "red").slideDown().slideUp(DEFAULT_HIDE_TIMEOUT));
			}

			#optionsForImportedDocument = () => {
				return [
					this.#mapToOption(ImportDocumentStatus.IMPORTED),
					this.#mapToOption(ImportDocumentStatus.UPDATE),
				];
			}

			#optionsForNonImportedDocument = () => {
				return [
					this.#mapToOption(ImportDocumentStatus.IMPORT),
					this.#mapToOption(ImportDocumentStatus.SKIP),
				];
			}

			#mapToOption = (entity) => {
				return {
					'data-value': entity.id ? entity.id : entity,
					text: entity.name ? entity.name : entity
				}
			}
		}

		const ImportDocumentsAdminTab = function (name, tabElements) {
			SuperAdminTab.call(this, name, tabElements);
		};

		ImportDocumentsAdminTab.prototype = Object.create(SuperAdminTab.prototype);

		ImportDocumentsAdminTab.prototype.getDocLink = () => texts.documentationLink;

		return new ImportDocumentsAdminTab(texts.name,
			[
				buildSelectionWindowContainer(),
				buildSectionsContainer()
			],
			{
				style: 'position: relative; height: 100%;'
			}
		);
	}
)
