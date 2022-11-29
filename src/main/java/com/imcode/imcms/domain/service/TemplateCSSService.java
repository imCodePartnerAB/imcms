package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TemplateCSSHistoryEntry;
import com.imcode.imcms.domain.dto.TemplateCSSVersion;

import java.util.List;
import java.util.Set;

/**
 * <p>Service to work with template CSS.</p>
 * <p>Template CSS - is a CSS code that applied to page which has such template on load.</p>
 *
 * <p>See also <b>templateCSS.tag</b> </p>
 */
public interface TemplateCSSService {
	String CSS_EXTENSION = ".css";
	String CSS_WORKING_VERSION_SUFFIX = "_working";

	/**
	 * Uses provided in properties folder(<b>TemplateCSSPath</b>) to sync it from SVN to local.
	 * If there are no files on SVN new ones will be created and committed to repository.
	 * Working version of template CSS will always be created on project start(they are not stored on SVN).
	 *
	 * @param templateNames template names used to check out/create CSS files
	 */
	void sync(final Set<String> templateNames);

	/**
	 * Creates template CSS file
	 *
	 * @param templateName template name which will be used to create template CSS file
	 */
	void create(String templateName);

	/**
	 * Creates template CSS files
	 *
	 * @param templateNames template names which will be used to create template CSS files
	 */
	void create(Set<String> templateNames);

	/**
	 * Get specific version of template CSS local file.
	 *
	 * @param templateName template name that accords to template CSS file
	 * @param version      version of template CSS file. Can be ACTIVE or WORKING
	 * @return template CSS string
	 * @see TemplateCSSVersion
	 */
	String get(String templateName, TemplateCSSVersion version);

	/**
	 * Get specific revision of template CSS from version control.
	 *
	 * @param templateName template name that accords to template    CSS file
	 * @param revision     template CSS revision
	 * @return template CSS string
	 */
	String getRevision(String templateName, Long revision);

	/**
	 * Update WORKING version of template CSS file.
	 *
	 * @param templateName template name that accords to template CSS file
	 * @param css          CSS code
	 */
	void update(String templateName, String css);

	void rename(String templateName, String newTemplateName);

	/**
	 * Copy WORKING version to ACTIVE version local file.
	 *
	 * @param templateName template name that accords to template CSS file
	 */
	void publish(String templateName);

	/**
	 * Check whether the local template CSS file exists.
	 *
	 * @param templateName template name that accords to template CSS file
	 */
	boolean existsLocally(String templateName);

	/**
	 * Check whether the template CSS exists on version control.
	 *
	 * @param templateName template name that accords to template CSS file
	 */
	boolean existsOnSVN(String templateName);

	/**
	 * Check whether the specific revision of template CSS exists on version control.
	 *
	 * @param templateName template name that accords to template CSS file
	 * @param revision     template CSS revision
	 */
	boolean existsOnSVN(String templateName, Long revision);

	/**
	 * Get revision history of specified template CSS file from version control
	 *
	 * @param templateName template name that accords to template CSS file
	 * @return collection of {@link TemplateCSSHistoryEntry}
	 * @see TemplateCSSHistoryEntry
	 */
	List<TemplateCSSHistoryEntry> getHistory(String templateName);

	/**
	 * Compare WORKING and ACTIVE versions of local template CSS file.
	 *
	 * @param templateName template name that accords to template CSS file
	 */
	boolean equalsVersions(String templateName);

	/**
	 * Checks whether the given CSS code equals to WORKING version of local template CSS file
	 *
	 * @param templateName template name that accords to template CSS file
	 * @param css          CSS code
	 */
	boolean equalsWorkingVersion(String templateName, String css);
}
