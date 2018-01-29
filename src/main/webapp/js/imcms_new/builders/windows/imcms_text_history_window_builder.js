/**
 * Text history window builder in text editor.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.01.18
 */
Imcms.define("imcms-text-history-window-builder",
    ["imcms-window-builder", "imcms-bem-builder", "imcms-components-builder", "jquery"],
    function (WindowBuilder, BEM, components, $) {

        var textData;

        function onWriteToTextField() {
            console.log(textData);
            textHistoryWindowBuilder.closeWindow();
        }

        function onCancel() {
            textHistoryWindowBuilder.closeWindow();
        }

        function buildFooter() {
            return textHistoryWindowBuilder.buildFooter([
                components.buttons.negativeButton({
                    text: "Cancel",
                    click: onCancel
                }),
                components.buttons.saveButton({
                    text: "Write to text field",
                    click: onWriteToTextField
                })
            ]);
        }

        function buildHistoryView() {
            return $("<div>", {"class": "imcms-right-side text-history-view"});
        }

        function buildHistoryListContainer() {
            return $("<div>", {"class": "imcms-left-side text-history-list"});
        }

        function buildTextHistory() {
            return new BEM({
                block: "imcms-pop-up-modal",
                elements: {
                    "head": textHistoryWindowBuilder.buildHead("Text history"),
                    "left-side": buildHistoryListContainer(),
                    "right-side": buildHistoryView(),
                    "footer": buildFooter()
                }
            }).buildBlockStructure("<div>", {"class": "text-history"});
        }

        function loadData(textDTO) {
            textData = textDTO;
        }

        function clearData() {

        }

        var textHistoryWindowBuilder = new WindowBuilder({
            factory: buildTextHistory,
            loadDataStrategy: loadData,
            clearDataStrategy: clearData
        });

        return {
            buildTextHistory: function (textData) {
                textHistoryWindowBuilder.buildWindowWithShadow.applyAsync(arguments, textHistoryWindowBuilder);
            }
        };
    }
);
