package imcode.server ;

import org.apache.log4j.* ;

/**
   Class that represents a text in imCMS.
   It only represents an abstract model of a text,
   so it should contain only the text itself, together with the type of the text.
   It's up to View-classes using this to
   format the text correctly for the target view themselves.
**/
public class IMCText {

    protected String text ;
    protected int type ;

    /* Text-types. */

    /** Plain text, with linebreaks. **/
    public final static int TEXT_TYPE_PLAIN              = 0 ;

    /** HTML-code. **/
    public final static int TEXT_TYPE_HTML               = 1 ;

    /**
       Create a text for a text-page.
       @param text The text
       @param type The type of the text. Either
    **/
    public IMCText (String text, int type) {
	setText(text) ;
	setType(type) ;
    }

    /**
       Gets the value of text

       @return the value of text
    **/
    public String getText() {
	return this.text;
    }

    /**
       Sets the value of text

       @param text Value to assign to text
    **/
    public void setText(String text){
	this.text = text;
    }

    /**
       Gets the value of type

       @return the value of type
    **/
    public int getType() {
	return this.type;
    }

    /**
       Sets the value of type

       @param type Value to assign to type
    **/
    public void setType(int type){
	switch ( type ) {
	case TEXT_TYPE_PLAIN:
	case TEXT_TYPE_HTML:
	    this.type = type;
	    break;
	default:
	    throw new IllegalArgumentException("Illegal text-type.") ;
	}
    }

    /**
       Equivalent to getText()
    **/
    public String toString() {
	return getText() ;
    }

}
