package imcode.util.log ;

import javax.servlet.http.* ;

import org.apache.log4j.* ;
import org.apache.log4j.or.* ;

import imcode.server.* ;
import imcode.server.parser.* ;

public class FakeRequestRenderer implements ObjectRenderer {

    public FakeRequestRenderer() { }

    /**
       Render a DocumentRequest as a String.
    **/
    public String doRender(Object o) {

	DocumentRequest docReq = (DocumentRequest) o ;

	Document document = docReq.getDocument() ;
	Document referrer = docReq.getReferrer() ;

	StringBuffer result = new StringBuffer(docReq.getRemoteAddr()) ;
	result.append(' ').append(docReq.getUser().getUserId()) ;

	result.append(' ').append(docReq.getSessionId()) ;

	result.append(' ').append(renderDocument(document)) ;
	if (null != referrer) {
	    result.append(' ').append(renderDocument(referrer)) ;
	}
	return result.toString() ;
    }

    private String renderDocument(Document document) {

	String section  = document.getSection() ;
	int    metaId   = document.getMetaId() ;
	int    docType  = document.getDocumentType() ;
	String headline = document.getHeadline() ;
	Template template = document.getTemplate() ;

	StringBuffer result = new StringBuffer() ;
	result.append('/') ;
	if (null != section) {
	    result.append(java.net.URLEncoder.encode(section)) ;
	}
	result.append('/') ;
	result.append(metaId) ;
	result.append('/') ;
	result.append(docType) ;
	result.append('/') ;
	result.append(java.net.URLEncoder.encode(headline)) ;
	result.append('/') ;
	if (null != template) {
	    result.append(java.net.URLEncoder.encode(template.getName())) ;
	}
	result.append('/') ;

	return result.toString() ;
    }

}
