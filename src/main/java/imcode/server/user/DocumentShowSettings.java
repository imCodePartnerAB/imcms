package imcode.server.user;

import com.imcode.imcms.api.DocumentVersionSelector;

/**
 * Document show settings.
 */
public class DocumentShowSettings {
		
	/** 
	 * When set to true then Meta.getI18nShowMode value
	 * is not taken into account.
	 *  
	 * Default is false.
	 * 
	 * Only logged in user with appropriate permissions can change the value.
	 */	
	private boolean ignoreI18nShowMode = false;
		
	/**
	 * Specifies document version for showed documents.
	 *
	 * PUBLISHED_SPECIFIER is default.
	 * 
	 * Only logged in user with appropriate permissions can change the value.
	 */
	private DocumentVersionSelector versionSelector = DocumentVersionSelector.PUBLISHED;

	public DocumentVersionSelector getVersionSelector() {
		return versionSelector;
	}

	public void setVersionSelector(DocumentVersionSelector versionSelector) {
		if (versionSelector == null) {
			throw new IllegalArgumentException("versionSelector can not be set to null.");
		}
		
		this.versionSelector = versionSelector;
	}
	
	public boolean isIgnoreI18nShowMode() {
		return ignoreI18nShowMode;
	}

	public void setIgnoreI18nShowMode(boolean ignoreI18nShowMode) {
		this.ignoreI18nShowMode = ignoreI18nShowMode;
	}
}
