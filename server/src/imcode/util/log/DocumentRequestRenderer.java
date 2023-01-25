package imcode.util.log;

import com.imcode.imcms.mapping.SectionFromIdTransformer;
import imcode.server.DocumentRequest;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;


public final class DocumentRequestRenderer {

	private final static String REDIRECT_PREFIX = "/RD";

	/**
	 * Render a DocumentRequest as a String.
	 */
	public static String render(final DocumentRequest documentRequest) {
		final StringBuilder message = new StringBuilder();
		final DocumentDomainObject document = documentRequest.getDocument();
		final DocumentDomainObject referrer = documentRequest.getReferrer();

		message.append(documentRequest.getHttpServletRequest().getRemoteAddr());
		message.append(' ').append(documentRequest.getUser().getId());

		message.append(" sessionID=").append(documentRequest.getHttpServletRequest().getSession().getId());

		message.append(' ').append(documentRequest.getHttpServletRequest().getContextPath()).append(REDIRECT_PREFIX).append(renderDocument(document));
		if (null != referrer) {
			message.append(' ').append(documentRequest.getHttpServletRequest().getContextPath()).append(REDIRECT_PREFIX).append(renderDocument(referrer));
		}

		return message.toString();
	}

	private static String renderDocument(DocumentDomainObject document) {

        Set sectionIds = document.getSectionIds();
        int metaId = document.getId();
        int docType = document.getDocumentTypeId();
        String headline = document.getHeadline();

        StringBuffer result = new StringBuffer();
        result.append('/');
        if (!sectionIds.isEmpty()) {
            Iterator sectionsIterator = new TransformIterator(sectionIds.iterator(), new SectionFromIdTransformer(Imcms.getServices()));
            result.append(lossyUrlEncode(StringUtils.join(sectionsIterator, ",")));
        } else {
            result.append('_');
        }
        result.append('/');
        result.append(metaId);
        result.append('/');
        result.append(docType);
        result.append('/');
        result.append(lossyUrlEncode(headline));
        result.append('/');
        if (document instanceof TextDocumentDomainObject) {
            String templateName = ((TextDocumentDomainObject) document).getTemplateName();
            result.append(lossyUrlEncode(templateName));
        }
        result.append('/');

        return result.toString();
    }

    private static String lossyUrlEncode(String url) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < url.length(); ++i) {
            char c = url.charAt(i);
            if (' ' == c) {
                // Spaces convert to '_'
                result.append('_');
            } else if (',' == c || '.' == c || '-' == c || '_' == c) {
                // We explicitly allow some punctuation that are known safe url-characters.
                // Everything else is likely to break somewhere, somehow.
                result.append(c);
            } else if (c < 32 || c >= 128 && c < 160) {
                // We strip control chars (including newlines and tabs),
                // and unassigned characters in iso-8859-1.
            } else if (c < 256 && Character.isLetterOrDigit(c)) {
                // It's a letter or digit in iso-8859-1.
                result.append(c);
            } else {
                try {
                    // Otherwise, url-encode it.
                    // Note that UTF-8 is the W3C-recommended standard.
                    result.append(URLEncoder.encode("" + c, "UTF-8"));
                } catch (UnsupportedEncodingException uee) {
                    // All JVMs are required to support UTF-8.
                }
            }
        }
        return result.toString();
    }
}
