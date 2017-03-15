<%@ page import="imcode.server.Imcms"%><%@
    page contentType="text/javascript" pageEncoding="UTF-8"
%>

        /* *******************************************************************************************
         *         Browser sniffer                                                                   *
         ******************************************************************************************* */

    var platf = navigator.platform;
    var ua = navigator.userAgent;

    var hasDocumentLayers = (document.layers);
    var hasDocumentAll = (document.all);
    var hasGetElementById = (document.getElementById);

    var isGecko = inStr(ua, "Gecko");
    var isOpera = inStr(ua, "Opera");
    var isSafari = inStr(ua, "Safari");
    var isWindows = inStr(platf, "Win32");
    var isMac = inStr(platf, "Mac");

    var isIE55 = (isWindows && hasDocumentAll && hasGetElementById && /MSIE \d+/.test(ua) && !isOpera);

    function inStr(str, val, cas) {
        var ret;
        if (cas) { /* Case sensitive */
            ret = (str.indexOf(val) != -1);
        } else { /* Not Case sensitive */
            str = str.toUpperCase();
            val = val.toUpperCase();
            ret = (str.indexOf(val) != -1);
        }
        return ret;
    }

        /* *******************************************************************************************
         *         Functions                                                                         *
         ******************************************************************************************* */

    function singleclicked() {
        if (isMac && isSafari) return true;
        if (!("clicked" in this)) {
            this.clicked = 1;
            return true;
        } else {
            return false;
        }
    }

    function focusField(theFormName, theElementName) {
        var f = (!isNaN(theFormName)) ? eval("document.forms[" + theFormName + "]") : eval("document.forms." + theFormName);
        if (f) {
            var el = eval("f." + theElementName);
            if (el) el.focus();
        }
    }

    function getParam(attrib) {			// get querystring-parameters from document.location
        var sParams = location.search;
        var retVal = "";
        if (sParams.indexOf('?') != -1) {
            var pairs = sParams.substring(1, sParams.length).split('&');
            for (var i = 0; i < pairs.length; i++) {
                var nameVal = pairs[i].split('=');
                if (nameVal[0] == attrib) {
                    retVal = nameVal[1];
                }
            }
        }
        return retVal;
    }

    function onSelectChange(str) {
        document.getElementsByName("hidden")[0].checked = (str.indexOf("(Hidden)") >= 0);
    }

        /* *******************************************************************************************
         *         POPUP functions                                                                   *
         ******************************************************************************************* */

    function openHelpW(helpDocName) {
        <%--window.open("@documentationurl@/Help?name=" + helpDocName + "&lang=$language", "help");--%>
        <%-- IMCMS-94: replaced without arguments since we have new documentation--%>
        <%--window.open('@documentationurl@'); --%>
        <%-- IMCMS-149: replaced with URL from system properties since we do not use velocity tag here --%>
        window.open("<%= Imcms.getServerProperties().getProperty("documentation-host") %>");
    }

    function popWinOpen(winW, winH, sUrl, sName, iResize, iScroll) {
        var winX, winY;
        if (screen) {
            if ((screen.height - winH) < 150) {
                winX = (screen.width - winW) / 2;
                winY = 0;
            } else {
                winX = (screen.width - winW) / 2;
                winY = (screen.height - winH) / 2;
            }
            var popWindow = window.open(sUrl, sName, "resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
            if (popWindow) popWindow.focus();
        } else {
            window.open(sUrl, sName, "resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
        }
    }
