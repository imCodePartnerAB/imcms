package imcode.server.parser;

import com.imcode.imcms.api.TextDocumentViewing;
import imcode.server.DocumentRequest;
import imcode.server.ImcmsServices;
import imcode.server.document.TemplateDomainObject;
import imcode.server.document.TemplateGroupDomainObject;
import imcode.server.document.TemplateMapper;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Html;
import imcode.util.ShouldNotBeThrownException;
import imcode.util.Utility;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.oro.text.regex.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

public class TextDocumentParser {

    private final static Logger LOG = Logger.getLogger( TextDocumentParser.class );

    static Pattern hashtagPattern;
    private static Pattern htmlTagPattern;
    private static Pattern htmlTagHtmlPattern;
    private static Pattern headEndTagHtmlPattern;

    static {
        Perl5Compiler patComp = new Perl5Compiler();
        try {
            // OK, so this pattern is simple, ugly, and prone to give a lot of errors.
            // Very good. Very good. Know something? NO SOUP FOR YOU!
            htmlTagPattern = patComp.compile( "<[^>]+?>", Perl5Compiler.READ_ONLY_MASK );

            htmlTagHtmlPattern = patComp.compile( "<[hH][tT][mM][lL]\\b", Perl5Compiler.READ_ONLY_MASK );

            headEndTagHtmlPattern = patComp.compile( "<\\/[hH][eE][aA][dD]\\b", Perl5Compiler.READ_ONLY_MASK );

            hashtagPattern = patComp.compile( "#[^ #\"<>&;\\t\\r\\n]+#", Perl5Compiler.READ_ONLY_MASK );
        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
            LOG.fatal( "Bad pattern.", ignored );
        }
    }

    private ImcmsServices service;

    public TextDocumentParser( ImcmsServices service ) {
        this.service = service;
    }

