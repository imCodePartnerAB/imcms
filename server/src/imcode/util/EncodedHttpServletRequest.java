/* ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */

package imcode.util ;

import javax.servlet.* ;
import javax.servlet.http.* ;

import java.util.* ;
import java.io.UnsupportedEncodingException ;
import java.io.IOException ;

import imcode.server.IMCConstants ;
import org.apache.log4j.Category ;

/**
   The class wraps a servlet 2.2 HttpServletRequest
   to add support for the servlet 2.3 HttpServletRequest method setCharacterEncoding().

   This class is a ripoff of parts of Apache Jakarta Tomcat 4.0.1
   (org.apache.catalina.util.RequestUtil, org.apache.catalina.connector.HttpRequestBase,
   and org.apache.catalina.connector.RequestBase),
   hence the license header.
   This saves me from having to reinvent the wheel, until we move to servlet 2.3.

   @since v1_5_0-pre10

**/
public class EncodedHttpServletRequest extends HttpServletRequestWrapper {

    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
     * The parsed parameters for this request.  This is populated only if
     * parameter information is requested via one of the
     * <code>getParameter()</code> family of method calls.  The key is the
     * parameter name, while the value is a String array of values for this
     * parameter.
     * <p>
     * <strong>IMPLEMENTATION NOTE</strong> - Once the parameters for a
     * particular request are parsed and stored here, they are not modified.
     * Therefore, application level access to the parameters need not be
     * synchronized.
     */
    private HashMap parameters = null ;

    /** The character encoding parsed from the content-typ. **/
    private String characterEncoding = null ;

    /** Whether we have already parsed the content-type for an encoding or not. **/
    private boolean parsedEncoding = false ;

    /** Whether we have already parsed the parameters or not. **/
    private boolean parsed = false ;

    /** A Log4J Category **/
    private static Category log = Category.getInstance( IMCConstants.ERROR_LOG ) ;

    /**
     * The ServletInputStream that has been returned by
     * <code>getInputStream()</code>, if any.
     */
    private ServletInputStream stream = null;

    /**
       Creates an EncodedHttpServletRequest that wraps the specified HttpServletRequest.
    **/
    public EncodedHttpServletRequest(HttpServletRequest request) {
	super(request) ;
    }

    /**
     * Overrides the name of the character encoding used in the body of
     * this request.  This method must be called prior to reading request
     * parameters or reading input using <code>getReader()</code>.
     *
     * @param enc The character encoding to be used
     *
     * @exception UnsupportedEncodingException if the specified encoding
     *  is not supported
     *
     * @since Servlet 2.3
     */
    public void setCharacterEncoding(String enc)
        throws UnsupportedEncodingException {
        // Ensure that the specified encoding is valid
        byte buffer[] = new byte[1];
        buffer[0] = (byte) 'a';
        String dummy = new String(buffer, enc);
        // Save the validated encoding
        this.characterEncoding = enc;

    }

    /**
       Return the character encoding (charset) set for this request.
       It is either parsed from the content-type or set explicitly with setCharacterEncoding().
    **/
    public String getCharacterEncoding() {
	if (this.characterEncoding == null && !this.parsedEncoding) {
	    this.characterEncoding = this.parseCharacterEncoding() ;
	    this.parsedEncoding = true ;
	}
	return this.characterEncoding ;
    }

    /**
     * Return the servlet input stream for this Request.  The default
     * implementation returns a servlet input stream created by
     * <code>createInputStream()</code>.
     *
     * @exception IllegalStateException if <code>getReader()</code> has
     *  already been called for this request
     * @exception IOException if an input/output error occurs
     */
    public ServletInputStream getInputStream() throws IOException {

        if (stream == null)
            stream = super.getInputStream();
        return (stream);

    }

    /**
     * Return the value of the specified request parameter, if any; otherwise,
     * return <code>null</code>.  If there is more than one value defined,
     * return only the first one.
     *
     * @param name Name of the desired request parameter
     */
    public String getParameter(String name) {
     
        parseParameters();
        String values[] = (String[]) parameters.get(name);
        if (values != null)
            return (values[0]);
        else
            return (null);

    }

    /**
     * Return the defined values for the specified request parameter, if any;
     * otherwise, return <code>null</code>.
     *
     * @param name Name of the desired request parameter
     */
    public String[] getParameterValues(String name) {

        parseParameters();
        String values[] = (String[]) parameters.get(name);
        if (values != null)
            return (values);
        else
            return (null);

    }

    /**
     * Return the names of all defined request parameters for this request.
     */
    public Enumeration getParameterNames() {

        parseParameters();
        return (new Enumerator(parameters.keySet()));

    }


