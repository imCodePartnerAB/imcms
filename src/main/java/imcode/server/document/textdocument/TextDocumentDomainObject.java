package imcode.server.document.textdocument;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextDocumentDomainObject extends DocumentDomainObject {

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

    public void setIncludesMap(Map<Integer, Integer> includesMap) {
        this.includesMap = new ConcurrentHashMap<>(includesMap);
    }

    public String getTemplateName() {
        return templateNames.getTemplateName();
    }

    public void setTemplateName(String templateName) {
        templateNames.setTemplateName(templateName);
    }

    public int getTemplateGroupId() {
        return templateNames.getTemplateGroupId();
    }

    public void setTemplateGroupId(int v) {
        templateNames.setTemplateGroupId(v);
    }

    public Map<Integer, TextDomainObject> getTexts() {
        return Collections.unmodifiableMap(texts);
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

    public void deleteImage(int no) {
        images.remove(no);
    }

    public void deleteImage(LoopItemRef loopItemRef) {
        loopImages.remove(loopItemRef);
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
        return templateNames.clone();
    }

    private ConcurrentHashMap<Integer, Integer> cloneIncludesMap() {
        return new ConcurrentHashMap<>(includesMap);
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

    public Map<Integer, Loop> getLoops() {
        return Collections.unmodifiableMap(loops);
    }

    public void setLoops(Map<Integer, Loop> loops) {
        this.loops = new ConcurrentHashMap<>(loops);
    }

    public Loop getLoop(int no) {
        return loops.get(no);
    }

    /**
     * Sets content loop clone passed to the method.
     *
     * @param no   content loop no in this document.
     * @param loop content loop to set.
     * @return contentLoop set to this document.
     */
    public Loop setLoop(int no, Loop loop) {
        loops.put(no, loop);

        return loop;
    }

    public Map<LoopItemRef, TextDomainObject> getLoopTexts() {
        return Collections.unmodifiableMap(loopTexts);
    }

    public Map<LoopItemRef, ImageDomainObject> getLoopImages() {
        return Collections.unmodifiableMap(loopImages);
    }

    public static class TemplateNames implements Cloneable {
        private String templateName;
        private int templateGroupId;
        private String defaultTemplateName;
        private String defaultTemplateNameForRestricted1;
        private String defaultTemplateNameForRestricted2;

        @Override
        public TemplateNames clone() {
            try {
                return (TemplateNames) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public int getTemplateGroupId() {
            return templateGroupId;
        }

        public void setTemplateGroupId(int templateGroupId) {
            this.templateGroupId = templateGroupId;
        }

        public String getDefaultTemplateName() {
            return defaultTemplateName;
        }

        public void setDefaultTemplateName(String defaultTemplateName) {
            this.defaultTemplateName = defaultTemplateName;
        }

        public String getDefaultTemplateNameForRestricted1() {
            return defaultTemplateNameForRestricted1;
        }

        public void setDefaultTemplateNameForRestricted1(String defaultTemplateNameForRestricted1) {
            this.defaultTemplateNameForRestricted1 = defaultTemplateNameForRestricted1;
        }

        public String getDefaultTemplateNameForRestricted2() {
            return defaultTemplateNameForRestricted2;
        }

        public void setDefaultTemplateNameForRestricted2(String defaultTemplateNameForRestricted2) {
            this.defaultTemplateNameForRestricted2 = defaultTemplateNameForRestricted2;
        }
    }

    /**
     * Reference to text document loop item.
     * <p>
     * In the current implementation a loop can contain texts ans images.
     * </p>
     */
    public static class LoopItemRef {

        private final int loopNo;
        private final int entryNo;
        private final int itemNo;
        private final int cachedHashCode;
        public LoopItemRef(int loopNo, int entryNo, int itemNo) {
            this.loopNo = loopNo;
            this.entryNo = entryNo;
            this.itemNo = itemNo;
            this.cachedHashCode = Objects.hash(loopNo, entryNo, itemNo);
        }

        public static LoopItemRef of(int loopNo, int entryNo, int itemNo) {
            return new LoopItemRef(loopNo, entryNo, itemNo);
        }

        public static LoopItemRef of(LoopEntryRef loopEntryRef, int itemNo) {
            return new LoopItemRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo(), itemNo);
        }

        public static Optional<LoopItemRef> of(String loopNo, String contentNo, String itemNo) {
            Integer loopNoInt = Ints.tryParse(loopNo);
            Integer contentNoInt = Ints.tryParse(contentNo);
            Integer itemNoInt = Ints.tryParse(itemNo);

            return Optional.ofNullable(
                    loopNoInt != null && contentNoInt != null && itemNo != null
                            ? LoopItemRef.of(loopNoInt, contentNoInt, itemNoInt)
                            : null
            );
        }

        public static Optional<LoopItemRef> of(String ref) {
            Matcher matcher = Pattern.compile("(\\d+)_(\\d+)_(\\d+)").matcher(Strings.nullToEmpty(ref).trim());

            return Optional.ofNullable(
                    matcher.find()
                            ? LoopItemRef.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)))
                            : null
            );
        }

        @Override
        public boolean equals(Object o) {
            return this == o || (o instanceof LoopItemRef && equals((LoopItemRef) o));
        }

        private boolean equals(LoopItemRef that) {
            return loopNo == that.loopNo && entryNo == that.entryNo && itemNo == that.itemNo;
        }

        @Override
        public int hashCode() {
            return cachedHashCode;
        }

        @Override
        public String toString() {
            return com.google.common.base.Objects.toStringHelper(this)
                    .add("entryNo", entryNo)
                    .add("loopNo", loopNo)
                    .add("itemNo", itemNo)
                    .toString();
        }

        public int getLoopNo() {
            return loopNo;
        }

        public int getEntryNo() {
            return entryNo;
        }

        public int getItemNo() {
            return itemNo;
        }

        public LoopEntryRef getEntryRef() {
            return LoopEntryRef.of(loopNo, entryNo);
        }
    }
}