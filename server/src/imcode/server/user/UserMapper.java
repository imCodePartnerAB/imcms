package imcode.server.user;

public interface UserMapper {

    void addUser( UserDomainObject user, UserDomainObject currentUser );

    void initUserRoles( UserDomainObject user );

    void initUserPhoneNumbers( UserDomainObject user );

    void initUserAttributes( UserDomainObject user );
}