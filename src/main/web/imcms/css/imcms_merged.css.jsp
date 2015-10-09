<%@ page
	contentType="text/css"
%>
<%
String cp = request.getContextPath() ;
%>

<%--BASE--%>

.reset,
.reset * {
    margin: 0;
    padding: 0;
    border: 0;
    background: transparent;
    text-decoration: none;
    width: auto;
    height: auto;
    vertical-align: baseline;
    box-sizing: content-box;
    -moz-box-sizing: content-box;
    -webkit-box-sizing: content-box;
    position: static;
    -webkit-transition: none;
    -moz-transition: none;
    -ms-transition: none;
    transition: none;
    border-collapse: collapse;
    font: normal normal normal 12px Arial, Helvetica, Tahoma, Verdana, Sans-Serif;
    color: #000;
    text-align: left;
    white-space: nowrap;
    cursor: auto;
    float: none;
}

[aria-invalid=true],
.error[aria-invalid=true] {
    background-color: #ffaaaa !important;
}

.imcms-w3c-errors h2 {
    font-size: 30px;
}

.imcms-w3c-error > div:first-child {
    font-weight: bold;
}

.imcms-w3c-error > div:last-child,
.imcms-w3c-error code {
    padding: 10px !important;
}

.imcms-text-history,
.imcms-text-history > .imcms-content,
.imcms-text-history > .imcms-left-panel {
    height: 400px;
}

.imcms-text-history > .imcms-left-panel {
    float: left;
    width: 30%;
    overflow: auto;
    height: 420px;
    margin: -20px 5px -20px -10px;
}

.imcms-text-history > .imcms-left-panel .selected {
    background-color: #0091e1;
    color: #fff;
}

.imcms-text-history > .imcms-left-panel > div:not(.imcms-separator) {
    padding: 5px;
}

.imcms-text-history > .imcms-left-panel > div:hover:not(.imcms-separator) {
    background-color: #ffff64;
    color: #000;
}

.imcms-text-history > .imcms-left-panel > .imcms-separator {
    padding: 10px 0 0 0;
    font-weight: bold;
    border-bottom: solid 1px black;
}

.imcms-text-history > .imcms-content {
    max-height: 400px;
    width: 70%;
    max-width: 70%;
}

.imcms-text-history > .imcms-content * {
    text-align: center;
}

<%--BASE--%>

<%--

editor theme

--%>
.edit-mode {
}

.editor-form {
    background: #fff;
    display: none;
    position: fixed;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: 1002;
}

.editor-form, .editor-form *, .pop-up-form *, .pop-up-form {
    font: normal 15px Arial;
}

.editor-form .imcms-header {
    background: #ff9600;
    overflow: hidden;
}

.editor-form .imcms-header .imcms-title {
    color: #fff;
    line-height: 30px;
    font-size: 15px;
    text-transform: uppercase;
    float: left;
    padding: 0 20px;
    height: 30px;
}

.editor-form .imcms-header .imcms-save-and-close {
    line-height: 20px;
    float: left;
    margin: 5px 5px 0 0;
    height: 20px;
}

.editor-form .imcms-content {
    overflow: auto;
}

.editor-form .imcms-content .imcms-negative {
    background: #dc0000 url("<%= cp %>/images/remove.png") no-repeat center;
    line-height: 20px;
    display: none;
    padding: 0;
    width: 20px;
    height: 20px;
}

.editor-form .imcms-footer {
    background: #f0f0f0;
    padding: 20px;
    /*height: 30px;*/
}

.editor-form .imcms-footer input {
    border: none;
    padding: 0 10px;
    line-height: 30px;
    float: left;
    width: 200px;
    height: 30px;
}

.editor-form .imcms-footer .browse {
    text-align: center;
    float: left;
    padding: 0;
    width: 30px;
}

.editor-form .imcms-footer .add {
    float: left;
}

.editor-form .imcms-footer .create-new {
    float: left;
    margin-left: 20px;
}

.editor-base {
    overflow: hidden;
}

.editor-menu-wrapper-adder, .editor-menu-wrapper-accepter {
    height: 34px;
    width: 34px;
}

.custom-combobox {
    position: relative;
    display: inline-block;
    float: left;
    margin-right: 45px;
}

.custom-combobox-toggle {
    position: absolute;
    top: 0;
    bottom: 0;
    margin-left: -1px;
    padding: 0;
}

.custom-combobox-input {
    margin: 0;
    padding: 5px 10px;
}

.editor-menu-item > a {
    display: block;
    float: left;
    line-height: 22px;
    margin: 0px 5px
}

.editor-menu-item-wrapper-button {
    width: 22px;
    height: 22px;
}

.editor-form .imcms-header .imcms-save-and-close, .editor-form .imcms-header .close-without-saving {
    line-height: 20px;
    float: left;
    padding: 0 10px;
    margin: 5px 5px 0 0;
    height: 20px;
}