    public void parsePage(ParserParameters paramsToParse,
                          Writer out) throws IOException {
        NDC.push( "parsePage" );
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            untimedParsePage( paramsToParse, out);
            stopWatch.stop();
            LOG.trace("Parsing template took "+stopWatch.getTime()+"ms.");
        } finally {
            NDC.pop();
        }
    }

    public void untimedParsePage(ParserParameters parserParameters,
                                 Writer out) throws IOException {
        TextDocumentViewing viewing = new TextDocumentViewing( parserParameters );
        TextDocumentViewing previousViewing = TextDocumentViewing.putInRequest( viewing );
        ParserParameters previousParameters = ParserParameters.putInRequest(parserParameters);
        try {
            DocumentRequest documentRequest = parserParameters.getDocumentRequest();

            TextDocumentDomainObject document = (TextDocumentDomainObject)documentRequest.getDocument();
            UserDomainObject user = documentRequest.getUser();

            String templateName = document.getTemplateName();

            TemplateMapper templateMapper = service.getTemplateMapper();
            if ( parserParameters.getTemplateName() != null ) {
                TemplateDomainObject template1 = templateMapper.getTemplateByName( parserParameters.getTemplateName() );
                if ( null != template1 ) {
                    templateName = template1.getName();
                }
            }

            TemplateDomainObject template = templateMapper.getTemplateByName(templateName);

            Perl5Matcher patMat = new Perl5Matcher();

            final String imcmsMessage     = service.getAdminTemplate( "textdoc/imcms_message.html", user, null );
            final String imcmsAdminScript = (viewing.isEditingTexts()) ?
				            service.getAdminTemplate( "textdoc/imcms_admin_headtag_mode_text.html", user, null ) :
				            service.getAdminTemplate( "textdoc/imcms_admin_headtag.html", user, null ) ;
						HttpServletRequest request    = null;
						HttpServletResponse response  = null;
						boolean hasAdminPanel         = false;
						try {
							request = documentRequest.getHttpServletRequest();
							response = documentRequest.getHttpServletResponse();
							hasAdminPanel = !"".equals(Html.getAdminButtons(user, document, request, response));
						} catch (Exception e) {}
						if (null == template) {
                throw new RuntimeException("Template not found: "+templateName);
            } else if (template.getFileName().endsWith(".jsp") || template.getFileName().endsWith(".jspx")) {
                try {
                    String contents = Utility.getContents("/WEB-INF/templates/text/" + template.getFileName(), request, response);
                    contents = Util.substitute( patMat, htmlTagHtmlPattern, new Substitution() {
                       public void appendSubstitution( StringBuffer stringBuffer, MatchResult matchResult, int i, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern ) {
                          stringBuffer.append( imcmsMessage ).append( matchResult.group( 0 ) );
                       }
                    }, contents ) ;
	                  if (hasAdminPanel) {
											contents = Util.substitute( patMat, headEndTagHtmlPattern, new Substitution() {
												 public void appendSubstitution( StringBuffer stringBuffer, MatchResult matchResult, int i, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern ) {
														stringBuffer.append( imcmsAdminScript ).append( matchResult.group( 0 ) );
												 }
											}, contents ) ;
	                  }
	                  out.write(contents);
                } catch ( ServletException e ) {
                    throw new UnhandledException(e);
                }
            } else {
                String templateContent = service.getTemplateMapper().getTemplateData(templateName);

                SimpleDateFormat datetimeFormatWithSeconds = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING );

                Properties hashTags = getHashTags( user, datetimeFormatWithSeconds, document, viewing.isEditingTemplate(), parserParameters );
                MapSubstitution hashtagsubstitution = new MapSubstitution( hashTags, true );
                TagParser tagParser = new TagParser( this, parserParameters);

                String tagsReplaced = tagParser.replaceTags(templateContent, false);
                tagsReplaced = Util.substitute( patMat, hashtagPattern, hashtagsubstitution, tagsReplaced, Util.SUBSTITUTE_ALL );

                String emphasizedAndTagsReplaced = applyEmphasis( documentRequest, user, tagsReplaced, patMat );
                String cont = Util.substitute( patMat, htmlTagHtmlPattern, new Substitution() {
                    public void appendSubstitution( StringBuffer stringBuffer, MatchResult matchResult, int i, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern ) {
                        stringBuffer.append( imcmsMessage ).append( matchResult.group( 0 ) );
                    }
                }, emphasizedAndTagsReplaced );
	              if (hasAdminPanel) {
									cont = Util.substitute( patMat, headEndTagHtmlPattern, new Substitution() {
											public void appendSubstitution( StringBuffer stringBuffer, MatchResult matchResult, int i, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern ) {
													stringBuffer.append( imcmsAdminScript ).append( matchResult.group( 0 ) );
											}
									}, cont );
	              }
								out.write(cont);
            }
        } finally {
            if (null != previousViewing) {
                TextDocumentViewing.putInRequest( previousViewing ) ;
            }
            if (null != previousParameters) {
                ParserParameters.putInRequest(previousParameters) ;
            }
        }
    }
	 

    private String applyEmphasis( DocumentRequest documentRequest, UserDomainObject user, String string,
                                  Perl5Matcher patMat ) {
        String[] emp = documentRequest.getEmphasize();
        if ( emp != null ) { // If we have something to emphasize...
            String emphasize_string = service.getAdminTemplate( "textdoc/emphasize.html", user, null );
            Perl5Substitution emphasize_substitution = new Perl5Substitution( emphasize_string );
            StringBuffer result = new StringBuffer( string.length() ); // A StringBuffer to hold the result
            PatternMatcherInput emp_input = new PatternMatcherInput( string );    // A PatternMatcherInput to match on
            int last_html_offset = 0;
            String non_html_tag_string;
            while ( patMat.contains( emp_input, htmlTagPattern ) ) {
                int current_html_offset = emp_input.getMatchBeginOffset();
                non_html_tag_string = result.substring( last_html_offset, current_html_offset );
                last_html_offset = emp_input.getMatchEndOffset();
                String html_tag_string = result.substring(current_html_offset, last_html_offset);
                non_html_tag_string = emphasizeString( non_html_tag_string, emp, emphasize_substitution, patMat );
                // for each string to emphasize
                result.append( non_html_tag_string );
                result.append( html_tag_string );
            } // while
            non_html_tag_string = result.substring( last_html_offset );
            non_html_tag_string = emphasizeString( non_html_tag_string, emp, emphasize_substitution, patMat );
            result.append( non_html_tag_string );
            return result.toString();
        }
        return string;
    }

    private Properties getHashTags( UserDomainObject user, SimpleDateFormat datetimeFormatWithSeconds,
                                    TextDocumentDomainObject document,
                                    boolean templatemode, ParserParameters parserParameters ) throws IOException {

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

        if ( parserParameters.getFlags() >= 0 && parserParameters.isAdminButtonsVisible() ) {
            tags.setProperty( "#adminMode#", Html.getAdminButtons( user, document, parserParameters.getDocumentRequest().getHttpServletRequest(), parserParameters.getDocumentRequest().getHttpServletResponse() ) );
        }

        String changeTemplateUi = createChangeTemplateUi( templatemode, user, document, service);
        tags.setProperty( "#changePage#", changeTemplateUi );
        return tags;
    }

    public static String createChangeTemplateUi(boolean templatemode, UserDomainObject user,
                                          TextDocumentDomainObject document, ImcmsServices service) throws IOException {
        String changeTemplateUi = "";
        if ( templatemode ) {	//Templatemode! :)

            TemplateMapper templateMapper = service.getTemplateMapper();

            TemplateGroupDomainObject selectedTemplateGroup = user.getTemplateGroup();
            if ( null == selectedTemplateGroup ) {
                selectedTemplateGroup = templateMapper.getTemplateGroupById( document.getTemplateGroupId() );
            }

            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)user.getPermissionSetFor( document );

            Set allowedTemplateGroupIds = textDocumentPermissionSet.getAllowedTemplateGroupIds();
            List allowedTemplateGroups = templateMapper.getTemplateGroups(allowedTemplateGroupIds);
            String templateGroupsHtmlOptionList = templateMapper.createHtmlOptionListOfTemplateGroups( allowedTemplateGroups, selectedTemplateGroup );

            List<TemplateDomainObject> templates = new ArrayList<TemplateDomainObject>();
            if ( allowedTemplateGroupIds.contains(new Integer(selectedTemplateGroup.getId()) ) ) {
                templates = templateMapper.getTemplatesInGroup( selectedTemplateGroup );
            }
            String templateId = document.getTemplateName();
            TemplateDomainObject template = templateMapper.getTemplateByName(templateId) ;
            String templatesHtmlOptionList = templateMapper.createHtmlOptionListOfTemplates( templates, template );

            // Oh! I need a set of tags to be replaced in the templatefiles we'll load...
            List temptags = new ArrayList();

            temptags.add( "#getMetaId#" );
            temptags.add( "" + document.getId() );
            temptags.add( "#group#" );
            temptags.add( selectedTemplateGroup.getName() );
            temptags.add( "#getTemplateGroups#" );
            temptags.add( templateGroupsHtmlOptionList );
            temptags.add( "#simple_name#" );
            temptags.add( template.getName() );
            temptags.add( "#getTemplatesInGroup#" );
            temptags.add( templatesHtmlOptionList );

            // Put templateadmintemplate in list of files to load.
            changeTemplateUi = service.getAdminTemplate( "textdoc/inPage_admin.html", user, temptags );
        }  // if (templatemode)
        return changeTemplateUi;
    }

    private String emphasizeString(String string, String[] emp, Substitution emphasize_substitution,
                                   PatternMatcher patMat) {
        String emphasizedString = string;

        Perl5Compiler empCompiler = new Perl5Compiler();
        // for each string to emphasize
        for ( String anEmp : emp ) {
            try {
                Pattern empPattern = empCompiler.compile(
                        "(" + Perl5Compiler.quotemeta(anEmp) + ")", Perl5Compiler.CASE_INSENSITIVE_MASK);
                emphasizedString = Util.substitute(patMat, empPattern, emphasize_substitution, emphasizedString, Util.SUBSTITUTE_ALL);
            } catch ( MalformedPatternException ex ) {
                throw new ShouldNotBeThrownException(ex);
            }
        }
        return emphasizedString;
    }

    
}
