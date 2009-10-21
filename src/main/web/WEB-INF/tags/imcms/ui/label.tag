<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@
        attribute name="idref" required="true" %><%@
        attribute name="key" required="true" %>
<label for="${idref}"><fmt:message key="${key}"/></label>
