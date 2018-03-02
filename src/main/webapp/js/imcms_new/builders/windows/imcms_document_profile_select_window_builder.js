/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.define(
    "imcms-document-profile-select-window-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-i18n-texts", "imcms-components-builder",
        "imcms-profiles-rest-api", "imcms-document-validation-rest-api", "imcms"
    ],
    function (WindowBuilder, BEM, texts, components, profilesRestApi, documentValidationAPI, imcms) {

        texts = texts.editors.newDocumentProfile;

        var radioButtonsGroup, $parentDocIdInput, $profilesSelect, $profileSelectBlock, $parentSelect,
            $validationErrorBlock;

        function loadProfiles($profilesSelect) {
            profilesRestApi.read().done(function (profiles) {
                var profilesDataMapped = profiles.map(function (profile) {
                    /** @namespace profile.documentName */
                    return {
                        text: profile.name,
                        "data-value": profile.documentName
                    }
                });

                components.selects.addOptionsToSelect(profilesDataMapped, $profilesSelect);
            });
        }

        function buildProfileSelect() {
            var $profilesSelectContainer = components.selects.selectContainer("<div>", {
                id: "doc-profiles",
                text: texts.selectProfile,
                name: "profile",
                emptySelect: true
            });

            $profilesSelect = $profilesSelectContainer.getSelect();

            loadProfiles($profilesSelect);

            return new BEM({
                block: "imcms-profile-select",
                elements: {
                    "select": $profilesSelectContainer
                }
            }).buildBlockStructure("<div>");
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

        var $currentDocIdOption;

        function buildChoosingRadio($profileSelect, $parentSelect) {
            var $choosingTitle = components.texts.titleText("<div>", texts.chooseProfileOrParent);

            var $profilesOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByProfile,
                name: "select-profile-or-doc-or-current_doc",
                value: "profile",
                // checked: true,
                click: function () {
                    $validationErrorBlock.slideUp(400);
                    $parentSelect.slideUp(400);
                    $profileSelect.slideDown(400);
                }
            });

            var $docIdOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByParent,
                name: "select-profile-or-doc-or-current_doc",
                value: "docId",
                click: function () {
                    $validationErrorBlock.slideUp(400);
                    $parentSelect.slideDown(400);
                    $profileSelect.slideUp(400);
                }
            });

            $currentDocIdOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByCurrentDocId,
                name: "select-profile-or-doc-or-current_doc",
                value: "currentDocId",
                click: function () {
                    $validationErrorBlock.slideUp(400);
                    $parentSelect.slideUp(400);
                    $profileSelect.slideUp(400);
                }
            });

            radioButtonsGroup = components.radios.group($profilesOption, $docIdOption, $currentDocIdOption);

            return new BEM({
                block: "new-doc-parent-options",
                elements: {
                    "title": $choosingTitle,
                    "by-profile": $profilesOption,
                    "by-doc-id": $docIdOption,
                    "by-current-doc-id": $currentDocIdOption
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
            var $radioBlock = buildChoosingRadio($profileSelectBlock, $parentSelect);

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
            var checkedValue = radioButtonsGroup.getCheckedValue();

            if ("docId" === checkedValue) {
                var selectedParentDoc = $parentDocIdInput.getValue().trim();

                if (selectedParentDoc) {
                    documentValidationAPI.checkIsTextDocument(selectedParentDoc).success(function (isTextDoc) {
                        if (isTextDoc) {
                            windowBuilder.closeWindow();
                            onProfileOrParentSelectedCallback(selectedParentDoc);

                        } else {
                            $validationErrorBlock.slideDown(400);
                        }
                    });
                } else {
                    $validationErrorBlock.slideDown(400);
                }
            } else if ("profile" === checkedValue) {
                var parentDocId = $profilesSelect.getSelectedValue();

                if (parentDocId) {
                    windowBuilder.closeWindow();
                    onProfileOrParentSelectedCallback(parentDocId);

                } else {
                    $validationErrorBlock.slideDown(400);
                }
            } else if ("currentDocId" === checkedValue) {
                windowBuilder.closeWindow();
                onProfileOrParentSelectedCallback(imcms.document.id);
            }
        }

        function buildFooter() {
            return windowBuilder.buildFooter([
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
            radioButtonsGroup.setCheckedValue("profile");
            $parentSelect.css("display", "none");
            $profileSelectBlock.css("display", "block");
            $parentDocIdInput.setValue('');
            $profilesSelect.selectFirst();
            $validationErrorBlock.css("display", "none");
        }

        function loadData(onParentSelected, config) {
            var cssDisplayValue = (config && config.inMenu) ? "block" : "none";
            $currentDocIdOption.css({"display":cssDisplayValue});
        }

        var windowBuilder = new WindowBuilder({
            factory: buildProfileSelectWindow,
            loadDataStrategy: loadData,
            clearDataStrategy: clear
        });

        var onProfileOrParentSelectedCallback;

        return {
            build: function (onParentSelected, config) {
                onProfileOrParentSelectedCallback = onParentSelected;
                windowBuilder.buildWindowWithShadow.applyAsync(arguments, windowBuilder);
            }
        };
    }
);
