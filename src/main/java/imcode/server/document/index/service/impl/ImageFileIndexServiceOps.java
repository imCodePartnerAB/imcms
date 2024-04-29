package imcode.server.document.index.service.impl;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.storage.StorageClient;
import com.imcode.imcms.storage.StoragePath;
import imcode.server.document.index.ImageFileIndex;
import imcode.server.document.index.service.IndexServiceOps;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

@Component
public class ImageFileIndexServiceOps implements IndexServiceOps {

	private static final Logger logger = LogManager.getLogger(ImageFileIndexServiceFactory.class);

	private final ImageFolderService imageFolderService;
	private final ImageFileService imageFileService;
	private final StorageClient storageClient;
	private final String imagesPath;

	private final AtomicLong indexedDocumentsAmount = new AtomicLong(-1);
	private final Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO;

	public ImageFileIndexServiceOps(@Qualifier("storagePathToImageFileDTO") Function<StoragePath, ImageFileDTO> storagePathToImageFileDTO,
	                                ImageFolderService imageFolderService,
	                                ImageFileService imageFileService,
	                                @Qualifier("imageStorageClient") StorageClient storageClient,
	                                @Value("${ImagePath}") String imagesPath) {

		this.imageFolderService = imageFolderService;
		this.imageFileService = imageFileService;
		this.storageClient = storageClient;
		this.imagesPath = imagesPath;
		this.storagePathToImageFileDTO = storagePathToImageFileDTO;
	}

	@Override
	public QueryResponse query(SolrClient solrClient, SolrQuery solrQuery) throws SolrServerException, IOException {
		return solrClient.query(solrQuery);
	}

	@Override
	public void rebuildIndex(SolrClient solrClient) {
		rebuildIndex(solrClient, indexRebuildProgress -> {
		});

	}

	@SneakyThrows
	private void rebuildIndex(SolrClient solrClient, Consumer<IndexRebuildProgress> progressCallback) {
		logger.debug("Rebuilding image files index.");

//		final List<ImageFileDTO> files = getAllImageFiles();
		final List<ImageFileDTO> files = walkImageFolder();

		final int size = files.size();
		int docNo = 0;
		final Date rebuildStartDt = new Date();
		final long rebuildStartTime = rebuildStartDt.getTime();

		indexedDocumentsAmount.set(0);

		for (ImageFileDTO imageFile : files) {
			if (Thread.interrupted()) {
				solrClient.rollback();
				throw new InterruptedException();
			}

			SolrInputDocument solrInputDoc = toSolrInputDocument(imageFile);
			if (solrInputDoc != null) {
				solrClient.add(solrInputDoc);
				logger.debug("Added image file doc with id {} to index.", imageFile.getPath());
			}

			indexedDocumentsAmount.incrementAndGet();
			docNo += 1;
			progressCallback.accept(new IndexRebuildProgress(rebuildStartTime, System.currentTimeMillis(), size, docNo));
		}
		logger.debug("Deleting old image file documents from index.");

		//todo think
		solrClient.deleteByQuery(String.format("timestamp:{* TO %s}", rebuildStartDt.toInstant().toString()));
		solrClient.commit();
		indexedDocumentsAmount.set(-1);

		logger.debug("Index rebuild is complete.");
	}

	private List<ImageFileDTO> walkImageFolder() {
		final ImageFolderDTO imageFolder = imageFolderService.getImageFolder();
		final List<ImageFileDTO> files = new ArrayList<>();

		walkImageFolderRecursively(imageFolder, files);

		return files;
	}

	private List<ImageFileDTO> getAllImageFiles() {
		return storageClient.walk(StoragePath.get(DIRECTORY, imagesPath)).stream()
				.filter(storagePath -> storagePath.getType().equals(FILE))
				.map(storagePathToImageFileDTO)
				.toList();
	}

	private void walkImageFolderRecursively(ImageFolderDTO imageFolder, List<ImageFileDTO> files) {
		final List<ImageFileDTO> imageFiles = imageFolder.getFiles().isEmpty() ? imageFolderService.getImagesFrom(imageFolder).getFiles() : imageFolder.getFiles();
		files.addAll(imageFiles);

		for (ImageFolderDTO subfolder : imageFolder.getFolders()) {
			walkImageFolderRecursively(subfolder, files);
		}
	}

	@Override
	public void addToIndex(SolrClient solrClient, String path) throws SolrServerException, IOException {
		final SolrInputDocument solrInputDocument = toSolrInputDocument(path);

		if (solrInputDocument != null) {
			solrClient.add(solrInputDocument);
			solrClient.commit(false, false, true);

			logger.error(String.format("Added solrInputDoc with image file id %s into the index.", path));
		}
	}

	@Override
	public void updateDocumentVersionInIndex(SolrClient solrClient, String id) throws SolrServerException, IOException {
		throw new UnsupportedOperationException("Document version update not supported in imageFiles index.");
	}

	@Override
	public void deleteFromIndex(SolrClient solrClient, String path) throws SolrServerException, IOException {
		final String query = toDeleteDocSolrQuery(path);

		solrClient.deleteByQuery(query);
		solrClient.commit(false, false, true);
		logger.info(String.format("Removed image file metadata document with path %s from index.", path));
	}

	private String toDeleteDocSolrQuery(String path) {
		return String.format("%s:%s", ImageFileIndex.FIELD__ID, path);
	}

	@Override
	public long getAmountOfIndexedDocuments() {
		return indexedDocumentsAmount.get();
	}

	private SolrInputDocument toSolrInputDocument(String path) {
		try {
			return imageFileService.index(path);
		} catch (Exception e) {
			logger.error(String.format("Can`t create SolrInputDocument from ImageFile with path: %s", path), e);
			return null;
		}
	}

	private SolrInputDocument toSolrInputDocument(ImageFileDTO imageFileDTO) {
		try {
			return imageFileService.index(imageFileDTO);
		} catch (Exception e) {
			logger.error(String.format("Can`t create SolrInputDocument from ImageFile with path: %s", imageFileDTO.getPath()), e);
			return null;
		}
	}

}
