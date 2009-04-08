package imcode.server.user;

import com.imcode.imcms.api.DocumentVersionSpecifier;

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
	private DocumentVersionSpecifier versionSpecifier = DocumentVersionSpecifier.PUBLISHED;

	public DocumentVersionSpecifier getVersionSpecifier() {
		return versionSpecifier;
	}

	public void setVersionSpecifier(DocumentVersionSpecifier versionSpecifier) {
		if (versionSpecifier == null) {
			throw new IllegalArgumentException("versionSpecifier can not be set to null.");
		}
		
		this.versionSpecifier = versionSpecifier;
	}
	
	public boolean isIgnoreI18nShowMode() {
		return ignoreI18nShowMode;
	}

	public void setIgnoreI18nShowMode(boolean ignoreI18nShowMode) {
		this.ignoreI18nShowMode = ignoreI18nShowMode;
	}
}
