package imcode.server ;

import java.io.IOException ;

public class Preference {
	java.util. Properties prop ;
	String m_TemplateHome = "" ;
	String m_DefaultHomePage = "" ;
	String m_ServletUrl = "" ;
	String m_ImageFolder = "" ;
	String m_ExternalDocType = "" ;
	String m_StartUrl = "" ;
	String m_Language = "" ;
	String m_WebMaster = "" ;
	String m_WebMasterEmail = "" ;
	String m_ServerMaster = "" ;
	String m_ServerMasterEmail = "" ;
	int m_ServerPort ;
	int m_ServerCount = 1;

	public boolean loadConfig(String name,String serverName)
	throws IOException {
		boolean rc = false;

		// Get our class loader
		ClassLoader cl = getClass().getClassLoader();

		// Attempt to open an input stream to the configuration file.
		// The configuration file is considered to be a system
		// resource.

		java.io.InputStream in = new java.io.FileInputStream(name);

		// If the input stream is null, then the configuration file
		// was not found
		if (in == null) {
			throw new IOException("Imcode Server configuration file '" +
				name + "' not found");
		} else {
			try {
				prop = new java.util.Properties();

				// Load the configuration file into the properties table
				prop.load(in);

				// Got the properties. Pull out the properties that we
				// are interested in
				m_TemplateHome      = consume(prop, serverName + ".template_home");
				m_DefaultHomePage   = consume(prop, serverName + ".default_home_page");
				m_ServletUrl        = consume(prop, serverName + ".servlet_url") ;
				m_ImageFolder       = consume(prop, serverName + ".image_folder") ;
				m_ExternalDocType   = consume(prop, serverName + ".external_doctype") ;
				m_StartUrl          = consume(prop, serverName + ".start_url") ;
				m_Language          = consume(prop, serverName + ".language") ;
				m_WebMaster		   = consume(prop, serverName + ".web_master") ;
				m_WebMasterEmail	   = consume(prop, serverName + ".web_master_email") ;
				m_ServerMaster	   = consume(prop, serverName + ".server_master") ;
				m_ServerMasterEmail = consume(prop, serverName + ".server_master_email") ;	     
				m_ServerPort        = consumeInt(prop, "server_port") ;	
				m_ServerCount       = consumeInt(prop, "server_count") ;
				
					rc = true;
			}
			finally {
				// Always close the input stream
				if (in != null) {
					try {
						in.close();
					}
					catch (IOException ex) {
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
	private String consume(java.util.Properties p, String key) {
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
	private int consumeInt(java.util.Properties p, String key) {
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

	public String getTemplateHome() {
		return m_TemplateHome ;
	}

	public String getDefaultHomePage() {
		return m_DefaultHomePage ;
	}

	public String getServletUrl() {
		return m_ServletUrl ;
	}

	public String getImageFolder() {
		return m_ImageFolder ;
	}

	public String getExternalDocType() {
		return m_ExternalDocType ;
	}

	public String getStartUrl() {
		return m_StartUrl ;
	}

	public String getLanguage() {
		return m_Language ;
	}

	public String getWebMaster() {
		return m_WebMaster ;
	}

	public String getWebMasterEmail() {
		return m_WebMasterEmail ;
	}

	public String getServerMaster() {
		return m_ServerMaster ;
	}


	public String getServerMasterEmail() {
		return m_ServerMasterEmail ;
	}


	public int getServerPort() {
		return m_ServerPort ;
	}
	
	public int getServerCount() {
		return m_ServerCount ;
	}





} // END CLASS