.pop-up-form {
    background: #fff;
    display: none;
    position: fixed;
    width: 1000px;
    z-index: 1009;
}

.pop-up-form .imcms-title {
    background: #ff9600;
    color: #fff;
    line-height: 30px;
    text-transform: uppercase;
    padding: 0 20px;
}

.pop-up-form .imcms-content {
    position: relative;
    padding: 20px;
}

.pop-up-form .with-tabs {
    padding-left: 220px;
}

.pop-up-form .imcms-content .imcms-tabs {
    background: #323232;
    position: absolute;
    left: 0;
    top: 0;
    width: 200px;
    height: 100%;
}

.pop-up-form .imcms-content .imcms-tabs .imcms-tab {
    color: #fff;
    line-height: 30px;
    padding: 0 20px;
}

.pop-up-form .imcms-content .imcms-pages .imcms-page {
    height: 300px;
    overflow-y: auto;
    display: none;
}

.pop-up-form .imcms-content .imcms-pages .imcms-page.active {
    display: block;
}

.pop-up-form .imcms-content .imcms-tabs .imcms-tab:hover {
    background: #484848;
}

.pop-up-form .imcms-content .imcms-tabs .active {
    background: #fff !important;
    color: #000 !important;
}

.editor-form .imcms-content .field,
.pop-up-form .imcms-content .field {
    clear: both;
    padding-top: 10px;
}

.editor-form .imcms-content .field label,
.pop-up-form .imcms-content .field label {
    display: block;
    margin-bottom: 2px;
}

.editor-form .imcms-content .field select + div.pqSelect,
.pop-up-form .imcms-content .field select + div.pqSelect,
.pop-up-form .imcms-content .field select,
.editor-form .imcms-content .field input,
.pop-up-form .imcms-content .field input {
    background: #f0f0f0;
    border: none;
    padding: 5px 10px;
    width: 400px;
    outline: none;
    height: 20px;
    resize: none;
}

.pop-up-form .imcms-content .field select {
    height: 20px;
    width: 420px;
}

.pop-up-form .imcms-content .field select[multiple] {
    height: 200px;
}

.pop-up-form .imcms-content .field textarea {
    background: #f0f0f0;
    border: none;
    padding: 5px 10px;
    width: 705px;
    height: 100px;
}

.pop-up-form .imcms-content .checkbox {
    padding-top: 10px;
}

.pop-up-form .imcms-content .buttons {
    padding: 20px 0 0;
    overflow: hidden;
}

.pop-up-form .imcms-content .buttons .imcms-positive {
    margin-right: 20px;
}

.pop-up-form .imcms-content .buttons .imcms-neutral {
    background: #323232;
}

.pop-up-form .imcms-content .buttons .imcms-neutral:hover {
    background: #484848;
}

::-moz-focus-inner {
    border: 0;
    padding: 0;
}

::-ms-clear {
    display: none;
}

::-ms-reveal {
    display: none;
}

<%--EDITOR THEME--%>

<%--PANEL--%>

.admin-panel {
    display: block;
    width: auto;
    background: #eee;
    -moz-border-radius: 5px;
    -webkit-border-radius: 5px;
    border-radius: 5px;
    padding: 3px;
    overflow: auto;
    position: fixed;
    -webkit-box-shadow: 0 0 15px 0 rgba(50, 50, 50, 0.5);
    -moz-box-shadow: 0 0 15px 0 rgba(50, 50, 50, 0.5);
    box-shadow: 0 0 15px 0 rgba(50, 50, 50, 0.5);
    font-size: 9pt;
}

.admin-panel-draggable {
    width: 14px;
    margin-right: 3px;
    background: url("<%=cp%>/images/bg_draggable.png") 0 0 no-repeat;
    cursor: move;
}

.admin-panel-content {
    width: auto;
    background: #fff;
}

.admin-panel-content-section-language {
    width: auto;
}

.admin-panel,
.admin-panel-draggable,
.admin-panel-content,
.admin-panel-content-section {
    height: 70px;
}

.admin-panel-content-separator,
.admin-panel-button {
    height: 64px;
    margin: 3px;
}

.admin-panel-content-separator {
    width: 3px;
    background: #eee;
}

.admin-panel-content-separator-white {
    background: #fff;
}

.admin-panel-content-separator,
.admin-panel-draggable,
.admin-panel-button,
.admin-panel-content,
.admin-panel-content-section {
    float: left;
}

.admin-panel-button {
    width: 64px;
    -moz-border-radius: 10px;
    -webkit-border-radius: 10px;
    border-radius: 10px;
}

.admin-panel-content-section-disabled {
    opacity: 0.3;
}

.admin-panel-content-section[data-mode=readonly] .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_readonly.png") no-repeat;
}

.admin-panel-content-section[data-mode=edit] .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_edit.png") no-repeat;
}

.admin-panel-content-section[data-mode=info] .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_pageinfo.png") no-repeat;
}

.admin-panel-content-section[data-mode=docs] .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_documents.png") no-repeat;
}

