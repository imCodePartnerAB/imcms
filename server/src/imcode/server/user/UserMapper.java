package imcode.server.user;

public interface UserMapper {

    void addUser( UserDomainObject newUser );

    void initUserRoles( UserDomainObject user );

    void initUserPhoneNumbers( UserDomainObject user );

    void initUserAttributes( UserDomainObject user );
}