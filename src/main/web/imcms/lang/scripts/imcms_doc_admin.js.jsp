<%@ page
    
    import="com.imcode.imcms.servlet.AdminPanelServlet,
            imcode.util.ui.BrowserCheck, imcode.server.ImcmsConstants"
    
    contentType="text/javascript"
    pageEncoding="UTF-8"
    
%><%@ taglib prefix="vel" uri="imcmsvelocity" %><%

int metaId = AdminPanelServlet.getIntRequestParameter("meta_id", 0, request) ;
int flags  = AdminPanelServlet.getIntRequestParameter("flags", 0, request) ;

if (0 == flags && null != session.getAttribute("flags")) {
    try {
        flags = (Integer) session.getAttribute("flags") ;
    } catch (Exception e) {}
}

boolean isTemplateMode = (ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE == flags) ;

String cp = request.getContextPath() ;

/* Check browser */

BrowserCheck browser = new BrowserCheck(request) ;

boolean isIE       = browser.isIE() ;
boolean isGecko    = browser.isGecko() ;
boolean isIE55plus = browser.isIE55plus() ;
boolean isIE7plus  = browser.isIE7plus() ;

%>

<jsp:include page="imcms_jquery_1.4.2.js" />

<jsp:include page="imcms_jquery-ui_1.8.5.js" />

var lastActiveTabId = "" ;
var flags = <%= flags %> ;//(document.location.toString().indexOf('servlet/SaveInPage') != -1) ? <%= ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE %> : <%= flags %> ;
var isTemplateMode = (<%= ImcmsConstants.PERM_EDIT_TEXT_DOCUMENT_TEMPLATE + " == flags" %>) ;
var doMakePlace = false ;


jQ(document).ready(function($) {
    
    $.get('<%= AdminPanelServlet.getPathToAjaxHandler(metaId, request) %>',
        {
            get:   'adminPanelHtml',
            flags: flags,
            rnd:   '<%= System.currentTimeMillis() %>'
        }, function(adminPanel) {
            if (null != adminPanel && '' != adminPanel) {
                $('body').prepend(adminPanel) ;
                generateTabsFromButtons($, function() {
                    attachEventsToToolBar($) ;
                }) ;
            }
        }
    ) ;<%--
    $('#adminPanelTd1_2').click(function(event) {
        alert($('#imcmsToolBar').html()) ;
    }) ;--%>
    
}) ;

<%--
/* *******************************************************************************************
 *         Generate tabs from the <button> tags                                              *
 ******************************************************************************************* */
--%>

function generateTabsFromButtons($, completeFn) {
    $(':button.imcmsToolBarTab,:submit.imcmsToolBarTab').each(function() {
        var sId          = $(this).attr('id') ;
        var isActive     = $(this).hasClass('imcmsToolBarTabActive') ;
        var isModeBtn    = $(this).hasClass('imcmsToolBarTabMode') ;
        var isToolTip    = $(this).hasClass('imcmsToolTip') ;
        var isToolTipRev = $(this).hasClass('imcmsToolTipRev') ;
        var title        = (isToolTip || isToolTipRev) ? $(this).attr('title') : '' ;
        $(this).after('<a href="javascript://" id="' + sId + '_tab" class="imcmsToolBarTabLink' +
                      (isActive ? ' imcmsToolBarTabActive' : '') +
                      (isModeBtn ? ' imcmsToolBarTabMode' : '') +
                      (isToolTip ? ' imcmsToolTip' : isToolTipRev ? ' imcmsToolTip' : '') + '"' +
                      ('' != title ? ' title="' + $(this).attr('title') + '"' : '') +
                      '><span><b>' + $(this).html() + '</b></span></a>').hide() ;
        $('#' + sId + '_tab').click(function(event) {
            event.preventDefault() ;
            $('#' + sId).click() ;
        }) ;
    }) ;
    if ($.isFunction(completeFn)) {
        completeFn() ;
    }
}

<%--
/* *******************************************************************************************
 *         Activate one tab                                                                  *
 ******************************************************************************************* */
--%>

function deActivateTab($, id) {
    $('#' + id + '_tab')
        .removeClass('imcmsToolBarTabActive')
        .removeClass('imcmsToolBarTabSubActive') ;
    activateTab($, lastActiveTabId, true) ;
}