    /**
     * Parse the parameters of this request, if it has not already occurred.
     * If parameters are present in both the query string and the request
     * content, they are merged.
     */
    protected void parseParameters() {

        if (parsed)
            return;

        HashMap results = parameters;
        if (results == null)
            results = new HashMap();

        String encoding = getCharacterEncoding();
        if (encoding == null) 
            encoding = "ISO-8859-1";

	log.debug("Using encoding "+encoding) ;

        // Parse any parameters specified in the query string
        String queryString = getQueryString();
        try {
            parseParameters(results, queryString, encoding);
        } catch (Throwable t) {
	    log.debug("Error parsing query-string.",t) ;
        }

        // Parse any parameters specified in the input stream
        String contentType = getContentType();
        if (contentType == null)
            contentType = "";
        int semicolon = contentType.indexOf(';');
        if (semicolon >= 0)
            contentType = contentType.substring(0, semicolon).trim();
        if ("POST".equals(getMethod()) && (getContentLength() > 0)
            && (this.stream == null)
            && contentType.startsWith("application/x-www-form-urlencoded")) {
            try {
                int max = getContentLength();
                int len = 0;
                byte buf[] = new byte[getContentLength()];
                ServletInputStream is = getInputStream();
                while (len < max) {
                    int next = is.read(buf, len, max - len);
                    len += next;
                }
                is.close();
		log.debug("UTF-8: "+new String(buf,"ISO-8859-1")+" :UTF-8") ;
                parseParameters(results, buf, encoding);
            } catch (Throwable t) {
		log.debug("Error parsing inputstream.",t) ;
            }
        }

        // Store the final results
        parsed = true;
        parameters = results;
    }

    /**
     * Parse the character encoding from the specified content type header.
     * If the content type is null, or there is no explicit character encoding,
     * <code>null</code> is returned.
     *
     * @param contentType a content type header
     */
    private String parseCharacterEncoding() {
	String contentType = getContentType() ;
	if (contentType == null) {
	    return null ;
	}
        int start = contentType.indexOf("charset=");
        if (start < 0) {
            return (null) ;
	}
        String encoding = contentType.substring(start + 8);
        int end = encoding.indexOf(';');
        if (end >= 0)
            encoding = encoding.substring(0, end);
        encoding = encoding.trim();
        if ((encoding.length() > 2) && (encoding.startsWith("\""))
            && (encoding.endsWith("\"")))
            encoding = encoding.substring(1, encoding.length() - 1);
        return (encoding.trim());
    }

    /**
     * Append request parameters from the specified String to the specified
     * Map.  It is presumed that the specified Map is not accessed from any
     * other thread, so no synchronization is performed.
     * <p>
     * <strong>IMPLEMENTATION NOTE</strong>:  URL decoding is performed
     * individually on the parsed name and value elements, rather than on
     * the entire query string ahead of time, to properly deal with the case
     * where the name or value includes an encoded "=" or "&" character
     * that would otherwise be interpreted as a delimiter.
     *
     * @param map Map that accumulates the resulting parameters
     * @param data Input string containing request parameters
     * @param urlParameters true if we're parsing parameters on the URL
     *
     * @exception IllegalArgumentException if the data is malformed
     */
    public static void parseParameters(Map map, String data, String encoding)
        throws UnsupportedEncodingException {
 
        if ((data != null) && (data.length() > 0)) {
            int len = data.length();
            byte[] bytes = new byte[len];
            data.getBytes(0, len, bytes, 0);
            parseParameters(map, bytes, encoding);
        }
 
    }


    /**
     * Append request parameters from the specified String to the specified
     * Map.  It is presumed that the specified Map is not accessed from any
     * other thread, so no synchronization is performed.
     * <p>
     * <strong>IMPLEMENTATION NOTE</strong>:  URL decoding is performed
     * individually on the parsed name and value elements, rather than on
     * the entire query string ahead of time, to properly deal with the case
     * where the name or value includes an encoded "=" or "&" character
     * that would otherwise be interpreted as a delimiter.
     *
     * NOTE: byte array data is modified by this method.  Caller beware.
     *
     * @param map Map that accumulates the resulting parameters
     * @param data Input string containing request parameters
     * @param encoding Encoding to use for converting hex
     *
     * @exception UnsupportedEncodingException if the data is malformed
     */
    private static void parseParameters(Map map, byte[] data, String encoding) throws UnsupportedEncodingException {
	if (data != null && data.length > 0) {
            int    pos = 0;
            int    ix = 0;
            int    ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                case '&':
                    value = new String(data, 0, ox, encoding);
                    if (key != null) {
                        putMapEntry(map, key, value);
                        key = null;
                    }
                    ox = 0;
                    break;
                case '=':
                    key = new String(data, 0, ox, encoding);
                    ox = 0;
                    break;
                case '+':
                    data[ox++] = (byte)' ';
                    break;
                case '%':
                    data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4)
                                    + convertHexDigit(data[ix++]));
                    break;
                default:
                    data[ox++] = c;
                }
            }
            //The last value does not end in '&'.  So save it now.
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map, key, value);
            }
        }

    }

    /**
     * Convert a byte character value to hexidecimal digit value.
     *
     * @param b the character value byte
     */
    private static byte convertHexDigit( byte b ) {
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        return 0;
    }

    /**
     * Put name value pair in map.
     *
     * @param b the character value byte
     *
     * Put name and value pair in map.  When name already exist, add value
     * to array of values.
     */
    private static void putMapEntry( Map map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = (String[]) map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

}
