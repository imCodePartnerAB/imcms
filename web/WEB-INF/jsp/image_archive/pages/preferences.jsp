<%@ include file="/WEB-INF/jsp/image_archive/includes/taglibs.jsp" %>
<spring:message var="title" code="archive.title.preferences" htmlEscape="true"/>
<spring:message var="pageHeading" code="archive.pageHeading.preferences" htmlEscape="true"/>
<c:set var="currentPage" value="preferences"/>
<c:set var="javascript">
    <script type="text/javascript">
        initPreferences();
    </script>
</c:set>
<%@ include file="/WEB-INF/jsp/image_archive/includes/header.jsp" %>
<%@ include file="/WEB-INF/jsp/image_archive/includes/top.jsp" %>

<div id="containerContent">
    <h4>
        <spring:message code="archive.preferences.changeRole" htmlEscape="true"/>
    </h4><div class="hr"></div>
    <c:url var="saveCategoriesUrl" value="/web/archive/preferences/role/save"/>
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
                    <option value="${role.id}" ${currentRole.id eq role.id ? 'selected="selected"' : ''} ><c:out value="${role.roleName}"/></option>
                </c:forEach>
            </select>
        </div>
        
        <div class="minH20">
            <input type="checkbox" id="canUse" name="canUse" ${currentRole.canUseImage ? 'checked="checked"' : ''} style="margin-right:5px;"/>
            <label for="canUse">
                <spring:message code="archive.preferences.canUseImages" htmlEscape="true"/>
            </label>
        </div>
        
        <div class="minH20">
            <input type="checkbox" id="canChange" name="canChange" ${currentRole.canChangeImage ? 'checked="checked"' : ''} style="margin-right:5px;"/>
            <label for="canChange">
                <spring:message code="archive.preferences.canChangeImages" htmlEscape="true"/>
            </label>
        </div><br/>
        
        <div>
            <div class="left">
                <label for="freeCategories">
                    <spring:message code="archive.preferences.categories" htmlEscape="true"/>
                </label><br/>
                <select id="freeCategories" size="6" style="width:190px;" multiple="multiple">
                    <c:forEach var="category" items="${freeCategories}" varStatus="status">
                        <option value="${category.id}"><c:out value="${category.name}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="left" style="padding:40px 10px;">
                <spring:message var="rightText" code="archive.moveRight" htmlEscape="true"/>
                <spring:message var="leftText" code="archive.moveLeft" htmlEscape="true"/>
                <input id="addCategory" type="button" value="${rightText}" style="width:30px;" class="btnBlue small"/><br/><br/>
                <input id="deleteCategory" type="button" value="${leftText}" style="width:30px;" class="btnBlue small"/>
            </div>
            <div class="left">
                <label for="assignedCategories">
                    <spring:message code="archive.preferences.assignedCategories" htmlEscape="true"/>
                </label><br/>
                <select id="assignedCategories" size="6" style="width:190px;" multiple="multiple">
                    <c:forEach var="category" items="${roleCategories}">
                        <option value="${category.id}"><c:out value="${category.name}"/></option>
                    </c:forEach>
                </select>
            </div>

            <div class="clearboth m10t" style="text-align:center;">
                <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                <input id="saveCategories" type="submit" value="${saveText}" class="btnBlue"/>
            </div>
        </div>
    </form:form>
    
    <h4>
        <spring:message code="archive.preferences.libraries.changeLibraryRoles" htmlEscape="true"/>
    </h4><div class="hr"></div>
    <c:url var="saveLibraryUrl" value="/web/archive/preferences/library/save"/>
    <form:form action="${saveLibraryUrl}" commandName="saveLibraryRoles" method="post" cssClass="m15t clearfix">
        <c:choose>
            <c:when test="${currentLibrary eq null}">
                <h3><spring:message code="archive.preferences.libraries.noLibrariesCreated" htmlEscape="true"/></h3>
            </c:when>
            <c:otherwise>
                <div class="minH30">
                    <span class="left" style="width:110px;">
                        <label for="library"><spring:message code="archive.preferences.libraries.libraryFolder" htmlEscape="true"/></label>
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
                        <label for="libraryNm"><spring:message code="archive.preferences.libraries.libraryName" htmlEscape="true"/></label>
                    </span>
                    <div class="left">
                        <form:input path="libraryNm" id="libraryNm" maxlength="120" htmlEscape="true"/><br/>
                        <form:errors path="libraryNm" cssClass="red"/>
                    </div>
                </div><br/><br/>

                <div class="left">
                    <label for="availableLibraryRoles"><spring:message code="archive.preferences.libraries.roles" htmlEscape="true"/></label><br/>
                    <select id="availableLibraryRoles" multiple="true" size="6" style="width:190px;">
                        <c:forEach var="role" items="${availableLibraryRoles}">
                            <option value="${role.id}"><c:out value="${role.roleName}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="left" style="padding:60px 10px;">
                    <spring:message var="addText" code="archive.preferences.libraries.add" htmlEscape="true"/>
                    <input type="button" id="addLibraryRole" value="${addText}" class="btnBlue small"/>
                </div>
                
                <input type="hidden" id="libraryRolesStr" name="libraryRolesStr" value=""/>
                <spring:message var="deleteText" code="archive.preferences.libraries.delete" htmlEscape="true"/>
                <input type="hidden" id="deleteText" value="${deleteText}"/>
                <div class="left">
                    <label for="libraryRolesTbl"><spring:message code="archive.preferences.libraries.assignedRoles" htmlEscape="true"/></label>
                    <table id="libraryRolesTbl">
                        <tr>
                            <td colspan="4"><div class="hr" style="margin:0;"></div></td>
                        </tr>
                        <tr>
                            <td class="minW60"></td>
                            <td class="minW60" style="text-align:center;">
                                <spring:message code="archive.preferences.libraries.use" htmlEscape="true"/>
                            </td>
                            <td class="minW60" style="text-align:center;">
                                <spring:message code="archive.preferences.libraries.change" htmlEscape="true"/>
                            </td>
                            <td class="minW60"></td>
                        </tr>
                        <c:forEach var="libraryRole" items="${libraryRoles}">
                            <c:set var="id" value="${libraryRole.roleId}"/>
                            <tr id="libraryRoleRow_${id}">
                                <td class="minW60"><c:out value="${libraryRole.roleName}"/></td>
                                <td class="minW60" style="text-align:center;">
                                    <input type="radio" name="permission_${id}" value="0" ${libraryRole.canUse ? 'checked="checked"' : ''} />
                                </td>
                                <td class="minW60" style="text-align:center;">
                                    <input type="radio" name="permission_${id}" value="1" ${libraryRole.canChange ? 'checked="checked"' : ''} />
                                </td>
                                <td class="minW60" style="text-align:center;">
                                    <input type="button" id="deleteLibraryRole_${id}" value="${deleteText}" class="btnBlue small"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>

                <div class="clearboth m10t" style="text-align:center">
                    <spring:message var="saveText" code="archive.save" htmlEscape="true"/>
                    <input id="saveLibraryRoles" type="submit" value="${saveText}" class="btnBlue"/>
                </div>
            </c:otherwise>
        </c:choose>
    </form:form>
</div>

<%@ include file="/WEB-INF/jsp/image_archive/includes/footer.jsp" %>