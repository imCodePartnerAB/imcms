package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.MenuElementHtmlWrapper;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class ImcmsMenuElementHtmlWrapper implements MenuElementHtmlWrapper {

    private static final String TAG_DATA_REGEX = "(^<.*?>)";
    private static final String CONTENT_TITLE_REGEX = TAG_DATA_REGEX + "(.*?)$";

    private final Pattern patternTagData = Pattern.compile(TAG_DATA_REGEX);
    private final Pattern patternGetTitle = Pattern.compile(CONTENT_TITLE_REGEX);

    @Override
    public String getTitleFromSingleTag(String elementContent) {
        String title = "";
        Matcher matcherTitle = patternGetTitle.matcher(elementContent);
        if (matcherTitle.find()) {
            title = matcherTitle.group(2);
        }
        return title;
    }

    @Override
    public String getTitleFromContent(String content) {
        return null;
    }

    @Override
    public String getTagDataElement(String elementContent) {
        String tagData = "";
        Matcher matcherTitle = patternTagData.matcher(elementContent);
        if (matcherTitle.find()) {
            tagData = matcherTitle.group();
        }
        return tagData;
    }

    @Override
    public String getWrappedContent(String content, List<String> wrappers, MenuItemDTO itemDTO) {
        final StringBuilder wrapContentBuilder = new StringBuilder();
        String wrappedElement = "";
        final String title = itemDTO.getTitle();
        final String tagData = getTagDataElement(content);//<li..>
        for (String wrap : wrappers) {
            if (!wrapContentBuilder.toString().isEmpty()) {
                wrappedElement = String.format("<%s>".concat(wrapContentBuilder.toString()).concat("</%s>"),
                        wrap.trim(), wrap.trim());
                wrapContentBuilder.replace(0, wrapContentBuilder.toString().length(), wrappedElement);
            } else {
                wrappedElement = wrapContentBuilder.append(String.format("<%s>".concat(title).concat("</%s>"),
                        wrap.trim(), wrap.trim())).toString();
            }
        }

        return wrappers.isEmpty()
                ? content.concat(title)
                : String.format("%s <%s href=\"/%d\"> %s </%s>",
                tagData, LINK_A_TAG, itemDTO.getDocumentId(), wrappedElement, LINK_A_TAG);
    }
}
