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

<h3>Example pages</h3>
<p>Warning!</p>
<p>
The following links works differently depending on the user's permissions on the corresponding admin-pages. That is, what
user you are logged in as, and what administrative permissions that user has.<br>
That means that even though you don't specify a user in most of the API:s method, usually the current logged in user
is passed as an invisible parameter.
</p>

<h4>Exeption handling in jsp-pages</h4>
- NotLoggedInException, NoPermissionException and other.
<p>
If the user that is accessing the page doesn't have the right permissions a NoPermissionException is thrown from the methods.
There are two normal ways to handle this. <BR>
1. In the jsp pages page-tag set the errorpage attribute and let that page handle the response to the user<BR>
2. Use try/catch in a normal Java way.<BR>
In this sample page, the first choice is made; see <%=makeLinks("error.jsp")%> files for details.


<h4>Who you are</h4>
To see what user you are logged in as, <%= makeLinks("logged_in_user.jsp") %><br>
<br>
First, make sure that you are logged in, <a href="../../../login/index.html">../../../login/index.html</a><br>
Log out (and become an non-logged in user), <a href="../../../servlet/LogOut">../../../servlet/LogOut</a>
</p>

<h4>Users</h4>
To see what users that exists in the system, <%= makeLinks("user_listing.jsp") %><br>

<h4>Roles</h4>
List all roles, <%= makeLinks("role_list_all.jsp") %><br>
Add, <%= makeLinks("role_create_role.jsp") %><br>
Delete, <%= makeLinks("role_delete_role.jsp") %><br>
<br>
List users with different roles, <%= makeLinks("role_get_user_with_role.jsp") %><br>

<h4>Document</h4>
The following samples modify document 1001.<br>
It currently looks like this, <a href="../servlet/GetDoc?meta_id=1001">document</a><br>
<br>
Get the information about the document, <%= makeLinks("document_get_document_info.jsp") %><br>
Change the document information, <%= makeLinks("document_change_document_info.jsp") %><br>
<br>
List all possible document categories, <%= makeLinks("categories_show_all.jsp") %><br>
<br>
Show template for a document, <%= makeLinks("document_show_templates.jsp") %><br>
Change template for a document, <%= makeLinks("document_change_template.jsp") %><br>
Set template for a document to "Start", <%= makeLinks("document_change_template_by_name.jsp") %><br>
Set template for a document to one with id=1, <%= makeLinks("document_change_template_by_id.jsp") %><br>
<br>
Get the first text field from the document, <%= makeLinks("document_get_text_from_field.jsp") %><br>
Change the first and the second field, <%= makeLinks("document_set_text_field.jsp") %><br>
Clear the first and the second field, </a><%= makeLinks("document_clear_text_field.jsp") %><br>
<br>
Get image no 2 from the document, <%= makeLinks("document_get_image.jsp") %><br>
Set image no 2 in the document, use one image from the image folder, <%= makeLinks("document_set_image.jsp") %><br>
<br>
Get the first menu from the document, <%= makeLinks("document_get_menu.jsp") %><br>
Add a document to a menu, <%= makeLinks("document_add_to_menu.jsp") %><br>
Remove a document from a menu, <%= makeLinks("document_remove_from_menu.jsp") %><br>
Set the sort order of all menus in a document, <%= makeLinks("document_set_sortorder.jsp") %><br>
<br>
Get the first include from the document, <%= makeLinks("document_get_include.jsp") %><br>
Change the first include, <%= makeLinks("document_set_include.jsp") %><br>
Clear the first include, <%= makeLinks("document_clear_include.jsp") %><br>
<br>
To create different kinds of document that is linked from the document above, <%= makeLinks("document_create_documents.jsp") %><br>
<br>

<h4>Searching for documents</h4>
Searching for documents, <%= makeLinks("document_search.jsp") %><br>

<h4>Documents and permissions </h4>
You can manipulate documents in various ways. To do so the current user has to have specific roles, that in turn has to
have specific rights. <%= makeLinks("document_permissions.jsp") %><br>
Set the permissions for a role on a document, <%= makeLinks("document_set_permissions.jsp") %><br>
<br>
There are two different levels of configurable "restricted" permissions that can be modified.
To see what specified "Restricted One" permissions a page has, see <%= makeLinks("document_permission_show_restricted_1.jsp") %><br>

<h4>Database connection</h4>
Get access to the database, <%= makeLinks("database_get_connection.jsp") %><br>

</body>
</html>


