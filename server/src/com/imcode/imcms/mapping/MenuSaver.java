package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlUpdateDatabaseCommand;
import com.imcode.db.commands.InsertIntoTableDatabaseCommand;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

public class MenuSaver {

    private Database database;

    public MenuSaver(Database database) {
        this.database = database ;
    }

    void updateTextDocumentMenus(TextDocumentDomainObject textDocument, ImcmsServices services, TextDocumentDomainObject oldTextDocument, UserDomainObject savingUser) {
        Map menuMap = textDocument.getMenus();
        for ( Iterator iterator = menuMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer menuIndex = (Integer) entry.getKey();
            MenuDomainObject menu = (MenuDomainObject) entry.getValue();
            if(oldTextDocument != null) {
                MenuDomainObject oldMenu = oldTextDocument.getMenu(menuIndex.intValue());
                if(oldMenu != null && oldMenu.getMenuItemsUnsorted().size()>0 && !oldMenu.equals(menu)) {
                   updateTextDocumentMenuHistory(oldTextDocument, menuIndex, oldMenu, savingUser, services );
                }
            }
            updateTextDocumentMenu(textDocument, menuIndex, menu, services);
        }
        deleteUnusedMenus(textDocument);
    }

    private void updateTextDocumentMenuHistory(TextDocumentDomainObject oldTextDocument, Integer menuIndex, MenuDomainObject oldMenu, UserDomainObject savingUser, ImcmsServices services) {
        insertTextDocumentMenuHistory(oldTextDocument, menuIndex, oldMenu, savingUser, services);
    }

    private void insertTextDocumentMenuHistory(TextDocumentDomainObject oldTextDocument, Integer menuIndex, MenuDomainObject oldMenu, UserDomainObject savingUser, ImcmsServices services) {
        SimpleDateFormat dateFormat = new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING);
        database.execute(new InsertIntoTableDatabaseCommand("menus_history", new Object[][] {
                { "menu_id", new Integer(oldMenu.getId())},
                { "meta_id", new Integer(oldTextDocument.getId())},
                { "menu_index", new Integer(menuIndex) },
                { "sort_order", new Integer(oldMenu.getSortOrder()) },
                { "modified_datetime", dateFormat.format(new Date()) },
                { "user_id", new Integer(savingUser.getId()) }
        }));

        Collection menuItems = oldMenu.getMenuItemsUnsorted();
        for ( Iterator iterator = menuItems.iterator(); iterator.hasNext(); ) {
            MenuItemDomainObject menuItem = (MenuItemDomainObject) iterator.next();
            sqlInsertMenuItemHistory(oldMenu, menuItem);
        }
    }

    private void sqlInsertMenuItemHistory(MenuDomainObject oldMenu, MenuItemDomainObject menuItem) {

        database.execute(new InsertIntoTableDatabaseCommand("childs_history", new Object[][] {
                { "menu_id", new Integer(oldMenu.getId())},
                { "to_meta_id", new Integer(menuItem.getDocumentReference().getDocumentId())},
                { "manual_sort_order", new Integer(menuItem.getSortKey()) },
                { "tree_sort_index", menuItem.getTreeSortKey().toString() }
        }));
    }

    private void deleteUnusedMenus( TextDocumentDomainObject textDocument ) {
        Collection menus = textDocument.getMenus().values();
        if (!menus.isEmpty()) {
            Collection menuIds = CollectionUtils.collect(menus, new Transformer() {
                public Object transform(Object input) {
                    return new Integer(((MenuDomainObject) input).getId());
                }
            });
            String sqlInMenuIds = StringUtils.join(menuIds.iterator(), ",");

            String whereClause = "menu_id NOT IN (" + sqlInMenuIds + ")";
            String sqlDeleteUnusedMenuItems = "DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ?) AND "
                                              + whereClause;
            database.execute( new SqlUpdateDatabaseCommand(sqlDeleteUnusedMenuItems, new String[]{"" + textDocument.getId()} ));
            String sqlDeleteUnusedMenus = "DELETE FROM menus WHERE meta_id = ? AND " + whereClause;
            database.execute( new SqlUpdateDatabaseCommand(sqlDeleteUnusedMenus, new String[]{"" + textDocument.getId()}));
        }
    }

    private void updateTextDocumentMenu(TextDocumentDomainObject textDocument, Integer menuIndex,
                                        MenuDomainObject menu, ImcmsServices services) {
        deleteTextDocumentMenu(textDocument, menuIndex);
        insertTextDocumentMenu(textDocument, menuIndex, menu, services);
    }

    private void insertTextDocumentMenu(TextDocumentDomainObject textDocument, Integer menuIndex,
                                        MenuDomainObject menu, ImcmsServices services) {
        sqlInsertMenu(textDocument, menuIndex.intValue(), menu);
        insertTextDocumentMenuItems(menu, services);
    }

    private void deleteTextDocumentMenu(TextDocumentDomainObject textDocument,
                                        Integer menuIndex) {
        deleteTextDocumentMenuItems(textDocument, menuIndex);
        String sqlDeleteMenu = "DELETE FROM menus WHERE meta_id = ? AND menu_index = ?";
        database.execute(new SqlUpdateDatabaseCommand(sqlDeleteMenu, new String[]{"" + textDocument.getId(), "" + menuIndex} ));
    }

    private void deleteTextDocumentMenuItems(TextDocumentDomainObject textDocument,
                                             Integer menuIndex) {
        String sqlDeleteMenuItems = "DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ? AND menu_index = ?)";
        database.execute( new SqlUpdateDatabaseCommand(sqlDeleteMenuItems, new String[]{"" + textDocument.getId(),
                "" + menuIndex}) );
    }

    private void insertTextDocumentMenuItems(MenuDomainObject menu, ImcmsServices services) {
        Collection menuItems = menu.getMenuItemsUnsorted();
        for ( Iterator iterator = menuItems.iterator(); iterator.hasNext(); ) {
            MenuItemDomainObject menuItem = (MenuItemDomainObject) iterator.next();
            sqlInsertMenuItem(menu, menuItem);
        }
    }

    private void sqlInsertMenuItem( MenuDomainObject menu, MenuItemDomainObject menuItem ) {
        String sqlInsertMenuItem = "INSERT INTO childs (menu_id, to_meta_id, manual_sort_order, tree_sort_index) VALUES(?,?,?,?)";
        String[] parameters = new String[]{
                "" + menu.getId(),
                "" + menuItem.getDocumentReference().getDocumentId(),
                "" + menuItem.getSortKey().intValue(),
                "" + menuItem.getTreeSortKey()
        };
        database.execute( new SqlUpdateDatabaseCommand(sqlInsertMenuItem, parameters));
    }

    private void sqlInsertMenu(TextDocumentDomainObject textDocument, int menuIndex,
                               MenuDomainObject menu) {
        Number menuId = (Number) database.execute(new InsertIntoTableDatabaseCommand("menus", new Object[][] {
                { "meta_id", new Integer(textDocument.getId())},
                { "menu_index", new Integer(menuIndex) },
                { "sort_order", new Integer(menu.getSortOrder()) }
        }));
        menu.setId( menuId.intValue() );
    }


}
