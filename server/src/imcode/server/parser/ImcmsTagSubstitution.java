package imcode.server.parser ;

import java.io.* ;
import java.util.* ;
import java.net.* ;

import org.apache.oro.text.regex.* ;
import imcode.server.* ;
import imcode.util.* ;
import java.text.DateFormatSymbols;

import org.apache.log4j.Category;

public class ImcmsTagSubstitution implements Substitution, IMCConstants {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private static Pattern HTML_PREBODY_PATTERN  = null ;
    private static Pattern HTML_POSTBODY_PATTERN  = null ;
    private static Pattern IMCMS_TAG_ATTRIBUTES_PATTERN  = null ;
    private static Pattern READRUNNER_FILTER_STOPSEP_PATTERN = null ;
    private static Pattern READRUNNER_FILTER_STOP_PATTERN = null ;
    private static Pattern READRUNNER_FILTER_SEP_PATTERN = null ;
    private static Pattern READRUNNER_FILTER_PATTERN = null ;
    private static Pattern HTML_TAG_PATTERN  = null ;
    private static Pattern HTML_ESCAPE_HIDE_PATTERN  = null ;
    private static Pattern HTML_ESCAPE_UNHIDE_PATTERN  = null ;

    private static Category log = Category.getInstance("server");

    private FileCache fileCache = new FileCache() ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {

	    IMCMS_TAG_ATTRIBUTES_PATTERN = patComp.compile("\\s*(\\w+)\\s*=\\s*([\"'])(.*?)\\2", Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_PREBODY_PATTERN = patComp.compile("^.*?<[Bb][Oo][Dd][Yy].*?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_POSTBODY_PATTERN = patComp.compile("<\\/[Bb][Oo][Dd][Yy]>.*$", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

	    final String READRUNNER_PATTERN_START = "([^\\s](?:.*?" ;
	    final String READRUNNER_PATTERN_END = "\\B\\S*|.*\\S))\\s*$?" ;

	    READRUNNER_FILTER_STOPSEP_PATTERN = patComp.compile(READRUNNER_PATTERN_START+"[.?!,:;\\\\/-]"+READRUNNER_PATTERN_END , Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    READRUNNER_FILTER_STOP_PATTERN    = patComp.compile(READRUNNER_PATTERN_START+"[.?!]"+READRUNNER_PATTERN_END, Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    READRUNNER_FILTER_SEP_PATTERN     = patComp.compile(READRUNNER_PATTERN_START+"[,:;\\\\/-]"+READRUNNER_PATTERN_END, Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    READRUNNER_FILTER_PATTERN         = patComp.compile("([^\\s].*?)\\s*$" , Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

	    HTML_TAG_PATTERN = patComp.compile("<[^>]+?>",Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_ESCAPE_HIDE_PATTERN = patComp.compile("(&#?\\w+;)",Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_ESCAPE_UNHIDE_PATTERN = patComp.compile("_(&#?\\w+;)_",Perl5Compiler.READ_ONLY_MASK) ;

	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    log.fatal("Danger, Will Robinson!",ignored) ;
	}
    }

    private final Substitution NULL_SUBSTITUTION = new StringSubstitution("") ;

    private final Substitution HTML_ESCAPE_HIDE_SUBSTITUTION = new Perl5Substitution("_$1_") ;
    private final Substitution HTML_ESCAPE_UNHIDE_SUBSTITUTION = new Perl5Substitution("$1") ;

    private TextDocumentParser textDocParser ;
    private User user ;
    private int meta_id ;

    private File templatePath ;
    private String servletUrl ;

    private boolean includeMode ;
    private int includeLevel ;
    private File includePath ;
    private int implicitIncludeNumber = 1 ;

    private Map textMap ;
    private boolean textMode ;
    private int implicitTextNumber = 1 ;

    private Map imageMap ;
    private boolean imageMode ;
    private String imageUrl ;
    private int implicitImageNumber = 1 ;

    private Document document;

    private HashMap included_docs = new HashMap() ;

    private ParserParameters parserParameters ;

    private class ReadrunnerQuoteSubstitution implements Substitution {
	private int readrunnerQuoteSubstitutionCount = 0 ;

	public void appendSubstitution(java.lang.StringBuffer appendBuffer,
				       MatchResult match,
				       int substitutionCount,
				       PatternMatcherInput originalInput,
				       PatternMatcher matcher,
				       Pattern pattern) {
	    appendBuffer.append("<q>") ;
	    appendBuffer.append(match.group(1)) ;
	    appendBuffer.append("</q>") ;
	    ++readrunnerQuoteSubstitutionCount ;
	}

	public int getReadrunnerQuoteSubstitutionCount() {
	    return readrunnerQuoteSubstitutionCount ;
	}
    } ;

    private ReadrunnerQuoteSubstitution readrunnerQuoteSubstitution = new ReadrunnerQuoteSubstitution() ;


    /**
       @param user           The user
       @param meta_id        The document-id
       @param included_list  A list of (include-id, included-meta-id, ...)
       @param includemode    Whether to include the admin-template instead of the included document.
       @param includelevel   The number of levels of recursion we've gone through.
    **/
    public ImcmsTagSubstitution (TextDocumentParser textdocparser, User user, int meta_id,
				 File templatepath, String servleturl,
				 List included_list, boolean includemode, int includelevel, File includepath,
				 Map textmap, boolean textmode,
				 Map imagemap, boolean imagemode, String imageurl,Document theDoc, ParserParameters parserParameters) {
	this.textDocParser = textdocparser ;
	this.user = user ;
	this.meta_id = meta_id ;

	this.templatePath = templatepath ;
	this.servletUrl = servleturl ;

	this.includeMode = includemode ;
	this.includeLevel = includelevel ;
	this.includePath = includepath ;
	for (Iterator i = included_list.iterator(); i.hasNext() ;) {
	    included_docs.put(i.next(), i.next()) ;
	}

	this.textMap = textmap ;
	this.textMode = textmode ;

	this.imageMap = imagemap ;
	this.imageMode = imagemode ;
	this.imageUrl  = imageurl ;
	this.document = theDoc;
	this.parserParameters = parserParameters ;
    }

    /**
       Handle a <?imcms:meta-id?> tag.

    **/
    public String tagMetaId () {
	return ""+meta_id ;
    }

    /**
       Handle a <?imcms:include ...?> tag

       @param attributes The attributes of the include tag
       @param patMat     A pattern matcher.
    **/
    public String tagInclude (Properties attributes, PatternMatcher patMat) {
	int no = 0 ;
	String attributevalue ;

	//lets get the templates simplename or null if there isn't one
	ParserParameters paramsToParse = new ParserParameters() ;
	paramsToParse.setTemplate(attributes.getProperty("template")) ;
	paramsToParse.setParameter(attributes.getProperty("param")) ;

	if (null != (attributevalue = attributes.getProperty("no"))) {	    // If we have the attribute no="number"...
	    // Set the number of this include-tag
	    try {
		no = Integer.parseInt(attributevalue) ; // Then set the number wanted
		implicitIncludeNumber = no + 1 ;
	    }
	    catch (NumberFormatException ex) {
		return "" ;
	    }
	} else if (null != (attributevalue = attributes.getProperty("file"))) { // If we have the attribute file="filename"...
	    // Fetch a file from the disk
	    try {
		return fileCache.getCachedFileString(new File(includePath, attributevalue)) ; // Get a file from the include directory
	    } catch (IOException ignored) {}
	    return "" ;
	} else if (null != (attributevalue = attributes.getProperty("document"))) { // If we have the attribute document="meta-id"
	    try {
		if (includeLevel>0) {
		    int included_meta_id = Integer.parseInt(attributevalue) ;
		    // Recursively parse the wanted page.
		    String document = textDocParser.parsePage(included_meta_id,user,-1,includeLevel-1,paramsToParse) ;
		    document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_PREBODY_PATTERN,NULL_SUBSTITUTION,document) ;
		    document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_POSTBODY_PATTERN,NULL_SUBSTITUTION,document) ;
		    return document ;
		}
	    }
	    catch (NumberFormatException ex) {
		return "<!-- imcms:include failed: "+ex+" -->" ;
	    } catch (IOException ex) {
		return "<!-- imcms:include failed: "+ex+" -->" ;
	    } catch (RuntimeException ex) {
		return "<!-- imcms:include failed: "+ex+" -->" ;
	    }
	    return "" ;
	} else if (null != (attributevalue = attributes.getProperty("url"))) { // If we have an attribute of the form url="url:url"
	    try {
		URL url = new URL(attributevalue) ;
		if (url.getProtocol().equalsIgnoreCase("file")) { // Make sure we don't have to defend against file://urls...
		    return "" ;
		}
		InputStreamReader urlInput = new InputStreamReader(url.openConnection().getInputStream()) ;
		int charsRead = -1 ;
		final int URL_BUFFER_LEN = 16384 ;
		char[] buffer = new char[URL_BUFFER_LEN] ;
		StringBuffer urlResult = new StringBuffer() ;
		while (-1 != (charsRead = urlInput.read(buffer,0,URL_BUFFER_LEN))) {
		    urlResult.append(buffer,0,charsRead) ;
		}
		return urlResult.toString() ;
	    } catch (MalformedURLException ex) {
		return "<!-- imcms:include failed: "+ex+" -->" ;
	    } catch (IOException ex) {
		return "<!-- imcms:include failed: "+ex+" -->" ;
	    } catch (RuntimeException ex) {
		return "<!-- imcms:include failed: "+ex+" -->" ;
	    }
	} else { // If we have none of the attributes no, file, or document
	    no = implicitIncludeNumber++ ; // Implicitly use the next number.
	}
	try {
	    if (includeMode) {
		String included_meta_id_str = (String)included_docs.get(String.valueOf(no)) ;
		return imcode.util.Parser.parseDoc(fileCache.getCachedFileString(new File(templatePath, user.getLangPrefix()+"/admin/change_include.html")),
						   new String[] {
						       "#meta_id#",         String.valueOf(meta_id),
						       "#servlet_url#",     servletUrl,
						       "#include_id#",      String.valueOf(no),
						       "#include_meta_id#", included_meta_id_str == null ? "" : included_meta_id_str
						   }
						   ) ;
	    } else if (includeLevel>0) {
		int included_meta_id = Integer.parseInt((String)included_docs.get(String.valueOf(no))) ;
		String document = textDocParser.parsePage(included_meta_id,user,-1,includeLevel-1,paramsToParse) ;         ;
		document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_PREBODY_PATTERN,NULL_SUBSTITUTION,document) ;
		document = org.apache.oro.text.regex.Util.substitute(patMat,HTML_POSTBODY_PATTERN,NULL_SUBSTITUTION,document) ;
		return document ;
	    }
	} catch (IOException ex) {
	    return "<!-- imcms:include failed: "+ex+" -->" ;
	} catch (NumberFormatException ex) {
	    // There was no such include in the db.
	    return "" ;
	}

	return "" ;
    }

    /**
       Handle a <?imcms:text ...?> tag

       @param attributes The attributes of the text tag
       @param patMat     A pattern matcher.
    **/
    public String tagText (Properties attributes, PatternMatcher patMat) {
	String mode =  attributes.getProperty("mode") ;
	if ( ( mode != null && !"".equals(mode) )
	     && ( ( textMode && "read".startsWith(mode) ) // With mode="read", we don't want anything in textMode.
		  || ( !textMode && "write".startsWith(mode) ) // With mode="write", we don't want anything unless we're in textMode.
		  ) ) {
	    return "" ;
	}
	// Get the 'no'-attribute of the <?imcms:text no="..."?>-tag
	String noStr = attributes.getProperty("no") ;
	IMCText text = null ;
	if (null != noStr) {
	    text = (IMCText)textMap.get(noStr) ;
	    implicitTextNumber = Integer.parseInt(noStr) + 1 ;
	} else {
	    text = (IMCText)textMap.get(noStr = String.valueOf(implicitTextNumber++)) ;
	}
	String result ;
	if (text == null) {
	    result = "" ;
	} else {
	    // Since this is supposed to be a html-view of the db, we'll do some html-escaping.
	    result = htmlize(text) ;
	}

	String filter = attributes.getProperty("filter") ;
	if (null != filter && "readrunner".equalsIgnoreCase(filter)) {

	    result = filterReadrunner(result,patMat) ;
	}

	String finalresult = result ;
	if (textMode) {
	    // FIXME: Remove this html-crap.
	    finalresult = "<img src=\""+imageUrl+"red.gif\" border=\"0\">&nbsp;"+finalresult+"<a href=\"ChangeText?meta_id="+meta_id+"&txt="+noStr+"\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>" ;
	} else if (!"".equals(result)) { // Else, we're not in textmode, and do we have something other than the empty string?
	    String tempAtt = null ;
	    if ((tempAtt = attributes.getProperty("pre")) != null) {
		finalresult = tempAtt + finalresult ;
	    }
	    if ((tempAtt = attributes.getProperty("post")) != null) {
		finalresult = finalresult + tempAtt ;
	    }
	}
	return finalresult ;
    }

    /**
       Handle a <?imcms:image ...?> tag

       @param attributes The attributes of the image tag
       @param patMat     A pattern matcher.
    **/
    public String tagImage (Properties attributes, PatternMatcher patMat) {
	String mode =  attributes.getProperty("mode") ;
	if ( ( mode != null && !"".equals(mode) )
	     && ( ( imageMode && "read".startsWith(mode) ) // With mode="read", we don't want anything in imageMode.
		  || ( !imageMode && "write".startsWith(mode) ) // With mode="write", we don't want anything it not in imageMode.
		  ) ) {
	    return "" ;
	}
	// Get the 'no'-attribute of the <?imcms:text no="..."?>-tag
	String noStr = attributes.getProperty("no") ;
	String result = null ;
	if (null != noStr) {
	    result = (String)imageMap.get(noStr) ;
	    implicitImageNumber = Integer.parseInt(noStr) + 1 ;
	} else {
	    result = (String)imageMap.get(noStr = String.valueOf(implicitImageNumber++)) ;
	}
	if (result == null) {
	    result = "" ;
	}
	String finalresult = result ;
	if (imageMode && "".equals(result)) { // If imageMode, and no data in the db-field.
	    // FIXME: Remove this html-crap.
	    finalresult = "<a href=\"ChangeImage?meta_id="+meta_id+"&img="+noStr+"\"><img src=\""+imageUrl+"bild.gif\" border=\"0\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>" ;
	} else if (imageMode) {               // If imageMode, with data in the db-field.
	    // FIXME: Remove this html-crap.
	    finalresult += "<a href=\"ChangeImage?meta_id="+meta_id+"&img="+noStr+"\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>" ;
	} else if(!"".equals(result)) {                              // Else, if we have something other than the empty string...
	    String tempAtt = null ;
	    if ((tempAtt = attributes.getProperty("pre")) != null) {
		finalresult = tempAtt + finalresult ;   // Prepend the contents of the 'pre'-attribute.
	    }
	    if ((tempAtt = attributes.getProperty("post")) != null) {
		finalresult = finalresult + tempAtt ;   // Append the contents of the 'post'-attribute.
	    }
	}
	return finalresult ;
    }

    /**
       Handle a <?imcms:datetime ...?> tag

       @param attributes The attributes of the datetime tag.
       format attribute defines a user pattern to use when geting the date.
       type attribute defines what date to get they can bee
       now, created, modified, activated, archived

    **/
    public String tagDatetime (Properties attributes) {
	String format =  attributes.getProperty("format") == null ? DATETIME_FORMAT_STD : attributes.getProperty("format") ;
	String type	  =  attributes.getProperty("type")	;
	String lang	  =  attributes.getProperty("lang")	;

	Date date = null;

	if (type != null) {
	    type = type.toLowerCase();
	    if ("now".startsWith(type)) {
		date = new Date();
	    } else if("created".startsWith(type)) {
		date = document.getCreatedDatetime();
	    } else if("modified".startsWith(type)) {
		date = document.getModifiedDatetime();
	    } else if("archived".startsWith(type)) {
		date = document.getActivatedDatetime();
	    } else if("activated".startsWith(type)) {
		date = document.getArchivedDatetime();
	    } else {
		return "<!-- <?imcms:datetime ... type=\""+type+"\" is empty, wrong or does not exist! -->";
	    }
	} else {
	    date = new Date();
	}

	java.text.SimpleDateFormat formatter;
	if (lang == null){
	    formatter = new java.text.SimpleDateFormat(format);
	} else {
	    formatter = new java.text.SimpleDateFormat(format, new Locale(lang,""));
	}

	try {
	    return formatter.format(date);
	} catch (IllegalArgumentException ex) {
	    return "<!-- imcms:datetime failed: "+ex.getMessage()+" -->";
	}
    }

    public void appendSubstitution( StringBuffer sb, MatchResult matres, int sc, PatternMatcherInput originalInput, PatternMatcher patMat, Pattern pat) {
	String tagname = matres.group(1) ;
	String tagattributes = matres.group(2) ;
	Properties attributes = new Properties() ;
	PatternMatcherInput pminput = new PatternMatcherInput(tagattributes) ;
	while(patMat.contains(pminput,IMCMS_TAG_ATTRIBUTES_PATTERN)) {
	    MatchResult attribute_matres = patMat.getMatch() ;
	    attributes.setProperty(attribute_matres.group(1), attribute_matres.group(3)) ;
	}
	String result ;

	// FIXME: This is quickly growing ugly. A better solution would be a class per tag (TagHandler's if you will), with a known interface, looked up through some HashMap. JSP already fixes this with tag-libs.
	if ("text".equals(tagname)) {
	    result = tagText(attributes, patMat) ;
	} else if ("image".equals(tagname)) {
	    result = tagImage(attributes, patMat) ;
	} else if ("include".equals(tagname)) {
	    result = tagInclude(attributes, patMat) ;
	} else if ("metaid".equals(tagname)) {
	    result = tagMetaId() ;
	} else if ("datetime".equals(tagname)) {
	    result = tagDatetime(attributes) ;
	} else {
	    result = matres.group(0) ;
	}
	sb.append(result) ;
    }

    /**
       Perform escaping necessary to htmlize an IMCText.
    **/
    private String htmlize (IMCText text) {
	String result = text.getText() ;
	if ( text.getType() == IMCText.TEXT_TYPE_PLAIN ) {
	    String[] vp = new String[] {
		"&",   "&amp;",
		"<",   "&lt;",
		">",   "&gt;",
		"\"",  "&quot;",
		"\r\n","\n",
		"\r",  "\n",
		"\n",  "<BR>\n",
	    } ;
	    result = Parser.parseDoc(result,vp) ;
	}
	return result ;
    }

    /**
       Do the filtering necessary for Readrunner.
    **/
    private String filterReadrunner ( String text, PatternMatcher patMat) {
	org.apache.oro.text.perl.Perl5Util perl5util = new org.apache.oro.text.perl.Perl5Util() ;
	List firsttaglist = new ArrayList() ;

	// Split on <html-tags>
	perl5util.split(firsttaglist,"m!(<[^>]+?>)!",text) ;

	// Create a set of our linebreak-tags.
	Set linebreakTags = new HashSet() ;
	linebreakTags.add("a") ;
	linebreakTags.add("p") ;
	linebreakTags.add("h1") ;
	linebreakTags.add("h2") ;
	linebreakTags.add("h3") ;
	linebreakTags.add("h4") ;
	linebreakTags.add("h5") ;
	linebreakTags.add("h6") ;
	linebreakTags.add("br") ;
	linebreakTags.add("ul") ;
	linebreakTags.add("ol") ;
	linebreakTags.add("li") ;
	linebreakTags.add("div") ;

	List secondtaglist = new ArrayList() ;

	StringBuffer nextListItem = new StringBuffer() ;

	// Loop through all parts, html-tags and not.
	for (Iterator i = firsttaglist.iterator(); i.hasNext() ; ) {

	    String part = (String)i.next() ;

	    if (perl5util.match("m!</?(\\w+)[^>]*?>!", part)) {
		String tagName = perl5util.group(1).toLowerCase() ;

		if (linebreakTags.contains(tagName)) {
		    secondtaglist.add(nextListItem.toString()) ;
		    nextListItem.setLength(0) ;
		    secondtaglist.add(part) ;
		    continue ;
		}
	    }
	    nextListItem.append(part) ;
	}

	secondtaglist.add(nextListItem.toString()) ;

	// Set up a buffer for storing the result
	StringBuffer resultBuffer = new StringBuffer() ;

	// Loop through all parts, html-tags and not.
	for (Iterator i = secondtaglist.iterator(); i.hasNext() ; ) {

	    String part = (String)i.next() ;

	    // For each part, check if it looks like a html-tag.
	    if (!perl5util.match("/^<[^>]+?>$/",part)) {
		// Not a html-tag

		// Change all "&abc;" and "&#123;" to "_&abc;_" and "_&#123;_"
		part = org.apache.oro.text.regex.Util.substitute(patMat,
								 HTML_ESCAPE_HIDE_PATTERN,
								 HTML_ESCAPE_HIDE_SUBSTITUTION,
								 part,
								 org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;


		// Choose a nice readrunner-pattern.
		Pattern readrunnerFilterPattern = null ;
		if (parserParameters.getReadrunnerUseStopChars() && parserParameters.getReadrunnerUseSepChars()) {
		    readrunnerFilterPattern = READRUNNER_FILTER_STOPSEP_PATTERN ;
		} else if (parserParameters.getReadrunnerUseStopChars()) {
		    readrunnerFilterPattern = READRUNNER_FILTER_STOP_PATTERN ;
		} else if (parserParameters.getReadrunnerUseSepChars()) {
		    readrunnerFilterPattern = READRUNNER_FILTER_SEP_PATTERN ;
		} else {
		    readrunnerFilterPattern = READRUNNER_FILTER_PATTERN ;
		}

		// <q>Quote</q> all sentences.
		part = org.apache.oro.text.regex.Util.substitute(patMat,
								 readrunnerFilterPattern,
								 readrunnerQuoteSubstitution,
								 part,
								 org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

		// Change all "_&abc;_" and "_&#123;_" to "&abc;" and "&#123;"
		part = org.apache.oro.text.regex.Util.substitute(patMat,
								 HTML_ESCAPE_UNHIDE_PATTERN,
								 HTML_ESCAPE_UNHIDE_SUBSTITUTION,
								 part,
								 org.apache.oro.text.regex.Util.SUBSTITUTE_ALL) ;

	    }
	    resultBuffer.append(part) ;
	}

	if (resultBuffer.length()>0) {
	    resultBuffer.insert(0,"<div id='RR1'>") ;
	    resultBuffer.append("</div>") ;
	}

	return resultBuffer.toString() ;
    }

    public int getReadrunnerQuoteSubstitutionCount() {
	return readrunnerQuoteSubstitution.getReadrunnerQuoteSubstitutionCount() ;
    }
}
