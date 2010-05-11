$(document).ready(function() {

    $.getJSON( 'http://imcms.dev.imcode.com/ws/doc/1001/labels/json', function(data) {
        alert('Headline ' + data.headline);
    });

});