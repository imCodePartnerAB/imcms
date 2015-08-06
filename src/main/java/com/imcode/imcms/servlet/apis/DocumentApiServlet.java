package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentCommonContent;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.util.JSONUtils;
import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.util.io.FileInputStreamSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 17.02.2015.
 */
@RestController
@RequestMapping("/document")
public class DocumentApiServlet {
    private static final Logger LOG = Logger.getLogger(DocumentApiServlet.class);


    @RequestMapping(method = RequestMethod.GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("id") != null) {
            Integer id = Integer.parseInt(request.getParameter("id"));


            DocumentDomainObject documentDomainObject = Imcms.getServices().getDocumentMapper().getDocument(id);
            DocumentEntity result;

            switch (documentDomainObject.getDocumentTypeId()) {
                case DocumentTypeDomainObject.URL_ID: {
                    result = new UrlDocumentEntity();
                    asUrlEntity((UrlDocumentEntity) result, (UrlDocumentDomainObject) documentDomainObject);
                }
                break;
                case DocumentTypeDomainObject.FILE_ID: {
                    result = new FileDocumentEntity();
                    asFileEntity((FileDocumentEntity) result, (FileDocumentDomainObject) documentDomainObject);
                }
                break;
                case DocumentTypeDomainObject.TEXT_ID:
                default: {
                    result = new TextDocumentEntity();
                    asTextEntity((TextDocumentEntity) result, (TextDocumentDomainObject) documentDomainObject);
                }
                break;
            }

            prepareEntity(result, documentDomainObject);

            JSONUtils.defaultJSONAnswer(response, result);
        } else {
            List<Map<String, Object>> result = new ArrayList<>();
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            List<DocumentDomainObject> documents = documentMapper.getDocuments(documentMapper.getAllDocumentIds());
            for (DocumentDomainObject document : documents) {
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("id", document.getId());
                objectMap.put("name", document.getName());
                objectMap.put("label", document.getHeadline());
                objectMap.put("language", document.getLanguage().getName());
                objectMap.put("alias", document.getAlias());
                objectMap.put("type", document.getDocumentType().getName().toLocalizedString(Imcms.getUser()));
                result.add(objectMap);

            }
            JSONUtils.defaultJSONAnswer(response, result);
        }
    }


    @RequestMapping(method = RequestMethod.POST)
    protected Object doPut(HttpServletRequest req,
                           @RequestParam("type") Integer type,
                           @RequestParam("data") String data,
                           @RequestParam(value = "file", required = false) MultipartFile file) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            DocumentDomainObject documentDomainObject;

            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            DocumentEntity documentEntity;

            switch (type) {
                case DocumentTypeDomainObject.URL_ID: {
                    documentEntity = new ObjectMapper()
                            .readValue(
                                    data,
                                    new TypeReference<UrlDocumentEntity>() {
                                    }
                            );
                    documentDomainObject = documentEntity.id == null ? documentMapper.createDocumentOfTypeFromParent(UrlDocument.TYPE_ID,
                            documentMapper.getDocument(documentMapper.getHighestDocumentId()),
                            Imcms.getUser()) : documentMapper.getDocument(documentEntity.id);

                    asUrlDocument((UrlDocumentDomainObject) documentDomainObject, (UrlDocumentEntity) documentEntity);
                }
                break;
                case DocumentTypeDomainObject.FILE_ID: {
                    documentEntity = new ObjectMapper()
                            .readValue(
                                    data,
                                    new TypeReference<FileDocumentEntity>() {
                                    }
                            );
                    documentDomainObject = documentEntity.id == null ? documentMapper.createDocumentOfTypeFromParent(FileDocument.TYPE_ID,
                            documentMapper.getDocument(documentMapper.getHighestDocumentId()),
                            Imcms.getUser()) : documentMapper.getDocument(documentEntity.id);
                    asFileDocument((FileDocumentDomainObject) documentDomainObject, (FileDocumentEntity) documentEntity, file);

                }
                break;
                case DocumentTypeDomainObject.TEXT_ID:
                default: {
                    documentEntity = new ObjectMapper()
                            .readValue(
                                    data,
                                    new TypeReference<TextDocumentEntity>() {
                                    }
                            );
                    documentDomainObject = documentEntity.id == null ? documentMapper.createDocumentOfTypeFromParent(TextDocument.TYPE_ID,
                            documentMapper.getDocument(documentMapper.getHighestDocumentId()),
                            Imcms.getUser()) : documentMapper.getDocument(documentEntity.id);

                    asTextDocument((TextDocumentDomainObject) documentDomainObject, (TextDocumentEntity) documentEntity);
                }
                break;
            }


            prepareDocument(documentEntity, documentDomainObject);


            if (documentEntity.id != null)
                documentMapper.saveDocument(documentDomainObject, getContentMap(documentEntity), Imcms.getUser());
            else
                documentDomainObject.setId(documentMapper.saveNewDocument(documentDomainObject, getContentMap(documentEntity), Imcms.getUser()).getId());

            documentEntity.id = documentDomainObject.getId();
            result.put("result", true);
            result.put("data", documentEntity);
        } catch (Exception e) {
            LOG.error("Problem during document creating", e);
            result.put("result", false);
        }
        return result;
    }

    protected void prepareDocument(DocumentEntity documentEntity, DocumentDomainObject documentDomainObject) {
        CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();

        documentEntity.access.forEach((id, map) ->
                documentDomainObject
                        .setDocumentPermissionSetTypeForRoleId(new RoleId(id),
                                DocumentPermissionSetTypeDomainObject
                                        .values()[Integer.parseInt(map.get("permission").toString())]));

        if (StringUtils.isNotEmpty(documentEntity.alias)) {
            documentDomainObject.setAlias(documentEntity.alias);
        } else {
            documentDomainObject.setAlias(null);
        }

        documentDomainObject.setTarget(documentEntity.target);
        documentDomainObject.setKeywords(documentEntity.keywords);
        documentDomainObject.setPublicationStatus(Document.PublicationStatus.of(documentEntity.status));
        documentDomainObject.setCategoryIds(
                documentEntity.categories.entrySet().stream()
                        .filter(entry -> {
                            for (String item : entry.getValue()) {
                                if (!StringUtils.isNotEmpty(item)) {
                                    return false;
                                }
                            }
                            return true;
                        })
                        .map(entry -> {
                            List<Integer> ids = new ArrayList<>();
                            for (String item : entry.getValue()) {
                                ids.add(categoryMapper.getCategoryByTypeAndName(
                                        categoryMapper.getCategoryTypeByName(entry.getKey()),
                                        item).getId());
                            }
                            return ids;
                        })
                        .flatMap(List::stream)
                        .collect(Collectors.toSet())
        );
    }

    protected Map<DocumentLanguage, DocumentCommonContent> getContentMap(DocumentEntity entity) {
        Map<DocumentLanguage, DocumentCommonContent> contentMap = new HashMap<>();
        for (DocumentLanguage language : Imcms.getServices().getDocumentLanguages().getAll()) {
            DocumentEntity.LanguageEntity languageEntity = entity.languages.get(language.getName());
            if (languageEntity.enabled)
                contentMap.put(
                        language,
                        DocumentCommonContent.builder()
                                .headline(languageEntity.title)
                                .menuImageURL(languageEntity.image)
                                .menuText(languageEntity.menuText)
                                .build()
                );
        }
        return contentMap;
    }

    protected void asFileDocument(FileDocumentDomainObject document, FileDocumentEntity entity, MultipartFile multipartFile) throws IOException, ServletException {
        FileDocumentDomainObject.FileDocumentFile fileDocumentFile = new FileDocumentDomainObject.FileDocumentFile();
        fileDocumentFile.setFilename(multipartFile.getOriginalFilename());

        fileDocumentFile.setMimeType(multipartFile.getContentType());
        File file = new File(Imcms.getServices().getConfig().getFilePath(), multipartFile.getName());
        file.createNewFile();
        multipartFile.transferTo(file);
        fileDocumentFile.setInputStreamSource(new FileInputStreamSource(file));
        document.addFile(multipartFile.getName(), fileDocumentFile);
    }

    protected void asTextDocument(TextDocumentDomainObject document, TextDocumentEntity entity) {
        TextDocumentPermissionSetDomainObject permissionSetDomainObject1 = new TextDocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
        TextDocumentPermissionSetDomainObject permissionSetDomainObject2 = new TextDocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_2);
        DocumentPermissionSets documentPermissionSets = new DocumentPermissionSets();

        permissionSetDomainObject1.setEditImages(entity.permissions.get(0).canEditImage);
        permissionSetDomainObject1.setEditMenus(entity.permissions.get(0).canEditMenu);
        permissionSetDomainObject1.setEditTexts(entity.permissions.get(0).canEditText);
        permissionSetDomainObject1.setEditLoops(entity.permissions.get(0).canEditLoop);
        permissionSetDomainObject1.setEditDocumentInformation(entity.permissions.get(0).canEditDocumentInformation);
        permissionSetDomainObject1.setEditPermissions(entity.permissions.get(0).canEditDocumentInformation);

        permissionSetDomainObject2.setEditImages(entity.permissions.get(1).canEditImage);
        permissionSetDomainObject2.setEditMenus(entity.permissions.get(1).canEditMenu);
        permissionSetDomainObject2.setEditTexts(entity.permissions.get(1).canEditText);
        permissionSetDomainObject2.setEditLoops(entity.permissions.get(1).canEditLoop);
        permissionSetDomainObject2.setEditDocumentInformation(entity.permissions.get(1).canEditDocumentInformation);
        permissionSetDomainObject2.setEditPermissions(entity.permissions.get(1).canEditDocumentInformation);

        documentPermissionSets.setRestricted1(permissionSetDomainObject1);
        documentPermissionSets.setRestricted2(permissionSetDomainObject2);

        document.setPermissionSets(documentPermissionSets);
        document.setPermissionSetsForNewDocument(documentPermissionSets);

        document.setTemplateName(entity.template);
    }

    protected void asUrlDocument(UrlDocumentDomainObject document, UrlDocumentEntity entity) {
        document.setUrl(entity.url);
    }

    protected void prepareEntity(DocumentEntity entity, DocumentDomainObject document) {
        CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();

        entity.languages = new HashMap<>();
        entity.alias = document.getAlias();
        entity.id = document.getId();
        entity.status = document.getPublicationStatus().asInt();
        entity.target = document.getTarget();

        entity.access = Stream
                .of(document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings())
                .collect(Collectors.toMap(b -> b.getRoleId().intValue(), a -> {
                    Map<String, java.io.Serializable> map = new HashMap<>();
                    map.put("permission", a.getDocumentPermissionSetType().getId());
                    map.put("role", a.getRoleId());
                    return map;
                }));

        entity.keywords = document.getKeywords();

        entity.categories = Stream.of(categoryMapper.getAllCategoryTypes())
                .distinct()
                .collect(
                        Collectors.toMap(
                                CategoryTypeDomainObject::getName,
                                val ->
                                        categoryMapper.getCategoriesOfType(
                                                val,
                                                document.getCategoryIds()
                                        ).stream()
                                                .map(val1 -> val1 == null ? "" : val1.getName())
                                                .collect(Collectors.toList())
                                                .toArray(new String[0]))
                );

        Map<DocumentLanguage, DocumentCommonContent> contentMap = Imcms.getServices()
                .getDocumentMapper().getCommonContents(document.getId());

        for (Map.Entry<DocumentLanguage, DocumentCommonContent> entry : contentMap.entrySet()) {
            DocumentEntity.LanguageEntity languageEntity = new DocumentEntity.LanguageEntity();
            languageEntity.code = entry.getKey().getCode();
            languageEntity.enabled = true;
            languageEntity.image = entry.getValue().getMenuImageURL();
            languageEntity.menuText = entry.getValue().getMenuText();
            languageEntity.title = entry.getValue().getHeadline();
            entity.languages.put(entry.getKey().getName(), languageEntity);
        }
    }

    protected void asFileEntity(FileDocumentEntity entity, FileDocumentDomainObject document) {
        entity.file = document.getDefaultFile().getFilename();
    }

    protected void asUrlEntity(UrlDocumentEntity entity, UrlDocumentDomainObject document) {
        entity.url = document.getUrl();
    }

    protected void asTextEntity(TextDocumentEntity entity, TextDocumentDomainObject document) {
        DocumentPermissionSets documentPermissionSets = document.getPermissionSets();

        TextDocumentPermissionSetDomainObject permissionSetDomainObject1 = ((TextDocumentPermissionSetDomainObject) documentPermissionSets.getRestricted1());
        TextDocumentPermissionSetDomainObject permissionSetDomainObject2 = ((TextDocumentPermissionSetDomainObject) documentPermissionSets.getRestricted2());

        TextDocumentPermission textDocumentPermission1 = new TextDocumentPermission();
        TextDocumentPermission textDocumentPermission2 = new TextDocumentPermission();

        textDocumentPermission1.canEditImage = permissionSetDomainObject1.getEditImages();
        textDocumentPermission1.canEditLoop = permissionSetDomainObject1.getEditLoops();
        textDocumentPermission1.canEditMenu = permissionSetDomainObject1.getEditMenus();
        textDocumentPermission1.canEditText = permissionSetDomainObject1.getEditTexts();
        textDocumentPermission1.canEditDocumentInformation = permissionSetDomainObject1.getEditDocumentInformation();

        textDocumentPermission2.canEditImage = permissionSetDomainObject2.getEditImages();
        textDocumentPermission2.canEditLoop = permissionSetDomainObject2.getEditLoops();
        textDocumentPermission2.canEditMenu = permissionSetDomainObject2.getEditMenus();
        textDocumentPermission2.canEditText = permissionSetDomainObject2.getEditTexts();
        textDocumentPermission2.canEditDocumentInformation = permissionSetDomainObject2.getEditDocumentInformation();

        entity.permissions = new ArrayList<>();

        entity.permissions.add(textDocumentPermission1);
        entity.permissions.add(textDocumentPermission2);

        entity.template = document.getTemplateName();
    }


    @RequestMapping(method = RequestMethod.DELETE)
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        Imcms.getServices().getDocumentMapper().deleteDocument(Integer.parseInt(req.getPathInfo().replace("/", "")), Imcms.getUser());
        result.put("result", true);
        JSONUtils.defaultJSONAnswer(resp, result);
    }

    private static class DocumentEntity {
        public Integer id;
        public Map<String, LanguageEntity> languages;
        public String alias;
        public String target;
        public Integer status;
        public Map<Integer, Map> access;
        public Set<String> keywords;
        public Map<String, String[]> categories;

        private static class LanguageEntity {
            public String code;
            public boolean enabled;
            public String image;
            public String title;
            @JsonProperty("menu-text")
            public String menuText;
        }
    }

    private static class TextDocumentEntity extends DocumentEntity {
        public String template;
        public List<TextDocumentPermission> permissions;
    }

    private static class TextDocumentPermission {
        public boolean canEditLoop;
        public boolean canEditText;
        public boolean canEditImage;
        public boolean canEditMenu;
        public boolean canEditDocumentInformation;
    }

    private static class UrlDocumentEntity extends DocumentEntity {
        public String url;
    }

    private static class FileDocumentEntity extends DocumentEntity {
        public String file;
    }
}
