package imcode.server.document;

import com.imcode.imcms.api.util.InputStreamSource;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.FileInputStreamSource;
import imcode.util.FileUtility;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentStoringVisitor extends DocumentVisitor {

    protected IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
    protected UserDomainObject user;

    private static final int FILE_BUFFER_LENGTH = 2048;
    private static final int DB_FIELD_MAX_LENGTH__FILENAME = 255;

    public DocumentStoringVisitor(UserDomainObject user) {
        this.user = user;
    }

    protected void saveFileDocumentFile( int fileDocumentId, FileDocumentDomainObject.FileDocumentFile fileDocumentFile,
                                       String fileId ) {
        try {
            InputStreamSource inputStreamSource = fileDocumentFile.getInputStreamSource();
            InputStream in;
            try {
                in = inputStreamSource.getInputStream();
            } catch (FileNotFoundException e) {
                throw new UnhandledException("The file for filedocument " + fileDocumentId
                        + " has disappeared.", e);
            }
            if (null == in) {
                return;
            }

            File file = getFileForFileDocument( fileDocumentId, fileId );
            boolean sameFileOnDisk = inputStreamSource instanceof FileInputStreamSource
                                     && ( (FileInputStreamSource)inputStreamSource ).getFile().equals( file )
                                     && file.exists() ;
            if ( sameFileOnDisk ) {
                return;
            }
            byte[] buffer = new byte[FILE_BUFFER_LENGTH];
            final OutputStream out = new FileOutputStream(file);
            try {
                for (int bytesRead; -1 != (bytesRead = in.read(buffer));) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
                in.close();
            }
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    public static File getFileForFileDocument( int fileDocumentId, String fileId ) {
        File filePath = ApplicationServer.getIMCServiceInterface().getConfig().getFilePath();
        String filename = "" + fileDocumentId ;
        if (StringUtils.isNotBlank( fileId )) {
            filename += "."+FileUtility.escapeFilename(fileId) ;
        }
        File file = new File(filePath, filename);
        return file;
    }

    static String makeSqlInsertString(String tableName, String[] columnNames) {
        return "INSERT INTO " + tableName + " (" + StringUtils.join(columnNames, ",") + ")"
                + "VALUES(?" + StringUtils.repeat(",?", columnNames.length - 1) + ")";
    }

    void updateTextDocumentMenus(TextDocumentDomainObject textDocument) {
        Map menuMap = textDocument.getMenus();
        for (Iterator iterator = menuMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Integer menuIndex = (Integer) entry.getKey();
            MenuDomainObject menu = (MenuDomainObject) entry.getValue();
            updateTextDocumentMenu(textDocument, menuIndex, menu);
        }
        deleteUnusedMenus(textDocument);
    }

    private void deleteUnusedMenus(TextDocumentDomainObject textDocument) {
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
            service.sqlUpdateQuery(sqlDeleteUnusedMenuItems, new String[]{"" + textDocument.getId()});
            String sqlDeleteUnusedMenus = "DELETE FROM menus WHERE meta_id = ? AND " + whereClause;
            service.sqlUpdateQuery(sqlDeleteUnusedMenus, new String[]{"" + textDocument.getId()});
        }
    }

    private void updateTextDocumentMenu(TextDocumentDomainObject textDocument, Integer menuIndex,
                                        MenuDomainObject menu) {
        deleteTextDocumentMenu(textDocument, menuIndex);
        insertTextDocumentMenu(textDocument, menuIndex, menu);
    }

    private void insertTextDocumentMenu(TextDocumentDomainObject textDocument, Integer menuIndex,
                                        MenuDomainObject menu) {
        sqlInsertMenu(textDocument, menuIndex.intValue(), menu);
        insertTextDocumentMenuItems(menu);
    }

    private void deleteTextDocumentMenu(TextDocumentDomainObject textDocument, Integer menuIndex) {
        deleteTextDocumentMenuItems(textDocument, menuIndex);
        String sqlDeleteMenu = "DELETE FROM menus WHERE meta_id = ? AND menu_index = ?";
        service.sqlUpdateQuery(sqlDeleteMenu, new String[]{"" + textDocument.getId(), "" + menuIndex});
    }

    private void deleteTextDocumentMenuItems(TextDocumentDomainObject textDocument, Integer menuIndex) {
        String sqlDeleteMenuItems = "DELETE FROM childs WHERE menu_id IN (SELECT menu_id FROM menus WHERE meta_id = ? AND menu_index = ?)";
        service.sqlUpdateQuery(sqlDeleteMenuItems, new String[]{"" + textDocument.getId(), "" + menuIndex});
    }

    void updateTextDocumentTexts(TextDocumentDomainObject textDocument) {
        deleteTextDocumentTexts(textDocument);
        insertTextDocumentTexts(textDocument);
    }

    void updateTextDocumentImages(TextDocumentDomainObject textDocument) {
        deleteTextDocumentImages(textDocument);
        insertTextDocumentImages(textDocument);
    }

    void updateTextDocumentIncludes(TextDocumentDomainObject textDocument) {
        deleteTextDocumentIncludes(textDocument);
        insertTextDocumentIncludes(textDocument);
    }

    private void deleteTextDocumentIncludes(TextDocumentDomainObject textDocument) {
        String sqlDeleteDocumentIncludes = "DELETE FROM includes WHERE meta_id = ?";
        service.sqlUpdateQuery(sqlDeleteDocumentIncludes, new String[]{"" + textDocument.getId()});
    }

    private void insertTextDocumentImages(TextDocumentDomainObject textDocument) {
        Map images = textDocument.getImages();
        for (Iterator iterator = images.keySet().iterator(); iterator.hasNext();) {
            Integer imageIndex = (Integer) iterator.next();
            ImageDomainObject image = (ImageDomainObject) images.get(imageIndex);
            saveDocumentImage(textDocument.getId(), imageIndex.intValue(), image);
        }
    }

    private void deleteTextDocumentImages(TextDocumentDomainObject textDocument) {
        String sqlDeleteImages = "DELETE FROM images WHERE meta_id = ?";
        service.sqlUpdateQuery(sqlDeleteImages, new String[]{"" + textDocument.getId()});
    }

    private void insertTextDocumentTexts(TextDocumentDomainObject textDocument) {
        Map texts = textDocument.getTexts();
        for (Iterator iterator = texts.keySet().iterator(); iterator.hasNext();) {
            Integer textIndex = (Integer) iterator.next();
            TextDomainObject text = (TextDomainObject) texts.get(textIndex);
            sqlInsertText(textDocument, textIndex, text);
        }
    }

    private void sqlInsertText(TextDocumentDomainObject textDocument, Integer textIndex, TextDomainObject text) {
        service.sqlUpdateQuery("INSERT INTO texts (meta_id, name, text, type) VALUES(?,?,?,?)", new String[]{
            "" + textDocument.getId(), "" + textIndex, text.getText(), "" + text.getType()
        });
    }

    private void deleteTextDocumentTexts(TextDocumentDomainObject textDocument) {
        String sqlDeleteTexts = "DELETE FROM texts WHERE meta_id = ?";
        service.sqlUpdateQuery(sqlDeleteTexts, new String[]{"" + textDocument.getId()});
    }

    private void insertTextDocumentMenuItems(MenuDomainObject menu) {
        MenuItemDomainObject[] menuItems = menu.getMenuItems();
        for (int i = 0; i < menuItems.length; i++) {
            MenuItemDomainObject menuItem = menuItems[i];
            sqlInsertMenuItem(menu, menuItem);
        }
    }

    private void sqlInsertMenuItem(MenuDomainObject menu, MenuItemDomainObject menuItem) {
        String sqlInsertMenuItem = "INSERT INTO childs (menu_id, to_meta_id, manual_sort_order, tree_sort_index) VALUES(?,?,?,?)";
        service.sqlUpdateQuery(sqlInsertMenuItem, new String[]{
            "" + menu.getId(),
            "" + menuItem.getDocument().getId(),
            "" + menuItem.getSortKey().intValue(),
            "" + menuItem.getTreeSortKey()
        });
    }

    private void sqlInsertMenu(TextDocumentDomainObject textDocument, int menuIndex,
                               MenuDomainObject menu) {
        String sqlInsertMenu = "INSERT INTO menus (meta_id, menu_index, sort_order) VALUES(?,?,?) SELECT @@IDENTITY";
        String menuIdString = service.sqlQueryStr(sqlInsertMenu, new String[]{
            "" + textDocument.getId(), "" + menuIndex, "" + menu.getSortOrder()
        });
        int menuId = Integer.parseInt(menuIdString);
        menu.setId(menuId);
    }

    private void insertTextDocumentIncludes(TextDocumentDomainObject textDocument) {
        Map includes = textDocument.getIncludes();
        for (Iterator iterator = includes.keySet().iterator(); iterator.hasNext();) {
            Integer includeIndex = (Integer) iterator.next();
            Integer includedDocumentId = (Integer) includes.get(includeIndex);
            sqlInsertTextDocumentInclude(textDocument, includeIndex, includedDocumentId);
        }

    }

    private void sqlInsertTextDocumentInclude(TextDocumentDomainObject textDocument, Integer includeIndex,
                                              Integer includedDocumentId) {
        service.sqlUpdateQuery("INSERT INTO includes (meta_id, include_id, included_meta_id) VALUES(?,?,?)", new String[]{
            "" + textDocument.getId(), "" + includeIndex, "" + includedDocumentId
        });
    }

    public static void saveDocumentImage(int meta_id, int img_no, ImageDomainObject image) {
        String sqlStr = "update images\n"
                + "set imgurl  = ?, \n"
                + "width       = ?, \n"
                + "height      = ?, \n"
                + "border      = ?, \n"
                + "v_space     = ?, \n"
                + "h_space     = ?, \n"
                + "image_name  = ?, \n"
                + "target      = ?, \n"
                + "align       = ?, \n"
                + "alt_text    = ?, \n"
                + "low_scr     = ?, \n"
                + "linkurl     = ?, \n"
                + "type        = ?  \n"
                + "where meta_id = ? \n"
                + "and name = ? \n";

        int rowUpdateCount = sqlImageUpdateQuery(sqlStr, image, meta_id, img_no);
        if (0 == rowUpdateCount) {
            sqlStr = "insert into images (imgurl, width, height, border, v_space, h_space, image_name, target, align, alt_text, low_scr, linkurl, type, meta_id, name)"
                    + " values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)";

            sqlImageUpdateQuery(sqlStr, image, meta_id, img_no);
        }
    }

    private static int sqlImageUpdateQuery(String sqlStr, ImageDomainObject image, int meta_id, int img_no) {
        return ApplicationServer.getIMCServiceInterface().sqlUpdateQuery(sqlStr, new String[]{
            image.getUrl(),
            "" + image.getWidth(),
            "" + image.getHeight(),
            "" + image.getBorder(),
            "" + image.getVerticalSpace(),
            "" + image.getHorizontalSpace(),
            image.getName(),
            image.getTarget(),
            image.getAlign(),
            image.getAlternateText(),
            image.getLowResolutionUrl(),
            image.getLinkUrl(),
            "" + image.getType(),
            "" + meta_id,
            "" + img_no,
        });
    }

    public void visitFileDocument( FileDocumentDomainObject fileDocument ) {
        Map fileDocumentFiles = fileDocument.getFiles();

        String sqlDelete = "DELETE FROM fileupload_docs WHERE meta_id = ?";
        service.sqlUpdateQuery(sqlDelete, new String[]{"" + fileDocument.getId()});

        for ( Iterator iterator = fileDocumentFiles.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iterator.next();
            String fileId = (String)entry.getKey();
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = (FileDocumentDomainObject.FileDocumentFile)entry.getValue();

            String filename = fileDocumentFile.getFilename();
            if ( filename.length() > DB_FIELD_MAX_LENGTH__FILENAME ) {
                filename = truncateFilename( filename, DB_FIELD_MAX_LENGTH__FILENAME );
            }
            String sqlInsert = "INSERT INTO fileupload_docs (meta_id, variant_name, filename, mime, created_as_image, default_variant) VALUES(?,?,?,?,?,?)";
            boolean isDefaultFile = fileId.equals( fileDocument.getDefaultFileId());
            service.sqlUpdateQuery( sqlInsert, new String[]{""+ fileDocument.getId(), fileId, filename, fileDocumentFile.getMimeType(), fileDocumentFile.isCreatedAsImage() ? "1" : "0", isDefaultFile ? "1" : "0"} );
            saveFileDocumentFile( fileDocument.getId(), fileDocumentFile, fileId );
        }
        DocumentMapper.deleteOtherFileDocumentFiles( fileDocument ) ;
    }

    private String truncateFilename(String filename, int length) {
        String truncatedFilename = StringUtils.left(filename, length);
        String extensions = getExtensionsFromFilename(filename);
        if (extensions.length() > length) {
            return truncatedFilename;
        }
        String basename = StringUtils.chomp(filename, extensions);
        String truncatedBasename = StringUtils.substring(basename, 0, length - extensions.length());
        truncatedFilename = truncatedBasename + extensions;
        return truncatedFilename;
    }

    private String getExtensionsFromFilename(String filename) {
        String extensions = "";
        Matcher matcher = Pattern.compile("(?:\\.\\w+)+$").matcher(filename);
        if (matcher.find()) {
            extensions = matcher.group();
        }
        return extensions;
    }

}
