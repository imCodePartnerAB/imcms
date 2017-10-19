package imcode.server.document.index.service;

import com.imcode.imcms.util.Value;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.core.CoreContainer;

import java.io.File;

import static java.lang.String.format;

public class SolrServerFactory {

    public static final String DEFAULT_CORE_NAME = "core";
    public static final String DEFAULT_DATA_DIR_NAME = "data";
    private static final Logger logger = Logger.getLogger(SolrServerFactory.class);

    public static HttpSolrServer createHttpSolrServer(String solrUrl) {
        return Value.with(new HttpSolrServer(solrUrl), solr ->
                solr.setRequestWriter(new BinaryRequestWriter())
        );
    }

    public static EmbeddedSolrServer createEmbeddedSolrServer(String solrHome) {
        return createEmbeddedSolrServer(solrHome, false);
    }

    public static EmbeddedSolrServer createEmbeddedSolrServer(String solrHome, boolean recreateDataDir) {
        logger.info(format("Creating embedded SOLr server. Solr home: %s, recreateDataDir: %s.",
                solrHome, recreateDataDir));

        if (recreateDataDir) {
            File dataDir = new File(solrHome, String.join("/", DEFAULT_CORE_NAME, DEFAULT_DATA_DIR_NAME));
            if (dataDir.exists() && !FileUtils.deleteQuietly(dataDir)) {
                String msg = format("Unable to delete SOLr data dir %s.", dataDir);
                logger.fatal(msg);
                throw new IllegalStateException(msg);
            }
        }


        CoreContainer coreContainer = Value.with(new CoreContainer(solrHome), CoreContainer::load);

        return new EmbeddedSolrServer(coreContainer, DEFAULT_CORE_NAME);
    }

}