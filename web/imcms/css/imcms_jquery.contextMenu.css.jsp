<%@ page

	contentType="text/css"
	pageEncoding="UTF-8"

%><%

String cp = request.getContextPath() ;

%>

/* Generic context menu styles */

.imcmsContextMenu {
	position: absolute;
	width: 300px;
	z-index: 99999999;
	border: solid 1px #000;
	background: #f1eded;
	padding: 0;
	margin: 0;
	white-space: nowrap;
	font: 11px Tahoma,Arial,Verdana,sans-serif;
	display: none;
}

.imcmsContextMenu LI {
	list-style: none;
	padding: 0;
	margin: 0;
	text-align: left;
}

.imcmsContextMenu A {
	display: block;
	color: #333;
	text-decoration: none;
	line-height: 20px;
	height: 20px;
	outline: none;
	padding: 1px 5px 1px 24px;
	background: url(<%= cp %>/imcms/images/icons/icon_checked_false.gif) 5px 6px no-repeat;
}

.imcmsContextMenu LI.active A {
	background-image: url(<%= cp %>/imcms/images/icons/icon_checked_true.gif);
}
.imcmsContextMenu LI.action A {
	background-image: url(<%= cp %>/imcms/images/icons/icon_dot_arrow.gif);
}

.imcmsContextMenu LI.disabled A {
	color: #aaa !important;
	cursor: default;
}
.imcmsContextMenu LI.action.disabled A {
	background-image: url(<%= cp %>/imcms/images/icons/icon_dot_arrow.gif) !important;
}

.imcmsContextMenu LI.hover.disabled A {
	background-color: transparent;
}

.imcmsContextMenu LI.separator {
	border-top: solid 1px #fff;
}

.imcmsContextMenu LI.hover A {
	color: #fff;
	background-color: #20568d;<%-- #f6851c --%>
}
.imcmsContextMenu LI.hover.action A {
	background-image: url(<%= cp %>/imcms/images/icons/icon_dot_arrow_inv.gif);
}

.imcmsContextMenu LI#li_SHOW_HELP A {
	background-image: url(<%= cp %>/imcms/images/icons/icon_help.gif);
}
.imcmsContextMenu LI#li_SHOW_HELP.hover A {
	background-image: url(<%= cp %>/imcms/images/icons/icon_help_inv.gif);
}

/*
	Adding Icons
	
	You can add icons to the context menu by adding
	classes to the respective LI element(s)
*/
/*
.imcmsContextMenu LI.edit A { background-image: url(images/page_white_edit.png); }
.imcmsContextMenu LI.cut A { background-image: url(images/cut.png); }
.imcmsContextMenu LI.copy A { background-image: url(images/page_white_copy.png); }
.imcmsContextMenu LI.paste A { background-image: url(images/page_white_paste.png); }
.imcmsContextMenu LI.delete A { background-image: url(images/page_white_delete.png); }
.imcmsContextMenu LI.quit A { background-image: url(images/door.png); }

*/
