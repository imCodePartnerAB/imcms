package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.IMCServiceInterface;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.regex.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TextDocumentParser implements imcode.server.IMCConstants {

    private final static Logger log = Logger.getLogger( TextDocumentParser.class );

    static Pattern HASHTAG_PATTERN = null;
    private static Pattern MENU_PATTERN = null;
    private static Pattern IMCMS_TAG_PATTERN = null;
    private static Pattern HTML_TAG_PATTERN = null;

    static {
        Perl5Compiler patComp = new Perl5Compiler();
        try {
            // OK, so this pattern is simple, ugly, and prone to give a lot of errors.
            // Very good. Very good. Know something? NO SOUP FOR YOU!
            HTML_TAG_PATTERN = patComp.compile( "<[^>]+?>", Perl5Compiler.READ_ONLY_MASK );

            IMCMS_TAG_PATTERN = patComp.compile( "<\\?imcms:([-\\w]+)(.*?)\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                 | Perl5Compiler.READ_ONLY_MASK );
            HASHTAG_PATTERN = patComp.compile( "#[^ #\"<>&;\\t\\r\\n]+#", Perl5Compiler.READ_ONLY_MASK );
            MENU_PATTERN = patComp.compile( "<\\?imcms:menu(.*?)\\?>(.*?)<\\?\\/imcms:menu\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                                 | Perl5Compiler.READ_ONLY_MASK );

        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
            log.fatal( "Bad pattern.", ignored );
        }
    }

    private IMCServiceInterface service;
    private static final int EXPECTED_CONTENT_BLOAT = 16384;

    public TextDocumentParser( IMCServiceInterface serverobject ) {
        this.service = serverobject;
    }

    public String parsePage( ParserParameters paramsToParse ) throws IOException {
        NDC.push( "parsePage" );
        String page = parsePage( paramsToParse, 5 );
        NDC.pop();
        return page;
    }

    public String parsePage( ParserParameters parserParameters, int includelevel ) throws IOException {
        DocumentRequest documentRequest = parserParameters.getDocumentRequest();
        int flags = parserParameters.getFlags();
        try {
            TextDocumentDomainObject document = (TextDocumentDomainObject)documentRequest.getDocument();
            UserDomainObject user = documentRequest.getUser();
            DocumentMapper documentMapper = service.getDocumentMapper();

            boolean textmode = false;
            boolean imagemode = false;
            boolean menumode = false;
            boolean templatemode = false;
            boolean includemode = false;

            if ( flags > 0 ) {
                int user_set_id = documentMapper.getUsersMostPrivilegedPermissionSetIdOnDocument( user, document );
                int user_perm_set = documentMapper.getUsersPermissionBitsOnDocumentIfRestricted( user_set_id, document );

                textmode = ( flags & PERM_EDIT_TEXT_DOCUMENT_TEXTS ) != 0
                           && ( user_set_id == 0 || ( user_perm_set & PERM_EDIT_TEXT_DOCUMENT_TEXTS ) != 0 );
                imagemode = ( flags & PERM_EDIT_TEXT_DOCUMENT_IMAGES ) != 0
                            && ( user_set_id == 0 || ( user_perm_set & PERM_EDIT_TEXT_DOCUMENT_IMAGES ) != 0 );
                menumode = ( flags & PERM_EDIT_TEXT_DOCUMENT_MENUS ) != 0
                           && ( user_set_id == 0 || ( user_perm_set & PERM_EDIT_TEXT_DOCUMENT_MENUS ) != 0 );
                templatemode = ( flags & PERM_EDIT_TEXT_DOCUMENT_TEMPLATE ) != 0
                               && ( user_set_id == 0 || ( user_perm_set & PERM_EDIT_TEXT_DOCUMENT_TEMPLATE ) != 0 );
                includemode = ( flags & PERM_EDIT_TEXT_DOCUMENT_INCLUDES ) != 0
                              && ( user_set_id == 0 || ( user_perm_set & PERM_EDIT_TEXT_DOCUMENT_INCLUDES ) != 0 );
            }

            String template = getTemplate( document, parserParameters );

            StringBuffer result = new StringBuffer( template.length() + EXPECTED_CONTENT_BLOAT );

            Perl5Matcher patMat = new Perl5Matcher();

            SimpleDateFormat datetimeFormatWithSeconds = new SimpleDateFormat( DateConstants.DATETIME_SECONDS_FORMAT_STRING );

            String imcmsMessage = service.getAdminTemplate( "textdoc/imcms_message.html", user, null );
            result.append( imcmsMessage );

            Properties hashTags = getHashTags( user, datetimeFormatWithSeconds, document, templatemode, parserParameters );
            MapSubstitution hashtagsubstitution = new MapSubstitution( hashTags, true );
            MenuParserSubstitution menuparsersubstitution = new MenuParserSubstitution( parserParameters, document, menumode );
            ImcmsTagSubstitution imcmstagsubstitution = new ImcmsTagSubstitution( this, parserParameters, includemode, includelevel, textmode, imagemode );

            String returnresult = replaceTags( template, patMat, menuparsersubstitution, imcmstagsubstitution, hashtagsubstitution );

            returnresult = applyEmphasis( documentRequest, user, returnresult, patMat, result );
            return returnresult;
        } catch ( RuntimeException ex ) {
            log.error( "Error occurred during parsing.", ex );
            throw ex;
        }
    }

    private String getTemplate( TextDocumentDomainObject document, ParserParameters parserParameters ) throws IOException {
        TemplateDomainObject documentTemplate = document.getTemplate();
        int documentTemplateId = documentTemplate.getId();

        String template_name = parserParameters.getTemplate();
        if ( template_name != null ) {
            TemplateMapper templateMapper = service.getTemplateMapper();
            TemplateDomainObject template = templateMapper.getTemplateByName( template_name );
            if ( null != template ) {
                documentTemplateId = template.getId();
            }
        }

        String templateContents = service.getTemplateData( documentTemplateId );
        return templateContents;
    }

    private String applyEmphasis( DocumentRequest documentRequest, UserDomainObject user, String returnresult,
                                  Perl5Matcher patMat, StringBuffer result ) {
        String[] emp = documentRequest.getEmphasize();
        if ( emp != null ) { // If we have something to emphasize...
            String emphasize_string = service.getAdminTemplate( "textdoc/emphasize.html", user, null );
            Perl5Substitution emphasize_substitution = new Perl5Substitution( emphasize_string );
            StringBuffer emphasized_result = new StringBuffer( returnresult.length() ); // A StringBuffer to hold the result
            PatternMatcherInput emp_input = new PatternMatcherInput( returnresult );    // A PatternMatcherInput to match on
            int last_html_offset = 0;
            int current_html_offset;
            String non_html_tag_string;
            String html_tag_string;
            while ( patMat.contains( emp_input, HTML_TAG_PATTERN ) ) {
                current_html_offset = emp_input.getMatchBeginOffset();
                non_html_tag_string = result.substring( last_html_offset, current_html_offset );
                last_html_offset = emp_input.getMatchEndOffset();
                html_tag_string = result.substring( current_html_offset, last_html_offset );
                non_html_tag_string = emphasizeString( non_html_tag_string, emp, emphasize_substitution, patMat );
                // for each string to emphasize
                emphasized_result.append( non_html_tag_string );
                emphasized_result.append( html_tag_string );
            } // while
            non_html_tag_string = result.substring( last_html_offset );
            non_html_tag_string = emphasizeString( non_html_tag_string, emp, emphasize_substitution, patMat );
            emphasized_result.append( non_html_tag_string );
            returnresult = emphasized_result.toString();
        }
        return returnresult;
    }

    private String replaceTags( String template, Perl5Matcher patMat, MenuParserSubstitution menuparsersubstitution,
                                ImcmsTagSubstitution imcmstagsubstitution, MapSubstitution hashtagsubstitution ) {
        // Menus.
        String result = Util.substitute( patMat, MENU_PATTERN, menuparsersubstitution, template, Util.SUBSTITUTE_ALL );
        // <?imcms:tags?>
        result = Util.substitute( patMat, IMCMS_TAG_PATTERN, imcmstagsubstitution, result, Util.SUBSTITUTE_ALL );
        // #hashtags#
        result = Util.substitute( patMat, HASHTAG_PATTERN, hashtagsubstitution, result, Util.SUBSTITUTE_ALL );

        return result;
    }

    private Properties getHashTags( UserDomainObject user, SimpleDateFormat datetimeFormatWithSeconds,
                                    TextDocumentDomainObject document,
                                    boolean templatemode, ParserParameters parserParameters ) {

        Properties tags = new Properties();	// A properties object to hold the results from the db...
        // Put tags and corresponding data in Properties
        tags.setProperty( "#userName#", user.getFullName() );
        tags.setProperty( "#session_counter#", String.valueOf( service.getSessionCounter() ) );
        tags.setProperty( "#session_counter_date#", service.getSessionCounterDateAsString() );
        tags.setProperty( "#lastDate#", datetimeFormatWithSeconds.format( document.getModifiedDatetime() ) );
        tags.setProperty( "#metaHeadline#", document.getHeadline() );
        tags.setProperty( "#metaText#", document.getMenuText() );

        String meta_image = document.getMenuImage();
        if ( !"".equals( meta_image ) ) {
            meta_image = "<img src=\"" + meta_image + "\" border=\"0\">";
        }
        tags.setProperty( "#metaImage#", meta_image );
        tags.setProperty( "#sys_message#", service.getSystemData().getSystemMessage() );
        tags.setProperty( "#webMaster#", service.getSystemData().getWebMaster() );
        tags.setProperty( "#webMasterEmail#", service.getSystemData().getWebMasterAddress() );
        tags.setProperty( "#serverMaster#", service.getSystemData().getServerMaster() );
        tags.setProperty( "#serverMasterEmail#", service.getSystemData().getServerMasterAddress() );

        tags.setProperty( "#param#", parserParameters.getParameter() );
        tags.setProperty( "#externalparam#", parserParameters.getExternalParameter() );

        if ( parserParameters.getFlags() >= 0 ) {
            tags.setProperty( "#adminMode#", service.getAdminButtons( user, document ) );
        }

        String changeTemplateUi = createChangeTemplateUi( templatemode, user, document );
        tags.setProperty( "#changePage#", changeTemplateUi );
        return tags;
    }

    private String createChangeTemplateUi( boolean templatemode, UserDomainObject user,
                                           TextDocumentDomainObject document ) {
        String changeTemplateUi = "";
        if ( templatemode ) {	//Templatemode! :)

            TemplateMapper templateMapper = service.getTemplateMapper();

            TemplateGroupDomainObject selectedTemplateGroup = user.getTemplateGroup();
            if ( null == selectedTemplateGroup ) {
                selectedTemplateGroup = templateMapper.getTemplateGroupById( document.getTemplateGroupId() );
            }

            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)service.getDocumentMapper().getUsersMostPrivilegedPermissionSetOnDocument( user, document ) ;

            TemplateGroupDomainObject[] allowedTemplateGroups = textDocumentPermissionSet.getAllowedTemplateGroups();
            String templateGroupsHtmlOptionList = templateMapper.createHtmlOptionListOfTemplateGroups( allowedTemplateGroups, selectedTemplateGroup );

            TemplateDomainObject[] templates = new TemplateDomainObject[0] ;
            if (ArrayUtils.contains( allowedTemplateGroups, selectedTemplateGroup )){
                templates = templateMapper.getTemplatesInGroup( selectedTemplateGroup );
            }
            String templatesHtmlOptionList = templateMapper.createHtmlOptionListOfTemplates( templates, document.getTemplate() );

            // Oh! I need a set of tags to be replaced in the templatefiles we'll load...
            List temptags = new ArrayList();

            temptags.add( "#getMetaId#" );
            temptags.add( "" + document.getId() );
            temptags.add( "#group#" );
            temptags.add( selectedTemplateGroup.getName() );
            temptags.add( "#getTemplateGroups#" );
            temptags.add( templateGroupsHtmlOptionList );
            temptags.add( "#simple_name#" );
            temptags.add( document.getTemplate().getName() );
            temptags.add( "#getTemplatesInGroup#" );
            temptags.add( templatesHtmlOptionList );

            // Put templateadmintemplate in list of files to load.
            changeTemplateUi = service.getAdminTemplate( "textdoc/inPage_admin.html", user, temptags );
        }  // if (templatemode)
        return changeTemplateUi;
    }

    private String emphasizeString( String str, String[] emp, Substitution emphasize_substitution,
                                    PatternMatcher patMat ) {

        Perl5Compiler empCompiler = new Perl5Compiler();
        // for each string to emphasize
        for ( int i = 0; i < emp.length; ++i ) {
            try {
                Pattern empPattern = empCompiler.compile( "(" + Perl5Compiler.quotemeta( emp[i] ) + ")", Perl5Compiler.CASE_INSENSITIVE_MASK );
                str = org.apache.oro.text.regex.Util.substitute( patMat, empPattern, emphasize_substitution, str, org.apache.oro.text.regex.Util.SUBSTITUTE_ALL );
            } catch ( MalformedPatternException ex ) {
                log.warn( "Dynamic Pattern-compilation failed in IMCService.emphasizeString(). Suspected bug in jakarta-oro Perl5Compiler.quotemeta(). The String was '"
                          + emp[i]
                          + "'", ex );
            }
        }
        return str;
    }

}
