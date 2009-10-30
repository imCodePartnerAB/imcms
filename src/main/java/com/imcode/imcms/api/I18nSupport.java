package com.imcode.imcms.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I18n support.
 * This class intended to be used as a singleton and can not be instantiated.
 * 
 * @see com.imcode.imcms.servlet.ImcmsFilter
 * @author Anton Josua
 */
public class I18nSupport {

	/** 
	 * Current language.
	 * 
	 * Language selected by user or assigned by the system on a first run. 
	 *  
	 * @see com.imcode.imcms.servlet.ImcmsFilter
	 */
	private static ThreadLocal<I18nLanguage> currentLanguage = new ThreadLocal<I18nLanguage>();
		
	/**
	 * Default language.  
	 */
	private static I18nLanguage defaultLanguage;
	
	/**
	 * Available languages list. 
	 */
	private static List<I18nLanguage> languages;
	
	/**
	 * Available languages map.   
	 */
	private static Map<String, I18nLanguage> codeMap;
	
	/**
	 * Available languages map. 
	 */
	private static Map<Integer, I18nLanguage> idMap;
	
	/** 
	 * Implicit instantiation is not allowed.
	 */
	private I18nSupport() {}
	
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
	public static void setDefaultLanguage(I18nLanguage language) 
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
	public static I18nLanguage getDefaultLanguage()
	throws I18nException {
		if (defaultLanguage == null) {
			throw new I18nException("Default language is not set.");
		}
		
		return defaultLanguage;
	}
	
	/**
	 * Returns I18nLanguage instance bound to the current thread. 
	 * 
	 * @throws I18nException if no I18nLanguage instance 
	 * is bound to the current thread
	 */
	public static I18nLanguage getCurrentLanguage() 
	throws I18nException {
		I18nLanguage language = currentLanguage.get();
		
		if (language == null) {
			throw new I18nException("Current language is not set.");
		}
		
		return language;		
	}
	
	
	/**
	 * Bounds I18nLanguage instance to the current thread.
	 * 
	 * @throws IllegalArgumentException in case of attempt to 
	 * assign null to current language. 
	 */
	public static void setCurrentLanguage(I18nLanguage language) 
	throws IllegalArgumentException {
		if (language == null) {
			throw new IllegalArgumentException("Language argument " +
					"can not be null.");			
		}
		
		currentLanguage.set(language);
	}
	
	/**
	 * Return if tested language is default.
	 */
	public static boolean isDefault(I18nLanguage language) {
		if (language == null) {
			throw new IllegalArgumentException("Language argument " +
					"can not be null.");			
		}
		
		return language.equals(getDefaultLanguage());
	}
	
	/**
	 * Return if tested language is current.
	 */
	public static boolean isCurrent(I18nLanguage language) {
		if (language == null) {
			throw new IllegalArgumentException("Language argument " +
					"can not be null.");			
		}
		
		return language.equals(getCurrentLanguage());
	}

	public static List<I18nLanguage> getLanguages() {
		return languages;
	}

	public static void setLanguages(List<I18nLanguage> languages) {
		if (languages == null) {
			throw new IllegalArgumentException("Languages argument " +
					"can not be null.");			
		}
		
		I18nSupport.languages = languages;
		
		idMap = new HashMap<Integer, I18nLanguage>();
		codeMap = new HashMap<String, I18nLanguage>();
		
		for (I18nLanguage language: languages) {
			idMap.put(language.getId(), language);
			codeMap.put(language.getCode(), language);
		}
	}	
	
	
	public static I18nLanguage getByCode(String code) {
		return codeMap.get(code);
	}
	
	public static I18nLanguage getById(Integer id) {
		return idMap.get(id);
	}	
	
	public static boolean isEnabled() {
		return defaultLanguage != null && currentLanguage != null;
	}
	
	public static boolean getCurrentIsDefault() {
		return getCurrentLanguage().equals(getDefaultLanguage());
	}
}