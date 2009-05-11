package com.imcode.imcms.api;


/**
 * Specifies document's version in document retrieving API.
 * 
 * Used in DocumentMapper.getDocumentXXX to select document's version. 
 */
public class DocumentVersionSelector {
	
	/**
	 * Predefined PUBLISHED document selector.
	 */
	public static final DocumentVersionSelector PUBLISHED = 
		new DocumentVersionSelector(Tag.PUBLISHED, null);
	
	/**
	 * Predefined WORKING document selector.
	 */	
	public static final DocumentVersionSelector WORKING = 
		new DocumentVersionSelector(Tag.WORKING, null);	
	
	/**
	 * Version tag.
	 * 
	 * PUBLISHED specifies versions tagged as PUBLISHED
	 * WORKING specifies versions tagged as WORKING
	 * CUSTOM specifies any document version including PUBLISHED and WORKING.
	 */
	public static enum Tag {
		PUBLISHED,
		WORKING,
		CUSTOM
	}
	
	/**
	 * Requested document's version tag.
	 */
	private final Tag tag;
	
	/**
	 * Document's version number. 
	 * Used if tag is set to CUSTOM.
	 */
	private final Integer number;
	
	/**
	 * Can not be instantiated directly.
	 * Use factories.
	 */
	private DocumentVersionSelector(Tag tag, Integer number) {
		this.tag = tag;
		this.number = number;
	}
	
	/**
	 * Creates custom version specifier.
	 * 
	 * @param number version number.
	 * @return custom version specifier.
	 */
	public static DocumentVersionSelector createCustomSelector(Integer number) {
		if (number == null) {
			throw new IllegalArgumentException("versionNumber argument can not be null.");
		}
		
		return new DocumentVersionSelector(Tag.CUSTOM, number);
	}
	
	/**
	 * Returns unique version selector.
	 * 
	 * @param uniueTagSpecifierName unique tag specifier name (WORKING or PUBLISHED).
	 * @return unique specifier.
	 * 
	 * @throws IllegalArgumentException
	 */
	public static DocumentVersionSelector getUniqueSelector(String tagName) {
		if (tagName == null) {
			throw new IllegalArgumentException("tagName argument can not be null.");
		}
		
		tagName = tagName.toUpperCase();
		
		if (tagName.equals(Tag.PUBLISHED.name())) {
			return PUBLISHED;
		} else if (tagName.equals(Tag.WORKING.name())) {
			return WORKING;
		}
		
		throw new IllegalArgumentException(String.format("%s is not a unique tag name. Legal values are: %s and %s", tagName, Tag.PUBLISHED, Tag.WORKING));
	}	
	
	public Tag getTag() {
		return tag;
	}
	
	public Integer getNumber() {
		return number;
	}	
}
