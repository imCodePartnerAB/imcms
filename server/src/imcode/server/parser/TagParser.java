package imcode.server.parser;

import com.imcode.imcms.servlet.ImcmsSetupFilter;
import imcode.server.*;
import imcode.server.document.*;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.*;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;

class TagParser {

    private static Pattern HTML_PREBODY_PATTERN = null;
    private static Pattern HTML_POSTBODY_PATTERN = null;
    private static Pattern IMCMS_TAG_PATTERN = null;
    private static Pattern IMCMS_END_TAG_PATTERN = null;
    private static Pattern ATTRIBUTES_PATTERN;

    private final static Logger log = Logger.getLogger( TagParser.class.getName() );

    private FileCache fileCache = new FileCache();

    static {
        Perl5Compiler patComp = new Perl5Compiler();

        try {

            HTML_PREBODY_PATTERN = patComp.compile( "^.*?<[Bb][Oo][Dd][Yy].*?>", Perl5Compiler.SINGLELINE_MASK
                                                                                 | Perl5Compiler.READ_ONLY_MASK );
            HTML_POSTBODY_PATTERN = patComp.compile( "<\\/[Bb][Oo][Dd][Yy]>.*$", Perl5Compiler.SINGLELINE_MASK
                                                                                 | Perl5Compiler.READ_ONLY_MASK );
            IMCMS_TAG_PATTERN = patComp.compile( "<\\?imcms:(\\w+)\\b(.*?)\\s*\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                              | Perl5Compiler.READ_ONLY_MASK );
            IMCMS_END_TAG_PATTERN = patComp.compile( "<\\?/imcms:(\\w+)\\s*\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                  | Perl5Compiler.READ_ONLY_MASK );
            ATTRIBUTES_PATTERN = patComp.compile( "\\s+(\\w+)\\s*=\\s*([\"'])(.*?)\\2", Perl5Compiler.SINGLELINE_MASK
                                                                                                | Perl5Compiler.READ_ONLY_MASK );
        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
            log.fatal( "Danger, Will Robinson!", ignored );
        }
    }

    private final Substitution NULL_SUBSTITUTION = new StringSubstitution( "" );

    private TextDocumentParser textDocParser;

    private boolean includeMode;
    private int includeLevel;
    private int implicitIncludeNumber = 1;

    private Map textMap;
    private boolean textMode;
    private int implicitTextNumber = 1;

    private Map imageMap;
    private boolean imageMode;
    private int implicitImageNumber = 1;

    private ImcmsServices service;
    private ParserParameters parserParameters;
    private DocumentRequest documentRequest;
    private TextDocumentDomainObject document;

    TagParser( TextDocumentParser textdocparser, ParserParameters parserParameters,
                          boolean includemode, int includelevel,
                          boolean textmode,
                          boolean imagemode ) {
        this.textDocParser = textdocparser;
        this.parserParameters = parserParameters;
        this.documentRequest = parserParameters.getDocumentRequest();
        this.document = (TextDocumentDomainObject)documentRequest.getDocument();
        this.service = documentRequest.getServerObject();

        this.includeMode = includemode;
        this.includeLevel = includelevel;

        this.textMode = textmode;
        this.textMap = document.getTexts();

        this.imageMode = imagemode;
        this.imageMap = getImageMap( document, imageMode, documentRequest );

    }

    private Map getImageMap( TextDocumentDomainObject document, boolean imageMode,
                             DocumentRequest documentRequest ) {
        Map images = document.getImages();
        Map imageMap = new HashMap();
        for ( Iterator iterator = images.keySet().iterator(); iterator.hasNext(); ) {
            Integer imageIndex = (Integer)iterator.next();
            ImageDomainObject image = (ImageDomainObject)images.get( imageIndex );
            ImageDomainObject.ImageSource imageSource = image.getSource();
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            if ( !( imageSource instanceof ImageDomainObject.FileDocumentImageSource )
                 || imageMode
                 || documentRequest.getUser().canAccess( ( (ImageDomainObject.FileDocumentImageSource)imageSource ).getFileDocument() ) ) {
                imageMap.put( imageIndex, ImcmsImageUtils.getImageHtmlTag( image, documentRequest.getHttpServletRequest() ) );
            }
        }
        return imageMap;
    }

    /**
     * Handle a <?imcms:metaid?> tag.
     */
    private String tagMetaId() {
        return "" + document.getId();
    }

    /**
     * Handle a <?imcms:section?> tag.
     */
    private String tagSection( Properties attributes ) {
        return tagSections( attributes );
    }

    /**
     * Handle a <?imcms:section?> tag.
     */
    private String tagSections( Properties attributes ) {
        SectionDomainObject[] section = document.getSections();

        String separator = attributes.getProperty( "separator", "," );

        return StringUtils.join( section, separator );
    }

    /**
     * Handle a <?imcms:include ...?> tag
     *
     * @param attributes The attributes of the include tag
     * @param patMat     A pattern matcher.
     */
    private String tagInclude( Properties attributes, PatternMatcher patMat ) {
        int no ;
        String attributevalue;

        if ( null != ( attributevalue = attributes.getProperty( "no" ) ) ) {	    // If we have the attribute no="number"...
            // Set the number of this include-tag
            try {
                no = Integer.parseInt( attributevalue.trim() ); // Then set the number wanted
                implicitIncludeNumber = no + 1;
            } catch ( NumberFormatException ex ) {
                return "<!-- imcms:include no failed: " + ex + " -->";
            }
        } else if ( null != ( attributevalue = attributes.getProperty( "path" ) ) ) {
            return includePath( attributevalue );
        } else if ( null != ( attributevalue = attributes.getProperty( "file" ) ) ) { // If we have the attribute file="filename"...
            return includeFile( attributevalue );
        } else if ( null != ( attributevalue = attributes.getProperty( "document" ) ) ) { // If we have the attribute document="meta-id"
            return includeDocument( attributevalue, attributes, patMat );
        } else if ( null != ( attributevalue = attributes.getProperty( "url" ) ) ) { // If we have an attribute of the form url="url:url"
            return includeUrl( attributevalue, attributes );
        } else { // If we have none of the attributes no, file, url, or document
            no = implicitIncludeNumber++; // Implicitly use the next number.
        }
        return includeEditing( attributes, no, patMat );
    }

    private String includePath( String path ) {
        HttpServletRequest request = documentRequest.getHttpServletRequest();
        HttpServletRequestWrapper metaIdHeaderHttpServletRequest = new TagParser.MetaIdHeaderHttpServletRequest( request, document.getId() );
        try {
            return Utility.getContents( path, metaIdHeaderHttpServletRequest, documentRequest.getHttpServletResponse() );
        } catch ( ServletException ex ) {
            return "<!-- imcms:include path failed: " + ex + " -->";
        } catch ( IOException ex ) {
            return "<!-- imcms:include path failed: " + ex + " -->";
        }
    }

    private String includeEditing( Properties attributes, int no, PatternMatcher patMat ) {
        try {
            String label = attributes.getProperty( "label" );
            label = label == null ? "" : label;
            Integer includedDocumentId = document.getIncludedDocumentId( no );
            if ( includeMode ) {
                HttpServletRequest request = documentRequest.getHttpServletRequest();
                HttpServletResponse response = documentRequest.getHttpServletResponse();
                UserDomainObject user = documentRequest.getUser();
                try {
                    request.setAttribute( "includingDocument", document );
                    request.setAttribute( "includedDocumentId", includedDocumentId );
                    request.setAttribute( "label", label );
                    request.setAttribute( "includeIndex", new Integer( no ));
                    return Utility.getContents( "/imcms/"+user.getLanguageIso639_2()+"/jsp/docadmin/text/edit_include.jsp",request, response ) ;
                } catch ( Exception e ) {
                    throw new UnhandledException( e );
                }
            } else if ( includeLevel > 0 ) {
                if ( null == includedDocumentId ) {
                    return "";
                }
                ParserParameters includedDocumentParserParameters = createIncludedDocumentParserParameters( parserParameters, includedDocumentId.intValue(), attributes );
                String documentStr = textDocParser.parsePage( includedDocumentParserParameters, includeLevel - 1 );
                documentStr = Util.substitute( patMat, HTML_PREBODY_PATTERN, NULL_SUBSTITUTION, documentStr );
                documentStr = Util.substitute( patMat, HTML_POSTBODY_PATTERN, NULL_SUBSTITUTION, documentStr );
                return documentStr;
            } else {
                return "<!-- imcms:include failed: max include-level reached. -->";
            }
        } catch ( IOException ex ) {
            return "<!-- imcms:include failed: " + ex + " -->";
        }
    }

    private String includeUrl( String attribute, Properties attributes ) {
        String urlStr = attribute ;
        try {
            String commaSeparatedNamesOfParametersToSend = attributes.getProperty( "sendparameters" );

            urlStr += -1 == urlStr.indexOf( '?' ) ? "?" : "&";
            Set parameterNamesToSend = createSetFromCommaSeparatedString( commaSeparatedNamesOfParametersToSend );
            urlStr += createQueryStringFromRequest( documentRequest.getHttpServletRequest(), parameterNamesToSend );

            if ( urlStr.startsWith( "/" ) ) {  // lets add hostname if we got a relative path
                urlStr = documentRequest.getHttpServletRequest().getScheme()
                         + "://" + documentRequest.getHttpServletRequest().getServerName()
                         + ':'
                         + documentRequest.getHttpServletRequest().getServerPort()
                         + urlStr;
            }
            URL url = new URL( urlStr );
            String urlProtocol = url.getProtocol();
            if ( "file".equalsIgnoreCase( urlProtocol ) ) { // Make sure we don't have to defend against file://urls...
                return "<!-- imcms:include url failed: file-url not allowed -->";
            }
            String sessionId = documentRequest.getHttpServletRequest().getSession().getId();
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty( "User-Agent",
                                              documentRequest.getHttpServletRequest().getHeader( "User-agent" ) );
            if ( null != attributes.getProperty( "sendsessionid" ) ) {
                urlConnection.addRequestProperty( "Cookie", ImcmsSetupFilter.JSESSIONID_COOKIE_NAME + "="
                                                            + sessionId );
            }
            if ( null != attributes.getProperty( "sendcookies" ) ) {
                Cookie[] requestCookies = documentRequest.getHttpServletRequest().getCookies();
                for ( int i = 0; requestCookies != null && i < requestCookies.length; ++i ) {
                    Cookie theCookie = requestCookies[i];
                    if ( !ImcmsSetupFilter.JSESSIONID_COOKIE_NAME.equals( theCookie.getName() ) ) {
                        urlConnection.addRequestProperty( "Cookie", theCookie.getName() + "="
                                                                    + theCookie.getValue() );
                    }
                }
            }
            if ( null != attributes.getProperty( "sendmetaid" ) ) {
                urlConnection.setRequestProperty( "X-Meta-Id", "" + document.getId() );
            }

            InputStream connectionInputStream = urlConnection.getInputStream();
            String contentType = urlConnection.getContentType();
            String contentEncoding = StringUtils.substringAfter( contentType, "charset=" );
            if ( "".equals( contentEncoding ) ) {
                contentEncoding = WebAppGlobalConstants.DEFAULT_ENCODING_WINDOWS_1252;
            }
            InputStreamReader urlInput = new InputStreamReader( connectionInputStream, contentEncoding );
            int charsRead = -1;
            final int URL_BUFFER_LEN = 16384;
            char[] buffer = new char[URL_BUFFER_LEN];
            StringBuffer urlResult = new StringBuffer();
            while ( -1 != ( charsRead = urlInput.read( buffer, 0, URL_BUFFER_LEN ) ) ) {
                urlResult.append( buffer, 0, charsRead );
            }
            return urlResult.toString();
        } catch ( MalformedURLException ex ) {
            return "<!-- imcms:include url failed: " + ex + " -->";
        } catch ( IOException ex ) {
            return "<!-- imcms:include url failed: " + ex + " -->";
        } catch ( RuntimeException ex ) {
            return "<!-- imcms:include url failed: " + ex + " -->";
        }
    }

    private String includeDocument( String attributevalue, Properties attributes, PatternMatcher patMat ) {
        try {
            if ( includeLevel > 0 ) {
                int included_meta_id = Integer.parseInt( attributevalue );
                ParserParameters includedDocumentParserParameters = createIncludedDocumentParserParameters( parserParameters, included_meta_id, attributes );
                String documentStr = textDocParser.parsePage( includedDocumentParserParameters, includeLevel - 1 );
                documentStr = Util.substitute( patMat, HTML_PREBODY_PATTERN, NULL_SUBSTITUTION, documentStr );
                documentStr = Util.substitute( patMat, HTML_POSTBODY_PATTERN, NULL_SUBSTITUTION, documentStr );
                return documentStr;
            }
        } catch ( NumberFormatException ex ) {
            return "<!-- imcms:include document failed: " + ex + " -->";
        } catch ( IOException ex ) {
            return "<!-- imcms:include document failed: " + ex + " -->";
        } catch ( RuntimeException ex ) {
            return "<!-- imcms:include document failed: " + ex + " -->";
        }
        return "";
    }

    private String includeFile( String attributevalue ) {// Fetch a file from the disk
        try {
            return fileCache.getCachedFileString( new File( service.getIncludePath(), attributevalue ) ); // Get a file from the include directory
        } catch ( IOException ex ) {
            return "<!-- imcms:include file failed: " + ex + " -->";
        }
    }

    private ParserParameters createIncludedDocumentParserParameters( ParserParameters parserParameters,
                                                                     int included_meta_id, Properties attributes ) {
        ParserParameters includedParserParameters = null;
        try {
            includedParserParameters = (ParserParameters)parserParameters.clone();
            includedParserParameters.setTemplate( attributes.getProperty( "template" ) );
            includedParserParameters.setParameter( attributes.getProperty( "param" ) );
            includedParserParameters.getDocumentRequest().setDocument( service.getDocumentMapper().getDocument( included_meta_id ) );
            includedParserParameters.getDocumentRequest().setReferrer( document );
            includedParserParameters.setFlags( -1 );
        } catch ( CloneNotSupportedException e ) {
            // ignored, supported
        }
        return includedParserParameters;
    }

    private String createQueryStringFromRequest( HttpServletRequest httpServletRequest, Set parameterNamesToSend ) {
        List parameterNameValuePairs = new ArrayList();
        Enumeration parameterNames = httpServletRequest.getParameterNames();
        while ( parameterNames.hasMoreElements() ) {
            String parameterName = (String)parameterNames.nextElement();
            if ( null == parameterNamesToSend || parameterNamesToSend.contains( parameterName ) ) {
                String[] parameterValues = httpServletRequest.getParameterValues( parameterName );
                for ( int i = 0; i < parameterValues.length; i++ ) {
                    String parameterValue = parameterValues[i];
                    parameterNameValuePairs.add( URLEncoder.encode( parameterName ) + '='
                                                 + URLEncoder.encode( parameterValue ) );
                }
            }
        }
        return StringUtils.join( parameterNameValuePairs.iterator(), '&' );
    }

    private Set createSetFromCommaSeparatedString( String commaSeparatedNames ) {
        if ( null == commaSeparatedNames ) {
            return null;
        }
        StringTokenizer commaAndWhitespaceSeparatedTokenizer = new StringTokenizer( commaSeparatedNames, ", \t\r\n" );
        Set names = new HashSet();
        while ( commaAndWhitespaceSeparatedTokenizer.hasMoreTokens() ) {
            String parameterName = commaAndWhitespaceSeparatedTokenizer.nextToken();
            names.add( parameterName );
        }
        return names;
    }

    /**
     * Handle a <?imcms:text ...?> tag
     *
     * @param attributes The attributes of the text tag
     *                   <p/>
     *                   attributes:
     *                   - no	( int )   text number in document
     *                   - label ( String ) lable to show in write mode
     *                   - mode  ( read | write )
     *                   - filter ( String )
     *                   - type   (String)
     *                   <p/>
     *                   Supported text_types is:
     *                   <p/>
     *                   pollquestion-n	#where n represent the questíon number in this document
     *                   <p/>
     *                   pollanswer-n-m		#where n represent the questíon number in this document
     *                   and m represent the answer number in question number n
     *                   <p/>
     *                   pollpointanswer-n-m     #where n represent the questíon number in this document
     *                   and m represent the answer number in question number n
     *                   <p/>
     *                   pollparameter-popup_frequency
     *                   pollparameter-cookie
     *                   pollparameter-hideresults
     *                   pollparameter-confirmation_text
     *                   pollparameter-email_recipients
     *                   pollparameter-email_from
     *                   pollparameter-email_subject
     *                   pollparameter-result_template     #template to use when return the result
     *                   pollparameter-name
     *                   pollparameter-description
     */
    private String tagText( Properties attributes ) {
        String mode = attributes.getProperty( "mode" );
        if ( mode != null && !"".equals( mode )
             && ( textMode && "read".startsWith( mode ) // With mode="read", we don't want anything in textMode.
                  || !textMode && "write".startsWith( mode )// With mode="write", we don't want anything unless we're in textMode.
                ) ) {
            return "";
        }
        // Get the 'no'-attribute of the <?imcms:text no="..."?>-tag
        String noStr = attributes.getProperty( "no" );
        int no;
        TextDomainObject text = null;
        if ( null == noStr ) {
            no = implicitTextNumber++;
            text = (TextDomainObject)textMap.get( new Integer( no ) );
        } else {
            noStr = noStr.trim();
            no = Integer.parseInt( noStr );
            text = (TextDomainObject)textMap.get( new Integer( no ) );
            implicitTextNumber = no + 1;
        }
        String result;
        if ( text == null ) {
            result = "";
        } else {
            // Since this is supposed to be a html-view of the db, we'll do some html-escaping.
            result = text.toHtmlString();
        }

        String type = attributes.getProperty( "type" ); // get text type, ex. pollparameter-xxxx
        if ( type == null ) {
            type = "";
        }
        String finalresult = result;
        if ( textMode ) {
            HttpServletRequest request = documentRequest.getHttpServletRequest() ;
            HttpServletResponse response = documentRequest.getHttpServletResponse();
            String formatsAttribute = attributes.getProperty( "formats", "" ) ;
            String[] formats = null != formatsAttribute ? formatsAttribute.split( "\\W+" ) : null ;
            request.setAttribute( "document", documentRequest.getDocument());
            request.setAttribute( "textIndex", new Integer( no ));
            String label = attributes.getProperty( "label", "" );
            request.setAttribute( "label", label);
            request.setAttribute( "content", finalresult );
            request.setAttribute( "formats", formats );

            try {
                finalresult = Utility.getContents( "/imcms/"+documentRequest.getUser().getLanguageIso639_2()+"/jsp/docadmin/text/edit_text.jsp", request, response ) ;
            } catch ( ServletException e ) {
                throw new UnhandledException( e );
            } catch ( IOException e ) {
                throw new UnhandledException( e );
            }
        }

        return finalresult;
    }

    private String[] getLabelTags( Properties attributes, int no,
                                   String finalresult ) {
        String label = attributes.getProperty( "label", "" );
        String label_urlparam = "";
        if ( !"".equals( label ) ) {
            label_urlparam = removeHtmlTagsAndUrlEncode( label );
        }
        String[] replace_tags = new String[]{
            "#meta_id#", String.valueOf( document.getId() ),
            "#content_id#", "" + no,
            "#content#", finalresult,
            "#label_url#", label_urlparam,
            "#label#", label
        };
        return replace_tags;
    }

    private String removeHtmlTagsAndUrlEncode( String label ) {
        String label_urlparam;
        label_urlparam = Html.removeTags( label );
        label_urlparam = URLEncoder.encode( label_urlparam );
        return label_urlparam;
    }

    /**
     * Handle a <?imcms:image...?> tag
     *
     * @param attributes The attributes of the image tag
     */
    private String tagImage( Properties attributes ) {
        String mode = attributes.getProperty( "mode" );
        if ( mode != null && !"".equals( mode )
             && ( imageMode && "read".startsWith( mode ) // With mode="read", we don't want anything in imageMode.
                  || !imageMode && "write".startsWith( mode )// With mode="write", we don't want anything it not in imageMode.
                ) ) {
            return "";
        }
        // Get the 'no'-attribute of the <?imcms:text no="..."?>-tag
        String noStr = attributes.getProperty( "no" );
        int no = 0;
        String result = null;
        if ( null == noStr ) {
            no = implicitImageNumber++;
            result = (String)imageMap.get( new Integer( no ) );
        } else {
            noStr = noStr.trim();
            no = Integer.parseInt( noStr );
            result = (String)imageMap.get( new Integer( no ) );
            implicitImageNumber = no + 1;
        }
        if ( result == null ) {
            result = "";
        }

        String finalresult = result;
        if ( imageMode ) {
            String[] replace_tags = getLabelTags( attributes, no, finalresult );
            String admin_template_file;
            if ( "".equals( result ) ) { // no data in the db-field.
                admin_template_file = "textdoc/admin_no_image.frag";
            } else {               // data in the db-field.
                admin_template_file = "textdoc/admin_image.frag";
            }

            finalresult = service.getAdminTemplate( admin_template_file, documentRequest.getUser(), Arrays.asList( replace_tags ) );
        }

        return finalresult;
    }

    /**
     * Handle a <?imcms:datetime ...?> tag
     *
     * @param attributes The attributes of the datetime tag.
     *                   format attribute defines a user pattern to use when geting the date.
     *                   type attribute defines what date to get they can bee
     *                   now, created, modified, activated, archived
     */
    private String tagDatetime( Properties attributes ) {
        String format = attributes.getProperty( "format" ) == null
                        ? DateConstants.DATETIME_NO_SECONDS_FORMAT_STRING : attributes.getProperty( "format" );
        String type = attributes.getProperty( "type" );
        String lang = attributes.getProperty( "lang" );

        Date date;

        if ( type != null ) {
            type = type.toLowerCase();
            if ( "now".startsWith( type ) ) {
                date = new Date();
            } else if ( "created".startsWith( type ) ) {
                date = document.getCreatedDatetime();
            } else if ( "modified".startsWith( type ) ) {
                date = document.getModifiedDatetime();
            } else if ( "archived".startsWith( type ) ) {
                date = document.getArchivedDatetime();
            } else if ( "activated".startsWith( type ) ) {
                date = document.getPublicationStartDatetime();
            } else {
                return "<!-- <?imcms:datetime ... type=\"" + type + "\" is empty, wrong or does not exist! -->";
            }
        } else {
            date = new Date();
        }

        java.text.SimpleDateFormat formatter;
        if ( lang == null ) {
            formatter = new java.text.SimpleDateFormat( format );
        } else {
            formatter = new java.text.SimpleDateFormat( format, new Locale( lang, "" ) );
        }

        try {
            if ( null == date ) {
                return ""; // There was no date of the requested type (activated/archived?)
            } else {
                return formatter.format( date );
            }
        } catch ( IllegalArgumentException ex ) {
            return "<!-- imcms:datetime failed: " + ex.getMessage() + " -->";
        }
    }

    /**
     * Handle a <?imcms:user who='...' get='xxxxxxx'?> tag.
     */
    private String tagUser( Properties attributes ) {

        UserDomainObject user;
        String who = attributes.getProperty( "who" );

        if ( null != who && "creator".equalsIgnoreCase( who ) ) {
            user = documentRequest.getDocument().getCreator();
        } else if ( null != who && "publisher".equalsIgnoreCase( who ) ) {
            user = documentRequest.getDocument().getPublisher();
            if ( null == user ) {
                return "";
            }
        } else {
            user = documentRequest.getUser();
        }

        String result = "";
        String get = attributes.getProperty( "get" );

        if ( get != null && !"".equals( get ) ) {
            if ( "name".equalsIgnoreCase( get ) ) {
                result = user.getFullName();
            } else if ( "firstname".equalsIgnoreCase( get ) ) {
                result = user.getFirstName();
            } else if ( "lastname".equalsIgnoreCase( get ) ) {
                result = user.getLastName();
            } else if ( "company".equalsIgnoreCase( get ) ) {
                result = user.getCompany();
            } else if ( "address".equalsIgnoreCase( get ) ) {
                result = user.getAddress();
            } else if ( "zip".equalsIgnoreCase( get ) ) {
                result = user.getZip();
            } else if ( "city".equalsIgnoreCase( get ) ) {
                result = user.getCity();
            } else if ( "workphone".equalsIgnoreCase( get ) ) {
                result = user.getWorkPhone();
            } else if ( "mobilephone".equalsIgnoreCase( get ) ) {
                result = user.getMobilePhone();
            } else if ( "homephone".equalsIgnoreCase( get ) ) {
                result = user.getHomePhone();
            } else if ( "email".equalsIgnoreCase( get ) ) {
                result = user.getEmailAddress();
            }
        }

        return result;
    }

    private String tagCategories( Properties attributes ) {
        String categoryTypeName = attributes.getProperty( "type" );
        CategoryDomainObject[] categories;
        final String shouldOutputDescription = attributes.getProperty( "outputdescription" );
        if ( null == categoryTypeName ) {
            categories = document.getCategories();
        } else {
            CategoryTypeDomainObject categoryType = service.getDocumentMapper().getCategoryType( categoryTypeName );
            final CategoryDomainObject[] categoriesOfType = document.getCategoriesOfType( categoryType );
            categories = categoriesOfType;
        }
        String[] categoryStrings = new String[categories.length];
        for ( int i = 0; i < categories.length; i++ ) {
            CategoryDomainObject category = categories[i];
            String categoryString = category.getName();
            if ( null == categoryTypeName ) {
                categoryString = category.getType() + ": " + categoryString;
            }
            if ( "only".equalsIgnoreCase( shouldOutputDescription ) ) {
                categoryString = category.getDescription();
            } else if ( "true".equalsIgnoreCase( shouldOutputDescription ) ) {
                categoryString += " - " + category.getDescription();
            }
            categoryStrings[i] = categoryString;
        }
        String separator = attributes.getProperty( "separator", "," );
        return StringUtils.join( categoryStrings, separator );
    }

    private String tagLanguage( Properties attributes ) {
        String representation = attributes.getProperty( "representation" );
        if ( null == representation ) {
            return LanguageMapper.getCurrentLanguageNameInUsersLanguage( documentRequest.getUser(), document.getLanguageIso639_2() );
        } else if ( LanguageMapper.ISO639_2.equalsIgnoreCase( representation ) ) {
            return document.getLanguageIso639_2();
        } else {
            return "<!-- <?imcms:language ... representation=\"" + representation
                   + "\" is empty, wrong or does not exist! -->";
        }
    }

    private String singleTag( String tagname, Properties attributes, String entireMatch,
                        PatternMatcher patMat ) {
        String tagResult;

        if ( "text".equals( tagname ) ) {
            tagResult = tagText( attributes );
        } else if ( "image".equals( tagname ) ) {
            tagResult = tagImage( attributes );
        } else if ( "include".equals( tagname ) ) {
            tagResult = tagInclude( attributes, patMat );
        } else if ( "metaid".equals( tagname ) ) {
            tagResult = tagMetaId();
        } else if ( "datetime".equals( tagname ) ) {
            tagResult = tagDatetime( attributes );
        } else if ( "section".equals( tagname ) ) {
            tagResult = tagSection( attributes );
        } else if ( "sections".equals( tagname ) ) {
            tagResult = tagSections( attributes );
        } else if ( "user".equals( tagname ) ) {
            tagResult = tagUser( attributes );
        } else if ( "documentlanguage".equals( tagname ) ) {
            tagResult = tagLanguage( attributes );
        } else if ( "documentcategories".equals( tagname ) ) {
            tagResult = tagCategories( attributes );
        } else if ( "contextpath".equals( tagname ) ) {
            tagResult = tagContextPath();
        } else {
            tagResult = entireMatch ;
        }

        return tagResult;
    }

    private String tagContextPath() {
        return documentRequest.getHttpServletRequest().getContextPath();
    }

    public String replaceTags( Perl5Matcher patMat, String template ) {
        StringBuffer result = new StringBuffer() ;
        PatternMatcherInput input = new PatternMatcherInput( template );
        int lastMatchEndOffset = 0;
        while (patMat.contains( input, IMCMS_TAG_PATTERN )) {
            result.append(input.substring(lastMatchEndOffset, input.getMatchBeginOffset() )) ;

            MatchResult matres = patMat.getMatch();
            String entireTag = matres.group( 0 );
            String tagName = matres.group( 1 );
            String tagattributes = matres.group( 2 );
            Properties attributes = parseAttributes( tagattributes, patMat );

            String tagResult = entireTag ;

            if ("menu".equals( tagName ) || "velocity".equals( tagName )) {
                tagResult = findEndTag( tagName, attributes, tagResult, patMat, input );
            } else {
                tagResult = singleTag( tagName, attributes, entireTag, patMat );
            }
            addResultWithPrePost( result, tagResult, attributes );
            lastMatchEndOffset = input.getCurrentOffset();
        }
        result.append(template.substring( lastMatchEndOffset ));

        return result.toString() ;
    }

    private String findEndTag( String tagName, Properties attributes, String entireTag, Perl5Matcher patMat,
                               PatternMatcherInput input ) {
        String tagResult = entireTag ;
        PatternMatcherInput endTagInput = new PatternMatcherInput( input.getBuffer(), input.getMatchEndOffset(), input.getEndOffset()-input.getMatchEndOffset() ) ;
        while (patMat.contains( endTagInput, IMCMS_END_TAG_PATTERN)) {
            String endTagName = patMat.getMatch().group( 1 );
            if (endTagName.equals( tagName )) {
                String elementContent = endTagInput.preMatch() ;
                input.setCurrentOffset( endTagInput.getMatchEndOffset() );
                tagResult = blockTag( tagName, attributes, elementContent, patMat ) ;
                break ;
            }
        }
        return tagResult;
    }

    private void addResultWithPrePost( StringBuffer result, String tagResult, Properties attributes ) {
        if ( tagResult.length() > 0 ) {
            String preAttribute = attributes.getProperty( "pre" );
            if ( preAttribute != null ) {
                result.append(preAttribute) ;
            }
            result.append( tagResult );
            String postAttribute = attributes.getProperty( "post" );
            if ( postAttribute != null ) {
                result.append(postAttribute) ;
            }
        }
    }

    String blockTag( String tagname, Properties attributes, String content,
                PatternMatcher patternMatcher ) {
        String result = content;
        if ( "menu".equals( tagname ) ) {
            result = tagMenu( attributes, content, patternMatcher );
        } else if ( "velocity".equals( tagname ) ) {
            result = tagVelocity( content );
        }
        return result;
    }

    private String tagMenu( Properties attributes, String content, PatternMatcher patternMatcher ) {
        String result;
        MenuParser menuParser = new MenuParser( parserParameters );
        result = menuParser.tag( attributes, content, patternMatcher );
        return result;
    }

    private String tagVelocity( String content ) {
        String result;
        VelocityEngine velocityEngine = service.getVelocityEngine( parserParameters.getDocumentRequest().getUser() ) ;
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put( "request", parserParameters.getDocumentRequest().getHttpServletRequest() );
        velocityContext.put( "response", parserParameters.getDocumentRequest().getHttpServletResponse() );
        StringWriter stringWriter = new StringWriter();
        try {
            velocityEngine.init();
            velocityEngine.evaluate( velocityContext, stringWriter, null, content );
        } catch ( ParseErrorException e ) {
            throw new UnhandledException( e );
        } catch ( MethodInvocationException e ) {
            throw new UnhandledException( e );
        } catch ( ResourceNotFoundException e ) {
            throw new UnhandledException( e );
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        } catch ( Exception e ) {
            throw new UnhandledException( e );
        }
        result = stringWriter.toString();
        return result;
    }

    public class MetaIdHeaderHttpServletRequest extends HttpServletRequestWrapper {

        private int metaId;

        public MetaIdHeaderHttpServletRequest( HttpServletRequest request, int metaId ) {
            super(request);
            this.metaId = metaId;
        }

        public String getHeader( String headerName ) {
            if ("x-meta-id".equalsIgnoreCase( headerName )) {
                return ""+metaId ;
            }
            return super.getHeader( headerName ) ;
        }
    }

    /**
     * Take a String of attributes, as may be found inside a tag, (name="...", and so on...) and transform it into a Properties.
     */
    public static Properties parseAttributes( String attributes_string, PatternMatcher patternMatcher ) {
        Properties attributes = new Properties();

        PatternMatcherInput attributes_input = new PatternMatcherInput( attributes_string );
        while ( patternMatcher.contains( attributes_input, ATTRIBUTES_PATTERN ) ) {
            MatchResult attribute_matres = patternMatcher.getMatch();
            attributes.setProperty( attribute_matres.group( 1 ), attribute_matres.group( 3 ) );
        }

        return attributes;
    }


}
