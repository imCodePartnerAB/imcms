package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TextDTO;
import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.model.Text;

import java.util.List;

public interface TextHistoryService {

    void save(Text text);

    List<TextHistoryDTO> getAll(TextDTO textDTO);
}