package com.imcode.imcms.util.rss;

import java.util.Collection;

public interface Channel extends Item {

    Collection<Item> getItems();

}