.admin-panel-content-section[data-mode=admin] .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_adminmanager.png") no-repeat;
}

.admin-panel-content-section[data-mode=logout] .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_logout.png") no-repeat;
}

.admin-panel-content-section.active[data-mode=readonly] .admin-panel-button .admin-panel-button-image,
.admin-panel-content-section[data-mode=readonly] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_readonly.png") 0 -32px no-repeat;
}

.admin-panel-content-section.active[data-mode=edit] .admin-panel-button .admin-panel-button-image,
.admin-panel-content-section[data-mode=edit] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_edit.png") 0 -32px no-repeat;
}

.admin-panel-content-section[data-mode=info] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_pageinfo.png") 0 -32px no-repeat;
}

.admin-panel-content-section[data-mode=docs] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_documents.png") 0 -32px no-repeat;
}

.admin-panel-content-section[data-mode=admin] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_adminmanager.png") 0 -32px no-repeat;
}

.admin-panel-content-section[data-mode=logout] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_logout.png") 0 -32px no-repeat;
}

.admin-panel-content-section-disabled[data-mode=info] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_pageinfo.png") 0 0 no-repeat;
}

.admin-panel-content-section-disabled[data-mode=docs] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_documents.png") 0 0 no-repeat;
}

.admin-panel-content-section-disabled[data-mode=admin] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_adminmanager.png") 0 0 no-repeat;
}

.admin-panel-content-section-disabled[data-mode=logout] .admin-panel-button:hover .admin-panel-button-image {
    background: url("<%=cp%>/images/ic_logout.png") 0 0 no-repeat;
}

.admin-panel-button-image {
    width: 32px;
    height: 32px;
    margin: 10px auto 0 auto;
}

.admin-panel-content-section .admin-panel-version,
.admin-panel-content-section .admin-panel-version * {
    color: #0091e1;
    font-size: 11px;
    margin: 14px 20px 0 20px;
}

.admin-panel-content-section .admin-panel-version span {
    font-weight: bold;
    margin: 0;
}

.admin-panel-content-section .admin-panel-button-description {
    display: inline-block;
    width: 64px;
    text-align: center;
}

.admin-panel-content-section .admin-panel-button {
    color: black;
    text-align: center;
}

.admin-panel-content-section.active .admin-panel-button,
.admin-panel-content-section .admin-panel-button:hover {
    background-color: #ebf0ff;
    color: #0091e1;
}

#additionalInfo.admin-panel-content-section .admin-panel-button *,
#additionalInfo.admin-panel-content-section .admin-panel-button:hover {
    background-color: #fff;
    color: #0091e1;
}

#additionalInfo.admin-panel-content-section .admin-panel-button div {
    padding-top: 10px;
}

#additionalInfo.admin-panel-content-section .admin-panel-button span {
    font-weight: bold;
}

.admin-panel-content-section-disabled .admin-panel-button:hover {
    background-color: #fff;
    color: #000;
}

.admin-panel-content-section div.admin-panel-language {
    margin: 0 20px;
}

.admin-panel-content-section div.admin-panel-language a {
    float: left;
}

.admin-panel-content-section div.admin-panel-language a:first-child {
    display: block;
    float: left;
    margin-right: 5px;
}

.admin-panel-content-section div.admin-panel-language a.active {
    background: #ebf0ff;
    padding: 0 6px;
    border-radius: 5px;
    margin: 0 0 0 -6px;
}

<%--PANEL--%>

<%--FRAME--%>

.editor-frame {
    cursor: pointer;
    padding: 0;
    position: absolute;
    width: auto;
    height: 20px;
    opacity: 0.9;
    -webkit-border-radius: 3px;
    -moz-border-radius: 3px;
    border-radius: 3px;
}

.editor-frame:hover {
    opacity: 1;
}

.editor-frame .header-ph {
    display: block;
    width: auto;
    overflow: hidden;
}

.editor-frame .header-ph .imcms-header {
    position: relative;
    overflow: hidden;
    width: auto;
}

.editor-frame .header-ph .imcms-header .imcms-title {
    background: #0091e1;
    color: #fff;
    line-height: 20px;
    font-size: 10px;
    text-transform: uppercase;
    float: left;
    padding: 0 10px;
    height: 20px;
}

<%--FRAME--%>

<%--WINDOW--%>

.editor-form .imcms-header,
.pop-up-form .imcms-header {
    height: 20px;
    width: 100%;
    border-bottom: solid #eee 3px;
    background-color: #fff;
    padding: 11px 0;
}

.editor-form .imcms-header .imcms-title,
.pop-up-form .imcms-header .imcms-title {
    background-color: transparent;
    font-size: 15px;
    padding: 0 20px;
    color: #0091e1;
    line-height: 20px;
    text-transform: uppercase;
    float: left;
    height: 20px;
}

.editor-form .imcms-footer,
.pop-up-form .imcms-footer {
    height: 30px;
    padding: 10px;
    background-color: #eee;
}

