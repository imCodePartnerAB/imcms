package imcode.server.parser;

import imcode.server.DocumentRequest;

public class ParserParameters implements Cloneable {

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private String externalParam; //used to store the param prodused from external class.
    private Integer editingMenuIndex;
    private DocumentRequest documentRequest;
    private int flags;

    public ParserParameters() {

    }

    // set methods
    public void setTemplate( String template ) {
        this.template = template;
    }

    public void setParameter( String param ) {
        this.param = param;
    }

    public void setExternalParameter( String externalparam ) {
        this.externalParam = externalparam;
    }

    // get methods
    public String getTemplate() {
        return this.template;
    }

    public String getParameter() {
        return this.param == null ? "" : this.param;
    }

    public String getExternalParameter() {
        return this.externalParam == null ? "" : this.externalParam;
    }

    public Integer getEditingMenuIndex() {
        return editingMenuIndex ;
    }

    public void setEditingMenuIndex( Integer editingMenuIndex ) {
        this.editingMenuIndex = editingMenuIndex;
    }

    public DocumentRequest getDocumentRequest() {
        return documentRequest;
    }

    public void setDocumentRequest( DocumentRequest documentRequest ) {
        this.documentRequest = documentRequest;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags( int flags ) {
        this.flags = flags;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone() ;
    }
}
