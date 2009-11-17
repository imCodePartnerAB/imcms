package com.imcode.imcms.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I18n support.
 * 
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class I18nSupport {

    private Map<String, I18nLanguage> hosts;
		
	/**
	 * Default language.  
	 */
	private I18nLanguage defaultLanguage;
	
	/**
	 * Available languages list. 
	 */
	private List<I18nLanguage> languages;
	
	/**
	 * Available languages map.   
	 */
	private Map<String, I18nLanguage> codeMap;
	
	/**
	 * Available languages map. 
	 */
	private Map<Integer, I18nLanguage> idMap;

    /**
	 * Implicit instantiation is not allowed.
	 */
	public I18nSupport() {}
	
	/**
	 * Sets default language.
	 * 
	 * When application running in container default language should be set
	 * during setup.
	 * 
	 * @param language default language.
	 * @throws IllegalArgumentException in case of attempt 
	 * to assign null to default language.
	 */
	public void setDefaultLanguage(I18nLanguage language) 
	throws IllegalArgumentException {
		if (language == null) {
			throw new IllegalArgumentException("Language argument " +
					"can not be null.");			
		}
		
		defaultLanguage = language;
	}
	
	/**
	 * Returns default language. 
	 */
	public I18nLanguage getDefaultLanguage()
	throws I18nException {
		if (defaultLanguage == null) {
			throw new I18nException("Default language is not set.");
		}
		
		return defaultLanguage;
	}

	public List<I18nLanguage> getLanguages() {
		return languages;
	}

	public void setLanguages(List<I18nLanguage> languages) {
		if (languages == null) {
			throw new IllegalArgumentException("Languages argument " +
					"can not be null.");			
		}
		
		this.languages = languages;
		
		idMap = new HashMap<Integer, I18nLanguage>();
		codeMap = new HashMap<String, I18nLanguage>();
		
		for (I18nLanguage language: languages) {
			idMap.put(language.getId(), language);
			codeMap.put(language.getCode(), language);
		}
	}	
	
	
	public I18nLanguage getByCode(String code) {
		return codeMap.get(code);
	}
	
	public I18nLanguage getById(Integer id) {
		return idMap.get(id);
	}

	public boolean isDefault(I18nLanguage language) {
		return getDefaultLanguage().equals(language); 
	}

    public Map<String, I18nLanguage> getHosts() {
        return hosts;
    }

    public void setHosts(Map<String, I18nLanguage> hosts) {
        this.hosts = hosts;
    }
}