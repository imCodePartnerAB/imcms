package imcode.util ;

import java.util.* ;

/**
	Parses MIME multipart/* data into parts and MIME-headers.
*/

public class MultipartParser {

	String bodies[] ;	// Contains the MIME-bodies
	Properties headers[] ;	//Contains the MIME-headers
	Hashtable headparams[] ;	//Contains a Hashtable of Properties to keep track of the parameters for each individual header.
	
	/**
		Parses the multipartdata into parts.
		@param data The multipartdata.
		@param contenttype The MIME-header "Content-Type" for the data. Must contain a 'boundary=" "' parameter.
	*/
	public MultipartParser (byte data[], String contenttype) {
		try {
			parse (new String(data,"8859_1"),contenttype) ;
		} catch ( java.io.UnsupportedEncodingException ex ) {
		}
	}
	/**
		Parses the multipartdata into parts.
		@param data The multipartdata.
		@param contenttype The MIME-header "Content-Type" for the data. Must contain a 'boundary=" "' parameter.
	*/
	public MultipartParser (String data, String contenttype) {
		parse (data,contenttype) ;
	}
	
	private void parse (String data, String contenttype) {
		int boundaryindex = contenttype.indexOf("boundary=") ;
		if (boundaryindex == -1 || contenttype.indexOf("multipart") == -1) {
			throw new IllegalArgumentException ("Not given a valid Content-Type") ;
		}
		String boundary = contenttype.substring(boundaryindex+9) ;
		if (boundary.charAt(0) == '\"') {
			boundary = boundary.substring(1,boundary.length()-1) ;
		}
		String dashboundary = "--" + boundary ;
		String delimiter = "\r\n" + dashboundary ;
		String closedelimiter = delimiter + "--" ;
		// Remove all data before and including the first boundary (dashboundary), and after and including the last boundary (closedelimiter).
		String body = data.substring(data.indexOf(dashboundary)+dashboundary.length(),data.indexOf(closedelimiter)) ;
		// Partition the data into parts. They are separated by delimiters. (CRLF + "--" + boundary)
		ExStringTokenizer est = new ExStringTokenizer(body,delimiter) ;
		bodies = new String [est.countTokens()] ;
		headers = new Properties [est.countTokens()] ;
		headparams = new Hashtable [est.countTokens()] ;
		int bodycounter = 0 ;
		while ( est.hasMoreTokens() ) {
			String temp = est.nextToken() ;
			temp = temp.substring(temp.indexOf("\r\n")+2) ;
			int tempindex = temp.indexOf("\r\n\r\n") ;
			// Partition the parts into MIME-headers and data
			String head = temp.substring(0,tempindex+2) ;
			bodies[bodycounter] = temp.substring(tempindex+4) ;
			// Partition the MIME-headers into individual headers. They are separated by CRLFs.
			ExStringTokenizer esth = new ExStringTokenizer(head,"\r\n") ;
			headers[bodycounter] = new Properties () ;
			headparams[bodycounter] = new Hashtable () ;
			while ( esth.hasMoreTokens() ) {
				String temph = esth.nextToken() ;
				int headindex = temph.indexOf(":") ;
				// Partition the individual headers into { header=value [; parameter=value] [...] }
				StringTokenizer sth = new StringTokenizer(temph,";") ;
				temph = sth.nextToken() ;
				String headkey = temph.substring(0,headindex).trim() ;
				String headvalue = temph.substring(headindex+1).trim() ;
				Properties hp = new Properties () ;
				while ( sth.hasMoreTokens() ) {
					String tempp = sth.nextToken().trim() ;
					int pindex = tempp.indexOf("=") ;
					String pkey = tempp.substring(0,pindex) ;
					String pvalue = tempp.substring(pindex+1) ;
					// Remove any quotes from the parameter.
					if ( pvalue.charAt(0) == '\"' ) {
						pvalue = pvalue.substring(1,pvalue.length()-1) ;
					}
					hp.setProperty(pkey.toLowerCase(),pvalue) ;
					headparams[bodycounter].put(headkey.toLowerCase(),hp) ;
				}
				headers[bodycounter].setProperty(headkey.toLowerCase(),headvalue) ;
			}
			bodycounter++ ;
		}
	}
	
	/**
		Counts the number of parts.
		@return The number of parts.
	*/
	public int countParts () {
		return bodies.length ;
	}
	/**
		Gets the body from a part.
		@param part The part to get the body from.
		@return A string containing the body.
	*/
	public String getBody (int part) {
		return bodies[part] ;
	}
	/**
		Gets the MIME-headers from a part.
		@param part The part to get the header from.
		@return A propertylist containing the headers.
	*/
	public Properties getHeaders (int part) {
		return headers[part] ;
	}
	
	/**
		Gets a single MIME-header from a part.
		@param part The part to get the header from.
		@param name The name of the header.
		@return The value of the header.
	*/
	public String getHeader (int part, String name) {
		return headers[part].getProperty(name) ;
	}
	
	/**
		Gets the parameters of a single MIME-header from a part.
		@param part The part to get the header from.
		@param name The name of the header.
		@return A propertylist containing the parameters of the header.
	*/
	public Properties getHeaderParams (int part, String name) {
		return (Properties)headparams[part].get(name) ;
	}

}