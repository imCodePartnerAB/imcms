package com.imcode.imcms.util.rss.imcms;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.util.rss.Item;
import com.imcode.imcms.util.rss.dc.DublinCoreChannel;

import java.util.ArrayList;
import java.util.List;

public class DocumentMenuChannel extends DublinCoreChannel {

    private final TextDocument document;
    private final String urlRoot;
    private int menuIndex;

    public DocumentMenuChannel(TextDocument document, String urlRoot, int menuIndex) {
        super(new DocumentDublinCoreTerms(urlRoot, document));
        this.document = document;
        this.urlRoot = urlRoot;
        this.menuIndex = menuIndex;
    }

    public Iterable<Item> getItems() {
        TextDocument.MenuItem[] visibleMenuItems = document.getMenu(menuIndex).getVisibleMenuItems();
        List<Item> result = new ArrayList<Item>();
        for ( TextDocument.MenuItem menuItem : visibleMenuItems ) {
            result.add(new MenuItemItem(urlRoot, menuItem));
        }
        return result;
    }

}
