package imcode.server.user;

/**
 * Document show mode.
 * 
 * @author Anton Josua
 */
public class DocumentShowSettings {
	
	/**
	 * Document version show mode.
	 * 
	 * In PUBLISHED mode a user sees published document content.
	 * In WORKING mode a user sees working (draft) document content.
	 * In CUSTOM mode a user can choose document's versionShowMode manually.
	 *
	 * PUBLISHED mode is default.
	 * 
	 * Only logged in user is allowed to change the mode.
	 */
	public static enum VersionShowMode {
		WORKING,		
		PUBLISHED,		
		CUSTOM
	}	

	/** 
	 * When set to true then Meta.getI18nShowMode value
	 * is not taken into account.
	 *  
	 * Default is false.
	 * 
	 * Only logged in user is allowed to change the value.
	 */	
	private boolean ignoreI18nShowMode = false;
	
	private VersionShowMode versionShowMode = VersionShowMode.PUBLISHED;

	
	public VersionShowMode getShowVersionMode() {
		return versionShowMode;
	}

	public void setVersionShowMode(VersionShowMode versionShowMode) {
		if (versionShowMode == null) {
			throw new NullPointerException("Version mode can not be set to null.");
		}
		
		this.versionShowMode = versionShowMode;
	}
	
	public boolean isIgnoreI18nShowMode() {
		return ignoreI18nShowMode;
	}

	public void setIgnoreI18nShowMode(boolean ignoreI18nShowMode) {
		this.ignoreI18nShowMode = ignoreI18nShowMode;
	}
}
