package com.imcode.imcms.mapping;

import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.CopyableHashMap;
import imcode.server.document.textdocument.FileDocumentImageSource;
import imcode.server.document.textdocument.ImageArchiveImageSource;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.ImagesPathRelativePathImageSource;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.util.LazilyLoadedObject;
import imcode.util.Utility;
import imcode.util.image.Format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.db.Database;
import imcode.util.image.Resize;

public class TextDocumentInitializer {

    private final static Logger LOG = Logger.getLogger(TextDocumentInitializer.class);

    private final Collection documentIds;
    private final Database database;
    private final DocumentGetter documentGetter;
    private Map documentsMenuItems;
    private Map documentsImages;
    private Map documentsIncludes;
    private Map documentsTexts;
    private Map documentsTemplateIds;

    static final String SQL_GET_MENU_ITEMS = "SELECT meta_id, menus.menu_id, menu_index, sort_order, to_meta_id, manual_sort_order, tree_sort_index FROM menus,childs WHERE menus.menu_id = childs.menu_id AND meta_id ";

    public TextDocumentInitializer(Database database, DocumentGetter documentGetter, Collection documentIds) {
        this.database = database;
        this.documentGetter = documentGetter;
        this.documentIds = documentIds;
    }

    public void initialize(TextDocumentDomainObject document) {
        Integer documentId = new Integer(document.getId()) ;
        document.setLazilyLoadedMenus(new LazilyLoadedObject(new MenusLoader(documentId)));
        document.setLazilyLoadedTexts(new LazilyLoadedObject(new TextsLoader(documentId)));
        document.setLazilyLoadedImages(new LazilyLoadedObject(new ImagesLoader(documentId)));
        document.setLazilyLoadedIncludes(new LazilyLoadedObject(new IncludesLoader(documentId)));
        document.setLazilyLoadedTemplateIds(new LazilyLoadedObject(new TemplateIdsLoader(documentId)));
    }

