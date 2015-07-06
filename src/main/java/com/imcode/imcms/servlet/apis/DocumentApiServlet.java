package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.TextDocument;
import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentCommonContent;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.mapping.DocumentSaveException;
import com.imcode.imcms.util.JSONUtils;
import com.imcode.imcms.util.RequestUtils;
import imcode.server.Imcms;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 17.02.2015.
 */
public class DocumentApiServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            TextDocumentDomainObject documentDomainObject = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(TextDocument.TYPE_ID,
                    documentMapper.getDocument(documentMapper.getHighestDocumentId()),
                    Imcms.getUser());
            documentDomainObject.setHeadline(req.getParameter("name"));

            documentMapper.saveNewDocument(documentDomainObject, Imcms.getUser());

            result.put("result", true);
            result.put("id", documentMapper.getLowestDocumentId());
        } catch (DocumentSaveException e) {
            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(resp, result);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("id") != null) {
            Integer id = Integer.parseInt(request.getParameter("id"));
            CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();
            TextDocumentDomainObject documentDomainObject = Imcms.getServices().getDocumentMapper().getDocument(id);
            DocumentEntity result = new DocumentEntity();
            result.languages = new HashMap<>();
            result.alias = documentDomainObject.getAlias();
            result.id = id;
            result.status = documentDomainObject.getPublicationStatus().asInt();
            result.target = documentDomainObject.getTarget();
            result.template = documentDomainObject.getTemplateName();
            result.access = Stream
                    .of(documentDomainObject.getRoleIdsMappedToDocumentPermissionSetTypes().getMappings())
                    .collect(Collectors.toMap(b -> b.getRoleId().intValue(), a -> {
                        Map<String, java.io.Serializable> map = new HashMap<>();
                        map.put("permission", a.getDocumentPermissionSetType().getId());
                        map.put("role", a.getRoleId());
                        return map;
                    }));
            result.keywords = documentDomainObject.getKeywords();
            result.categories = Stream.of(categoryMapper.getAllCategoryTypes())
                    .distinct()
                    .collect(
                            Collectors.toMap(
                                    CategoryTypeDomainObject::getName,
                                    val -> {
                                        CategoryDomainObject[] categories = categoryMapper.getCategoriesOfType(
                                                val,
                                                documentDomainObject.getCategoryIds()
                                        ).toArray(new CategoryDomainObject[1]);
                                        return categories[0] != null ? categories[0].getName() : "";
                                    }
                            )
                    );
            Map<DocumentLanguage, DocumentCommonContent> contentMap = Imcms.getServices().getDocumentMapper().getCommonContents(id);
            for (Map.Entry<DocumentLanguage, DocumentCommonContent> entry : contentMap.entrySet()) {
                DocumentEntity.LanguageEntity languageEntity = new DocumentEntity.LanguageEntity();
                languageEntity.code = entry.getKey().getCode();
                languageEntity.enabled = true;
                languageEntity.image = entry.getValue().getMenuImageURL();
                languageEntity.menuText = entry.getValue().getMenuText();
                languageEntity.title = entry.getValue().getHeadline();
                result.languages.put(entry.getKey().getName(), languageEntity);
            }
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> answer = new HashMap<>();
            DocumentEntity documentEntity = new ObjectMapper()
                    .readValue(
                            RequestUtils.parse(req.getInputStream()).get("data"),
                            new TypeReference<DocumentEntity>() {
                            }
                    );
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();
            final TextDocumentDomainObject documentDomainObject =
                    documentEntity.id == null ? (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(TextDocument.TYPE_ID,
                            documentMapper.getDocument(documentMapper.getHighestDocumentId()),
                            Imcms.getUser()) : documentMapper.getDocument(documentEntity.id);
            Map<DocumentLanguage, DocumentCommonContent> contentMap = new HashMap<>();
            for (DocumentLanguage language : Imcms.getServices().getDocumentLanguages().getAll()) {
                DocumentEntity.LanguageEntity languageEntity = documentEntity.languages.get(language.getName());
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
            documentEntity.access.forEach((id, map) ->
                    documentDomainObject
                            .setDocumentPermissionSetTypeForRoleId(new RoleId(id),
                                    DocumentPermissionSetTypeDomainObject
                                            .values()[Integer.parseInt(map.get("permission").toString())]));
            documentDomainObject.setAlias(documentEntity.alias);
            documentDomainObject.setTemplateName(documentEntity.template);
            documentDomainObject.setTarget(documentEntity.target);
            documentDomainObject.setKeywords(documentEntity.keywords);
            documentDomainObject.setPublicationStatus(Document.PublicationStatus.of(documentEntity.status));
            documentDomainObject.setCategoryIds(
                    documentEntity.categories.entrySet().stream()
                            .filter(entry -> StringUtils.isNotEmpty(entry.getValue()))
                            .map(entry -> categoryMapper.getCategoryByTypeAndName(
                                    categoryMapper.getCategoryTypeByName(entry.getKey()),
                                    entry.getValue()).getId())
                            .collect(Collectors.toSet())
            );
            if (documentEntity.id != null)
                documentMapper.saveDocument(documentDomainObject, contentMap, Imcms.getUser());
            else
                documentDomainObject.setId(documentMapper.saveNewDocument(documentDomainObject, contentMap, Imcms.getUser()).getId());
            answer.put("id", documentDomainObject.getId());
            result.put("result", true);
            result.put("data", answer);
        } catch (Exception e) {
            result.put("result", false);
        }
        JSONUtils.defaultJSONAnswer(resp, result);
    }

    @Override
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
        public String template;
        public Map<Integer, Map> access;
        public Set<String> keywords;
        public Map<String, String> categories;

        private static class LanguageEntity {
            public String code;
            public boolean enabled;
            public String image;
            public String title;
            @JsonProperty("menu-text")
            public String menuText;
        }
    }
}
