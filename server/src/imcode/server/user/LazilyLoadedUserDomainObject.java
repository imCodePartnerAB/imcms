package imcode.server.user;

import imcode.server.Imcms;

public class LazilyLoadedUserDomainObject extends UserDomainObject {

    private boolean attributesLoaded;
    private boolean phoneNumbersLoaded;
    private boolean rolesLoaded;

    public LazilyLoadedUserDomainObject( int id ) {
        this(id, false) ;
    }

    public LazilyLoadedUserDomainObject( int id, boolean loadAttributes ) {
        this.id = id ;
        this.attributesLoaded = !loadAttributes ;
    }

    private void loadAttributes() {
        if ( attributesLoaded ) {
            return;
        }
        attributesLoaded = true;
        if ( 0 != id ) {
            UserMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
            userMapper.initUserAttributes( this );
        }
    }

    private void loadPhoneNumbers() {
        if ( phoneNumbersLoaded ) {
            return;
        }
        phoneNumbersLoaded = true;
        if ( 0 != id ) {
            UserMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
            userMapper.initUserPhoneNumbers( this );
        }
    }

    private void loadRoles() {
        if ( rolesLoaded ) {
            return;
        }
        rolesLoaded = true;
        if ( 0 != id ) {
            UserMapper userMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
            userMapper.initUserRoles( this );
        }
    }

    public void addRole( RoleDomainObject role ) {
        loadRoles();
        super.addRole( role );
    }

    public String getAddress() {
        loadAttributes() ;
        return super.getAddress();
    }

    public String getCity() {
        loadAttributes();
        return super.getCity();
    }

    public String getCompany() {
        loadAttributes();
        return super.getCompany();
    }

    public String getCountry() {
        loadAttributes();
        return super.getCountry();
    }

    public String getCountyCouncil() {
        loadAttributes();
        return super.getCountyCouncil();
    }

    public String getCreateDate() {
        loadAttributes();
        return super.getCreateDate();
    }

    public String getEmailAddress() {
        loadAttributes();
        return super.getEmailAddress();
    }

    public String getFaxPhone() {
        loadPhoneNumbers();
        return super.getFaxPhone();
    }

    public String getFirstName() {
        loadAttributes();
        return super.getFirstName();
    }

    public String getHomePhone() {
        loadPhoneNumbers();
        return super.getHomePhone();
    }

    public int getLangId() {
        loadAttributes();
        return super.getLangId();
    }

    public String getLanguageIso639_2() {
        loadAttributes();
        return super.getLanguageIso639_2();
    }

    public String getLastName() {
        loadAttributes();
        return super.getLastName();
    }

    public String getLoginName() {
        loadAttributes();
        return super.getLoginName();
    }

    public String getMobilePhone() {
        loadPhoneNumbers();
        return super.getMobilePhone();
    }

    public String getOtherPhone() {
        loadPhoneNumbers();
        return super.getOtherPhone();
    }

    public String getPassword() {
        loadAttributes();
        return super.getPassword();
    }

    public RoleDomainObject[] getRoles() {
        loadRoles();
        return super.getRoles();
    }

    public String getTitle() {
        return super.getTitle();
    }

    public String getWorkPhone() {
        loadPhoneNumbers();
        return super.getWorkPhone();
    }

    public String getZip() {
        loadAttributes();
        return super.getZip();
    }

    public boolean hasRole( RoleDomainObject role ) {
        loadRoles();
        return super.hasRole( role );
    }

    public boolean isActive() {
        loadAttributes();
        return super.isActive();
    }

    public boolean isImcmsExternal() {
        loadAttributes();
        return super.isImcmsExternal();
    }

    public void removeRole( RoleDomainObject role ) {
        loadRoles();
        super.removeRole( role );
    }

    public void setActive( boolean active ) {
        loadAttributes();
        super.setActive( active );
    }

    public void setAddress( String address ) {
        loadAttributes();
        super.setAddress( address );
    }

    public void setCity( String city ) {
        loadAttributes();
        super.setCity( city );
    }

    public void setCompany( String company ) {
        loadAttributes();
        super.setCompany( company );
    }

    public void setCountry( String country ) {
        loadAttributes();
        super.setCountry( country );
    }

    public void setCountyCouncil( String countyCouncil ) {
        loadAttributes();
        super.setCountyCouncil( countyCouncil );
    }

    public void setCreateDate( String create_date ) {
        loadAttributes();
        super.setCreateDate( create_date );
    }

    public void setEmailAddress( String emailAddress ) {
        loadAttributes();
        super.setEmailAddress( emailAddress );
    }

    public void setFaxPhone( String faxphone ) {
        loadPhoneNumbers();
        super.setFaxPhone( faxphone );
    }

    public void setFirstName( String firstName ) {
        loadAttributes();
        super.setFirstName( firstName );
    }

    public void setHomePhone( String homephone ) {
        loadPhoneNumbers();
        super.setHomePhone( homephone );
    }

    public void setImcmsExternal( boolean external ) {
        loadAttributes();
        super.setImcmsExternal( external );
    }

    public void setLangId( int lang_id ) {
        loadAttributes();
        super.setLangId( lang_id );
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        loadAttributes();
        super.setLanguageIso639_2( languageIso639_2 );
    }

    public void setLastName( String lastName ) {
        loadAttributes();
        super.setLastName( lastName );
    }

    public void setLoginName( String loginName ) {
        loadAttributes();
        super.setLoginName( loginName );
    }

    public void setMobilePhone( String mobilephone ) {
        loadPhoneNumbers();
        super.setMobilePhone( mobilephone );
    }

    public void setOtherPhone( String otherphone ) {
        loadPhoneNumbers();
        super.setOtherPhone( otherphone );
    }

    public void setPassword( String password ) {
        loadAttributes();
        super.setPassword( password );
    }

    public void setRoles( RoleDomainObject[] rolesForUser ) {
        loadRoles();
        super.setRoles( rolesForUser );
    }

    public void setTitle( String title ) {
        loadAttributes();
        super.setTitle( title );
    }

    public void setWorkPhone( String workphone ) {
        loadPhoneNumbers();
        super.setWorkPhone( workphone );
    }

    public void setZip( String zip ) {
        loadAttributes();
        super.setZip( zip );
    }
}
