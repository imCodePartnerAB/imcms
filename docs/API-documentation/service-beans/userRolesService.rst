UserRolesService
================


    List<User> getUsersByRole(Role role);

    List<Role> getRolesByUser(int userId);

    void updateUserRoles(List<? extends Role> roles, User user);