function activateTab($, id, deactivateOthers) {
    if (deactivateOthers) {
        $('.imcmsToolBarTabActive,.imcmsToolBarTabSubActive').each(function() {
            if ($(this).hasClass('imcmsToolBarTabMode')) {
                lastActiveTabId = $(this).attr('id').replace(/_tab/, '') ;
            }
            $(this)
                .removeClass('imcmsToolBarTabActive')
                .removeClass('imcmsToolBarTabSubActive') ;
        }) ;
    }
    $('#' + id + '_tab').addClass('imcmsToolBarTabActive') ;
}

<%--
/* *******************************************************************************************
 *         Activate one button                                                               *
 ******************************************************************************************* */
--%>

function deActivateBtn($, id) {
    $('#' + id).removeClass('imcmsToolBarLinkActive') ;
}

function activateBtn($, id, deactivateOthers) {
    if (deactivateOthers) {
        $('.imcmsToolBarLinkActive').each(function() {
            $(this).removeClass('imcmsToolBarLinkActive') ;
        }) ;
    }
    $('#' + id).addClass('imcmsToolBarLinkActive') ;
}

<%--
/* *******************************************************************************************
 *         Attach jQuery events to the ToolBar                                               *
 ******************************************************************************************* */
--%>

function attachEventsToToolBar($) {
    
    if ('true' == $.getCookie('<%= AdminPanelServlet.PARAM_COOKIE_PANEL_HIDE %>')) {
        hideToolBarDirect($) ;
    }
    scrollHandler($, '#imcmsToolBar') ;
    makePlaceHandler($, false, true) ;
    $.toolTip('#imcmsToolBar,#imcmsToolBarHidden') ;
    
    $('#imcmsToolBarMetaTd .langIconDisabled img').fadeTo(0, 0.2) ;
    $('#statusIcon img').fadeTo(0, 0.6) ;
    $('#statusIcon a').hover(
        function() {
            $('#statusIcon img').fadeTo('slow', 1) ;
        }, function() {
            $('#statusIcon img').fadeTo('slow', 0.6) ;
        }
    ) ;
    
    $('#imcmsToolBarHide').click(function(event) {
        event.preventDefault() ;
        setToolBarHeight($, true) ;
        var oToolBarVis = $('#imcmsToolBar') ;
        var oToolBarHid = $('#imcmsToolBarHidden') ;
        //var posTop      = $(oToolBarVis).position().top ;
        var posTop      = parseInt($(oToolBarVis).css('top')) ;
        var isFixed     = (20000000 == $(oToolBarVis).css('z-index')) ;
        $('#adminPanelTd1_2').html('scrollTop:' + posTop + 'px, isFixed: ' + isFixed + ', H:' + $(oToolBarHid).height() + 'px, Top:' + '-' + (posTop + $(oToolBarHid).height()) + 'px, Top +=' + $(oToolBarHid).height() + 'px') ;
        if (!isFixed) {
            $(oToolBarVis).hide(0, function() {
                 $(oToolBarHid).show(0) ;
            }) ;
        } else {
            $(oToolBarVis).animate({'top': '-=' + $(oToolBarVis).height() + 'px'}, 'fast', function() {
                $(this).hide(0) ;
                if (posTop > 0) {
                    $(oToolBarHid).css('top', (posTop - $(oToolBarHid).height()) + 'px') ;
                }
                $(oToolBarHid)
                    .css('top', (posTop - $(oToolBarHid).height()) + 'px')
                    .show(0)
                    .animate({'top': '+=' + $(oToolBarHid).height() + 'px'}, 'slow', function() {
                        scrollHandler($, oToolBarHid) ;
                        makePlaceHandler($, true, false) ;
                        $.setCookie('<%= AdminPanelServlet.PARAM_COOKIE_PANEL_HIDE %>', 'true') ;
                    }) ;
            }) ;
        }
    }) ;
    
    $('#imcmsToolBarShow').click(function(event) {
        event.preventDefault() ;
        var oToolBarVis = $('#imcmsToolBar') ;
        var oToolBarHid = $('#imcmsToolBarHidden') ;
        //var posTop      = $(oToolBarHid).position().top ;
        var posTop      = parseInt($(oToolBarHid).css('top')) ;
        var isFixed     = (20000000 == $(oToolBarHid).css('z-index')) ;
        $('#adminPanelTd1_2').html('scrollTop:' + posTop + 'px, isFixed: ' + isFixed + ', H:' +  $(oToolBarVis).height() + 'px, Top:' + '-' + (posTop + $(oToolBarVis).height()) + 'px, Top +=' + $(oToolBarVis).height() + 'px') ;
        if (!isFixed) {
            $(oToolBarHid).hide(0, function() {
                 $(oToolBarVis).show(0) ;
            }) ;
        } else {
            $(oToolBarHid).animate({'top': '-=' + $(oToolBarHid).height() + 'px'}, 'fast', function() {
                $(this).hide(0) ;
                if (posTop > 0) {
                    $(oToolBarVis).css('top', (posTop - $(oToolBarVis).height()) + 'px') ;
                }
                $(oToolBarVis)
                    .show(0)
                    .animate({'top': '+=' + $(oToolBarVis).height() + 'px'}, 'slow', function() {
                        scrollHandler($, oToolBarVis) ;
                        makePlaceHandler($, false, true) ;
                        setToolBarHeight($, false) ;
                        $.setCookie('<%= AdminPanelServlet.PARAM_COOKIE_PANEL_HIDE %>', 'false') ;
                    }) ;
            }) ;
        }
    }) ;
    
    $('#imcmsToolBarBtnAppearance').live('click', function(event) {
        event.preventDefault() ;
        if ($('#imcmsToolBarSubAppearance').is(':visible')) {
            hideAppearanceSub($) ;
        } else {
            showAppearanceSub($) ;
        }
    }) ;
    if (isTemplateMode) {
        showAppearanceSub($) ;
    }
    
    $('#imcmsToolBarBtnSettings').live('click', function(event) {
        event.preventDefault() ;
        if ($('#imcmsToolBarSubSettings').is(':visible')) {
            hideSubPanel($, null, 'imcmsToolBarBtnSettings') ;
        } else {
            showSubPanel($, null, 'imcmsToolBarBtnSettings', 'imcmsToolBarSubSettings') ;
        }
    }) ;
    
    $('#imcmsToolBarMakePlace').click(function(event) {
        event.preventDefault() ;
        makePlaceHandler($, false, false) ;
    }) ;
    
    /*$('#imcmsToolBarMain .imcmsToolBarShowBtnSub,<%
     %>#imcmsToolBarMain .imcmsToolBarShowBtnDrop').click(function(event) {
        event.preventDefault() ;
        toggleToolBarSub($, this.id) ;
    }) ;*/
    
    $('#imcmsToolBarHide').attr('src', $('#imcmsToolBarHide').attr('src').replace(/_1/, '_0')) ;
    $('#imcmsToolBarHide').hover(
        function() {
            $(this).attr('src', $('#imcmsToolBarHide').attr('src').replace(/_0/, '_1')) ;
        }, function() {
            $(this).attr('src', $('#imcmsToolBarHide').attr('src').replace(/_1/, '_0')) ;
        }
    ) ;/**/
    //alert($('#imcmsToolBar').css('position')) ;
}

