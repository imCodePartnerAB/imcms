package imcode.server.parser;

import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.LanguageMapper;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import imcode.util.Html;
import imcode.util.ImcmsImageUtils;
import imcode.util.Utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.StringSubstitution;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.imcode.imcms.api.TextDocumentViewing;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.SectionFromIdTransformer;
import com.imcode.imcms.servlet.ImcmsSetupFilter;
import com.imcode.util.CountingIterator;
import java.util.regex.Matcher;
import org.apache.oro.text.regex.*;

public class TagParser {

    private static Pattern htmlPrebodyPattern;
    private static Pattern htmlPostbodyPattern;
    private static Pattern imcmsTagPattern;
    private static Pattern imcmsEndTagPattern;
    private static Pattern attributesPattern;
    
    private static Pattern widthPattern;
    private static Pattern heightPattern;
    private static Pattern maxWidthPattern;
    private static Pattern maxHeightPattern;

    private static final java.util.regex.Pattern REMOVE_ATTRS_PATTERN = java.util.regex.Pattern.compile(
            "(?<!-)(?:max-width|max-height|width|height)\\s*:\\s*\\d+\\s*px\\s*;?", java.util.regex.Pattern.CASE_INSENSITIVE);
    
    private final static Logger LOG = Logger.getLogger(TagParser.class.getName());

