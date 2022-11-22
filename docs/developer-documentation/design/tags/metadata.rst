Metadata Tag
============

Activate metadata support in template. Without this metadata functionality does not apply to the document.

Must be in the ``<head>``. It is important to insert this tag beyond all other ones.

.. code-block:: jsp

    <imcms:metadata/>

.. seealso:: Read how to manage meta in the :doc:`Text Document Management </user-documentation/document-management/page-info/text-document>` article.

********************
Additional meta name
********************

If you want to add new meta name you need to add them using database. Use ``imcms_html_meta_tags`` table.
You need to specify only tag name.

.. code-block:: sql

    INSERT INTO imcms_html_meta_tags (name) VALUE ('metaTagName');

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
    		<!-- inserts metas -->
            <imcms:metadata/>

    		<title>Demo Page</title>
            <link rel="stylesheet" href="${contextPath}/demo/css/demo.css">
		</head>

		<body>
		</body>
	</html>