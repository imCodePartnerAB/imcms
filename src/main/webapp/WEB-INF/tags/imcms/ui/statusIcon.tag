<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="imcms" uri="imcms" %>
<%@ attribute name="lifeCyclePhase" required="true" type="imcode.server.document.LifeCyclePhase" %>

<jsp:include page="${imcms:getStatusIconTemplatePath(lifeCyclePhase)}"/>
