package imcode.server.parser ;

import org.apache.oro.text.regex.* ;
import java.util.* ;


public class ParserParameters {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private String _template; 	//used to store the template if not default is wanted
	private String _param;		//used to store the parameter param
	private String _externalParam; //used to store the param prodused from external class.
	
	public ParserParameters(){
	}
	
	public ParserParameters(String template){
		_template = template;
	}
	
	public ParserParameters(String template, String param){
		_template = template;
		_param = param;
	}
	
	public ParserParameters(String template, String param, String externalParam){
		_template = template;
		_param = param;
		_externalParam = externalParam;
	}
	
	// set methods
	public void setTemplate(String template)
	{
		_template = template;
	}
	
	public void setParameter(String param)
	{
		_param = param;
	}
	
	public void setExternalParameter(String externalparam)
	{
		_externalParam = externalparam;
	}
	// get methods
	public String getTemplate()
	{
		return	_template;
	}
	
	public String getParameter()
	{
		return	_param == null ? "":_param;
	}
	
	public String getExternalParameter()
	{
		return	_externalParam == null ? "":_externalParam;
	}
}
