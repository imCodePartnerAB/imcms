define(
	'imcms-templates-css-tab-builder',
	["imcms-window-builder", 'imcms-super-admin-tab', 'imcms-i18n-texts', 'imcms-components-builder', 'imcms-modal-window-builder', 'imcms-field-wrapper',
		'jquery', 'imcms-bem-builder', 'imcms-templates-rest-api', 'imcms-templates-css-rest-api', 'imcms-templates-css-versions'],
	function (WindowBuilder, SuperAdminTab, texts, components, modal, fieldWrapper, $, BEM, templatesRestApi, templatesCSSRestApi, templatesCSSVersions) {
		texts = texts.superAdmin.templatesCSS;

		let tab = {};

		function buildTabTitle() {
			return fieldWrapper.wrap(components.texts.titleText('<div>', texts.editorTitle));
		}

		function buildTemplatesSelectContainer() {
			const $templatesSelect = components.selects.imcmsSelect("<div>", {
					text: texts.templatesSelectTitle,
					name: "templates",
					emptySelect: true
				}),
				$loadHistoryBtn = components.buttons.positiveButton({
					text: texts.buttons.historyBtnText,
					click: onLoadHistoryButtonClick
				}),
				$controls = new BEM({
					block: 'controls',
					elements: {
						'load-history-button': $loadHistoryBtn
					}
				}).buildBlockStructure('<div>', {});

			tab.$templatesSelect = $templatesSelect;
			tab.$loadHistoryBtn = $loadHistoryBtn;

			templatesRestApi.read()
				.done(templates => {
					const templateOptions = templates.map(template => {
						return {
							'data-value': template.name,
							text: template.name
						}
					})

					components.selects.addOptionsToSelect(templateOptions, $templatesSelect, onOptionSelect);
					$templatesSelect.find('.imcms-drop-down-list__items').children().first().on('click', onNoneOptionClick);
				})

			return new BEM({
				block: "templates-select-container",
				elements: {
					'select': $templatesSelect,
					'controls': $controls
				}
			}).buildBlockStructure('<div>')
		}

		function onOptionSelect() {
			tab.$controls.$leftSide.$activeVersionBtn.click();
			tab.$controls.$leftSide.slideDown();
		}

		function onLoadHistoryButtonClick() {
			templatesCSSHistoryWindowBuilder.buildWindowWithShadow.apply(templatesCSSHistoryWindowBuilder, arguments)
		}

		function onNoneOptionClick() {
			tab.$controls.$leftSide.slideUp();
			tab.$controls.$rightSide.slideUp();
			tab.$loadHistoryBtn.slideUp();
			onClearBtnClick();
		}

		function buildTemplateCSSEditAreaContainer() {
			const $templateCSSEditArea = $('<textarea disabled="disabled">');
			const $loadingAnimationWrapEditArea = $("<div>").append($("<div class='loading-animation'>"));

			const $templateCSSEditAreaContainer = new BEM({
				block: 'templates-css-edit-area-container',
				elements: {
					'edit-area': $templateCSSEditArea,
					'loading-animation': $loadingAnimationWrapEditArea
				}
			}).buildBlockStructure('<div>', {});

			tab.$templateCSSEditArea = $templateCSSEditArea;
			tab.$loadingAnimationWrapEditArea = $loadingAnimationWrapEditArea;

			components.overlays.defaultTooltip(tab.$templateCSSEditArea, texts.errors.EMPTY_AREA, {
				delay: 0,
				followCursor: true,
			});

			return $templateCSSEditAreaContainer;
		}

		function buildControls() {
			const $activeVersionBtn = components.buttons.neutralButton({
					text: texts.buttons.activeVersionBtnText,
					class: "active",
					click: onActiveVersionButtonClick
				}),
				$workingVersionButton = components.buttons.neutralButton({
					text: texts.buttons.workingVersionBtnText,
					click: onWorkingVersionButtonClick
				}),
				$saveButton = components.buttons.positiveButton({
					text: texts.buttons.saveBtnText,
					click: onSaveButtonClick
				}),
				$clearButton = components.buttons.negativeButton({
					text: texts.buttons.clearBtnText,
					click: onClearBtnClick
				}),
				$publishButton = components.buttons.saveButton({
					text: texts.buttons.publishBtnText,
					click: onPublishButtonClick
				});

			const $leftSide = new BEM({
					block: 'templates-css-controls-left-side',
					elements: {
						'active-version': $activeVersionBtn,
						'working': $workingVersionButton
					}
				}).buildBlockStructure('<div>', {}),
				$rightSide = new BEM({
					block: 'templates-css-controls-right-side',
					elements: {
						'publish': $publishButton,
						'save': $saveButton,
						'clear': $clearButton,
					}
				}).buildBlockStructure('<div>', {});

			const $controls = new BEM({
				block: 'templates-css-controls',
				elements: {
					'left-side': $leftSide,
					'right-side': $rightSide
				}
			}).buildBlockStructure('<div>', {});

			tab.$controls = $controls;
			tab.$controls.$leftSide = $leftSide;
			tab.$controls.$leftSide.$activeVersionBtn = $activeVersionBtn;
			tab.$controls.$rightSide = $rightSide;

			return $controls;
		}

		function onActiveVersionButtonClick() {
			const templateName = tab.$templatesSelect.getSelectedValue();

			switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, true);
			templatesCSSRestApi.get(templateName, templatesCSSVersions.ACTIVE)
				.done((data) => {
					tab.$templateCSSEditArea.attr('disabled', true).css('cursor', 'not-allowed').val(data);
					tab.$loadHistoryBtn.slideUp();
					tab.$controls.$rightSide.slideUp();

					underlineCurrentVersionBtn.apply(this);

					components.overlays.enable(tab.$templateCSSEditArea)
					components.overlays.changeTooltipText(tab.$templateCSSEditArea, texts.errors.ACTIVE_VERSION);
				})
				.always(() => {
					switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, false);
				})
		}

		function onWorkingVersionButtonClick() {
			const templateName = tab.$templatesSelect.getSelectedValue();

			switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, true);
			templatesCSSRestApi.get(templateName, templatesCSSVersions.WORKING)
				.done((data) => {
					tab.$templateCSSEditArea.attr('disabled', false).css('cursor', 'auto').val(data);
					tab.$loadHistoryBtn.slideDown();
					tab.$controls.$rightSide.slideDown();

					underlineCurrentVersionBtn.apply(this);

					components.overlays.disable(tab.$templateCSSEditArea);
				})
				.always(() => {
					switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, false);
				})
		}

		function onSaveButtonClick() {
			const templateName = tab.$templatesSelect.getSelectedValue()
			const css = tab.$templateCSSEditArea.val();
			switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, true);
			templatesCSSRestApi.replace(templateName, css ? css : " ")
				.done(() => {
					//nothing
				})
				.fail((response) => {
					if (response.status === 400)
						modal.buildErrorWindow(texts.errors.EQUALS_WORKING_VERSION);
				})
				.always(() => {
					switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, false);
				})
		}

		function onPublishButtonClick() {
			switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, true);
			templatesCSSRestApi.publish(tab.$templatesSelect.getSelectedValue())
				.done(() => {
					//nothing
				})
				.fail((response) => {
					if (response.status === 400) {
						modal.buildErrorWindow(texts.errors.SAVE_FIRST);
					}
				})
				.always(() => {
					switchLoadingAnimation(tab.$templateCSSEditArea, tab.$loadingAnimationWrapEditArea, false);
				})
		}

		function onClearBtnClick() {
			tab.$templateCSSEditArea.val('');
		}

		function buildTemplatesCSSHistory() {
			return new BEM({
				block: "imcms-pop-up-modal",
				elements: {
					"head": templatesCSSHistoryWindowBuilder.buildHead(texts.history.headText),
					"left-side": tab.$templatesCSShistoryListContainer = buildTemplatesCSSHistoryListContainer(),
					"right-side": buildHistoryView(),
					"footer": buildFooter()
				}
			}).buildBlockStructure("<div>", {"class": "template-css-history"});
		}

		function buildTemplatesCSSHistoryListContainer() {
			return $("<div>", {"class": "imcms-left-side template-css-history-list"});
		}

		function buildHistoryView() {
			const $loadingAnimationWrapHistory = $("<div>").append($("<div class='loading-animation'>"));

			tab.$loadingAnimationWrapHistory = $loadingAnimationWrapHistory;

			return new BEM({
				block: "imcms-right-side",
				elements: {
					"templates-css-history-view": tab.$templatesCSSHistoryView = $("<pre>"),
					"loading-animation": $loadingAnimationWrapHistory
				}
			}).buildBlockStructure("<div>");
		}

		function buildFooter() {
			return WindowBuilder.buildFooter([
				components.buttons.negativeButton({
					text: texts.history.closeBtnText,
					"class": "imcms-templates-css-history-cancel",
					click: closeTemplateCSSHistoryWindow
				}),
				components.buttons.saveButton({
					text: texts.history.useBtnText,
					click: onWriteToTemplateCSSEditArea
				})
			]);
		}

		function onWriteToTemplateCSSEditArea() {
			tab.$templateCSSEditArea.val(tab.$templatesCSSHistoryView.text());
			tab.$controls.$rightSide.show();
			closeTemplateCSSHistoryWindow();
		}

		function loadData() {
			switchLoadingAnimation(tab.$templatesCSSHistoryView, tab.$loadingAnimationWrapHistory, true);

			templatesCSSRestApi.getHistory(tab.$templatesSelect.getSelectedValue())
				.done(historyEntries => {
					const dateToTemplateCSSHistoryUnits = {};

					historyEntries.forEach(historyEntry => {
						const date = historyEntry.modified.date;
						dateToTemplateCSSHistoryUnits[date] = (dateToTemplateCSSHistoryUnits[date] || []);
						dateToTemplateCSSHistoryUnits[date].push(historyEntry);
					});

					$.each(dateToTemplateCSSHistoryUnits, buildTemplateCSSHistoryUnit);
				})
				.always(() => {
					switchLoadingAnimation(tab.$templatesCSSHistoryView, tab.$loadingAnimationWrapHistory, false);
				})
		}

		function buildTemplateCSSHistoryUnit(date, templateHistoriesDate) {
			const elements = [{"date": buildTemplateCSSHistoriesDateContainer(date)}];

			new BEM({
				block: "template-css-history-date-unit",
				elements: elements.concat(buildTemplateCSSHistoriesDate(templateHistoriesDate))
			}).buildBlockStructure("<div>").appendTo(tab.$templatesCSShistoryListContainer);
		}

		function buildTemplateCSSHistoriesDateContainer(date) {
			const $date = $("<div>", {
				"class": "template-css-history-date",
				text: date
			});

			const $separator = $("<div>", {"class": "template-css-history-date-separator"});

			return new BEM({
				block: "template-css-history-date-container",
				elements: {
					"date": $date,
					"separator": $separator
				}
			}).buildBlockStructure("<div>");
		}

		function buildTemplateCSSHistoriesDate(templateHistoriesDate) {
			return templateHistoriesDate.map(function (templateCSSHistory) {
				const $textHistoryUnit = $("<div>", {
					"class": "template-css-history-unit",
					text: templateCSSHistory.modified.time + " | " + templateCSSHistory.modified.by,
					click: function () {
						onTemplateCSSHistoryUnitClick(templateCSSHistory.revision);
					}
				});

				return {"unit": $textHistoryUnit};
			});
		}

		function onTemplateCSSHistoryUnitClick(revision) {
			const templateName = tab.$templatesSelect.getSelectedValue();

			switchLoadingAnimation(tab.$templatesCSSHistoryView, tab.$loadingAnimationWrapHistory, true);

			templatesCSSRestApi.getRevision(templateName, revision)
				.done(data => {
					tab.$templatesCSSHistoryView.text(data);
				})
				.always(() => {
					switchLoadingAnimation(tab.$templatesCSSHistoryView, tab.$loadingAnimationWrapHistory, false);
				})
		}

		function clearData() {

		}

		function closeTemplateCSSHistoryWindow() {
			templatesCSSHistoryWindowBuilder.closeWindow()
		}

		function underlineCurrentVersionBtn() {
			const $this = $(this);
			const $currentActiveVersionBtn = tab.$controls.find(".active");

			if ($this !== $currentActiveVersionBtn) {
				$currentActiveVersionBtn.toggleClass("active");
				$this.toggleClass("active");
			}
		}

		function switchLoadingAnimation($area, $loadingAnimation, enable) {
			if (enable) {
				$area.css('opacity', '50%');
				$loadingAnimation.show();
			} else {
				$area.css('opacity', '100%');
				$loadingAnimation.hide();
			}
		}

		let templatesCSSHistoryWindowBuilder = new WindowBuilder({
			factory: buildTemplatesCSSHistory,
			loadDataStrategy: loadData,
			clearDataStrategy: clearData,
			onEscKeyPressed: "close",
			onEnterKeyPressed: onWriteToTemplateCSSEditArea
		});

		const TemplateCSSAdminTab = function (name, tabElements) {
			SuperAdminTab.call(this, name, tabElements);
		};

		TemplateCSSAdminTab.prototype = Object.create(SuperAdminTab.prototype);

		TemplateCSSAdminTab.prototype.getDocLink = () => texts.documentationLink;

		return new TemplateCSSAdminTab(texts.name, [
			buildTabTitle(),
			buildTemplatesSelectContainer(),
			buildTemplateCSSEditAreaContainer(),
			buildControls()
		]);
	}
)
