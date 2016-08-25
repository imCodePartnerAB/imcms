<%@ page pageEncoding="UTF-8" %>

<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<imcms:variables/>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <base href="${pageContext.request.contextPath}">
	<title>${document.headline} - Powered by imCMS from imCode Partner AB</title>
	<meta charset="utf-8"/>
	<link rel="stylesheet" href="<%=request.getContextPath()%>/imcms/css/template/demo.css"/>
	<script src="//ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js"></script>
    <script src="${pageContext.request.contextPath}/js/js.cookie.js"></script>
    <script src="${pageContext.request.contextPath}/imcms/${user.language.isoCode639_2}/scripts/imcms_linker.js"></script>
</head>
<body>
<div class="container">
	<section class="header">
		<div class="wrapper">
			<div class="menu">
				<!-- Example of ImCMS menu tag -->
				<imcms:menu no='1' docId="1001" label="Example">
					<ul>
						<imcms:menuloop>
							<imcms:menuitem>
								<li>
									<imcms:menuitemlink>
										${menuitem.document.headline}
									</imcms:menuitemlink>
									<!-- sub menu definition -->
									<imcms:menuloop>
										<imcms:menuitem>
											<div>
												<imcms:menuitemlink>
													${menuitem.document.headline}
												</imcms:menuitemlink>
											</div>
										</imcms:menuitem>
									</imcms:menuloop>
								</li>
							</imcms:menuitem>
						</imcms:menuloop>
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
			<h1>${document.headline}</h1>

			<imcms:text no="1" document="${document.id}"/>

			<h2>Learn more</h2>

            <imcms:text label="asdas" no="2" document="1001" mode="read" pre=""/>
            <imcms:text label="test" no="4" formats="text"/>
		</div>
		<div class="wrapper">
			<imcms:loop no="1">
				<div class="figure">
					<imcms:image no="1" document="${document.id}" style="max-width:100px;"/>
					<div class="description">
						<imcms:text no="1" document="${document.id}"/>
					</div>

				</div>
			</imcms:loop>
		</div>
	</section>

	<section class="footer-placeholder">
		<section class="footer">
			<div class="wrapper">
				<div class="additional">
					<imcms:text no="3" document="1001"
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