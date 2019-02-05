package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.LanguageDTO;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Language;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.imcode.imcms.servlet.ImcmsSetupFilter.USER_LANGUAGE_IN_COOKIE_NAME;

@RestController
@RequestMapping("/languages")
public class LanguageController {

    private final LanguageService languageService;

    LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping
    public List<Language> getLanguages() {
        return languageService.getAll();
    }

    @PutMapping
    public void changeLanguageForCurrentUser(@RequestBody LanguageDTO languageDTO,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {

        final HttpSession session = request.getSession();
        final Cookie[] cookies = request.getCookies();
        final String languageCode = languageDTO.getCode();

        final Optional<Cookie> optionalUserLanguageCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(USER_LANGUAGE_IN_COOKIE_NAME))
                .findFirst();

        if (optionalUserLanguageCookie.isPresent()) {
            final Cookie languageCookie = optionalUserLanguageCookie.get();

            if (!languageCookie.getValue().equals(languageCode)) {
                final Cookie newUserLanguageCookie = new Cookie(USER_LANGUAGE_IN_COOKIE_NAME, languageCode);

                newUserLanguageCookie.setMaxAge(session.getMaxInactiveInterval());
                newUserLanguageCookie.setPath("/");

                response.addCookie(newUserLanguageCookie);
            }
        }
    }
}
