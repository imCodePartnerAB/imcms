package com.imcode.imcms.servlet;

import imcode.server.DefaultImcmsServices;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.FallbackDecoder;
import imcode.util.Utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.dao.LanguageDao;

public class ImcmsSetupFilter implements Filter {

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    
    private final Logger logger = Logger.getLogger(getClass());
    
    public void doFilter( ServletRequest r, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
        r.setCharacterEncoding(Imcms.DEFAULT_ENCODING);
        
        HttpServletRequest request = (HttpServletRequest) r;

        HttpSession session = request.getSession();

        ImcmsServices service = Imcms.getServices();
        
        if ( session.isNew() ) {
            service.incrementSessionCounter();
            setDomainSessionCookie( response, session );
        }
        
        setCurrentLanguage(request);

        String workaroundUriEncoding = service.getConfig().getWorkaroundUriEncoding();
        FallbackDecoder fallbackDecoder = new FallbackDecoder(Charset.forName(Imcms.DEFAULT_ENCODING),
                                                              null != workaroundUriEncoding ? Charset.forName(workaroundUriEncoding) : Charset.defaultCharset());
        if ( null != workaroundUriEncoding ) {
            request = new UriEncodingWorkaroundWrapper(request, fallbackDecoder);
        }

        UserDomainObject user = Utility.getLoggedOnUser(request) ;
        if ( null == user ) {
            user = service.verifyUserByIpOrDefault(request.getRemoteAddr()) ;
            assert user.isActive() ;
            Utility.makeUserLoggedIn(request, user);
        }

        ResourceBundle resourceBundle = Utility.getResourceBundle(request);
        Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, new LocalizationContext(resourceBundle));
        
        Utility.initRequestWithApi(request, user);

        NDC.setMaxDepth( 0 );
        String contextPath = request.getContextPath();
        if ( !"".equals( contextPath ) ) {
            NDC.push( contextPath );
        }
        NDC.push( StringUtils.substringAfterLast( request.getRequestURI(), "/" ) );

        handleDocumentUri(chain, request, response, service, fallbackDecoder);
        NDC.setMaxDepth( 0 );
    }

    private void handleDocumentUri(FilterChain chain, HttpServletRequest request, ServletResponse response,
                                   ImcmsServices service, FallbackDecoder fallbackDecoder) throws ServletException, IOException {
        String path = Utility.fallbackUrlDecode(request.getRequestURI(), fallbackDecoder) ;
        path = StringUtils.substringAfter( path, request.getContextPath() ) ;
        String documentIdString = getDocumentIdString(service, path);
        ServletContext servletContext = request.getSession().getServletContext();
        if ( null == servletContext.getResourcePaths(path) ) {
            DocumentDomainObject document = service.getDocumentMapper().getDocument(documentIdString);
            if (null != document) {
                try {
                    GetDoc.viewDoc( document, request, (HttpServletResponse)response );
                    return ;
                } catch( NumberFormatException nfe ) {}
            }
        }
        chain.doFilter( request, response );
    }

    public static String getDocumentIdString(ImcmsServices service, String path) {
        String documentPathPrefix = service.getConfig().getDocumentPathPrefix() ;
        String documentIdString = null ;
        if ( StringUtils.isNotBlank( documentPathPrefix ) && path.startsWith( documentPathPrefix )) {
            documentIdString = path.substring( documentPathPrefix.length());
            if (documentIdString.endsWith( "/" ) ) {
                documentIdString = documentIdString.substring(0,documentIdString.length()-1);
            }
        }
        return documentIdString;
    }

    private void setDomainSessionCookie( ServletResponse response, HttpSession session ) {

        String domain = Imcms.getServices().getConfig().getSessionCookieDomain();
        if (StringUtils.isNotBlank(domain)) {
            Cookie cookie = new Cookie( JSESSIONID_COOKIE_NAME, session.getId());
            cookie.setDomain( domain );
            cookie.setPath( "/" );
            ((HttpServletResponse)response).addCookie( cookie );
        }
    }
    
    private void setCurrentLanguage(HttpServletRequest request) 
    throws ServletException {
    	String languageCode = request.getParameter("lang");
    	I18nLanguage language = null;    	
    	
    	if (languageCode != null) {
    		language = I18nSupport.getByCode(languageCode);
    	}
    	
    	if (language == null) {
    		language = (I18nLanguage)request.getSession().getAttribute("lang");    		
    	}     	    	        	
    	
    	// TODO: if session does not contain language
    	// do not allow any admin oparation and forward to front page!!!
    	if (language == null) {
    		language = I18nSupport.getDefaultLanguage();
    	}
    	
		request.getSession().setAttribute("lang", language);    	
		request.setAttribute("currentLanguage", language);		
    	
    	
    	/* 
    	if (language == null) {
    		List<I18nLanguage> languages = languageDao.getAllLanguages();
    		
    		if (languages.size() == 0) {
    			String msg = "No languages definitions found in i18n_languages table. This table must contain at least one record.";
    			logger.fatal(msg);
    			
    			throw new ServletException(msg);
    		}
    		
    		language = languages.get(0);
    	}
    	*/
    	
    	I18nSupport.setCurrentLanguege(language);
    }

    public void init( FilterConfig config ) throws ServletException {
    	WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
    			config.getServletContext());
    	
    	DefaultImcmsServices services = (DefaultImcmsServices)Imcms.getServices();
    	
    	services.setWebApplicationContext(webApplicationContext);
    	
    	LanguageDao languageDao = (LanguageDao) Imcms.getServices().getSpringBean("languageDao");
    	
    	// TODO i18n: implement 
    	//int languageCount = languageDao.checkDefaultLanguageCount();
    	//int defaultLanguageCount = languageDao.checkDefaultLanguageCount();
    	
    	I18nLanguage defaultLanguage = languageDao.getDefaultLanguage();
    	List<I18nLanguage> languages = languageDao.getAllLanguages();
    	
    	I18nSupport.setDefaultLanguage(defaultLanguage);
    	I18nSupport.setLanguages(languages);
    	
    	config.getServletContext().setAttribute("defaultLanguage", defaultLanguage);
    	config.getServletContext().setAttribute("languages", languages);
    }

    public void destroy() {
    }
}
