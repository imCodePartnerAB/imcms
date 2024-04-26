package imcode.server.document.index;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import imcode.server.document.index.service.AddDocToIndex;
import imcode.server.document.index.service.DeleteDocFromIndex;
import imcode.server.document.index.service.DocumentIndexService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

public class ImageFileIndexImpl implements ImageFileIndex {

	private static final Logger logger = LogManager.getLogger(ImageFileIndexImpl.class);
	private final DocumentIndexService service;

	public ImageFileIndexImpl(DocumentIndexService service) {
		this.service = service;
	}

	@Override
	public IndexSearchResult<ImageFileStoredFields> search(SolrQuery query) throws IndexException {
		final QueryResponse queryResponse = service.query(query);
		return new ImageFileIndexSearchResult(query, queryResponse);
	}

	@Override
	public void indexImageFile(String path) {
		service.update(new AddDocToIndex(path));
	}

	@Override
	public void indexImageFile(ImageFileDTO imageFile) {
		service.update(new AddDocToIndex(imageFile.getPath()));
	}

	@Override
	public void removeImageFile(String path) {
		if (path.startsWith("/")){
			path = path.substring(1);
		}
		service.update(new DeleteDocFromIndex(path));
	}

	@Override
	public void rebuild() {
		service.rebuild();
	}

	@Override
	public DocumentIndexService getService() {
		return service;
	}
}
