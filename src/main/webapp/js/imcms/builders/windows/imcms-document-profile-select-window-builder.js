/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
define(
    "imcms-document-profile-select-window-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-i18n-texts", "imcms-components-builder",
        "imcms-profiles-rest-api", "imcms-document-validation-rest-api", "imcms", "imcms-modal-window-builder"
    ],
    function (WindowBuilder, BEM, texts, components, profilesRestApi, documentValidationAPI, imcms, modal) {

        texts = texts.editors.newDocumentProfile;

        let radioButtonsGroup, $parentDocIdInput, $profilesSelect, $profileSelectBlock, $parentSelect,
            $validationErrorBlock;

        function buildProfileSelect() {
            const $profilesSelectContainer = components.selects.selectContainer("<div>", {
                id: "doc-profiles",
                text: texts.selectProfile,
                name: "profile",
                emptySelect: true
            });

            $profilesSelect = $profilesSelectContainer.getSelect();

            return new BEM({
                block: "imcms-profile-select",
                elements: {
                    "select": $profilesSelectContainer
                }
            }).buildBlockStructure("<div>", {"style": "display: none;"});
        }

        function buildParentSelect() {
            $parentDocIdInput = components.texts.textBox("<div>", {
                name: "title",
                text: texts.selectParent
            });

            return new BEM({
                block: "imcms-parent-select",
                elements: {
                    "text-box": $parentDocIdInput
                }
            }).buildBlockStructure("<div>", {"style": "display: none;"});
        }

        let $currentDocIdOption, $profilesOption;

        function buildChoosingRadio($profileSelect, $parentSelect) {
            const $choosingTitle = components.texts.titleText("<div>", texts.chooseProfileOrParent);

            $profilesOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByProfile,
                name: "select-profile-or-doc-or-current_doc",
                value: "profile",
                click: () => {
                    $validationErrorBlock.slideUp(400);
                    $parentSelect.slideUp(400);
                    $profileSelect.slideDown(400);
                }
            }).css("display", "none");

            const $docIdOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByParent,
                name: "select-profile-or-doc-or-current_doc",
                value: "docId",
                checked: true,
                click: () => {
                    $validationErrorBlock.slideUp(400);
                    $parentSelect.slideDown(400);
                    $profileSelect.slideUp(400);
                }
            });

            $currentDocIdOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByCurrentDocId,
                name: "select-profile-or-doc-or-current_doc",
                value: "currentDocId",
                click: () => {
                    $validationErrorBlock.slideUp(400);
                    $parentSelect.slideUp(400);
                    $profileSelect.slideUp(400);
                }
            }).css("display", "none");

            radioButtonsGroup = components.radios.group($profilesOption, $docIdOption, $currentDocIdOption);

            return new BEM({
                block: "new-doc-parent-options",
                elements: {
                    "title": $choosingTitle,
                    "by-current-doc-id": $currentDocIdOption,
                    "by-profile": $profilesOption,
                    "by-doc-id": $docIdOption
                }
            }).buildBlockStructure("<div>");
        }

        function buildValidationErrorBlock() {
            return components.texts.errorText("<div>", texts.validationErrorMessage, {"style": "display: none;"});
        }

        function buildBody() {
            $profileSelectBlock = buildProfileSelect();
            $parentSelect = buildParentSelect();
            $validationErrorBlock = buildValidationErrorBlock();
            const $radioBlock = buildChoosingRadio($profileSelectBlock, $parentSelect);

            return new BEM({
                block: "imcms-document-profile",
                elements: {
                    "choose-the-way": $radioBlock,
                    "profile-select": $profileSelectBlock,
                    "parent-select": $parentSelect,
                    "validation-error": $validationErrorBlock
                }
            }).buildBlockStructure("<div>");
        }

        function onSubmit() {
            const checkedValue = radioButtonsGroup.getCheckedValue();

            if ("docId" === checkedValue) {
                const selectedParentDoc = $parentDocIdInput.getValue().trim();

                if (selectedParentDoc) {
                    documentValidationAPI.checkIsTextDocument(selectedParentDoc)
                        .done(isTextDoc => {
                            if (isTextDoc) {
                                closeWindow();
                                onProfileOrParentSelectedCallback(selectedParentDoc);
                            } else {
                                $validationErrorBlock.slideDown(400);
                            }
                        })
                        .fail(() => $validationErrorBlock.slideDown(400));
                } else {
                    $validationErrorBlock.slideDown(400);
                }
            } else if ("profile" === checkedValue) {
                const parentDocId = $profilesSelect.getSelectedValue();

                if (parentDocId) {
                    closeWindow();
                    onProfileOrParentSelectedCallback(parentDocId);

                } else {
                    $validationErrorBlock.slideDown(400);
                }
            } else if ("currentDocId" === checkedValue) {
                closeWindow();
                onProfileOrParentSelectedCallback(imcms.document.id);
            }
        }

        function buildFooter() {
            return WindowBuilder.buildFooter([
                components.buttons.positiveButton({
                    text: texts.createDocButton,
                    click: onSubmit
                })
            ]);
        }

        function buildProfileSelectWindow() {
            return new BEM({
                block: "imcms-document-profile-select-window",
                elements: {
                    "head": windowBuilder.buildHead(texts.title),
                    "body": buildBody(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>");
        }

        function clear() {
            radioButtonsGroup.setCheckedValue("docId");
            $parentSelect.css("display", "none");
            $profileSelectBlock.css("display", "none");
            $parentDocIdInput.setValue('');
            $profilesSelect.selectFirst();
            $validationErrorBlock.css("display", "none");
        }

        function loadData(onParentSelected, config) {
            profilesRestApi.read()
                .done(profiles => {
                    const profilesDataMapped = profiles.map(profile => {
                        /** @namespace profile.documentName */
                        return {
                            text: profile.name,
                            "data-value": profile.documentName
                        };
                    });

                    let profilesExist = profilesDataMapped && profilesDataMapped.length;

                    if (profilesExist) {
                        components.selects.addOptionsToSelect(profilesDataMapped, $profilesSelect);
                        $profilesOption && $profilesOption.css("display", "block");
                        profilesExist = true;
                    }

                    let isInMenu = config && config.inMenu;
                    const cssDisplayValue = isInMenu ? "block" : "none";
                    $currentDocIdOption.css({"display": cssDisplayValue});

                    const checkedValue = isInMenu
                        ? "currentDocId"
                        : profilesExist ? "profile" : "docId";

                    radioButtonsGroup.setCheckedValue(checkedValue);

                    if (!isInMenu) {
                        if (profilesExist) {
                            $profileSelectBlock.css("display", "block");
                            $profilesOption.css("display", "block");

                        } else {
                            $parentSelect.css("display", "block");
                        }
                    }
                })
                .fail(() => modal.buildErrorWindow(texts.error.loadProfilesFailed));
        }

        function closeWindow() {
            windowBuilder.closeWindow();
        }

        var windowBuilder = new WindowBuilder({
            factory: buildProfileSelectWindow,
            loadDataStrategy: loadData,
            clearDataStrategy: clear,
            onEscKeyPressed: closeWindow,
            onEnterKeyPressed: onSubmit
        });

        var onProfileOrParentSelectedCallback;

        return {
            build: function (onParentSelected, config) {
                onProfileOrParentSelectedCallback = onParentSelected;
                windowBuilder.buildWindowWithShadow.apply(windowBuilder, arguments);
            }
        };
    }
);