.editor-form .imcms-positive,
.pop-up-form .imcms-positive {
    background: #649b00;
    border: none;
    color: #fff;
    cursor: pointer;
    line-height: 30px;
    display: inline-block;
    padding: 0 20px;
    height: 30px;
}

.pop-up-form .imcms-positive * {
    color: #fff;
}

.editor-form .imcms-positive:hover,
.pop-up-form .imcms-positive:hover {
    background: #6eaf00;
}

.editor-form .imcms-negative,
.pop-up-form .imcms-negative {
    background: #dc0000;
    border: none;
    color: #fff;
    cursor: pointer;
    line-height: 30px;
    display: inline-block;
    padding: 0 10px;
    height: 30px;
}

.pop-up-form .imcms-negative * {
    color: #fff;
}

.editor-form .imcms-negative:hover,
.pop-up-form .imcms-negative:hover {
    background: #e60000;
}

.editor-form .imcms-neutral,
.pop-up-form .imcms-neutral {
    background: rgba(0, 0, 0, 0.2);
    border: none;
    color: #fff;
    cursor: pointer;
    line-height: 30px;
    display: inline-block;
    padding: 0 20px;
    height: 30px;
}

.pop-up-form .imcms-neutral * {
    color: #fff;
}

.editor-form .imcms-neutral:hover,
.pop-up-form .imcms-neutral:hover {
    background: rgba(0, 0, 0, 0.1);
}

.editor-form .imcms-footer .imcms-neutral,
.pop-up-form .imcms-footer .imcms-neutral {
    background: #323232;
}

.editor-form .imcms-footer .imcms-neutral:hover,
.pop-up-form .imcms-footer .imcms-neutral:hover {
    background: #484848;
}

.editor-form .imcms-header .imcms-close-button,
.pop-up-form .imcms-header .imcms-close-button {
    cursor: pointer;
    float: right;
    margin: 0 20px;
    padding: 0;
    border: none;
    width: 20px;
    height: 20px;
    background: rgba(220, 0, 0, 0) url("<%=cp%>/images/close_button.png") no-repeat center;
}

.editor-form .imcms-footer .imcms-save-and-close {
    float: right;
}

.editor-form .imcms-header .imcms-close-button:focus,
.pop-up-form .imcms-header .imcms-close-button:focus {
    border: none;
    outline: none;
}

.document-viewer .imcms-footer:before {
    content: "";
    display: block;
    float: left;
    width: 180px;
    height: 100%;
    background-color: #0091e1;
    padding: 10px;
    margin: -10px 20px 0 -10px;
}

.pop-up-form .imcms-content {
    position: relative;
    padding: 20px;
}

.pop-up-form .with-tabs {
    padding-left: 220px;
}

.pop-up-form .imcms-content .imcms-tabs {
    background: #0091e1;
    position: absolute;
    left: 0;
    top: 0;
    width: 200px;
    height: 100%;
}

.pop-up-form .imcms-content .imcms-tabs .imcms-tab {
    color: #fff;
    line-height: 30px;
    padding: 0 20px;
}

.pop-up-form .imcms-content .imcms-pages .imcms-page {
    height: 300px;
    overflow-y: auto;
    display: none;
}

.pop-up-form .imcms-content .imcms-pages .imcms-page.active {
    display: block;
}

.pop-up-form .imcms-content .imcms-tabs .imcms-tab:hover {
    background: #484848;
}

.pop-up-form .imcms-content .imcms-tabs .active {
    background: #fff !important;
    color: #000 !important;
}

.pop-up-form .imcms-footer .imcms-positive {
    margin-right: 20px;
}

.pop-up-form .imcms-footer .imcms-neutral {
    background: #323232;
}

.pop-up-form .imcms-footer .imcms-neutral:hover {
    background: #484848;
}

.pop-up-form {
    -webkit-box-shadow: 0 0 15px 0 rgba(50, 50, 50, 0.5);
    -moz-box-shadow: 0 0 15px 0 rgba(50, 50, 50, 0.5);
    box-shadow: 0 0 15px 0 rgba(50, 50, 50, 0.5);
}

<%--WINDOW--%>

<%--LOOP--%>
.loop-viewer .imcms-content table {
    border-left: none;
    border-right: none;
    width: 100%;
    overflow: hidden;
    background-color: #fff;
}

.loop-viewer .imcms-content table tr td {
    border-bottom: 1px solid #f0f0ff;
    padding: 7.5px 20px;
    vertical-align: middle;
}

.loop-viewer .imcms-content table tr:hover td {
    background: #ffff64;
    color: black;
}

.loop-viewer .imcms-content table tr td:last-child {
    margin: 0;
    width: 20px;
    padding: 0 20px;
}

.loop-viewer .imcms-content table tr td:last-child button,
.loop-viewer .imcms-content table tr td:last-child .button {
    font-size: 13px;
    line-height: 25px;
}

