package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import imcode.server.document.index.service.impl.IndexRebuildScheduler;
import imcode.server.document.index.service.impl.InternalDocumentIndexService;
import imcode.server.document.index.service.impl.RemoteDocumentIndexService;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.function.Function;

// translated from scala...
public class DocumentIndexFactory {

    private static Logger logger = Logger.getLogger(DocumentIndexFactory.class);

    public static DocumentIndex create(Config config, DocumentIndexServiceOps documentIndexServiceOps) {
        final Function<String, Optional<String>> trimToOption = s -> Optional.ofNullable(s)
                .map(String::trim)
                .filter(str -> str.length() > 0);

        final Optional<String> oSolrUrl = trimToOption.apply(config.getSolrUrl());
        final Optional<String> oSolrHome = trimToOption.apply(config.getSolrHome());
        final long periodInMinutes = config.getIndexingSchedulePeriodInMinutes();

        final DocumentIndexService service;

        if (oSolrUrl.isPresent()) {
            final String solrUrl = oSolrUrl.get();

            service = new RemoteDocumentIndexServiceScheduler(
                    solrUrl, solrUrl, documentIndexServiceOps, periodInMinutes
            );

        } else if (oSolrHome.isPresent()) {
            final String solrHome = oSolrHome.get();

            service = new InternalDocumentIndexServiceScheduler(
                    solrHome, documentIndexServiceOps, periodInMinutes
            );

        } else {
            final String errMsg = "Configuration error. Unable to create DocumentIndex.\n"
                    + "Neither Config.solrUrl nor Config.solrHome is set.\n"
                    + "Set Config.solrUrl for remote SOLr server or Config.solrHome for internal SOLr server.\n"
                    + "If both are set then Config.solrUrl takes precedence and Config.solrHome is ignored.";

            logger.fatal(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        return new DocumentIndexImpl(service);
    }

    static class RemoteDocumentIndexServiceScheduler extends RemoteDocumentIndexService implements IndexRebuildScheduler {
        RemoteDocumentIndexServiceScheduler(String solrReadUrl, String solrWriteUrl, DocumentIndexServiceOps serviceOps, long periodInMinutes) {
            super(solrReadUrl, solrWriteUrl, serviceOps);
            setRebuildIntervalInMinutes(periodInMinutes);
        }
    }

    static class InternalDocumentIndexServiceScheduler extends InternalDocumentIndexService implements IndexRebuildScheduler {
        InternalDocumentIndexServiceScheduler(String solrHome, DocumentIndexServiceOps serviceOps, long periodInMinutes) {
            super(solrHome, serviceOps);
            setRebuildIntervalInMinutes(periodInMinutes);
        }
    }

}
