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
import java.security.KeyStore;
import java.text.Collator;
import java.util.*;

public interface ImcmsServices extends Clock {

    /** Verify a Internet/Intranet user. Data from any SQL Database. **/
    UserDomainObject verifyUser(String login, String password)
	;

    String parsePage( ParserParameters paramsToParse ) throws IOException ;

    /** @deprecated Use {@link imcode.server.document.HtmlDocumentDomainObject#getHtml()} instead. **/
    String isFramesetDoc( int meta_id )
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

    // get templatehome
    String getTemplateData(int template_id)
    throws IOException ;

    // get templatehome
    File getTemplatePath()
	;

    // get language prefix by id
    String getLanguagePrefixByLangId ( int lang_id );

    // get doctype
    int getDocType(int meta_id)
    ;

    // save template to disk
     int saveTemplate(String name,String file_name,byte[] data,boolean overwrite,String lang_prefix)
    ;

    // get demo template data
    Object[] getDemoTemplate(int template_id)
	throws IOException ;

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

    int getSessionCounter();

    String getSessionCounterDateAsString();

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

    KeyStore getKeyStore();

    Database getDatabase();
}
