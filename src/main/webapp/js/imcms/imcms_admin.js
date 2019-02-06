function focusField(theFormName, theElementName) {
    const f = isNaN(theFormName) ? eval("document.forms." + theFormName) : eval("document.forms[" + theFormName + "]");
    if (f) {
        const el = eval("f." + theElementName);
        if (el) el.focus();
    }
}

function getParam(attrib) {			// get querystring-parameters from document.location
    const sParams = location.search;
    let retVal = "";
    if (sParams.indexOf('?') != -1) {
        const pairs = sParams.substring(1, sParams.length).split('&');
        for (let i = 0; i < pairs.length; i++) {
            const nameVal = pairs[i].split('=');
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

function popWinOpen(winW, winH, sUrl, sName, iResize, iScroll) {
    let winX, winY;
    if (screen) {
        if ((screen.height - winH) < 150) {
            winX = (screen.width - winW) / 2;
            winY = 0;
        } else {
            winX = (screen.width - winW) / 2;
            winY = (screen.height - winH) / 2;
        }
        const popWindow = window.open(sUrl, sName, "resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH + ",top=" + winY + ",left=" + winX + "");
        if (popWindow) popWindow.focus();
    } else {
        window.open(sUrl, sName, "resizable=" + iResize + ",menubar=0,scrollbars=" + iScroll + ",width=" + winW + ",height=" + winH);
    }
}
