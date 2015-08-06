Register Tag
============


In this article:
    - `Introduction`_
    - `Use in template`_

Introduction
------------
``Register`` tag it is a simple on page sign up provider, that help register user in the ImCMS system without
redirect on base sing up page.

Use in template
---------------

For configure ``register`` tag in template just consider the code below.

.. code-block:: jsp

    <imcms:registration>
        <imcms:registrationlogin/>
        <imcms:registrationemail/>
        <imcms:registrationname/>
        <imcms:registrationsurname/>
        <imcms:registrationpassword1/>
        <imcms:registrationpassword2/>
        <button class="positive" type="submit">Register</button>
    </imcms:registration>


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
            <label>Name</label>
            <imcms:registrationname/>
        </div>
        <div class="field">
            <label>Second Name</label>
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
    </body>
    </html>
