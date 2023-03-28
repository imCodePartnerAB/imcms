package imcode.server.document.textdocument;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.imcode.imcms.domain.dto.export.FileDocumentSerializer;
import com.imcode.imcms.domain.dto.export.TextDocumentSerializer;
import com.imcode.imcms.mapping.DocumentMenusMap;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.util.LazilyLoadedObject;
import org.apache.commons.lang.UnhandledException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@JsonSerialize(using = TextDocumentSerializer.class)
public class TextDocumentDomainObject extends DocumentDomainObject {

    /**
     * Modified text indexes.
     * <p>
     * Every modified text can be saved to history.
     * This controlled by setting boolean flag.
     * <p>
     * Required when saving only particular set of text fields.
     */
    private Map<Integer, Boolean> modifiedTextIndexes = new TreeMap<>();

    private LazilyLoadedObject<CopyableHashMap<Integer, TextDomainObject>> texts = new LazilyLoadedObject<>(new Attributes.CopyableHashMapLoader<>());
    private LazilyLoadedObject<CopyableHashMap<Integer, ImageDomainObject>> images = new LazilyLoadedObject<>(new Attributes.CopyableHashMapLoader<>());
    private LazilyLoadedObject<CopyableHashMap<Integer, Integer>> includes = new LazilyLoadedObject<>(new Attributes.CopyableHashMapLoader<>());
    private LazilyLoadedObject<DocumentMenusMap> menus = new LazilyLoadedObject<>(DocumentMenusMap::new);
    private LazilyLoadedObject<TemplateNames> templateNames = new LazilyLoadedObject<>(TemplateNames::new);

    public TextDocumentDomainObject() {
        this(ID_NEW);
    }

    public TextDocumentDomainObject(int documentId) {
        setId(documentId);
    }

    public void loadAllLazilyLoaded() {
        super.loadAllLazilyLoaded();

        texts.load();
        images.load();
        includes.load();
        menus.load();
        templateNames.load();
    }

    public Object clone() throws CloneNotSupportedException {
        TextDocumentDomainObject clone = (TextDocumentDomainObject) super.clone();
        clone.texts = texts.clone();
        clone.images = images.clone();
        clone.includes = includes.clone();
        clone.menus = menus.clone();
        clone.templateNames = templateNames.clone();
        return clone;
    }

    public DocumentTypeDomainObject getDocumentType() {
        return DocumentTypeDomainObject.TEXT;
    }

    public Set getChildDocumentIds() {
        Set<Integer> childDocuments = new HashSet<>();

        for (MenuDomainObject menu : getMenus().values()) {
            MenuItemDomainObject[] menuItems = menu.getMenuItems();
            for (MenuItemDomainObject menuItem : menuItems) {
                childDocuments.add(menuItem.getDocumentId());
            }
        }
        return childDocuments;
    }

    public ImageDomainObject getImage(int imageIndex) {
        ImageDomainObject image = images.get().get(imageIndex);
        if (null == image) {
            image = new ImageDomainObject();
        }
        return image;
    }

    public Integer getIncludedDocumentId(int includeIndex) {
        return includes.get().get(includeIndex);
    }

    public MenuDomainObject getMenu(int menuIndex) {
        Map menusMap = menus.get();
        MenuDomainObject menu = (MenuDomainObject) menusMap.get(menuIndex);
        if (null == menu) {
            menu = new MenuDomainObject();
            setMenu(menuIndex, menu);
        }
        return menu;
    }

    public TextDomainObject getText(int textFieldIndex) {
        return texts.get().get(textFieldIndex);
    }

    public void accept(DocumentVisitor documentVisitor) {
        documentVisitor.visitTextDocument(this);
    }

    public void removeAllImages() {
        images.get().clear();
    }

    public void removeAllIncludes() {
        includes.get().clear();
    }

    public void removeAllMenus() {
        menus.get().clear();
    }

    public void removeAllTexts() {
        texts.get().clear();
    }

    public void setInclude(int includeIndex, int includedDocumentId) {
        includes.get().put(includeIndex, includedDocumentId);
    }

