package imcode.server ;

import imcode.server.parser.* ;

/**
   This class exists to store data about requests for documents in.
**/
public class DocumentRequest {

    private String   remoteAddr ;
    private String   sessionId ;

    private User     user ;
    private Document document ;
    private Document referrer ;

    private IMCServiceInterface serverObject ;
	private  Revisits            revisits ;

    public DocumentRequest(IMCServiceInterface serverObject, String remoteAddr, String sessionId, User user, int metaId, Document referrer) {
	this.serverObject = serverObject ;
	this.remoteAddr   = remoteAddr ;
	this.sessionId    = sessionId ;
	this.user         = user ;
	this.document     = serverObject.getDocument(metaId) ;
	this.referrer     = referrer ;
    }

    public IMCServiceInterface getServerObject() {
	return this.serverObject ;
    }
    
	public void setRevisits(Revisits revisits) {
	this.revisits= revisits;
    }
	
	public Revisits getRevisits() {
	return this.revisits ;
    }

    public String getRemoteAddr() {
	return this.remoteAddr ;
    }

    public String getSessionId() {
	return this.sessionId ;
    }

    public User getUser() {
	return this.user ;
    }

    public Document getDocument() {
	return document ;
    }

    public Document getReferrer() {
	return referrer ;
    }
}
