package imcode.server.document.index;

import com.imcode.imcms.domain.dto.ImageFileDTO;
import imcode.server.document.index.service.DocumentIndexService;
import org.apache.solr.client.solrj.SolrQuery;

public interface ImageFileIndex {
	String FIELD__ID = "id";
	String FIELD__NAME = "name";
	String FIELD__PATH = "path";
	String FIELD__UPLOADED = "uploaded";
	String FIELD__SIZE = "size";
//	String FIELD__RESOLUTION = "resolution";
	String FIELD__WIDTH = "width";
	String FIELD__HEIGHT = "height";
	String FIELD__ALL_EXIF = "all_exif";
	String FIELD__PHOTOGRAPHER = "photographer";
	String FIELD__UPLOADED_BY = "uploaded_by";
	String FIELD__COPYRIGHT = "copyright";
	String LICENSE_PERIOD_START = "license_period_start";
	String LICENSE_PERIOD_END = "license_period_end";
	String ALT_TEXT = "alt_text";
	String DESCRIPTION_TEXT = "description_text";

	IndexSearchResult<ImageFileStoredFields> search(SolrQuery query) throws IndexException;

	void indexImageFile(String path);

	void indexImageFile(ImageFileDTO imageFile);

	void removeImageFile(String path);

	void rebuild();

	DocumentIndexService getService();
}
