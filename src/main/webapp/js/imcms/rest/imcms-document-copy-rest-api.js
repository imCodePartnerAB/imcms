define("imcms-document-copy-rest-api", ["imcms-rest-api"], function (rest) {
    return {
        copy: function (docIdentifier) {
            var url = "/documents/copy/" + docIdentifier;
            return rest.ajax.call({url: url, type: "POST", json: false});
        }
    }
});