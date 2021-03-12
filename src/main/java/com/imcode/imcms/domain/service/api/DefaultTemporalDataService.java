package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TemporalDataService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.persistence.entity.DataOfTimeLastUseJPA;
import com.imcode.imcms.persistence.repository.TemporalTimeLastUseRepository;
import imcode.server.document.index.ResolvingQueryIndex;
import imcode.server.document.index.service.impl.DocumentIndexServiceOps;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.imcode.imcms.persistence.entity.Meta.DocumentType.TEXT;
import static com.imcode.imcms.persistence.entity.Meta.PublicationStatus.APPROVED;
import static imcode.server.ImcmsConstants.*;
import static net.sf.ehcache.CacheManager.getCacheManager;

@Service
@Transactional
public class DefaultTemporalDataService implements TemporalDataService {

    private final static Logger logger = Logger.getLogger(DefaultTemporalDataService.class);
    private final Integer IDENTIFIER_LAST_DATA_USE = 1;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final DocumentsCache publicDocumentsCache;
    private final ResolvingQueryIndex resolvingQueryIndex;
    private final DocumentMapper documentMapper;
    private final TemporalTimeLastUseRepository temporalTimeLastUseRepository;

    private final DocumentIndexServiceOps documentIndexServiceOps;
    private final DocumentService<DocumentDTO> defaultDocumentService;

    public DefaultTemporalDataService(DocumentsCache publicDocumentsCache,
                                      ResolvingQueryIndex resolvingQueryIndex,
                                      DocumentMapper documentMapper,
                                      TemporalTimeLastUseRepository temporalTimeLastUseRepository,
                                      DocumentIndexServiceOps documentIndexServiceOps,
                                      DocumentService<DocumentDTO> defaultDocumentService) {
        this.publicDocumentsCache = publicDocumentsCache;
        this.resolvingQueryIndex = resolvingQueryIndex;
        this.documentMapper = documentMapper;
        this.temporalTimeLastUseRepository = temporalTimeLastUseRepository;
        this.documentIndexServiceOps = documentIndexServiceOps;
        this.defaultDocumentService = defaultDocumentService;
    }

    @Override
    public void invalidatePublicDocumentCache() {
        publicDocumentsCache.invalidateCache();

        final DataOfTimeLastUseJPA updatedLastDate = getLastUseDateTime(PUBLIC_CACHE_NAME);

        logger.error("Public-document-invalidate-cache-date: " + formatter.format(updatedLastDate.getTimeLastRemovePublicCache()));
    }

    @Override
    public void invalidateStaticContentCache() {
        getCacheManager(null).getEhcache(STATIC_CACHE_NAME).removeAll();

        final DataOfTimeLastUseJPA updatedLastDate = getLastUseDateTime(STATIC_CACHE_NAME);

        logger.error("Static-content-invalidate-cache-date: " + formatter.format(updatedLastDate.getTimeLastRemoveStaticCache()));
    }

    @Override
    public void invalidateOtherContentCache() {
        getCacheManager(null).getEhcache(OTHER_CACHE_NAME).removeAll();

        final DataOfTimeLastUseJPA updatedLastDate = getLastUseDateTime(OTHER_CACHE_NAME);

        logger.error("Content-invalidate-cache-date: " + formatter.format(updatedLastDate.getTimeLastRemoveOtherCache()));
    }

    @Override
    public long rebuildDocumentIndexAndGetDocumentsAmount() {
        if (getAmountOfIndexedDocuments() == -1) {
            resolvingQueryIndex.rebuild();

            final DataOfTimeLastUseJPA updatedLastDate = getLastUseDateTime(REINDEX_NAME);
            logger.error("Last-date-reindex: " + formatter.format(updatedLastDate.getTimeLastReindex()));
        }

        return defaultDocumentService.countDocuments();
    }

    @Override
    public long getAmountOfIndexedDocuments() {
        return documentIndexServiceOps.getAmountOfIndexedDocuments();
    }

    @Override
    public String getDateInvalidateDocumentCache() {
        return formatter.format(temporalTimeLastUseRepository.findOne(IDENTIFIER_LAST_DATA_USE)
                .getTimeLastRemovePublicCache());
    }

