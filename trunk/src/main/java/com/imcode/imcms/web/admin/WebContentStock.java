package com.imcode.imcms.web.admin;

import imcode.server.ImcmsServices;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.imcode.imcms.api.CategoryType;


/**
 *
 * Prototype class
 *
 */
public final class WebContentStock {
	private ImcmsServices imSevices;
	private static WebContentStock instance;

	private WebContentStock() {	
	}
	
	public static synchronized WebContentStock getInstance(ImcmsServices imSevices) {
		if (instance == null && imSevices != null) {	
			instance = new WebContentStock();
			instance.imSevices = imSevices;
		}
		
		return instance;
	}
	
	public List<NameValuePair> getAllCategoryTypes() {
		List<NameValuePair> allCateroryTypes = new ArrayList<NameValuePair>();
		
		//
		
		return allCateroryTypes;
	}
	
	public List<NameValuePair> getAllCategoriesOfType(CategoryType categoryType) {
		List<NameValuePair> allCategoriesOfType = new ArrayList<NameValuePair>();
		
		//
		
		return allCategoriesOfType;
	}
	
	public List<NameValuePair> getAllTemplates() {
		List<NameValuePair> allTamplates = new ArrayList<NameValuePair>();
		
		//
		
		return allTamplates;
	}
	
	public List<NameValuePair> getAllRoles() {
		List<NameValuePair> allRoles = new ArrayList<NameValuePair>();
		
		//
		
		return allRoles;
	}
	
	@SuppressWarnings("unchecked")
	public List<NameValuePair> getAllPermissionGroups() {
		List<NameValuePair> allPermissionGroups = new ArrayList<NameValuePair>();
		
		
		allPermissionGroups.addAll(CollectionUtils.collect(PermissionGroupsBasket.getAllPermissionGroups().values(), new Transformer() {
			
			public Object transform(Object input) {
				PermissionGroup pg = (PermissionGroup) input;
				assert pg != null;
				
				NameValuePair pair = new NameValuePair(pg.getName(), pg.getId());
				return pair;
			}
		}));
		
		return allPermissionGroups;
	}
	
	public List<NameValuePair> getAllProfileOnCreateNew() {
		List<NameValuePair> allProfileGroups = new ArrayList<NameValuePair>();
		
		//
		
		return allProfileGroups;
	}
	
	public List<NameValuePair> getAllProfiles() {
		List<NameValuePair> allProfiles = new ArrayList<NameValuePair>();
		
        //
		
		return allProfiles;
	}
	
	public List<NameValuePair> getAllPuvlicationStatuses() {
		List<NameValuePair> allPublicationStatuses = new ArrayList<NameValuePair>();
		
		//
		
		return allPublicationStatuses;
	}
	
	public WebRawData getAllStock() {
		WebRawData raw = new WebRawData();
		
		raw.setCategoryTypes(getAllCategoryTypes());
		raw.setPermissionGroups(getAllPermissionGroups());
		raw.setProfiles(getAllProfiles());
		raw.setProfilesOnCreateNew(getAllProfileOnCreateNew());
		raw.setRoles(getAllRoles());
		raw.setTemplates(getAllTemplates());
		raw.setPublicationStatuses(getAllPuvlicationStatuses());
		
		return raw;
	}
}
