package imcode.server.parser ;

public interface Text extends Node {
    final static String CVS_REV = "$Revision$" ;
    final static String CVS_DATE = "$Date$" ;

    /** @return A String containing the contents of this element. **/
    public String getContent() ;

}
