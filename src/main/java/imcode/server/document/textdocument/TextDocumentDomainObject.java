package imcode.server.document.textdocument;

import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.ContentLoopItemRef;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.imcode.imcms.mapping.orm.TemplateNames;

public class TextDocumentDomainObject extends DocumentDomainObject {

    /**
     * Modified text indexes.
     *
     * Every modified text can be saved to history.
     * This controlled by setting boolean flag.
     *
     * Required when saving only particular set of text fields.
     */
    //private Map<Integer, Boolean> modifiedTextIndexes = new TreeMap<>();

    /**
     * Content loop unique item key.
     */
    public static final class LoopItemKey {

        public final int itemNo;
        public final ContentLoopItemRef contentLoopRef;
        public final int hashCode;

        public LoopItemKey(int itemNo, ContentLoopItemRef contentLoopRef) {
            this.itemNo = itemNo;
            this.contentLoopRef = contentLoopRef;
            this.hashCode = Objects.hash(itemNo, contentLoopRef);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof LoopItemKey) && o.hashCode() == hashCode();
        }
    }

    /**
     * Images outside of loops.
     */
    private volatile ConcurrentHashMap<Integer, ImageDomainObject> images = new ConcurrentHashMap<>();

    /**
     * Texts outside of loops.
     */
    private volatile ConcurrentHashMap<Integer, TextDomainObject> texts = new ConcurrentHashMap<>();

    /**
     * Texts in loops.
     */
    private volatile ConcurrentHashMap<LoopItemKey, TextDomainObject> loopTexts = new ConcurrentHashMap<>();


    /**
     * Images in loops.
     */
    private volatile ConcurrentHashMap<LoopItemKey, ImageDomainObject> loopImages = new ConcurrentHashMap<>();


    /**
     * Includes map.
     * <p/>
     * Map key is an included doc's order index in this document.
     * <p/>
     * Map value is an included doc's id.
     */
    private volatile ConcurrentHashMap<Integer, Integer> includesMap = new ConcurrentHashMap<>();

    /**
     * Menus map.
     * <p/>
     * Map index is a menu's no in this document.
     */
    private volatile ConcurrentHashMap<Integer, MenuDomainObject> menus = new ConcurrentHashMap<>();

    /**
     * Template names.
     */
    private volatile TemplateNames templateNames = new TemplateNames();

    /**
     * Content loops.
     * <p/>
     * Map key is a content's no in this document.
     */
    private volatile ConcurrentHashMap<Integer, ContentLoop> contentLoops = new ConcurrentHashMap<>();


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
        Set<Integer> childDocuments = new HashSet<>();

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
    public TextDomainObject getText(int textNo, ContentLoopItemRef contentLoopRef) {
        return contentLoopRef == null ? texts.get(textNo) : loopTexts.get(new LoopItemKey(textNo, contentLoopRef));
    }

    /**
     * Removes all image.
     */
    public void removeAllImages() {
        images.clear();
        loopImages.clear();
    }

    public void removeAllIncludes() {
        includesMap.clear();
    }

    public void removeAllMenus() {
        menus.clear();
    }

    public void removeAllContentLoops() {
        contentLoops.clear();
    }

    public void removeAllTexts() {
        texts.clear();
        loopTexts.clear();
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

        if (oldMenu != null) newMenu.setSortOrder(oldMenu.getSortOrder());

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
        ContentLoopItemRef contentLoopRef = text.getContentLoopRef();
        LoopItemKey key = contentLoopRef == null
                ? null
                : new LoopItemKey(no, contentLoopRef);

        TextDomainObject newText = text.clone();

        if (key == null) {
            texts.put(no, newText);
        } else {
            ContentLoop loop = getContentLoop(contentLoopRef.getLoopNo());

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Invalid text. Loop does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getRef(), contentLoopRef.getLoopNo(), contentLoopRef.getContentNo(), no)
                );
            }

            boolean contentExist = loop.findContentByNo(contentLoopRef.getContentNo()).isPresent();

            if (!contentExist) {
                throw new IllegalStateException(String.format(
                        "Invalid text. Content does not exists. DocRef identity: %s, contentRef: %s, text no: %s."
                        , getRef(), contentLoopRef, no)
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
        return Collections.unmodifiableMap(texts);
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
        return Collections.unmodifiableMap(images);
    }


    public ImageDomainObject setImage(int no, ImageDomainObject image) {
        ContentLoopItemRef contentLoopRef = image.getContentLoopRef();
        LoopItemKey key = contentLoopRef == null ? null : new LoopItemKey(no, contentLoopRef);

        ImageDomainObject oldImage = key == null ? images.get(no) : loopImages.get(key);
        ImageDomainObject newImage = image.clone();

        if (key == null) {
            images.put(no, newImage);
        } else {
            ContentLoop loop = getContentLoop(contentLoopRef.getLoopNo());

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Invalid image. Loop does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getRef(), contentLoopRef.getLoopNo(), contentLoopRef.getContentNo(), no)
                );
            }

            boolean contentExist = loop.findContentByNo(contentLoopRef.getContentNo()).isPresent();

            if (!contentExist) {
                throw new IllegalStateException(String.format(
                        "Invalid image. Content does not exists. DocRef: %s, contentRef: %s, image no: %s."
                        , getRef(), contentLoopRef, no)
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

    public ImageDomainObject getImage(int imageNo, ContentLoopItemRef contentLoopRef) {
        return loopImages.get(new LoopItemKey(imageNo, contentLoopRef));
    }

    private ConcurrentHashMap<Integer, MenuDomainObject> cloneMenusMap() {
        ConcurrentHashMap<Integer, MenuDomainObject> menusClone = new ConcurrentHashMap<>();

        for (Map.Entry<Integer, MenuDomainObject> entry : menus.entrySet()) {
            MenuDomainObject menu = entry.getValue();
            MenuDomainObject menuClone = menu.clone();

            menusClone.put(entry.getKey(), menuClone);
        }

        return menusClone;
    }


    private ConcurrentHashMap<Integer, ImageDomainObject> cloneImages() {
        ConcurrentHashMap<Integer, ImageDomainObject> imagesClone = new ConcurrentHashMap<>();

        for (Map.Entry<Integer, ImageDomainObject> imagesEntry : images.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }


    private ConcurrentHashMap<LoopItemKey, ImageDomainObject> cloneLoopImages() {
        ConcurrentHashMap<LoopItemKey, ImageDomainObject> imagesClone = new ConcurrentHashMap<>();

        for (Map.Entry<LoopItemKey, ImageDomainObject> imagesEntry : loopImages.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }

    private ConcurrentHashMap<Integer, TextDomainObject> cloneTexts() {
        ConcurrentHashMap<Integer, TextDomainObject> textsClone = new ConcurrentHashMap<>();

        for (Map.Entry<Integer, TextDomainObject> textsEntry : texts.entrySet()) {
            TextDomainObject text = textsEntry.getValue().clone();

            textsClone.put(textsEntry.getKey(), text);
        }

        return textsClone;
    }

    private ConcurrentHashMap<LoopItemKey, TextDomainObject> cloneLoopTexts() {
        ConcurrentHashMap<LoopItemKey, TextDomainObject> textsClone = new ConcurrentHashMap<>();

        for (Map.Entry<LoopItemKey, TextDomainObject> textsEntry : loopTexts.entrySet()) {
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
        ConcurrentHashMap<Integer, Integer> includesMapClone = new ConcurrentHashMap<>(includesMap);

        return includesMapClone;
    }

    private ConcurrentHashMap<Integer, ContentLoop> cloneContentLoopsMap() {
        return new ConcurrentHashMap<>(contentLoops);
    }

    public TemplateNames getTemplateNames() {
        return templateNames;
    }

    public void setTemplateNames(TemplateNames templateNames) {
        this.templateNames = templateNames;
    }

    public Map<Integer, MenuDomainObject> getMenus() {
        return Collections.unmodifiableMap(menus);
    }

    public void setMenus(Map<Integer, MenuDomainObject> menus) {
        this.menus = new ConcurrentHashMap<>(menus);
    }

    public void setIncludesMap(Map<Integer, Integer> includesMap) {
        this.includesMap = new ConcurrentHashMap<>(includesMap);
    }

    public Map<Integer, ContentLoop> getContentLoops() {
        return Collections.unmodifiableMap(contentLoops);
    }

    public void setContentLoops(Map<Integer, ContentLoop> contentLoops) {
        this.contentLoops = new ConcurrentHashMap<>(contentLoops);
    }

    public ContentLoop getContentLoop(int no) {
        return contentLoops.get(no);
    }

    /**
     * Sets content loop clone passed to the method.
     *
     * @param no          content loop no in this document.
     * @param contentLoop content loop to set.
     * @returncontentLoop set to this document.
     */
    public ContentLoop setContentLoop(int no, ContentLoop contentLoop) {
        contentLoops.put(no, contentLoop);

        return contentLoop;
    }

    public Map<LoopItemKey, TextDomainObject> getLoopTexts() {
        return Collections.unmodifiableMap(loopTexts);
    }

    public Map<LoopItemKey, ImageDomainObject> getLoopImages() {
        return Collections.unmodifiableMap(loopImages);
    }
}