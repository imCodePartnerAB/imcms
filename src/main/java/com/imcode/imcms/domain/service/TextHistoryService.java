package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.TextHistoryDTO;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;

import java.util.List;

public interface TextHistoryService {

    void save(Text text);

    List<TextHistoryDTO> findAllByLanguageAndLoopEntryRefAndNo(String langCode, LoopEntryRef loopEntryRef, int no);
}