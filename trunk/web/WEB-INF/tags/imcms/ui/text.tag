<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="ui" tagdir="/WEB-INF/tags/imcms/ui" %><%@
        attribute name="id" required="true" %><%@
        attribute name="value" required="true" %><%@
        attribute name="maxlength"%><%@
        attribute name="size"%>
<input type="text" id="${id}" name="${id}" value="${value}" maxlength="${maxlength}" size="${size}"/>
