package com.imcode.imcms.addon.treemenu;

import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.api.Document;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: tomull
 * imCode Partner AB
 * Date: 2008-sep-25
 * Time: 15:04:49
 */
public class ImcmsTreeMenu {
	TextDocument thisDoc ;
	TextDocument.MenuItem[] allMenuItems ;
	List<TreeMenuItem> visibleTreeMenuItems ;
	List<BreadCrumbItem> breadCrumbItems ;
	String activeTreeKeyString = "" ;

	public ImcmsTreeMenu (TextDocument thisDoc, TextDocument.MenuItem[] allMenuItems) {
		this.thisDoc = thisDoc;
		this.allMenuItems = allMenuItems;
		visibleTreeMenuItems = new ArrayList<TreeMenuItem>() ;
		breadCrumbItems = new ArrayList<BreadCrumbItem>() ;
		if (null != allMenuItems && allMenuItems.length > 0) {
			for (int i = 0; i < allMenuItems.length; i++) {
				Document menuDocument = allMenuItems[i].getDocument() ;
				TextDocument.MenuItem.TreeKey treeKey = allMenuItems[i].getTreeKey() ;
				if (menuDocument.getId() == thisDoc.getId()) {
					if (null == treeKey || "".equals(treeKey.toString())) {
						continue ;
					}
					String thisTreeKey = treeKey.toString() ;
					int thisLevel  = treeKey.getLevelCount() ;
					int nextLevel  = getNextLevel(allMenuItems, i+1) ;
					boolean hasSub = nextLevel > thisLevel ;
					activeTreeKeyString = (thisTreeKey.matches("^\\d+(\\.\\d+)*$")) ? thisTreeKey : "" ;
					if (!hasSub) {
						if (thisLevel == 4) {
							activeTreeKeyString = treeKey.getLevelKey(1) + "." + treeKey.getLevelKey(2) + "." + treeKey.getLevelKey(3) ;
						} else if (thisLevel == 3) {
							activeTreeKeyString = treeKey.getLevelKey(1) + "." + treeKey.getLevelKey(2) ;
						} else if (thisLevel == 2) {
							activeTreeKeyString = treeKey.getLevelKey(1) + "" ;
						} else if (thisLevel == 1) {
							activeTreeKeyString = "" ;
						}
					}
					break ;
				}
			}
			int actLev  = getLevel(activeTreeKeyString) ;
			for (int i = 0; i < allMenuItems.length; i++) {
				TextDocument.MenuItem.TreeKey treeKey = allMenuItems[i].getTreeKey() ;
				if (null == treeKey || "".equals(treeKey.toString())) {
					continue ;
				}
				int thisLevel  = treeKey.getLevelCount() ;
				int nextLevel  = getNextLevel(allMenuItems, i+1) ;
				boolean hasSubLevels = nextLevel > thisLevel ;
				if (menuItemIsVisible(treeKey.toString())) {
					Document menuItemDoc = allMenuItems[i].getDocument() ;
					boolean isThisDoc = menuItemDoc.getId() == thisDoc.getId() ;
					boolean hasVisibleSubLevels = ((treeKey + ".").startsWith(activeTreeKeyString.replaceFirst("\\.\\d+$", "") + ".") && thisLevel <= actLev) ;
					visibleTreeMenuItems.add( new TreeMenuItem( allMenuItems[i], isThisDoc, hasSubLevels, hasVisibleSubLevels ) ) ;
					
					// BreadCrumbs
					
					int actLevOne = (null != activeTreeKeyString && activeTreeKeyString.matches(".*\\d+.*")) ?
				                     Integer.parseInt(activeTreeKeyString.split("\\.")[0]) :
				                     0 ;
					int iLevOneSort = treeKey.getLevelKey(1) ;
					String navLink ;
					if (!isThisDoc && thisLevel == 1 && iLevOneSort == actLevOne) {
						// Add 1:st level to navLinks
						BreadCrumbItem breadCrumbItem = new BreadCrumbItem(menuItemDoc.getHeadline(), menuItemDoc.getName(), false) ;
						breadCrumbItems.add(breadCrumbItem) ;
					} else if (!isThisDoc && thisLevel == 2 && hasVisibleSubLevels) {
						// Add 2:nd level to navLinks
						BreadCrumbItem breadCrumbItem = new BreadCrumbItem(menuItemDoc.getHeadline(), menuItemDoc.getName(), false) ;
						breadCrumbItems.add(breadCrumbItem) ;
					} else if (!isThisDoc && thisLevel == 3 && hasVisibleSubLevels) {
						// Add 3:rd level to navLinks
						BreadCrumbItem breadCrumbItem = new BreadCrumbItem(menuItemDoc.getHeadline(), menuItemDoc.getName(), false) ;
						breadCrumbItems.add(breadCrumbItem) ;
					}
					
					
				}
			}
			
			// BreadCrumbs - Add THIS doc
			
			BreadCrumbItem breadCrumbItem = new BreadCrumbItem(thisDoc.getHeadline(), thisDoc.getName(), true) ;
			breadCrumbItems.add(breadCrumbItem) ;
			
		}
	}

	public List<TreeMenuItem> getVisibleTreeMenuItems () {
		return visibleTreeMenuItems;
	}

	public String getActiveTreeKeyString () {
		return activeTreeKeyString;
	}

	public List<BreadCrumbItem> getBreadCrumbItems () {
		return breadCrumbItems;
	}

	private int getLevel( String sTreeSort ) {
		int ret = 0 ;
		char actChar ;
		if (sTreeSort != null && !sTreeSort.equals("")) {
			ret = 1 ;
			for (int i = 0; i < sTreeSort.length(); i++) {
				actChar = sTreeSort.charAt(i) ;
				if (actChar == '.') ret++ ;
			}
		}
		return ret ;
	}

	private int getNextLevel( TextDocument.MenuItem[] menu, int nextIdx ) {
		int ret = 0 ;
		try {
			ret = menu[nextIdx].getTreeKey().getLevelCount() ;
		} catch (Exception ex) {}
		return ret ;
	}

	private boolean menuItemIsVisible( String treeKey ) {
		int actLev  = getLevel(activeTreeKeyString) ;
		int thisLev = getLevel(treeKey) ;
		if (thisLev == 1) {
			return true ;
		} else if ((treeKey.replaceFirst("\\.\\d+$", "") + ".").startsWith(activeTreeKeyString + ".") && (thisLev - 1) <= actLev) {
			return true ;
		} else if ((treeKey + ".").startsWith(activeTreeKeyString.replaceFirst("\\.\\d+$", "") + ".") && thisLev <= actLev) {
			return true ;
		} else if (thisLev == 2 && activeTreeKeyString.startsWith(treeKey.split("\\.")[0] + ".")) {
			return true ;
		}
		return false ;
	}
}
