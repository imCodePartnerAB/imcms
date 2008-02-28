package com.imcode.imcms.api;

/**
 * I18n support.
 * 
 * This class intended to be used as a singleton and can not be instantiated.
 * 
 * @author Anton Josua
 */
public class I18nSupport {

	/** 
	 * Language bound to current thread.
	 * 
	 * When application running in container current language is set
	 * using HTTP request filter. 
	 */
	private static ThreadLocal<I18nLanguage> currentLanguage = new ThreadLocal<I18nLanguage>();

	/**
	 * Default language.  
	 */
	private static I18nLanguage defaultLanguage;
	
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
			throw new IllegalArgumentException("Default language argument " +
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
			throw new I18nException(
			    "No I18nLanguage instance is bound to the current thread.");
		}
		
		return language;		
	}
	
	
	/**
	 * Bound I18nLanguage instance to the current thread.
	 * 
	 * @throws IllegalArgumentException in case of attempt to 
	 * assign null to current language. 
	 */
	public static void setCurrentLanguege(I18nLanguage language) 
	throws IllegalArgumentException {
		if (language == null) {
			throw new IllegalArgumentException("Language argument " +
					"can not be null.");			
		}
		
		currentLanguage.set(language);
	}
}