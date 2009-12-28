<%@ page
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
%>
<%@taglib prefix="imcms" uri="imcms"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<imcms:text no="1" label="Text (Rubrik)" pre='<div class="imcHeading">' post='</div>' />
<imcms:text no='2' label='<br>Text' post='<br><br>' />
<imcms:menu no='1' label='<br><br>Meny (punktlista)'>
    <ul>
        <imcms:menuloop>
            <imcms:menuitem>
                <li style="color: green;"><imcms:menuitemlink><c:out value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
            </imcms:menuitem>
            <imcms:menuitem>
                <imcms:menuitemhide>
                    <li style="color: red;"><imcms:menuitemlink><c:out value="${menuitem.document.headline}"/></imcms:menuitemlink></li>
                </imcms:menuitemhide>
            </imcms:menuitem>
        </imcms:menuloop>
    </ul>
</imcms:menu>
<imcms:include url="@documentationwebappurl@/servlet/GetDoc?meta_id=1054&template=imcmsDemoContent" pre='<hr>' post='<hr>'/>
<imcms:image no='3' label='Bild' pre='<br><br>' post='<br>'/><br>
<imcms:include no='1' label='Dynamisk inkludering 1'/>