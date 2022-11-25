TemplateCSS Tag
===============

This tag inserts custom css styles. Without this Template CSS functionality does not apply to the document.

Must be in the ``<head>``.
It is important to place It under ``<imcms:admin/>`` tag because you might want that your styles will not be overwritten.

.. code-block:: jsp

    <imcms:templateCSS/>

.. seealso:: Read also the :doc:`Template CSS </user-documentation/admin-settings/template-css>` article.

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

    		<imcms:admin/>
    		<!-- inserts custom css styles -->
    		<imcms:templateCSS/>
		</head>

		<body>
		</body>
	</html>