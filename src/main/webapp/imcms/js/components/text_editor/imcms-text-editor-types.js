/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 15.08.18
 */
define('imcms-text-editor-types', [], function () {
    return {
        text: 'TEXT',
        textFromEditor: 'TEXT_FROM_EDITOR', // needs only on client, means 'TEXT' for server
        html: 'HTML',
        htmlFromEditor: 'HTML_FROM_EDITOR', // needs only on client, means 'HTML' for server
        editor: 'EDITOR'
    }
});
