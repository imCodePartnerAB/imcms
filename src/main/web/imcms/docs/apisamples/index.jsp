<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>imCMS public API samples</title>
</head>
<%!
    private String makeLinks(String path) {
        String execute = "<a href=\""+path+"\">"+path+"</a>";
        String source = "<a href=\""+path+".txt\">source</a>";
        return " execute " + execute +", view " + source;
    }
%>
<body>
<h1>imCMS - API documentation </h1>
<p>
imCMS comes with an API for usage in servlets and jsp-pages, see <a href="../api/">the javadocs</a>.<br>
Also, linked from this document are differens examples on how to use the api. Follow the links below.

<h3>Who you are</h3>
To see what user you are logged in as, <%= makeLinks("logged_in_user.jsp") %><br>
<br>
First, make sure that you are logged in, <a href="<%= request.getContextPath() %>/login/"><%= request.getContextPath() %>/login/</a><br>
Log out (and become an non-logged in user), <a href="<%= request.getContextPath() %>/servlet/LogOut"><%= request.getContextPath() %>/servlet/LogOut</a>
</p>

<h3>Users</h3>
To create a user, <%= makeLinks( "user_create_user.jsp") %><br>
To see what users that exists in the system, <%= makeLinks("user_listing.jsp") %><br>
To get attributes for one user by login name or by user id, <%=makeLinks("user_get_user.jsp") %><br>

<h3>Roles</h3>
List all roles, <%= makeLinks("role_list_all.jsp") %><br>
Add, <%= makeLinks("role_create_role.jsp") %><br>
Delete, <%= makeLinks("role_delete_role.jsp") %><br>
Rename, <%= makeLinks("role_edit_role.jsp") %><br>
<br>
List users with different roles, <%= makeLinks("role_get_user_with_role.jsp") %><br>

<h3>Documents</h3>
The following samples modify document 1001.<br>
It currently looks like this, <a href="<%= request.getContextPath() %>/servlet/GetDoc?meta_id=1001"><%= request.getContextPath() %>/servlet/GetDoc?meta_id=1001</a><br>
<br>
Get the information about the document, <%= makeLinks("document_get_document_info.jsp") %><br>
Change the document information, <%= makeLinks("document_change_document_info.jsp") %><br>
<br>
Show template for a document, <%= makeLinks("document_show_templates.jsp") %><br>
Change template for a document, <%= makeLinks("document_change_template.jsp") %><br>
Set template for a document to "Start", <%= makeLinks("document_change_template_by_name.jsp") %><br>
Set template for a document to one with id=1, <%= makeLinks("document_change_template_by_id.jsp") %><br>
List all possible templates and template groups, <%= makeLinks("templates_show_all.jsp") %><br>
<br>
Get the first text field from the document, <%= makeLinks("document_get_texts_from_fields.jsp") %><br>
Change the first and the second field, <%= makeLinks("document_set_text_field.jsp") %><br>
Clear the first and the second field, </a><%= makeLinks("document_clear_text_field.jsp") %><br>
<br>
Get image no 2 from the document, <%= makeLinks("document_get_images.jsp") %><br>
Set image no 2 in the document, use one image from the image folder, <%= makeLinks("document_set_image.jsp") %><br>
<br>
Get the first menu from the document, <%= makeLinks("document_get_menu.jsp") %><br>
Add a document to a menu, <%= makeLinks("document_add_to_menu.jsp") %><br>
Remove a document from a menu, <%= makeLinks("document_remove_from_menu.jsp") %><br>
Set the sort order of all menus in a document, <%= makeLinks("document_set_sortorder.jsp") %><br>
<br>
Get the first include from the document, <%= makeLinks("document_get_includes.jsp") %><br>
Change the first include, <%= makeLinks("document_set_include.jsp") %><br>
Clear the first include, <%= makeLinks("document_clear_include.jsp") %><br>
<br>
To create different kinds of document that is linked from the document above, <%= makeLinks("document_create_documents.jsp") %><br>
<br>
To list files in file-documents, <%= makeLinks("file_document_search.jsp") %><br>

<h3>Categories</h3>
List all possible document categories, <%= makeLinks("categories_show_all.jsp") %><br>
Create a new category type and a category, <%= makeLinks("category_create.jsp") %><br>
Change the name of a category, <%= makeLinks( "category_edit.jsp" ) %><br>

<h3>Searching for documents</h3>
Searching for documents, <%= makeLinks("document_search.jsp") %><br>

<h3>Documents and permissions </h3>
You can manipulate documents in various ways. To do so the current user has to have specific roles, that in turn has to
have specific rights. <%= makeLinks("document_permissions.jsp") %><br>
Set the permissions for a role on a document, <%= makeLinks("document_set_permissions.jsp") %><br>
<br>
There are two different levels of configurable "restricted" permissions that can be modified.
To see what specified "Restricted One" permissions a page has, see <%= makeLinks("document_permission_show_restricted_1.jsp") %><br>

<h3>Sending mail</h3>
To send a file-document as attachments in a mail, <%= makeLinks("mail_send_file_document.jsp") %><br>

<h3>Database connection</h3>
Get access to the database, <%= makeLinks("database_get_connection.jsp") %><br>

</body>
</html>


