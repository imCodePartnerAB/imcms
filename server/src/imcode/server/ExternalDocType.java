package imcode.server ;

public class ExternalDocType implements java.io.Serializable {

    private int    m_DocType ;
	private String m_CallServlet ;

    public ExternalDocType(int DocType,String CallServet ) {
		m_DocType     = DocType ;
		m_CallServlet = CallServet ;
    }


	public int getDocType() {
		return m_DocType ;
	}


	public String getCallServlet() {
		return m_CallServlet ;
	}

}
