package imcode.server ;

import java.util.* ;


public class Template {
	private final static String CVS_REV="$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	int id ;
	String description ;
	String file_name ;
	Vector data = new Vector() ;


	/**
	Constructor.
	*/
	public Template() {
	}

	/**
	Constructor.
	*/
	public Template(int id,String description,String file_name,Vector data) {
		this.id          = id ;
		this.description = description ;
		this.file_name   = file_name ;
		this.data        = data ;
	}




	/**
	Get template id.
	*/
	public int getId() {
		return id ;
	}


	/**
	Get template description.
	*/
	public String getDescription() {
		return description ;
	}


	/**
	Get template filename.
	*/
	public String getFileName() {
		return file_name ;
	}


	/**
	Get template data.
	*/
	public Vector getData() {
		return data ;
	}

	/**
	Set template id.
	*/
	public void setId(int id) {
		this.id = id ;
	}

	/**
	Set template description.
	*/
	public void setDescription(String description) {
		this.description = description ;
	}


	/**
	Set template file_name.
	*/
	public void setFileName(String file_name) {
		this.file_name = file_name ;
	}


	/**
	Set template data.
	*/
	public void setData(Vector data) {
		this.data = (Vector)data.clone() ;
	}




}

