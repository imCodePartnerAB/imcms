package com.imcode.imcms.api;

import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;

import java.util.Collection;
import java.util.Iterator;

public class User {
    private UserDomainObject internalUser;

    imcode.server.user.UserDomainObject getInternal() {
        return internalUser;
    }

    User( UserDomainObject internalUser ) {
        this.internalUser = internalUser;
    }

    public int getId() {
        return internalUser.getId();
    }

    public String getLoginName() {
        return internalUser.getLoginName();
    }

    public String getCompany() {
        return internalUser.getCompany();
    }

    public String getFirstName() {
        return internalUser.getFirstName();
    }

    public String getLastName() {
        return internalUser.getLastName();
    }

    public String getTitle() {
        return internalUser.getTitle();
    }

    public String getAddress() {
        return internalUser.getAddress();
    }

    public String getCity() {
        return internalUser.getCity();
    }

    public String getZip() {
        return internalUser.getZip();
    }

    public String getCountry() {
        return internalUser.getCountry();
    }

    public String getCountyCouncil() {
        return internalUser.getCountyCouncil();
    }

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

    public boolean isActive() {
        return internalUser.isActive();
    }

    public String toString() {
        return getLoginName();
    }

    /**
     * @since 2.0
     */
    public boolean hasRole(Role role) {
        return internalUser.hasRole( role.getInternal() ) ;
    }

    public boolean isDefaultUser() {
        return internalUser.isDefaultUser() ;
    }

    public boolean isSuperAdmin() {
        return internalUser.isSuperAdmin() ;
    }

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
     * @since 2.0
     */
    public Role[] getRoles() {
        RoleDomainObject[] roleDOs = internalUser.getRoles();
        Role[] roles = new Role[roleDOs.length];
        for ( int i = 0; i < roleDOs.length; i++ ) {
            roles[i] = new Role(roleDOs[i]);
        }
        return roles ;
    }

    /**
     * @since 2.0
     */
    public void setRoles(Role[] roles) {
        RoleDomainObject[] roleDOs = new RoleDomainObject[roles.length];
        for ( int i = 0; i < roles.length; i++ ) {
            roleDOs[i] = roles[i].getInternal();
        }
        internalUser.setRoles( roleDOs );
    }

    /**
     * @since 2.0
     */
    public void addRole(Role role) {
        internalUser.addRole( role.getInternal() );
    }

    /**
     * @since 2.0
     */
    public void removeRole(Role role) {
        internalUser.removeRole( role.getInternal() ) ;
    }

    public void setActive( boolean active ) {
        internalUser.setActive( active );
    }

    public void setAddress( String address ) {
        internalUser.setAddress( address );
    }

    public void setCity( String city ) {
        internalUser.setCity( city );
    }

    public void setCompany( String company ) {
        internalUser.setCompany( company );
    }

    public void setCountry( String country ) {
        internalUser.setCountry( country );
    }

    public void setCountyCouncil( String countyCouncil ) {
        internalUser.setCountyCouncil( countyCouncil );
    }

    public void setEmailAddress( String emailAddress ) {
        internalUser.setEmailAddress( emailAddress );
    }

    public void setFaxPhone( String faxphone ) {
        internalUser.setFaxPhone( faxphone );
    }

    public String getFaxPhone() {
        return internalUser.getFaxPhone();
    }

    public void setFirstName( String firstName ) {
        internalUser.setFirstName( firstName );
    }

    public void setHomePhone( String homephone ) {
        internalUser.setHomePhone( homephone );
    }

    public void setLastName( String lastName ) {
        internalUser.setLastName( lastName );
    }

    public void setLoginName( String loginName ) {
        internalUser.setLoginName( loginName );
    }

    public void setMobilePhone( String mobilephone ) {
        internalUser.setMobilePhone( mobilephone );
    }

    public void setOtherPhone( String otherphone ) {
        internalUser.setOtherPhone( otherphone );
    }

    public void setPassword( String password ) {
        internalUser.setPassword( password );
    }

    public void setTitle( String title ) {
        internalUser.setTitle( title );
    }

    public void setWorkPhone( String workphone ) {
        internalUser.setWorkPhone( workphone );
    }

    public void setZip( String zip ) {
        internalUser.setZip( zip );
    }

    public void setLanguage( Language language ) {
        internalUser.setLanguageIso639_2( language.getIsoCode639_2() );
    }

    public Language getLanguage() {
        return Language.getLanguageByISO639_2( internalUser.getLanguageIso639_2() ) ;
    }

}