<%--
/* *******************************************************************************************
 *         If the ToolBar is hidden - Hide it directly                                       *
 ******************************************************************************************* */
--%>

function hideToolBarDirect($) {
    var oToolBarVis = $('#imcmsToolBar') ;
    var oToolBarHid = $('#imcmsToolBarHidden') ;
    var posTop      = $(window).scrollTop() ;
    $(oToolBarVis).hide(0) ;
    $(oToolBarHid)
        .css({ 'top' : posTop + 'px', 'display' : 'block' })
        .show(0, function() {
            scrollHandler($, oToolBarHid) ;
            makePlaceHandler($, true, false) ;
        }) ;
}

/*        
function toggleToolBarSub($, objId) {
    var oBtn           = $('#' + objId) ;
    var isDropMenu     = $(oBtn).hasClass('imcmsToolBarShowBtnDrop') ;
    var oBtnFirst      = (isDropMenu) ? $(oBtn) : $('#imcmsToolBarMain .imcmsToolBarShowBtnSub:first') ;
    var oBtnLast       = (isDropMenu) ? $(oBtn) : $('#imcmsToolBarMain .imcmsToolBarShowBtnSub:last') ;
    var oToolBarMain   = $('#imcmsToolBarMain') ;
    var oToolBarActive = $('#' + objId + 'ToolBar') ;
    var btnPosFirst    = $(oBtnFirst).position() ;
    var btnPosLast     = $(oBtnLast).position() ;
    var isRightAligned = ($('#imcmsToolBarRight #' + objId).length > 0) ;
    if (isDropMenu) {
        $('.imcmsToolBarDrop:not(#' + objId + 'ToolBar)').slideUp('slow') ;
    } else {
        $('.imcmsToolBarSub:not(#' + objId + 'ToolBar)').slideUp('slow') ;
    }<%--
    alert(isRightAligned + ", w:" + $(window).width() + ", pos:" + btnPosLast.left + ", calc:" + ($(window).width() - btnPosLast.left - $(oBtnLast).width() - 5)) ; --%>
    var oCss = (isRightAligned) ? {
        top   : ($(oToolBarMain).height() + 10) + 'px',
        right : ($(window).width() - btnPosLast.left - $(oBtnLast).width() - 30) + 'px',
        left  : 'auto'
    } : {
        top   : ($(oToolBarMain).height() + 10) + 'px',
        right : 'auto',
        left  : (btnPosFirst.left - 5) + 'px'
    } ;
    $(oToolBarActive).css(oCss).slideToggle('slow', function() {
        setToolBarHeight($, false) ;
        if (doMakePlace && !isDropMenu) {
            makePlaceHandler($, false, true) ;
        }
    }) ;
}*/

