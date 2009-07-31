package com.imcode.imcms.web.admin.controller;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentComparator;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.LifeCyclePhase;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.SimpleDocumentQuery;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.lucene.search.BooleanQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.flow.EditDocumentInformationPageFlow;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.web.admin.AdminPageController;
import com.imcode.imcms.web.admin.DocumentSearchQuery;
import com.imcode.imcms.web.admin.FieldDateRange;
import com.imcode.imcms.web.admin.IntegerRange;
import com.imcode.imcms.web.admin.NameValuePair;
import com.imcode.imcms.web.admin.PresetDateRange;
import com.imcode.imcms.web.admin.AdminPageController.AdminModul;
import com.imcode.imcms.web.admin.DocumentSearchQuery.Relationship;
import com.imcode.imcms.web.admin.command.SearchDocumentsCommand;
import com.imcode.imcms.web.admin.command.SearchDocumentsFoldCommand;
import com.imcode.imcms.web.admin.command.SearchDocumentsCommand.SearchType;


@Controller
public class SearchController {
	private static final String FOLD_KEY = SearchController.class.getName() + ".fold";
	private static final String FOUND_META_IDS_KEY = SearchController.class.getName() + ".metaIds";
	
	@Autowired
	private MessageSource messageSource;
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder, Locale locale) {
		String pattern = messageSource.getMessage("admin/search/preset_date_pattern", null, locale);
		DateFormat dateFormat = new SimpleDateFormat(pattern, locale);
		
		CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
		
		binder.registerCustomEditor(Date.class, dateEditor);
	}
	
	@RequestMapping("/search")
	@SuppressWarnings("unchecked")
	public String indexHandler(
			@ModelAttribute("search") SearchDocumentsCommand command, 
			BindingResult errors, 
			SearchDocumentsFoldCommand fold, 
			HttpServletRequest request, 
			HttpServletResponse response, 
			HttpSession session, 
			ModelMap model, 
			Locale locale) {
		
		ImcmsServices services = Imcms.getServices();
		
		if (command.isAction()) {
			setFold(session, fold);
		} else {
			fold = getFold(session);
		}
		
		if (command.isClear()) {
			command.clear();
			setFoundMetaIds(session, null);
			
		} else if (command.isSearch()) {
			DocumentSearchQuery searchQuery = commandToQuery(command);
			Set<Integer> foundMetaIds = getFoundMetaIds(session);
			
			SearchType searchType = command.getSearchType();
			
			if (searchType == SearchType.SEARCH_IN_SELECTION) {
				if (!foundMetaIds.isEmpty()) {
					searchQuery.searchInPreviousResults(foundMetaIds);
					
				} else { 
					searchQuery = new DocumentSearchQuery();
					
				}
			}
			
			BooleanQuery luceneQuery = searchQuery.toLuceneQuery();
			
			if (searchType == SearchType.SEARCH) {
				if (!foundMetaIds.isEmpty()) {
					luceneQuery = DocumentSearchQuery.includePreviousResults(luceneQuery, foundMetaIds);
				}
			}
			
			
			if (luceneQuery.getClauses().length != 0) {
				SimpleDocumentQuery query = new SimpleDocumentQuery(luceneQuery);
				
				DocumentIndex documentIndex = services.getDocumentMapper().getDocumentIndex();
				List<DocumentDomainObject> documents = documentIndex.search(query, Utility.getLoggedOnUser(request));
				
				if (searchType == SearchType.ADD_TO_SELECTION) {
					for (DocumentDomainObject document : documents) {
						foundMetaIds.add(document.getId());
					}
					setFoundMetaIds(session, foundMetaIds);
				}
				
				Collections.sort(documents, DocumentComparator.HEADLINE);
				model.put("documents", documents);
			}
		}
		
		if (!command.isSearch()) {
			IntRange documentIdRange = services.getDocumentMapper().getDocumentIdRange();
			
			IntegerRange metaRange = command.getMetaRange();
			metaRange.setFrom(documentIdRange.getMinimumInteger());
			metaRange.setTo(documentIdRange.getMaximumInteger());
		}
		
		ImcmsAuthenticatorAndUserAndRoleMapper authMapper = services.getImcmsAuthenticatorAndUserAndRoleMapper();
		UserDomainObject[] users = authMapper.getUsers(false, false);
		
		// split creators, publishers and categories into two -- not selected and selected
		addSelectedUsers(users, command.getCreators(), model, "creators", "searchCreators");
		addSelectedUsers(users, command.getPublishers(), model, "publishers", "searchPublishers");
		addSelectedCategories(command.getCategories(), model, "categories", "searchCategories");
		
		model.put("roles", authMapper.getAllRoles());
		model.put("templates", services.getTemplateMapper().getAllTemplates());
		model.put("presetDateRanges", PresetDateRange.getStandardPresets(locale));
		model.put("fold", fold);
		
		
		String view = null;
		
		AdminPageController apc = new AdminPageController(model);
		apc.setLocale(locale);
		
		if (command.isSearch()) {
			apc.setAdminModul(AdminModul.SEARCH_RESULTS);
			view = apc.getPartialPage();
		} else if (command.isClear()) {
			apc.setAdminModul(AdminModul.SEARCH_FORM);
			view = apc.getPartialPage();
		} else {
			apc.setAdminModul(AdminModul.SEARCH_FORM);
			view = apc.getAdminPage();
		}
		
		return view;
	}
	
	@RequestMapping("/search/edit")
	public String editDocumentInfoHandler(
	        @RequestParam("meta_id") int metaId, 
	        HttpServletRequest request, 
	        HttpServletResponse response) throws ServletException, IOException {
	    
	    DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
	    
	    DocumentDomainObject document = documentMapper.getDocument(metaId);
	    if (document == null) {
	        return "redirect:/newadmin/search";
	    }
	    
	    DispatchCommand returnCommand = new DispatchCommand() {
            private static final long serialVersionUID = 4089854289866507881L;
            
            public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.sendRedirect(request.getContextPath() + "/newadmin/search");
            }
        };
        
        EditDocumentInformationPageFlow editDocumentInformationPageFlow = new EditDocumentInformationPageFlow(document, returnCommand, new DocumentMapper.SaveEditedDocumentCommand());
        editDocumentInformationPageFlow.setAdminButtonsHidden(true);
        editDocumentInformationPageFlow.dispatch(request, response);
	    
	    return null;
	}
	
	private DocumentSearchQuery commandToQuery(SearchDocumentsCommand command) {
		DocumentSearchQuery searchQuery = new DocumentSearchQuery();
		
		if (command.getCreators() != null) {
			searchQuery.creators(command.getCreators());
		}
		
		if (command.getPublishers() != null) {
			searchQuery.publishers(command.getPublishers());
		}
		
		if (command.getCategories() != null) {
			searchQuery.categories(command.getCategories());
		}
		
		if (!StringUtils.isEmpty(command.getTemplate())) {
			searchQuery.template(command.getTemplate());
		}
		
		if (command.getRole() > SearchDocumentsCommand.NOT_SELECTED) {
			searchQuery.role(command.getRole(), command.isRoleNotOnPage());
		}
		
		List<LifeCyclePhase> phases = new ArrayList<LifeCyclePhase>();
		if (command.isStatusNew()) {
			phases.add(LifeCyclePhase.NEW);
		}
		
		if (command.isStatusApproved()) {
			phases.add(LifeCyclePhase.APPROVED);
		}
		
		if (command.isStatusPublished()) {
			phases.add(LifeCyclePhase.PUBLISHED);
		}
		
		if (command.isStatusArchived()) {
			phases.add(LifeCyclePhase.ARCHIVED);
		}
		
		if (command.isStatusExpired()) {
			phases.add(LifeCyclePhase.UNPUBLISHED);
		}
		
		if (command.isStatusPublishingDenied()) {
			phases.add(LifeCyclePhase.DISAPPROVED);
		}
		
		searchQuery.lifecyclePhases(phases);
		
		List<FieldDateRange> dateRanges = new ArrayList<FieldDateRange>();
		if (command.getCreationRange().isSet()) {
			dateRanges.add(command.getCreationRange());
		}
		
		if (command.getChangeRange().isSet()) {
			dateRanges.add(command.getChangeRange());
		}
		
		if (command.getPublishingRange().isSet()) {
			dateRanges.add(command.getPublishingRange());
		}
		
		if (command.getArchivedRange().isSet()) {
			dateRanges.add(command.getArchivedRange());
		}
		
		if (command.getExpiredRange().isSet()) {
			dateRanges.add(command.getExpiredRange());
		}
		
		searchQuery.dateRanges(dateRanges);
		
		List<Relationship> relationships = new ArrayList<Relationship>();
		if (command.isParents()) {
			relationships.add(Relationship.PARENTS);
		}
		
		if (command.isNoParents()) {
			relationships.add(Relationship.NO_PARENTS);
		}
		
		if (command.isChildren()) {
			relationships.add(Relationship.CHILDREN);
		}
		
		if (command.isNoChildren()) {
			relationships.add(Relationship.NO_CHILDREN);
		}
		
		searchQuery.relationships(relationships);
		
		DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();
		
		IntegerRange metaRange = command.getMetaRange();
		if (metaRange.isSet()) {
			IntRange documentIdRange = documentMapper.getDocumentIdRange();
			
			boolean defaultRange = true;
			
			Integer from = metaRange.getFrom();
			Integer to = metaRange.getTo();
			int defaultFrom = documentIdRange.getMinimumInteger();
			int defaultTo = documentIdRange.getMaximumInteger();
			
			if (from != null && !from.equals(defaultFrom)) {
				defaultRange = false;
				
			} else if (to != null && !to.equals(defaultTo)) {
				defaultRange = false;
				
			}
			
			if (!defaultRange) {
				searchQuery.metaRange(command.getMetaRange());
			}
		}
		
		if (!StringUtils.isEmpty(command.getText())) {
			searchQuery.text(command.getText());
		}
		
		String alias = StringUtils.trimToEmpty(command.getAlias());
		if (alias.length() != 0) {
			searchQuery.alias(alias);
		}
		
		return searchQuery;
	}
	
	private static void addSelectedUsers(UserDomainObject[] users, int[] selectedUserIds, 
			ModelMap model, String notSelectedKey, String selectedKey) {
		
		List<NameValuePair> notSelectedUsers = new ArrayList<NameValuePair>(users.length);
		List<NameValuePair> selectedUsers = new ArrayList<NameValuePair>();
		
		if (selectedUserIds == null) {
			for (UserDomainObject user : users) {
				NameValuePair pair = new NameValuePair(user.getFullName(), user.getId());
				notSelectedUsers.add(pair);
			}
			
		} else {
			for (UserDomainObject user : users) {
				NameValuePair pair = new NameValuePair(user.getFullName(), user.getId());
				boolean found = false;
				
				for (int selectedUserId : selectedUserIds) {
					if (selectedUserId == user.getId()) {
						selectedUsers.add(pair);
						found = true;
						
						break;
					}
				}
				
				if (!found) {
					notSelectedUsers.add(pair);
				}
			}
			
		}
		
		model.put(notSelectedKey, notSelectedUsers);
		model.put(selectedKey, selectedUsers);
	}
	
	private static void addSelectedCategories(int[] selectedCategoryIds, ModelMap model, 
			String notSelectedKey, String selectedKey) {
		
		List<CategoryDomainObject> categories = Imcms.getServices().getCategoryMapper().getAllCategories();
		
		List<NameValuePair> notSelectedCategories = new ArrayList<NameValuePair>(categories.size());
		List<NameValuePair> selectedCategories = new ArrayList<NameValuePair>();
		
		if (selectedCategoryIds == null) {
			for (CategoryDomainObject category : categories) {
				String name = category.getType().getName() + ":" + category.getName();
				
				NameValuePair pair = new NameValuePair(name, category.getId());
				notSelectedCategories.add(pair);
			}
			
		} else {
			for (CategoryDomainObject category : categories) {
				String name = category.getType().getName() + ":" + category.getName();
				NameValuePair pair = new NameValuePair(name, category.getId());
				
				boolean found = false;
				
				for (int selectedCategoryId : selectedCategoryIds) {
					if (selectedCategoryId == category.getId()) {
						selectedCategories.add(pair);
						found = true;
						
						break;
					}
				}
				
				if (!found) {
					notSelectedCategories.add(pair);
				}
			}
			
		}
		
		model.put(notSelectedKey, notSelectedCategories);
		model.put(selectedKey, selectedCategories);
	}
	
	@SuppressWarnings("unchecked")
	private static Set<Integer> getFoundMetaIds(HttpSession session) {
		Set<Integer> metaIds = (Set<Integer>) session.getAttribute(FOUND_META_IDS_KEY);
		
		if (metaIds == null) {
			metaIds = new HashSet<Integer>();
		}
		
		return metaIds;
	}
	
	private static void setFoundMetaIds(HttpSession session, Set<Integer> foundMetaIds) {
		session.setAttribute(FOUND_META_IDS_KEY, foundMetaIds);
	}
	
	private static SearchDocumentsFoldCommand getFold(HttpSession session) {
		SearchDocumentsFoldCommand fold = (SearchDocumentsFoldCommand) session.getAttribute(FOLD_KEY);
		
		if (fold == null) {
			fold = new SearchDocumentsFoldCommand();
		}
		
		return fold;
	}
	
	private static void setFold(HttpSession session, SearchDocumentsFoldCommand fold) {
		session.setAttribute(FOLD_KEY, fold);
	}
}
