package com.imcode.imcms.domain.component;

import com.imcode.imcms.model.Text;
import org.jsoup.safety.Whitelist;

/**
 * Text content filter, based on tags whitelist and cleaning text feature.
 * Used to clean non-supported tags from e.g. imcms:text tag.
 * <p>
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.03.17.
 */
public interface TextContentFilter {

    String cleanText(String cleanMe);

    String cleanText(String cleanMe, Whitelist whitelist);

    String cleanText(String cleanMe, Text.HtmlFilteringPolicy filteringPolicy);

}
