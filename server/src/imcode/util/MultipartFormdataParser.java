package imcode.util ;

import java.util.* ;

/**
	Parses multipart/form-data into parts, easily gettable with it's methods.
*/
public class MultipartFormdataParser {

    private MultipartParser mp ;
	private String[] names ;
	/**
		Parses the multipartdata into parts.
		@param data The multipartdata.
		@param contenttype The MIME-header "Content-Type" for the data. Must contain a 'boundary=" "' parameter.
	*/
	public MultipartFormdataParser (String data, String contenttype) {
		mp = new MultipartParser (data, contenttype) ;
		names = new String[mp.countParts()] ;
		for ( int i = 0; i<mp.countParts() ; i++ ) {
			names[i] = mp.getHeaderParams(i,"content-disposition").getProperty("name") ;
		}
	}

	/**
		Parses the multipartdata into parts.
		@param data The multipartdata.
		@param contenttype The MIME-header "Content-Type" for the data. Must contain a 'boundary=" "' parameter.
	*/
	public MultipartFormdataParser (byte[] data, String contenttype) {
		mp = new MultipartParser (data, contenttype) ;
		names = new String[mp.countParts()] ;
		for ( int i = 0; i<mp.countParts() ; i++ ) {
			names[i] = mp.getHeaderParams(i,"content-disposition").getProperty("name") ;
		}
	}

    /**
		Returns the values of the specified parameter for the request as an array of strings, or null if the named parameter does not exist.
		@param name the name of the parameter whose value is required. 
		@return An array of strings, or null if the named parameter does not exist.
	*/
	public String[] getParameterValues (String name) {
		LinkedList list = new LinkedList () ;
		for ( int i = 0 ; i<names.length ; i++ ) {
			if ( names[i].equals(name) ) {
				list.add((String)mp.getBody(i)) ;
			}
		}
		if ( list.size()==0 ) {
			return null ;
		}
		String[] ret = new String[list.size()] ;
		for ( int i = 0 ; i<list.size() ; i++ ) {
			ret[i] = (String)list.get(i) ;
		}
		return ret ;
	}
	
	/**
		Returns the value of the specified parameter, or null if the named parameter does not exist.
		@param name the name of the parameter whose value is required. 
		@return An array of strings, or null if the named parameter does not exist.
	*/
	public String getParameter (String name) {
		String ret[] = getParameterValues(name) ;
		if ( ret == null  ) {
			return null ;
		}
		return ret[0] ;
	}

	/**
		Gets the filename produced by a "file"-control on a form.
		@param The name of the control.
		@return The filename, or null if none is found.
	*/	
	public String getFilename (String name) {
		for ( int i = 0 ; i<names.length ; i++ ) {
			if ( names[i].equals(name) ) {
				String filename = (mp.getHeaderParams(i,"content-disposition").getProperty("filename")) ;
                if (null != filename) {
                    filename = filename.substring( filename.lastIndexOf( '/' ) + 1 );
                    filename = filename.substring( filename.lastIndexOf( '\\' ) + 1 );
                }
                return filename ;
			}
		}
		return null ;
	}

}