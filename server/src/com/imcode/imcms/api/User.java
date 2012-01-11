package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;

/**
 * Class describing imcms user
 */
public class User {
    private UserDomainObject internalUser;

    imcode.server.user.UserDomainObject getInternal() {
        return internalUser;
    }

    User( UserDomainObject internalUser ) {
        this.internalUser = internalUser;
    }

    /**
     * Returns user id
     * @return user id
     */
    public int getId() {
        return internalUser.getId();
    }

    /**
     * Returns user login name
     * @return login name
     */
    public String getLoginName() {
        return internalUser.getLoginName();
    }

    /**
     * Returns user company
     * @return user company
     */
    public String getCompany() {
        return internalUser.getCompany();
    }

    /**
     * Returns user first name
     * @return first name
     */
    public String getFirstName() {
        return internalUser.getFirstName();
    }

    /**
     * Returns user last name
     * @return last name
     */
    public String getLastName() {
        return internalUser.getLastName();
    }

    /**
     * Returns user title(Mr., Mrs etc)
     * @return user title
     */
    public String getTitle() {
        return internalUser.getTitle();
    }

    /**
     * Returns address
     * @return address
     */
    public String getAddress() {
        return internalUser.getAddress();
    }

    /**
     * Returns user city
     * @return user city
     */
    public String getCity() {
        return internalUser.getCity();
    }

    /**
     * Returns zip code
     * @return zip code
     */
    public String getZip() {
        return internalUser.getZip();
    }

    /**
     * Returns country
     * @return country
     */
    public String getCountry() {
        return internalUser.getCountry();
    }

    /**
     * @deprecated Use {@link #getProvince()}. Will be removed in 4.0.
     */
    public String getCountyCouncil() {
        return internalUser.getProvince();
    }

    /**
     * Returns province
     * @return province
     */
    public String getProvince() {
        return internalUser.getProvince();
    }

    /**
     * Sets province
     * @param province a string representing a province
     */
    public void setProvince(String province) {
        internalUser.setProvince(province);
    }

    /**
     * Returns user's email address
     * @return email address
     */
    public String getEmailAddress() {
        return internalUser.getEmailAddress();
    }

    public String getOtherPhone() {
        return internalUser.getOtherPhone();
    }

    public String getWorkPhone() {
        return internalUser.getWorkPhone();
    }

    public String getMobilePhone() {
        return internalUser.getMobilePhone();
    }

    public String getHomePhone() {
        return internalUser.getHomePhone();
    }

    /**
     * Indicates if the user is active
     * @return true if the user is active, false othewise
     */
    public boolean isActive() {
        return internalUser.isActive();
    }

    /**
     * Returns a String representation of this user, in form of user's login name.
     * @return a string representing this user.
     */
    public String toString() {
        return getLoginName();
    }

    /**
     * Tests if this user possesses the given role
     * @since 2.0
     * @return true if the user has the given role
     */
    public boolean hasRole(Role role) {
        return internalUser.hasRoleId(role.getInternal().getId()) ;
    }

    /**
     * Tests if this user is a default one
     * @return true if this user is a default one, false otherwise
     */
    public boolean isDefaultUser() {
        return internalUser.isDefaultUser() ;
    }

    /**
     * Tests if this user is a super admin, meaning, possesses SuperAdmin role.
     * @return true if this user is a super admin, false otherwise
     */
    public boolean isSuperAdmin() {
        return internalUser.isSuperAdmin() ;
    }

    /**
     * Tests if this user is a user admin, meaning, possesses UserAdmin role.
     * @return true if this user is a user admin, false otherwise
     */
    public boolean isUserAdmin() {
        return internalUser.isUserAdmin();
    }

