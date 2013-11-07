package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.orm.TemplateNames;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TextDocumentDomainObject extends DocumentDomainObject {

    /**
     * Modified text indexes.
     *
     * Every modified text can be saved to history.
     * This controlled by setting boolean flag.
     *
     * Required when saving only particular set of text fields.
     */
    //private Map<Integer, Boolean> modifiedTextIndexes = new TreeMap<Integer, Boolean>();

    /**
     * Content loop unique item key.
     */
    private static final class ContentLoopItemKey {

        public final int itemNo;
        public final ContentRef contentRef;
        public final int hashCode;

        public ContentLoopItemKey(int itemNo, ContentRef contentRef) {
            this.itemNo = itemNo;
            this.contentRef = contentRef;
            this.hashCode = new HashCodeBuilder(17, 31).
                    append(contentRef.loopNo()).
                    append(contentRef.contentNo()).
                    append(itemNo).
                    toHashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof ContentLoopItemKey) && o.hashCode() == hashCode();
        }
    }

    /**
     * Images outside of loops.
     */
    private volatile ConcurrentHashMap<Integer, ImageDomainObject> images = new ConcurrentHashMap<Integer, ImageDomainObject>();

    /**
     * Texts outside of loops.
     */
    private volatile ConcurrentHashMap<Integer, TextDomainObject> texts = new ConcurrentHashMap<Integer, TextDomainObject>();

    /**
     * Texts in loops.
     */
    private volatile ConcurrentHashMap<ContentLoopItemKey, TextDomainObject> loopTexts
            = new ConcurrentHashMap<ContentLoopItemKey, TextDomainObject>();


    /**
     * Images in loops.
     */
    private volatile ConcurrentHashMap<ContentLoopItemKey, ImageDomainObject> loopImages
            = new ConcurrentHashMap<ContentLoopItemKey, ImageDomainObject>();


    /**
     * Includes map.
     * <p/>
     * Map key is an included doc's order index in this document.
     * <p/>
     * Map value is an included doc's id.
     */
    private volatile ConcurrentHashMap<Integer, Integer> includesMap = new ConcurrentHashMap<Integer, Integer>();

    /**
     * Menus map.
     * <p/>
     * Map index is a menu's no in this document.
     */
    private volatile ConcurrentHashMap<Integer, MenuDomainObject> menus = new ConcurrentHashMap<Integer, MenuDomainObject>();

    /**
     * Template names.
     */
    private volatile TemplateNames templateNames = new TemplateNames();

    /**
     * Content loops.
     * <p/>
     * Map key is a content's no in this document.
     */
    private volatile ConcurrentHashMap<Integer, ContentLoop> contentLoops = new ConcurrentHashMap<Integer, ContentLoop>();


    public TextDocumentDomainObject() {
        this(ID_NEW);
    }

    public TextDocumentDomainObject(int documentId) {
        setId(documentId);
    }

    @Override
    public TextDocumentDomainObject clone() {
        TextDocumentDomainObject clone = (TextDocumentDomainObject) super.clone();

        clone.images = cloneImages();
        clone.loopImages = cloneLoopImages();
        clone.includesMap = cloneIncludesMap();
        clone.menus = cloneMenusMap();
        clone.templateNames = cloneTemplateNames();
        clone.texts = cloneTexts();
        clone.loopTexts = cloneLoopTexts();
        clone.contentLoops = cloneContentLoopsMap();

        return clone;
    }

    public void accept(DocumentVisitor documentVisitor) {
        documentVisitor.visitTextDocument(this);
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.TEXT;
    }

    public Set<Integer> getChildDocumentIds() {
        Set<Integer> childDocuments = new HashSet<Integer>();

        for (MenuDomainObject menu : getMenus().values()) {
            for (MenuItemDomainObject menuItem : menu.getMenuItems()) {
                childDocuments.add(menuItem.getDocumentId());
            }
        }

        return childDocuments;
    }

    public Integer getIncludedDocumentId(int includeIndex) {
        return includesMap.get(includeIndex);
    }

    /**
     * Returns menu.
     * If menu does not exists creates and adds menu to this document.
     * @param menuNo
     * @return Menu
     */
    public MenuDomainObject getMenu(int menuNo) {
        MenuDomainObject menu = menus.get(menuNo);

        if (menu == null) {
            setMenu(menuNo, new MenuDomainObject());
            menu = menus.get(menuNo);
        }

        return menu;
    }


    public TextDomainObject getText(int no) {
        return texts.get(no);
    }


    /**
     * @return TextDomainObject or null if text can not be found.
     */
    public TextDomainObject getText(int textNo, ContentRef contentRef) {
        return contentRef == null ? texts.get(textNo) : loopTexts.get(new ContentLoopItemKey(textNo, contentRef));
    }

    /**
     * Removes all image.
     */
    public synchronized void removeAllImages() {
        images = new ConcurrentHashMap<Integer, ImageDomainObject>();
        loopImages = new ConcurrentHashMap<ContentLoopItemKey, ImageDomainObject>();
    }

    public void removeAllIncludes() {
        includesMap = new ConcurrentHashMap<Integer, Integer>();
    }

    public void removeAllMenus() {
        menus = new ConcurrentHashMap<Integer, MenuDomainObject>();
    }

    public void removeAllContentLoops() {
        contentLoops = new ConcurrentHashMap<Integer, ContentLoop>();
    }

    public synchronized void removeAllTexts() {
        texts = new ConcurrentHashMap<Integer, TextDomainObject>();
        loopTexts = new ConcurrentHashMap<ContentLoopItemKey, TextDomainObject>();
    }


    public void setInclude(int includeIndex, int includedDocumentId) {
        includesMap.put(includeIndex, includedDocumentId);
    }

    /**
     * @param no
     * @param menu
     * @return a copy of added menu.
     */
    public MenuDomainObject setMenu(int no, MenuDomainObject menu) {
        MenuDomainObject newMenu = menu.clone();
        MenuDomainObject oldMenu = menus.get(no);
        Long id = oldMenu != null ? oldMenu.getId() : null;

        if (oldMenu != null) newMenu.setSortOrder(oldMenu.getSortOrder());

        newMenu.setId(id);
        newMenu.setNo(no);
        newMenu.setDocRef(getRef());

        menus.put(no, newMenu);

        return newMenu.clone();
    }

    /**
     * Inserts a text to this document.
     * <p/>
     * If a text belongs to a content loop then both loop and its content must exist in this document.
     *
     * @param no text position in the document.
     * @param text text being inserted.
     * @return copy of inserted text.
     */
    public TextDomainObject setText(int no, TextDomainObject text) {
        ContentRef contentRef = text.getContentRef();
        ContentLoopItemKey key = contentRef == null
                ? null
                : new ContentLoopItemKey(no, contentRef);

        TextDomainObject oldText = key == null ? texts.get(no) : loopTexts.get(key);
        TextDomainObject newText = text.clone();

        newText.setId(oldText != null ? oldText.getId() : text.getId());
        newText.setI18nDocRef(getI18nRef());
        newText.setNo(no);

        if (key == null) {
            texts.put(no, newText);
        } else {
            ContentLoop loop = getContentLoop(contentRef.loopNo());

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Invalid text. Loop does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getI18nRef(), contentRef.loopNo(), contentRef.contentNo(), no)
                );
            }

            boolean contentExist = loop.findContent(contentRef.contentNo()).isPresent();

            if (!contentExist) {
                throw new IllegalStateException(String.format(
                        "Invalid text. Content does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getI18nRef(), contentRef.loopNo(), contentRef.contentNo(), no)
                );
            }

            loopTexts.put(key, newText);
        }

        return newText.clone();
    }

    public Map<Integer, Integer> getIncludesMap() {
        return Collections.unmodifiableMap(includesMap);
    }

    public String getTemplateName() {
        return templateNames.getTemplateName();
    }

    public int getTemplateGroupId() {
        return templateNames.getTemplateGroupId();
    }

    public Map<Integer, TextDomainObject> getTexts() {
        return texts;
    }


    public void setTemplateName(String templateName) {
        templateNames.setTemplateName(templateName);
    }

    public void setTemplateGroupId(int v) {
        templateNames.setTemplateGroupId(v);
    }

    public String getDefaultTemplateName() {
        return templateNames.getDefaultTemplateName();
    }

    public void setDefaultTemplateId(String defaultTemplateId) {
        templateNames.setDefaultTemplateName(defaultTemplateId);
    }

    public void removeInclude(int includeIndex) {
        includesMap.remove(includeIndex);
    }

    public String getDefaultTemplateNameForRestricted1() {
        return templateNames.getDefaultTemplateNameForRestricted1();
    }

    public String getDefaultTemplateNameForRestricted2() {
        return templateNames.getDefaultTemplateNameForRestricted2();
    }

    public void setDefaultTemplateIdForRestricted1(String defaultTemplateIdForRestricted1) {
        templateNames.setDefaultTemplateNameForRestricted1(defaultTemplateIdForRestricted1);
    }

    public void setDefaultTemplateIdForRestricted2(String defaultTemplateIdForRestricted2) {
        templateNames.setDefaultTemplateNameForRestricted2(defaultTemplateIdForRestricted2);
    }


    /**
     * @return images outside ot content loops.
     */
    public Map<Integer, ImageDomainObject> getImages() {
        return images;
    }


    public ImageDomainObject setImage(int no, ImageDomainObject image) {
        ContentRef contentRef = image.getContentRef();
        ContentLoopItemKey key = contentRef == null ? null : new ContentLoopItemKey(no, contentRef);

        ImageDomainObject oldImage = key == null ? images.get(no) : loopImages.get(key);
        ImageDomainObject newImage = image.clone();

        newImage.setId(oldImage != null ? oldImage.getId() : image.getId());
        newImage.setDocRef(getRef());
        newImage.setNo(no);
        newImage.setLanguage(getLanguage());

        if (key == null) {
            images.put(no, newImage);
        } else {
            ContentLoop loop = getContentLoop(contentRef.loopNo());

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Invalid image. Loop does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getRef(), contentRef.loopNo(), contentRef.contentNo(), no)
                );
            }

            boolean contentExist = loop.findContent(contentRef.contentNo()).isPresent();

            if (!contentExist) {
                throw new IllegalStateException(String.format(
                        "Invalid image. Content does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getRef(), contentRef.loopNo(), contentRef.contentNo(), no)
                );
            }

            loopImages.put(key, newImage);
        }

        return newImage.clone();
    }

    /**
     * @return image outside of content loop.
     */
    public ImageDomainObject getImage(int no) {
        return images.get(no);
    }

    public ImageDomainObject getImage(int imageNo, ContentRef contentRef) {
        return loopImages.get(new ContentLoopItemKey(imageNo, contentRef));
    }

    private ConcurrentHashMap<Integer, MenuDomainObject> cloneMenusMap() {
        ConcurrentHashMap<Integer, MenuDomainObject> menusClone = new ConcurrentHashMap<Integer, MenuDomainObject>();

        for (Map.Entry<Integer, MenuDomainObject> entry : menus.entrySet()) {
            MenuDomainObject menu = entry.getValue();
            MenuDomainObject menuClone = menu.clone();

            menusClone.put(entry.getKey(), menuClone);
        }

        return menusClone;
    }


    private ConcurrentHashMap<Integer, ImageDomainObject> cloneImages() {
        ConcurrentHashMap<Integer, ImageDomainObject> imagesClone = new ConcurrentHashMap<Integer, ImageDomainObject>();

        for (Map.Entry<Integer, ImageDomainObject> imagesEntry : images.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }


    private ConcurrentHashMap<ContentLoopItemKey, ImageDomainObject> cloneLoopImages() {
        ConcurrentHashMap<ContentLoopItemKey, ImageDomainObject> imagesClone
                = new ConcurrentHashMap<ContentLoopItemKey, ImageDomainObject>();

        for (Map.Entry<ContentLoopItemKey, ImageDomainObject> imagesEntry : loopImages.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }

    private ConcurrentHashMap<Integer, TextDomainObject> cloneTexts() {
        ConcurrentHashMap<Integer, TextDomainObject> textsClone = new ConcurrentHashMap<Integer, TextDomainObject>();

        for (Map.Entry<Integer, TextDomainObject> textsEntry : texts.entrySet()) {
            TextDomainObject text = textsEntry.getValue().clone();

            textsClone.put(textsEntry.getKey(), text);
        }

        return textsClone;
    }

    private ConcurrentHashMap<ContentLoopItemKey, TextDomainObject> cloneLoopTexts() {
        ConcurrentHashMap<ContentLoopItemKey, TextDomainObject> textsClone = new ConcurrentHashMap<ContentLoopItemKey, TextDomainObject>();

        for (Map.Entry<ContentLoopItemKey, TextDomainObject> textsEntry : loopTexts.entrySet()) {
            TextDomainObject text = textsEntry.getValue().clone();

            textsClone.put(textsEntry.getKey(), text);
        }

        return textsClone;
    }

    private TemplateNames cloneTemplateNames() {
        TemplateNames templateNamesClone = templateNames.clone();

        return templateNamesClone;
    }

    private ConcurrentHashMap<Integer, Integer> cloneIncludesMap() {
        ConcurrentHashMap<Integer, Integer> includesMapClone = new ConcurrentHashMap<Integer, Integer>(includesMap);

        return includesMapClone;
    }

    private ConcurrentHashMap<Integer, ContentLoop> cloneContentLoopsMap() {
        ConcurrentHashMap<Integer, ContentLoop> contentLoopsMapClone = new ConcurrentHashMap<Integer, ContentLoop>();

        for (Map.Entry<Integer, ContentLoop> entry : contentLoops.entrySet()) {
            contentLoopsMapClone.put(entry.getKey(), entry.getValue().clone());
        }

        return contentLoopsMapClone;
    }

    public TemplateNames getTemplateNames() {
        return templateNames;
    }

    public void setTemplateNames(TemplateNames templateNames) {
        this.templateNames = templateNames;
    }

    public Map<Integer, MenuDomainObject> getMenus() {
        return menus;
    }

    public void setMenus(Map<Integer, MenuDomainObject> menus) {
        this.menus = new ConcurrentHashMap<Integer, MenuDomainObject>(menus);
    }

    public void setIncludesMap(Map<Integer, Integer> includesMap) {
        this.includesMap = new ConcurrentHashMap<Integer, Integer>(includesMap);
    }

    public Map<Integer, ContentLoop> getContentLoops() {
        return contentLoops;
    }

    public void setContentLoops(Map<Integer, ContentLoop> contentLoops) {
        this.contentLoops = new ConcurrentHashMap<Integer, ContentLoop>(contentLoops);
    }

    public ContentLoop getContentLoop(int no) {
        return contentLoops.get(no);
    }

    /**
     * Sets content loop clone passed to the method.
     *
     * @param no          content loop no in this document.
     * @param contentLoop content loop to set.
     * @return clone of a ContentLoop set to this document.
     */
    public ContentLoop setContentLoop(int no, ContentLoop contentLoop) {
        ContentLoop oldContentLoop = contentLoops.get(no);
        ContentLoop newContentLoop = ContentLoop.builder(contentLoop)
                .id(oldContentLoop != null ? oldContentLoop.getId() : contentLoop.getId())
                .docRef(getRef())
                .no(no)
                .build();

        contentLoops.put(no, newContentLoop);

        return newContentLoop;
    }

    public Map<ContentLoopItemKey, TextDomainObject> getLoopTexts() {
        return loopTexts;
    }

    public Map<ContentLoopItemKey, ImageDomainObject> getLoopImages() {
        return loopImages;
    }
}