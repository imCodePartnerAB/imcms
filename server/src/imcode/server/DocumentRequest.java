package imcode.server;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**

 This class exists to store data about requests for documents in.
 **/
public class DocumentRequest implements Cloneable {

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    private ImcmsServices serverObject;
    private UserDomainObject user;
    private DocumentDomainObject document;
    private DocumentDomainObject referrer;
    private String[] emphasize ;

    private Revisits revisits;

    public DocumentRequest( ImcmsServices serverObject, UserDomainObject user, DocumentDomainObject document,
                            DocumentDomainObject referrer, HttpServletRequest httpServletRequest,
                            HttpServletResponse httpServletResponse ) {
        this.serverObject = serverObject;
        this.user = user;
        this.document = document ;
        this.referrer = referrer;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone() ;
    }

    public ImcmsServices getServices() {
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

    public String[] getEmphasize() {
        return null != emphasize ? (String[])emphasize.clone() : null ;
    }

    public void setEmphasize( String[] emphasize ) {
        this.emphasize = null != emphasize ? (String[])emphasize.clone() : null ;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }
}
