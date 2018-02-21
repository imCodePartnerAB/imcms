/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.02.18
 */
Imcms.define(
    "imcms-document-profile-select-window-builder",
    ["imcms-window-builder", "imcms-bem-builder", "imcms-i18n-texts", "imcms-components-builder"],
    function (WindowBuilder, BEM, texts, components) {

        texts = texts.editors.newDocumentProfile;

        function buildProfileSelect() {
            var $profilesSelectContainer = components.selects.selectContainer("<div>", {
                id: "doc-profiles",
                text: texts.selectProfile,
                name: "profile",
                emptySelect: true
            });

            // usersRestApi.read(null).done(function (users) {
            //     var usersDataMapped = users.map(function (user) {
            //         return {
            //             text: user.username,
            //             "data-value": user.id
            //         }
            //     });
            //
            //     components.selects.addOptionsToSelect(usersDataMapped, $profilesSelectContainer.getSelect());
            // });

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
            }).buildBlockStructure("<div>");
        }

        function buildBody() {
            return new BEM({
                block: "imcms-document-profile",
                elements: {
                    "profile-select": buildProfileSelect(),
                    "parent-select": buildParentSelect()
                }
            }).buildBlockStructure("<div>");
        }

        function buildFooter(onDocTypeSelected) {
            var parentDocId, profileId;
            return windowBuilder.buildFooter([
                components.buttons.positiveButton({
                    text: texts.buildButton,
                    click: function () {
                        // define stuff
                        onDocTypeSelected(parentDocId, profileId);
                    }
                })
            ])
        }

        function buildProfileSelectWindow(onDocTypeSelected) {
            return new BEM({
                block: "imcms-document-profile-select-window",
                elements: {
                    "head": windowBuilder.buildHead(texts.title),
                    "body": buildBody(),
                    "footer": buildFooter(onDocTypeSelected)
                }
            }).buildBlockStructure("<div>");
        }

        var windowBuilder = new WindowBuilder({
            factory: buildProfileSelectWindow
        });

        return {
            build: function (onParentSelected) {
                windowBuilder.buildWindowWithShadow(onParentSelected);
            }
        };
    }
);
