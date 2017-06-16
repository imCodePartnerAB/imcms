<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.preferences" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.preferences" htmlEscape="true"/>
<spring:message var="editCategoryBtnText" code="archive.preferences.category.edit" htmlEscape="true"/>
<spring:message var="saveCategoryBtnText" code="archive.preferences.category.save" htmlEscape="true"/>
<spring:message var="deleteCategoryBtnText" code="archive.preferences.category.delete" htmlEscape="true"/>
<spring:message var="cancelCategoryBtnText" code="archive.preferences.category.cancel" htmlEscape="true"/>
<spring:message var="show" code="archive.preferences.show"/>
<spring:message var="hide" code="archive.preferences.hide"/>
<c:set var="currentPage" value="preferences"/>

<c:set var="javascript">
    <script type="text/javascript">
        initPreferences();

        function toggleIndicators(colapsableLabel) {
            var indicator = $(colapsableLabel).find(".indicator");

            if(indicator.hasClass('unfolded')) {
                indicator.removeClass('unfolded');
                indicator.addClass('folded');
                indicator.text('${show}');
            } else {
                indicator.removeClass('folded');
                indicator.addClass('unfolded');
                indicator.text('${hide}');
            }

            $(".colapsableLabel").not(colapsableLabel).each(function(){
                var otherIndicator = $(this).find('.indicator');
                otherIndicator.removeClass('unfolded');
                otherIndicator.addClass('folded');
                otherIndicator.text('${show}');
            });
        }

        $(document).ready(function() {
            var contentToHide = $(".contentToHide");
            <c:if test="${editingCategories}">
                contentToHide = contentToHide.not($("#contentToHideCategories"));
            </c:if>
            <c:if test="${editingRoles}">
                contentToHide = contentToHide.not($("#contentToHideRoles"));
            </c:if>
            <c:if test="${editingLibraries}">
                contentToHide = contentToHide.not($("#contentToHideLibraries"));
            </c:if>
            contentToHide.hide();

            $(".colapsableLabel").click(function() {
                var parent = $(this).parent();
                $(".contentToHide").not(parent.find(".contentToHide")).hide();

                if(parent.find(".contentToHide:hidden").length > 0) {
                    parent.find(".contentToHide").show();
                    $(this).removeClass("folded");
                    $(this).addClass("unfolded");
                } else {
                    parent.find(".contentToHide").hide();
                    $(this).removeClass("folded");
                    $(this).addClass("unfolded");
                }

                toggleIndicators(this);
            });


            var editBtn = $('<button class="imcmsFormBtnSmall inBtnGroup" type="button" name="edit">${editCategoryBtnText}</button>');
            var saveBtn = $('<button class="imcmsFormBtnSmall inBtnGroup" type="submit" name="saveCategoryAction">${saveCategoryBtnText}</button>');
            var deleteBtn = $('<button class="imcmsFormBtnSmall inBtnGroup" type="submit" name="removeCategoryAction">${deleteCategoryBtnText}</button>');
            var cancelBtn = $('<button class="imcmsFormBtnSmall inBtnGroup" type="button" name="cancel">${cancelCategoryBtnText}</button>');
            var categoryOldName;
            var editCategoryId;

            function cancelEditing(row) {
                // presence of any of save/delete/cancel buttons means editing
                if (row.find("button[name=cancel]").length > 0) {
                    var categoryName = row.find("input[type=text]");
                    categoryName.attr("disabled", "disabled");
                    categoryName.toggleClass("disabled");
                    categoryName.val(categoryOldName);
                    var controls = row.find(".controls");
                    controls.empty();
                    controls.append(editBtn.clone(true));
                }
            }

            editBtn.click(function() {
                var thisRow = $(this).parent().parent();
                // cancel editing on all rows
                thisRow.parent().find("tr").each(function() {
                    cancelEditing($(this));
                });

                var categoryName = thisRow.find("input[type=text]");
                editCategoryId.val(categoryName.attr("data-categoryId"));
                categoryName.removeAttr("disabled");
                categoryName.toggleClass("disabled");
                categoryOldName = categoryName.val();
                var controls = $(this).parent();
                controls.empty();
                controls.append(saveBtn.clone(true), deleteBtn.clone(true), cancelBtn.clone(true));
            });

            cancelBtn.click(function() {
                var thisRow = $(this).parent().parent();
                cancelEditing(thisRow);
            });

            $(".controls").append(editBtn.clone(true));
            editCategoryId = $("#editCategoryId");
        });
    </script>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
