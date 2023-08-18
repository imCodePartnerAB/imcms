${"<!--"}
<%@ page trimDirectiveWhitespaces="true" %>
${"-->"}
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Imcms Demo Page</title>
    <script type="text/javascript" id="jqForSite"
            src="//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>

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

        <imcms:ifAdmin>
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
            padding: 5px 10px;
            background-color: #389ECF;
            color: #fff;
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
            padding: 5px 10px 3px 10px;
            border-bottom: 2px solid #ccc;
            background-color: #fff;
            color: #000;
            font: 600 13px/15px 'Source Sans Pro', sans-serif;
            text-transform: uppercase;
            cursor: pointer;
        }

        ul#imcmsAdminTabs li.tab-active,
        ul#imcmsAdminTabs li:hover {
            color: #389ECF;
            border-color: #389ECF;
        }

        .imcms-admin-tab-div {
            display: none;
            clear: both;
        }

        .imcms-admin-tab-div.tab-active {
            display: block;
        }

        </imcms:ifAdmin>
    </style>

</head>
<body>

<%--@elvariable id="isSuperAdmin" type="boolean"--%>
<%--@elvariable id="accessToAdminPages" type="boolean"--%>
<c:if test="${isSuperAdmin or accessToAdminPages}">
    <div id="imcmsAdminSpecial" data-link-text="Site specific" data-title-text="Site specific title" class="imcms-collapsible imcms-collapsible-hidden">
        <div id="imcmsAdminSpecialInner">
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
        </div>
    </div>
</c:if>

<imcms:admin/>

<script>
    var addEventsToSpecialAdmin = function () {
        var $imcmsAdminSpecial = jQuery('#imcmsAdminSpecial');
        var $tabs = $imcmsAdminSpecial.find('#imcmsAdminTabs li');
        var $divs = $imcmsAdminSpecial.find('.imcms-admin-tab-div');

        $tabs.on('click', function () {
            var $thisTab = jQuery(this);
            var $thisDiv = jQuery('#imcmsAdminTab_' + $thisTab.data('for'));
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

    <imcms:ifAdmin>
    Imcms.initSiteSpecific(addEventsToSpecialAdmin);
    </imcms:ifAdmin>

</script>

<div id="outer">
    <%--<img src="http://lorempixel.com/1200/300/" alt="">--%>
    <h1>Client's content...</h1>
    <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
        dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
        suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
    <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
        feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
        delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
        <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
            dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper
            suscipit lobortis nisl ut aliquip ex ea commodo consequat.</p>
        <p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu
            feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril
            delenit augue duis dolore te feugait nulla facilisi.</p>
</div>


</body>
</html>