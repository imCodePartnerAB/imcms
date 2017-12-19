package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.ImcmsServices;
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile;
import imcode.server.document.index.service.DocumentIndexService;
import imcode.server.document.index.service.impl.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

// translated from scala...
public class DocumentIndexFactory {

    private static Logger logger = Logger.getLogger(DocumentIndexFactory.class);

    public static DocumentIndex create(ImcmsServices services) {
        final Config config = services.getConfig();
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
                    solrUrl, solrUrl, createDocumentIndexServiceOps(services), periodInMinutes
            );

        } else if (oSolrHome.isPresent()) {
            final String solrHome = oSolrHome.get();

            service = new InternalDocumentIndexServiceScheduler(
                    solrHome, createDocumentIndexServiceOps(services), periodInMinutes
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

    private static DocumentIndexServiceOps createDocumentIndexServiceOps(ImcmsServices services) {

        final Config config = services.getConfig();
        final Set<String> disabledFileExtensions = config.getIndexDisabledFileExtensionsAsSet();
        final Set<String> disabledFileMimes = config.getIndexDisabledFileMimesAsSet();
        final boolean noIgnoredFileNamesAndExtensions = disabledFileExtensions.isEmpty() && disabledFileMimes.isEmpty();

        final Predicate<FileDocumentFile> fileDocFileFilter = fileDocumentFile -> {
            if (noIgnoredFileNamesAndExtensions) {
                return true;

            } else {
                final String ext = getExtension(fileDocumentFile.getFilename());
                final String mime = getExtension(fileDocumentFile.getMimeType());

                return !(disabledFileExtensions.contains(ext) || disabledFileMimes.contains(mime));
            }
        };

        return new DocumentIndexServiceOps(services.getDocumentMapper(),
                new DocumentIndexer(
                        services.getCategoryMapper(),
                        new DocumentContentIndexer(fileDocFileFilter)
                )
        );
    }

    private static String getExtension(String filename) {
        return FilenameUtils.getExtension(StringUtils.trimToEmpty(filename)).toLowerCase();
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
