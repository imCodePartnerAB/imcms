$(document).ready(function() {

//        $.getJSON( 'http://imcms.dev.imcode.com/ws/doc/1001/labels/json', function(data) {
//            alert('Headline ' + data.headline);
//        });


       // alert('ready');

    //    $('#hideButton').click(function() {
    //        $('#disclaimer').hide();
    //    });

    //$(this).addClass('zebraHover');
    //$(this).removeClass('zebraHover');

    //    $('#com_imcode_imcms_loop').hover(
    //            function() {
    //                $(this).fadeTo('fast', .3, function() {
    //                    alert('menu');
    //
    //                });
    //            },
    //
    //            function() {
    //                $(this).fadeTo('fast', 1);
    //            }
    //     );
    //
    //    $('#navigation a').hover(
    //        function() {
    //            $('#navigation_blob').animate(
    //                {width: $(this).width() + 10, left: $(this).position().left},
    //                {duration: 'slow', easing: 'easeOutElastic', queue: false} );
    //        },
    //
    //        function() {
    //            $('#navigation_blob').stop(true) .animate(
    //                {width: 'hide'}, {duration: 'slow', easing: 'easeOutCirc', queue: false}
    //                ).animate(
    //                    {left: $('#navigation li:first a').position().left},
    //                    'fast' );
    //} );
    //
    //    $('<div id="navigation_blob"></div>').css({ width: $('#navigation li:first a').width() + 10, height: $('#navigation li:first a').height() + 10
    //    }).appendTo('#navigation');

    $('ul.dropdown li.dropdown_trigger').hover(function() {
        // Show subsequent <ul>.
        $(this).find('ul').fadeIn(1);
    }, function() {
        // Hide subsequent <ul>.
        $(this).find('ul').hide();
    });
});

