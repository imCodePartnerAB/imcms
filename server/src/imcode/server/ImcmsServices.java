package imcode.server ;

import imcode.server.db.Database;
import imcode.server.document.DocumentMapper;
import imcode.server.document.TemplateMapper;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.parser.ParserParameters;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Clock;
import imcode.util.net.SMTP;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.*;

public interface ImcmsServices extends Database, Clock {

    /** Verify a Internet/Intranet user. Data from any SQL Database. **/
    UserDomainObject verifyUser(String login, String password)
	;

    /**
       @deprecated Use {@link imcode.server.document.textdocument.TextDocumentDomainObject#getText(int)} instead.
     **/
    TextDomainObject getText(int meta_id,int txt_no)
	;

    String parsePage( ParserParameters paramsToParse ) throws IOException ;

    /** @deprecated Use {@link imcode.server.document.HtmlDocumentDomainObject#getHtml()} instead. **/
    String isFramesetDoc( int meta_id )
	;

    // get external template folder
    File getExternalTemplateFolder(int meta_id, UserDomainObject user)
	;

    void incrementSessionCounter();

    // set session counter
    void setSessionCounter(int value)  ;

    // set  session counter date
    void setSessionCounterDate(Date date)  ;

    // set  session counter date
    Date getSessionCounterDate()  ;

    // parsedoc use template
    String getAdminTemplate( String adminTemplateName, UserDomainObject user, java.util.List tagsWithReplacements )  ;

    // parseExternaldoc use template
    String getTemplateFromDirectory( String adminTemplateName, UserDomainObject user, java.util.List variables,
                                                 String directory )
	;

    // parseExternaldoc use template
    String getTemplateFromSubDirectoryOfDirectory( String adminTemplateName, UserDomainObject user, java.util.List variables,
                                                               String directory, String subDirectory )
	;

    // get templatehome
    String getTemplateData(int template_id)
	throws IOException ;

    // get templatehome
    File getTemplatePath()
	;

    // get file-path to imcmsimages
    File getImcmsPath()
	;

    // get language prefix by id
    String getLanguagePrefixByLangId ( int lang_id );

    // get language prefix for user
    String getUserLangPrefixOrDefaultLanguage( UserDomainObject user )
    ;

    // get doctype
    int getDocType(int meta_id)
	;

    /** @deprecated Use {@link UserDomainObject#canEdit(imcode.server.document.DocumentDomainObject)} **/
    boolean checkDocAdminRights(int meta_id, UserDomainObject user)
	;

    // save template to disk
     int saveTemplate(String name,String file_name,byte[] data,boolean overwrite,String lang_prefix)
	;

    // get demo template data
    Object[] getDemoTemplate(int template_id)
	throws IOException ;

    boolean checkDocAdminRights(int meta_id, UserDomainObject user, int permissions)
	;

    boolean checkDocAdminRightsAny(int meta_id, UserDomainObject user, int permissions)
	;

    // save demo template
    void saveDemoTemplate(int template_id,byte [] data, String suffix) throws IOException
    ;

    // get server date
    Date getCurrentDate()
    ;

    // get demotemplates
    String[] getDemoTemplateIds()
    ;

    // delete demotemplate
    void deleteDemoTemplate(int template_id) throws IOException
    ;

    SystemData getSystemData()  ;

    void setSystemData(SystemData sd)  ;

    String[][] getAllDocumentTypes(String langPrefixStr)  ;

    String getFortune(String path) throws IOException ;

    List getQuoteList(String quoteListName);

    void setQuoteList(String quoteListName, List quoteList) throws IOException ;

    List getPollList(String pollListName);

    void setPollList(String pollListName, List pollList) throws IOException ;

    int getSessionCounter();

    String getSessionCounterDateAsString();

    /** Get all possible userflags **/
    Map getUserFlags() ;
    /** Get all userflags for a single user **/
    Map getUserFlags(UserDomainObject user) ;
    /** Get all userflags of a single type **/
    Map getUserFlags(int type) ;
    /** Get all userflags for a single user of a single type **/
    Map getUserFlags(UserDomainObject user, int type) ;

    void setUserFlag(UserDomainObject user, String flagName);

    void unsetUserFlag(UserDomainObject user, String flagName);

    /** Get an interface to the poll handling system **/
    imcode.util.poll.PollHandlingSystem getPollHandlingSystem();

    /** Get an interface to the shopping order system **/
    imcode.util.shop.ShoppingOrderSystem getShoppingOrderSystem() ;

    void updateMainLog( String logMessage );

    DocumentMapper getDocumentMapper();

    ImcmsAuthenticatorAndUserAndRoleMapper getImcmsAuthenticatorAndUserAndRoleMapper();

    String getDefaultLanguage();

    TemplateMapper getTemplateMapper();

    SMTP getSMTP();

    Properties getLanguageProperties(UserDomainObject user);

    File getIncludePath();

    Collator getDefaultLanguageCollator();

    VelocityEngine getVelocityEngine(UserDomainObject user);

    VelocityContext getVelocityContext( UserDomainObject user );

    Config getConfig();

    Database getDatabase();

    Clock getClock();

    File getRealContextPath();
}
