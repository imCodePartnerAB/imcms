package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.TextJPA;

import java.util.List;
import java.util.Set;

public interface TextService extends DeleterByDocumentId, VersionedContentService {

    Text getText(Text textRequestData);

    Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    List<TextJPA> getText(Integer index, String key);

    Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text save(Text text);

    Set<Text> getPublicTexts(int docId, Language language);

    Text filter(Text text);

}