    static {
        Perl5Compiler patComp = new Perl5Compiler();

        try {

            htmlPrebodyPattern = patComp.compile("^.*?<[Bb][Oo][Dd][Yy].*?>", Perl5Compiler.SINGLELINE_MASK
                                                                              | Perl5Compiler.READ_ONLY_MASK);
            htmlPostbodyPattern = patComp.compile("<\\/[Bb][Oo][Dd][Yy]>.*$", Perl5Compiler.SINGLELINE_MASK
                                                                              | Perl5Compiler.READ_ONLY_MASK);
            imcmsTagPattern = patComp.compile("<\\?imcms:(\\w+)\\b(.*?)\\s*\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                                  | Perl5Compiler.READ_ONLY_MASK);
            imcmsEndTagPattern = patComp.compile("<\\?/imcms:(\\w+)\\s*\\?>", Perl5Compiler.SINGLELINE_MASK
                                                                              | Perl5Compiler.READ_ONLY_MASK);
            attributesPattern = patComp.compile("\\s+(\\w+)\\s*=\\s*([\"'])(.*?)\\2", Perl5Compiler.SINGLELINE_MASK
                                                                                      | Perl5Compiler.READ_ONLY_MASK);
            
            widthPattern = patComp.compile("(?:^|[\\s;])width\\s*:\\s*(\\d+)\\s*px", Perl5Compiler.CASE_INSENSITIVE_MASK);
            
            heightPattern = patComp.compile("(?:^|[\\s;])height\\s*:\\s*(\\d+)\\s*px", Perl5Compiler.CASE_INSENSITIVE_MASK);
            
            maxWidthPattern = patComp.compile("(?:^|[\\s;])max-width\\s*:\\s*(\\d+)\\s*px", Perl5Compiler.CASE_INSENSITIVE_MASK);
            
            maxHeightPattern = patComp.compile("(?:^|[\\s;])max-height\\s*:\\s*(\\d+)\\s*px", Perl5Compiler.CASE_INSENSITIVE_MASK);
            
        } catch ( MalformedPatternException ignored ) {
            // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
            LOG.fatal("Danger, Will Robinson!", ignored);
        }
    }

    private final static Substitution NULL_SUBSTITUTION = new StringSubstitution("");

    private TextDocumentParser textDocParser;

    private boolean includeMode;
    private int implicitIncludeNumber = 1;

    private boolean textMode;
    private int implicitTextNumber = 1;

    private boolean imageMode;
    private int[] implicitImageIndex = new int[] { 1 };

    private ImcmsServices service;
    private ParserParameters parserParameters;
    private DocumentRequest documentRequest;
    private TextDocumentDomainObject document;

    private TextDocumentViewing viewing;

    public TagParser(TextDocumentParser textdocparser, ParserParameters parserParameters
    ) {
        this.textDocParser = textdocparser;
        this.parserParameters = parserParameters;
        this.documentRequest = parserParameters.getDocumentRequest();
        this.document = (TextDocumentDomainObject) documentRequest.getDocument();
        this.service = documentRequest.getServices();

        this.includeMode = parserParameters.isIncludesMode() ;

        this.textMode = parserParameters.isTextMode();

        this.imageMode = parserParameters.isImageMode();

        this.viewing = new TextDocumentViewing(parserParameters);

    }

    /** Handle a <?imcms:metaid?> tag. */
    private String tagMetaId() {
        return "" + document.getId();
    }

    /** Handle a <?imcms:section?> tag. */
    private String tagSection(Properties attributes) {
        return tagSections(attributes);
    }

    /** Handle a <?imcms:section?> tag. */
    private String tagSections(Properties attributes) {
        Set sectionIds = document.getSectionIds();
        Iterator sectionsIterator = new TransformIterator(sectionIds.iterator(), new SectionFromIdTransformer(service));
        String separator = attributes.getProperty("separator", ",");

        return StringUtils.join(sectionsIterator, separator);
    }

    /**
     * Handle a <?imcms:include ...?> tag
     *
     * @param attributes The attributes of the include tag
     */
    public String tagInclude(Properties attributes) {
        if ( shouldOutputNothingAccordingToMode(attributes, includeMode) ) {
            return "";
        }

        int no;
        String attributevalue;

        if ( null
             != ( attributevalue = attributes.getProperty("no") ) ) {        // If we have the attribute no="number"...
            // Set the number of this include-tag
            try {
                no = Integer.parseInt(attributevalue.trim()); // Then set the number wanted
                implicitIncludeNumber = no + 1;
            } catch ( NumberFormatException ex ) {
                return "<!-- imcms:include no failed: " + ex + " -->";
            }
        } else if ( null != ( attributevalue = attributes.getProperty("path") ) ) {
            return includePath(attributevalue);
        } else if ( null
                    != ( attributevalue = attributes.getProperty("file") ) ) { // If we have the attribute file="filename"...
            return includeFile(attributevalue);
        } else if ( null
                    != ( attributevalue = attributes.getProperty("document") ) ) { // If we have the attribute document="meta-id"
            return includeDocument(attributevalue, attributes);
        } else if ( null
                    != ( attributevalue = attributes.getProperty("url") ) ) { // If we have an attribute of the form url="url:url"
            return includeUrl(attributevalue, attributes);
        } else { // If we have none of the attributes no, file, url, or document
            no = implicitIncludeNumber++; // Implicitly use the next number.
        }
        return includeEditing(attributes, no);
    }

    private String includePath(String path) {
        HttpServletRequest request = documentRequest.getHttpServletRequest();
        HttpServletRequestWrapper metaIdHeaderHttpServletRequest = new MetaIdHeaderHttpServletRequest(request, document.getId());
        try {
            return Utility.getContents(path, metaIdHeaderHttpServletRequest, documentRequest.getHttpServletResponse());
        } catch ( Exception ex ) {
            LOG.warn("imcms:include path "+path+" failed.",ex);
            return "<!-- imcms:include path failed: " + StringEscapeUtils.escapeHtml(ex.toString()) + " -->";
        }
    }

    private String includeEditing(Properties attributes, int no) {
        try {
            String label = getLabel(attributes);
            Integer includedDocumentId = document.getIncludedDocumentId(no);
            if ( includeMode ) {
                HttpServletRequest request = documentRequest.getHttpServletRequest();
                HttpServletResponse response = documentRequest.getHttpServletResponse();
                UserDomainObject user = documentRequest.getUser();
                try {
                    request.setAttribute("includingDocument", document);
                    request.setAttribute("includedDocumentId", includedDocumentId);
                    request.setAttribute("label", label);
                    request.setAttribute("includeIndex", new Integer(no));
                    return Utility.getContents("/imcms/" + user.getLanguageIso639_2()
                                               + "/jsp/docadmin/text/edit_include.jsp", request, response);
                } catch ( Exception e ) {
                    throw new UnhandledException(e);
                }
            } else if ( parserParameters.getIncludeLevel() > 0 ) {
                if ( null == includedDocumentId ) {
                    return "";
                }
                ParserParameters includedDocumentParserParameters = createIncludedDocumentParserParameters(parserParameters, includedDocumentId.intValue(), attributes);
                StringWriter writer = new StringWriter();
                textDocParser.untimedParsePage(includedDocumentParserParameters, writer);
                PatternMatcher patMat = new Perl5Matcher();
                String documentStr = Util.substitute(patMat, htmlPrebodyPattern, NULL_SUBSTITUTION, writer.toString());
                documentStr = Util.substitute(patMat, htmlPostbodyPattern, NULL_SUBSTITUTION, documentStr);
                return documentStr;
            } else {
                return "<!-- imcms:include failed: max include-level reached. -->";
            }
        } catch ( IOException ex ) {
            return "<!-- imcms:include failed: " + ex + " -->";
        }
    }

    private String includeUrl(String attribute, Properties attributes) {
        String urlStr = attribute;
        try {
            String commaSeparatedNamesOfParametersToSend = attributes.getProperty("sendparameters");

            urlStr += -1 == urlStr.indexOf('?') ? "?" : "&";
            Set parameterNamesToSend = createSetFromCommaSeparatedString(commaSeparatedNamesOfParametersToSend);
            urlStr += createQueryStringFromRequest(documentRequest.getHttpServletRequest(), parameterNamesToSend);

            if ( urlStr.startsWith("/") ) {  // lets add hostname if we got a relative path
                urlStr = documentRequest.getHttpServletRequest().getScheme()
                         + "://" + documentRequest.getHttpServletRequest().getServerName()
                         + ':'
                         + documentRequest.getHttpServletRequest().getServerPort()
                         + urlStr;
            }
            URL url = new URL(urlStr);
            String urlProtocol = url.getProtocol();
            if ( "file".equalsIgnoreCase(urlProtocol) ) { // Make sure we don't have to defend against file://urls...
                return "<!-- imcms:include url failed: file-url not allowed -->";
            }
            String sessionId = documentRequest.getHttpServletRequest().getSession().getId();
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent",
                                             documentRequest.getHttpServletRequest().getHeader("User-agent"));
            if ( null != attributes.getProperty("sendsessionid") ) {
                urlConnection.addRequestProperty("Cookie", ImcmsSetupFilter.JSESSIONID_COOKIE_NAME + "="
                                                           + sessionId);
            }
            if ( null != attributes.getProperty("sendcookies") ) {
                Cookie[] requestCookies = documentRequest.getHttpServletRequest().getCookies();
                for ( int i = 0; requestCookies != null && i < requestCookies.length; ++i ) {
                    Cookie theCookie = requestCookies[i];
                    if ( !ImcmsSetupFilter.JSESSIONID_COOKIE_NAME.equals(theCookie.getName()) ) {
                        urlConnection.addRequestProperty("Cookie", theCookie.getName() + "="
                                                                   + theCookie.getValue());
                    }
                }
            }
            if ( null != attributes.getProperty("sendmetaid") ) {
                urlConnection.setRequestProperty("X-Meta-Id", "" + document.getId());
            }

            InputStream connectionInputStream = urlConnection.getInputStream();
            String contentType = urlConnection.getContentType();
            String contentEncoding = StringUtils.substringAfter(contentType, "charset=");
            if ( StringUtils.isBlank(contentEncoding) ) {
                contentEncoding = Imcms.DEFAULT_ENCODING;
            }
            InputStreamReader urlInput = null;
            try {
                urlInput = new InputStreamReader(connectionInputStream, contentEncoding);
                int charsRead;
                final int URL_BUFFER_LEN = 16384;
                char[] buffer = new char[URL_BUFFER_LEN];
                StringBuffer urlResult = new StringBuffer();
                while ( -1 != ( charsRead = urlInput.read(buffer, 0, URL_BUFFER_LEN) ) ) {
                    urlResult.append(buffer, 0, charsRead);
                }
                return urlResult.toString();
            } finally {
                if ( null != urlInput ) {
                    urlInput.close();
                }
            }
        } catch ( Exception ex ) {
            LOG.warn("imcms:include url "+urlStr+" failed.",ex);
            return "<!-- imcms:include url failed: " + StringEscapeUtils.escapeHtml(ex.toString()) + " -->";
        }
    }

