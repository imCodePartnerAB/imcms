(function (Imcms) {
    function ajax(request, response) {
        $.ajax({
            url: this.url,
            type: this.type,
            data: request,
            success: response
        });
    }

    /**
     * Simple Imcms REST API Constructor to prevent creating similar code in many places.
     * Automatically applies request and response.
     * Works only if url is constant for each instance.
     *
     * Created by Serhii Maksymchuk from Ubrainians for imCode
     * 15.11.16
     */
    return Imcms.REST = {
        API: function (path) {
            this.post = ajax.bind({url: path, type: "POST"});
            this.get = ajax.bind({url: path, type: "GET"});
            this.put = ajax.bind({url: path, type: "PUT"});
            this.patch = ajax.bind({url: path, type: "PATCH"});
            this["delete"] = ajax.bind({url: path, type: "DELETE"})
        }
    };
})(Imcms);
