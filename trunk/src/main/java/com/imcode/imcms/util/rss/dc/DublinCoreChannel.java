package com.imcode.imcms.util.rss.dc;

import com.imcode.imcms.util.rss.Channel;

public abstract class DublinCoreChannel extends DublinCoreItem implements Channel {

    protected DublinCoreChannel(DublinCoreTerms dublinCoreTerms) {
        super(dublinCoreTerms);
    }

}
