Login Tag
=========

In this article:
    - `Introduction`_
    - `Use in template`_

Introduction
------------
``Login`` tag it is a simple on page logging, that help sign in user without redirect on base sing in page.

Use in template
---------------

For configure ``login`` tag in template just consider the code below.

.. code-block:: jsp

    <imcms:login>
        <imcms:loginname attributes="placeholder='Enter your login'"/>

        <imcms:loginpassword/>
    </imcms:login>


Example:
""""""""
.. code-block:: jsp

    <%@taglib prefix="imcms" uri="imcms" %>

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Template</title>
        <meta charset="utf-8"/>
    </head>
    <body>
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
    </body>
    </html>





