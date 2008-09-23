package imcode.util;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.velocity.VelocityContext;

public class VelocityTag extends BodyTagSupport {

    public int doEndTag() throws JspException {
        try {
            UserDomainObject user = Utility.getLoggedOnUser( (HttpServletRequest)pageContext.getRequest() );
            ImcmsServices service = Imcms.getServices();
            VelocityContext context = service.getVelocityContext( user );
            service.getVelocityEngine( user ).evaluate( context, pageContext.getOut(), "velocity", bodyContent.getReader() );
        } catch ( Exception e ) {
            throw new JspException( e ) ;
        }
        return super.doEndTag() ;
    }

}