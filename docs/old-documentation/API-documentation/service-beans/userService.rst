UserService
===========

Init or get instance UserService over global Imcms.getServices ``Imcms.getServices().getUserService();``

.. warning:: This init instance over Imcms.getServices().getUserService() working from 10 version

.. code-block:: jsp

    User getUser(int id) throws UserNotExistsException;

    UserDTO getUser(String login) throws UserNotExistsException;

    void updateUser(UserDTO updateMe);

    List<UserDTO> getAdminUsers();

    List<UserDTO> getAllActiveUsers();

    List<User> findAll(boolean includeExternal, boolean includeInactive);

    List<User> findByNamePrefix(String prefix, boolean includeInactive);

    List<UserDTO> getUsersByEmail(String email);

    void saveUser(UserFormData userData);

    List<UserDTO> searchUsers(String searchTerm, Set<Integer> withRoles, boolean includeInactive);

    ExternalUser saveExternalUser(ExternalUser user);





