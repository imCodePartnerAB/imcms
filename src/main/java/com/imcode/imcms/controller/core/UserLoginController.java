package com.imcode.imcms.controller.core;

import com.imcode.imcms.domain.service.LanguageService;
import imcode.server.Imcms;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.imcode.imcms.servlet.VerifyUser.REQUEST_PARAMETER__NEXT_META;
import static com.imcode.imcms.servlet.VerifyUser.REQUEST_PARAMETER__NEXT_URL;
import static imcode.util.Utility.getUserLanguageFromCookie;
import static imcode.util.Utility.writeUserLanguageCookie;

@Controller
public class UserLoginController {

	private final LanguageService languageService;

	UserLoginController(LanguageService languageService) {
		this.languageService = languageService;
	}

	@RequestMapping({"/login**", "/login/**"})
    public ModelAndView goToLoginPage(@RequestParam(value = REQUEST_PARAMETER__NEXT_URL, required = false) String nextUrl,
                                      @RequestParam(value = REQUEST_PARAMETER__NEXT_META, required = false) String nextMeta,
                                      @RequestParam(value = "lang", required = false) String requestedLangCode,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {

        final ModelAndView modelAndView = new ModelAndView("Login");
        final String documentPathPrefix = Imcms.getServices().getConfig().getDocumentPathPrefix();

        String nextUrlParam = null;

        if (nextUrl != null) {
            nextUrlParam = nextUrl;

        } else if (nextMeta != null) {
            nextUrlParam = request.getContextPath() + documentPathPrefix + nextMeta;
        }

        if (nextUrlParam != null) {
            modelAndView.addObject(REQUEST_PARAMETER__NEXT_URL, nextUrlParam);
        }

        final String languageCode = languageService.isLanguageAvailableByCode(requestedLangCode) ? requestedLangCode : getUserLanguageFromCookie(request.getCookies()).getCode();

		modelAndView.addObject("userLanguage", languageCode);
		writeUserLanguageCookie(response, languageCode);

        return modelAndView;
    }

}
