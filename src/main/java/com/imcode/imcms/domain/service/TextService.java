package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;

public interface TextService extends DeleterByDocumentId {

    Text getText(Text textRequestData);

    Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    void save(Text textDTO);

}