    private String includeDocument(String attributevalue, Properties attributes) {
        try {
            if ( parserParameters.getIncludeLevel() > 0 ) {
                int included_meta_id = Integer.parseInt(attributevalue);
                ParserParameters includedDocumentParserParameters = createIncludedDocumentParserParameters(parserParameters, included_meta_id, attributes);
                StringWriter writer = new StringWriter();
                textDocParser.untimedParsePage(includedDocumentParserParameters, writer);
                PatternMatcher patMat = new Perl5Matcher();
                String documentStr = Util.substitute(patMat, htmlPrebodyPattern, NULL_SUBSTITUTION, writer.toString());
                documentStr = Util.substitute(patMat, htmlPostbodyPattern, NULL_SUBSTITUTION, documentStr);
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

    private String includeFile(String attributevalue) {// Fetch a file from the disk
        try {
            return replaceTags(service.getFileCache().getCachedFileString(new File(service.getIncludePath(), attributevalue)), false); // Get a file from the include directory
        } catch ( IOException ex ) {
            return "<!-- imcms:include file failed: " + ex + " -->";
        }
    }

    private ParserParameters createIncludedDocumentParserParameters(ParserParameters parserParameters,
                                                                    int included_meta_id, Properties attributes) {
        ParserParameters includedParserParameters = null;
        try {
            includedParserParameters = (ParserParameters) parserParameters.clone();
            includedParserParameters.setTemplate(attributes.getProperty("template"));
            includedParserParameters.setParameter(attributes.getProperty("param"));
            includedParserParameters.getDocumentRequest().setDocument(service.getDocumentMapper().getDocument(included_meta_id));
            includedParserParameters.getDocumentRequest().setReferrer(document);
            includedParserParameters.setFlags(0);
            includedParserParameters.setIncludeLevel(parserParameters.getIncludeLevel() - 1);
            includedParserParameters.setAdminButtonsVisible(false);
        } catch ( CloneNotSupportedException e ) {
            // ignored, supported
        }
        return includedParserParameters;
    }

    private String createQueryStringFromRequest(HttpServletRequest httpServletRequest, Set parameterNamesToSend) {
        List parameterNameValuePairs = new ArrayList();
        Enumeration parameterNames = httpServletRequest.getParameterNames();
        while ( parameterNames.hasMoreElements() ) {
            String parameterName = (String) parameterNames.nextElement();
            if ( null == parameterNamesToSend || parameterNamesToSend.contains(parameterName) ) {
                String[] parameterValues = httpServletRequest.getParameterValues(parameterName);
                for ( int i = 0; i < parameterValues.length; i++ ) {
                    String parameterValue = parameterValues[i];
                    parameterNameValuePairs.add(URLEncoder.encode(parameterName) + '='
                                                + URLEncoder.encode(parameterValue));
                }
            }
        }
        return StringUtils.join(parameterNameValuePairs.iterator(), '&');
    }

    private Set createSetFromCommaSeparatedString(String commaSeparatedNames) {
        if ( null == commaSeparatedNames ) {
            return null;
        }
        StringTokenizer commaAndWhitespaceSeparatedTokenizer = new StringTokenizer(commaSeparatedNames, ", \t\r\n");
        Set names = new HashSet();
        while ( commaAndWhitespaceSeparatedTokenizer.hasMoreTokens() ) {
            String parameterName = commaAndWhitespaceSeparatedTokenizer.nextToken();
            names.add(parameterName);
        }
        return names;
    }

    /**
     * Handle a <?imcms:text ...?> tag
     *
     * @param attributes The attributes of the text tag
     *                   attributes:
     *                   - no  Text number in document
     *                   - document  Document to get text from ( id or alias )
     *                   - label Label to show in write mode
     *                   - mode  ( "read" | "write" )
     *                   - filter
     *                   - formats
     *                   - rows
     */
    public String tagText(Properties attributes) {
        TextDocumentDomainObject textDocumentToUse = getTextDocumentToUse(attributes);
        if ( shouldOutputNothingAccordingToMode(attributes, textMode) || textDocumentToUse==null) {
            return "";
        }
        // Get the 'no'-attribute of the <?imcms:text no="..."?>-tag
        String noStr = attributes.getProperty("no");
        int no;
        TextDomainObject text;
        if ( null == noStr ) {
            no = implicitTextNumber++;
            text = textDocumentToUse.getText(no);
        } else {
            noStr = noStr.trim();
            no = Integer.parseInt(noStr);
            text = textDocumentToUse.getText(no);
            implicitTextNumber = no + 1;
        }
        String result = "";
        if ( text != null ) {
            result = text.toHtmlString();
            if ( text.getType() == TextDomainObject.TEXT_TYPE_HTML ) {
                result = replaceTags(result, true);
            }
        }

        if (textMode && (textDocumentToUse.getId() == document.getId())) {
            HttpServletRequest request = documentRequest.getHttpServletRequest();
            HttpServletResponse response = documentRequest.getHttpServletResponse();
            String formatsAttribute = attributes.getProperty("formats", "");
            String[] formats = null != formatsAttribute ? formatsAttribute.split("\\W+") : null;
            request.setAttribute("document", documentRequest.getDocument());
            request.setAttribute("textIndex", new Integer(no));
            String label = getLabel(attributes);
            request.setAttribute("label", label);
            request.setAttribute("content", result);
            request.setAttribute("formats", formats);
            request.setAttribute("rows", attributes.getProperty("rows"));

            try {
                result = Utility.getContents("/imcms/" + documentRequest.getUser().getLanguageIso639_2()
                                             + "/jsp/docadmin/text/edit_text.jsp", request, response);
            } catch ( ServletException e ) {
                throw new UnhandledException(e);
            } catch ( IOException e ) {
                throw new UnhandledException(e);
            }
        }

        return result;
    }

    private static boolean shouldOutputNothingAccordingToMode(Properties attributes, boolean mode) {
        String modeAttribute = attributes.getProperty("mode");
        return StringUtils.isNotBlank(modeAttribute)
               && ( mode && "read".startsWith(modeAttribute) // With mode="read", we don't want anything in textMode.
                    || !mode
                       && "write".startsWith(modeAttribute)// With mode="write", we don't want anything unless we're in textMode.
        );
    }

    private static String getLabel(Properties attributes) {
        return attributes.getProperty("label", "").replaceAll("\\s+", " ");
    }

    private static String[] getLabelTags(Properties attributes, int no,
                                  String finalresult, TextDocumentDomainObject document) {
        String label = getLabel(attributes);
        String label_urlparam = "";
        if ( !"".equals(label) ) {
            label_urlparam = removeHtmlTagsAndUrlEncode(label);
        }
        return new String[] {
                "#meta_id#", String.valueOf(document.getId()),
                "#content_id#", "" + no,
                "#content#", finalresult,
                "#label_url#", label_urlparam,
                "#label#", label
        };
    }

    private static String removeHtmlTagsAndUrlEncode(String label) {
        return URLEncoder.encode(Html.removeTags(label));
    }

    /**
     * Handle a <?imcms:image...?> tag
     *
     * @param attributes The attributes of the image tag
     */
    public String tagImage(Properties attributes) {
        return tagImage(attributes, imageMode, implicitImageIndex, documentRequest.getUser(), document, documentRequest.getHttpServletRequest(), service);
    }

    public String tagImage(Properties attributes, boolean imageMode, int[] implicitImageIndex,
                           UserDomainObject user, TextDocumentDomainObject document,
                           HttpServletRequest httpServletRequest, ImcmsServices service) {

        TextDocumentDomainObject textDocumentToUse = getTextDocumentToUse(attributes);
        if ( shouldOutputNothingAccordingToMode(attributes, imageMode) || textDocumentToUse==null) {
            return "";
        }
        // Get the 'no'-attribute of the <?imcms:text no="..."?>-tag
        String noStr = attributes.getProperty("no");
        int imageIndex;
        if ( null == noStr ) {
            imageIndex = implicitImageIndex[0]++;
        } else {
            noStr = noStr.trim();
            imageIndex = Integer.parseInt(noStr);
            implicitImageIndex[0] = imageIndex + 1;
        }
        ImageDomainObject image = textDocumentToUse.getImage(imageIndex) ;
        ImageSource imageSource = image.getSource();
        String imageTag = "" ;
        String style = (String) attributes.getProperty("style");
        if ( !( imageSource instanceof FileDocumentImageSource )
             || imageMode
             || user.canAccess(( (FileDocumentImageSource) imageSource ).getFileDocument()) ) {
            
            if (style != null) {
                Matcher matcher = REMOVE_ATTRS_PATTERN.matcher(style);
                
                String cleanedStyle = matcher.replaceAll(" ");
                
                attributes.put("style", cleanedStyle);
            }
            
            imageTag = ImcmsImageUtils.getImageHtmlTag(textDocumentToUse.getId(), image, httpServletRequest, attributes);
        }

        if ( imageMode && (textDocumentToUse.getId() == document.getId())) {
            String[] replace_tags = getLabelTags(attributes, imageIndex, imageTag, textDocumentToUse);
            String admin_template_file;
            if ( "".equals(imageTag) ) { // no data in the db-field.
                admin_template_file = "textdoc/admin_no_image.frag";
            } else {               // data in the db-field.
                admin_template_file = "textdoc/admin_image.frag";
            }
            
            String imageWidth = "0";
            String imageHeight = "0";
            String maxWidth = "0";
            String maxHeight = "0";
            
            if (style != null) {
            	PatternMatcher matcher = new Perl5Matcher();
                
                if (matcher.contains(style, widthPattern)) {
            		imageWidth = matcher.getMatch().group(1);
            	}
            	if (matcher.contains(style, heightPattern)) {
            		imageHeight = matcher.getMatch().group(1);
            	}
                if (matcher.contains(style, maxWidthPattern)) {
                    maxWidth = matcher.getMatch().group(1);
                }
                if (matcher.contains(style, maxHeightPattern)) {
                    maxHeight = matcher.getMatch().group(1);
                }
            }
            
            
            List<String> replaceTags = new ArrayList<String>(replace_tags.length + 8);
            CollectionUtils.addAll(replaceTags, replace_tags);
            replaceTags.add("#image_width#");
            replaceTags.add(imageWidth);
            replaceTags.add("#image_height#");
            replaceTags.add(imageHeight);
            replaceTags.add("#max_width#");
            replaceTags.add(maxWidth);
            replaceTags.add("#max_height#");
            replaceTags.add(maxHeight);
            
            imageTag = service.getAdminTemplate(admin_template_file, user, replaceTags);
        }

        return imageTag;
    }

    /**
     * Handle a <?imcms:datetime ...?> tag
     *
     * @param attributes The attributes of the datetime tag.
     *                   format attribute defines a user pattern to use when geting the date.
     *                   type attribute defines what date to get they can bee
     *                   now, created, modified, activated, archived
     */
    private String tagDatetime(Properties attributes) {
        String format = attributes.getProperty("format") == null
                        ? DateConstants.DATETIME_NO_SECONDS_FORMAT_STRING : attributes.getProperty("format");
        String type = attributes.getProperty("type");
        String lang = attributes.getProperty("lang");

        Date date;

        if ( type != null ) {
            type = type.toLowerCase();
            if ( "now".startsWith(type) ) {
                date = new Date();
            } else if ( "created".startsWith(type) ) {
                date = document.getCreatedDatetime();
            } else if ( "modified".startsWith(type) ) {
                date = document.getModifiedDatetime();
            } else if ( "archived".startsWith(type) ) {
                date = document.getArchivedDatetime();
            } else if ( "activated".startsWith(type) ) {
                date = document.getPublicationStartDatetime();
            } else {
                return "<!-- <?imcms:datetime ... type=\"" + type + "\" is empty, wrong or does not exist! -->";
            }
        } else {
            date = new Date();
        }

        java.text.SimpleDateFormat formatter;
        if ( lang == null ) {
            formatter = new java.text.SimpleDateFormat(format);
        } else {
            formatter = new java.text.SimpleDateFormat(format, new Locale(lang, ""));
        }

        try {
            if ( null == date ) {
                return ""; // There was no date of the requested type (activated/archived?)
            } else {
                return formatter.format(date);
            }
        } catch ( IllegalArgumentException ex ) {
            return "<!-- imcms:datetime failed: " + ex.getMessage() + " -->";
        }
    }

    /** Handle a <?imcms:user who='...' get='xxxxxxx'?> tag. */
    private String tagUser(Properties attributes) {

        UserDomainObject user;
        String who = attributes.getProperty("who");

        ImcmsAuthenticatorAndUserAndRoleMapper userMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();
        if ( null != who && "creator".equalsIgnoreCase(who) ) {
            user = userMapper.getUser(documentRequest.getDocument().getCreatorId());
        } else if ( null != who && "publisher".equalsIgnoreCase(who) ) {
            Integer publisherId = documentRequest.getDocument().getPublisherId();
            if (null == publisherId) {
                return "" ;
            }
            user = userMapper.getUser(publisherId.intValue()) ;
        } else {
            user = documentRequest.getUser();
        }

        String result = "";
        String get = attributes.getProperty("get");

        if ( get != null && !"".equals(get) ) {
            if ( "name".equalsIgnoreCase(get) ) {
                result = user.getFullName();
            } else if ( "firstname".equalsIgnoreCase(get) ) {
                result = user.getFirstName();
            } else if ( "lastname".equalsIgnoreCase(get) ) {
                result = user.getLastName();
            } else if ( "company".equalsIgnoreCase(get) ) {
                result = user.getCompany();
            } else if ( "address".equalsIgnoreCase(get) ) {
                result = user.getAddress();
            } else if ( "zip".equalsIgnoreCase(get) ) {
                result = user.getZip();
            } else if ( "city".equalsIgnoreCase(get) ) {
                result = user.getCity();
            } else if ( "workphone".equalsIgnoreCase(get) ) {
                result = user.getWorkPhone();
            } else if ( "mobilephone".equalsIgnoreCase(get) ) {
                result = user.getMobilePhone();
            } else if ( "homephone".equalsIgnoreCase(get) ) {
                result = user.getHomePhone();
            } else if ( "email".equalsIgnoreCase(get) ) {
                result = user.getEmailAddress();
            }
        }

        return result;
    }

    private String tagCategories(Properties attributes) {
        String categoryTypeName = attributes.getProperty("type");
        final String shouldOutputDescription = attributes.getProperty("outputdescription");
        CategoryMapper categoryMapper = service.getCategoryMapper();
        Set categories ;
        if ( null == categoryTypeName ) {
            categories = categoryMapper.getCategories(document.getCategoryIds());
        } else {
            CategoryTypeDomainObject categoryType = categoryMapper.getCategoryTypeByName(categoryTypeName);
            categories = categoryMapper.getCategoriesOfType(categoryType, document.getCategoryIds());
        }
        String[] categoryStrings = new String[categories.size()];
        for ( CountingIterator iterator = new CountingIterator(categories.iterator()); iterator.hasNext(); ) {
            CategoryDomainObject category = (CategoryDomainObject) iterator.next();
            String categoryString = category.getName();
            if ( null == categoryTypeName ) {
                categoryString = category.getType() + ": " + categoryString;
            }
            if ( "only".equalsIgnoreCase(shouldOutputDescription) ) {
                categoryString = category.getDescription();
            } else if ( "true".equalsIgnoreCase(shouldOutputDescription) ) {
                categoryString += " - " + category.getDescription();
            }
            categoryStrings[iterator.getCount()-1] = categoryString;
        }
        String separator = attributes.getProperty("separator", ",");
        return StringUtils.join(categoryStrings, separator);
    }

    private String tagLanguage(Properties attributes) {
        String representation = attributes.getProperty("representation");
        if ( null == representation ) {
            return service.getLanguageMapper().getCurrentLanguageNameInUsersLanguage(documentRequest.getUser(), document.getLanguageIso639_2());
        } else if ( LanguageMapper.ISO639_2.equalsIgnoreCase(representation) ) {
            return document.getLanguageIso639_2();
        } else {
            return "<!-- <?imcms:language ... representation=\"" + representation
                   + "\" is empty, wrong or does not exist! -->";
        }
    }

    private String singleTag(String tagname, Properties attributes, String entireMatch,
                             PatternMatcher patMat, boolean insideText) {
        String tagResult = entireMatch;

        if ( "contextpath".equals(tagname) ) {
            tagResult = tagContextPath();
        } else if ( !insideText ) {
            if ( "text".equals(tagname) ) {
                tagResult = tagText(attributes);
            } else if ( "image".equals(tagname) ) {
                tagResult = tagImage(attributes);
            } else if ( "include".equals(tagname) ) {
                tagResult = tagInclude(attributes);
            } else if ( "metaid".equals(tagname) ) {
                tagResult = tagMetaId();
            } else if ( "datetime".equals(tagname) ) {
                tagResult = tagDatetime(attributes);
            } else if ( "section".equals(tagname) ) {
                tagResult = tagSection(attributes);
            } else if ( "sections".equals(tagname) ) {
                tagResult = tagSections(attributes);
            } else if ( "user".equals(tagname) ) {
                tagResult = tagUser(attributes);
            } else if ( "documentlanguage".equals(tagname) ) {
                tagResult = tagLanguage(attributes);
            } else if ( "documentcategories".equals(tagname) ) {
                tagResult = tagCategories(attributes);
            }
        }

        return tagResult;
    }

    private String tagContextPath() {
        return documentRequest.getHttpServletRequest().getContextPath();
    }

    public String replaceTags(String template, boolean insideText) {
        StringBuffer result = new StringBuffer();
        PatternMatcherInput input = new PatternMatcherInput(template);
        int lastMatchEndOffset = 0;
        PatternMatcher patMat = new Perl5Matcher();
        while ( patMat.contains(input, imcmsTagPattern) ) {
            result.append(input.substring(lastMatchEndOffset, input.getMatchBeginOffset()));

            MatchResult matres = patMat.getMatch();
            String entireTag = matres.group(0);
            String tagName = matres.group(1);
            String tagattributes = matres.group(2);
            Properties attributes = parseAttributes(tagattributes, patMat, documentRequest.getHttpServletRequest());

            String tagResult = entireTag;

            if ( "menu".equals(tagName) || "velocity".equals(tagName) ) {
                tagResult = findEndTag(tagName, attributes, tagResult, patMat, input);
            } else {
                tagResult = singleTag(tagName, attributes, entireTag, patMat, insideText);
            }
            result.append(addPreAndPost(attributes, tagResult)) ;
            lastMatchEndOffset = input.getCurrentOffset();
        }
        result.append(template.substring(lastMatchEndOffset));

        return result.toString();
    }

    private String findEndTag(String tagName, Properties attributes, String entireTag, PatternMatcher patMat,
                              PatternMatcherInput input) {
        String tagResult = entireTag;
        PatternMatcherInput endTagInput = new PatternMatcherInput(input.getBuffer(), input.getMatchEndOffset(),
                                                                  input.getEndOffset() - input.getMatchEndOffset());
        while ( patMat.contains(endTagInput, imcmsEndTagPattern) ) {
            String endTagName = patMat.getMatch().group(1);
            if ( endTagName.equals(tagName) ) {
                String elementContent = endTagInput.preMatch();
                input.setCurrentOffset(endTagInput.getMatchEndOffset());
                tagResult = blockTag(tagName, attributes, elementContent, patMat);
                break;
            }
        }
        return tagResult;
    }

    public static String addPreAndPost(Properties attributes, String tagResult) {
        if ( 0 == tagResult.length() ) {
            return "" ;
        }
        String preAttribute = StringUtils.defaultString(attributes.getProperty("pre"));
        String postAttribute = StringUtils.defaultString(attributes.getProperty("post"));
        return preAttribute+tagResult+postAttribute;
    }

    String blockTag(String tagname, Properties attributes, String content,
                    PatternMatcher patternMatcher) {
        String result = content;
        if ( "menu".equals(tagname) ) {
            result = tagMenu(attributes, content, patternMatcher);
        } else if ( "velocity".equals(tagname) ) {
            result = tagVelocity(content);
        }
        return result;
    }

    private String tagMenu(Properties attributes, String content, PatternMatcher patternMatcher) {
        MenuParser menuParser = new MenuParser(parserParameters);
        return menuParser.tag(attributes, content, patternMatcher, this);
    }

    private String tagVelocity(String content) {
        VelocityEngine velocityEngine = service.getVelocityEngine(parserParameters.getDocumentRequest().getUser());
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("request", parserParameters.getDocumentRequest().getHttpServletRequest());
        velocityContext.put("response", parserParameters.getDocumentRequest().getHttpServletResponse());
        velocityContext.put("viewing", viewing);
        velocityContext.put("document", viewing.getTextDocument());
        velocityContext.put("util", new VelocityTagUtil());
        StringWriter stringWriter = new StringWriter();
        try {
            velocityEngine.init();
            velocityEngine.evaluate(velocityContext, stringWriter, null, content);
        } catch ( Exception e ) {
            throw new UnhandledException(e);
        }
        return stringWriter.toString();
    }

    private TextDocumentDomainObject getTextDocumentToUse(Properties attributes) {
        String documentName = attributes.getProperty("document");
        TextDocumentDomainObject textDocumentToUse = document;
        if(StringUtils.isNotBlank(documentName)) {
            textDocumentToUse = null;
            try{
                textDocumentToUse = (TextDocumentDomainObject)service.getDocumentMapper().getDocument(documentName);
            }catch(ClassCastException e ){/* return null */}
        }
       return textDocumentToUse;
    }

    public class MetaIdHeaderHttpServletRequest extends HttpServletRequestWrapper {

        private int metaId;

        public MetaIdHeaderHttpServletRequest(HttpServletRequest request, int metaId) {
            super(request);
            this.metaId = metaId;
        }

        public String getHeader(String headerName) {
            if ( "x-meta-id".equalsIgnoreCase(headerName) ) {
                return "" + metaId;
            }
            return super.getHeader(headerName);
        }
    }

    /** Take a String of attributes, as may be found inside a tag, (name="...", and so on...) and transform it into a Properties. */
    public Properties parseAttributes(String attributes_string, PatternMatcher patternMatcher,
                                      HttpServletRequest request) {
        Properties attributes = new Properties();

        PatternMatcherInput attributes_input = new PatternMatcherInput(attributes_string);
        while ( patternMatcher.contains(attributes_input, attributesPattern) ) {
            MatchResult attribute_matres = patternMatcher.getMatch();
            String escapedValue = attribute_matres.group(3);
            String value = StringEscapeUtils.unescapeHtml(escapedValue);
            value = tagVelocity(value);
            attributes.setProperty(attribute_matres.group(1), value);
        }

        return attributes;
    }

    public static class VelocityTagUtil {

        public String escapeHtml(String s) {
            return StringEscapeUtils.escapeHtml(s);
        }
    }

}