<div class="preferencesSection">
    <h4 class="colapsableLabel imcmsAdmHeading clearfix">
        <span class="left"><spring:message code="archive.preferences.categories" htmlEscape="true"/></span>
        <span class="right indicator ${editingCategories ? 'unfolded' : 'folded'}">${editingCategories ? hide : show}</span>
    </h4>

    <div class="contentToHide" id="contentToHideCategories">
        <h4 class="m15t">
            <spring:message code="archive.preferences.createNewCategory" htmlEscape="true"/>
        </h4>

        <c:url var="preferencesUrl" value="/web/archive/preferences"/>

        <form:form action="${preferencesUrl}" commandName="createCategory" method="post" cssClass="m15t clearfix">
            <div class="minH30 clearfix">
                <span class="left" style="width:65px;">
                    <label for="createCategoryName"><spring:message code="archive.preferences.name"
                                                                    htmlEscape="true"/></label>
                </span>

                <div class="left">
                    <form:input path="createCategoryName" id="createCategoryName" maxlength="128"
                                cssStyle="width:180px;"/>
                    <spring:message var="createText" code="archive.preferences.create" htmlEscape="true"/>
                    <input type="submit" name="createCategoryAction" value="${createText}" style="margin-left:3px;" class="imcmsSpecialButton"/>
                    <br/>
                    <form:errors path="createCategoryName" cssClass="red"/>
                </div>
            </div>
        </form:form>

        <h4 class="m15t">
            <spring:message code="archive.preferences.editCategory" htmlEscape="true"/>
        </h4>

        <c:url var="preferencesUrl" value="/web/archive/preferences"/>
        <form:form action="${preferencesUrl}" commandName="editCategory" method="post" cssClass="m15t clearfix">
            <input type="hidden" name="editCategoryId" id="editCategoryId"/>

            <div class="clearfix left">
                <table class="editCategoryTable tablesorter" cellpadding="0" cellspacing="1">
                    <thead>
                        <tr>
                            <th><spring:message code="archive.preferences.category" htmlEscape="true"/></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="category" items="${categories}" varStatus="categoryStatus">
                        <tr>
                            <td>
                                <input class="disabled" name="editCategoryName" data-categoryId="${category.id}" type="text"
                                       value="<c:out value="${category.name}"/>" disabled/>
                            </td>
                            <td class="controls">
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </form:form>
    </div>
</div>

<div class="preferencesSection">
    <h4 class="colapsableLabel imcmsAdmHeading clearfix">
        <span class="left"><spring:message code="archive.preferences.categories.roles" htmlEscape="true"/></span>
        <span class="right indicator ${editingCategories ? 'unfolded' : 'folded'}">${editingRoles ? hide : show}</span>
    </h4>
    <div class="contentToHide" id="contentToHideRoles">
        <h4 class="m15t">
            <spring:message code="archive.preferences.changeRole" htmlEscape="true"/>
        </h4>

        <c:url var="saveCategoriesUrl" value="/web/archive/preferences"/>
        <form:form action="${saveCategoriesUrl}" commandName="saveCategories" method="post" cssClass="m15t clearfix">
            <input type="hidden" id="categoryIds" name="categoryIds" value=""/>

            <div class="minH30">
                <span class="left" style="width:40px;">
                    <label for="roles">
                        <spring:message code="archive.preferences.role" htmlEscape="true"/>
                    </label>
                </span>
                <select id="roles" class="left" style="width:130px;">
                    <c:forEach var="role" items="${roles}">
                        <option value="${role.id}" ${currentRole.id eq role.id ? 'selected="selected"' : ''} ><c:out
                                value="${role.roleName}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="clearfix">
                <div class="left" style="width:500px;">
                    <table class="roleTable tablesorter" cellpadding="0" cellspacing="1">
                        <thead>
                            <tr>
                                <th class="labelCell">
                                    <spring:message code="archive.preferences.category" htmlEscape="true"/>
                                </th>
                                <th style="text-align:center;">
                                    <label for="allRolesCanUseCategory"><spring:message code="archive.preferences.useImages" htmlEscape="true"/></label>
                                    <input type="checkbox" class="allCanUse" id="allRolesCanUseCategory"/>
                                </th>
                                <th style="text-align:center;">
                                    <label for="allRolesCanEditCategory"><spring:message code="archive.preferences.editAddImages" htmlEscape="true"/></label>
                                    <input type="checkbox" class="allCanEdit" id="allRolesCanEditCategory"/>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="category" items="${allCategories}">
                                <tr class="dataRow">
                                    <td>
                                        <label for="catId${category.id}"><c:out value="${category.name}"/></label>
                                        <input id="catId${category.id}" type="hidden" value="${category.id}" disabled/>
                                    </td>
                                    <td class="useCell">
                                        <c:set var="canUse" value="false"/>
                                        <c:set var="canChange" value="false"/>
                                        <c:forEach var="catRole" items="${categoryRoles}">
                                            <c:if test="${catRole.categoryId eq category.id}">
                                                <c:if test="${catRole.canUse}">
                                                    <c:set var="canUse" value="true"/>
                                                </c:if>
                                                <c:if test="${catRole.canChange}">
                                                    <c:set var="canChange" value="true"/>
                                                </c:if>
                                            </c:if>
                                        </c:forEach>
                                        <input class="use" type="checkbox" ${canUse ? "checked='checked'" : ""}/>
                                    </td>
                                    <td class="editCell">
                                        <input class="edit" type="checkbox" ${canChange ? "checked='checked'" : ""}/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="clearboth m10t" style="text-align:right;">
                        <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                        <input id="saveCategoriesBtn" type="submit" name="saveRoleCategoriesAction" value="${saveText}"
                               class="imcmsFormBtn"/>
                    </div>

                    <div class="clearboth m10t" style="text-align:left;">
                        <c:url var="addNewRoleButtonURL" value="/servlet/AdminRoles"/>
                        <a href="${addNewRoleButtonURL}" target="blank">
                            <spring:message code="archive.preferences.libraries.addNewRole" htmlEscape="true"/></a>
                        <span> <spring:message code="archive.preferences.libraries.addNewRoleHint" htmlEscape="true"/></span>
                    </div>
                </div>
            </div>
        </form:form>
    </div>