    @Override
    public String getDateStaticContentCache() {
        return formatter.format(temporalTimeLastUseRepository.findOne(IDENTIFIER_LAST_DATA_USE)
                .getTimeLastRemoveStaticCache());
    }

    @Override
    public String getDateInvalidateContentCache() {
        return formatter.format(temporalTimeLastUseRepository.findOne(IDENTIFIER_LAST_DATA_USE)
                .getTimeLastRemoveOtherCache());
    }

    @Override
    public String getDateDocumentReIndex() {
        return formatter.format(temporalTimeLastUseRepository.findOne(IDENTIFIER_LAST_DATA_USE)
                .getTimeLastReindex());
    }

    @Override
    public String getDateAddedInCacheDocuments() {
        return formatter.format(temporalTimeLastUseRepository.findOne(IDENTIFIER_LAST_DATA_USE)
                .getTimeLastBuildCache());
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
            final DataOfTimeLastUseJPA updatedLastDate = getLastUseDateTime(BUILD_CACHE_NAME);
            logger.error("Last-date-recache: " + formatter.format(updatedLastDate.getTimeLastBuildCache()));
            publicDocumentsCache.setAmountOfCachedDocuments(-1);
        }
    }

    @Override
    public int getTotalAmountTextDocDataForCaching() {
        return getCountPublishedTextDocIdAndAlias().size();
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

    private DataOfTimeLastUseJPA getLastUseDateTime(String nameDate) {
        DataOfTimeLastUseJPA updatedLastDate = new DataOfTimeLastUseJPA();
        final Date currentDate = new Date();

        DataOfTimeLastUseJPA receivedDate = temporalTimeLastUseRepository.findOne(IDENTIFIER_LAST_DATA_USE);
        final Integer id = receivedDate.getId();
        final Date timeLastRemovePublicCache = receivedDate.getTimeLastRemovePublicCache();
        final Date timeLastReindex = receivedDate.getTimeLastReindex();
        final Date timeLastRemoveStaticCache = receivedDate.getTimeLastRemoveStaticCache();
        final Date timeLastRemoveOtherCache = receivedDate.getTimeLastRemoveOtherCache();
        final Date timeLastBuildCache = receivedDate.getTimeLastBuildCache();

        switch (nameDate) {
            case PUBLIC_CACHE_NAME:
                updatedLastDate = temporalTimeLastUseRepository.saveAndFlush(new DataOfTimeLastUseJPA(
                        id, timeLastReindex, currentDate,
                        timeLastRemoveStaticCache, timeLastRemoveOtherCache, timeLastBuildCache
                ));
                break;
            case STATIC_CACHE_NAME:

                updatedLastDate = temporalTimeLastUseRepository.saveAndFlush(new DataOfTimeLastUseJPA(
                        id, timeLastReindex, timeLastRemovePublicCache,
                        currentDate, timeLastRemoveOtherCache, timeLastBuildCache
                ));
                break;
            case OTHER_CACHE_NAME:

                updatedLastDate = temporalTimeLastUseRepository.saveAndFlush(new DataOfTimeLastUseJPA(
                        id, timeLastReindex, timeLastRemovePublicCache,
                        timeLastRemoveStaticCache, currentDate, timeLastBuildCache
                ));
                break;
            case REINDEX_NAME:
                updatedLastDate = temporalTimeLastUseRepository.saveAndFlush(new DataOfTimeLastUseJPA(
                        id, currentDate, timeLastRemovePublicCache,
                        timeLastRemoveStaticCache, timeLastRemoveOtherCache,timeLastBuildCache
                ));
                break;
            case BUILD_CACHE_NAME:
                updatedLastDate = temporalTimeLastUseRepository.saveAndFlush(new DataOfTimeLastUseJPA(
                        id, timeLastReindex, timeLastRemovePublicCache,
                        timeLastRemoveStaticCache, timeLastRemoveOtherCache, currentDate
                ));
                break;
        }

        return updatedLastDate;
    }
}

