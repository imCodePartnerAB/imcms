package com.imcode.imcms.domain.service;

import com.imcode.imcms.enums.SaveMode;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.LoopEntryRef;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.TextJPA;

import java.util.List;
import java.util.Set;

public interface TextService extends DeleterByDocumentId, VersionedContentService {

    List<TextJPA> getByDocId(Integer docId);

    Text getText(Text textRequestData);

    Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text getText(int docId, int index, int versionNo, String langCode, LoopEntryRef loopEntryRef);

    List<TextJPA> getText(Integer index, String key);

    Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text getLikePublishedText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text save(Text text);

    Text updateTextInCurrentVersion(TextJPA text, SaveMode saveMode);

    Set<Text> getPublicTexts(int docId, Language language);

    Set<Text> getLikePublishedTexts(int docId, Language language);

    Text filter(Text text);

	List<Text> getLoopTexts(int docId, String langCode, int loopIndex);

	List<Text> getTextsContaining(String content);
}
