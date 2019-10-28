package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    private static final String[] WHITE_LIST_IMG_ATTRIBUTES = {
            "class", "data-no", "data-meta", "data-cke-saved-src", "src"
    };
    private static final String[] WHITE_LIST_COMMON_ATTRIBUTES = {
            "src", "href", "rel", "alt", "align", "width", "height", "border",
            "cellspacing", "cellpadding", "target", "title", "data-doc-id", "data-lang-code",
            "data-in-text", "data-index", "data-mce-style", "data-mce-src"
    };

    private final Whitelist plainTextWhitelist = Whitelist.none().addTags("br", "p");
    private final Whitelist basicHtmlTagsWhiteList = Whitelist.basic().removeTags("span");

    @PostConstruct
    public void init() {
        addCommonAttributesToWhiteLists(plainTextWhitelist, basicHtmlTagsWhiteList);
    }

    private void addCommonAttributesToWhiteLists(Whitelist... whitelists) {
        for (Whitelist whitelist : whitelists) {
            whitelist
                    .addAttributes("img", WHITE_LIST_IMG_ATTRIBUTES)
                    .addAttributes(":all", WHITE_LIST_COMMON_ATTRIBUTES);
        }
    }

    // TODO: Need to delete this method when TextDocument will be removed
    @Override
    public String cleanText(String cleanMe) {
        cleanMe = StringUtils.trimToEmpty(cleanMe);
        return StringEscapeUtils.unescapeXml(Jsoup.clean(cleanMe, plainTextWhitelist))
                .replaceAll(">\\n ", ">")
                .replaceAll("\\n<", "<")
                .replaceAll("(<br>*){2,}", "<br>");
    }

    @Override
    public String cleanText(String cleanMe, Whitelist whitelist) {
        cleanMe = StringUtils.trimToEmpty(cleanMe);
        cleanMe = removeIllegalTags(cleanMe);
        return StringEscapeUtils.unescapeXml(Jsoup.clean(cleanMe, whitelist));
    }

    private String removeIllegalTags(String cleanMe) {
        return cleanMe
                .replaceAll("<head>.+?</head>", "")
                .replaceAll("<script>.+?</script>", "")
                .replaceAll("<script.+?/>", "")
                .replaceAll("<embed>.+?</embed>", "")
                .replaceAll("<embed.+?/>", "")
                .replaceAll("<style>.+?</style>", "");
    }

    @Override
    public String cleanText(String cleanMe, Text.HtmlFilteringPolicy filteringPolicy) {
        switch (filteringPolicy) {
            case RESTRICTED:
                return cleanText(cleanMe, plainTextWhitelist);
            case RELAXED:
                return cleanText(cleanMe, basicHtmlTagsWhiteList);
            default:
                return cleanMe; // no changes
        }
    }
}
