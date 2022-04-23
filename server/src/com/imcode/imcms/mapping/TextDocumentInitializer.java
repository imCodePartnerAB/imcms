package com.imcode.imcms.mapping;

import com.imcode.db.Database;
import imcode.server.document.DirectDocumentReference;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.GetterDocumentReference;
import imcode.server.document.textdocument.*;
import imcode.server.document.textdocument.ImageDomainObject.CropRegion;
import imcode.server.document.textdocument.ImageDomainObject.RotateDirection;
import imcode.server.document.textdocument.TextDocumentDomainObject.TemplateNames;
import imcode.util.LazilyLoadedObject;
import imcode.util.Utility;
import imcode.util.image.Format;
import imcode.util.image.Resize;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class TextDocumentInitializer {

    static final String SQL_GET_MENU_ITEMS = "SELECT meta_id, menus.menu_id, menu_index, sort_order, to_meta_id, manual_sort_order, tree_sort_index FROM menus,childs WHERE menus.menu_id = childs.menu_id AND meta_id ";
    private final static Logger LOG = LogManager.getLogger(TextDocumentInitializer.class);
    private final Collection<Integer> documentIds;
    private final Database database;
    private final DocumentGetter documentGetter;
    private Map<Integer, DocumentMenusMap> documentsMenuItems;
    private Map<Integer, CopyableHashMap<Integer, ImageDomainObject>> documentsImages;
    private Map<Integer, CopyableHashMap<Integer, Integer>> documentsIncludes;
    private Map<Integer, CopyableHashMap<Integer, TextDomainObject>> documentsTexts;
    private Map<Integer, TemplateNames> documentsTemplateIds;

    public TextDocumentInitializer(Database database, DocumentGetter documentGetter, Collection<Integer> documentIds) {
        this.database = database;
        this.documentGetter = documentGetter;
        this.documentIds = documentIds;
    }

    public void initialize(TextDocumentDomainObject document) {
        Integer documentId = document.getId();
        document.setLazilyLoadedMenus(new LazilyLoadedObject<>(new MenusLoader(documentId)));
        document.setLazilyLoadedTexts(new LazilyLoadedObject<>(new TextsLoader(documentId)));
        document.setLazilyLoadedImages(new LazilyLoadedObject<>(new ImagesLoader(documentId)));
        document.setLazilyLoadedIncludes(new LazilyLoadedObject<>(new IncludesLoader(documentId)));
        document.setLazilyLoadedTemplateIds(new LazilyLoadedObject<>(new TemplateIdsLoader(documentId)));
    }

    private class MenusLoader implements LazilyLoadedObject.Loader<DocumentMenusMap> {

        private final Integer documentId;

        MenusLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public DocumentMenusMap load() {
            initDocumentsMenuItems();
            DocumentMenusMap menusMap = documentsMenuItems.get(documentId);
            if (null == menusMap) {
                menusMap = new DocumentMenusMap();
            }
            return menusMap;
        }

        void initDocumentsMenuItems() {
            if (null == documentsMenuItems) {
                documentsMenuItems = new HashMap<>();
                final Set<Integer> destinationDocumentIds = new HashSet<>();
                final BatchDocumentGetter batchDocumentGetter = new BatchDocumentGetter(destinationDocumentIds, documentGetter);
                DocumentInitializer.executeWithAppendedIntegerInClause(database, SQL_GET_MENU_ITEMS, documentIds, rs -> {
                    while (rs.next()) {
                        int documentId = rs.getInt(1);
                        int menuId = rs.getInt(2);
                        int menuIndex = rs.getInt(3);
                        int menuSortOrder = rs.getInt(4);
                        Integer destinationDocumentId = rs.getInt(5);
                        Integer sortKey = Utility.getInteger(rs.getObject(6));

                        destinationDocumentIds.add(destinationDocumentId);
                        DocumentMenusMap documentMenus = documentsMenuItems.get(documentId);
                        if (null == documentMenus) {
                            documentMenus = new DocumentMenusMap();
                            documentsMenuItems.put(documentId, documentMenus);
                        }

                        MenuDomainObject menu = documentMenus.get(menuIndex);
                        if (null == menu) {
                            menu = new MenuDomainObject(menuId, menuSortOrder);
                            documentMenus.put(menuIndex, menu);
                        }
                        MenuItemDomainObject menuItem = new MenuItemDomainObject(new GetterDocumentReference(destinationDocumentId, batchDocumentGetter), sortKey, new TreeSortKeyDomainObject(rs.getString(7)));
                        menu.addMenuItemUnchecked(menuItem);
                    }
                    return null;
                });
            }
        }
    }

    private class IncludesLoader implements LazilyLoadedObject.Loader<CopyableHashMap<Integer, Integer>> {

        private final Integer documentId;

        IncludesLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public CopyableHashMap<Integer, Integer> load() {
            initDocumentsIncludes();
            CopyableHashMap<Integer, Integer> documentIncludesMap = documentsIncludes.get(documentId);
            if (null == documentIncludesMap) {
                documentIncludesMap = new CopyableHashMap<>();
            }
            return documentIncludesMap;
        }

        private void initDocumentsIncludes() {
            if (null == documentsIncludes) {
                documentsIncludes = new HashMap<>();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, include_id, included_meta_id FROM includes WHERE meta_id ", documentIds, rs -> {
                    while (rs.next()) {
                        Integer documentId = rs.getInt(1);
                        Integer includeIndex = rs.getInt(2);
                        Integer includedDocumentId = rs.getInt(3);

                        CopyableHashMap<Integer, Integer> documentIncludesMap = documentsIncludes.get(documentId);
                        if (null == documentIncludesMap) {
                            documentIncludesMap = new CopyableHashMap<>();
                            documentsIncludes.put(documentId, documentIncludesMap);
                        }
                        documentIncludesMap.put(includeIndex, includedDocumentId);
                    }
                    return null;
                });
            }
        }
    }

    private class ImagesLoader implements LazilyLoadedObject.Loader<CopyableHashMap<Integer, ImageDomainObject>> {

        private final Integer documentId;

        ImagesLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public CopyableHashMap<Integer, ImageDomainObject> load() {
            initDocumentsImages();
            CopyableHashMap<Integer, ImageDomainObject> documentImagesMap = documentsImages.get(documentId);
            if (null == documentImagesMap) {
                documentImagesMap = new CopyableHashMap<>();
            }
            return documentImagesMap;
        }

        private void initDocumentsImages() {
            if (null == documentsImages) {
                documentsImages = new HashMap<>();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id,name,image_name,imgurl,"
                        + "width,height,border,v_space,h_space,"
                        + "target,align,alt_text,low_scr,linkurl,type,archive_image_id, "
                        + "format, crop_x1, crop_y1, crop_x2, crop_y2, rotate_angle, gen_file,"
                        + "resize "
                        + "FROM images WHERE meta_id ", documentIds, rs -> {
                    while (rs.next()) {
                        Integer documentId = rs.getInt(1);
                        CopyableHashMap<Integer, ImageDomainObject> imageMap = documentsImages.get(documentId);
                        if (null == imageMap) {
                            imageMap = new CopyableHashMap<>();
                            documentsImages.put(documentId, imageMap);
                        }
                        Integer imageIndex = rs.getInt(2);
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

                        if (StringUtils.isNotBlank(imageSource)) {
                            if (ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT == imageType) {
                                try {
                                    int fileDocumentId = Integer.parseInt(imageSource);
                                    DocumentDomainObject document = documentGetter.getDocument(fileDocumentId);
                                    if (null != document) {
                                        image.setSource(new FileDocumentImageSource(new DirectDocumentReference(document)));
                                    }
                                } catch (NumberFormatException nfe) {
                                    LOG.warn("Non-numeric document-id \"" + imageSource + "\" for image in database.");
                                } catch (ClassCastException cce) {
                                    LOG.warn("Non-file-document-id \"" + imageSource + "\" for image in database.");
                                }
                            } else if (ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH == imageType) {
                                image.setSource(new ImagesPathRelativePathImageSource(imageSource));
                            } else if (ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE == imageType) {
                                image.setSource(new ImageArchiveImageSource(imageSource));
                            }
                        }
                        imageMap.put(imageIndex, image);
                    }
                    return null;
                });
            }

        }
    }

    private class TextsLoader implements LazilyLoadedObject.Loader<CopyableHashMap<Integer, TextDomainObject>> {

        private final Integer documentId;

        TextsLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public CopyableHashMap<Integer, TextDomainObject> load() {
            initDocumentsTexts();
            CopyableHashMap<Integer, TextDomainObject> documentTexts = documentsTexts.get(documentId);
            if (null == documentTexts) {
                documentTexts = new CopyableHashMap<>();
            }
            return documentTexts;
        }

        private void initDocumentsTexts() {
            if (null == documentsTexts) {
                documentsTexts = new HashMap<>();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, name, text, type FROM texts WHERE meta_id ", documentIds, rs -> {
                    while (rs.next()) {
                        Integer documentId = rs.getInt(1);
                        Integer textIndex = rs.getInt(2);
                        String text = rs.getString(3);
                        int textType = rs.getInt(4);
                        CopyableHashMap<Integer, TextDomainObject> documentTextsMap = documentsTexts.get(documentId);
                        if (null == documentTextsMap) {
                            documentTextsMap = new CopyableHashMap<>();
                            documentsTexts.put(documentId, documentTextsMap);
                        }
                        documentTextsMap.put(textIndex, new TextDomainObject(text, textType));
                    }
                    return null;
                });
            }
        }

    }

    private class TemplateIdsLoader implements LazilyLoadedObject.Loader<TemplateNames> {

        private final Integer documentId;

        TemplateIdsLoader(Integer documentId) {
            this.documentId = documentId;
        }

        public TemplateNames load() {
            initDocumentsTemplateIds();
            TemplateNames templateNames = documentsTemplateIds.get(documentId);
            if (null == templateNames) {
                templateNames = new TemplateNames();
            }
            return templateNames;
        }

        private void initDocumentsTemplateIds() {
            if (null == documentsTemplateIds) {
                documentsTemplateIds = new HashMap<>();
                DocumentInitializer.executeWithAppendedIntegerInClause(database, "SELECT meta_id, template_name, group_id, default_template, default_template_1, default_template_2 FROM text_docs WHERE meta_id ", documentIds, rs -> {
                    while (rs.next()) {
                        Integer documentId = rs.getInt("meta_id");
                        TemplateNames templateNames = new TemplateNames();
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
                });
            }
        }

    }

}
