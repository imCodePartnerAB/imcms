package imcode.util;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import org.apache.velocity.VelocityContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class VelocityTag extends BodyTagSupport implements BodyTag {

    public int doEndTag() throws JspException {
        try {
            UserDomainObject user = Utility.getLoggedOnUser( (HttpServletRequest)pageContext.getRequest() );
            IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
            VelocityContext context = service.getVelocityContext( user );
            service.getVelocityEngine( user ).evaluate( context, pageContext.getOut(), "velocity", bodyContent.getReader() );
        } catch ( Exception e ) {
            throw new JspException( e ) ;
        }
        return super.doEndTag() ;
    }

}