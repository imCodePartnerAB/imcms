package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TemporalDataService;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Meta.DocumentType.TEXT;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.APPROVED;
import static imcode.server.ImcmsConstants.IMCMS_HEADER_CACHING_ACTIVE;
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
    private static final String RECACHE_DATE_REGEX = "(Last-date-recache:)\\s+([\\d|-]*\\s+[\\d|:]*)";

    private final static Logger logger = Logger.getLogger(DefaultTemporalDataService.class);

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final DocumentsCache publicDocumentsCache;
    private final ResolvingQueryIndex resolvingQueryIndex;
    private final DocumentMapper documentMapper;

    private final Pattern patternReindexDate = Pattern.compile(REINDEX_DATE_REGEX);
    private final Pattern patternDocCacheDate = Pattern.compile(PUBLIC_DOC_CACHE_DATE_REGEX);
    private final Pattern patternStaticContentCacheDate = Pattern.compile(STATIC_CONTENT_CACHE_DATE_REGEX);
    private final Pattern patternContentCacheDate = Pattern.compile(CONTENT_CACHE_DATE_REGEX);
    private final Pattern patternReCacheDate = Pattern.compile(RECACHE_DATE_REGEX);

    private final DocumentIndexServiceOps documentIndexServiceOps;
    private final DocumentService<DocumentDTO> defaultDocumentService;

    @Value("/WEB-INF/logs/error.log")
    private Path path;

    public DefaultTemporalDataService(DocumentsCache publicDocumentsCache,
                                      ResolvingQueryIndex resolvingQueryIndex,
                                      DocumentMapper documentMapper,
                                      DocumentIndexServiceOps documentIndexServiceOps,
                                      DocumentService<DocumentDTO> defaultDocumentService) {
        this.publicDocumentsCache = publicDocumentsCache;
        this.resolvingQueryIndex = resolvingQueryIndex;
        this.documentMapper = documentMapper;
        this.documentIndexServiceOps = documentIndexServiceOps;
        this.defaultDocumentService = defaultDocumentService;
    }

    @Override
    public void invalidatePublicDocumentCache() {
        publicDocumentsCache.invalidateCache();
        logger.error("Public-document-invalidate-cache-date: " + formatter.format(new Date()));
    }

    @Override
    public void invalidateStaticContentCache() {
        getCacheManager(null).getEhcache(STATIC_CACHE_NAME).removeAll();
        logger.error("Static-content-invalidate-cache-date: " + formatter.format(new Date()));
    }

    @Override
    public void invalidateOtherContentCache() {
        getCacheManager(null).getEhcache(OTHER_CACHE_NAME).removeAll();
        logger.error("Content-invalidate-cache-date: " + formatter.format(new Date()));
    }

    @Override
    public long rebuildDocumentIndexAndGetDocumentsAmount() {
        if (getAmountOfIndexedDocuments() == -1) {
            resolvingQueryIndex.rebuild();
            logger.error("Last-date-reindex: " + formatter.format(new Date()));
        }

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
        return getLastDateModification(patternReindexDate);
    }

    @Override
    public String getDateAddedInCacheDocuments() throws IOException {
        return getLastDateModification(patternReCacheDate);
    }

    @Override
    public void addDocumentsInCache(HttpServletRequest request) {
        if (publicDocumentsCache.getAmountOfCachedDocuments() == -1) {
            publicDocumentsCache.setAmountOfCachedDocuments(0);
            final HttpHeaders headers = new HttpHeaders();
            headers.set(IMCMS_HEADER_CACHING_ACTIVE, Boolean.toString(true));

            final HttpEntity httpEntity = new HttpEntity(headers);
            final int serverPort = request.getServerPort();
            final List<String> docIdsAndAlias = getCountPublishedTextDocIdAndAlias();

            String path = null;

            for (int i = 0; i < docIdsAndAlias.size(); i++) {
                final String docData = docIdsAndAlias.get(i);
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    if ((serverPort == 80) || (serverPort == 443)) {
                        path = String.format("%s://%s/", request.getScheme(), request.getServerName()) + docData;
                    } else {
                        path = String.format("%s://%s:%s/", request.getScheme(), request.getServerName(), serverPort) + docData;
                    }

                    logger.info("Will call request by URL " + path);
                    restTemplate.exchange(path, HttpMethod.GET, httpEntity, String.class);
                    publicDocumentsCache.setAmountOfCachedDocuments(i + 1);
                } catch (RestClientException r) {
                    logger.error(String.format("Not connect on the path URL %s !", path));
                }
            }
            logger.error("Last-date-recache: " + formatter.format(new Date()));
            publicDocumentsCache.setAmountOfCachedDocuments(-1);

        }
    }

    @Override
    public int getTotalAmountTextDocDataForCaching() {
        return getCountPublishedTextDocIdAndAlias().size();
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

    private List<String> getCountPublishedTextDocIdAndAlias() {
        final List<String> docIdsAndAlias = new ArrayList<>();
        final List<DocumentDTO> documentsDTO = documentMapper.getAllDocumentIds().stream()
                .map(defaultDocumentService::get)
                .filter(doc -> doc.getCommonContents().stream().findAny().get().isEnabled())
                .filter(doc -> doc.getType().equals(TEXT))
                .filter(doc -> doc.getPublicationStatus().equals(APPROVED))
                .collect(Collectors.toList());

        for (DocumentDTO documentDTO : documentsDTO) {
            if (StringUtils.isNotBlank(documentDTO.getAlias())) {
                docIdsAndAlias.add(documentDTO.getAlias());
            }
            docIdsAndAlias.add(documentDTO.getId().toString());
        }

        return docIdsAndAlias;
    }
}
