package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageHistoryDTO;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.LanguageJPA;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;

public interface ImageHistoryService {

    void save(ImageJPA image);

    void save(ImageDTO image, LanguageJPA language, Version version);

    List<ImageHistoryDTO> getAll(ImageDTO image);

}
