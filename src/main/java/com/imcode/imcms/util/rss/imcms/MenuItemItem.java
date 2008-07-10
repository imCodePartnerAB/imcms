package com.imcode.imcms.util.rss.imcms;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.util.rss.NameSpace;
import com.imcode.imcms.util.rss.SimpleNameSpace;
import com.imcode.imcms.util.rss.dc.DublinCoreItem;

public class MenuItemItem extends DublinCoreItem {

    private TextDocument.MenuItem menuItem;
    private static final String IMCMS_MENU_NAMESPACE_URI = "imcms:menu";
    private static final NameSpace IMCMS_MENU_NAME_SPACE = new SimpleNameSpace("imcms", IMCMS_MENU_NAMESPACE_URI);

    public MenuItemItem(String urlRoot, TextDocument.MenuItem menuItem) {
        super(new DocumentDublinCoreTerms(urlRoot, menuItem.getDocument()));
        this.menuItem = menuItem;
    }

    public Map<NameSpace, Map<String, String>> getNameSpaceStrings() {
        Map<NameSpace, Map<String, String>> nameSpaces = super.getNameSpaceStrings();
        nameSpaces.put(IMCMS_MENU_NAME_SPACE, ArrayUtils.toMap(new Object[][] {
                { "target", menuItem.getDocument().getTarget() },
                { "treeKey", menuItem.getTreeKey().toString() }
        }));
        return nameSpaces;
    }
}
