package imcode.server;

import javax.servlet.http.HttpServletRequest;

import imcode.server.parser.*;

/**
 This class exists to store data about requests for documents in.
 **/
public class DocumentRequest implements Cloneable {

    private HttpServletRequest httpServletRequest;

    private User user;
    private Document document;
    private Document referrer;

    private IMCServiceInterface serverObject;
    private Revisits revisits;

    public DocumentRequest( IMCServiceInterface serverObject, User user, int metaId, Document referrer, HttpServletRequest httpServletRequest ) {
        this.serverObject = serverObject;
        this.user = user;
        this.httpServletRequest = httpServletRequest;
        this.document = serverObject.getDocument( metaId );
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

    public User getUser() {
        return this.user;
    }

    public Document getDocument() {
        return document;
    }

    public Document getReferrer() {
        return referrer;
    }

    public void setDocument( Document document ) {
        this.document = document;
    }

    public void setReferrer( Document referrer ) {
        this.referrer = referrer;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

}
