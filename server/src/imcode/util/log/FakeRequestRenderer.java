package imcode.util.log;

import imcode.server.DocumentRequest;
import imcode.server.Revisits;
import imcode.server.Imcms;
import com.imcode.imcms.mapping.SectionFromIdTransformer;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.log4j.or.ObjectRenderer;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.Iterator;

public class FakeRequestRenderer implements ObjectRenderer {

    private final static String REDIRECT_PREFIX = "/RD";

    /**
     * Render a DocumentRequest as a String.
     */
    public String doRender( Object o ) {

        DocumentRequest docReq = (DocumentRequest)o;

        DocumentDomainObject document = docReq.getDocument();
        DocumentDomainObject referrer = docReq.getReferrer();

        StringBuffer result = new StringBuffer( docReq.getHttpServletRequest().getRemoteAddr() );
        result.append( ' ' ).append( docReq.getUser().getId() );

        result.append( " sessionID=" ).append( docReq.getHttpServletRequest().getSession().getId() );
        Revisits revisits = docReq.getRevisits();

        result.append( ";imVisits=" ).append( revisits.getRevisitsId() );
        if ( null != revisits.getRevisitsDate() ) {
            result.append( revisits.getRevisitsDate() );
        }
        result.append( ' ' ).append( docReq.getHttpServletRequest().getContextPath() ).append( REDIRECT_PREFIX ).append( renderDocument( document ) );
        if ( null != referrer ) {
            result.append( ' ' ).append( docReq.getHttpServletRequest().getContextPath() ).append( REDIRECT_PREFIX ).append( renderDocument( referrer ) );
        }
        return result.toString();
    }

    private String renderDocument( DocumentDomainObject document ) {

        Set sectionIds = document.getSectionIds();
        int metaId = document.getId();
        int docType = document.getDocumentTypeId();
        String headline = document.getHeadline();

        StringBuffer result = new StringBuffer();
        result.append( '/' );
        if ( !sectionIds.isEmpty() ) {
            Iterator sectionsIterator = new TransformIterator(sectionIds.iterator(), new SectionFromIdTransformer(Imcms.getServices()));
            result.append( lossyUrlEncode( StringUtils.join( sectionsIterator, "," ) ) );
        } else {
            result.append( '_' );
        }
        result.append( '/' );
        result.append( metaId );
        result.append( '/' );
        result.append( docType );
        result.append( '/' );
        result.append( lossyUrlEncode( headline ) );
        result.append( '/' );
        if ( document instanceof TextDocumentDomainObject ) {
            String templateName = ( (TextDocumentDomainObject) document ).getTemplateName();
            result.append( lossyUrlEncode( templateName ) );
        }
        result.append( '/' );

        return result.toString();
    }

    private String lossyUrlEncode( String url ) {
        StringBuffer result = new StringBuffer();
        for ( int i = 0; i < url.length(); ++i ) {
            char c = url.charAt( i );
            if ( ' ' == c ) {
                // Spaces convert to '_'
                result.append( '_' );
            } else if ( ',' == c || '.' == c || '-' == c || '_' == c ) {
                // We explicitly allow some punctuation that are known safe url-characters.
                // Everything else is likely to break somewhere, somehow.
                result.append( c );
            } else if ( c < 32 || c >= 128 && c < 160 ) {
                // We strip control chars (including newlines and tabs),
                // and unassigned characters in iso-8859-1.
            } else if ( c < 256 && Character.isLetterOrDigit( c ) ) {
                // It's a letter or digit in iso-8859-1.
                result.append( c );
            } else {
                try {
                    // Otherwise, url-encode it.
                    // Note that UTF-8 is the W3C-recommended standard.
                    result.append( URLEncoder.encode( "" + c, "UTF-8" ) );
                } catch ( UnsupportedEncodingException uee ) {
                    // All JVMs are required to support UTF-8.
                }
            }
        }
        return result.toString();
    }
}