.loop-viewer .imcms-content table tr:hover td:last-child button,
.loop-viewer .imcms-content table tr:hover td:last-child .button {
    display: block;
}

<%--LOOP--%>

<%--MENU--%>

.menu-viewer .imcms-content table {
    border-left: none;
    border-right: none;
    width: 100%;
    overflow: hidden;
    background-color: #fff;
}

.menu-viewer .imcms-content table tr th,
.menu-viewer .imcms-content table tr td {
    font-weight: normal;
    text-align: left;
    vertical-align: top;
}

.menu-viewer .imcms-content table tr th {
    background: #0091e1;
    font-weight: 500;
    text-transform: uppercase;
    height: 0;
    line-height: 0;
    padding-top: 0;
    padding-bottom: 0;
    color: transparent;
    border: none;
    white-space: nowrap;
}

.menu-viewer .imcms-content table tr td {
    border-bottom: 1px solid #f0f0ff;
    padding: 7.5px 20px;
}

.menu-viewer .imcms-content table tr.clicked td {
    background: #0091e1;
    color: white;
}

.menu-viewer .imcms-content table tr:hover td {
    background: #ffff64;
    color: black;
}

.menu-viewer .imcms-content table tr td.buttons {
    margin: 0;
}

.menu-viewer .imcms-content table tr td.buttons button,
.menu-viewer .imcms-content table tr td.buttons .button {
    font-size: 13px;
    line-height: 25px;
    visibility: hidden;
    padding: 0 10px;
}

.menu-viewer .imcms-content table tr:hover td.buttons button,
.menu-viewer .imcms-content table tr:hover td.buttons .button {
    visibility: visible;
}

.menu-viewer .imcms-content form > div .field:last-child {
    position: relative;
    padding-top: 30px;
    margin-top: 10px;
    background-color: #0091e1;
}

.menu-viewer .imcms-content form > div .field .field-wrapper {
    height: 290px;
    overflow-x: hidden;
    overflow-y: auto;
    background-color: #fff;
}

.menu-viewer .imcms-content form > div .field .field-wrapper table th div {
    position: absolute;
    background: transparent;
    color: #fff;
    top: 0;
    line-height: 30px;
    padding: 0 20px;
    cursor: pointer;
}

.editor-form .imcms-content ul, .editor-form .imcms-content ul li {
    margin: 0;
    padding: 0;
}

.editor-form .imcms-content ul {
    background: #f0f0f0;
    padding-left: 0 !important;
}

.editor-form .imcms-content .jqtree-tree {
    padding: 0 !important;
}

.editor-form .imcms-content ul li {
    background: #fff;
    list-style-type: none;
}

.editor-form .imcms-content ul li .jqtree-element {
    overflow: hidden;
    line-height: 20px;
    width: 100%;
}

.editor-form .imcms-content ul li .jqtree-element span {
    line-height: 30px;
    float: left;
    padding: 0 20px;
}

.editor-form .imcms-content ul li .jqtree-element span.column-right {
    float: right;
    padding: 5px;
}

.editor-form .imcms-content ul li .jqtree-element span.buttons {
    display: none;
}

.editor-form .imcms-content ul li .jqtree-element span.buttons > * {
    float: left;
    height: 20px;
    line-height: 20px;
    margin-right: 5px;
}

.editor-form .imcms-content ul li .jqtree-element span.buttons * {
    color: #fff;
}

.editor-form .imcms-content ul li .jqtree-element:hover span.buttons {
    display: block;
}

.editor-form ul.jqtree-tree *:nth-child(2n+1) .jqtree-element {
    /*background: #ffffe6;*/
}

.editor-form .imcms-footer input[name=menu-sort-case] {
    display: none;
}

.editor-form .imcms-footer input[name=menu-sort-case]:checked + button {
    background-color: #000;
}

ul.jqtree-tree li.jqtree-selected > .jqtree-element,
ul.jqtree-tree li.jqtree-selected > .jqtree-element:hover {
    background-color: #fff !important;
    background: #fff !important;
}

.editor-form ul.jqtree-tree .jqtree-element:hover {
    background: #ffff64;
}

ul.jqtree-tree span.jqtree-border {
    position: absolute !important;
    display: block !important;
    left: -2px !important;
    top: 0 !important;
    margin: 0 !important;
    border: none !important;
    box-sizing: content-box !important;
    background-color: #649b00 !important;
    height: 100%
    -ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=50)"; /* IE 8 */
    filter: alpha(opacity=50); /* IE 5-7 */
    -moz-opacity: 0.5; /* Netscape */
    -khtml-opacity: 0.5; /* Safari 1.x */
    opacity: 0.5;
}

ul.jqtree-tree li.jqtree-ghost span.jqtree-circle {
    border: solid 2px #649b00;
    -webkit-border-radius: 100px;
    -moz-border-radius: 100px;
    border-radius: 100px;
    height: 8px;
    width: 8px;
    position: absolute;
    top: -4px;
    left: -6px;
}

