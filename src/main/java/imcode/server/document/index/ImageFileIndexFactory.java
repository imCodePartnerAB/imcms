package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.document.index.service.IndexServiceFactory;
import imcode.server.document.index.service.SolrClientFactory;
import imcode.server.document.index.service.impl.DocumentIndexRebuildService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class ImageFileIndexFactory {

	private static Logger logger = LogManager.getLogger(ImageFileIndexFactory.class);
	private final Config config;
	private final String imageFilesMetadataSolrUrl;
	private final IndexServiceFactory imageFileIndexServiceFactory;

	public ImageFileIndexFactory(@Value("${ImageFilesMetadataSolrUrl}") String imageFilesMetadataSolrUrl,
	                             Config config,
	                             IndexServiceFactory imageFileIndexServiceFactory) {
		super();
		this.config = config;
		this.imageFilesMetadataSolrUrl = imageFilesMetadataSolrUrl;
		this.imageFileIndexServiceFactory = imageFileIndexServiceFactory;
	}

	public ImageFileIndex create() {
		final Function<String, Optional<String>> trimToOption = s -> Optional.ofNullable(s)
				.map(String::trim)
				.filter(str -> str.length() > 0);

		final Optional<String> oSolrUrl = trimToOption.apply(imageFilesMetadataSolrUrl);
		final Optional<String> oSolrHome = trimToOption.apply(config.getSolrHome());
		final long periodInMinutes = config.getIndexingSchedulePeriodInMinutes();

		final String solrPath;
		final BiFunction<String, Boolean, SolrClient> solrClientFactory;

		if (oSolrUrl.isPresent()) {
			solrPath = oSolrUrl.get();
			solrClientFactory = SolrClientFactory::createHttpSolrClient;

		} else if (oSolrHome.isPresent()) {
			solrPath = oSolrHome.get() + '/' + SolrClientFactory.DEFAULT_IMAGE_FILE_METADATA_CORE_NAME;
			solrClientFactory = SolrClientFactory::createEmbeddedSolrClient;

		} else {
			final String errMsg = "Configuration error. Unable to create DocumentIndex.\n"
					+ "Neither Config.solrUrl nor Config.solrHome is set.\n"
					+ "Set Config.solrUrl for remote SOLr server or Config.solrHome for internal SOLr server.\n"
					+ "If both are set then Config.solrUrl takes precedence and Config.solrHome is ignored.";

			logger.fatal(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		return new ImageFileIndexImpl(new DocumentIndexRebuildService(
				solrPath, solrClientFactory, periodInMinutes, imageFileIndexServiceFactory
		));
	}
}
