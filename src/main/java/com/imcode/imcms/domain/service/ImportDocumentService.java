package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.dto.ImportProgress;

public interface ImportDocumentService {

	void importDocuments(int startId, int endId, boolean autoImportMenus);

	void importDocuments(int[] importDocIds, boolean autoImportMenus);

	ImportProgress getImportingProgress();

	void removeAlias(int importDocId);

	void removeAlias(BasicImportDocumentInfoDTO importDocInfo);

	void removeAliases(int[] importDocIds);

	void removeAliasesInRange(int startId, int endId);

	void replaceAlias(int importDocId);

	void replaceAlias(BasicImportDocumentInfoDTO importDocInfo);

	void replaceAliases(int[] importDocIds);

	void replaceAliasesInRange(int startId, int endId);

}
