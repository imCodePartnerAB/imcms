package imcode.server.parser ;

import org.apache.oro.text.regex.* ;
import java.util.* ;

import imcode.util.log.* ;

public class ParserParameters {
	private String _template; 	//used to store the template if not default is wanted
	private String _param;		//used to store the parameter param
	
	public ParserParameters(){
	}
	
	public ParserParameters(String template){
		_template = template;
	}
	
	public ParserParameters(String template, String param){
		_template = template;
		_param = param;
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
	
	// get methods
	public String getTemplate()
	{
		return	_template;
	}
	
	public String getParameter()
	{
		return	_param == null ? "":_param;
	}
}