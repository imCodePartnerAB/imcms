package com.imcode.imcms.domain.component;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc2.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class SVNService {
	public static final String IMCODE_USERNAME_PROPERTY = "imcode:username";
	public static final String SVN_WEBAPP_PATH = "/src/main/webapp/";
	private static final Long LATEST_REVISION = -1L;
	private final SVNURL svnRepositoryURL;
	private final SVNRepository svnRepository;
	private final SvnOperationFactory svnOperationFactory;

	/**
	 * Retrieve file revisions starting from first one to latest. Use {@value SVN_WEBAPP_PATH} to compose path
	 *
	 * @param filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @return Collection of SVNLogEntry entities.
	 * @see SVNLogEntry
	 */
	public Collection<SVNLogEntry> getRevisions(String filePath) {
		try {
			final SvnLog svnLog = svnOperationFactory.createLog();

			svnLog.setSingleTarget(SvnTarget.fromURL(appendToSVNURL(filePath, true)));
			svnLog.setRevisionRanges(Collections.singleton(
					SvnRevisionRange.create(SVNRevision.create(1),
							SVNRevision.HEAD)));

			return svnLog.run(null);
		} catch (SVNException e) {
			throw new RuntimeException("Error receiving revisions", e);
		}
	}

	/**
	 * Get file of the latest revision from SVN.
	 *
	 * @param filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @return OutputStream that contains all bytes
	 */
	public OutputStream get(String filePath) {
		return getFile(filePath, LATEST_REVISION);
	}

	/**
	 * Get file of the specific revision from SVN.
	 *
	 * @param filePath filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @param revision revision number
	 * @return OutputStream that contains all bytes
	 * @see #getRevisions(String)
	 */
	public OutputStream get(String filePath, Long revision) {
		return getFile(filePath, (revision == null) ? LATEST_REVISION : revision);
	}

	/**
	 * Commit file to SVN with specified message and custom properties.
	 * If you want to commit newly created file you need to add it to SVN. Use {@link #add(Path)} or {@link #add(Set)}
	 *
	 * @param path       real path to local file. e.g. /home/imcms/v6/WEB-INF/templates/css/demo.css
	 * @param message    commit message that describes you actions.
	 * @param properties custom properties you want to put on. Can be null or empty. e.g. {@value IMCODE_USERNAME_PROPERTY}
	 */
	public void commit(Path path, String message, Map<String, String> properties) {
		try {
			final SvnCommit commit = svnOperationFactory.createCommit();

			commit.setSingleTarget(SvnTarget.fromFile(path.toFile()));
			commit.setCommitMessage(message);

			if (MapUtils.isNotEmpty(properties)) {
				for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
					commit.setRevisionProperty(propertyEntry.getKey(), SVNPropertyValue.create(propertyEntry.getValue()));
				}
			}

			commit.run();
		} catch (SVNException e) {
			throw new RuntimeException("Error performing commit", e);
		}
	}

	/**
	 * Adds file to SVN.
	 *
	 * @param path real path to local file. e.g. /home/imcms/v6/WEB-INF/templates/css/demo.css
	 */
	public void add(Path path) {
		addToSVN(Set.of(path));
	}

	/**
	 * Adds files to SVN.
	 *
	 * @param paths real paths to local files. e.g. /home/imcms/v6/WEB-INF/templates/css/demo.css
	 */
	public void add(Set<Path> paths) {
		addToSVN(paths);
	}

	/**
	 * Check whether the file/folder exists on SVN
	 *
	 * @param filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 */
	public boolean exists(String filePath) {
		return checkIfExists(filePath, null);
	}

	/**
	 * Check whether the file/folder exists on SVN with specific revision
	 *
	 * @param filePath filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @param revision revision number
	 */
	public boolean exists(String filePath, Long revision) {
		return checkIfExists(filePath, revision);
	}

	/**
	 * This method will create all directories that not exist. e.g. /src/main/webapp/WEB-INF/template/css/.
	 * Will be created ../WEB-INF/, ../WEB-INF/templates/, ../WEB-INF/templates/css/
	 *
	 * @param path folder or folders you want to create.
	 */
	public void createFolder(String path) {
		try {
			final SvnRemoteMkDir mkDir = svnOperationFactory.createMkDir();

			Path target = Path.of("/");
			for (Path value : Path.of(path)) {
				target = target.resolve(value);
				if (!exists(target.toString())) {
					mkDir.addTarget(SvnTarget.fromURL(appendToSVNURL(target.toString(), true)));
				}
			}

			if (!mkDir.getTargets().isEmpty())
				mkDir.run();
		} catch (SVNException e) {
			throw new RuntimeException("Cannot create folder", e);
		}
	}

	/**
	 * This method will delete provided directories or files. e.g. /src/main/webapp/WEB-INF/template/css/.
	 * Will be deleted ../css/ folder.
	 *
	 * @param paths folders or files you want to delete.
	 */
	public void delete(String... paths) {
		try {
			final SvnRemoteDelete svnRemoteDelete = svnOperationFactory.createRemoteDelete();

			for (String path : paths) {
				svnRemoteDelete.addTarget(SvnTarget.fromURL(appendToSVNURL(path, true)));
			}

			svnRemoteDelete.run();
		} catch (SVNException e) {
			throw new RuntimeException("Cannot delete folders", e);
		}
	}

	/**
	 * This method checks out the latest revision of target file/folder into destination folder.
	 *
	 * @param target      path related to the svn repository. Can be file/folder. e.g. /src/main/webapp/WEB-INF/template/css/
	 * @param destination real local path where you want to check out files
	 */
	public void checkout(Path target, Path destination) {
		try {
			final SvnCheckout checkout = svnOperationFactory.createCheckout();

			checkout.setSingleTarget(SvnTarget.fromFile(destination.toFile()));
			checkout.setSource(SvnTarget.fromURL(appendToSVNURL(target.toString(), true)));

			checkout.run();
		} catch (SVNException e) {
			throw new RuntimeException("Error performing checkout", e);
		}
	}

	private void addToSVN(Set<Path> paths) {
		try {
			final SvnScheduleForAddition svnAdd = svnOperationFactory.createScheduleForAddition();

			paths.forEach(path -> svnAdd.addTarget(SvnTarget.fromFile(path.toFile())));

			svnAdd.run();
		} catch (SVNException e) {
			throw new RuntimeException("Error adding to repository", e);
		}
	}

	private OutputStream getFile(String filePath, Long revision) {
		try {
			final SVNProperties fileProperties = new SVNProperties();
			final OutputStream outputStream = new ByteArrayOutputStream();

			svnRepository.getFile(filePath, revision, fileProperties, outputStream);

			return outputStream;
		} catch (SVNException e) {
			throw new RuntimeException("Cannot load file from svn repository", e);
		}
	}

	private boolean checkIfExists(String filePath, Long revision) {
		try {
			final SVNNodeKind kind = svnRepository.checkPath(filePath, revision == null ? LATEST_REVISION : revision);

			return kind == SVNNodeKind.FILE || kind == SVNNodeKind.DIR;
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}

	private SVNURL appendToSVNURL(String path, boolean uriEncoded) {
		try {
			return svnRepositoryURL.appendPath(path, uriEncoded);
		} catch (SVNException e) {
			throw new RuntimeException(
					String.format("Cannot append given path %s to SVN URL %s", path, svnRepositoryURL), e);
		}
	}
}
