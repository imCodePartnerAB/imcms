package com.imcode.imcms.util.rss.dc;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.ArrayUtils;

import java.util.Map;

import com.imcode.imcms.util.rss.Item;
import com.imcode.imcms.util.rss.NameSpace;

public class DublinCoreItem implements Item {

    private DublinCoreTerms dublinCoreTerms;

    public DublinCoreItem(DublinCoreTerms dublinCoreTerms) {
        this.dublinCoreTerms = dublinCoreTerms;
    }

    public String getLink() {
        return dublinCoreTerms.getIdentifer();
    }

    public String getTitle() {
        return dublinCoreTerms.getTitle();
    }

    public String getDescription() {
        return dublinCoreTerms.getDescription();
    }

    public Map<NameSpace,Map<String,String>> getNameSpaceStrings() {
        return new DublinCoreTermsMapFactory().getNameSpaceStrings(dublinCoreTerms);
    }

    public Map<NameSpace, DynaBean> getNameSpaceBeans() {
        WrapDynaBean dublinCoreDynaBean = new WrapDynaBean(dublinCoreTerms);
        return ArrayUtils.toMap(new Object[][] {
                { DublinCoreTerms.DUBLIN_CORE_ELEMENTS_NAME_SPACE, dublinCoreDynaBean },
                { DublinCoreTerms.DUBLIN_CORE_TERMS_NAME_SPACE, dublinCoreDynaBean },
        });
    }
}
