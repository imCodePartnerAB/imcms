package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.DocumentDataDTO;

public interface DocumentDataService {

    DocumentDataDTO getDataByDocIdAndAvailableLangs(Integer id);
}
