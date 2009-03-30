package imcode.server.user;

import com.imcode.imcms.api.DocumentVersionTag;

/**
 * Document show mode.
 * 
 * @author Anton Josua
 */
public class DocumentShowSettings {
	
	/** 
	 * When set to true then Meta.getI18nShowMode value
	 * is not taken into account.
	 *  
	 * Default is false.
	 * 
	 * Only logged in user is allowed to change the value.
	 */	
	private boolean ignoreI18nShowMode = false;
		
	/**
	 * Document version tag.
	 * 
	 * In PUBLISHED mode a user sees published document content.
	 * In WORKING mode a user sees working document content.
	 * In CUSTOM mode a user can choose archived document version manually - implementation was not requested.
	 *
	 * PUBLISHED mode is default.
	 * 
	 * Only logged in user is allowed to change the value.
	 */
	private DocumentVersionTag documentVersionTag = DocumentVersionTag.PUBLISHED;

	public DocumentVersionTag getDocumentVersionTag() {
		return documentVersionTag;
	}

	public void setDocumentVersionTag(DocumentVersionTag documentVersionTag) {
		if (documentVersionTag == null) {
			throw new NullPointerException("Version tag can not be set to null.");
		}
		
		this.documentVersionTag = documentVersionTag;
	}
	
	public boolean isIgnoreI18nShowMode() {
		return ignoreI18nShowMode;
	}

	public void setIgnoreI18nShowMode(boolean ignoreI18nShowMode) {
		this.ignoreI18nShowMode = ignoreI18nShowMode;
	}
}
