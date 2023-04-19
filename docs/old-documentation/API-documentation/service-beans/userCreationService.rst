UserCreationService
===================

Init or get instance UserCreationService over global Imcms.getServices ``Imcms.getServices().getManagedBean(UserCreationService.class)``

Use API
-------

.. code-block:: jsp

    void createUser(final UserFormData userData) throws UserValidationException;

Example usages
""""""""""""""
.. code-block:: jsp

    UserService userService = Imcms.getServices().getUserService();

    UserFormData userData = userService.getUserData(int userId);

    Imcms.getServices().getManagedBean(UserCreationService.class).createUser(userData);
