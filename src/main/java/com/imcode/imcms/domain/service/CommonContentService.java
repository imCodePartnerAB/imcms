package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Version;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * Gets common content for working or published versions.
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @param language  to get language code
     * @return common content of docId, versionNo and user language.
     */
    CommonContent getOrCreate(int docId, int versionNo, Language language);

	CommonContent getByAlias(String alias);

    <T extends CommonContent> void save(int docId, Collection<T> saveUs);

    Set<CommonContent> getByVersion(Version version);

	Optional<CommonContentDTO> getByVersionAndLanguage(Version version, Language language);

    List<CommonContent> getAll();

	Boolean existsByAlias(String alias);

	Integer getDocIdByAlias(String alias);

	List<String> getAllAliases();

	void removeAlias(String alias);
}
