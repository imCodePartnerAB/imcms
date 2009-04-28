package com.imcode.imcms.api;


/**
 * Specifies document's version in document retrieving API.
 * 
 * Used in DocumentMapper.getDocumentXXX to specify document's version. 
 */
public class DocumentVersionSpecifier {
	
	/**
	 * Predefined PUBLISHED document specifier.
	 */
	public static final DocumentVersionSpecifier PUBLISHED = 
		new DocumentVersionSpecifier(TagSpecifier.PUBLISHED, null);
	
	/**
	 * Predefined WORKING document specifier.
	 */	
	public static final DocumentVersionSpecifier WORKING = 
		new DocumentVersionSpecifier(TagSpecifier.WORKING, null);	
	
	/**
	 * Version tag specifiers.
	 * 
	 * PUBLISHED specifies versions tagged as PUBLISHED
	 * WORKING specifies versions tagged as WORKING
	 * CUSTOM specifies any document version including PUBLISHED and WORKING.
	 */
	public static enum TagSpecifier {
		PUBLISHED,
		WORKING,
		CUSTOM
	}
	
	/**
	 * Specifies requested document's version tag.
	 */
	private final TagSpecifier tagSpecifier;
	
	/**
	 * Document's version number. 
	 * Used if tagSpecifier is set to CUSTOM.
	 */
	private final Integer versionNumber;
	
	/**
	 * Can not be instantiated directly.
	 * Use factories.
	 */
	private DocumentVersionSpecifier(TagSpecifier tagSpecifier, Integer versionNumber) {
		this.tagSpecifier = tagSpecifier;
		this.versionNumber = versionNumber;
	}
	
	/**
	 * Creates custom version specifier.
	 * 
	 * @param versionNumber version number.
	 * @return custom version specifier.
	 */
	public static DocumentVersionSpecifier createCustomSpecifier(Integer versionNumber) {
		if (versionNumber == null) {
			throw new IllegalArgumentException("versionNumber argument can not be null.");
		}
		
		return new DocumentVersionSpecifier(TagSpecifier.CUSTOM, versionNumber);
	}
	
	/**
	 * Creates unique tag specifier.
	 * 
	 * @param uniueTagSpecifierName unique tag specifier name (WORKING or PUBLISHED).
	 * @return unique specifier.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static DocumentVersionSpecifier createUniqueSpecifier(String uniueTagSpecifierName) {
		if (uniueTagSpecifierName == null) {
			throw new IllegalArgumentException("uniueTagSpecifierName argument can not be null.");
		}
		
		uniueTagSpecifierName = uniueTagSpecifierName.toUpperCase();
		
		if (uniueTagSpecifierName.equals(TagSpecifier.PUBLISHED.name())) {
			return PUBLISHED;
		} else if (uniueTagSpecifierName.equals(TagSpecifier.WORKING.name())) {
			return WORKING;
		}
		
		throw new IllegalArgumentException(String.format("%s is not a unique tag specifier name. Legal values are: %s and %s", uniueTagSpecifierName, TagSpecifier.PUBLISHED, TagSpecifier.WORKING));
	}	
	
	public TagSpecifier getTagSpecifier() {
		return tagSpecifier;
	}
	
	public Integer getVersionNumber() {
		return versionNumber;
	}	
}
