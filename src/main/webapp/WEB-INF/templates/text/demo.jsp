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
	<imcms:metadata/>
	<title>Imcms Demo Page</title>
    <!--style files-->
    <link rel="stylesheet" href="${contextPath}/demo/css/demo.css">
    <imcms:admin/>
	<imcms:templateCSS/>
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

        <div>imcms:image tag without style attribute</div>
        <imcms:image no="100"/>
        <imcms:image:data no="100">
            <div>
                <b>Image data:</b>
                <div>Image alternateText: ${alternateText}</div>
                <div>Image descriptionText: ${descriptionText}</div>
            </div>
        </imcms:image:data>
        <br>

        <div>imcms:image tag with attribute style="width: 100px;"</div>
        <imcms:image no="101" style="width: 100px;"/>

        <div>imcms:image tag with attribute style="max-width: 120px;"</div>
        <imcms:image no="102" style="max-width: 120px;"/>

        <div>imcms:image tag with attribute style="height: 100px;"</div>
        <imcms:image no="103" style="height: 100px;"/>

        <div>imcms:image tag with attribute style="max-height: 120px;"</div>
        <imcms:image no="104" style="max-height: 120px;"/>

        <div>imcms:image tag with attribute style="width: 100px; height: 100px;"</div>
        <imcms:image no="105" style="width: 100px; height: 100px;"/>

        <div>imcms:image tag with attribute style="max-width: 120px; max-height: 120px;"</div>
        <imcms:image no="106" style="max-width: 120px; max-height: 120px;"/>

        <div>Text editor, mode="read" example:</div>
        <imcms:text no="4" label="Read mode example" mode="read" post="<br/>"/>

        <div>Text editor, mode="write" example:</div>
        <imcms:text no="4" label="Write mode example" mode="write" post="<br/>"/>
        <%--if need that be placeholder works need add any formats--%>

        <br/>
        <div>Text editor, formats="text" example:</div>
        <imcms:text no="5" label="Text format example" formats="text" post="<br/>" rows="4"/>

        <br/>
        <div>Text editor, formats="html", rows="1", placeholder="text2" example:</div>
        <imcms:text no="6" label="HTML format 1 row example" formats="html" rows="1" post="<br/>" placeholder="text2"/>

        <br/>
        <div>Text editor, rows="1" with param showEditToSuperAdmin=true example:</div>
        <imcms:text no="7" label="1 row text example" formats="text" post="<br/>" rows="1" showEditToSuperAdmin="true"/>

        <br/>
        <div>Text for doc 1001:</div>
        <imcms:text document="1001" no="8" label="Read-only text for all non-1001 docs" formats="text" post="<br/>"/>

        <br/>
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

        <br/>
        <br/>
        <imcms:contentLoop index="10" document="1001">
            <imcms:loop>
                <div>#${entryIndex} Loop example for doc 1001</div>
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

        <br/>
        <div>Image for doc 1001:</div>
        <imcms:image no="30" document="1001"/>
    </div>

    <br/>
    <div>Menu for doc 1001:</div>
    <imcms:menu index='10' document="1001">
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
</div>

</body>
</html>