package imcode.external.diverse ;

public class JanusPrefs {
	java.util. Properties prop ;
	String m_ServerIP ;
	int m_ServletPort ;
	String m_ServletUrl ;
	String m_StartUrl ;
	String m_AdminUrl ;
	int m_StartDoc ;
	String m_NoPermissionUrl ;



public synchronized boolean loadConfig(String name)
    throws Exception
    {
      boolean rc = false;

      // Get our class loader
      ClassLoader cl = getClass().getClassLoader();

      // Attempt to open an input stream to the configuration file.
      // The configuration file is considered to be a system
      // resource.
      java.io.InputStream in;
      
      if (cl != null) {
        in = cl.getResourceAsStream(name);
      }
      else {
        in = ClassLoader.getSystemResourceAsStream(name);
      }

      // If the input stream is null, then the configuration file
      // was not found
      if (in == null) {
        throw new Exception("Servlet configuration file '" +
                           name + "' not found");
      }
      else {
        try {
          prop = new java.util.Properties();
          
          // Load the configuration file into the properties table
          prop.load(in);

          // Got the properties. Pull out the properties that we
          // are interested in
          m_ServerIP        = consume(prop, "imcserv_ip");
        	m_ServletPort     = consumeInt(prop,"imcserv_port") ;
        	m_ServletUrl      = consume(prop,"servlet_url") ;
        	m_StartUrl        = consume(prop,"start_url") ;
        	m_StartDoc        = consumeInt(prop,"start_doc") ;
        	m_AdminUrl        = consume(prop,"admin_url") ;
        	m_NoPermissionUrl = consume(prop,"no_permission_url") ;
          
          rc = true;
        }
        finally {
          // Always close the input stream
          if (in != null) {
            try {
              in.close();
            }
            catch (Exception ex) {
            }
          }
        }
      }
      return rc;
    }

  /**
    * <p>Consumes the given property and returns the value.
    *
    * @param properties Properties table
    * @param key Key of the property to retrieve and remove from
    * the properties table
    * @return Value of the property, or null if not found
    */
  private String consume(java.util.Properties p, String key)
    {
      String s = null;

      if ((p != null) &&
          (key != null)) {

        // Get the value of the key
        s = p.getProperty(key);
        
        // If found, remove it from the properties table
        if (s != null) {
          p.remove(key);
        }
      }
      return s;
    }

  /**
    * <p>Consumes the given property and returns the integer
    * value.
    *
    * @param properties Properties table
    * @param key Key of the property to retrieve and remove from
    * the properties table
    * @return Value of the property, or -1 if not found
    */
  private int consumeInt(java.util.Properties p, String key)
    {
      int n = -1;

      // Get the String value
      String value = consume(p, key);

      // Got a value; convert to an integer
      if (value != null) {
        try {
          n = Integer.parseInt(value);
        }
        catch (Exception ex) {
        }
      }
      return n;
    }
	
	public String getServerIP() {
		return m_ServerIP ;
	}
	
	public int getServerPort() {
		return m_ServletPort ;
	}
	
	public String getServletUrl() {
		return m_ServletUrl ;
	}
	
	public String getStartUrl() {
		return m_StartUrl ;
	}
	
	public int getStartDoc() {
		return m_StartDoc ;
	}
	
	public String getAdminUrl() {
		return m_AdminUrl ;
	}
	
	public String getNoPermissionUrl() {
		return m_NoPermissionUrl ;
	}

	
/**
	GetProp. Returns "" if the named property is not found.
*/

public String getProperty(String arg) {
	String answer = "" ;
	answer = prop.getProperty(arg) ;
	if( answer == null)
		return "" ;
	return answer ;
	}	
	

	
} // END CLASS
	
	
	
	