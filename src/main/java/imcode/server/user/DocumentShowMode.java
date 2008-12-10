package imcode.server.user;

/**
 * Document show mode.
 * 
 * @author Anton Josua
 */
public class DocumentShowMode {

	
	/**
	 * Document version show mode.
	 * 
	 * PUBLISHED mode is default.
	 * 
	 * In PUBLISHED mode a user sees published document content.
	 * In WORKING mode a user sees working (draft) document content.
	 * In CUSTOM mode a user can choose document's version manually.
	 * 
	 * TODO: CUSTOM mode is not yet supported.
	 * 
	 * Only logged in user is allowed to change the mode.
	 */
	public static enum VersionShowMode {
		WORKING,		
		PUBLISHED,		
		ARCHIVED
	}	

	/** 
	 * When set to true then Meta.getI18nContentShowMode value
	 * is not taken into account.
	 *  
	 * Default is false.
	 * 
	 * Only logged in user is allowed to change the value.
	 */	
	private boolean ignoreI18nContentShowMode = false;
	
	private VersionShowMode versionShowMode = VersionShowMode.PUBLISHED;

	
	public VersionShowMode getVersionShowMode() {
		return versionShowMode;
	}

	public void setVersionShowMode(VersionShowMode versionShowMode) {
		if (versionShowMode == null) {
			throw new NullPointerException("Document version show mode can not be set to null.");
		}
		
		this.versionShowMode = versionShowMode;
	}
	
	public boolean isIgnoreI18nContentShowMode() {
		return ignoreI18nContentShowMode;
	}

	public void setIgnoreI18nContentShowMode(boolean ignoreI18nShowMode) {
		this.ignoreI18nContentShowMode = ignoreI18nShowMode;
	}
}
