package com.imcode.imcms.util.rss.dc;

import com.imcode.imcms.util.rss.NameSpace;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.text.Format;
import java.util.Date;
import java.util.Map;

class DublinCoreTermsMapFactory {

    public Map<NameSpace, Map<String, String>> getNameSpaceStrings(DublinCoreTerms dublinCoreTerms) {
        Format iso8601Format = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT;
        return ArrayUtils.toMap(new Object[][] {
                { DublinCoreTerms.DUBLIN_CORE_ELEMENTS_NAME_SPACE, ArrayUtils.toMap(new Object[][] {
                        {"creator", null != dublinCoreTerms.getCreator() ? dublinCoreTerms.getCreator().getName() : null},
                        {"description",dublinCoreTerms.getDescription()},
                        {"identifier",dublinCoreTerms.getIdentifer()},
                        {"title",dublinCoreTerms.getTitle()},
                })},
                { DublinCoreTerms.DUBLIN_CORE_TERMS_NAME_SPACE, ArrayUtils.toMap(new Object[][] {
                        {"created", format(iso8601Format, dublinCoreTerms.getCreated()) },
                        {"issued",format(iso8601Format, dublinCoreTerms.getIssued()) },
                        {"modified",format(iso8601Format, dublinCoreTerms.getModified()) },
                })},
        });
    }

    private String format(Format dateFormat, Date datetime) {
        return null != datetime ? dateFormat.format(datetime) : null;
    }
}
