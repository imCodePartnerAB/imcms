package imcode.server.document.textdocument;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.orm.TemplateNames;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TextDocumentDomainObject extends DocumentDomainObject {

    /**
     * Content loop unique item key.
     */
    private static final class ContentLoopItemKey {

        public final int itemNo;
        public final ContentLoopIdentity contentLoopIdentity;
        public final int hashCode;

        public ContentLoopItemKey(int itemNo, ContentLoopIdentity contentLoopIdentity) {
            this.itemNo = itemNo;
            this.contentLoopIdentity = contentLoopIdentity;
            this.hashCode = new HashCodeBuilder(17, 31).
                    append(contentLoopIdentity.getLoopNo()).
                    append(contentLoopIdentity.getContentNo()).
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
    private Map<Integer, ImageDomainObject> images = new HashMap<Integer, ImageDomainObject>();

    /**
     * Texts outside of loops.
     */
    private Map<Integer, TextDomainObject> texts = new HashMap<Integer, TextDomainObject>();

    /**
     * Texts in loops.
     */
    private Map<ContentLoopItemKey, TextDomainObject> loopTexts
            = new HashMap<ContentLoopItemKey, TextDomainObject>();


    /**
     * Images in loops.
     */
    private Map<ContentLoopItemKey, ImageDomainObject> loopImages
            = new HashMap<ContentLoopItemKey, ImageDomainObject>();


    /**
     * Includes map.
     * <p/>
     * Map key is an included doc's order index in this document.
     * <p/>
     * Map value is an included doc's id.
     */
    private Map<Integer, Integer> includesMap = new HashMap<Integer, Integer>();

    /**
     * Menus map.
     * <p/>
     * Map index is a menu's no in this document.
     */
    private Map<Integer, MenuDomainObject> menus = new HashMap<Integer, MenuDomainObject>();

    /**
     * Template names.
     */
    private TemplateNames templateNames = new TemplateNames();

    /**
     * Content loops.
     * <p/>
     * Map key is a content's no in this document.
     */
    private Map<Integer, ContentLoop> contentLoops = new HashMap<Integer, ContentLoop>();


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


    public MenuDomainObject getMenu(int menuIndex) {
        MenuDomainObject menu = menus.get(menuIndex);

        if (null == menu) {
            menu = new MenuDomainObject();
            setMenu(menuIndex, menu);
        }

        return menu;
    }


    public TextDomainObject getText(int no) {
        return texts.get(no);
    }


    /**
     * @return TextDomainObject or null if text can not be found.
     */
    public TextDomainObject getText(int textNo, ContentLoopIdentity contentLoopIdentity) {
        return loopTexts.get(new ContentLoopItemKey(textNo, contentLoopIdentity));
    }

    /**
     * Removes all image.
     */
    public synchronized void removeAllImages() {
        images = new HashMap<Integer, ImageDomainObject>();
        loopImages = new HashMap<ContentLoopItemKey, ImageDomainObject>();
    }

    public void removeAllIncludes() {
        includesMap = new HashMap<Integer, Integer>();
    }

    public void removeAllMenus() {
        menus = new HashMap<Integer, MenuDomainObject>();
    }

    public void removeAllContentLoops() {
        contentLoops = new HashMap<Integer, ContentLoop>();
    }

    public void removeAllTexts() {
        texts = new HashMap<Integer, TextDomainObject>();
        loopTexts = new HashMap<ContentLoopItemKey, TextDomainObject>();
    }


    public void setInclude(int includeIndex, int includedDocumentId) {
        includesMap.put(includeIndex, includedDocumentId);
    }


    public MenuDomainObject setMenu(int no, MenuDomainObject menu) {
        MenuDomainObject newMenu = menu.clone();
        MenuDomainObject oldMenu = menus.get(no);
        Long id = oldMenu != null ? oldMenu.getId() : null;

        if (oldMenu != null) newMenu.setSortOrder(oldMenu.getSortOrder());

        newMenu.setId(id);
        newMenu.setNo(no);
        newMenu.setDocIdentity(getIdentity());

        menus.put(no, newMenu);

        return newMenu.clone();
    }

    /**
     * Sets a text to this document.
     * <p/>
     * If a text belongs to a content loop then both loop and its content must exist in this document.
     *
     * @param no
     * @param text
     * @return
     */
    public TextDomainObject setText(int no, TextDomainObject text) {
        ContentLoopIdentity contentLoopIdentity = text.getContentLoopIdentity();
        ContentLoopItemKey key = contentLoopIdentity == null
                ? null :
                new ContentLoopItemKey(no, contentLoopIdentity);

        TextDomainObject oldText = key == null ? texts.get(no) : loopTexts.get(key);
        TextDomainObject newText = text.clone();
        Long id = oldText != null ? oldText.getId() : null;

        newText.setId(id);
        newText.setDocIdentity(getIdentity());
        newText.setNo(no);
        newText.setLanguage(getLanguage());

        if (key == null) {
            texts.put(no, newText);
        } else {
            ContentLoop loop = getContentLoop(contentLoopIdentity.getLoopNo());

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Invalid text. Loop does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getIdentity(), contentLoopIdentity.getLoopNo(), contentLoopIdentity.getContentNo(), no)
                );
            }

            Content content = loop.getContent(contentLoopIdentity.getContentNo());

            if (content == null) {
                throw new IllegalStateException(String.format(
                        "Invalid text. Content does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getIdentity(), contentLoopIdentity.getLoopNo(), contentLoopIdentity.getContentNo(), no)
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
        ContentLoopIdentity contentLoopIdentity = image.getContentLoopIdentity();
        ContentLoopItemKey key = contentLoopIdentity == null ? null : new ContentLoopItemKey(no, contentLoopIdentity);

        ImageDomainObject oldImage = key == null ? images.get(no) : loopImages.get(key);
        ImageDomainObject newImage = image.clone();
        Long id = oldImage != null ? oldImage.getId() : null;

        newImage.setId(id);
        newImage.setDocIdentity(getIdentity());
        newImage.setNo(no);
        newImage.setLanguage(getLanguage());

        if (key == null) {
            images.put(no, newImage);
        } else {
            ContentLoop loop = getContentLoop(contentLoopIdentity.getLoopNo());

            if (loop == null) {
                throw new IllegalStateException(String.format(
                        "Invalid image. Loop does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getIdentity(), contentLoopIdentity.getLoopNo(), contentLoopIdentity.getContentNo(), no)
                );
            }

            Content content = loop.getContent(contentLoopIdentity.getContentNo());

            if (content == null) {
                throw new IllegalStateException(String.format(
                        "Invalid image. Content does not exists. Doc identity: %s, loop no: %s, content no: %s, text no: %s."
                        , getIdentity(), contentLoopIdentity.getLoopNo(), contentLoopIdentity.getContentNo(), no)
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

    public ImageDomainObject getImage(int imageNo, ContentLoopIdentity contentLoopIdentity) {
        return loopImages.get(new ContentLoopItemKey(imageNo, contentLoopIdentity));
    }

    private Map<Integer, MenuDomainObject> cloneMenusMap() {
        Map<Integer, MenuDomainObject> menusClone = new HashMap<Integer, MenuDomainObject>();

        for (Map.Entry<Integer, MenuDomainObject> entry : menus.entrySet()) {
            MenuDomainObject menu = entry.getValue();
            MenuDomainObject menuClone = menu.clone();

            menusClone.put(entry.getKey(), menuClone);
        }

        return menusClone;
    }


    private Map<Integer, ImageDomainObject> cloneImages() {
        Map<Integer, ImageDomainObject> imagesClone = new HashMap<Integer, ImageDomainObject>();

        for (Map.Entry<Integer, ImageDomainObject> imagesEntry : images.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }


    private Map<ContentLoopItemKey, ImageDomainObject> cloneLoopImages() {
        Map<ContentLoopItemKey, ImageDomainObject> imagesClone
                = new HashMap<ContentLoopItemKey, ImageDomainObject>();

        for (Map.Entry<ContentLoopItemKey, ImageDomainObject> imagesEntry : loopImages.entrySet()) {
            ImageDomainObject image = imagesEntry.getValue().clone();

            imagesClone.put(imagesEntry.getKey(), image);
        }

        return imagesClone;
    }

    private Map<Integer, TextDomainObject> cloneTexts() {
        Map<Integer, TextDomainObject> textsClone = new HashMap<Integer, TextDomainObject>();

        for (Map.Entry<Integer, TextDomainObject> textsEntry : texts.entrySet()) {
            TextDomainObject text = textsEntry.getValue().clone();

            textsClone.put(textsEntry.getKey(), text);
        }

        return textsClone;
    }

    private Map<ContentLoopItemKey, TextDomainObject> cloneLoopTexts() {
        Map<ContentLoopItemKey, TextDomainObject> textsClone = new HashMap<ContentLoopItemKey, TextDomainObject>();

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

    private Map<Integer, Integer> cloneIncludesMap() {
        Map<Integer, Integer> includesMapClone = new HashMap<Integer, Integer>(includesMap);

        return includesMapClone;
    }

    private Map<Integer, ContentLoop> cloneContentLoopsMap() {
        Map<Integer, ContentLoop> contentLoopsMapClone = new HashMap<Integer, ContentLoop>();

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
        this.menus = menus;
    }

    public void setIncludesMap(Map<Integer, Integer> includesMap) {
        this.includesMap = includesMap;
    }

    public Map<Integer, ContentLoop> getContentLoops() {
        return contentLoops;
    }

    public void setContentLoops(Map<Integer, ContentLoop> contentLoops) {
        this.contentLoops = contentLoops;
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
        ContentLoop oldContentLoop = getContentLoop(no);
        ContentLoop newContentLoop = contentLoop.clone();

        Integer docId = getIdValue();
        Integer docVersionNo = getVersionNo();
        Long id = oldContentLoop != null ? oldContentLoop.getId() : null;

        newContentLoop.setDocIdentity(getIdentity());
        newContentLoop.setId(id);
        newContentLoop.setNo(no);

        contentLoops.put(no, newContentLoop);

        return newContentLoop.clone();
    }

    public Map<ContentLoopItemKey, TextDomainObject> getLoopTexts() {
        return loopTexts;
    }

    public Map<ContentLoopItemKey, ImageDomainObject> getLoopImages() {
        return loopImages;
    }
}