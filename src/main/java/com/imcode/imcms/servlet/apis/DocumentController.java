package com.imcode.imcms.servlet.apis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import imcode.util.Utility;
import imcode.util.io.FileInputStreamSource;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
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

	private static final String[] DATE_TYPES = {
			"created",
			"modified",
			"archived",
			"published",
			"publication-end"
	};

	private static final Collection<String> WRONG_DATE = Collections.unmodifiableCollection(Arrays.asList("T:00Z", "--T--:00Z"));

	private static final String BAD_ATTRIBUTES = ",\"created-date\":\"\",\"created-time\":\"\",\"created-by\":\"\",\"modified-date\":\"\",\"modified-time\":\"\",\"modified-by\":\"\",\"archived-date\":\"\",\"archived-time\":\"\",\"archived-by\":\"\",\"published-date\":\"\",\"published-time\":\"\",\"published-by\":\"\",\"publication-end-date\":\"\",\"publication-end-time\":\"\",\"publication-end-by\":\"\"";

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
			String query = Stream.of(new String[]{
					DocumentIndex.FIELD__META_ID,
					DocumentIndex.FIELD__META_HEADLINE,
					DocumentIndex.FIELD__META_TEXT,
					DocumentIndex.FIELD__KEYWORD,
					DocumentIndex.FIELD__ALIAS,})
					.map(field -> String.format("%s:*%s*", field, term))
					.collect(Collectors.joining(" "));
			solrQuery = new SolrQuery(query);
		} else {
			solrQuery = new SolrQuery("*:*");
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
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.POST)
	protected Object createOrUpdateDocument(@RequestParam("type") Integer type,
											@RequestParam(value = "parent", defaultValue = "1001") Integer parentDocumentId,
											@RequestParam("data") String data,
											@RequestParam(value = "file", required = false) MultipartFile file) throws ServletException, IOException {
		Map<String, Object> result = new HashMap<>();
		try {
			DocumentDomainObject docDomainObject;

			DocumentMapper docMapper = Imcms.getServices().getDocumentMapper();
			DocumentEntity docEntity;

			//before do smth needs to check data to replace wrong attributes
			//todo: check data on JS side and send correct data to here, then may delete the checkData(data) method
			data = checkData(data);

			switch (type) {
				case DocumentTypeDomainObject.URL_ID: {
					docEntity = newMapper(data, new TypeReference<UrlDocumentEntity>() {
					});
					docDomainObject = createOrGetDoc(type, parentDocumentId, docEntity, docMapper);
					asUrlDocument((UrlDocumentDomainObject) docDomainObject, (UrlDocumentEntity) docEntity);
				}
				break;
				case DocumentTypeDomainObject.FILE_ID: {
					docEntity = newMapper(data, new TypeReference<FileDocumentEntity>() {
					});
					docDomainObject = createOrGetDoc(type, parentDocumentId, docEntity, docMapper);
					asFileDocument((FileDocumentDomainObject) docDomainObject, (FileDocumentEntity) docEntity, file);
				}
				break;
				case DocumentTypeDomainObject.TEXT_ID:
				default: {
					int id = TextDocument.TYPE_ID;
					docEntity = newMapper(data, new TypeReference<TextDocumentEntity>() {
					});
					docDomainObject = createOrGetDoc(id, parentDocumentId, docEntity, docMapper);
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

	private DocumentDomainObject createOrGetDoc(Integer typeId, Integer parentDocumentId, DocumentEntity documentEntity, DocumentMapper documentMapper) {
		return documentEntity.id == null
				? documentMapper.createDocumentOfTypeFromParent(typeId, documentMapper.getDocument(parentDocumentId), Imcms.getUser())
				: documentMapper.getDocument(documentEntity.id);
	}

	protected String checkData(String data) {
		return data.replace(BAD_ATTRIBUTES, "");
	}

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

//	@RequestMapping(method = RequestMethod.POST, value = "/dateTimes/null")
//	protected Object nullDocDateTimes() {
//		Map<String, Object> result = new HashMap<>();
//		return result.put("result", true);
//	}
//
//	@RequestMapping(method = RequestMethod.POST, value = "/dateTimes/{id}")
//	protected Object changeDateTimes(@PathVariable(value = "id") Integer id,
//									 @RequestParam(value = "created", defaultValue = "") String created,
//									 @RequestParam(value = "modified", defaultValue = "") String modified,
//									 @RequestParam(value = "archived", defaultValue = "") String archived,
//									 @RequestParam(value = "published", defaultValue = "") String published,
//									 @RequestParam(value = "publication-end", defaultValue = "") String publicationEnd) {
//
//		//	/dateTimes/{id}?created=2010-08-08T10:10:00Z&modified=.......
//		Map<String, Object> result = new HashMap<>();
//
//		if (null == id) {
//			return result.put("result", true);
//		}
//		DocumentDomainObject doc = Imcms.getServices().getDocumentMapper().getDocument(id);
//
//		String[] dates = {
//				created,
//				modified,
//				archived,
//				published,
//				publicationEnd
//		};
//
//		try {
//			handleDateTime(doc, dates);
//			Imcms.getServices().getDocumentMapper().saveDocument(doc, Imcms.getUser());
//			result.put("result", true);
//		} catch (DocumentSaveException e) {
//			e.printStackTrace();
//			LOG.error("Problem during date and time changing", e);
//			result.put("result", false);
//		} catch (IOException e) {
//			//all right, just don't need to save now
//			result.put("result", true);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.put("result", true);
//		}
//		return result;
//	}
//
//	private void handleDateTime(DocumentDomainObject doc, String[] dates) throws IOException {
//		LinkedList<Date> datesList = new LinkedList<>();
//		for (String date : dates) {
//			datesList.add(parseDate(date));
//		}
//
//		if (datesList.getFirst() == null) {
//			throw new IOException();
//			//means first date-time - date and time of document's creation - may not be null, so it
//			// seems that we have smth wrong and in this case we don't need to rewrite dates, just ignore
//		}
//
//		// full stack of dates - 5
//		if (5 == datesList.size()) {
//			ListIterator<Date> iter = datesList.listIterator();
//			doc.setCreatedDatetime(iter.next());
//			doc.setModifiedDatetime(iter.next());
//			doc.setArchivedDatetime(iter.next());
//			doc.setPublicationStartDatetime(iter.next());
//			doc.setPublicationEndDatetime(iter.next());
//		}
//	}
//
//	private Date parseDate(String date) {
//		return WRONG_DATE.contains(date) ? null : DateUtils.addHours(Date.from(Instant.parse(date)), -2);
//	}

	/**
	 * Provide API access to create copy of special document
	 *
	 * @param id id of document that should be copied
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/copy")
	protected Object copyDocument(@PathVariable("id") Integer id) {
		Map<String, Object> result = new HashMap<>();

		try {
			Map<String, Object> map = new HashedMap<>();
			DocumentDomainObject documentDomainObject = Imcms.getServices().getDocumentMapper().copyDocument(
					Imcms.getServices()
							.getDocumentMapper()
							.getDocument(id), Imcms.getUser()
			);

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
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	protected Object deleteDocument(@PathVariable("id") Integer id,
									@RequestParam(value = "action", required = false, defaultValue = "") String action
	) throws ServletException, IOException {
		Map<String, Object> result = new HashMap<>();
		switch (action) {
			case "unarchive":
			case "archive": {
				DocumentDomainObject document = Imcms.getServices().getDocumentMapper().getDocument(id);
				document.setArchivedDatetime(action.equals("unarchive") ? null : new Date());
                document.setArchiverId(Imcms.getUser().getId());
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
				Imcms.getServices().getDocumentMapper().deleteDocument(id);
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
				contentMap.put(language, DocumentCommonContent.builder()
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
	 */
	protected void prepareDocument(DocumentEntity documentEntity, DocumentDomainObject documentDomainObject) {
		CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();

		documentEntity.access.forEach(
				(id, map) -> documentDomainObject.setDocumentPermissionSetTypeForRoleId(
						new RoleId(id), DocumentPermissionSetTypeDomainObject.values()[Integer.parseInt(map.get("permission").toString())]));

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
		TextDocumentPermissionSetDomainObject permissions1 = new TextDocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
		TextDocumentPermissionSetDomainObject permissions2 = new TextDocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_2);
		DocumentPermissionSets docPermissions = new DocumentPermissionSets();

		permissions1.setEditImages(entity.permissions.get(0).canEditImage);
		permissions1.setEditMenus(entity.permissions.get(0).canEditMenu);
		permissions1.setEditTexts(entity.permissions.get(0).canEditText);
		permissions1.setEditLoops(entity.permissions.get(0).canEditLoop);
		permissions1.setEditDocumentInformation(entity.permissions.get(0).canEditDocumentInformation);
		permissions1.setEditPermissions(entity.permissions.get(0).canEditDocumentInformation);

		permissions2.setEditImages(entity.permissions.get(1).canEditImage);
		permissions2.setEditMenus(entity.permissions.get(1).canEditMenu);
		permissions2.setEditTexts(entity.permissions.get(1).canEditText);
		permissions2.setEditLoops(entity.permissions.get(1).canEditLoop);
		permissions2.setEditDocumentInformation(entity.permissions.get(1).canEditDocumentInformation);
		permissions2.setEditPermissions(entity.permissions.get(1).canEditDocumentInformation);

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
			languageEntity.code = entry.getKey().getCode();
			languageEntity.enabled = true;
			languageEntity.image = entry.getValue().getMenuImageURL();
			languageEntity.menuText = entry.getValue().getMenuText();
			languageEntity.title = entry.getValue().getHeadline();
			entity.languages.put(entry.getKey().getName(), languageEntity);
		}
	}

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
