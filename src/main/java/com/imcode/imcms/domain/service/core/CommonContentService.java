package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

@Service
public class CommonContentService {

    private static final int WORKING_VERSION_INDEX = 0;

    private final CommonContentRepository commonContentRepository;

    public CommonContentService(CommonContentRepository commonContentRepository) {
        this.commonContentRepository = commonContentRepository;
    }

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is null. it creates new common content based on working.
     *
     * @param docId of document version
     * @param versionNo version no
     * @param userDO user to get language
     * @return common content of docId, versionNo and user language.
     */
    public CommonContent getOrCreate(int docId, int versionNo, UserDomainObject userDO) {
        final String code = LanguageMapper.convert639_2to639_1(userDO.getLanguageIso639_2());
        final CommonContent commonContent = commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(docId, versionNo, code);
        if (commonContent != null) {
            return commonContent;
        } else if (versionNo == WORKING_VERSION_INDEX) {
            throw new IllegalStateException("Common content for working version should always exist!");
        }
        return createFromWorkingVersion(docId, code, versionNo);
    }

    private CommonContent createFromWorkingVersion(int docId, String code, int versionNo) {
        final CommonContent commonContent = commonContentRepository
                .findByDocIdAndVersionNoAndLanguageCode(docId, WORKING_VERSION_INDEX, code);
        commonContent.setId(null);
        commonContent.setVersionNo(versionNo);
        return commonContentRepository.saveAndFlush(commonContent);
    }

}
