package imcode.server.document.index;

import imcode.server.Config;
import imcode.server.Imcms;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;

public class EmbeddedSolrFactory extends SolrFactory {

    private static CoreContainer coreContainer;

    static {
        try {
            CoreContainer.Initializer initializer = new CoreContainer.Initializer();
            coreContainer = initializer.initialize();
        }
        catch (Exception e) {
            throw new IndexException(e);
        }
    }

    @Override
    public SolrServer createServer() {
        try {
            Config config = Imcms.getServices().getConfig();
            return new EmbeddedSolrServer(coreContainer, config.getSolrCoreName());
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }
}
