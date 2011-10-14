package com.imcode.imcms.addon.treemenu;

import com.imcode.imcms.api.TextDocument;

/**
 * Created by IntelliJ IDEA.
 * User: tomull
 * imCode Partner AB
 * Date: 2008-sep-25
 * Time: 15:04:38
 */
public class TreeMenuItem {
	TextDocument.MenuItem menuItem ;
	boolean isThisDoc = false ;
	boolean hasSubLevels = false ;
	boolean hasVisibleSubLevels = false ;
	public TreeMenuItem (TextDocument.MenuItem menuItem, boolean isThisDoc, boolean hasSubLevels, boolean hasVisibleSubLevels) {
		this.menuItem = menuItem;
		this.isThisDoc = isThisDoc;
		this.hasSubLevels = hasSubLevels;
		this.hasVisibleSubLevels = hasVisibleSubLevels;
	}
	public TextDocument.MenuItem getMenuItem () {
		return menuItem;
	}
	public boolean isThisDoc () {
		return isThisDoc;
	}
	public boolean hasSubLevels () {
		return hasSubLevels;
	}
	public boolean hasVisibleSubLevels () {
		return hasVisibleSubLevels;
	}
}
