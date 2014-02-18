package imcode.server.document.textdocument;

import com.imcode.imcms.api.Loop;
import com.imcode.imcms.api.LoopItemRef;
import com.imcode.imcms.mapping.orm.TemplateNames;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private volatile ConcurrentHashMap<LoopItemRef, TextDomainObject> loopTexts = new ConcurrentHashMap<>();

    /**
     * Images in loops.
     */
    private volatile ConcurrentHashMap<LoopItemRef, ImageDomainObject> loopImages = new ConcurrentHashMap<>();


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
    private volatile ConcurrentHashMap<Integer, Loop> loops = new ConcurrentHashMap<>();


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
        clone.loops = cloneLoopsMap();

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
     *
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
    public TextDomainObject getText(LoopItemRef loopItemRef) {
        Objects.requireNonNull(loopItemRef);

        return loopTexts.get(loopItemRef);
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
        loops.clear();
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
     */
    // fixme: check sort order vs RB4
    public void setMenu(int no, MenuDomainObject menu) {
        MenuDomainObject newMenu = menu.clone();
        MenuDomainObject oldMenu = menus.get(no);

        if (oldMenu != null) newMenu.setSortOrder(oldMenu.getSortOrder());

        menus.put(no, newMenu);
    }

    /**
     * Inserts a text to this document.
     * <p/>
     *
     * @param no   text position in the document.
     * @param text text being inserted.
     */
    public void setText(int no, TextDomainObject text) {
        Objects.requireNonNull(text);

        texts.put(no, text.clone());
    }

    public void setText(LoopItemRef loopItemRef, TextDomainObject text) {
        Objects.requireNonNull(loopItemRef);
        Objects.requireNonNull(text);

        loopTexts.put(loopItemRef, text);
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


    public void setImage(int no, ImageDomainObject image) {
        Objects.requireNonNull(image);
        images.put(no, image);
    }

    public void setImage(LoopItemRef loopItemRef, ImageDomainObject image) {
        Objects.requireNonNull(loopItemRef);
        Objects.requireNonNull(image);

        loopImages.put(loopItemRef, image);
    }

    /**
     * @return image outside of content loop.
     */
    public ImageDomainObject getImage(int no) {
        ImageDomainObject image = images.get(no);
        if (image == null) {
            image = new ImageDomainObject();
            images.put(no, image);
        }

        return images.get(no);
    }

    public ImageDomainObject getImage(LoopItemRef loopItemRef) {
        Objects.requireNonNull(loopItemRef);

        ImageDomainObject image = loopImages.get(loopItemRef);
        if (image == null) {
            image = new ImageDomainObject();
            loopImages.put(loopItemRef, image);
        }

        return image;
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


    private ConcurrentHashMap<LoopItemRef, ImageDomainObject> cloneLoopImages() {
        ConcurrentHashMap<LoopItemRef, ImageDomainObject> imagesClone = new ConcurrentHashMap<>();

        for (Map.Entry<LoopItemRef, ImageDomainObject> imagesEntry : loopImages.entrySet()) {
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

    private ConcurrentHashMap<LoopItemRef, TextDomainObject> cloneLoopTexts() {
        ConcurrentHashMap<LoopItemRef, TextDomainObject> textsClone = new ConcurrentHashMap<>();

        for (Map.Entry<LoopItemRef, TextDomainObject> textsEntry : loopTexts.entrySet()) {
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

    private ConcurrentHashMap<Integer, Loop> cloneLoopsMap() {
        return new ConcurrentHashMap<>(loops);
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

    public Map<Integer, Loop> getLoops() {
        return Collections.unmodifiableMap(loops);
    }

    public void setLoops(Map<Integer, Loop> loops) {
        this.loops = new ConcurrentHashMap<>(loops);
    }

    public Loop getContentLoop(int no) {
        return loops.get(no);
    }

    /**
     * Sets content loop clone passed to the method.
     *
     * @param no   content loop no in this document.
     * @param loop content loop to set.
     * @returncontentLoop set to this document.
     */
    public Loop setContentLoop(int no, Loop loop) {
        loops.put(no, loop);

        return loop;
    }

    public Map<LoopItemRef, TextDomainObject> getLoopTexts() {
        return Collections.unmodifiableMap(loopTexts);
    }

    public Map<LoopItemRef, ImageDomainObject> getLoopImages() {
        return Collections.unmodifiableMap(loopImages);
    }
}