package imcode.util;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class VelocityTag extends BodyTagSupport implements BodyTag {

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().write( Utility.evaluateVelocity( bodyContent.getString(), (HttpServletRequest)pageContext.getRequest() ) );
        } catch ( Exception e ) {
            throw new JspException( e ) ;
        }
        return super.doEndTag() ;
    }

}