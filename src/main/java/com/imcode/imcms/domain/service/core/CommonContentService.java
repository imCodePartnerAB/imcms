package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.domain.dto.CommonContentDTO;
import com.imcode.imcms.persistence.entity.CommonContent;
import com.imcode.imcms.persistence.repository.CommonContentRepository;
import imcode.server.LanguageMapper;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CommonContentService {

    private static final int WORKING_VERSION_INDEX = 0;

    private final CommonContentRepository commonContentRepository;
    private final Function<CommonContent, CommonContentDTO> commonContentToDTO;
    private final Function<CommonContentDTO, CommonContent> commonContentSaver;

    public CommonContentService(CommonContentRepository commonContentRepository,
                                Function<CommonContent, CommonContentDTO> commonContentToDTO,
                                Function<CommonContentDTO, CommonContent> commonContentDtoToCommonContent) {

        this.commonContentRepository = commonContentRepository;
        this.commonContentToDTO = commonContentToDTO;
        this.commonContentSaver = commonContentDtoToCommonContent.andThen(commonContentRepository::save);
    }

    /**
     * Gets common content for working or published versions.
     * If common content of non working version is null. it creates new common content based on working.
     *
     * @param docId     of document version
     * @param versionNo version no
     * @param userDO    user to get language
     * @return common content of docId, versionNo and user language.
     */
    public CommonContentDTO getOrCreate(int docId, int versionNo, UserDomainObject userDO) {
        final String code = LanguageMapper.convert639_2to639_1(userDO.getLanguageIso639_2());
        final CommonContent commonContent = commonContentRepository
                .findByDocIdAndVersionNoAndLanguageCode(docId, versionNo, code);

        if (commonContent != null) {
            return commonContentToDTO.apply(commonContent);

        } else if (versionNo == WORKING_VERSION_INDEX) {
            throw new IllegalStateException("Common content for working version should always exist!");
        }

        return createFromWorkingVersion(docId, code, versionNo);
    }

    public void save(CommonContentDTO saveMe) {
        commonContentSaver.apply(saveMe);
    }

    private CommonContentDTO createFromWorkingVersion(int docId, String code, int versionNo) {
        final CommonContent commonContent = commonContentRepository
                .findByDocIdAndVersionNoAndLanguageCode(docId, WORKING_VERSION_INDEX, code);

        final CommonContent newCommonContent = new CommonContent();
        newCommonContent.setVersionNo(versionNo);
        newCommonContent.setDocId(docId);
        newCommonContent.setEnabled(commonContent.isEnabled());
        newCommonContent.setHeadline(commonContent.getHeadline());
        newCommonContent.setLanguage(commonContent.getLanguage());
        newCommonContent.setMenuImageURL(commonContent.getMenuImageURL());
        newCommonContent.setMenuText(commonContent.getMenuText());

        return commonContentToDTO.apply(commonContentRepository.saveAndFlush(commonContent));
    }

}
