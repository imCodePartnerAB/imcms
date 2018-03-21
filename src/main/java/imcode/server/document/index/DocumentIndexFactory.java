package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.document.index.service.SolrClientFactory;
import imcode.server.document.index.service.impl.DocumentIndexRebuildService;
import imcode.server.document.index.service.impl.DocumentIndexServiceFactory;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class DocumentIndexFactory {

    private static Logger logger = Logger.getLogger(DocumentIndexFactory.class);
    private final DocumentIndexServiceFactory documentIndexServiceFactory;
    private final Config config;

    public DocumentIndexFactory(DocumentIndexServiceFactory documentIndexServiceFactory, Config config) {
        this.documentIndexServiceFactory = documentIndexServiceFactory;
        this.config = config;
    }

    public DocumentIndex create() {
        final Function<String, Optional<String>> trimToOption = s -> Optional.ofNullable(s)
                .map(String::trim)
                .filter(str -> str.length() > 0);

        final Optional<String> oSolrUrl = trimToOption.apply(config.getSolrUrl());
        final Optional<String> oSolrHome = trimToOption.apply(config.getSolrHome());
        final long periodInMinutes = config.getIndexingSchedulePeriodInMinutes();

        final String solrPath;
        final BiFunction<String, Boolean, SolrClient> solrClientFactory;

        if (oSolrUrl.isPresent()) {
            solrPath = oSolrUrl.get();
            solrClientFactory = SolrClientFactory::createHttpSolrClient;

        } else if (oSolrHome.isPresent()) {
            solrPath = oSolrHome.get();
            solrClientFactory = SolrClientFactory::createEmbeddedSolrClient;

        } else {
            final String errMsg = "Configuration error. Unable to create DocumentIndex.\n"
                    + "Neither Config.solrUrl nor Config.solrHome is set.\n"
                    + "Set Config.solrUrl for remote SOLr server or Config.solrHome for internal SOLr server.\n"
                    + "If both are set then Config.solrUrl takes precedence and Config.solrHome is ignored.";

            logger.fatal(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        return new DocumentIndexImpl(new DocumentIndexRebuildService(
                solrPath, solrClientFactory, periodInMinutes, documentIndexServiceFactory
        ));
    }

}