    private class MenusLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        MenusLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsMenuItems();
            DocumentMenusMap menusMap = (DocumentMenusMap) documentsMenuItems.get(documentId);
            if ( null == menusMap ) {
                menusMap = new DocumentMenusMap();
            }
            return menusMap;
        }

        void initDocumentsMenuItems() {
            if ( null == documentsMenuItems ) {
                documentsMenuItems = new HashMap();
                final Set destinationDocumentIds = new HashSet();
                final BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
                DocumentInitializer.executeWithAppendedIntegerInClause(database, SQL_GET_MENU_ITEMS, documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            int documentId = rs.getInt(1);
                            int menuId = rs.getInt(2);
                            int menuIndex = rs.getInt(3);
                            int menuSortOrder = rs.getInt(4);
                            Integer destinationDocumentId = new Integer(rs.getInt(5));
                            Integer sortKey = Utility.getInteger(rs.getObject(6));

                            destinationDocumentIds.add(destinationDocumentId);
                            Map documentMenus = (Map) documentsMenuItems.get(new Integer(documentId));
                            if ( null == documentMenus ) {
                                documentMenus = new DocumentMenusMap();
                                documentsMenuItems.put(new Integer(documentId), documentMenus);
                            }

                            MenuDomainObject menu = (MenuDomainObject) documentMenus.get(new Integer(menuIndex));
                            if ( null == menu ) {
                                menu = new MenuDomainObject(menuId, menuSortOrder);
                                documentMenus.put(new Integer(menuIndex), menu);
                            }
                            MenuItemDomainObject menuItem = new MenuItemDomainObject(new GetterDocumentReference(destinationDocumentId.intValue(), batchDocumentGetter), sortKey, new TreeSortKeyDomainObject(rs.getString(7)));
                            menu.addMenuItemUnchecked(menuItem);
                        }
                        return null;
                    }
                });
            }
        }
    }

    private class IncludesLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        IncludesLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsIncludes();
            CopyableHashMap documentIncludesMap = (CopyableHashMap) documentsIncludes.get(documentId);
            if ( null == documentIncludesMap ) {
                documentIncludesMap = new CopyableHashMap();
            }
            return documentIncludesMap;
        }

        private void initDocumentsIncludes() {
            if ( null == documentsIncludes ) {
                documentsIncludes = new HashMap();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, include_id, included_meta_id FROM includes WHERE meta_id ", documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            Integer documentId = new Integer(rs.getInt(1));
                            Integer includeIndex = new Integer(rs.getInt(2));
                            Integer includedDocumentId = new Integer(rs.getInt(3));

                            CopyableHashMap documentIncludesMap = (CopyableHashMap) documentsIncludes.get(documentId);
                            if ( null == documentIncludesMap ) {
                                documentIncludesMap = new CopyableHashMap();
                                documentsIncludes.put(documentId, documentIncludesMap);
                            }
                            documentIncludesMap.put(includeIndex, includedDocumentId);
                        }
                        return null;
                    }
                });
            }
        }

    }

    private class ImagesLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        ImagesLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsImages();
            CopyableHashMap documentImagesMap = (CopyableHashMap) documentsImages.get(documentId);
            if ( null == documentImagesMap ) {
                documentImagesMap = new CopyableHashMap();
            }
            return documentImagesMap;
        }

        private void initDocumentsImages() {
            if ( null == documentsImages ) {
                documentsImages = new HashMap();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id,name,image_name,imgurl,"
                                                                                 + "width,height,border,v_space,h_space,"
                                                                                 + "target,align,alt_text,low_scr,linkurl,type,archive_image_id, "
																				 + "format, crop_x1, crop_y1, crop_x2, crop_y2, rotate_angle, gen_file,"
                                                                                 + "resize "
                                                                                 + "FROM images WHERE meta_id ", documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            Integer documentId = new Integer(rs.getInt(1));
                            Map imageMap = (Map) documentsImages.get(documentId);
                            if ( null == imageMap ) {
                                imageMap = new CopyableHashMap();
                                documentsImages.put(documentId, imageMap);
                            }
                            Integer imageIndex = new Integer(rs.getInt(2));
                            ImageDomainObject image = new ImageDomainObject();

                            image.setImageIndex(imageIndex);
                            image.setName(rs.getString(3));
                            String imageSource = rs.getString(4);
                            image.setWidth(rs.getInt(5));
                            image.setHeight(rs.getInt(6));
                            image.setBorder(rs.getInt(7));
                            image.setVerticalSpace(rs.getInt(8));
                            image.setHorizontalSpace(rs.getInt(9));
                            image.setTarget(rs.getString(10));
                            image.setAlign(rs.getString(11));
                            image.setAlternateText(rs.getString(12));
                            image.setLowResolutionUrl(rs.getString(13));
                            image.setLinkUrl(rs.getString(14));
                            int imageType = rs.getInt(15);
                            image.setArchiveImageId((Long) rs.getObject(16));
                            image.setFormat(Format.findFormat(rs.getShort(17)));
                            
                            CropRegion region = new CropRegion(rs.getInt(18), rs.getInt(19), rs.getInt(20), rs.getInt(21));
                            image.setCropRegion(region);
                            
                            image.setRotateDirection(RotateDirection.getByAngleDefaultIfNull(rs.getShort(22)));
                            image.setGeneratedFilename(rs.getString(23));
                            image.setResize(Resize.getByOrdinal(rs.getInt(24)));

                            if ( StringUtils.isNotBlank(imageSource) ) {
                                if ( ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT == imageType ) {
                                    try {
                                        int fileDocumentId = Integer.parseInt(imageSource);
                                        DocumentDomainObject document = documentGetter.getDocument(new Integer(fileDocumentId));
                                        if ( null != document ) {
                                            image.setSource(new FileDocumentImageSource(new DirectDocumentReference(document)));
                                        }
                                    } catch ( NumberFormatException nfe ) {
                                        LOG.warn("Non-numeric document-id \"" + imageSource + "\" for image in database.");
                                    } catch ( ClassCastException cce ) {
                                        LOG.warn("Non-file-document-id \"" + imageSource + "\" for image in database.");
                                    }
                                } else if ( ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH == imageType ) {
                                    image.setSource(new ImagesPathRelativePathImageSource(imageSource));
                                } else if ( ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE == imageType) {
                                	image.setSource(new ImageArchiveImageSource(imageSource));
                                }
                            }
                            imageMap.put(imageIndex, image);
                        }
                        return null;
                    }
                });
            }

        }
    }

    private class TextsLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        TextsLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsTexts();
            CopyableHashMap documentTexts = (CopyableHashMap) documentsTexts.get(documentId);
            if ( null == documentTexts ) {
                documentTexts = new CopyableHashMap();
            }
            return documentTexts;
        }

        private void initDocumentsTexts() {
            if ( null == documentsTexts ) {
                documentsTexts = new HashMap();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, name, text, type FROM texts WHERE meta_id ", documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            Integer documentId = new Integer(rs.getInt(1));
                            Integer textIndex = new Integer(rs.getInt(2));
                            String text = rs.getString(3);
                            int textType = rs.getInt(4);
                            CopyableHashMap documentTextsMap = (CopyableHashMap) documentsTexts.get(documentId);
                            if ( null == documentTextsMap ) {
                                documentTextsMap = new CopyableHashMap();
                                documentsTexts.put(documentId, documentTextsMap);
                            }
                            documentTextsMap.put(textIndex, new TextDomainObject(text, textType));
                        }
                        return null;
                    }
                });
            }
        }

    }

    private class TemplateIdsLoader implements LazilyLoadedObject.Loader {

        private final Integer documentId;

        TemplateIdsLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public LazilyLoadedObject.Copyable load() {
            initDocumentsTemplateIds();
            TextDocumentDomainObject.TemplateNames templateNames = (TextDocumentDomainObject.TemplateNames) documentsTemplateIds.get(documentId) ;
            if (null == templateNames ) {
                templateNames = new TextDocumentDomainObject.TemplateNames();
            }
            return templateNames ;
        }

        private void initDocumentsTemplateIds() {
            if ( null == documentsTemplateIds ) {
                documentsTemplateIds = new HashMap();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, template_name, group_id, default_template, default_template_1, default_template_2 FROM text_docs WHERE meta_id ", documentIds, new ResultSetHandler() {
                    public Object handle(ResultSet rs) throws SQLException {
                        while ( rs.next() ) {
                            Integer documentId = new Integer(rs.getInt("meta_id"));
                            TextDocumentDomainObject.TemplateNames templateNames = new TextDocumentDomainObject.TemplateNames();
                            templateNames.setTemplateName(rs.getString("template_name"));
                            templateNames.setTemplateGroupId(rs.getInt(3));
                            templateNames.setDefaultTemplateName(rs.getString("default_template"));
                            String defaultTemplateIdForR1 = rs.getString("default_template_1");
                            String defaultTemplateIdForR2 = rs.getString("default_template_2");
                            templateNames.setDefaultTemplateNameForRestricted1(defaultTemplateIdForR1);
                            templateNames.setDefaultTemplateNameForRestricted2(defaultTemplateIdForR2);
                            documentsTemplateIds.put(documentId, templateNames);
                        }
                        return null;
                    }
                });
            }
        }

    }

}
