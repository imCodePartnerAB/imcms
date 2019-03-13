package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageHistoryDTO;
import com.imcode.imcms.persistence.entity.ImageJPA;

import java.util.List;

public interface ImageHistoryService {

    void save(ImageJPA image);

    List<ImageHistoryDTO> getAll(ImageDTO image);

}
