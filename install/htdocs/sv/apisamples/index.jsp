<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>imCMS public API samples</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<%!
    private String makeLink(String path) {
        return "<a href=\""+path+"\">"+path+"</a>" ;
    }
%>
<body>
<H1>imCMS public API samples</H1>

<h2> Documentation </h2>
<p>
The documentation consists of this page and the jsp-pages that this page links to.
For documentation on the different classes that is used in the jsp-pages, <br>
see the <a href="docs/">java doc</a> for details.
</p>
<p>
There is also a demo-template for the tags,
which lists all tags you can use in a text-document, and what they do, <%= makeLink("alltags_template.html") %>.
</p>
<h3>Warning!</h3>
The following links works differently depending on the user's permissions on the corresponding admin-pages. That is, what
user you are logged in as, and what administrative permissions that user has.<br>
That means that even though you don't specify a user in most of the API:s method, usually the current logged in user
is passed as an invisible parameter.<br>

<H3>NotLoggedInException, NoPermissionException and exception handling in jsp-pages</H3>
<p>
If the user that is accessing the page doesn't have the right permissions a NoPermissionException is thrown from the methods.
There are two normal ways to handle this. <BR>
1. In the jsp pages page-tag set the errorpage attribute and let that page handle the response to the user<BR>
2. Use try/catch in a normal Java way.<BR>
In this sample page, the first choice is made; see <%=makeLink("error.jsp")%> files for details.

<h3>How to use this examples</h3>
When you click on a jsp-link below that page is going to be run, and produce a result.<br>
To see the actual code, pleas open the page in your favourite java/jsp editing environment.<br>

<h3>Who you are</h3>
First, make sure that you are logged in, <%= makeLink("../login/index.html") %><br>
To see what user you are logged in as, <%= makeLink("logged_in_user.jsp") %><br>
Log out (and become an non-logged in user), <%= makeLink("../servlet/LogOut") %>
</p>

<h3>Users</h3>
To see what users that exists in the system, <%= makeLink("user_listing.jsp") %><br>

<h3>Roles</h3>
List all roles, <%= makeLink("role_list_all.jsp") %><br>
Add, <%= makeLink("role_create_role.jsp") %><br>
Delete, <%= makeLink("role_delete_role.jsp") %><br>
<br>
List users with different roles, <%= makeLink("role_get_user_with_role.jsp") %><br>

<h3>Document</h3>
The following samples modify document 1001.<br>
It currently looks like this, <a href="../servlet/GetDoc?meta_id=1001">document</a><br>
<br>
Get the information about the document, <%= makeLink("document_get_document_info.jsp") %><br>
Change the document information, <%= makeLink("document_change_document_info.jsp") %><br>
<br>
List all possible document categories, <%= makeLink("categories_show_all.jsp") %><br>
<br>
Show template for a document, <%= makeLink("document_show_templates.jsp") %><br>
Change template for a document, <%= makeLink("document_change_template.jsp") %><br>
Set template for a document to "Start", <%= makeLink("document_change_template_by_name.jsp") %><br>
Set template for a document to one with id=1, <%= makeLink("document_change_template_by_id.jsp") %><br>
<br>
Get the first text field from the document, <%= makeLink("document_get_text_from_field.jsp") %><br>
Change the first and the second field, <%= makeLink("document_set_text_field.jsp") %><br>
Clear the first and the second field, </a><%= makeLink("document_clear_text_field.jsp") %><br>
<br>
Get image no 2 from the document, <%= makeLink("document_get_image.jsp") %><br>
Set image no 2 in the document, use one image from the image folder, <%= makeLink("document_set_image.jsp") %><br>
<br>
Get the first menu from the document, <%= makeLink("document_get_menu.jsp") %><br>
Add a document to a menu, <%= makeLink("document_add_to_menu.jsp") %><br>
Remove a document from a menu, <%= makeLink("document_remove_from_menu.jsp") %><br>
Set the sort order of all menus in a document, <%= makeLink("document_set_sortorder.jsp") %><br>
<br>
Get the first include from the document, <%= makeLink("document_get_include.jsp") %><br>
Change the first include, <%= makeLink("document_set_include.jsp") %><br>
Clear the first include, <%= makeLink("document_clear_include.jsp") %><br>
<br>
To create different kinds of document that is linked from the document above, <%= makeLink("document_create_documents.jsp") %><br>
<br>

<h3>Searching for documents</h3>
Searching for documents, <%= makeLink("document_search.jsp") %><br>

<h3>Documents and permissions </h3>
You can manipulate documents in various ways. To do so the current user has to have specific roles, that in turn has to
have specific rights. <%= makeLink("document_permissions.jsp") %><br>
Set the permissions for a role on a document, <%= makeLink("document_set_permissions.jsp") %><br>
<br>
There are two different levels of configurable "restricted" permissions that can be modified.
To see what specified "Restricted One" permissions a page has, see <%= makeLink("document_permission_show_restricted_1.jsp") %><br>

<h3>Database connection</h3>
Get access to the database, <%= makeLink("database_get_connection.jsp") %><br>

<h3>Performance test data creation </h3>
Get access to the database, <%= makeLink("createPerformanceTestData.jsp") %><br>

</body>
</html>


