<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Simple jsp page</title>
    <style type="text/css">
        .imageArchive {
            float: right;
        }

        /* slightly enhanced, universal clearfix hack */
.clearfix:after {
     visibility: hidden;
     display: block;
     font-size: 0;
     content: " ";
     clear: both;
     height: 0;
     }
.clearfix { display: inline-block; }
/* start commented backslash hack \*/
* html .clearfix { height: 1%; }
.clearfix { display: block; }
/* close commented backslash hack */

    #lightbox {
        position: absolute;
        top: 10%;
        left: 50%;
        width: 500px;
        background: #fff;
        z-index: 1001;
        display: none;
    }

    #lightbox-shadow {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: #000;
        filter: alpha(opacity=75);
        -moz-opacity: 0.75;
        -khtml-opacity: 0.75;
        opacity: 0.75;
        z-index: 1000;
        display: none;
    }
    </style>
</head>
<body>
<div><imcms:admin/></div>
<div class="imageArchiveHead">
    <imcms:image no="1" label="<br/>Logo<br/>" style="logo"/>
    <imcms:text no="1" pre="<span>" post="</span>"/>
</div>
<div id="overlay">
</div>
<div style="border:1px solid gray" class="clearfix">
<div style="float:left;width:20%;">
    <imcms:menu no='1' label='<br/><br/>Meny (punktlista)'>
        <ul>
            <imcms:menuloop>
                <imcms:menuitem>
                    <li style="padding-bottom:5px; color:green;"><imcms:menuitemlink><c:out
                            value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
                </imcms:menuitem>
                <imcms:menuitem>
                    <imcms:menuitemhide>
                        <li style="padding-bottom:5px; color:red;"><imcms:menuitemlink><c:out
                                value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
                    </imcms:menuitemhide>
                </imcms:menuitem>
            </imcms:menuloop>
        </ul>
    </imcms:menu>
</div>
<imcms:imageArchive styleClass="imageArchive"><link href="${contextPath}/css/tag_image_archive.css.jsp" rel="stylesheet" type="text/css" /></imcms:imageArchive>
</div>
</body>
</html>