ul.jqtree-tree li.jqtree-ghost span.jqtree-line {
    background-color: #649b00;
    height: 2px;
    padding: 0;
    position: absolute;
    top: -1px;
    left: 2px;
    width: 100%;
}

ul.jqtree-tree ul.jqtree_common {
    margin-left: 30px !important;
    background-color: white;
    border-left: 1px solid #eee;
}

.editor-form .imcms-content ul.jqtree-tree .jqtree_common span .imcms-negative {
    background: #dc0000 url("<%=cp%>/images/remove.png") no-repeat center;
    line-height: 20px;
    display: block;
    padding: 0;
    width: 20px;
    height: 20px;
}

.editor-form .imcms-content ul.jqtree-tree .jqtree_common:hover span .imcms-negative {
    display: block;
    background: #dc0000 url("<%=cp%>/images/remove.png") no-repeat center;
}

.clear {
    clear: both;
}

.ui-front {
    z-index: 1005;
}

.ui-dialog {
    z-index: 1003;
}

<%--MENU--%>

<%--CONTENT--%>

@keyframes name-hover-on {
    from {
        height: 0;
    }
    to {
        height: 30px;
    }
}

@keyframes name-hover-off {
    from {
        height: 30px;
    }
    to {
        height: 0;
    }
}

/* Chrome, Safari, Opera */
@-webkit-keyframes name-hover-on {
    from {
        height: 0;
    }
    to {
        height: 30px;
    }
}

@-webkit-keyframes name-hover-off {
    from {
        height: 0;
    }
    to {
        height: 0;
    }
}

.editor-content .imcms-footer:before {
    content: "";
    display: block;
    float: left;
    width: 230px;
    height: 100%;
    background-color: #0091e1;
    padding: 10px;
    margin: -10px 20px 0 -10px;
}

.editor-content .imcms-footer input.hidden {
    cursor: pointer;
    font-size: 150px;
    opacity: 0;
    filter: alpha(opacity=0);
    margin: -100px 0 0 -500px;
    width: 1000px;
    height: 100px;
}

.editor-content .imcms-footer .browse {
    line-height: 30px;
    text-align: center;
    width: 85px;
    height: 30px;
    overflow: hidden;
    padding: 0 20px;
    cursor: pointer;
}

.editor-content .imcms-footer .browse div {
    line-height: 30px;
    width: 100%;
    color: #fff;
}

.editor-content .imcms-content .folders {
    width: 250px;
    background-color: #0091e1;
}

.editor-content .imcms-content .folders ul li {
    background: #0091e1;
    height: 30px;
}

.editor-content .imcms-content .folders ul li > div.jqtree-element span {
    line-height: 20px;
    padding: 5px 20px;
    color: #fff;
    text-transform: uppercase;
}

.editor-content .imcms-content .folders ul li.jqtree-selected > div.jqtree-element span {
    color: #000;
}

.editor-content .imcms-content .folders {
    float: left;
    height: 100%;
    min-width: 200px;
}

.editor-content .imcms-content .files-wrapper {
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    position: relative;
}

.editor-content .imcms-content .files {
    display: -webkit-box;
    display: -moz-box;
    display: -ms-flexbox;
    display: -webkit-flex;
    display: flex;
    height: auto;

    -webkit-flex-pack: flex-start;
    -webkit-justify-content: flex-start;
    -moz-justify-content: flex-start;
    -ms-flex-pack: center;
    justify-content: flex-start;

    -webkit-flex-line-pack: center;
    -ms-flex-line-pack: center;
    -webkit-align-content: center;
    align-content: center;

    -ms-flex-wrap: wrap;
    -webkit-flex-wrap: wrap;
    flex-wrap: wrap;
    position: relative;
}

.editor-content .imcms-content .files-wrapper .dropzone {
    pointer-events: none;
    display: none;
}

.editor-content .imcms-content .files-wrapper .dropzone.hover {
    pointer-events: none;
    position: absolute;
    left: 0;
    top: 0;
    display: block;
    width: 100%;
    height: 100%;
    background: rgba(0, 145, 225, 0.8) url("<%=cp%>/images/dropzone.png") 50% 50% no-repeat;
    z-index: 99999;
}

.editor-content .imcms-content .content-preview {
    width: 130px;
    height: 130px;
    overflow: hidden;
    float: left;
    position: relative;
    display: flex;
    -webkit-transform: translateZ(0);
    transform: translateZ(0);
    box-shadow: 0 0 1px rgba(0, 0, 0, 0);
    -moz-osx-font-smoothing: grayscale;
    -webkit-transition-duration: 0.3s;
    transition-duration: 0.3s;
    -webkit-transition-property: box-shadow, transform;
    transition-property: box-shadow, transform;
}

.editor-content .imcms-content .files .content-preview.selected {
    box-shadow: 0 0 20px -5px rgba(0, 0, 0, 0.75);
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
    z-index: 100;
}

