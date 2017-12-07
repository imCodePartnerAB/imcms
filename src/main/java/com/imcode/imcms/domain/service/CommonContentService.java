package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Version;

import java.util.Collection;
import java.util.List;

public interface CommonContentService {
    /**
     * Get document's common contents for all languages
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @return a {@code List} of all common contents
     */
    List<CommonContentDTO> getOrCreateCommonContents(int docId, int versionNo);

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is {@code null} it creates new common content based on working.
     *
     * @param docId     of document
     * @param versionNo version no
     * @param language  to get language code
     * @return common content of docId, versionNo and user language.
     */
    CommonContentDTO getOrCreate(int docId, int versionNo, Language language);

    List<CommonContentDTO> getCommonContents(int docId, int versionNo);

    void save(Collection<CommonContentDTO> saveUs);

    void save(CommonContentDTO saveMe);

    /**
     * Creates empty CommonContent for non-existing document and for all
     * languages with {@link Version#WORKING_VERSION_INDEX}.
     * Not saves to DB.
     */
    List<CommonContentDTO> createCommonContents();

    void deleteByDocId(int docId);
}
