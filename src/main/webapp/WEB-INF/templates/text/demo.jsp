<%--
  Created by Serhii from Ubrainians for Imcode
  Date: 15.09.17
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="imcms" uri="imcms" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Imcms Demo Page</title>
    <!--style files-->
    <link rel="stylesheet" href="${contextPath}/demo/css/demo.css">
    <imcms:admin/>
</head>
<body>

<div class="imcms-info-msg">Move your mouse up!</div>
<div class="imcms-info-msg">Implemented admin panel buttons: "Public", "Edit", "Page Info" and "Document"</div>

<div class="imcms-demo-page">
    <imcms:menu index='1'>
        <div class="imcms-demo-page__menu imcms-demo-menu">
            <imcms:menuLoop>
                <div class="imcms-demo-menu__menu-item imcms-demo-menu-item${hasChildren?' imcms-demo-menu__menu-item--parent':''}${isCurrent?' imcms-demo-menu__menu-item--active':''}">
                    <imcms:menuItemLink classes="imcms-demo-menu-item__text">${menuItem.title}</imcms:menuItemLink>
                    <imcms:menuLoop>
                        <div class="imcms-demo-menu__menu-items imcms-demo-menu__menu-items--child">
                            <div class="imcms-demo-menu__menu-item">
                                <imcms:menuItemLink
                                        classes="imcms-demo-menu-item__text">${menuItem.title}</imcms:menuItemLink>
                            </div>
                        </div>
                    </imcms:menuLoop>
                </div>
            </imcms:menuLoop>
        </div>
    </imcms:menu>
    <div class="imcms-demo-page__content imcms-demo-content">
        <div class="imcms-demo-content__title">Start page</div>

        <div>Text editor, mode="read" example:</div>
        <imcms:text no="4" label="Read mode example" mode="read" post="<br/>"/>

        <div>Text editor, mode="write" example:</div>
        <imcms:text no="4" label="Write mode example" mode="write" post="<br/>"/>

        <br/>
        <div>Text editor, formats="text" example:</div>
        <imcms:text no="5" label="Text format example" formats="text" post="<br/>" rows="4"/>

        <br/>
        <div>Text editor, formats="html" example:</div>
        <imcms:text no="6" label="Text format example" formats="html" post="<br/>"/>

        <imcms:contentLoop index="1">
            <imcms:loop>
                <div class="imcms-demo-content__loop-content demo-loop-content">
                    <imcms:image no="1"/>
                    <div class="demo-loop-content__text-area demo-loop-texts">
                        <div class="demo-loop-texts__text-area demo-text-area demo-text-area--left">
                            <imcms:text no="1" label="Demo loop text 1" rows="4"/>
                        </div>
                        <div class="demo-loop-texts__text-area demo-text-area demo-text-area--right">
                            <imcms:text no="2" label="Demo loop text 2" rows="4"/>
                        </div>
                    </div>
                    <br>
                </div>
            </imcms:loop>
        </imcms:contentLoop>
        <div class="imcms-demo-content__images-texts-demo demo-row">
            <div class="demo-row__column demo-element">
                <imcms:image no="1"/>
                <imcms:text no="1" label="Demo text 1"/>
            </div>
            <div class="demo-row__column demo-element">
                <imcms:image no="2"/>
                <imcms:text no="2" label="Demo text 2" rows="4"/>
            </div>
            <div class="demo-row__column demo-element">
                <imcms:image no="3"/>
                <imcms:text no="3" label="Demo text 3"/>
            </div>
        </div>
    </div>
</div>

</body>
</html>
