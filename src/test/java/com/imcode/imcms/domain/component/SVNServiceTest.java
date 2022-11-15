package com.imcode.imcms.domain.component;

import com.imcode.imcms.WebAppSpringTestConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.SVNLogEntry;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import static com.imcode.imcms.domain.component.SVNService.SVN_WEBAPP_PATH;
import static org.junit.jupiter.api.Assertions.*;

public class SVNServiceTest extends WebAppSpringTestConfig {

	@Autowired
	private SVNService svnService;

	private static final String filesFolder = "WEB-INF/test-svn/files/";
	private static final Path filesFolderPath = Path.of(filesFolder);
	private static final String folderName = "folder";
	private static final String folderName2 = "folder1";
	private static final String folderName3 = "folder2";
	private static final String longFolderName = String.format("/%s/%s/%s/", folderName, folderName2, folderName3);

	@PostConstruct
	@SneakyThrows
	private void init() {
		Files.createDirectories(filesFolderPath);
		svnService.createFolder(filesFolder);
		svnService.checkout(filesFolderPath, filesFolderPath);
	}

	@Test
	public void checkIfFolderExists_When_NoFolderAvailable_Expect_CorrectResult() {
		assertFalse(svnService.exists(folderName));
	}

	@Test
	public void createThenDeleteFolder_Expect_CorrectResult() {
		final String folderSVNPath = SVN_WEBAPP_PATH + folderName;

		svnService.createFolder(folderSVNPath);
		assertTrue(svnService.exists(folderSVNPath));

		svnService.delete(folderSVNPath);
		assertFalse(svnService.exists(folderSVNPath));
	}

	@Test
	public void createThenDeleteFolders_Expect_CorrectResults() {
		final String folder1 = folderName;
		final String folder2 = String.format("%s/%s", folder1, folderName2);
		final String folder3 = String.format("%s/%s", folder2, folderName3);

		svnService.createFolder(longFolderName);

		assertTrue(svnService.exists(folder1));
		assertTrue(svnService.exists(folder2));
		assertTrue(svnService.exists(folder3));

		svnService.delete(folder1);
		assertFalse(svnService.exists(folder1));
		assertFalse(svnService.exists(folder2));
		assertFalse(svnService.exists(folder3));
	}

	@Test
	public void addFile_Expect_CorrectResul() {
		try {
			final Path path = Files.createTempFile(filesFolderPath, null, null);

			assertDoesNotThrow(() -> svnService.add(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void addFile_WhenThereAreNoSuchFile_Expect_CorrectResul() {
		assertThrows(RuntimeException.class, () -> svnService.add(filesFolderPath.resolve("file1.tmp")));
	}

	@Test
	public void checkoutFolder_Expect_CorrectResult() {
		assertDoesNotThrow(() -> svnService.checkout(filesFolderPath, filesFolderPath));
	}

	@Test
	public void addThenCommitFileThatExists_Expect_CorrectResults() {
		try {
			final Path path = Files.createTempFile(filesFolderPath.toAbsolutePath(), null, null);
			final String fileSVNPath = filesFolder + path.getFileName();

			svnService.add(path);
			svnService.commit(path, "message", null);

			assertTrue(svnService.exists(fileSVNPath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void commitLocalFileWithoutAddingIt_Expect_Correct_Exception() {
		try {
			final Path path = Files.createTempFile(filesFolderPath, null, null);

			assertThrows(RuntimeException.class, () -> svnService.commit(path, "message", null));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void addCommitUpdateGetFile_Expect_CorrectResults() {
		try {
			final Path path = Files.createTempFile(filesFolderPath.toAbsolutePath(), null, null);
			final String fileSVNPath = filesFolder + path.getFileName();
			final String fileData = "TEXT";

			svnService.add(path);
			svnService.commit(path, "message", null);

			assertNotEquals(fileData, svnService.get(fileSVNPath).toString());
			assertEquals("", svnService.get(fileSVNPath).toString());

			Files.writeString(path, fileData);
			svnService.commit(path, "changes", null);

			assertEquals(fileData, svnService.get(fileSVNPath).toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@SneakyThrows
	public void addCommitUpdateGetHistory_And_CompareRevisions_Expect_CorrectResults() {
		final Path path = createTempFile();
		final String fileSVNPath = filesFolder + path.getFileName();
		final String fileData0 = "";
		final String fileData1 = "TEXT1";
		final String fileData2 = "TEXT2";

		svnService.add(path);
		svnService.commit(path, "changes", null);

		Files.writeString(path, fileData1);
		svnService.commit(path, "changes1", null);

		Files.writeString(path, fileData2);
		svnService.commit(path, "changes2", null);

		final LinkedList<SVNLogEntry> entries = (LinkedList<SVNLogEntry>) svnService.getRevisions(fileSVNPath);

		assertEquals(3, entries.size());

		assertEquals(fileData0, svnService.get(fileSVNPath, entries.get(0).getRevision()).toString());
		assertEquals(fileData1, svnService.get(fileSVNPath, entries.get(1).getRevision()).toString());
		assertEquals(fileData2, svnService.get(fileSVNPath, entries.get(2).getRevision()).toString());
	}

	private Path createTempFile() {
		try {
			return Files.createTempFile(filesFolderPath.toAbsolutePath(), null, null);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