<%--
/* *******************************************************************************************
 *         Set height of the complete ToolBar - So it hides properly                         *
 ******************************************************************************************* */
--%>

function setToolBarHeight($, includeSubToolBars) {
    if (includeSubToolBars) {
        var oToolBarSub = $('.imcmsToolBarDrop:visible:first') ;
        if (0 == oToolBarSub.length) {
            oToolBarSub = $('.imcmsToolBarSubPanel:visible:first') ;
        }
        var iOffset = ($(oToolBarSub).length > 0) ? 20 : 10 ;
        $('#imcmsToolBar').height($('#imcmsToolBarMain').height() + $(oToolBarSub).height() + iOffset) ;<%--
        $('h1').text('includeSubToolBars: ' + oToolBarSub.length + ', ' + ($('#imcmsToolBarMain').height() + $(oToolBarSub).height()) + ', ' + $('#imcmsToolBar').height()) ; --%>
    } else {
        $('#imcmsToolBar').height($('#imcmsToolBarMain').height()) ;<%--
        $('h1').text('!includeSubToolBars: ' + $('#imcmsToolBarMain').height() + ', ' + $('#imcmsToolBar').height()) ; --%>
    }
}

<%--
/* *******************************************************************************************
 *         Scroll handler for browsers without posistion:fixed support                       *
 ******************************************************************************************* */
--%>

function scrollHandler($, obj) {
    var isFixed = (20000000 == $(obj).css('z-index')) ;
    if (false && !isFixed) {
        alert('scrollHandler ACTIVE!') ;
        $(window).scroll(function() {
            var iScrollH = $(this).scrollTop() ;
            //$(obj).css('top', iScrollH + 'px') ;
            $('#adminPanelTd1_1').html(iScrollH + 'px') ;
        }) ;
    }
}

<%--
/* *******************************************************************************************
 *         Make place handler - NOT implemented now - Pushes the content of the page down    *
 ******************************************************************************************* */
--%>

function makePlaceHandler($, forceRemove, restoreSaved) {<%--
    forceRemove  => remove margin.
    restoreSaved => restore saved value from cookie or jQuery element data on body.
    false, false => toggle on/off and save.
    false, true  => restore saved and recalculate.
    true, false  => remove margin.
    --%>
    var orgData = $('body').data('imcmsToolBarMakePlace') ;
    var forceAdd = false ;
    if (restoreSaved && 'true' == $.getCookie('imcmsToolBarMakePlace')) {
        forceAdd    = true ;
        forceRemove = false ;
    } else if (restoreSaved) {
        forceAdd    = false ;
        forceRemove = true ;
    }
    if (null == orgData) {
        orgData = $('body').css('margin-top') ;
        if (null == orgData) orgData = '' ;
        $('body').data('imcmsToolBarMakePlace', orgData) ;
    }
    if (!forceAdd && (forceRemove || $('body').hasClass('imcmsToolBarMakePlace'))) {
        $('body').removeClass('imcmsToolBarMakePlace').css({
            'margin-top' : orgData
        }) ;
        if (!restoreSaved && !forceAdd && !forceRemove) $.setCookie('imcmsToolBarMakePlace', 'false') ;
        doMakePlace = false ;
    } else {
        var oToolBarSub = $('.imcmsToolBarSubPanel:visible:first') ;
        $('body').addClass('imcmsToolBarMakePlace').css({
            'margin-top' : ($('#imcmsToolBarMain').height() + $(oToolBarSub).height() + 25) + 'px'
        }) ;
        if (!restoreSaved && !forceAdd && !forceRemove) $.setCookie('imcmsToolBarMakePlace', 'true') ;
        doMakePlace = true ;
    }
}

