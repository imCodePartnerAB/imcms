package imcode.server ;

import java.util.* ;


public class Template {
    private final static String CVS_REV="$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private int id ;
    private String name ;


    /**
       Constructor.
    */
    public Template(int id,String name) {
	this.id          = id ;
	this.name = name ;
    }

    /**
       Get template id.
    */
    public int getId() {
	return id ;
    }

    /**
       Get template name.
    */
    public String getName() {
	return name ;
    }
}

