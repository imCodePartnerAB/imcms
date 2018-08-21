package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Text content filter, based on Jsoup's tags whitelist and cleaning text feature.
 * Used to clean non-supported tags from e.g. imcms:text tag.
 * <p>
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
@Component
class PropertyBasedTextContentFilter implements TextContentFilter {

    private static final String[] WHITE_LIST_ATTRIBUTES = {"class", "data-no", "data-meta", "data-cke-saved-src", "src"};
    private final Whitelist htmlTagsWhitelist = Whitelist.none().addAttributes("img", WHITE_LIST_ATTRIBUTES);
    private final Properties imcmsProperties;

    @Autowired
    PropertyBasedTextContentFilter(Properties imcmsProperties) {
        this.imcmsProperties = imcmsProperties;
    }

    @PostConstruct
    private void init() {
        final String[] whiteListTags = imcmsProperties.getProperty("text.editor.html.tags.whitelist").split(";");
        addHtmlTagsToWhiteList(whiteListTags);
    }

    @Override
    public void addHtmlTagsToWhiteList(String[] newWhiteListTags) {
        htmlTagsWhitelist.addTags(newWhiteListTags)
                .removeTags("head", "script", "embed", "style")
                .addAttributes(":all", "src", "href", "rel", "alt", "align", "width", "height", "border",
                        "cellspacing", "cellpadding", "target", "title", "class", "data-doc-id", "data-lang-code",
                        "data-in-text", "data-index", "data-mce-style", "data-mce-src");
    }

    @Override
    public String cleanText(String cleanMe) {
        cleanMe = StringUtils.trimToEmpty(cleanMe);
        return StringEscapeUtils.unescapeXml(Jsoup.clean(cleanMe, htmlTagsWhitelist))
                .replaceAll(">\\n ", ">")
                .replaceAll("\\n<", "<")
                .replaceAll("(<br>*){2,}", "<br>");
    }

    @Override
    public String cleanText(String cleanMe, Text.HtmlFilteringPolicy filteringPolicy) {
        switch (filteringPolicy) {
            case RESTRICTED:
                return cleanText(cleanMe);
            case RELAXED:
                return unwrapNotAllowedTags(removeIllegalTags(cleanMe));
            case ALLOW_ALL:
            default:
                return cleanMe; // no changes
        }
    }

    private String unwrapNotAllowedTags(String cleanMe) {
        return cleanMe.replaceAll("<html>", "")
                .replaceAll("</html>", "")
                .replaceAll("<body>", "")
                .replaceAll("</body>", "")
                .replaceAll("<doctype>", "")
                .replaceAll("</doctype>", "");
    }

    private String removeIllegalTags(String cleanMe) {
        return cleanMe.replaceAll("<head>.+?</head>", "")
                .replaceAll("<script>.+?</script>", "")
                .replaceAll("<script.+?/>", "")
                .replaceAll("<embed>.+?</embed>", "")
                .replaceAll("<embed.+?/>", "")
                .replaceAll("<style>.+?</style>", "");
    }
}
