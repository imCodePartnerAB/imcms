package com.imcode.imcms.addon.imagearchive.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.imcode.imcms.addon.imagearchive.SessionConstants;
import com.imcode.imcms.addon.imagearchive.service.Facade;
import com.imcode.imcms.addon.imagearchive.util.ImcmsLocaleResolver;
import com.imcode.imcms.addon.imagearchive.util.SessionUtils;
import com.imcode.imcms.addon.imagearchive.util.Utils;
import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;

@Controller
public class GeneralController {
    private static final Log log = LogFactory.getLog(GeneralController.class);
    
    @Autowired
    private Facade facade;
    
    @Autowired
    private ImcmsLocaleResolver localeResolver;
    
    @RequestMapping("/archive/language")
    public ModelAndView languageChangeHandler(
            @RequestParam(required=false) String lang, 
            @RequestParam(required=false) String redir, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        lang = StringUtils.trimToNull(lang);
        redir = StringUtils.trimToNull(redir);
        
        localeResolver.setLocale(request, response, new Locale(lang));
        
        if (redir != null) {
            try {
                response.sendRedirect(response.encodeRedirectURL(redir));
            } catch (IOException ex) {
                log.warn(ex.getMessage(), ex);
            }
        } else {
            return new ModelAndView("redirect:/web/archive");
        }
        
        return null;
    }
    
    @RequestMapping("/archive/use")
    public String useInImcmsHandler(
            @RequestParam(required=false) Long id, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            HttpSession session) {
        ContentManagementSystem cms = ContentManagementSystem.fromRequest(request);
        User user = cms.getCurrentUser();
        
        String returnTo = SessionUtils.getImcmsReturnToUrl(session);
        
        if (user.isDefaultUser()) {
            Utils.redirectToLogin(request, response, facade);
            
            return null;
        } else if (id == null || returnTo == null || !facade.getImageService().canUseImage(user, id)) {
            return "redirect:/web/archive";
        }
        
        String imageName = facade.getImageService().findImageName(id);
        String fileName = facade.getFileService().transferImageToImcms(id);
        
        StringBuilder builder = new StringBuilder(returnTo);
        builder.append("&archive_img_id=");
        builder.append(id);
        builder.append("&archive_img_nm=");
        builder.append(Utils.encodeUrl(imageName));
        builder.append("&archive_file_nm=");
        builder.append(Utils.encodeUrl(fileName));
        
        try {
            response.sendRedirect(builder.toString());
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        session.removeAttribute(SessionConstants.IMCMS_RETURN_URL);
        
        return null;
    }
    
    @RequestMapping("/archive/back")
    public String backToImcmsHandler(HttpServletResponse response, HttpSession session) {
        String returnTo = SessionUtils.getImcmsReturnToUrl(session);
        if (returnTo == null) {
            return "redirect:/web/archive";
        }
        
        session.removeAttribute(SessionConstants.IMCMS_RETURN_URL);
        
        try {
            response.sendRedirect(returnTo);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
        
        return null;
    }
    
    @RequestMapping("/archive/service/keyword/add")
    public void addKeywordHandler(
    		@RequestParam(required=false) String keyword, 
    		HttpServletResponse response) {
    	Utils.addNoCacheHeaders(response);
    	
    	keyword = StringUtils.trimToEmpty(keyword);
    	
    	if (StringUtils.isEmpty(keyword)) {
    		Utils.sendErrorCode(response, HttpServletResponse.SC_NOT_FOUND);
    	} else {
    		keyword = StringUtils.substring(keyword, 0, 50);
    		facade.getImageService().createKeyword(keyword);
    		
    		response.setStatus(HttpServletResponse.SC_OK);
    	}
    }
}
