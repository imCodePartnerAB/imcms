package imcode.server.parser ;

public interface Text extends Node {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /** @return A String containing the contents of this element. **/
    String getContent() ;

}
