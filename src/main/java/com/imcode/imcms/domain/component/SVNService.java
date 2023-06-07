package com.imcode.imcms.domain.component;

import com.imcode.imcms.config.SVNConfig;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Log4j2
@Component
public class SVNService {
	public static final String IMCODE_USERNAME_PROPERTY = "imcode:username";
	public static final String SVN_WEBAPP_PATH = "/src/main/webapp/";
	private static final Long LATEST_REVISION = -1L;
	private final SVNURL svnRepositoryURL;
	private final SVNRepository svnRepository;
	private final SvnOperationFactory svnOperationFactory;


	public SVNService(SVNConfig svnConfig) {
		this.svnRepositoryURL = svnConfig.getSvnRepositoryURL();
		this.svnRepository = svnConfig.getSvnRepository();
		this.svnOperationFactory = svnConfig.getSvnOperationFactory();
	}

	/**
	 * Retrieve file revisions starting from first one to latest. Use {@value SVN_WEBAPP_PATH} to compose path
	 *
	 * @param filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @return Collection of SVNLogEntry entities.
	 * @see SVNLogEntry
	 */
	public Collection<SVNLogEntry> getRevisions(String filePath) throws SVNException {
		final SvnLog svnLog = svnOperationFactory.createLog();

		svnLog.setSingleTarget(SvnTarget.fromURL(appendToSVNURL(filePath)));
		svnLog.setRevisionRanges(Collections.singleton(
				SvnRevisionRange.create(SVNRevision.create(1),
						SVNRevision.HEAD)));

		return svnLog.run(null);
	}

	/**
	 * Get file of the latest revision from SVN.
	 *
	 * @param filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @return OutputStream that contains all bytes
	 */
	public OutputStream get(String filePath) throws SVNException {
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
	public OutputStream get(String filePath, Long revision) throws SVNException {
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
	public void commit(Path path, String message, Map<String, String> properties) throws SVNException {
		final SvnCommit commit = svnOperationFactory.createCommit();

		commit.setSingleTarget(SvnTarget.fromFile(path.toFile()));
		commit.setCommitMessage(message);

		if (MapUtils.isNotEmpty(properties))
			commit.setRevisionProperties(SVNProperties.wrap(properties));

		commit.run();
	}

	public void move(String oldPath, String newPath, Map<String, String> properties, String message) throws SVNException {
		//fixme somehow svn preserves file history changes(revisions, messages etc) but file data on each specific revision lost
		final SvnRemoteCopy remoteCopy = svnOperationFactory.createRemoteCopy();

		remoteCopy.addCopySource(SvnCopySource.create(SvnTarget.fromURL(appendToSVNURL(oldPath)), SVNRevision.HEAD));
		remoteCopy.setSingleTarget(SvnTarget.fromURL(appendToSVNURL(newPath)));
		remoteCopy.setMove(true);
		remoteCopy.setCommitMessage(message);
		remoteCopy.setRevisionProperties(SVNProperties.wrap(properties));

		remoteCopy.run();
	}

	/**
	 * Adds file to SVN.
	 *
	 * @param path real path to local file. e.g. /home/imcms/v6/WEB-INF/templates/css/demo.css
	 */
	public void add(Path path) throws SVNException {
		addToSVN(Set.of(path));
	}

	/**
	 * Adds files to SVN.
	 *
	 * @param paths real paths to local files. e.g. /home/imcms/v6/WEB-INF/templates/css/demo.css
	 */
	public void add(Set<Path> paths) throws SVNException {
		addToSVN(paths);
	}

	/**
	 * Check whether the file/folder exists on SVN
	 *
	 * @param path accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 */
	public boolean exists(String path) throws SVNException {
		return checkIfExists(path, null);
	}

	/**
	 * Check whether the file/folder exists on SVN with specific revision
	 *
	 * @param filePath filePath accords to file path on SVN. e.g. ../src/main/webapp//WEB-INF/templates/css/demo.css
	 * @param revision revision number
	 */
	public boolean exists(String filePath, Long revision) throws SVNException {
		return checkIfExists(filePath, revision);
	}

	/**
	 * This method will create all directories that not exist. e.g. /src/main/webapp/WEB-INF/template/css/.
	 * Will be created ../WEB-INF/, ../WEB-INF/templates/, ../WEB-INF/templates/css/
	 *
	 * @param path folder or folders you want to create.
	 */
	public void createFolder(String path) throws SVNException {
		final SvnRemoteMkDir mkDir = svnOperationFactory.createMkDir();

		Path target = Path.of("/");
		for (Path value : Path.of(path)) {
			target = target.resolve(value);
			if (!exists(target.toString())) {
				mkDir.addTarget(SvnTarget.fromURL(appendToSVNURL(target.toString())));
			}
		}

		if (!mkDir.getTargets().isEmpty())
			mkDir.run();
	}

	/**
	 * This method will delete provided directories or files. e.g. /src/main/webapp/WEB-INF/template/css/.
	 * Will be deleted ../css/ folder.
	 *
	 * @param paths folders or files you want to delete.
	 */
	public void delete(String... paths) throws SVNException {
		final SvnRemoteDelete svnRemoteDelete = svnOperationFactory.createRemoteDelete();

		for (String path : paths) {
			svnRemoteDelete.addTarget(SvnTarget.fromURL(appendToSVNURL(path)));
		}

		svnRemoteDelete.run();
	}

	/**
	 * This method checks out the latest revision of target folder into destination folder.
	 *
	 * @param target      path related to the svn repository. e.g. /src/main/webapp/WEB-INF/template/css/
	 * @param destination real local path where you want to check out
	 */
	public void checkout(String target, Path destination) throws SVNException {
		final SvnCheckout checkout = svnOperationFactory.createCheckout();

		checkout.setSingleTarget(SvnTarget.fromFile(destination.toFile()));
		checkout.setSource(SvnTarget.fromURL(appendToSVNURL(target)));

		checkout.run();
	}

	private void addToSVN(Set<Path> paths) throws SVNException {
		final SvnScheduleForAddition svnAdd = svnOperationFactory.createScheduleForAddition();

		paths.forEach(path -> svnAdd.addTarget(SvnTarget.fromFile(path.toFile())));

		svnAdd.run();
	}

	private OutputStream getFile(String filePath, Long revision) throws SVNException {
		final SVNProperties fileProperties = new SVNProperties();
		final OutputStream outputStream = new ByteArrayOutputStream();

		svnRepository.getFile(filePath, revision, fileProperties, outputStream);

		return outputStream;
	}

	private boolean checkIfExists(String path, Long revision) throws SVNException {
		final SVNNodeKind kind = svnRepository.checkPath(path, revision == null ? LATEST_REVISION : revision);

		return kind == SVNNodeKind.FILE || kind == SVNNodeKind.DIR;
	}

	private SVNURL appendToSVNURL(String path) {
		try {
			return svnRepositoryURL.appendPath(path, true);
		} catch (SVNException e) {
			throw new RuntimeException(
					String.format("Cannot append given path %s to SVN URL %s", path, svnRepositoryURL), e);
		}
	}
}
