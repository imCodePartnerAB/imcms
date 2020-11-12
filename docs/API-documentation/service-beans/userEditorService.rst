UserEditorService
=================


Init or get instance UserEditorService over global Imcms.getServices ``Imcms.getServices().getManagedBean(UserEditorService.class)``


Use API
-------

.. code-block:: jsp

    void editUser(UserFormData userData) throws UserValidationException;


Example usages
""""""""""""""
.. code-block:: jsp

    UserService userService = Imcms.getServices().getUserService();

    UserFormData userData = userService.getUserData(int userId);

    Imcms.getServices().getManagedBean(UserEditorService.class).editUser(userData);