.editor-content .imcms-content .files .content-preview .content-preview-info,
.editor-content .imcms-content .files .content-preview .content-preview-image {
    width: inherit;
    text-align: center;
}

.editor-content .imcms-content .files .content-preview .content-preview-info {
    overflow: hidden;
    position: absolute;
    height: 0;
    left: 0;
    bottom: 0;
    color: #fff;
    line-height: 30px;
    background: rgba(0, 0, 0, 0.6);
    animation: name-hover-off 0.2s forwards;
    -webkit-animation: name-hover-off 0.2s forwards;
}

.editor-content .imcms-content .files .content-preview:hover .content-preview-info {
    animation: name-hover-on 0.3s forwards;
    -webkit-animation: name-hover-on 0.3s forwards;
}

.editor-content .imcms-content .files .content-preview:hover {
    box-shadow: 0 0 20px -5px rgba(0, 0, 0, 0.75);
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
    z-index: 105;
}

.editor-content .imcms-content .files .content-preview .content-preview-image {
    height: inherit;
}

<%--CONTENT--%>

<%--DOCUMENT--%>

.document-viewer .imcms-content .multiselect-adapter {
    background-color: #f0f0f0;
    padding: 5px;
    width: 400px;
}

.document-viewer .imcms-content .multiselect-adapter .field:first-child {
    padding-top: 0;
}

.document-viewer .imcms-content table {
    border-left: none;
    border-right: none;
    margin-top: 20px;
    width: 100%;
    overflow: hidden;
}

.document-viewer .imcms-content table tr th,
.document-viewer .imcms-content table tr td {
    font-weight: normal;
    text-align: left;
    vertical-align: top;
}

.document-viewer .imcms-content table tr th {
    background: #0091e1;
    color: white;
    /*border-bottom: 3px solid #dcdcf0;*/
    font-weight: 300;
    line-height: 30px;
    text-transform: uppercase;
    padding: 0 20px;
    vertical-align: middle;
    text-align: center;
}

.document-viewer .imcms-content table tr td {
    border-bottom: 1px solid #f0f0ff;
    padding: 7.5px 20px;
    vertical-align: middle;
    text-align: center;
}

.document-viewer .imcms-content table tr.clicked td {
    background: #0091e1;
    color: white;
}

.document-viewer .imcms-content table tr:hover td {
    background: #ffff64;
}

.document-viewer .imcms-content table tr td.buttons {
    margin: 0;
}

.document-viewer .imcms-content table tr td.buttons button,
.document-viewer .imcms-content table tr td.buttons .button {
    font-size: 13px;
    line-height: 25px;
    visibility: hidden;
    padding: 0 10px;
}

.document-viewer .imcms-content table tr:hover td.buttons button,
.document-viewer .imcms-content table tr:hover td.buttons .button {
    visibility: visible;
}

.document-viewer .imcms-content table input[type=radio] {
    width: 20px;
    height: 20px;
}

.document-viewer .imcms-content .field input[type=checkbox] {
    float: left;
    height: 20px;
    width: 20px;
    padding: 10px;
}

.document-viewer .imcms-content .imcms-column {
    float: left;
    margin-right: 10px;
}

.document-viewer .imcms-content .field .label {
    padding: 5px 10px;
    height: 20px;
}

.document-viewer .imcms-content .keywords-page .field > * {
    float: left;
}

.document-viewer .imcms-content .keywords-page .field > label {
    float: none;
}

.document-viewer .imcms-content .file-page tr button,
.document-viewer .imcms-content .access-page tr button {
    visibility: hidden;
    background: #dc0000 url("<%=cp%>/images/remove.png") no-repeat center;
    line-height: 20px;
    padding: 0;
    width: 20px;
    height: 20px;
}

.document-viewer .imcms-content .file-page tr:hover button,
.document-viewer .imcms-content .access-page tr:hover button {

    visibility: visible;
}

.editor-document .imcms-content table {
    width: 100%;
}

.editor-document .imcms-content table tr td {
    vertical-align: middle;
    padding: 0 20px;
}

.editor-document .imcms-content table tr.archived td {
    vertical-align: middle;
    padding: 0 20px;
    background: #eee;
}

.editor-document .imcms-content table tr:hover td {
    background: #ffff64;
}

.editor-document .imcms-content table tr td:last-child {
    line-height: 0;
    padding: 5px;
    width: 186px;
}

.editor-document .imcms-content table tr td .imcms-positive {
    line-height: 20px;
    text-align: center;
    visibility: hidden;
    display: block;
    float: left;
    margin-right: 5px;
    height: 20px;
}

.editor-document .imcms-content table tr:hover td .imcms-positive {
    visibility: visible;
}

.editor-document .imcms-content table tr td .imcms-negative {
    background-image: url("<%=cp%>/images/remove.png");
    background-position: center;
    background-repeat: no-repeat;
    line-height: 20px;
    visibility: hidden;
    display: block;
    float: left;
    padding: 0;
    width: 20px;
    height: 20px;
}

