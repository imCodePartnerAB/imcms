package imcode.server ;

/**
   Storage-container for systemdata
 */
public class SystemData implements java.io.Serializable {
    private final static String CVS_REV="$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /** The startdocument **/
    protected int startDocument ;

    /** The servermaster */
    protected String serverMaster ;

    /** The servermaster's address */
    protected String serverMasterAddress ;

    /** The webmaster */
    protected String webMaster ;

    /** The webmaster's address */
    protected String webMasterAddress ;

    /** The systemmessage */
    protected String systemMessage ;


    /** Get the startdocument **/
    public int getStartDocument() {
	return startDocument ;
    }

    /** Get the servermaster */
    public String getServerMaster() {
	return serverMaster ;
    }

    /** Get the servermaster's address */
    public String getServerMasterAddress() {
	return serverMasterAddress ;
    }

    /** Get the webmaster */
    public String getWebMaster() {
	return webMaster ;
    }

    /** Get the webmaster's address */
    public String getWebMasterAddress() {
	return webMasterAddress ;
    }

    /** Get the systemmessage */
    public String getSystemMessage() {
	return systemMessage ;
    }


    public void setStartDocument(int startDocument) {
	this.startDocument = startDocument ;
    }

    public void setServerMaster(String serverMaster) {
	this.serverMaster = serverMaster ;
    }

    /** Get the servermaster's address */
    public void setServerMasterAddress(String serverMasterAddress) {
	this.serverMasterAddress = serverMasterAddress ;
    }

    /** Get the webmaster */
    public void setWebMaster(String webMaster) {
	this.webMaster = webMaster ;
    }

    /** Get the webmaster's address */
    public void setWebMasterAddress(String webMasterAddress) {
	this.webMasterAddress = webMasterAddress ;
    }

    /** Get the systemmessage */
    public void setSystemMessage(String systemMessage) {
	this.systemMessage = systemMessage ;
    }
}
