package imcode.util.log ;

import javax.servlet.http.* ;

import java.net.URLEncoder ;

import org.apache.log4j.* ;
import org.apache.log4j.or.* ;

import imcode.server.* ;
import imcode.server.document.DocumentDomainObject;
import imcode.server.parser.* ;

public class FakeRequestRenderer implements ObjectRenderer {

    private final static String REDIRECT_PREFIX = "/RD" ;

    /**
       Render a DocumentRequest as a String.
    **/
    public String doRender(Object o) {

	DocumentRequest docReq = (DocumentRequest) o ;

	DocumentDomainObject document = docReq.getDocument() ;
	DocumentDomainObject referrer = docReq.getReferrer() ;
	Revisits revisits ;

	StringBuffer result = new StringBuffer(docReq.getRemoteAddr()) ;
	result.append(' ').append(docReq.getUser().getUserId()) ;

	result.append(" sessionID=").append(docReq.getSessionId()) ;
	revisits = docReq.getRevisits() ;

	result.append(";imVisits=").append(revisits.getRevisitsId()) ;
	if (null != revisits.getRevisitsDate()) {
		result.append(revisits.getRevisitsDate()) ;
	}
	result.append(' ').append(docReq.getContextPath()).append(REDIRECT_PREFIX).append(renderDocument(document)) ;
	if (null != referrer) {
	    result.append(' ').append(docReq.getContextPath()).append(REDIRECT_PREFIX).append(renderDocument(referrer)) ;
	}

	return result.toString() ;
    }

    private String renderDocument(DocumentDomainObject document) {

	String section  = document.getSection() ;
	int    metaId   = document.getMetaId() ;
	int    docType  = document.getDocumentType() ;
	String headline = document.getHeadline() ;
	Template template = document.getTemplate() ;

	StringBuffer result = new StringBuffer() ;
	result.append('/') ;
	if (null != section && !"".equals(section)) {
	    result.append(lossyUrlEncode(section)) ;
	} else {
	    result.append('_') ;
	}
	result.append('/') ;
	result.append(metaId) ;
	result.append('/') ;
	result.append(docType) ;
	result.append('/') ;
	result.append(lossyUrlEncode(headline)) ;
	result.append('/') ;
	if (null != template) {
	    result.append(lossyUrlEncode(template.getName())) ;
	}
	result.append('/') ;

	return result.toString() ;
    }

    private final String lossyUrlEncode(String url) {
	StringBuffer result = new StringBuffer() ;
	for (int i = 0; i < url.length(); ++i) {
	    char c = url.charAt(i) ;
	    if (' ' == c) {
		// Spaces convert to '_'
		result.append('_') ;
	    } else if (',' == c || '.' == c || '-' == c || '_' == c) {
		// We explicitly allow some punctuation that are known safe url-characters.
		// Everything else is likely to break somewhere, somehow.
		// If you think you want to change this, know that you are fucking it up.
		result.append(c) ;
	    } else if (c < 32 || (c >= 128 && c < 160)) {
		// We strip control chars (including newlines and tabs),
		// and characters not found in iso-8859-1.
	    } else if (c < 256 && Character.isLetterOrDigit(c)) {
		// It's a letter or digit in iso-8859-1.
		result.append(c) ;
	    } else {
		try {
		    // Otherwise, url-encode it.
		    // Note that UTF-8 is the W3C-recommended standard.
		    // If you think you want to change this, know that you are fucking it up.
		    result.append(URLEncoder.encode(""+c, "UTF-8")) ;
		} catch (java.io.UnsupportedEncodingException uee) {
		    // All JVMs are required to support UTF-8.
		}
	    }
	}
	return result.toString() ;
    }
}