.editor-document .imcms-content .imcms-negative {
    background: #dc0000 url("<%=cp%>/images/remove.png") no-repeat center;
    line-height: 20px;
    display: none;
    padding: 0;
    width: 20px;
    height: 20px;
}

.editor-document .imcms-content table tr:hover td .imcms-negative {
    visibility: visible;
}

.editor-document .imcms-content table tr:hover td .imcms-negative[data-remove=false] {
    visibility: visible;
    text-align: center;
    background-image: none;
}

.waiter {
    padding: 10px;
    background: url("<%=cp%>/images/upload.gif") no-repeat center;
}

<%--DOCUMENT--%>

<%--IMAGE--%>

.editor-image .imcms-content .image {
    width: 500px;
    background: #e5f4fc;
    border-right: 3px solid #0091e1;
}

.editor-image .imcms-content .choose-image-field:before {
    content: "Choose image";
    display: block;
}

.editor-image .imcms-content .choose-image-field > label {
    background: #f0f0f0;
    border: none;
    padding: 5px 10px;
    outline: none;
    height: 20px;
    resize: none;
    float: left;
    width: 294px;
    max-width: 294px;
    overflow: hidden;
}

.editor-image .imcms-content .size-field > input {
    width: 190px;
}

.editor-image .imcms-content .shared-mode-field > input,
.editor-image .imcms-content .free-transformation-field > input {
    float: left;
    height: 20px;
    width: 20px;
    padding: 10px;
}

.editor-image .imcms-content .free-transformation-field:after {
    content: "Free transformation";
    float: left;
    padding: 5px 10px;
    height: 20px;
}

.editor-image .imcms-content .shared-mode-field:after {
    content: "Set for all languages";
    float: left;
    padding: 5px 10px;
    height: 20px;
}

.editor-image .imcms-content .choose-image {
    background: #323232;
    float: left;
}

.editor-image .imcms-content .modal {
    background: #000;
    opacity: 0.5;
    filter: alpha(opacity=50);
    display: none;
    position: fixed;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: 1008;
}

.editor-image .imcms-content .image-cropper {
    background: #e5f4fc;
    display: none;
    position: relative;
    height: 100%;
    width: 500px;
    float: left;
}

.editor-image .imcms-content .info {
    float: left;
    padding: 10px;
}

.editor-image .imcms-content .image-cropper img {
    max-width: 500px;
    /*max-height: 100%;*/
}

.editor-image .imcms-content .image-cropper .image-shader {
    background: #000;
    opacity: 0.75;
    filter: alpha(opacity=75);
    position: absolute;
    left: 0;
    top: 0;
}

.editor-image .imcms-content .image-cropper .image-cropping-frame {
    border: 1px solid #0096fa;
    cursor: move;
    position: absolute;
    left: -1px;
    top: -1px;
    overflow: hidden;
    background-color: #e5f4fc;
}

.editor-image .imcms-content .image-cropper .image-cropping-frame .image-fragment {
    position: absolute;
}

.editor-image .imcms-content .image-cropper .grip {
    background: #0096fa;
    cursor: se-resize;
    position: absolute;
    width: 11px;
    height: 11px;
}

.editor-image .imcms-content .cropping-field input {
    width: 85px;
}

<%--IMAGE--%>

<%--PROCESS--%>

@-webkit-keyframes bouncedelay {
    0%, 80%, 100% {
        -webkit-transform: scale(0.0)
    }
    40% {
        -webkit-transform: scale(1.0)
    }
}

@keyframes bouncedelay {
    0%, 80%, 100% {
        transform: scale(0.0);
        -webkit-transform: scale(0.0);
    }
    40% {
        transform: scale(1.0);
        -webkit-transform: scale(1.0);
    }
}

.process-window {
    position: fixed;
    left: 0;
    top: 0;
    background: rgba(256, 256, 256, 0.9);
    z-index: 999999;

}

.process-window .logo,
.process-window .spinner {
    margin: 100px auto 0;
    width: 70px;
    text-align: center;
    -webkit-transition: all 0.3s ease-in-out 0s;
    transition: all 0.3s ease-in-out 0s;
    display: block;
}

.process-window .logo {
    margin: 100px auto 0;
    width: 300px;
    height: auto;
}

.process-window .spinner > div {
    width: 18px;
    height: 18px;
    background-color: #333;

    border-radius: 100%;
    display: inline-block;
    -webkit-animation: bouncedelay 1.4s infinite ease-in-out;
    animation: bouncedelay 1.4s infinite ease-in-out;
    /* Prevent first frame from flickering when animation starts */
    -webkit-animation-fill-mode: both;
    animation-fill-mode: both;
}

.process-window .spinner .bounce0 {
    -webkit-animation-delay: -0.32s;
    animation-delay: -0.32s;
}

.process-window .spinner .bounce1 {
    -webkit-animation-delay: -0.16s;
    animation-delay: -0.16s;
}

<%--PROCESS--%>


