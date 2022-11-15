Admin Related Tags
==================

The *admin on the page* is the user who matches at least one condition:

* is a superadmin.
* has access to the Document Manager.
* has access to the Admin Page.
* can publish the current document.
* can edit some content.
* has access to Page Info.

***********
IfAdmin Tag
***********

The data inside this tag is included only if the user is the *admin*.

.. code-block:: jsp

        <imcms:ifAdmin>
            <div>Text visible to admins</div>
        </imcms:ifAdmin>

*********
Admin Tag
*********

This tag adds scripts and styles that are used by the system if the user is the *admin*. Must be in the ``<head>``.

.. code-block:: jsp

	<imcms:admin/>

*************
Usage example
*************

.. code-block:: jsp

	<%@ page contentType="text/html;charset=UTF-8" %>
	<%@ taglib prefix="imcms" uri="imcms" %>

	<!DOCTYPE html>
	<html lang="en">
		<head>
    			<meta charset="UTF-8">
    			<title>Demo Page</title>
    			<link rel="stylesheet" href="${contextPath}/demo/css/demo.css">

    			<!--script and styles which are used by the system if the user is an admin-->
    			<imcms:admin/>
		</head>
		<body>

	    		<div>Text visible to everyone</div>

	    		<imcms:ifAdmin>
        			<div>Text visible to admins</div>
    			</imcms:ifAdmin>
		</body>
	</html>
