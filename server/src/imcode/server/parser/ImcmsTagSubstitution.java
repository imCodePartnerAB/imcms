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

    private static Category log = Category.getInstance("server");

    FileCache fileCache = new FileCache() ;

    static {
	Perl5Compiler patComp = new Perl5Compiler() ;

	try {

	    IMCMS_TAG_ATTRIBUTES_PATTERN = patComp.compile("\\s*(\\w+)\\s*=\\s*([\"'])(.*?)\\2", Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_PREBODY_PATTERN = patComp.compile("^.*?<[Bb][Oo][Dd][Yy].*?>", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;
	    HTML_POSTBODY_PATTERN = patComp.compile("<\\/[Bb][Oo][Dd][Yy]>.*$", Perl5Compiler.SINGLELINE_MASK|Perl5Compiler.READ_ONLY_MASK) ;

	} catch (MalformedPatternException ignored) {
	    // I ignore the exception because i know that these patterns work, and that the exception will never be thrown.
	    log.fatal("Danger, Will Robinson!",ignored) ;
	}

    }

    TextDocumentParser textDocParser ;
    User user ;
    int meta_id ;

    File templatePath ;
    String servletUrl ;

    boolean includeMode ;
    int includeLevel ;
    File includePath ;
    int implicitIncludeNumber = 1 ;

    Map textMap ;
    boolean textMode ;
    int implicitTextNumber = 1 ;

    Map imageMap ;
    boolean imageMode ;
    String imageUrl ;
    int implicitImageNumber = 1 ;

    Document document;

    private final Substitution NULL_SUBSTITUTION = new StringSubstitution("") ;

    HashMap included_docs = new HashMap() ;

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
				 Map imagemap, boolean imagemode, String imageurl,Document theDoc) {
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
	ParserParameters paramsToParse = new ParserParameters(attributes.getProperty("template"),attributes.getProperty("param"));

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

	String finalresult = result ;
	if (textMode) {
	    finalresult = "<img src=\""+imageUrl+"red.gif\" border=\"0\">&nbsp;"+finalresult+"<a href=\"ChangeText?meta_id="+meta_id+"&txt="+noStr+"\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>" ;
	} else if (!"".equals(result)) { // Else, if we're not in adminmode, and we have something other than the empty string...
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
	    finalresult = "<a href=\"ChangeImage?meta_id="+meta_id+"&img="+noStr+"\"><img src=\""+imageUrl+"bild.gif\" border=\"0\"><img src=\""+imageUrl+"txt.gif\" border=\"0\"></a>" ;
	} else if (imageMode) {               // If imageMode, with data in the db-field.
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
			}else if("created".startsWith(type)) {
				date = document.getCreatedDatetime();
			}else if("modified".startsWith(type)) {
				date = document.getModifiedDatetime();
			}else if("arcived".startsWith(type)) {
				date = document.getActivatedDatetime();
			}else if("activated".startsWith(type)) {
				date = document.getArchivedDatetime();
			}
			if (date==null) {
				return "<!-- <?imcms:datetime ... type=\""+type+"\" is empty, wrong or doesnt exists! -->";
			}
		}else {
			date = new Date();
		}

		java.text.SimpleDateFormat formatter;
		if (lang == null){

			formatter = new java.text.SimpleDateFormat(format);
		}else{
			formatter = new java.text.SimpleDateFormat(format, new Locale(lang,""));
		}

		try{
			return formatter.format(date);
		}catch(IllegalArgumentException ex){
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
		"&", "&amp;",
		"<", "&lt;",
		">", "&gt;",
		"\"","&quot;",
		"\r\n","\n",
		"\r", "\n",
		"\n", "<BR>\n",
	    } ;
	    result = Parser.parseDoc(result,vp) ;
	}
	return result ;
    }
}
