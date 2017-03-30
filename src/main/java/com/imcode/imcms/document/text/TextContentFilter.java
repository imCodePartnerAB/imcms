package com.imcode.imcms.document.text;

import imcode.util.PropertyManager;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
@Component
public class TextContentFilter {
    private final Whitelist htmlTagsWhitelist = Whitelist.none();

    @PostConstruct
    public void init() {
        final String classPath = this.getClass()
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        final File imcmsRoot = new File(classPath).getParentFile().getParentFile();
        PropertyManager.setRoot(imcmsRoot);

        htmlTagsWhitelist.addTags(
                PropertyManager.getServerProperty("text.editor.html.tags.whitelist").split(";")
        );
    }

    public TextContentFilter addHtmlTagsToWhiteList(String[] newWhiteListTags) {
        htmlTagsWhitelist.addTags(newWhiteListTags);
        return this;
    }

    public AllowedTagsCheckingResult checkBadTags(String checkMe) {
        checkMe = checkMe.replaceAll(" ", "");
        final String cleanText = Jsoup.clean(checkMe, htmlTagsWhitelist).replaceAll("[\\n ]", "");
        boolean success;
        Set<String> badTags = new HashSet<>();

        if (cleanText.length() == checkMe.length()) {
            success = true;

        } else {
            success = false;

        }

        return new AllowedTagsCheckingResult(success, badTags);
    }
}
