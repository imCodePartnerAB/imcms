package com.imcode.imcms.domain.service.core;

import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import imcode.server.user.UserDomainObject;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class CommonContentService {

    private final CommonContentRepository commonContentRepository;

    public CommonContentService(CommonContentRepository commonContentRepository) {
        this.commonContentRepository = commonContentRepository;
    }

    public CommonContent findByDocIdAndVersionNoAndUserDomainObject(int docId, int versionNo, UserDomainObject userDO) {
        requireNonNull(userDO, "CommonContentService findByUser user is null.");
        return commonContentRepository.findByDocIdAndVersionNoAndLanguageCode(docId, versionNo, userDO.getLanguageIso639_2());
    }

}
