package com.imcode.imcms.document.text;

import imcode.util.PropertyManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Text content filter, based on Jsoup's tags whitelist and cleaning text feature.
 *
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
@Component
public class TextContentFilter {
    private final Whitelist htmlTagsWhitelist = Whitelist.none()
            .addAttributes("img", "class",  "data-no", "data-meta", "data-cke-saved-src", "src");

    @PostConstruct
    public void init() {
        final String classPath = this.getClass()
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        final File imcmsRoot = new File(classPath).getParentFile().getParentFile();
        PropertyManager.setRoot(imcmsRoot);

        final String[] whiteListTags = PropertyManager.getServerProperty("text.editor.html.tags.whitelist")
                .split(";");

        addHtmlTagsToWhiteList(whiteListTags);
    }

    public TextContentFilter addHtmlTagsToWhiteList(String[] newWhiteListTags) {
        htmlTagsWhitelist.addTags(newWhiteListTags);
        return this;
    }

    public String cleanText(String cleanMe) {
        cleanMe = StringUtils.trimToEmpty(cleanMe);
        return StringEscapeUtils.unescapeXml(Jsoup.clean(cleanMe, htmlTagsWhitelist))
                .replaceAll(">\\n ", ">")
                .replaceAll("\\n<", "<");
    }
}
