package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Version;

import java.util.*;

public interface CommonContentService extends VersionedContentService, DeleterByDocumentId {
    /**
     * Get document's common contents for all languages
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @return a {@code List} of all common contents
     */
    List<CommonContent> getOrCreateCommonContents(int docId, int versionNo);

    /**
     * Get common contents for latest version
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @return a {@code Map} in which the key is docId and the value is List of common contents
     */
    Map<Integer, List<CommonContent>> getOrCreateCommonContents(Collection<Integer> docIds);

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @param language  to get language code
     * @return common content of docId, versionNo and user language.
     */
    CommonContent getOrCreate(int docId, int versionNo, Language language);

    <T extends CommonContent> void save(int docId, Collection<T> saveUs);

    Set<CommonContent> getByVersion(Version version);

	Optional<CommonContentDTO> getByVersionAndLanguage(Version version, Language language);

    List<CommonContent> getAll();

    /**
     * Gets list of common content by alias for working and published versions.
     */
    List<CommonContent> getByAlias(String alias);

    Optional<CommonContent> getPublicByAlias(String alias);

	Boolean existsByAlias(String alias);

    Boolean existsPublicByAlias(String alias);

    Optional<Integer> getDocIdByPublicAlias(String alias);

	List<String> getAllAliases();

	void removeAlias(String alias);
}
