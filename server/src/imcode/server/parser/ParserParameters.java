package imcode.server.parser ;

import imcode.readrunner.ReadrunnerParameters;

public class ParserParameters {

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private String externalParam; //used to store the param prodused from external class.

    private ReadrunnerParameters readrunnerParameters ;

    public ParserParameters() {

    }

    // set methods
    public void setTemplate(String template) {
	this.template = template;
    }

    public void setParameter(String param) {
	this.param = param;
    }

    public void setExternalParameter(String externalparam) {
	this.externalParam = externalparam;
    }

    public void setReadrunnerParameters(ReadrunnerParameters readrunnerParameters) {
	this.readrunnerParameters = readrunnerParameters ;
    }

    // get methods
    public String getTemplate() {
	return	this.template;
    }

    public String getParameter() {
	return	this.param == null ? "":this.param;
    }

    public String getExternalParameter() {
	return	this.externalParam == null ? "":this.externalParam;
    }

    /**
       @return The readrunner-parameters set for this parser, or null if none were set.
    **/
    public ReadrunnerParameters getReadrunnerParameters() {
	return this.readrunnerParameters ;
    }

}
