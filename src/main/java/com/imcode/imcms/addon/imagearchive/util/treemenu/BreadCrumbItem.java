package com.imcode.imcms.addon.imagearchive.util.treemenu;

/**
 * Created by IntelliJ IDEA.
 * User: tomull
 * imCode Partner AB
 * Date: 2008-sep-25
 * Time: 15:24:55
 */
public class BreadCrumbItem {
	String headline ;
	String alias ;
	boolean isActivePage ;
	public BreadCrumbItem (String headline, String alias, boolean activePage) {
		this.headline = headline;
		this.alias = alias;
		isActivePage = activePage;
	}
	public String getHeadline () {
		return headline;
	}
	public String getAlias () {
		return alias;
	}
	public boolean isActivePage () {
		return isActivePage;
	}
}
