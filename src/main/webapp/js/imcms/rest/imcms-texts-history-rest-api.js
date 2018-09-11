/**
 * For texts history in Text Editor
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.01.18
 */
define("imcms-texts-history-rest-api", ["imcms-rest-api"], function (rest) {
    return new rest.API("/texts/history");
});
