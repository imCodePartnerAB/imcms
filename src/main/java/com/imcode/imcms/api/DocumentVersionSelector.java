package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;

import com.imcode.imcms.dao.MetaDao;
import com.imcode.imcms.mapping.DocumentGetter;
import java.io.Serializable;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Specifies document's version in document retrieving API.
 *  
 * There are three selector types - published, working and custom.
 * Published and a working are predefined and can be obtained as public final fields
 * or using {@link #getPredefinedSelector(String)} method.
 * 
 * @see DocumentGetter
 * @see MetaDao
 * @see DocumentVersionTag
 */
public class DocumentVersionSelector implements Serializable {

	/**
	 * Selector type.
	 *
	 * PUBLISHED specifies versions tagged as PUBLISHED
	 * WORKING specifies versions tagged as WORKING
	 * CUSTOM specifies any document version.
	 */
	public static enum Type {
		PUBLISHED,
		WORKING,
		CUSTOM
	}
    
	/**
	 * Predefined publish document selector.
	 */
	public static final DocumentVersionSelector PUBLISHED_SELECTOR = 
		new DocumentVersionSelector(Type.PUBLISHED, null) {
		
		/**
		 * Dispatches call to document getter.
		 * 
		 * @return published version of a document
		 */
		@Override
		public DocumentDomainObject getDocument(DocumentGetter documentGetter, Integer documentId) {
			return documentGetter.getPublishedDocument(documentId);
		}		
	};
	
	/**
	 * Predefined working document selector.
	 * 
	 * @return working version of a document
	 */	
	public static final DocumentVersionSelector WORKING_SELECTOR = 
		new DocumentVersionSelector(Type.WORKING, null) {
		
		/**
		 * Dispatches call to document getter.
		 */
		@Override
		public DocumentDomainObject getDocument(DocumentGetter documentGetter, Integer documentId) {
			return documentGetter.getWorkingDocument(documentId);
		}		
	};
	
	/**
	 * Selector type.
	 */
	private final Type type;
	
	/**
	 * Document's version number. Used if selector type is CUSTOM.
	 */
	private final Integer versionNumber;
	
	/**
	 * Can not be instantiated directly.
	 * Use factories.
	 */
	private DocumentVersionSelector(Type type, Integer number) {
		this.type = type;
		this.versionNumber = number;
	}


	/**
	 * Returns version selector.
	 *
	 * @param version version number or name.
	 *
	 * @return version selector.
	 *
	 * @throws IllegalArgumentException
	 */
	public static DocumentVersionSelector getSelector(String version) {
		if (version == null) {
			throw new IllegalArgumentException("version argument can not be null.");
		}

        if (NumberUtils.isDigits(version)) {
            return new DocumentVersionSelector(Type.CUSTOM, Integer.parseInt(version));
        } else {
			switch (Type.valueOf(version.toUpperCase())) {
			case PUBLISHED:
				return PUBLISHED_SELECTOR;

			case WORKING:
				return WORKING_SELECTOR;

			default:
				throw new IllegalArgumentException(String.format("%s is not a valid version name. Valid names are: %s and %s", version, Type.PUBLISHED, Type.WORKING));
			}
        }
	}
	
	/**
	 * Returns predefined selector.
	 * 
	 * @param typeName unique selector's type name (WORKING or PUBLISHED).
	 * @return unique specifier.
	 * 
	 * @throws IllegalArgumentException if typeName argument is null or does not match to 
	 * predefined selector type (WORKING or PUBLISHED).
	 */
	public static DocumentVersionSelector getPredefinedSelector(String typeName) {
		try {		
			switch (Type.valueOf(typeName.toUpperCase())) {
			case PUBLISHED:
				return PUBLISHED_SELECTOR;
			
			case WORKING:
				return WORKING_SELECTOR;
				
			default:
				throw new IllegalArgumentException(String.format("%s is not a predefined selector's type name. Legal type names are: %s and %s", typeName, Type.PUBLISHED, Type.WORKING));
			}
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("typeName argument can not be null.", e);
		}
	}

    
	/**
	 * Dispatches call to document getter.
	 * 
	 * @param documentId document id
	 * @return custom version of a document  
	 */
	public DocumentDomainObject getDocument(DocumentGetter documentGetter, Integer documentId) {
		return documentGetter.getDocument(documentId, versionNumber);
	}
	
	public Type getType() {
		return type;
	}

    /**
     * @return version number in case of CUSTOM selector or null in case of WORKING or PUBLISHED selector. 
     */
	public Integer getVersionNumber() {
		return versionNumber;
	}	
	
	public boolean isPublished() {
		return type == Type.WORKING;
	}
	
	public boolean isWorking() {
		return type == Type.PUBLISHED;
	}	
	
	public boolean isCustom() {
		return type == Type.CUSTOM;
	}
}