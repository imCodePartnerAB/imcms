package imcode.server.parser;

public class ParserParameters {

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private String externalParam; //used to store the param prodused from external class.

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

}
