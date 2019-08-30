package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.PublicDocumentsCache;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TemporalDataService;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static imcode.server.ImcmsConstants.OTHER_CACHE_NAME;
import static imcode.server.ImcmsConstants.STATIC_CACHE_NAME;
import static net.sf.ehcache.CacheManager.getCacheManager;

//TODO cover by tests if possible
@Service
public class DefaultTemporalDataService implements TemporalDataService {

    private static final String REINDEX_DATE_REGEX = "(Last-date-reindex:)\\s+([\\d|-]*\\s+[\\d|:]*)";
    private static final String PUBLIC_DOC_CACHE_DATE_REGEX = "(Public-document-invalidate-cache-date:)\\s+([\\d|-]*\\s+[\\d|:]*)";
    private static final String STATIC_CONTENT_CACHE_DATE_REGEX = "(Static-content-invalidate-cache-date:)\\s+([\\d|-]*\\s+[\\d|:]*)";
    private static final String CONTENT_CACHE_DATE_REGEX = "(Content-invalidate-cache-date:)\\s+([\\d|-]*\\s+[\\d|:]*)";

    private final static Logger logger = Logger.getLogger(DefaultTemporalDataService.class);

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final PublicDocumentsCache publicDocumentsCache;
    private final ResolvingQueryIndex resolvingQueryIndex;

    private final Pattern patternReindexDate = Pattern.compile(REINDEX_DATE_REGEX);
    private final Pattern patternDocCacheDate = Pattern.compile(PUBLIC_DOC_CACHE_DATE_REGEX);
    private final Pattern patternStaticContentCacheDate = Pattern.compile(STATIC_CONTENT_CACHE_DATE_REGEX);
    private final Pattern patternContentCacheDate = Pattern.compile(CONTENT_CACHE_DATE_REGEX);

    private final DocumentIndexServiceOps documentIndexServiceOps;
    private final DocumentService<DocumentDTO> defaultDocumentService;

    @Value("/WEB-INF/logs/error.log")
    private Path path;

    public DefaultTemporalDataService(PublicDocumentsCache publicDocumentsCache, ResolvingQueryIndex resolvingQueryIndex, DocumentIndexServiceOps documentIndexServiceOps, DocumentService<DocumentDTO> defaultDocumentService) {
        this.publicDocumentsCache = publicDocumentsCache;
        this.resolvingQueryIndex = resolvingQueryIndex;
        this.documentIndexServiceOps = documentIndexServiceOps;
        this.defaultDocumentService = defaultDocumentService;
    }

    @Override
    public void invalidatePublicDocumentCache() {
        publicDocumentsCache.invalidateCache();
        logger.info("Public-document-invalidate-cache-date: " + formatter.format(new Date()));
    }

    @Override
    public void invalidateStaticContentCache() {
        getCacheManager(null).getEhcache(STATIC_CACHE_NAME).removeAll();
        logger.info("Static-content-invalidate-cache-date: " + formatter.format(new Date()));
    }

    @Override
    public void invalidateOtherContentCache() {
        getCacheManager(null).getEhcache(OTHER_CACHE_NAME).removeAll();
        logger.info("Content-invalidate-cache-date: " + formatter.format(new Date()));
    }

    @Override
    public long rebuildDocumentIndexAndGetDocumentsAmount() {
        resolvingQueryIndex.rebuild();
        logger.info("Last-date-reindex: " + formatter.format(new Date()));

        return defaultDocumentService.countDocuments();
    }

    @Override
    public long getAmountOfIndexedDocuments() {
        return documentIndexServiceOps.getAmountOfIndexedDocuments();
    }

    @Override
    public String getDateInvalidateDocumentCache() throws IOException {
        return getLastDateModification(patternDocCacheDate);
    }

    @Override
    public String getDateStaticContentCache() throws IOException {
        return getLastDateModification(patternStaticContentCacheDate);
    }

    @Override
    public String getDateInvalidateContentCache() throws IOException {
        return getLastDateModification(patternContentCacheDate);
    }

    @Override
    public String getDateDocumentReIndex() throws IOException {
        getCacheManager(null).getEhcache(OTHER_CACHE_NAME).removeAll();
        return getLastDateModification(patternReindexDate);
    }

    private String getLastDateModification(Pattern pattern) throws IOException {
        final List<String> validLines = new ArrayList<>();
        final List<String> logLines = Files.readAllLines(path);
        for (String line : logLines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                validLines.add(matcher.group(2));
            }
        }
        final long amountValidLines = validLines.size();
        String validDateLine = null;
        if (amountValidLines == 1) {
            validDateLine = validLines.get(0);
        } else if (amountValidLines > 1) {
            validDateLine = validLines.stream().skip(amountValidLines - 1).findFirst().get();
        }
        return validDateLine;
    }
}
