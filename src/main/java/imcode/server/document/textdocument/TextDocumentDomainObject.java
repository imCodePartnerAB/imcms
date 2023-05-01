package imcode.server.document.textdocument;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.mapping.container.LoopEntryRef;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntry;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     *
     * Map key is an included doc's order index in this document.
     *
     * Map value is an included doc's id.
     */
    private volatile ConcurrentHashMap<Integer, Integer> includesMap = new ConcurrentHashMap<>();
    /**
     * Menus map.
     *
     * Map index is a menu's no in this document.
     */
    private volatile ConcurrentHashMap<Integer, MenuDTO> menus = new ConcurrentHashMap<>();
    /**
     * Template names.
     */
    private volatile TemplateNames templateNames = new TemplateNames();
    /**
     * Content loops.
     *
     * Map key is a content's no in this document.
     */
    private volatile ConcurrentHashMap<Integer, Loop> loops = new ConcurrentHashMap<>();

    public TextDocumentDomainObject() {
        this(ID_NEW);
    }

    public TextDocumentDomainObject(int documentId) {
        this(documentId, Imcms.getLanguage());
    }

    public TextDocumentDomainObject(String langCode){
        this(ID_NEW, Imcms.getServices().getLanguageService().findByCode(langCode));
    }

    public TextDocumentDomainObject(Language language) {
        this(ID_NEW, language);
    }

    public TextDocumentDomainObject(int documentId, String langCode) {
        setId(documentId);
        setLanguage(Imcms.getServices().getLanguageService().findByCode(langCode));
    }

    public TextDocumentDomainObject(int documentId, Language language) {
        setId(documentId);
        setLanguage(language);
    }

    @Override
    public TextDocumentDomainObject clone() {
        TextDocumentDomainObject clone = (TextDocumentDomainObject) super.clone();

        clone.images = cloneImages();
        clone.loopImages = cloneLoopImages();
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

        for (MenuDTO menu : getMenus().values()) {
            for (MenuItemDTO menuItem : menu.getMenuItems()) {
                childDocuments.add(menuItem.getDocumentId());
            }
        }

        return childDocuments;
    }

    /**
     * Returns menu.
     * If menu does not exists creates and adds menu to this document.
     *
     * @param menuNo Index of menu at document
     * @return Requested menu or new object if no exits
     */
    public MenuDTO getMenu(int menuNo) {
        MenuDTO menu = menus.get(menuNo);

        if (menu == null) {
            setMenu(menuNo, new MenuDTO());
            menu = menus.get(menuNo);
        }

        return menu;
    }

    public TextDomainObject getText(int no) {
        return texts.get(no);
    }

    /**
     *
     * @param loopItemRef
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

    public void setMenu(int no, MenuDTO menu) {
        menus.put(no, menu);
    }

    /**
     * Inserts a text to this document.
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

    private ConcurrentHashMap<Integer, MenuDTO> cloneMenusMap() {
        return new ConcurrentHashMap<>(menus);
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

    private ConcurrentHashMap<Integer, Loop> cloneLoopsMap() {
        return new ConcurrentHashMap<>(loops);
    }

    public TemplateNames getTemplateNames() {
        return templateNames;
    }

    public void setTemplateNames(TemplateNames templateNames) {
        this.templateNames = templateNames;
    }

    public Map<Integer, MenuDTO> getMenus() {
        return Collections.unmodifiableMap(menus);
    }

    public void setMenus(Map<Integer, MenuDTO> menus) {
        this.menus = new ConcurrentHashMap<>(menus);
    }

    public Map<Integer, Loop> getLoops() {
        return Collections.unmodifiableMap(loops);
    }

    public void setLoops(Map<Integer, Loop> loops) {
        this.loops = new ConcurrentHashMap<>(loops);
        updateLoopsContent();
    }

    public Loop getLoop(int loopIndex) {
        return loops.get(loopIndex);
    }

    private void updateLoopsContent() {
        loops.forEach(this::updateLoopContent);
    }

    private void updateLoopContent(Integer loopNo, Loop loop) {
        Set<Integer> entriesNo = loop.getEntries().stream().map(LoopEntry::getIndex).collect(Collectors.toSet());
        loopTexts.keySet().stream()
                .filter(loopItemRef -> (loopItemRef.getLoopNo() == loopNo) && (!entriesNo.contains(loopItemRef.getEntryNo())))
                .forEach(loopItemRef -> loopTexts.remove(loopItemRef));
    }

    public Map<LoopItemRef, TextDomainObject> getLoopTexts() {
        return Collections.unmodifiableMap(loopTexts);
    }

    public Map<LoopItemRef, ImageDomainObject> getLoopImages() {
        return Collections.unmodifiableMap(loopImages);
    }

    public static class TemplateNames implements Cloneable, Serializable {
        private String templateName;
        private int templateGroupId;
        private String defaultTemplateName;

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
    }

    /**
     * Reference to text document loop item.
     * <p>
     * In the current implementation a loop can contain texts ans images.
     * </p>
     */
    public static class LoopItemRef implements Serializable {

        private static final long serialVersionUID = -1466379121949797346L;

        private final int loopNo;
        private final int entryNo;
        private final int itemNo;
        private final int cachedHashCode;

        LoopItemRef(int loopNo, int entryNo, int itemNo) {
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
                    loopNoInt != null && contentNoInt != null && itemNoInt != null
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
	        return MoreObjects.toStringHelper(this)
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
