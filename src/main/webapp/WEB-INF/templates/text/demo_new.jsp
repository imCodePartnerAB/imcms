<%--
  Created by Serhii from Ubrainians for Imcode
  Date: 15.09.17
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<imcms:variables/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>imcms v2</title>
    <!--style files-->
    <link rel="stylesheet" href="<imcms:contextPath/>/demo/css/demo.css">
</head>
<body>

<div class="imcms-info-msg">Move your mouse up!</div>
<div class="imcms-info-msg">Implemented admin panel buttons: "Public", "Edit", "Page Info" and "Document"</div>

<div class="imcms-demo-page">
    <imcms:menu no='1'>
        <div class="imcms-demo-page__menu imcms-demo-menu">
            <imcms:menuLoop>
                <imcms:menuItem>
                    <div class="imcms-demo-menu__menu-item imcms-demo-menu-item${hasChildren?' imcms-demo-menu__menu-item--parent':''}">
                        <div class="imcms-demo-menu-item__text">
                            <imcms:menuItemLink>${menuItem.document.headline}</imcms:menuItemLink>
                        </div>
                        <imcms:menuLoop>
                            <div class="imcms-demo-menu__menu-items imcms-demo-menu__menu-items--child">
                                <imcms:menuItem>
                                    <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">
                                        <imcms:menuItemLink>${menuItem.document.headline}</imcms:menuItemLink>
                                    </div>
                                </imcms:menuItem>
                            </div>
                        </imcms:menuLoop>
                    </div>
                </imcms:menuItem>
            </imcms:menuLoop>
        </div>
    </imcms:menu>
    <div class="imcms-editor-area imcms-editor-area--menu" data-doc-id="1001" data-menu-id="1">
        <div class="imcms-editor-area__content imcms-editor-content">
            <div class="imcms-demo-page__menu imcms-demo-menu">
                <div class="imcms-demo-menu__menu-item imcms-demo-menu__menu-item--active imcms-demo-menu-item">
                    <div class="imcms-demo-menu-item__text">Start page</div>
                </div>
                <div class="imcms-demo-menu__menu-item imcms-demo-menu-item imcms-demo-menu__menu-item--parent">
                    <div class="imcms-demo-menu-item__text">Some page</div>
                    <div class="imcms-demo-menu__menu-items imcms-demo-menu__menu-items--child">
                        <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">Some page child 1</div>
                        <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">Some page child 2</div>
                        <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">Some page child 3</div>
                    </div>
                </div>
                <div class="imcms-demo-menu__menu-item imcms-demo-menu-item">
                    <div class="imcms-demo-menu-item__text">Childless page</div>
                </div>
                <div class="imcms-demo-menu__menu-item imcms-demo-menu-item imcms-demo-menu__menu-item--parent">
                    <div class="imcms-demo-menu-item__text">One more parent page</div>
                    <div class="imcms-demo-menu__menu-items imcms-demo-menu__menu-items--child">
                        <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">Page child 1</div>
                        <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">Page child 2</div>
                        <div class="imcms-demo-menu__menu-item imcms-demo-menu-item__text">Page child 3</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="imcms-editor-area__control-wrap">
            <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--menu"></div>
            <div class="imcms-editor-area__control-title">Menu Editor</div>
        </div>
    </div>

    <div class="imcms-demo-page__content imcms-demo-content">
        <div class="imcms-demo-content__title">Start page</div>
        <div class="imcms-editor-area imcms-editor-area--loop" data-doc-id="1001" data-loop-id="1">
            <div class="imcms-editor-area__content imcms-editor-content">
                <div class="imcms-demo-content__loop-content demo-loop-content">
                    <div class="demo-loop-content__image">Lorem ipsum dolor sit amet, consectetur adipiscing</div>
                    <div class="demo-loop-content__text-area demo-loop-texts">
                        <div class="demo-loop-texts__text-area demo-text-area demo-text-area--left">
                            <p>Title Name 1</p>

                            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ultrices dui ac mauris
                            facilisis ornare. Morbi congue mauris non eros ultrices porta. Suspendisse sed consequat
                            elit. Proin placerat augue tortor, nec gravida odio tempor a. Duis facilisis erat in
                            maximus sagittis. Suspendisse potenti. Vivamus rutrum facilisis elit, non ultricies
                            nulla tincidunt in. Suspendisse commodo tincidunt varius. Duis tincidunt, augue aliquet
                            tristique scelerisque, mi felis vehicula dui, a rhoncus mi mauris in neque. Maecenas non
                            elit non lacus blandit volutpat iaculis ac lacus. Sed sed mollis eros. Praesent bibendum
                            egestas mauris id tincidunt. Nulla lectus massa, tempor vel interdum non, sollicitudin
                            vitae nunc. Vestibulum nec sapien sit amet orci rutrum iaculis. Quisque sit amet aliquam
                            lorem.
                        </div>
                        <div class="demo-loop-texts__text-area demo-text-area demo-text-area--right">
                            <p>Title Name 2</p>

                            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ultrices dui ac mauris
                            facilisis ornare. Morbi congue mauris non eros ultrices porta. Suspendisse sed consequat
                            elit. Proin placerat augue tortor, nec gravida odio tempor a. Duis facilisis erat in
                            maximus sagittis. Suspendisse potenti. Vivamus rutrum facilisis elit, non ultricies
                            nulla tincidunt in. Suspendisse commodo tincidunt varius. Duis tincidunt, augue aliquet
                            tristique scelerisque, mi felis vehicula dui, a rhoncus mi mauris in neque.
                        </div>
                    </div>
                </div>
            </div>
            <div class="imcms-editor-area__control-wrap">
                <div class="imcms-editor-area__control-edit imcms-control imcms-control--edit imcms-control--loop">
                    <div class="imcms-editor-area__control-title">Loop Editor</div>
                </div>
            </div>
        </div>
        <div class="imcms-demo-content__images-texts-demo demo-row">
            <div class="demo-row__column demo-element">
                <imcms:image no="1"/>
                <imcms:text no="1" label="Demo text 1"/>
            </div>
            <div class="demo-row__column demo-element">
                <imcms:image no="2"/>
                <imcms:text no="2" label="Demo text 2"/>
            </div>
            <div class="demo-row__column demo-element">
                <imcms:image no="3"/>
                <imcms:text no="1" label="Demo text 3"/>
            </div>
        </div>
    </div>
</div>

</body>
<imcms:admin/>
</html>
