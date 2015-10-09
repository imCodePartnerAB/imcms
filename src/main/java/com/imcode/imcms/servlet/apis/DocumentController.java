package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentCommonContent;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileInputStreamSource;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Realise API for working with  {@link DocumentDomainObject}
 *
 * @see RestController
 */
@RestController
@RequestMapping("/document")
public class DocumentController {
    private static final Logger LOG = Logger.getLogger(DocumentController.class);

    /**
     * Provide API access to special document
     *
     * @param id          {@link DocumentDomainObject} id
     * @param isPrototype flag that several kind of fields in result entity should be empty
     * @return {@link DocumentEntity}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public Object getDocumentById(@PathVariable("id") Integer id,
                                  @RequestParam(value = "isPrototype", required = false) boolean isPrototype) {
        DocumentDomainObject documentDomainObject = Imcms.getServices().getDocumentMapper().getDocument(id);
        DocumentEntity result;

        switch (documentDomainObject.getDocumentTypeId()) {
            case DocumentTypeDomainObject.URL_ID: {
                result = new UrlDocumentEntity();

                if (!isPrototype) {
                    asUrlEntity((UrlDocumentEntity) result, (UrlDocumentDomainObject) documentDomainObject);
                }
            }
            break;
            case DocumentTypeDomainObject.FILE_ID: {
                result = new FileDocumentEntity();

                if (!isPrototype) {
                    asFileEntity((FileDocumentEntity) result, (FileDocumentDomainObject) documentDomainObject);
                }
            }
            break;
            case DocumentTypeDomainObject.TEXT_ID:
            default: {
                result = new TextDocumentEntity();

                asTextEntity((TextDocumentEntity) result, (TextDocumentDomainObject) documentDomainObject);

                if (isPrototype) {
                    ((TextDocumentEntity) result).template = ((TextDocumentDomainObject) documentDomainObject).getDefaultTemplateName();
                }
            }
            break;
        }

        prepareEntity(result, documentDomainObject);

        if (isPrototype) {
            asPrototype(result);
        }

        return result;
    }

    /**
     * Provide API access to find special documents based on special term.
     *
     * @param term  special term represented by word, or symbols sequence. It can be document id, or keyword, header,
     *              even special word, that contained in document text content
     * @param skip  Optional parameter, that indicate count of skipped document from list. Default value = 0
     * @param take  Optional parameter, that indicate how many document should be taken. Default value = 25
     * @param sort  Optional parameter, that indicate the field field on which will be sorted
     * @param order Optional parameter, that indicate document ordering in list. By default is natural ordering
     * @return List of documents
     * @throws ServletException
     * @throws IOException
     * @see SolrQuery
     * @see DocumentIndex
     * @see DocumentIndex#search(SolrQuery, UserDomainObject)
     */
    @RequestMapping(method = RequestMethod.GET)
    protected Object getDocumentsList(@RequestParam(value = "filter", required = false) String term,
                                      @RequestParam(value = "skip", required = false, defaultValue = "0") int skip,
                                      @RequestParam(value = "take", required = false, defaultValue = "25") int take,
                                      @RequestParam(value = "sort", required = false, defaultValue = "meta_id") String sort,
                                      @RequestParam(value = "order", required = false, defaultValue = "asc") String order) throws ServletException, IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        List<DocumentDomainObject> documents;
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
        List<Integer> documentStoredFieldsList;
        SolrQuery solrQuery;

        if (StringUtils.isNotBlank(term)) {
            String query = Arrays.asList(new String[]{DocumentIndex.FIELD__META_ID, DocumentIndex.FIELD__META_HEADLINE,
                    DocumentIndex.FIELD__META_TEXT, DocumentIndex.FIELD__KEYWORD, DocumentIndex.FIELD__ALIAS,
            }).stream().map(field -> String.format("%s:*%s*", field, term)).collect(Collectors.joining(" "));
            solrQuery = new SolrQuery(query);
        } else {
            solrQuery = new SolrQuery("*:*");
        }

