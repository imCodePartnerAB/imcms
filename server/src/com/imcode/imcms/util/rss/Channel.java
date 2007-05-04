package com.imcode.imcms.util.rss;

import java.util.Collection;

public interface Channel {

    String getTitle();

    String getLink();

    String getDescription();

    Collection<Item> getItems();
}