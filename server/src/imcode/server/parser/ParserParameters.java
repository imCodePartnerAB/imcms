package imcode.server.parser ;

import org.apache.oro.text.regex.* ;
import java.util.* ;


public class ParserParameters {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private String template;	//used to store the template if not default is wanted
    private String param;		//used to store the parameter param
    private String externalParam; //used to store the param prodused from external class.
    private boolean readrunnerUseStopChars = true ;
    private boolean readrunnerUseSepChars = true ;

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

    public void setReadrunnerUseStopChars(boolean v) {
	this.readrunnerUseStopChars = v ;
    }

    public void setReadrunnerUseSepChars(boolean v) {
	this.readrunnerUseSepChars = v ;
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

    public boolean getReadrunnerUseStopChars() {
	return readrunnerUseStopChars ;
    }

    public boolean getReadrunnerUseSepChars() {
	return readrunnerUseSepChars ;
    }
}