    public void setMenu(int menuIndex, MenuDomainObject menu) {
        menus.get().put(menuIndex, menu);
    }

    public void setText(int textIndex, TextDomainObject text) {
        texts.get().put(textIndex, text);
    }

    /**
     * @return Map<Integer ,   { @ link   ImageDomainObject }   *
          */
    public Map<Integer, ImageDomainObject> getImages() {
        return Collections.unmodifiableMap(images.get());
    }

    public Map getIncludes() {
        return Collections.unmodifiableMap(includes.get());
    }

    public Map<Integer, MenuDomainObject> getMenus() {
        return Collections.unmodifiableMap(menus.get());
    }

    public String getTemplateName() {
        return getTemplateNames().getTemplateName();
    }

    public void setTemplateName(String templateName) {
        getTemplateNames().setTemplateName(templateName);
    }

    private TemplateNames getTemplateNames() {
        return templateNames.get();
    }

    public int getTemplateGroupId() {
        return getTemplateNames().getTemplateGroupId();
    }

    public void setTemplateGroupId(int v) {
        getTemplateNames().setTemplateGroupId(v);
    }

    public Map<Integer, TextDomainObject> getTexts() {
        return Collections.unmodifiableMap(texts.get());
    }

    public void setImage(int imageIndex, ImageDomainObject image) {
        image.setImageIndex(imageIndex);
        images.get().put(imageIndex, image);
    }

    public String getDefaultTemplateName() {
        return getTemplateNames().getDefaultTemplateName();
    }

    public void setDefaultTemplateId(String defaultTemplateId) {
        getTemplateNames().setDefaultTemplateName(defaultTemplateId);
    }

    public void removeInclude(int includeIndex) {
        includes.get().remove(includeIndex);
    }

    public void setLazilyLoadedMenus(LazilyLoadedObject<DocumentMenusMap> menus) {
        this.menus = menus;
    }

    public void setLazilyLoadedImages(LazilyLoadedObject images) {
        this.images = images;
    }

    public void setLazilyLoadedIncludes(LazilyLoadedObject includes) {
        this.includes = includes;
    }

    public void setLazilyLoadedTexts(LazilyLoadedObject<CopyableHashMap<Integer, TextDomainObject>> texts) {
        this.texts = texts;
    }

    public String getDefaultTemplateNameForRestricted1() {
        return getTemplateNames().getDefaultTemplateNameForRestricted1();
    }

    public String getDefaultTemplateNameForRestricted2() {
        return getTemplateNames().getDefaultTemplateNameForRestricted2();
    }

    public void setDefaultTemplateIdForRestricted1(String defaultTemplateIdForRestricted1) {
        getTemplateNames().setDefaultTemplateNameForRestricted1(defaultTemplateIdForRestricted1);
    }

    public void setDefaultTemplateIdForRestricted2(String defaultTemplateIdForRestricted2) {
        getTemplateNames().setDefaultTemplateNameForRestricted2(defaultTemplateIdForRestricted2);
    }

    public void setLazilyLoadedTemplateIds(LazilyLoadedObject<TemplateNames> templateIds) {
        this.templateNames = templateIds;
    }

    public Map<Integer, Boolean> getModifiedTextIndexes() {
        return modifiedTextIndexes;
    }

    public void addModifiedTextIndex(int index, boolean saveToHistory) {
        modifiedTextIndexes.put(index, saveToHistory);
    }

    public void removeModifiedTextIndex(int index) {
        modifiedTextIndexes.remove(index);
    }

    public void removeAllModifiedTextIndexs() {
        modifiedTextIndexes.clear();
    }


    public static class TemplateNames implements LazilyLoadedObject.Copyable<TemplateNames>, Cloneable {
        private String templateName;
        private int templateGroupId;
        private String defaultTemplateName;
        private String defaultTemplateNameForRestricted1;
        private String defaultTemplateNameForRestricted2;

        public TemplateNames copy() {
            return (TemplateNames) clone();
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new UnhandledException(e);
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
}