        solrQuery.addSort(sort, SolrQuery.ORDER.valueOf(order));
        documentStoredFieldsList = documentMapper.getDocumentIndex()
                .search(solrQuery, Imcms.getUser()).documentStoredFieldsList()
                .stream().map(DocumentStoredFields::id).collect(Collectors.toList());

        documents = documentMapper.getDocuments(documentStoredFieldsList.stream().skip(skip).limit(take).collect(Collectors.toList()));

        for (DocumentDomainObject document : documents) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("id", document.getId());
            objectMap.put("name", document.getName());
            objectMap.put("status", document.getLifeCyclePhase().toString().toUpperCase().substring(0, 1));
            objectMap.put("label", document.getHeadline());
            objectMap.put("isArchived", document.isArchived());
            objectMap.put("language", document.getLanguage().getName());
            objectMap.put("alias", document.getAlias());
            objectMap.put("type", document.getDocumentType().getName().toLocalizedString(Imcms.getUser()));
            result.add(objectMap);
        }
        return result;
    }

    /**
     * Provide API access to creating|updating document.
     * Document creating base on several parameters as document type({@link UrlDocument}, {@link FileDocument},
     * {@link TextDocument}), parent document.
     * {@link DocumentEntity} represent web object, that connect client side with server side
     *
     * @param req
     * @param type
     * @param parentDocumentId
     * @param data
     * @param file
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    protected Object createOrUpdateDocument(HttpServletRequest req,
                                            @RequestParam("type") Integer type,
                                            @RequestParam(value = "parent", defaultValue = "1001") Integer parentDocumentId,
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
                            documentMapper.getDocument(parentDocumentId),
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
                            documentMapper.getDocument(parentDocumentId),
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
                            documentMapper.getDocument(parentDocumentId),
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

    /**
     * Provide API access to create copy of special document
     *
     * @param id id of document that should be copied
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/copy")
    protected Object copyDocument(@PathVariable("id") Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> map = new HashedMap<>();
            DocumentDomainObject documentDomainObject = Imcms.getServices().getDocumentMapper().copyDocument(Imcms.getServices().getDocumentMapper().getDocument(id), Imcms.getUser());

            map.put("id", documentDomainObject.getId());
            result.put("result", true);
            result.put("data", map);
        } catch (DocumentSaveException e) {
            e.printStackTrace();
            LOG.error("Problem during document creating", e);
            result.put("result", false);
        }

        return result;
    }


    /**
     * Provide API access to several operations such as document deleting, archiving and unarchiving
     *
     * @param id     document id
     * @param action special flag, that identify type of operation
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    protected Object deleteDocument(
            @PathVariable("id") Integer id,
            @RequestParam(value = "action", required = false, defaultValue = "") String action
    ) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        switch (action) {
            case "unarchive":
            case "archive": {
                DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(id);
                document.setArchivedDatetime(action.equals("unarchive") ? null : new Date());
                try {
                    Imcms.getServices().getDocumentMapper().saveDocument(document, Imcms.getUser());
                    result.put("result", true);
                } catch (DocumentSaveException e) {
                    e.printStackTrace();
                    result.put("result", false);
                }
            }
            break;
            default: {
                Imcms.getServices().getDocumentMapper().deleteDocument(id, Imcms.getUser());
                result.put("result", true);
            }
        }
        return result;
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

    /**
     * Provide basic document preparation base on {@link DocumentEntity}
     *
     * @param documentEntity
     * @param documentDomainObject
     */
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

        documentDomainObject.setSearchDisabled(documentEntity.isSearchDisabled);
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

    /**
     * Prepare {@link FileDocumentDomainObject}
     *
     * @param document      prepared document
     * @param entity        presented entity
     * @param multipartFile file, that should be added to document
     * @throws IOException
     * @throws ServletException
     */
    protected void asFileDocument(FileDocumentDomainObject document, FileDocumentEntity entity, MultipartFile multipartFile) throws IOException, ServletException {
        if (StringUtils.isNotEmpty(entity.defaultFile)) {
            document.setDefaultFileId(entity.defaultFile);
        }

        Stream.of(entity.removedFiles).forEach(document::removeFile);

        if (multipartFile != null) {
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = new FileDocumentDomainObject.FileDocumentFile();
            fileDocumentFile.setFilename(multipartFile.getOriginalFilename());

            fileDocumentFile.setMimeType(multipartFile.getContentType());
            File file = new File(Imcms.getServices().getConfig().getFilePath(), multipartFile.getOriginalFilename());

            if (!file.createNewFile() &&
                    document.getFile(multipartFile.getOriginalFilename()) != null) {
                document.removeFile(multipartFile.getOriginalFilename());
            }

            multipartFile.transferTo(file);
            fileDocumentFile.setInputStreamSource(new FileInputStreamSource(file));
            document.addFile(multipartFile.getOriginalFilename(), fileDocumentFile);

            document.setDefaultFileId(multipartFile.getOriginalFilename());
        }

    }

    /**
     * Prepare {@link TextDocumentDomainObject} using {@link TextDocumentEntity}
     *
     * @param document current document
     * @param entity   presented entity
     */
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
        document.setDefaultTemplateId(entity.defaultTemplate);
    }

    /**
     * Prepare {@link UrlDocumentDomainObject} using {@link UrlDocumentEntity}
     *
     * @param document current document
     * @param entity   presented entity
     */
    protected void asUrlDocument(UrlDocumentDomainObject document, UrlDocumentEntity entity) {
        document.setUrl(entity.url);
    }


    /**
     * Prepare Web-API entity base on special {@link DocumentDomainObject}
     *
     * @param entity   document entity
     * @param document current document
     */
    protected void prepareEntity(DocumentEntity entity, DocumentDomainObject document) {
        CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();
        RoleGetter roleGetter = Imcms.getServices().getRoleGetter();

        entity.type = document.getDocumentTypeId();
        entity.languages = new HashMap<>();
        entity.alias = document.getAlias();
        entity.id = document.getId();
        entity.status = document.getPublicationStatus().asInt();
        entity.target = document.getTarget();
        entity.isSearchDisabled = document.isSearchDisabled();

        entity.access = Stream
                .of(document.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings())
                .collect(Collectors.toMap(b -> b.getRoleId().intValue(), a -> {
                    Map<String, java.io.Serializable> map = new HashMap<>();

                    HashMap<String, Object> role = new HashMap<>();
                    role.put("roleId", a.getRoleId().getRoleId());
                    role.put("name", roleGetter.getRole(a.getRoleId()).getName());

                    map.put("permission", a.getDocumentPermissionSetType().getId());
                    map.put("role", role);
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

    /**
     *
     *
     * @param entity
     * @param document
     */
    protected void asFileEntity(FileDocumentEntity entity, FileDocumentDomainObject document) {
        if (document.getFiles().size() > 0) {
            entity.files = document.getFiles().keySet().stream().toArray(String[]::new);
            entity.defaultFile = document.getDefaultFileId();
        }
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
        entity.defaultTemplate = document.getDefaultTemplateName();
    }

    protected void asPrototype(DocumentEntity entity) {
        entity.alias = "";
        entity.id = null;
        entity.status = Document.STATUS_NEW;
        entity.languages = new HashMap<>();
        entity.keywords = new HashSet<>();
    }


    /**
     * Web-API entity
     */
    private static class DocumentEntity {
        public Integer id;
        public Map<String, LanguageEntity> languages;
        public String alias;
        public Integer type;
        public String target;
        public Integer status;
        public Map<Integer, Map> access;
        public Set<String> keywords;
        public Map<String, String[]> categories;
        public boolean isSearchDisabled;

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
        public String defaultTemplate;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FileDocumentEntity extends DocumentEntity {
        public String[] files;
        public String[] removedFiles;
        public String defaultFile;
    }
}
