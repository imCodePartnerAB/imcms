package com.imcode.imcms.util.rss;

public interface Channel extends Item {

    Iterable<Item> getItems();

}