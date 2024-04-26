package imcode.server.document.index.service;

import com.imcode.imcms.util.Value;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.NodeConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;

public class SolrClientFactory {

    public static final String DEFAULT_CORE_NAME = "core";
    public static final String DEFAULT_IMAGE_FILE_METADATA_CORE_NAME = "image_file_metadata";
    public static final String DEFAULT_DATA_DIR_NAME = "data";
	public static final String DEFAULT_CONFIGURATION_NODE = "configuration_node";

    private static final Logger logger = LogManager.getLogger(SolrClientFactory.class);

    public static SolrClient createHttpSolrClient(String solrUrl, boolean recreateDataDir) {
        logger.info(
                format("Connecting to remote Solr server. Solr URL: %s, recreateDataDir: %s.", solrUrl, recreateDataDir)
        );

        try {
            final int lastIndexOfSlash = solrUrl.lastIndexOf('/');

            final SolrClient solrClient = getSolrClient(solrUrl, lastIndexOfSlash);
            final String coreName = getCoreName(solrUrl, lastIndexOfSlash);

            createCoreIfNotExist(solrClient, coreName);
            recreateDataDir(solrClient, coreName, recreateDataDir);

        } catch (Exception e) {
            throw new IllegalArgumentException("Solr URL passed to method: " + solrUrl, e);
        }

        return createHttpSolrClient(solrUrl);
    }

    private static SolrClient getSolrClient(String solrUrl, int lastIndexOfSlash) {
        return new HttpSolrClient.Builder(getBaseSolrUrl(solrUrl, lastIndexOfSlash)).build();
    }

    private static String getCoreName(String solrUrl, int lastIndexOfSlash) {
        return solrUrl.substring(lastIndexOfSlash + 1);
    }

    private static String getBaseSolrUrl(String solrUrl, int lastIndexOfSlash) {
        return solrUrl.substring(0, lastIndexOfSlash);
    }

    private static void createCoreIfNotExist(SolrClient solrClient, String coreName)
            throws IOException, SolrServerException {

        final CoreAdminRequest coreAdminRequest = new CoreAdminRequest();
        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        coreAdminRequest.setIndexInfoNeeded(false);

        if (coreAdminRequest.process(solrClient).getCoreStatus(coreName) == null) {
            CoreAdminRequest.createCore(coreName, coreName, solrClient);

            logger.info(format("Core with name %s is created.", coreName));
        }
    }

    private static void recreateDataDir(SolrClient solrClient, String coreName, boolean recreateDataDir)
            throws IOException, SolrServerException {

        if (recreateDataDir) {
            CoreAdminRequest.unloadCore(coreName, true, true, solrClient);
            createCoreIfNotExist(solrClient, coreName);

            logger.info("Data directory is recreated.");
        }
    }

    private static SolrClient createHttpSolrClient(String solrUrl) {
        return Value.with(new HttpSolrClient.Builder(solrUrl).build(), solr ->
                solr.setRequestWriter(new BinaryRequestWriter())
        );
    }

    public static SolrClient createEmbeddedSolrClient(String solrHome, boolean recreateDataDir) {
        logger.info(format("Creating embedded SOLr server. Solr home: %s, recreateDataDir: %s.",
                solrHome, recreateDataDir));
        final int lastIndexOfSlash = solrHome.lastIndexOf('/');
        final String coreName = getCoreName(solrHome, lastIndexOfSlash);

        if (recreateDataDir) {
            File dataDir = new File(solrHome, String.join("/", coreName, DEFAULT_DATA_DIR_NAME));
            if (dataDir.exists() && !FileUtils.deleteQuietly(dataDir)) {
                String msg = format("Unable to delete SOLr data dir %s.", dataDir);
                logger.fatal(msg);
                throw new IllegalStateException(msg);
            }
        }

	    NodeConfig nodeConfig = new NodeConfig.NodeConfigBuilder(DEFAULT_CONFIGURATION_NODE, Path.of(solrHome)).build();
	    CoreContainer coreContainer = Value.with(new CoreContainer(nodeConfig), CoreContainer::load);
        return new EmbeddedSolrServer(coreContainer, coreName);
    }

}