</div>

<div class="preferencesSection">
    <h4 class="colapsableLabel imcmsAdmHeading clearfix">
        <span class="left"><spring:message code="archive.preferences.libraries.libraries" htmlEscape="true"/></span>
        <span class="right indicator ${editingCategories ? 'unfolded' : 'folded'}">${editingLibraries ? hide : show}</span>
    </h4>

    <div class="contentToHide" id="contentToHideLibraries">
        <h4 class="m15t">
            <spring:message code="archive.preferences.libraries.changeLibraryRoles" htmlEscape="true"/>
        </h4>

        <c:url var="saveLibraryUrl" value="/web/archive/preferences"/>
        <form:form action="${saveLibraryUrl}" commandName="saveLibraryRoles" method="post" cssClass="m15t clearfix">
            <c:choose>
                <c:when test="${currentLibrary eq null}">
                    <h3><spring:message code="archive.preferences.libraries.noLibrariesCreated" htmlEscape="true"/></h3>
                </c:when>
                <c:otherwise>
                    <div class="minH30">
                        <span class="left" style="width:110px;">
                            <label for="library"><spring:message code="archive.preferences.libraries.libraryFolder"
                                                                 htmlEscape="true"/></label>
                        </span>
                        <select class="left" id="library" style="width:130px;">
                            <c:forEach var="library" items="${libraries}">
                                <option value="${library.id}" ${currentLibrary.id eq library.id ? 'selected="selected"' : ''}>
                                    <c:out value="${library.folderNm}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="minH30">
                        <span class="left" style="width:110px;">
                            <label for="libraryNm"><spring:message code="archive.preferences.libraries.libraryName"
                                                                   htmlEscape="true"/></label>
                        </span>

                        <div class="left">
                            <form:input path="libraryNm" id="libraryNm" maxlength="120" htmlEscape="true"/><br/>
                            <form:errors path="libraryNm" cssClass="red"/>
                        </div>
                    </div>

                    <div class="left" style="width:500px;margin-top:20px;">
                        <table class="libraryCategoriesTable tablesorter" cellpadding="0" cellspacing="1">
                            <thead>
                                <tr>
                                    <th class="widerLabelCell"><spring:message code="archive.preferences.role" htmlEscape="true"/></th>
                                    <th style="text-align:center;">
                                        <label for="allCanUseLibrary"><spring:message code="archive.preferences.useImages" htmlEscape="true"/></label>
                                        <input type="checkbox" class="allCanUse" id="allCanUseLibrary"/>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="role" items="${availableLibraryRoles}">
                                    <tr class="dataRow">
                                        <td>
                                            <label for="roleId${role.id}"><c:out value="${role.roleName}"/></label>
                                            <input id="roleId${role.id}" value="${role.id}" type="hidden" disabled/>
                                        </td>
                                        <td class="useCell">
                                            <c:set var="canUse" value="false"/>
                                            <c:set var="canChange" value="false"/>
                                            <c:forEach var="libRole" items="${libraryRoles}">
                                                <c:if test="${libRole.roleId eq role.id}">
                                                    <c:if test="${libRole.canUse}">
                                                        <c:set var="canUse" value="true"/>
                                                    </c:if>
                                                    <c:if test="${libRole.canChange}">
                                                        <c:set var="canChange" value="true"/>
                                                    </c:if>
                                                </c:if>
                                            </c:forEach>
                                            <input class="use" type="checkbox" ${canUse ? "checked='checked'" : ""}/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <input type="hidden" id="libraryRolesStr" name="libraryRolesStr" value=""/>
                        <spring:message var="deleteText" code="archive.preferences.libraries.delete" htmlEscape="true"/>
                        <input type="hidden" id="deleteText" value="${deleteText}"/>

                        <div class="clearboth m10t" style="text-align:right">
                            <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                            <input id="saveLibraryRolesBtn" type="submit" name="saveLibraryRolesAction" value="${saveText}"
                                   class="imcmsFormBtn"/>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </form:form>
    </div>
</div>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>