<%--
/* *******************************************************************************************
 *         Sub ToolBars handlers                                                             *
 ******************************************************************************************* */
--%>

function showAppearanceSub($) {
    hideAllSubPanels($, function() {
        $.get('<%= AdminPanelServlet.getPathToAjaxHandler(metaId, request) %>',
            {
                get:   'changePageHtml',
                flags: flags,
                rnd:   '<%= System.currentTimeMillis() %>'
            }, function(changePage) {
                /*alert(changePage) ;*/
                if (null != changePage && '' != changePage) {
                    activateTab($, 'imcmsToolBarBtnAppearance', true) ;
                    $('#imcmsToolBarBtnAppearance_tab')
                        .addClass('imcmsToolBarTabSubActive') ;
                    $('#imcmsToolBarSubAppearance')
                        .stop().hide(0)
                        .html(changePage)
                        .show('slide', { direction: 'up' }, 'slow') ;
                }
            }
        ) ;
    }) ;
}
function hideAppearanceSub($, completeFn) {
    if ($('#imcmsToolBarSubAppearance:visible').length > 0) {
        $('#imcmsToolBarSubAppearance:visible')
            .stop()
            .hide('slide', { direction: 'up' }, 'slow', function(){
                $('#imcmsToolBarBtnAppearance_tab')
                    .removeClass('imcmsToolBarTabSubActive') ;
                if ('imcmsToolBarBtnAppearance' == lastActiveTabId) {
                    lastActiveTabId = 'imcmsToolBarBtnNormal' ;
                }
                activateTab($, lastActiveTabId, true) ;
                if ($.isFunction(completeFn)) {
                    completeFn() ;
                }
            }) ;
    } else if ($.isFunction(completeFn)) {
        completeFn() ;
    }
}

function showSubPanel($, btnId, tabId, panelId) {
    hideAppearanceSub($, function() {
        hideAllSubPanels($, function() {
            if (null != btnId && '' != btnId) {
                activateBtn($, btnId, true) ;
            } else if (null != tabId && '' != tabId) {
                activateTab($, tabId, true) ;
            }
            if (null != tabId && '' != tabId) {
                $('#' + tabId + '_tab')
                    .addClass('imcmsToolBarTabSubActive') ;
            }
            $('#' + panelId)
                .stop().hide(0)
                .show('slide', { direction: 'up' }, 'slow') ;
        }) ;
    }) ;
}
        
function hideSubPanel($, btnId, tabId) {
    hideAllSubPanels($, function() {
        if (null != btnId && '' != btnId) {
            deActivateBtn($, btnId) ;
        } else if (null != tabId && '' != tabId) {
            deActivateTab($, tabId) ;
        }
    }) ;
}

<%--
/* *******************************************************************************************
 *         Hide all sub panels - And show another if you want                                *
 ******************************************************************************************* */
--%>

function hideAllSubPanels($, completeFn) {
    if ($('.imcmsToolBarSubPanel:visible').length > 0) {
        $('.imcmsToolBarSubPanel:visible').hide('slide', { direction: 'up' }, 'slow', function() {
            if ($.isFunction(completeFn)) {
                completeFn() ;
            }
        }) ;
    } else if ($.isFunction(completeFn)) {
        completeFn() ;
    }
}

<%--
/* *******************************************************************************************
 *         Cookie/ToolTip functions                                                          *
 ******************************************************************************************* */
--%>

var oImcmsToolTipTimer = null ;

