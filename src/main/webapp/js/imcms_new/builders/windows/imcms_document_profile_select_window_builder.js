/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.define(
    "imcms-document-profile-select-window-builder",
    [
        "imcms-window-builder", "imcms-bem-builder", "imcms-i18n-texts", "imcms-components-builder",
        "imcms-profiles-rest-api"
    ],
    function (WindowBuilder, BEM, texts, components, profilesRestApi) {

        texts = texts.editors.newDocumentProfile;

        var $radioButtonsGroup, $parentSelect, $profilesSelect;

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
            var $parentDocIdInput = components.texts.textBox("<div>", {
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

        function buildChoosingRadio($profileSelect, $parentSelect) {
            var $choosingTitle = components.texts.titleText("<div>", texts.chooseProfileOrParent);

            var $profilesOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByProfile,
                name: "select-profile-or-doc",
                value: "profile",
                checked: true,
                click: function () {
                    $parentSelect.slideUp(400);
                    $profileSelect.slideDown(400);
                }
            });

            var $docIdOption = components.radios.imcmsRadio("<div>", {
                text: texts.buildByParent,
                name: "select-profile-or-doc",
                value: "docId",
                click: function () {
                    $parentSelect.slideDown(400);
                    $profileSelect.slideUp(400);
                }
            });

            $radioButtonsGroup = components.radios.group($profilesOption, $docIdOption);

            return new BEM({
                block: "new-doc-parent-options",
                elements: {
                    "title": $choosingTitle,
                    "by-profile": $profilesOption,
                    "by-doc-id": $docIdOption
                }
            }).buildBlockStructure("<div>");
        }

        function buildBody() {
            var $profileSelect = buildProfileSelect();
            $parentSelect = buildParentSelect();
            var $radioBlock = buildChoosingRadio($profileSelect, $parentSelect);

            return new BEM({
                block: "imcms-document-profile",
                elements: {
                    "choose-the-way": $radioBlock,
                    "profile-select": $profileSelect,
                    "parent-select": $parentSelect
                }
            }).buildBlockStructure("<div>");
        }

        function onSubmit() {
            var checkedValue = $radioButtonsGroup.getCheckedValue();

            if ("docId" === checkedValue) {
                var selectedParentDoc = $parentSelect.val().trim();

                if (selectedParentDoc) {
                    // validation here
                    windowBuilder.closeWindow();
                    onProfileOrParentSelectedCallback(selectedParentDoc, null);
                    return;
                }
            }

            if ("profile" === checkedValue) {
                var selectedValue = $profilesSelect.getSelectedValue();

                if (selectedValue) {
                    var profileId = selectedValue;

                    windowBuilder.closeWindow();
                    onProfileOrParentSelectedCallback(null, profileId);
                }
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

        var windowBuilder = new WindowBuilder({
            factory: buildProfileSelectWindow
        });

        var onProfileOrParentSelectedCallback;

        return {
            build: function (onParentSelected) {
                onProfileOrParentSelectedCallback = onParentSelected;
                windowBuilder.buildWindowWithShadow();
            }
        };
    }
);
