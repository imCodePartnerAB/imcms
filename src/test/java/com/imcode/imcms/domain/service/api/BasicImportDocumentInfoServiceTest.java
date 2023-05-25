package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.model.ImportDocumentStatus;
import com.imcode.imcms.persistence.repository.BasicImportDocumentInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BasicImportDocumentInfoServiceTest extends WebAppSpringTestConfig {

	@Autowired
	private DefaultBasicImportDocumentInfoService basicImportDocumentInfoService;
	@Autowired
	private BasicImportDocumentInfoRepository basicImportDocumentInfoRepository;

	private final int DEFAULT_ID = 14;
	private final int DEFAULT_META_ID = 1001;

	@BeforeEach
	public void clean() {
		basicImportDocumentInfoRepository.deleteAll();
		basicImportDocumentInfoRepository.flush();
	}

	@Test
	public void createBasicImportDocumentInfo_Expect_Created() {
		final BasicImportDocumentInfoDTO basicImportDocumentInfoDTO = basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT);

		assertNotNull(basicImportDocumentInfoDTO);
		assertEquals(DEFAULT_ID, basicImportDocumentInfoDTO.getId());
		assertEquals(ImportDocumentStatus.IMPORT, basicImportDocumentInfoDTO.getStatus());
		assertNull(basicImportDocumentInfoDTO.getMetaId());
	}

	@Test
	public void createBasicImportDocumentInfoWhenIdInUse_Expect_CorrectException() {
		final BasicImportDocumentInfoDTO basicImportDocumentInfoDTO = basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT);

		assertNotNull(basicImportDocumentInfoDTO);
		assertThrows(RuntimeException.class, () -> basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT));
	}

	@Test
	public void updateBasicImportDocumentInfo_Expect_Updated() {
		final BasicImportDocumentInfoDTO basicImportDocumentInfoDTO = basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT);

		assertNotNull(basicImportDocumentInfoDTO);

		basicImportDocumentInfoDTO.setMetaId(DEFAULT_META_ID);
		basicImportDocumentInfoDTO.setStatus(ImportDocumentStatus.IMPORTED);

		final BasicImportDocumentInfoDTO updated = basicImportDocumentInfoService.save(basicImportDocumentInfoDTO);
		assertNotNull(updated);
		assertEquals(basicImportDocumentInfoDTO.getId(), updated.getId());
		assertEquals(DEFAULT_META_ID, updated.getMetaId());
		assertEquals(ImportDocumentStatus.IMPORTED, updated.getStatus());
	}

	@Test
	public void getNonExistingBasicImportDocumentInfo_Expect_CorrectResult() {
		assertTrue(basicImportDocumentInfoService.getById(DEFAULT_ID).isEmpty());
	}

	@Test
	public void getExistingBasicImportDocumentInfo_Expect_CorrectResult() {
		final BasicImportDocumentInfoDTO actual = basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT);
		final Optional<BasicImportDocumentInfoDTO> basicImportDocumentInfoDTO = basicImportDocumentInfoService.getById(DEFAULT_ID);

		assertTrue(basicImportDocumentInfoDTO.isPresent());
		assertEquals(actual, basicImportDocumentInfoDTO.get());
		assertEquals(DEFAULT_ID, basicImportDocumentInfoDTO.get().getId());
	}

	@Test
	public void getAllBasicImportDocumentInfo_Expect_CorrectResult() {
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT));
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID + 1, ImportDocumentStatus.IMPORT));
		assertEquals(2, basicImportDocumentInfoService.getAll().getSize());
	}

	@Test
	public void getAllBasicImportDocumentInfoWithinRange_Expect_CorrectResult() {
		for (int i = 0; i < 5; i++) {
			assertNotNull(basicImportDocumentInfoService.create(i, ImportDocumentStatus.IMPORT));
		}

		assertEquals(1, basicImportDocumentInfoService.getAll(0, 0).getSize());
		assertEquals(3, basicImportDocumentInfoService.getAll(0, 2).getSize());
		assertEquals(5, basicImportDocumentInfoService.getAll(0, 5).getSize());
	}

	@Test
	public void getAllBasicImportDocumentInfoWithinRange_And_FilterSkipWithImported_Expect_CorrectResult() {
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT));
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID + 1, ImportDocumentStatus.IMPORT));
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID + 2, ImportDocumentStatus.SKIP));
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID + 3, ImportDocumentStatus.IMPORTED));
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID + 4, ImportDocumentStatus.IMPORTED));

		assertEquals(5, basicImportDocumentInfoService.getAll(DEFAULT_ID, DEFAULT_ID + 4).getSize());
		assertEquals(5, basicImportDocumentInfoService.getAll(DEFAULT_ID, DEFAULT_ID + 4, false, false).getSize());
		assertEquals(3, basicImportDocumentInfoService.getAll(DEFAULT_ID, DEFAULT_ID + 4, true, false).getSize());
		assertEquals(4, basicImportDocumentInfoService.getAll(DEFAULT_ID, DEFAULT_ID + 4, false, true).getSize());
		assertEquals(2, basicImportDocumentInfoService.getAll(DEFAULT_ID, DEFAULT_ID + 4, true, true).getSize());
	}

	@Test
	public void checkIfBasicImportDocumentInfoExistsOnNonExistingDocument_Expect_CorrectResult() {
		assertFalse(basicImportDocumentInfoService.exists(DEFAULT_ID));
	}

	@Test
	public void createBasicImportDocumentInfoAndCheckIfExists_Expect_CorrectResult() {
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT));
		assertTrue(basicImportDocumentInfoService.exists(DEFAULT_ID));
	}

	@Test
	public void checkIfDocumentImportedOnNonExisting_Expect_CorrectResult() {
		assertFalse(basicImportDocumentInfoService.isImported(DEFAULT_ID));
	}

	@Test
	public void checkIfDocumentImportedOnNonImportedDocument_Expect_CorrectResult() {
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT));
		assertFalse(basicImportDocumentInfoService.isImported(DEFAULT_ID));
	}

	@Test
	public void checkIfDocumentImported_When_ImportStatusImported_Expect_CorrectResult() {
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORTED));
		assertTrue(basicImportDocumentInfoService.isImported(DEFAULT_ID));
	}

	@Test
	public void toMetaIdFromId_When_InfoNonExists_Expect_CorrectResult() {
		assertNull(basicImportDocumentInfoService.toMetaId(DEFAULT_ID));
	}

	@Test
	public void toMetaIdFromId_When_ImportDocumentNotImportedYet_Expect_CorrectResult() {
		assertNotNull(basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORT));
		assertNull(basicImportDocumentInfoService.toMetaId(DEFAULT_ID));
	}

	@Test
	public void toMetaIdFromId_When_ImportDocumentImported_Expect_CorrectResult() {
		final BasicImportDocumentInfoDTO basicImportDocumentInfoDTO = basicImportDocumentInfoService.create(DEFAULT_ID, ImportDocumentStatus.IMPORTED);
		assertNotNull(basicImportDocumentInfoDTO);

		basicImportDocumentInfoDTO.setMetaId(DEFAULT_META_ID);
		basicImportDocumentInfoDTO.setStatus(ImportDocumentStatus.IMPORTED);
		basicImportDocumentInfoService.save(basicImportDocumentInfoDTO);

		final Integer metaId = basicImportDocumentInfoService.toMetaId(DEFAULT_ID);

		assertNotNull(metaId);
		assertEquals(DEFAULT_META_ID, metaId);
	}
}
