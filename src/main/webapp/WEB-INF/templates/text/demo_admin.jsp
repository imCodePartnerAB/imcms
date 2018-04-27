<%@ page

        import="com.imcode.imcms.api.ContentManagementSystem, com.imcode.imcms.api.TextDocumentViewing"

        contentType="text/html;charset=UTF-8"
        pageEncoding="UTF-8"

%>
<%@ taglib prefix="imcms" uri="imcms" %>
<%

    /*
    TODO: Add a "pin" icon to imCMS's panel. Make it remember in/out state.

    - Maybe 3 way toggle:
        1. Normal (As now auto in/out)
        2. Out (As now but remembered out with cookie)
        3. Sticky (Not fixed. Scrolls with the page. Remembered with cookie)
    */

    boolean hasAdminRights = ContentManagementSystem.fromRequest(request).getCurrentUser().canEdit(TextDocumentViewing.fromRequest(request).getTextDocument());

%><!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Imcms Demo Page</title>
    <script type="text/javascript" id="jqForSite"
            src="//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>


    <%-- TODO: Add logics to init script's load feature. Now with its own jQuery in a timeout to wait for panel to load: --%>
    <script>
        var loadImcmsSpecialAdmin = function ($) {
            var $imcmsAdminSpecial = $('#imcmsAdminSpecial');
            if ($imcmsAdminSpecial.length) {
                var $imcmsAdminPanelOuter = $('.imcms-admin:first');
                var $imcmsAdminPanelInner = $imcmsAdminPanelOuter.find('.imcms-admin-panel:first');
                $imcmsAdminPanelInner.attr('id', 'imcmsAdminPanel');
                //console.clear();
                $imcmsAdminSpecial.appendTo($imcmsAdminPanelOuter);
                addLinkToSpecialAdmin($, $imcmsAdminSpecial);
                <%-- TODO: Make a call to a specified function. If exists/defined. --%>
                try {
                    var specialAdmInitFn = eval('imcmsSpecialAdminInit') || function () {
                    };
                    if (specialAdmInitFn && $.isFunction(specialAdmInitFn)) {
                        specialAdmInitFn($);
                    }
                } catch (ignore) {
                }
                <%-- TODO: Add the right height to the admin panel slideToggle feature (now 90px)! --%>
            }
        };

        var addLinkToSpecialAdmin = function ($, $el) {
            var linkText = $el.data('link-text') || 'Special';
            var $link = $('<li title="Shows client specific administration" class="imcms-panel__item imcms-panel__item--specific">' + linkText + '</li>')
                .insertAfter('.imcms-admin-panel .imcms-panel__item--page-info:first');
            <%-- TODO: Collapsible click event - Re-code so it uses the same slide toggle type as the panel. Add cookie to remember in/out state: --%>
            $link.on('click', function () {
                var $collapsible = $('#imcmsAdminSpecial.imcms-collapsible');
                if ($collapsible.hasClass('imcms-collapsible-hidden')) {
                    $collapsible.removeClass('imcms-collapsible-hidden');
                    window.setTimeout(function () {
                        $('body').css('top', $('.imcms-admin:first').height() + 'px');
                        <%-- TODO: A better solution to know the height! --%>
                        $link.addClass('imcms-panel__item--active');
                    }, 300);
                } else {
                    $collapsible.addClass('imcms-collapsible-hidden');
                    $link.removeClass('imcms-panel__item--active');
                    window.setTimeout(function () {
                        $('body').css('top', '90px');
                        <%-- TODO: A better solution to know the height! --%>
                    }, 300);
                }
            });
        };

        jQuery(document).ready(function ($) {
            window.setTimeout(function () {
                loadImcmsSpecialAdmin($);
                <%-- TODO: Move logics to init script's load feature. Now with its own jQuery in a timeout to wait for panel to load! --%>
            }, 4000);
        });
    </script>

    <%-- TODO: imCMS specific CSS (in imCMS' CSS): --%>
    <style>
        .imcms-panel__item--specific {
            background-image: url('${contextPath}/images_new/admin_panel/icon_edit.png');
        <%-- TODO: Other icon! Maybe 'gear'? Defaulttext? --%>
        }

        #imcmsAdminPanel {
            position: relative;
            z-index: 1502;
            border-bottom: 1px solid #ccc !important;
            -webkit-box-shadow: 0 6px 14px 0 rgba(0, 0, 0, 0.25);
            -moz-box-shadow: 0 6px 14px 0 rgba(0, 0, 0, 0.25);
            box-shadow: 0 6px 14px 0 rgba(0, 0, 0, 0.25);
        }

        #imcmsAdminSpecial {
            position: relative;
            z-index: 1501;
            overflow: hidden;
            border-bottom: 1px solid #ccc;
            background-color: #fff;
            -webkit-box-shadow: 0 6px 14px 0 rgba(0, 0, 0, 0.25), inset 0 -23px 30px 0 rgba(0, 0, 0, 0.04);
            -moz-box-shadow: 0 6px 14px 0 rgba(0, 0, 0, 0.25), inset 0 -23px 30px 0 rgba(0, 0, 0, 0.04);
            box-shadow: 0 6px 14px 0 rgba(0, 0, 0, 0.25), inset 0 -23px 30px 0 rgba(0, 0, 0, 0.04);
        }

        #imcmsAdminSpecial.imcms-collapsible {
            max-height: 10000px;
            border-bottom: 1px solid #ccc;
            -webkit-transition: all .3s;
            -moz-transition: all .3s;
            -ms-transition: all .3s;
            -o-transition: all .3s;
            transition: all .3s;
        }

        #imcmsAdminSpecial.imcms-collapsible.imcms-collapsible-hidden {
            padding-bottom: 0;
            max-height: 0;
            border-bottom: 0;
            -webkit-box-shadow: none;
            -moz-box-shadow: none;
            box-shadow: none;
        }

        #imcmsAdminSpecialInner {
            max-width: 1000px;
            margin: 0 auto;
            padding: 0;
        }
    </style>


    <%-- TODO: Client specific JS (Init. Not in imCMS' JS): --%>
    <script>
        var imcmsSpecialAdminInit = function ($) {<%-- TODO: Standardized function name. Called by imCMS on init. --%>
            addEventsToSpecialAdmin($);
        };

        var addEventsToSpecialAdmin = function ($) {
            var $imcmsAdminSpecial = $('#imcmsAdminSpecial');
            var $tabs = $imcmsAdminSpecial.find('#imcmsAdminTabs li');
            var $divs = $imcmsAdminSpecial.find('.imcms-admin-tab-div');
            $tabs.on('click', function () {
                var $thisTab = $(this);
                var $thisDiv = $('#imcmsAdminTab_' + $thisTab.data('for'));
                var $otherTabs = $tabs.filter(':visible').not($thisTab);
                var $otherDivs = $divs.filter(':visible').not($thisDiv);
                $otherDivs.fadeOut('fast', function () {
                    $thisDiv.fadeIn('fast', function () {
                        $otherTabs.removeClass('tab-active');
                        $thisTab.addClass('tab-active');
                    });
                });
            });
        };
    </script>
    <%-- TODO: Client specific CSS (Override. Not in imCMS' CSS): --%>
    <style>
        /* *******************************************************************************************
         *         My site                                                                           *
         ******************************************************************************************* */
        body {
            margin: 0;
            padding: 20px;
            font: 12px Verdana, sans-serif;
            color: #333;
            background-color: #faefe4;
        }

        h1 {
            margin: 1em 0 .3em 0;
            font: bold 36px Arial, sans-serif;
        }

        #outer {
            width: 80%;
            min-width: 300px;
            max-width: 1200px;
            margin: 20px auto;
            padding: 20px;
            border: 2px solid #ccc;
            -moz-border-radius: 10px;
            -webkit-border-radius: 10px;
            -khtml-border-radius: 10px;
            -o-border-radius: 10px;
            border-radius: 10px;
            background-color: #fff;
            -webkit-box-shadow: 6px 6px 15px 0 rgba(0, 0, 0, 0.5);
            -moz-box-shadow: 6px 6px 15px 0 rgba(0, 0, 0, 0.5);
            box-shadow: 6px 6px 15px 0 rgba(0, 0, 0, 0.5);
        }

        #outer img {
            width: 100%;
            max-width: 100%;
            height: auto;
        }

        <% if (hasAdminRights) { %>
        /* *******************************************************************************************
         *         My admin                                                                          *
         ******************************************************************************************* */

        #imcmsAdminSpecialInner {
            width: 80%;
            min-width: 300px;
            max-width: 1200px;
            margin: 0 auto;
            padding: 0;
        }

        #imcmsAdminSpecial fieldset {
            position: relative;
            margin: 20px 0;
            padding: 20px;
            background-color: #F3F5F7;
            border: 1px solid #389ECF;
            font: normal 13px/15px 'Source Sans Pro', sans-serif;
        }

        #imcmsAdminSpecial fieldset legend {
            display: inline-block;
            width: auto;
            margin: 0 5px 0 0;
            padding: 5px 10px !important;
            background-color: #389ECF !important;
            color: #fff !important;
            font: 700 14px/16px 'Open Sans', Arial, sans-serif;
            -moz-border-radius: 3px;
            -webkit-border-radius: 3px;
            -khtml-border-radius: 3px;
            -o-border-radius: 3px;
            border-radius: 3px;
        }

        ul#imcmsAdminTabs {
            list-style-type: none;
            display: block;
            margin: 10px 0 0 0;
            padding: 0;
        }

        ul#imcmsAdminTabs li {
            display: inline-block;
            margin: 0 1px 3px 0;
            padding: 5px 10px 3px 10px !important;
            border-bottom: 2px solid #ccc;
            background-color: #fff !important;
            color: #000 !important;
            font: 600 13px/15px 'Source Sans Pro', sans-serif;
            text-transform: uppercase;
            cursor: pointer;
        }

        ul#imcmsAdminTabs li.tab-active,
        ul#imcmsAdminTabs li:hover {
            color: #389ECF !important;
            border-color: #389ECF;
        }

        .imcms-admin-tab-div {
            display: none;
            clear: both;
        }

        .imcms-admin-tab-div.tab-active {
            display: block;
        }

        <% } %>
    </style>

