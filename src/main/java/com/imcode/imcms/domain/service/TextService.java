package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.LoopEntryRefDTO;
import com.imcode.imcms.domain.dto.TextDTO;

public interface TextService extends DeleterByDocumentId {

    TextDTO getText(TextDTO textRequestData);

    TextDTO getText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRef);

    TextDTO getPublicText(int docId, int index, String langCode, LoopEntryRefDTO loopEntryRef);

    void save(TextDTO textDTO);

}
