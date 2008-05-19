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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

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
    
    private Map<String, I18nLanguage> i18nHosts = new HashMap<String, I18nLanguage>();
    
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
    	
    	if (i18nHosts.size() > 0) {
    		String hostname = request.getServerName();
    		I18nLanguage hostLanguage = i18nHosts.get(hostname);
    		
    		if (hostLanguage != null && !language.equals(hostLanguage)) {
    			language = hostLanguage;
    		}
    	}
    	
    	// TODO i18n: remove lang session parameter
    	// request and thread local parameters 
    	
		request.getSession().setAttribute("lang", language);    	
		request.setAttribute("currentLanguage", language);
    	
    	I18nSupport.setCurrentLanguege(language);
    }

    /**
     * Integrates springframework.
     * Set up i18n support.
     * 
     * TODO i18n: refactor out 
     */
    public void init( FilterConfig config ) throws ServletException {
    	ServletContext servletContext = config.getServletContext();
    	initSpringframework(servletContext);
    	initI18nSupport(servletContext);
    }
    
    private void initSpringframework(ServletContext servletContext) throws ServletException {
    	logger.info("Initializing springframework web application context.");
    	
    	WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(
    			servletContext);
    	
    	DefaultImcmsServices services = (DefaultImcmsServices)Imcms.getServices();
    	
    	services.setWebApplicationContext(webApplicationContext);    	
    }
    
    /**
     * Default language can be set either in configuration file 
     * or one (and only one) language in the database table i18n_languages
     * should be flagged as default.
     */
	private void initI18nSupport(ServletContext servletContext) throws ServletException {
    	logger.info("Initializing i18n support.");
    	
    	LanguageDao languageDao = (LanguageDao) Imcms.getServices().getSpringBean("languageDao");    	    	
    	List<I18nLanguage> languages = languageDao.getAllLanguages();    
    	I18nLanguage defaultLanguage = languageDao.getDefaultLanguage();
    	
    	if (languages.size() == 0) {
    		String msg = "I18n configuration error. Database table i18n_languages must contain at least one record.";
    		logger.fatal(msg);
    		throw new ServletException(msg);
    	}
    	    	
    	Properties properties = Imcms.getServerProperties();    	
    	String defaultLanguageCode = properties.getProperty("i18n.defaultLanguage.code");  
    	
    	if (StringUtils.isEmpty(defaultLanguageCode)) {
    		logger.info("I18n configuration property [i18n.defaultLanguage.code] is not set.");    
    		
    		if (defaultLanguage == null) {
        		String msg = "I18n configuration error. Default language is not set. Plese set configuration property [i18n.defaultLanguage.code].";
        		logger.fatal(msg);
        		throw new ServletException(msg);        			
    		}
    	} else {   
    		logger.info("I18n configuration property [i18n.defaultLanguage.code] is set to [" + defaultLanguageCode + "].");    	
    		
    		I18nLanguage newDefaultLanguage = languageDao.getByCode(defaultLanguageCode);
    		
    		if (newDefaultLanguage == null) {
        		String msg = "I18n configuration error. No language with code [" + defaultLanguageCode + "] was found in the database.";
        		logger.fatal(msg);
        		throw new ServletException(msg);    			
    		}
    		
    		if (!newDefaultLanguage.equals(defaultLanguage)) {
    			logger.info("Updating i18n default language database settings." +
    					"Current default language: [" + defaultLanguage + "], " +
    					"new default language: [" + newDefaultLanguage + "].");
    			
    			languageDao.setDefaultLanguage(newDefaultLanguage);
    			defaultLanguage = newDefaultLanguage;
    			languages = languageDao.getAllLanguages(); 
    		}
        	
        	if (StringUtils.isEmpty(defaultLanguageCode)) {
        		String msg = "I18n configuration error. No default language for i18n support is defined.";
        		logger.fatal(msg);
        		throw new ServletException(msg);        		
        	}
    	} 
    	
    	I18nSupport.setDefaultLanguage(defaultLanguage);
    	I18nSupport.setLanguages(languages);
    	
    	servletContext.setAttribute("defaultLanguage", defaultLanguage);
    	servletContext.setAttribute("languages", languages);	
    	
    	String prefix = "i18n.host.";
    	int prefixLength = prefix.length(); 
    	
    	for (Entry entry: properties.entrySet()) {
    		String key = (String)entry.getKey();
    		
    		if (!key.startsWith(prefix)) {
    			continue;
    		}
    		
			String languageCode = key.substring(prefixLength);
			I18nLanguage language = I18nSupport.getByCode(languageCode);
			
			if (language == null) {
				String msg = "I18n configuration error. No language for code [" + languageCode + "] is defined.";
        		logger.fatal(msg);
        		throw new ServletException(msg);
			}
			
			String value = (String)entry.getValue();
			String hosts[] = value.split("[ \\t]*,[ \\t]*");
			
			logger.info("I18n configurtion: virtual hosts mapped for language [" + language + "]: [" + value + "].");
			
			for (String host: hosts) {
				i18nHosts.put(host, language);
			}
    	}
	}    

    public void destroy() {}
}
