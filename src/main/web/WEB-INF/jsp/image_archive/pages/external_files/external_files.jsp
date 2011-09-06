<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.externalFiles" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.externalFiles" htmlEscape="true"/>
<c:set var="currentPage" value="externalFiles"/>
<c:set var="javascript">
    <c:if test="${activate}">
        <%@ include file="/WEB-INF/jsp/image_archive/pages/fragments/jscalendar.jsp" %>
    </c:if>
    <script type="text/javascript">
        $(document).ready(function(){
            initExternalFiles();
        });
    </script>
    <script type="text/javascript">

        var folded = $('<img src="${pageContext.servletContext.contextPath}/css/tree/folded.png"/>');
        var unfolded = $('<img src="${pageContext.servletContext.contextPath}/css/tree/unfolded.png"/>');
        var blank = $('<img class="blank" src="${pageContext.servletContext.contextPath}/css/tree/blank.png"/>');

        function toggleVisibility() {
            $("#listOfLibraries li:not(:has(ul))").each(function() {
                var indicator = $(this).find(" > img");
                if (!indicator.length) {
                    $(this).prepend(blank.clone());
                }
            });

            $("#listOfLibraries li ul:hidden").each(function() {
                var parent = $(this).parent();
                var indicator = parent.find(" > img");
                if (indicator.length) {
                    indicator.attr("src", folded.attr("src"));
                } else {
                    parent.prepend(folded.clone());
                }
            });

            $("#listOfLibraries li ul:visible").each(function() {
                var parent = $(this).parent();
                var indicator = parent.find(" > img");
                if (indicator.length) {
                    indicator.attr("src", unfolded.attr("src"));
                } else {
                    parent.prepend(unfolded.clone());
                }
            })
        }

        $(document).ready(function() {
            toggleVisibility();

            $("#listOfLibraries li img[class != 'blank']").click(function(event) {
                event.stopPropagation();
                $(" > ul", $(this).parent()).toggle();
                toggleVisibility();
            });

            /* google dictionary extension on chrome seems to throw an exception.
            *
            * many lines instead of one to prevent tablesorter exception in case of an empty table */

            if($("#fileNames td").length > 0) {
                $("#fileNames").tablesorter({sortList : [[${sortBy.ordinal}, ${sortBy.direction.ordinal}]],
                headers: { 0 : {sorter:false}, 1 : {sorter:false}}});
//                $("#fileNames").bind("sortEnd", function(){
//                    var even = $("#fileNames tr").removeClass("odd");
//                   var tableRow = $("#fileNames tr:odd");
//                    if(!tableRow.hasClass("odd")) {
//                        tableRow.addClass("odd");
//                    }
//                });
            } else {
                $("#fileNames").tablesorter({headers: { 0 : {sorter:false}, 1 : {sorter:false}}});
            }

            $(".fileName").each(function(){
                var name = $(this).parent().find(":checkbox").val();
                $(this).qtip({
                  content: {
                      text: function(api) {
                        return '';
                        },
                      ajax: {
                        url: '${pageContext.servletContext.contextPath}/web/archive/external-files/preview-tooltip',
                        type: 'GET',
                        data: { id : $("#libraryId").val(), name : name}
                      }
                  },
                show: {
                    effect: false,
                    solo: true
                },
                    position: {
                    my: 'top left',
                    at: 'center center',
                    effect: false
                }


                });
            });
        });

    </script>
</c:set>
<c:set var="css">
    <c:if test="${activate}">
        <link rel="stylesheet" type="text/css" href="${pageContext.servletContext.contextPath}/imcms/jscalendar/skins/aqua/theme.css.jsp"/>
    </c:if>
    <link href="${pageContext.servletContext.contextPath}/js/jquery.uploadify-v2.1.4/uploadify.css" rel="stylesheet" type="text/css" />
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
    <c:choose>
        <c:when test="${activate}">
            <%@ include file="/WEB-INF/jsp/image_archive/pages/external_files/activate_image.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/jsp/image_archive/pages/external_files/libraries.jsp" %>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>