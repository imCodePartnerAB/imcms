package imcode.server;

import javax.servlet.http.HttpServletRequest;

import imcode.server.user.UserDomainObject;
import imcode.server.document.DocumentDomainObject;

/**

 This class exists to store data about requests for documents in.
 **/
public class DocumentRequest implements Cloneable {

    private HttpServletRequest httpServletRequest;

    private IMCServiceInterface serverObject;
    private UserDomainObject user;
    private DocumentDomainObject document;
    private DocumentDomainObject referrer;

    private Revisits revisits;

    public DocumentRequest( IMCServiceInterface serverObject, UserDomainObject user, DocumentDomainObject document,
                            DocumentDomainObject referrer, HttpServletRequest httpServletRequest ) {
        this.serverObject = serverObject;
        this.user = user;
        this.httpServletRequest = httpServletRequest;
        this.document = document ;
        this.referrer = referrer;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone() ;
    }

    public IMCServiceInterface getServerObject() {
        return this.serverObject;
    }

    public void setRevisits( Revisits revisits ) {
        this.revisits = revisits;
    }

    public Revisits getRevisits() {
        return this.revisits;
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

    public void setDocument( DocumentDomainObject document ) {
        this.document = document;
    }

    public void setReferrer( DocumentDomainObject referrer ) {
        this.referrer = referrer;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

}