</head>
<body>

<%-- TODO: Server-side controlled div for special administration (Not in imCMS): --%>
<% if (hasAdminRights) { %>
<div id="imcmsAdminSpecial" data-link-text="Site specific" class="imcms-collapsible imcms-collapsible-hidden">
    <div id="imcmsAdminSpecialInner">
        <%-- TODO: Whatever admin content needed for this client: --%>
        <ul id="imcmsAdminTabs">
            <li data-for="page" class="tab-active">For this page</li>
            <li data-for="section">For this section</li>
            <li data-for="widgets">Widgets</li>
            <li data-for="general">General</li>
        </ul>
        <div id="imcmsAdminTab_page" class="imcms-admin-tab-div tab-active">
            <fieldset>
                <legend>Page</legend>
                Page admin content...
            </fieldset>
        </div>
        <div id="imcmsAdminTab_section" class="imcms-admin-tab-div">
            <fieldset>
                <legend>Section</legend>
                Section admin content...
            </fieldset>
        </div>
        <div id="imcmsAdminTab_widgets" class="imcms-admin-tab-div">
            <fieldset>
                <legend>Widgets</legend>
                Widgets admin content...
            </fieldset>
        </div>
        <div id="imcmsAdminTab_general" class="imcms-admin-tab-div">
            <fieldset>
                <legend>General</legend>
                General admin content...
            </fieldset>
        </div>
        <%-- / TODO: Whatever admin content needed for this client: --%>
    </div>
</div>
<% } %>
<%-- TODO: / Server-side controlled div for special administration: --%>

<imcms:admin/>

<div id="outer">
    <%--<img src="http://lorempixel.com/1200/300/" alt="">--%>
    <h1>Client's content...</h1>
    <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
        dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
        suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
    <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
        feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
        delenit augue duis dolore te feugait nulla facilisi.</p>
</div>


</body>
</html>