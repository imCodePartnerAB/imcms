package imcode.server.user;

import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.ApplicationServer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.io.Serializable;

public class UserDomainObject extends Hashtable {

    private String currentContextPath;

    public UserDomainObject() {
        lazilyLoadedUserAttributes = new LazilyLoadedUserAttributes();
    }

    UserDomainObject( int id ) {
        this.id = id ;
    }

    private int id;

    private class LazilyLoadedUserAttributes implements Serializable {

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
        private String workPhone = "";
        private String mobilePhone = "";
        private String homePhone = "";
        private int lang_id;
        private boolean active;
        private String create_date;

        private String languageIso639_2;

        private TemplateGroupDomainObject templateGroup;
        private String loginType;

        private boolean imcmsExternal = false;
        private Set roles = new HashSet();
    }

    private LazilyLoadedUserAttributes lazilyLoadedUserAttributes = null ;

    private LazilyLoadedUserAttributes getLazilyLoadedUserAttributes() {
        if (null == lazilyLoadedUserAttributes) {
            lazilyLoadedUserAttributes = new LazilyLoadedUserAttributes() ;
            ImcmsAuthenticatorAndUserMapper imcmsAuthenticatorAndUserMapper = ApplicationServer.getIMCServiceInterface().getImcmsAuthenticatorAndUserAndRoleMapper() ;
            imcmsAuthenticatorAndUserMapper.initUserFromSqlData(this, imcmsAuthenticatorAndUserMapper.sqlSelectUserById(id));
        }
        return lazilyLoadedUserAttributes;
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
        return this.getLazilyLoadedUserAttributes().workPhone;
    }

    /**
     * Set the users workphone
     */
    public void setWorkPhone( String workphone ) {
        this.getLazilyLoadedUserAttributes().workPhone = workphone;
    }

    /**
     * Get the users mobilephone
     */
    public String getMobilePhone() {
        return this.getLazilyLoadedUserAttributes().mobilePhone;
    }

    /**
     * Set the users mobilephone
     */
    public void setMobilePhone( String mobilephone ) {
        this.getLazilyLoadedUserAttributes().mobilePhone = mobilephone;
    }

    /**
     * Get the users homephone
     */
    public String getHomePhone() {
        return this.getLazilyLoadedUserAttributes().homePhone;
    }

    /**
     * Set the users homepohne
     */
    public void setHomePhone( String homephone ) {
        this.getLazilyLoadedUserAttributes().homePhone = homephone;
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
        getLazilyLoadedUserAttributes().roles.add( role ) ;
    }

    public void setRoles( RoleDomainObject[] rolesForUser ) {
        this.getLazilyLoadedUserAttributes().roles = new HashSet( Arrays.asList( rolesForUser ) );
    }

    public boolean hasRole( RoleDomainObject role ) {
        return this.getLazilyLoadedUserAttributes().roles.contains( role );
    }

    public RoleDomainObject[] getRoles() {
        return (RoleDomainObject[])getLazilyLoadedUserAttributes().roles.toArray( new RoleDomainObject[getLazilyLoadedUserAttributes().roles.size()] );
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
