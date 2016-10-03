package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.*;
import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleGetter;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.io.FileInputStreamSource;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static imcode.util.DateConstants.DATETIME_DOC_FORMAT;

/**
 * Realise API for working with  {@link DocumentDomainObject}
 *
 * @see RestController
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/document")
public class DocumentController {

	private static final String[] DATE_TYPES = {
			"created",
			"modified",
			"archived",
			"published",
			"publication-end"
	};

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
	 * @param userId Optional parameter, that indicate id of user for which documents must be shown. By default is showing for all users
	 * @param categoriesId Optional parameter, that indicate document filtering by list of categories id. By default it's empty
	 * @return List of documents
	 * @see SolrQuery
	 * @see DocumentIndex
	 * @see DocumentIndex#search(SolrQuery, UserDomainObject)
	 */
	@RequestMapping(method = RequestMethod.GET)
	protected Object getDocumentsList(@RequestParam(value = "filter", required = false) String term,
									  @RequestParam(value = "skip", required = false, defaultValue = "0") int skip,
									  @RequestParam(value = "take", required = false, defaultValue = "50") int take,
									  @RequestParam(value = "sort", required = false, defaultValue = "meta_id") String sort,
									  @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
									  @RequestParam(value = "userId", required = false) Integer userId,
									  @RequestParam(value = "categoriesId", required = false) List<Integer> categoriesId) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<DocumentDomainObject> documents;
		DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
		List<Integer> documentStoredFieldsList;
		SolrQuery solrQuery;

        String query = (StringUtils.isNotBlank(term))
                ? Stream.of(new String[]{
                DocumentIndex.FIELD__META_ID,
                DocumentIndex.FIELD__META_HEADLINE,
                DocumentIndex.FIELD__META_TEXT,
                DocumentIndex.FIELD__KEYWORD,
                DocumentIndex.FIELD__TEXT,
                DocumentIndex.FIELD__ALIAS})
                .map(field -> String.format("%s:*%s*", field, term))
                .collect(Collectors.joining(" "))
                : "*:*";

        if (categoriesId != null) {
            query = "(" + query + ") AND (" + DocumentIndex.FIELD__CATEGORY_ID + ":(" // :)
                    + categoriesId.stream().map(Object::toString).collect(Collectors.joining(" AND ")) + "))";
        }

        solrQuery = new SolrQuery(query);
        Integer searchUserId = null;
        if (userId != null) {
            if (userId > 0) {
                searchUserId = userId;
            }
        } else {
            searchUserId = Imcms.getUser().getId();
        }

        if (searchUserId != null) {
            String userFilter = DocumentIndex.FIELD__CREATOR_ID + ":" + searchUserId;
            solrQuery.addFilterQuery(userFilter);
        }


        solrQuery.addSort(sort, SolrQuery.ORDER.valueOf(order));

		documentStoredFieldsList = documentMapper.getDocumentIndex()
				.search(solrQuery, Imcms.getUser())
				.documentStoredFieldsList()
				.stream()
				.map(DocumentStoredFields::id)
				.collect(Collectors.toList());

		documents = documentMapper.getDocuments(documentStoredFieldsList.stream()
				.skip(skip)
				.limit(take)
				.collect(Collectors.toList()));

		result.addAll(documents.stream()
				.map(document -> new HashMap<String, Object>() {
					{
						put("id", document.getId());
						put("name", document.getName());
						put("status", document.getLifeCyclePhase().toString().toUpperCase().substring(0, 1));
						put("label", document.getHeadline());
						put("isArchived", document.isArchived());
						put("language", document.getLanguage().getName());
						put("alias", document.getAlias());
						put("lastModified", Utility.formatDateTime(document.getModifiedDatetime()));
						put("type", document.getDocumentType().getName().toLocalizedString(Imcms.getUser()));
					}
				})
				.collect(Collectors.toList()));
		return result;
	}

	/**
	 * Provide API access to creating|updating document.
	 * Document creating base on several parameters as document type({@link UrlDocument}, {@link FileDocument},
	 * {@link TextDocument}), parent document.
	 * {@link DocumentEntity} represent web object, that connect client side with server side
	 *
	 */
	@RequestMapping(method = RequestMethod.POST)
	protected Object createOrUpdateDocument(@RequestParam("type") Integer type,
											@RequestParam(value = "parent", defaultValue = "1001") Integer parentDocumentId,
											@RequestParam("data") String data,
											@RequestParam(value = "file", required = false) MultipartFile[] files) {
		Map<String, Object> result = new HashMap<>();
		try {
			DocumentDomainObject docDomainObject;
			DocumentMapper docMapper = Imcms.getServices().getDocumentMapper();
			DocumentEntity docEntity;

			switch (type) {
				case DocumentTypeDomainObject.URL_ID: {
					docEntity = newMapper(data, new TypeReference<UrlDocumentEntity>() {
					});
					docDomainObject = createOrGetDoc(type, parentDocumentId, docEntity.id, docMapper);
					asUrlDocument((UrlDocumentDomainObject) docDomainObject, (UrlDocumentEntity) docEntity);
				}
				break;
				case DocumentTypeDomainObject.FILE_ID: {
					docEntity = newMapper(data, new TypeReference<FileDocumentEntity>() {
					});
					docDomainObject = createOrGetDoc(type, parentDocumentId, docEntity.id, docMapper);
					asFileDocument((FileDocumentDomainObject) docDomainObject, (FileDocumentEntity) docEntity, files);
				}
				break;
				case DocumentTypeDomainObject.TEXT_ID:
				default: {
					int id = TextDocument.TYPE_ID;
					docEntity = newMapper(data, new TypeReference<TextDocumentEntity>() {
					});
					docDomainObject = createOrGetDoc(id, parentDocumentId, docEntity.id, docMapper);
					asTextDocument((TextDocumentDomainObject) docDomainObject, (TextDocumentEntity) docEntity);
				}
				break;
			}

			prepareDocument(docEntity, docDomainObject);

			if (docEntity.id != null) {
				docMapper.saveDocument(docDomainObject, getContentMap(docEntity), Imcms.getUser());
			} else {
				docDomainObject.setId(docMapper
						.saveNewDocument(docDomainObject, getContentMap(docEntity), Imcms.getUser())
						.getId());
			}

			docEntity.id = docDomainObject.getId();
			result.put("result", true);
			result.put("data", docEntity);
		} catch (Exception e) {
			LOG.error("Problem during document creating", e);
			result.put("result", false);
		}
		return result;
	}

	private <T, R> T newMapper(String data, TypeReference<R> typeReference) throws IOException {
		return new ObjectMapper().readValue(data, typeReference);
	}

	private DocumentDomainObject createOrGetDoc(Integer typeId, Integer parentDocumentId, Integer documentEntityId, DocumentMapper documentMapper) {
		return documentEntityId == null
				? documentMapper.createDocumentOfTypeFromParent(typeId, documentMapper.getDocument(parentDocumentId), Imcms.getUser())
				: documentMapper.getDocument(documentEntityId);
	}

    /**
     * Gets Map with created, modified, archived, published and publish-end
     * -date, -time and -user-by for specified document.
     *
     * @param id - document id
     * @return - Map with created, modified, archived, publish-start and
     * publish-end -date, -time and -user-by.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/getDateTimes/{id}")
	protected Object getDateTimes(@PathVariable(value = "id") int id,
                                  HttpServletRequest request) {

		Map<String, Object> map = new HashedMap<>();
		DocumentDomainObject doc = Imcms.getServices().getDocumentMapper().getDocument(id);

		Date[] dates = doc.getArrDates();
        String[] byUsers = doc.getByUsersArr(ContentManagementSystem.fromRequest(request).getUserService());

		for (int i = 0; i < DATE_TYPES.length; i++) {
		    String userBy = byUsers[i];
			String[] dateTimeBy = Utility.formatDateTime(dates[i]).split(" ");
			map.put(DATE_TYPES[i], new HashedMap<String, Object>() {{
				put("date", dateTimeBy[0]);
				put("time", dateTimeBy[1]);
                put("by", userBy);
			}});
		}
		return map;
	}

	/**
	 * Provide API access to create copy of special document
	 *
	 * @param id id of document that should be copied
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/copy")
	protected Object copyDocument(@PathVariable("id") Integer id) {
		Map<String, Object> result = new HashMap<>();

		try {
            DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
            DocumentDomainObject copyDocument = documentMapper.copyDocument(documentMapper.getDocument(id), Imcms.getUser());
            int docId = copyDocument.getId();
            String label = copyDocument.getHeadline();

			result.put("result", true);
			result.put("data", new HashedMap<String, Object>(){{
                put("id", docId);
                put("label", label);
            }});
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
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	protected Object deleteDocument(@PathVariable("id") Integer id,
									@RequestParam(value = "action", required = false, defaultValue = "") String action) {
		Map<String, Object> result = new HashMap<>();
        boolean resultValue;

		switch (action) {
			case "unarchive":
			case "archive": {
				DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(id);
				document.setArchivedDatetime(action.equals("unarchive") ? null : new Date());
                document.setArchiverId(Imcms.getUser().getId());
				try {
					Imcms.getServices().getDocumentMapper().saveDocument(document, Imcms.getUser());
					resultValue = true;
				} catch (DocumentSaveException e) {
					e.printStackTrace();
					resultValue = false;
				}
			}
			break;
			default: {
				Imcms.getServices().getDocumentMapper().deleteDocument(id);
				resultValue = true;
			}
		}
        result.put("result", resultValue);
		return result;
	}

	protected Map<DocumentLanguage, DocumentCommonContent> getContentMap(DocumentEntity entity) {
		Map<DocumentLanguage, DocumentCommonContent> contentMap = new HashMap<>();

		for (DocumentLanguage language : Imcms.getServices().getDocumentLanguages().getAll()) {
			DocumentEntity.LanguageEntity languageEntity = entity.languages.get(language.getName());

            contentMap.put(language, DocumentCommonContent.builder()
                    .headline(languageEntity.title)
                    .menuImageURL(languageEntity.image)
                    .menuText(languageEntity.menuText)
                    .enabled(languageEntity.enabled)
                    .build()
            );
        }
		return contentMap;
	}

	/**
	 * Provide basic document preparation base on {@link DocumentEntity}
	 */
	protected void prepareDocument(DocumentEntity docEntity, DocumentDomainObject docDomainObject) {
		CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();

        docEntity.access.forEach((id, map) -> {
            int permission = Integer.parseInt(map.get("permission").toString());
            DocumentPermissionSetTypeDomainObject permissionSetType = DocumentPermissionSetTypeDomainObject.values()[permission];
            docDomainObject.setDocumentPermissionSetTypeForRoleId(new RoleId(id), permissionSetType);
        });

        String alias = StringUtils.isNotEmpty(docEntity.alias)
                ? docEntity.alias
                : null;

        docDomainObject.setAlias(alias);
		docDomainObject.setSearchDisabled(docEntity.isSearchDisabled);
		docDomainObject.setTarget(docEntity.target);
		docDomainObject.setKeywords(docEntity.keywords);
		docDomainObject.setPublicationStatus(Document.PublicationStatus.of(docEntity.status));
		docDomainObject.setCategoryIds(docEntity.categories
                .entrySet()
                .stream()
                .filter(entry -> Arrays.stream(entry.getValue())
                        .anyMatch(StringUtils::isNotEmpty)
                )
                .map(entry -> Arrays.stream(entry.getValue())
                        .map(item -> {
                            CategoryTypeDomainObject categoryType = categoryMapper.getCategoryTypeByName(entry.getKey());
                            return categoryMapper.getCategoryByTypeAndName(categoryType, item).getId();
                        })
                        .collect(Collectors.toCollection(ArrayList::new))
                )
                .flatMap(List::stream)
                .collect(Collectors.toSet())
        );

        // todo: too much similar code, rethink
        {
            Date publishedDate = getValidDateOrNull(docEntity.publishedDate, docEntity.publishedTime,
                    docDomainObject.getPublicationStartDatetime());

            Date archivedDate = getValidDateOrNull(docEntity.archivedDate, docEntity.archivedTime,
                    docDomainObject.getArchivedDatetime());

            Date publicationEndDate = getValidDateOrNull(docEntity.publicationEndDate, docEntity.publicationEndTime,
                    docDomainObject.getPublicationEndDatetime());

            if (!isValidDateTime(docEntity.publishedDate, docEntity.publishedTime)) {
                docDomainObject.setPublicationStartDatetime(null);
                docDomainObject.setPublisherId(null);

            } else {
                Optional.ofNullable(publishedDate)
                        .ifPresent(newPublishedDate -> {
                            docDomainObject.setPublicationStartDatetime(newPublishedDate);
                            docDomainObject.setPublisherId(Imcms.getUser().getId());
                        });
            }

            if (!isValidDateTime(docEntity.archivedDate, docEntity.archivedTime)) {
                docDomainObject.setArchivedDatetime(null);
                docDomainObject.setArchiverId(null);

            } else {
                Optional.ofNullable(archivedDate)
                        .ifPresent(newArchivedDate -> {
                            docDomainObject.setArchivedDatetime(newArchivedDate);
                            docDomainObject.setArchiverId(Imcms.getUser().getId());
                        });
            }

            if (!isValidDateTime(docEntity.publicationEndDate, docEntity.publicationEndTime)) {
                docDomainObject.setPublicationEndDatetime(null);
                docDomainObject.setDepublisherId(null);

            } else {
                Optional.ofNullable(publicationEndDate)
                        .ifPresent(newPublicationEndDate -> {
                            docDomainObject.setPublicationEndDatetime(newPublicationEndDate);
                            docDomainObject.setDepublisherId(Imcms.getUser().getId());
                        });
            }
        }

        // in case of new doc with specified publisher without publication start date/time
        Optional.ofNullable(docEntity.publisherId).ifPresent(docDomainObject::setPublisherId);
        Optional.ofNullable(docEntity.missingLangProp).ifPresent(docDomainObject::setDisabledLanguageShowMode);
    }

    private Date getValidDateOrNull(String date, String time, Date documentDatetime) {
        if (isValidDateTime(date, time)) {
            try {
                String newDateStr = date + " " + time;
                Date newDate = DATETIME_DOC_FORMAT.parse(newDateStr);
                Date oldDate = null;

                if (documentDatetime != null) {
                    String oldDateStr = DATETIME_DOC_FORMAT.format(documentDatetime);
                    oldDate = DATETIME_DOC_FORMAT.parse(oldDateStr);
                }

                if (!newDate.equals(oldDate)) {
                    return newDate;
                }
            } catch (ParseException ignore) {
            }
        }
        return null;
    }

    private boolean isValidDateTime(String publishedDate, String publishedTime) {
        return StringUtils.isNotBlank(publishedDate)
                && !publishedDate.equals("--")
                && StringUtils.isNotBlank(publishedTime)
                && !publishedTime.equals("--");
    }

    /**
	 * Prepare {@link FileDocumentDomainObject}
	 *
	 * @param document      prepared document
	 * @param entity        presented entity
	 * @param multipartFiles file array, that should be added to document
	 * @throws IOException - if an I/O error occurred
	 */
    protected void asFileDocument(FileDocumentDomainObject document, FileDocumentEntity entity, MultipartFile[] multipartFiles) throws IOException {
        if (StringUtils.isNotEmpty(entity.defaultFile)) {
            document.setDefaultFileId(entity.defaultFile);
        }

        Stream.of(entity.removedFiles).forEach(document::removeFile);

		entity.editedFiles.forEach((k, v) -> document.changeFileId(k, v));

		if (multipartFiles != null) {
			for (MultipartFile multipartFile : multipartFiles) {
				FileDocumentDomainObject.FileDocumentFile fileDocumentFile = new FileDocumentDomainObject.FileDocumentFile();
				String originalFilename = multipartFile.getOriginalFilename();
				fileDocumentFile.setFilename(originalFilename);

				fileDocumentFile.setMimeType(multipartFile.getContentType());
				File file = new File(Imcms.getServices().getConfig().getFilePath(), originalFilename);

				if (!file.createNewFile() && document.getFile(originalFilename) != null) {
					document.removeFile(originalFilename);
				}

				multipartFile.transferTo(file);
				originalFilename = multipartFile.getOriginalFilename();
				fileDocumentFile.setInputStreamSource(new FileInputStreamSource(file));
				document.addFile(originalFilename, fileDocumentFile);

				document.setDefaultFileId(originalFilename);
			}
		}
	}

	/**
	 * Prepare {@link TextDocumentDomainObject} using {@link TextDocumentEntity}
	 *
	 * @param document current document
	 * @param entity   presented entity
	 */
	protected void asTextDocument(TextDocumentDomainObject document, TextDocumentEntity entity) {
		TextDocumentPermissionSetDomainObject permissions1 = new TextDocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
		TextDocumentPermissionSetDomainObject permissions2 = new TextDocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_2);
		DocumentPermissionSets docPermissions = new DocumentPermissionSets();

        TextDocumentPermission textDocPermission0 = entity.permissions.get(0);
        permissions1.setEditImages(textDocPermission0.canEditImage);
		permissions1.setEditMenus(textDocPermission0.canEditMenu);
		permissions1.setEditTexts(textDocPermission0.canEditText);
		permissions1.setEditLoops(textDocPermission0.canEditLoop);
		permissions1.setEditDocumentInformation(textDocPermission0.canEditDocumentInformation);
		permissions1.setEditPermissions(textDocPermission0.canEditDocumentInformation);

        TextDocumentPermission textDocPermission1 = entity.permissions.get(1);
        permissions2.setEditImages(textDocPermission1.canEditImage);
		permissions2.setEditMenus(textDocPermission1.canEditMenu);
		permissions2.setEditTexts(textDocPermission1.canEditText);
		permissions2.setEditLoops(textDocPermission1.canEditLoop);
		permissions2.setEditDocumentInformation(textDocPermission1.canEditDocumentInformation);
		permissions2.setEditPermissions(textDocPermission1.canEditDocumentInformation);

		docPermissions.setRestricted1(permissions1);
		docPermissions.setRestricted2(permissions2);

		document.setPermissionSets(docPermissions);
		document.setPermissionSetsForNewDocument(docPermissions);

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

        entity.docVersion = Imcms.getServices()
                .getDocumentMapper()
                .getDocumentVersionInfo(document.getId())
                .getLatestVersion();

        entity.missingLangProp = document.getDisabledLanguageShowMode().name();
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
				.collect(Collectors.toMap(CategoryTypeDomainObject::getName,
						val -> categoryMapper.getCategoriesOfType(val, document.getCategoryIds())
								.stream()
								.map(val1 -> val1 == null ? "" : val1.getName())
								.collect(Collectors.toList())
								.toArray(new String[0]))
				);

		Map<DocumentLanguage, DocumentCommonContent> contentMap = Imcms.getServices()
				.getDocumentMapper().getCommonContents(document.getId());

		for (Map.Entry<DocumentLanguage, DocumentCommonContent> entry : contentMap.entrySet()) {
			DocumentEntity.LanguageEntity languageEntity = new DocumentEntity.LanguageEntity();
            DocumentLanguage language = entry.getKey();
            DocumentCommonContent commonContent = entry.getValue();

            languageEntity.code = language.getCode();
			languageEntity.enabled = commonContent.getEnabled();
            languageEntity.image = commonContent.getMenuImageURL();
			languageEntity.menuText = commonContent.getMenuText();
			languageEntity.title = commonContent.getHeadline();
			entity.languages.put(language.getName(), languageEntity);
		}
	}

	protected void asFileEntity(FileDocumentEntity entity, FileDocumentDomainObject document) {
        if (document.getFiles().size() > 0) {
            entity.files = document.getFiles().entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getValue().getId(), e -> e.getValue().getFilename()));
            entity.defaultFile = document.getDefaultFileId();
        }
    }

	protected void asUrlEntity(UrlDocumentEntity entity, UrlDocumentDomainObject document) {
		entity.url = document.getUrl();
	}

	protected void asTextEntity(TextDocumentEntity entity, TextDocumentDomainObject document) {
		DocumentPermissionSets docPermissions = document.getPermissionSets();

		TextDocumentPermissionSetDomainObject permissions1 = ((TextDocumentPermissionSetDomainObject) docPermissions.getRestricted1());
		TextDocumentPermissionSetDomainObject permissions2 = ((TextDocumentPermissionSetDomainObject) docPermissions.getRestricted2());

		TextDocumentPermission textDocPermission1 = new TextDocumentPermission();
		TextDocumentPermission textDocPermission2 = new TextDocumentPermission();

		textDocPermission1.canEditImage = permissions1.getEditImages();
		textDocPermission1.canEditLoop = permissions1.getEditLoops();
		textDocPermission1.canEditMenu = permissions1.getEditMenus();
		textDocPermission1.canEditText = permissions1.getEditTexts();
		textDocPermission1.canEditDocumentInformation = permissions1.getEditDocumentInformation();

		textDocPermission2.canEditImage = permissions2.getEditImages();
		textDocPermission2.canEditLoop = permissions2.getEditLoops();
		textDocPermission2.canEditMenu = permissions2.getEditMenus();
		textDocPermission2.canEditText = permissions2.getEditTexts();
		textDocPermission2.canEditDocumentInformation = permissions2.getEditDocumentInformation();

		entity.permissions = new ArrayList<>();

		entity.permissions.add(textDocPermission1);
		entity.permissions.add(textDocPermission2);

		entity.template = document.getTemplateName();
		entity.defaultTemplate = document.getDefaultTemplateName();
	}

	protected void asPrototype(DocumentEntity entity) {
		entity.alias = "";
		entity.id = null;
		entity.status = Document.PublicationStatus.NEW.asInt();
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

        @JsonProperty("published-date")
        public String publishedDate;

        @JsonProperty("published-time")
        public String publishedTime;

        @JsonProperty("archived-date")
        public String archivedDate;

        @JsonProperty("archived-time")
        public String archivedTime;

        @JsonProperty("publication-end-date")
        public String publicationEndDate;

        @JsonProperty("publication-end-time")
        public String publicationEndTime;

        @JsonProperty("publisher")
        public Integer publisherId;

        @JsonProperty("missing-lang-prop")
        public String missingLangProp;

        public DocumentVersion docVersion;

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
		public Map<String, String> files;
		public Map<String, String> editedFiles;
		public String[] removedFiles;
		public String defaultFile;
	}
}
