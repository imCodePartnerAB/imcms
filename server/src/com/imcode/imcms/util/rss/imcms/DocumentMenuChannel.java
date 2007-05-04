package com.imcode.imcms.util.rss.imcms;

import com.imcode.imcms.util.rss.Channel;
import com.imcode.imcms.util.rss.Item;
import com.imcode.imcms.api.TextDocument;
import imcode.util.Utility;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class DocumentMenuChannel implements Channel {

    private final TextDocument document;
    private final String urlRoot;
    private int menuIndex;

    public DocumentMenuChannel(TextDocument document, String urlRoot, int menuIndex) {
        this.document = document;
        this.urlRoot = urlRoot;
        this.menuIndex = menuIndex;
    }

    public String getTitle() {
        return document.getHeadline();
    }

    public String getLink() {
        return urlRoot + Utility.getContextRelativePathToDocumentWithName(document.getName());
    }

    public String getDescription() {
        return document.getMenuText();
    }

    public Collection<Item> getItems() {
        TextDocument.MenuItem[] visibleMenuItems = document.getMenu(menuIndex).getVisibleMenuItems();
        List<Item> result = new ArrayList<Item>();
        for ( TextDocument.MenuItem menuItem : visibleMenuItems ) {
            result.add(new MenuItemItem(urlRoot, menuItem));
        }
        return result;
    }

}
