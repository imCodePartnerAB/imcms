package imcode.server.user;

import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.ApplicationServer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class UserDomainObject extends Hashtable {

    private String currentContextPath;

    public UserDomainObject() {
        lazilyLoadedUserAttributes = new LazilyLoadedUserAttributes();
        lazilyLoadedUserPhoneNumbers = new LazilyLoadedUserPhoneNumbers();
    }

    UserDomainObject( int id ) {
        this.id = id ;
    }

    private int id;

    private static class LazilyLoadedUserAttributes implements Serializable {

        private String loginName;
        private String password;
        private String firstName = "";
        private String lastName = "";
        private String title = "";
        private String company = "";
        private String address = "";
        private String city = "";
        private String zip = "";
        private String country = "";
        private String county_council = "";
        private String emailAddress = "";
        private int lang_id;
        private boolean active;
        private String create_date;

        private String languageIso639_2;

        private TemplateGroupDomainObject templateGroup;
        private String loginType;

        private boolean imcmsExternal = false;

    }

    private static class LazilyLoadedUserPhoneNumbers implements Serializable {
        private String workPhone = "";
        private String mobilePhone = "";
        private String homePhone = "";
        private String faxPhone = "";
        private String otherPhone = "";
    }

    private static class LazilyLoadedUserRoles implements Serializable {
        private Set roles = new HashSet();
    }

    private LazilyLoadedUserAttributes lazilyLoadedUserAttributes = null ;
    private LazilyLoadedUserPhoneNumbers lazilyLoadedUserPhoneNumbers = null ;
    private LazilyLoadedUserRoles lazilyLoadedUserRoles = null ;

    private LazilyLoadedUserAttributes getLazilyLoadedUserAttributes() {
        if (null == lazilyLoadedUserAttributes) {
            lazilyLoadedUserAttributes = new LazilyLoadedUserAttributes() ;
            if ( 0 != id ) {
                getUserMapper().initUserFromSqlData(this, getUserMapper().sqlSelectUserById(id));
            }
        }
        return lazilyLoadedUserAttributes;
    }

    private LazilyLoadedUserPhoneNumbers getLazilyLoadedUserPhoneNumbers() {
        if (null == lazilyLoadedUserPhoneNumbers) {
            lazilyLoadedUserPhoneNumbers = new LazilyLoadedUserPhoneNumbers() ;
            if ( 0 != id ) {
                getUserMapper().initUserPhoneNumbers(this) ;
            }
        }
        return lazilyLoadedUserPhoneNumbers ;
    }

    private LazilyLoadedUserRoles getLazilyLoadedUserRoles() {
        if (null == lazilyLoadedUserRoles) {
            lazilyLoadedUserRoles = new LazilyLoadedUserRoles() ;
            if (0 != id) {
                getUserMapper().initUserRoles(this) ;
            }
        }
        return lazilyLoadedUserRoles ;
    }

    private ImcmsAuthenticatorAndUserMapper getUserMapper() {
        ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper = ApplicationServer.getIMCServiceInterface().getImcmsAuthenticatorAndUserAndRoleMapper();
        return imcmsAuthenticatorAndUserMapper;
    }

    /**
     * get user-id
     */
    public int getId() {
        return this.id;
    }

    /**
     * set user-id
     */
    public void setId( int id ) {
        if (0 != this.id) {
            getLazilyLoadedUserAttributes() ;
            getLazilyLoadedUserPhoneNumbers() ;
            getLazilyLoadedUserRoles() ;
        }
        this.id = id;
    }

    /**
     * get login name (username)
     */
    public String getLoginName() {
        return this.getLazilyLoadedUserAttributes().loginName;
    }

    /**
     * set login name (username)
     */
    public void setLoginName( String loginName ) {
        this.getLazilyLoadedUserAttributes().loginName = loginName;
    }

    /**
     * get password
     */
    public String getPassword() {
        return this.getLazilyLoadedUserAttributes().password;
    }

    /**
     * set password
     */
    public void setPassword( String password ) {
        this.getLazilyLoadedUserAttributes().password = password;
    }

    /**
     * get full name
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * get first name
     */
    public String getFirstName() {
        return this.getLazilyLoadedUserAttributes().firstName;
    }

    /**
     * set first name
     */
    public void setFirstName( String firstName ) {
        this.getLazilyLoadedUserAttributes().firstName = firstName;
    }

    /**
     * get last name
     */
    public String getLastName() {
        return this.getLazilyLoadedUserAttributes().lastName;
    }

    /**
     * set last name
     */
    public void setLastName( String lastName ) {
        this.getLazilyLoadedUserAttributes().lastName = lastName;
    }

    /**
     * set title
     */
    public void setTitle( String title ) {
        this.getLazilyLoadedUserAttributes().title = title;
    }

    /**
     * get title
     */
    public String getTitle() {
        return this.getLazilyLoadedUserAttributes().title;
    }

    /**
     * set company
     */
    public void setCompany( String company ) {
        this.getLazilyLoadedUserAttributes().company = company;
    }

    /**
     * get company
     */
    public String getCompany() {
        return this.getLazilyLoadedUserAttributes().company;
    }

    /**
     * set address
     */
    public void setAddress( String address ) {
        this.getLazilyLoadedUserAttributes().address = address;
    }

    /**
     * get address
     */
    public String getAddress() {
        return this.getLazilyLoadedUserAttributes().address;
    }

    /**
     * set city
     */
    public void setCity( String city ) {
        this.getLazilyLoadedUserAttributes().city = city;
    }

    /**
     * get city
     */
    public String getCity() {
        return this.getLazilyLoadedUserAttributes().city;
    }

    /**
     * set zip
     */
    public void setZip( String zip ) {
        this.getLazilyLoadedUserAttributes().zip = zip;
    }

    /**
     * get zip
     */
    public String getZip() {
        return this.getLazilyLoadedUserAttributes().zip;
    }

    /**
     * set country
     */
    public void setCountry( String country ) {
        this.getLazilyLoadedUserAttributes().country = country;
    }

    /**
     * get country
     */
    public String getCountry() {
        return this.getLazilyLoadedUserAttributes().country;
    }

    /**
     * set county_council
     */
    public void setCountyCouncil( String county_council ) {
        this.getLazilyLoadedUserAttributes().county_council = county_council;
    }

    /**
     * get county_council
     */
    public String getCountyCouncil() {
        return this.getLazilyLoadedUserAttributes().county_council;
    }

    /**
     * Return the users e-mail address
     */
    public String getEmailAddress() {
        return this.getLazilyLoadedUserAttributes().emailAddress;
    }

    /**
     * Set the users e-mail address
     */
    public void setEmailAddress( String emailAddress ) {
        this.getLazilyLoadedUserAttributes().emailAddress = emailAddress;
    }

    /**
     * Get the users workphone
     */
    public String getWorkPhone() {
        return this.getLazilyLoadedUserPhoneNumbers().workPhone;
    }

    /**
     * Set the users workphone
     */
    public void setWorkPhone( String workphone ) {
        this.getLazilyLoadedUserPhoneNumbers().workPhone = workphone;
    }

    /**
     * Get the users mobilephone
     */
    public String getMobilePhone() {
        return this.getLazilyLoadedUserPhoneNumbers().mobilePhone;
    }

    /**
     * Set the users mobilephone
     */
    public void setMobilePhone( String mobilephone ) {
        this.getLazilyLoadedUserPhoneNumbers().mobilePhone = mobilephone;
    }

    /**
     * Get the users homephone
     */
    public String getHomePhone() {
        return this.getLazilyLoadedUserPhoneNumbers().homePhone;
    }

    /**
     * Set the users homepohne
     */
    public void setHomePhone( String homephone ) {
        this.getLazilyLoadedUserPhoneNumbers().homePhone = homephone;
    }

    /**
     * Get the users faxphone
     */
    public String getFaxPhone() {
        return this.getLazilyLoadedUserPhoneNumbers().faxPhone;
    }

    /**
     * Set the users faxpohne
     */
    public void setFaxPhone( String faxphone ) {
        this.getLazilyLoadedUserPhoneNumbers().faxPhone = faxphone;
    }

    /**
     * Get the users otherphone
     */
    public String getOtherPhone() {
        return this.getLazilyLoadedUserPhoneNumbers().otherPhone;
    }

    /**
     * Set the users otherpohne
     */
    public void setOtherPhone( String otherphone ) {
        this.getLazilyLoadedUserPhoneNumbers().otherPhone = otherphone;
    }

    /**
     * get lang_id
     */
    public int getLangId() {
        return this.getLazilyLoadedUserAttributes().lang_id;
    }

    /**
     * set lang_id
     */
    public void setLangId( int lang_id ) {
        this.getLazilyLoadedUserAttributes().lang_id = lang_id;
    }

    /**
     * Set whether the user is allowed to log in
     */
    public void setActive( boolean active ) {
        this.getLazilyLoadedUserAttributes().active = active;
    }

    /**
     * Check whether the user is allowed to log in
     */
    public boolean isActive() {
        return this.getLazilyLoadedUserAttributes().active;
    }

    /**
     * set create_date
     */
    public void setCreateDate( String create_date ) {
        this.getLazilyLoadedUserAttributes().create_date = create_date;
    }

    /**
     * get create_date
     */
    public String getCreateDate() {
        return this.getLazilyLoadedUserAttributes().create_date;
    }

    /**
     * set template group
     */
    public void setTemplateGroup( TemplateGroupDomainObject template_group ) {
        this.getLazilyLoadedUserAttributes().templateGroup = template_group;
    }

    /**
     * get template group
     */
    public TemplateGroupDomainObject getTemplateGroup() {
        return getLazilyLoadedUserAttributes().templateGroup;
    }

    /**
     * Return the users language
     */
    public String getLanguageIso639_2() {
        return this.getLazilyLoadedUserAttributes().languageIso639_2;
    }

    /**
     * Set the users language
     */
    public void setLanguageIso639_2( String languageIso639_2 ) {
        this.getLazilyLoadedUserAttributes().languageIso639_2 = languageIso639_2;
    }

    /**
     * Get the login-type.
     */
    public String getLoginType() {
        return this.getLazilyLoadedUserAttributes().loginType;
    }

    /**
     * Set the login-type.
     */
    public void setLoginType( String loginType ) {
        this.getLazilyLoadedUserAttributes().loginType = loginType;
    }

    public boolean isImcmsExternal() {
        return this.getLazilyLoadedUserAttributes().imcmsExternal;
    }

    public void setImcmsExternal( boolean external ) {
        this.getLazilyLoadedUserAttributes().imcmsExternal = external;
    }

    public void addRole( RoleDomainObject role ) {
        getLazilyLoadedUserRoles().roles.add( role ) ;
    }

    public void setRoles( RoleDomainObject[] rolesForUser ) {
        this.getLazilyLoadedUserRoles().roles = new HashSet( Arrays.asList( rolesForUser ) );
    }

    public boolean hasRole( RoleDomainObject role ) {
        return this.getLazilyLoadedUserRoles().roles.contains( role );
    }

    public RoleDomainObject[] getRoles() {
        return (RoleDomainObject[])getLazilyLoadedUserRoles().roles.toArray( new RoleDomainObject[getLazilyLoadedUserRoles().roles.size()] );
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof UserDomainObject ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        final UserDomainObject userDomainObject = (UserDomainObject)o;

        if ( id != userDomainObject.id ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + id;
        return result;
    }

    public boolean isSuperAdmin() {
        return hasRole( RoleDomainObject.SUPERADMIN );
    }

    public boolean isUserAdmin() {
        return hasRole( RoleDomainObject.USERADMIN );
    }

    public boolean canEdit( DocumentDomainObject document ) {
        return ApplicationServer.getIMCServiceInterface().getDocumentMapper().userHasMoreThanReadPermissionOnDocument( this, document );
    }

    public String toString() {
        return "(user " + id + " \"" + getLazilyLoadedUserAttributes().loginName + "\")";
    }

    /* FIXME: Current context path should be sent in a HttpServletRequest, not in an UserDomainObject. */
    public void setCurrentContextPath( String currentContextPath ) {
        this.currentContextPath = currentContextPath;
    }

    public String getCurrentContextPath() {
        return currentContextPath;
    }

}