jQ.extend({
    setCookie : function(name, value) { 
        var sPath = '/' ;
        var today = new Date() ;
        var expire = new Date() ;
        expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
        var sCookieCont = name + "=" + encodeURIComponent(value) ;
        sCookieCont += (expire == null) ? "" : "; expires=" + expire.toGMTString() ;
        sCookieCont += "; path=" + sPath ;
        document.cookie = sCookieCont ;
    },
    getCookie : function(name) { 
        var search = name + "=" ;
        if (document.cookie.length > 0) {
            var offset = document.cookie.indexOf(search) ;
            if (offset != -1) {
                offset += search.length ;
                var end = document.cookie.indexOf(";", offset) ;
                if (end == -1) {
                    end = document.cookie.length ;
                }
                return decodeURIComponent(document.cookie.substring(offset, end)) ;
            }
        }
        return "" ;
    },
    toolTip : function(selector){
        if (oImcmsToolTipTimer) {
            window.clearTimeout(oImcmsToolTipTimer) ;
        }
        xOffset = 10;
        yOffset = 20;
        $(selector).each(function(){
            //$(selector + ' .imcmsToolTip,' + selector + ' .imcmsToolTipHide').hover(function(e){
            $(this).find('.imcmsToolTip,.imcmsToolTipRev,.imcmsToolTipHide,.imcmsStatusIconImg').hover(function(e){
                this.t = this.title;
                this.title = "";
                var fileData = $(this).attr("rel") ;
                var isReversed = $(this).hasClass("imcmsToolTipRev") ;
                this.iconClass = "" ;
                if (null != fileData && fileData.length > 7 && fileData.indexOf("FIL") != -1) {
                    var fileExt = fileData.substring(4,7) ;
                    if (/^(PDF|DOC|ZIP|JPG|PNG|GIF|MP3|AVI|MPG)$/i.test(fileExt)) {
                        this.iconClass = "imcmsToolTipIcon_" + fileExt.toUpperCase() ;
                    }
                } else if (null != fileData && fileData.indexOf("URL") != -1) {
                    this.iconClass = "imcmsToolTipIcon_EXT_LINK" ;
                }
                $("body").append('<div id="imcmsToolTipPop">'+ this.t.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/#LT#/g, "&lt;").replace(/#GT#/g, "&gt;") + '</div>');
                if (isReversed) {
                    xOffset = -($("#imcmsToolTipPop").width()) ; 
                } else {
                    xOffset = 10;
                }
                $("#imcmsToolTipPop")
                    .css("top",(e.pageY + yOffset) + "px")
                    .css("left",(e.pageX + xOffset) + "px")
                    .fadeIn(document.all ? "fast" : "slow");
                if ($(this).hasClass('imcmsToolTipHide')) {
                    $("#imcmsToolTipPop").delay(1000).fadeTo(500, 0) ;
                    //this.title = this.t;
                }
            }, function(){
                this.title = this.t;
                $("#imcmsToolTipPop").remove();
            });
        }) ;
        $(".imcmsToolTip,.imcmsToolTipRev,.imcmsToolTipHide,.imcmsStatusIconImg").mousemove(function(e){
            $("#imcmsToolTipPop")
                .css("top",(e.pageY + yOffset) + "px")
                .css("left",(e.pageX + xOffset) + "px") ;
            if ("" != this.iconClass) {
                $("#imcmsToolTipPop").addClass(this.iconClass) ;
            }
        });
    }
}) ;


<%--
/* *******************************************************************************************
 *         Events for tabs/buttons - Open links                                              *
 ******************************************************************************************* */
--%>

function imAdmGoTo(event, path) {
    if (event && (event.shiftKey || event.ctrlKey)) {
        window.open(path) ;
    } else {
        top.location = path ;
    }
    return false ;
}
function imAdmOpen(path) {
    window.open(path) ;
    return false ;
}

<%-- Moved from inPage_admin.html --%>
function imcmsTargetNewWindow() {
    var exampelWindow = window.open("", "Exempelmall", "scrollbars=yes,toolbar=0,resizable=yes,location=0,directories=0,status=0,menubar=0,height=500,width=800") ;
    document.changePageForm.target = "Exempelmall" ;
    exampelWindow.focus() ;
}
function imcmsResetTarget() {
    document.changePageForm.target = "" ;
}

<vel:velocity>

<%-- Moved from adminbuttons.jsp --%>
function imcmsOpenHelpWin(helpDocName) {
    window.open('@documentationurl@/Help?name=' + helpDocName + '&lang=$language', 'imcmsHelpWin') ;
    return false ;
}
function openHelpW(helpDocName){
    window.open('@documentationurl@/Help?name=' + helpDocName + '&lang=$language', 'imcmsHelpWin') ;
}

</vel:velocity>