    /**
     * Tests if this user can edit the given document
     * @param document Document to be tested for this user's ability to be edited
     * @return true if this user can edit the given document, false otherwise
     */
    public boolean canEdit(Document document) {
        return internalUser.canEdit(document.getInternal()) ;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof User ) ) {
            return false;
        }

        final User user = (User)o;

        if ( internalUser != null ? !internalUser.equals( user.internalUser ) : user.internalUser != null ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return internalUser != null ? internalUser.hashCode() : 0;
    }

    /**
     * Returns user roles
     * @since 2.0
     * @return an array of Roles this user has.
     */
    public Role[] getRoles() {
        RoleId[] roleDOs = internalUser.getRoleIds();
        Role[] roles = new Role[roleDOs.length];
        for ( int i = 0; i < roleDOs.length; i++ ) {
            roles[i] = new Role(Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getRole(roleDOs[i]));
        }
        return roles ;
    }

    /**
     * Replaces user's roles with the given ones
     * @since 2.0
     * @param roles an array of roles to replace user's roles with
     */
    public void setRoles(Role[] roles) {
        RoleId[] roleIds = new RoleId[roles.length];
        for ( int i = 0; i < roles.length; i++ ) {
            roleIds[i] = roles[i].getInternal().getId();
        }
        internalUser.setRoleIds( roleIds );
    }

    /**
     * Adds a role to this user
     * @since 2.0
     * @param role a Role to add
     */
    public void addRole(Role role) {
        internalUser.addRoleId(role.getInternal().getId());
    }

    /**
     * Removes the given role from this user
     * @since 2.0
     * @param role a Role to remove
     */
    public void removeRole(Role role) {
        internalUser.removeRoleId(role.getInternal().getId()) ;
    }

    /**
     * Sets this user's active status
     * @param active true to make this user active, false to make inactive
     */
    public void setActive( boolean active ) {
        internalUser.setActive( active );
    }

    /**
     * Sets this user's address
     * @param address user address
     */
    public void setAddress( String address ) {
        internalUser.setAddress( address );
    }

    /**
     * Sets this user's city
     * @param city user city
     */
    public void setCity( String city ) {
        internalUser.setCity( city );
    }

    /**
     * Sets this user's company
     * @param company user company
     */
    public void setCompany( String company ) {
        internalUser.setCompany( company );
    }

    /**
     * Sets this user's country
     * @param country user country
     */
    public void setCountry( String country ) {
        internalUser.setCountry( country );
    }

    /**
     * @deprecated Use {@link #setProvince(String)}. Will be removed in 4.0.
     */
    public void setCountyCouncil( String countyCouncil ) {
        internalUser.setProvince( countyCouncil );
    }

    /**
     * Sets this user's email address
     * @param emailAddress user email address
     */
    public void setEmailAddress( String emailAddress ) {
        internalUser.setEmailAddress( emailAddress );
    }

    /**
     * Sets this user's fax number
     * @param faxphone a string representing user's fax number
     */
    public void setFaxPhone( String faxphone ) {
        internalUser.setFaxPhone( faxphone );
    }

    /**
     * Returns this user's fax number
     * @return user's fax number
     */
    public String getFaxPhone() {
        return internalUser.getFaxPhone();
    }

    /**
     * Sets this user's first name
     * @param firstName user first name
     */
    public void setFirstName( String firstName ) {
        internalUser.setFirstName( firstName );
    }

    /**
     * Sets this user's home phone number
     * @param homephone a string representing this user's home phone number
     */
    public void setHomePhone( String homephone ) {
        internalUser.setHomePhone( homephone );
    }

    /**
     * Sets this user's last name
     * @param lastName last name
     */
    public void setLastName( String lastName ) {
        internalUser.setLastName( lastName );
    }

    /**
     * Sets this user's login name
     * @param loginName login name
     */
    public void setLoginName( String loginName ) {
        internalUser.setLoginName( loginName );
    }

    /**
     * Sets this user's mobile phone number
     * @param mobilephone a string representing this user's mobile phone number
     */
    public void setMobilePhone( String mobilephone ) {
        internalUser.setMobilePhone( mobilephone );
    }

    /**
     * Sets this user 'other' phone number. A number that doens't fall into any phone number category.
     * Often used as storage for meta_id connected to the user.
     * @param otherphone a string representing any phone.
     */
    public void setOtherPhone( String otherphone ) {
        internalUser.setOtherPhone( otherphone );
    }

    /**
     * Sets user's password
     * @param password user password
     */
    public void setPassword( String password ) {
        internalUser.setPassword( password );
    }

    /**
     * Sets user's title (ie Mr., Mrs etc)
     * @param title user's title
     */
    public void setTitle( String title ) {
        internalUser.setTitle( title );
    }

    /**
     * Sets user's work phone number
     * @param workphone work phone number
     */
    public void setWorkPhone( String workphone ) {
        internalUser.setWorkPhone( workphone );
    }

    /**
     * Sets user's zip code
     * @param zip user zip code
     */
    public void setZip( String zip ) {
        internalUser.setZip( zip );
    }

    /**
     * Sets user language. User language is used in some areas of imcms for internationalization.
     * @param language Language used by this user
     */
    public void setLanguage( Language language ) {
        internalUser.setLanguageIso639_2( language.getIsoCode639_2() );
    }

    /**
     * Returns user's language.
     * @return Language used by the user
     */
    public Language getLanguage() {
        return Language.getLanguageByISO639_2( internalUser.getLanguageIso639_2() ) ;
    }

    /**
     * Returns session id associated with this user.
     * @return a String with user session id
     */
    public String getSessionId() {
        return internalUser.getSessionId();
    }

    /**
     * Sets user's session id
     * @param sessionId String do be used as this user's session id
     */
    public void setSessionId(String sessionId) {
        internalUser.setSessionId(sessionId);
    }
}