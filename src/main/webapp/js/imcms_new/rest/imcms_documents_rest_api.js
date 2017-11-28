Imcms.define("imcms-documents-rest-api", ["imcms-rest-api"], function (rest) {

    var api = new rest.API("/documents");

    api.remove = function (documentId) {
        return {
            done: function (callback) {
                var responses = [200, 500],
                    responseCode = responses[Math.floor(Math.random() * responses.length)];
                if (responseCode === 200) {
                    console.log("%c Document " + documentId + " was removed (not really)", "color: blue;");
                } else {
                    console.log("%c Document " + documentId + " wasn't removed due to some mock circumstances", "color: red;");

                }
                callback(responseCode);
            }
        }
    };

    return api;
});
