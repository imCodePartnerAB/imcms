package imcode.util.test;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

import com.meterware.httpunit.WebConversation;

/**
 * @author kreiger
 */
public class HttpUnitGeneratingFilter implements Filter {

    private final static Logger log = Logger.getLogger("httpunit");

    public void init(FilterConfig filterConfig) throws ServletException {
        generate("WebConversation webConversation = new WebConversation();") ;
    }

    private void generate(String s) {
        log.info(s);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest ;
        String uri = request.getRequestURI() ;
        String method = request.getMethod() ;
        String requestName = null ;
        if ("GET".equals(method)) {
            requestName = "getRequest" ;
            generate("GetMethodWebRequest "+requestName+" = new GetMethodWebRequest(\""+uri+"\");") ;
        } else if ("POST".equals(method)) {
            requestName = "postRequest";
            generate("PostMethodWebRequest "+requestName+" = new PostMethodWebRequest(\""+uri+"\");");
        }
        Enumeration parameterNameEnumeration = request.getParameterNames() ;
        while (parameterNameEnumeration.hasMoreElements()) {
            String parameter = (String) parameterNameEnumeration.nextElement() ;
            generate(requestName+".setParameter(\""+parameter+"\",\""+request.getParameter(parameter)+"\")") ;
        }
        generate("WebResponse response = webConversation.getResponse("+requestName+");") ;
        filterChain.doFilter(servletRequest,servletResponse);
    }

    public void destroy() {
        // TODO
    }

}
