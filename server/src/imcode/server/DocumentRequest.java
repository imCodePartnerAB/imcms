package imcode.server;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import imcode.server.parser.*;
import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentDomainObject;

/**
 * This class exists to store data about requests for documents in.
 */
public class DocumentRequest {

    private String remoteAddr;
    private String sessionId;
    private String userAgent;
    private String hostName;
    private String contextPath;
    private String queryString ;

    private UserDomainObject user;
    private DocumentDomainObject document;
    private DocumentDomainObject referrer;

    private IMCServiceInterface serverObject;
    private Revisits revisits;
    private Cookie[] cookies;

    public DocumentRequest(IMCServiceInterface serverObject, String remoteAddr, String sessionId, UserDomainObject user, int metaId, DocumentDomainObject referrer, String queryString) {
        this.serverObject = serverObject;
        this.remoteAddr = remoteAddr;
        this.sessionId = sessionId;
        this.user = user;
        this.queryString = queryString;
        this.document = serverObject.getDocument(metaId);
        this.referrer = referrer;
    }

    public IMCServiceInterface getServerObject() {
        return this.serverObject;
    }

    public void setRevisits(Revisits revisits) {
        this.revisits = revisits;
    }

    public Revisits getRevisits() {
        return this.revisits;
    }

    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public UserDomainObject getUser() {
        return this.user;
    }

    public DocumentDomainObject getDocument() {
        return document;
    }

    public DocumentDomainObject getReferrer() {
        return referrer;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    public Cookie[] getCookies() {
        return this.cookies;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getQueryString() {
        return queryString;
    }

}
