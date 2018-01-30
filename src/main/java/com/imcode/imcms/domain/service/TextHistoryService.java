package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.model.TextHistory;

import java.util.List;

public interface TextHistoryService {

    void save(Text text);

    List<TextHistory> findAllByLanguageAndLoopEntryRefAndNo(Language language, LoopEntryRef loopEntryRef, int no);
}