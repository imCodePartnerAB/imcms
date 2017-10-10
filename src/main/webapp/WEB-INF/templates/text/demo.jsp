<%@ page pageEncoding="UTF-8" %>

<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<imcms:contextPath/>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <base href="${contextPath}">
    <title>${currentDocument.headline} - Powered by imCMS from imCode Partner AB</title>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/imcms/css/template/demo.css"/>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/2.1.1/js.cookie.min.js"></script>
</head>
<body>
<div class="container">
    <section class="header">
        <div class="wrapper">
            <div class="menu">
                <!-- Example of ImCMS menu tag -->
                <imcms:menu no='1' docId="1001" label="Example">
                    <ul>
                        <imcms:menuLoop>
                            <li>
                                <imcms:menuItemLink>
                                    ${menuItem.document.headline}
                                </imcms:menuItemLink>
                                <!-- sub menu definition -->
                                <imcms:menuLoop>
                                    <div>
                                        <imcms:menuItemLink>
                                            ${menuItem.document.headline}
                                        </imcms:menuItemLink>
                                    </div>
                                </imcms:menuLoop>
                            </li>
                        </imcms:menuLoop>
                    </ul>
                </imcms:menu>
            </div>
            <div class="auth">
                <c:choose>
                    <c:when test="${not user.defaultUser}">
                        <div class="info">
                            <div class="user-name">${user.loginName}</div>
                            <div class="sign-out">
                                <imcms:logout>
                                    Sign Out
                                </imcms:logout>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="tabs">
                            <section>
                                <div class="tab" selected data-item="1">Sign In</div>
                                <div class="page" data-item="1" selected>
                                    <h1>Sign In</h1>
                                    <imcms:login>
                                        <div class="field">
                                            <label>Login</label>
                                            <imcms:loginname attributes="placeholder='Enter your login'"/>
                                        </div>
                                        <div class="field">
                                            <label>Password</label>
                                            <imcms:loginpassword/>
                                        </div>
                                        <input type="hidden" name="login" value="login"/>

                                        <div class="field">
                                            <button class="positive" type="submit">Login</button>
                                        </div>
                                    </imcms:login>
                                </div>
                            </section>
                            <section>
                                <div class="tab" data-item="2">Sign Up</div>
                                <div class="page" data-item="2">
                                    <h1>Sign Up</h1>
                                    <imcms:registration>
                                        <div class="field">
                                            <label>Login</label>
                                            <imcms:registrationlogin/>
                                        </div>
                                        <div class="field">
                                            <label>Email</label>
                                            <imcms:registrationemail/>
                                        </div>
                                        <div class="field">
                                            <label>First Name</label>
                                            <imcms:registrationname/>
                                        </div>
                                        <div class="field">
                                            <label>Last Name</label>
                                            <imcms:registrationsurname/>
                                        </div>
                                        <div class="field">
                                            <label>Password</label>
                                            <imcms:registrationpassword1/>
                                        </div>
                                        <div class="field">
                                            <label>Repeat password</label>
                                            <imcms:registrationpassword2/>
                                        </div>
                                        <div class="field">
                                            <button class="positive" type="submit">Register</button>
                                        </div>
                                    </imcms:registration>
                                </div>
                            </section>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>
    <section class="content">
        <div class="wrapper">
            <h1>${currentDocument.headline}</h1>

            <imcms:text label="test label" no="1" document="${currentDocument.id}"/>

            <h2>Learn more</h2>

            <imcms:text no="2" document="1001" mode="read" pre=""/>
            <imcms:text label="label test 2" no="4" formats="text"/>
        </div>
        <div class="wrapper">
            <imcms:contentLoop index="1">
                <imcms:loop>
                    <div class="figure">
                        <imcms:image no="1" document="${currentDocument.id}" style="max-width:100px;"/>
                        <div class="description">
                            <imcms:text no="1" document="${currentDocument.id}" label="loop_1" formats="CLEANHTML"/>
                        </div>
                    </div>
                </imcms:loop>
            </imcms:contentLoop>
        </div>
    </section>

    <section class="footer-placeholder">
        <section class="footer">
            <div class="wrapper">
                <div class="additional">
                    <imcms:text no="3" document="1001" label="label test once more"
                                placeholder="<i>now empty text field can be filled with attr `placeholder`</i>"/>
                </div>
                <div class="logo">
                    <imcms:image no="1" document="1001"/>
                </div>
            </div>
        </section>
    </section>
    <imcms:admin/>
</div>
</body>
</html>
