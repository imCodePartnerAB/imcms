package imcode.server.user;


/**
 * Document show mode.
 * 
 * @author Anton Josua
 */
public class DocumentShowSettings {
	
	/**
	 * Document's version show mode. 
	 */
	public static enum VersionShowMode {
		PUBLISHED,
		WORKING,
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
	
	/**
	 * Document version.
	 * Taken into account only when version tag is set to custom. 
	 */
	private Integer version;
		
	/**
	 * Document version mode.
	 * 
	 * In PUBLISHED mode an user sees published document content.
	 * In WORKING mode an user sees working document content.
	 * In CUSTOM mode an user sees document specified in version field. 
	 *
	 * PUBLISHED mode is default.
	 * 
	 * Only logged in user is allowed to change the value.
	 */
	private VersionShowMode versionShowMode = VersionShowMode.PUBLISHED;

	public VersionShowMode getVersionShowMode() {
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
