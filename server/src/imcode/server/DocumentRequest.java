package imcode.server ;

import imcode.server.parser.* ;

/**
   This class exists to store data about requests for documents in.
**/
public class DocumentRequest {

    private String   remoteAddr ;
    private User     user ;
    private Document document ;
    private Document referrer ;

    private IMCServiceInterface serverObject ;

    public DocumentRequest(IMCServiceInterface serverObject, String remoteAddr, User user, int metaId, Document referrer) {
	this.serverObject = serverObject ;
	this.remoteAddr   = remoteAddr ;

	this.user = user ;
	this.document = serverObject.getDocument(metaId) ;
	this.referrer = referrer ;
    }

    public IMCServiceInterface getServerObject() {
	return this.serverObject ;
    }

    public String getRemoteAddr() {
	return this.remoteAddr ;
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
