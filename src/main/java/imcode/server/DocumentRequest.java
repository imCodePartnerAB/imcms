package imcode.server;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.log.DocumentRequestRenderer;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class exists to store data about requests for documents in.
 */
public class DocumentRequest implements Message, StringBuilderFormattable, Cloneable {

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    private ImcmsServices serverObject;
    private UserDomainObject user;
    private DocumentDomainObject document;
    private DocumentDomainObject referrer;

    private Revisits revisits;

    /**
     * Simplest DocumentRequest constructor, when referrer document not needs.
     *
     * @param document            desired document
     * @param httpServletRequest  request
     * @param httpServletResponse response
     */
    public <T extends DocumentDomainObject> DocumentRequest(T document,
                                                            HttpServletRequest httpServletRequest,
                                                            HttpServletResponse httpServletResponse) {
        this.serverObject = Imcms.getServices();
        this.user = Imcms.getUser();
        this.document = document;
        this.referrer = null;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public <T extends DocumentDomainObject> DocumentRequest(ImcmsServices serverObject,
                                                            UserDomainObject user,
                                                            T document,
                                                            T referrer,
                                                            HttpServletRequest httpServletRequest,
                                                            HttpServletResponse httpServletResponse) {
        this.serverObject = serverObject;
        this.user = user;
        this.document = document;
        this.referrer = referrer;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ImcmsServices getServices() {
        return this.serverObject;
    }

    public Revisits getRevisits() {
        return this.revisits;
    }

    public void setRevisits(Revisits revisits) {
        this.revisits = revisits;
    }

    public UserDomainObject getUser() {
        return this.user;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T getDocument() {
        return (T) document;
    }

    public <T extends DocumentDomainObject> void setDocument(T document) {
        this.document = document;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T getReferrer() {
        return (T) referrer;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

	@Override
	public String getFormattedMessage() {
		return DocumentRequestRenderer.render(this);
	}

	@Override
	public String getFormat() {
		return getFormattedMessage();
	}

	@Override
	public Object[] getParameters() {
		return new Object[]{this};
	}

	@Override
	public Throwable getThrowable() {
		return null;
	}

	@Override
	public void formatTo(final StringBuilder buffer) {
		buffer.append(getFormattedMessage());
	}
}
