package com.imcode.imcms.util.rss;

import com.imcode.imcms.util.rss.dc.DublinCoreEntity;
import com.imcode.imcms.util.rss.dc.DublinCoreItem;
import com.imcode.imcms.util.rss.dc.DublinCoreTerms;
import imcode.util.Utility;
import junit.framework.TestCase;
import org.apache.commons.beanutils.DynaBean;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamResult;
import java.util.*;

public class Rss20DocumentFactoryTest extends TestCase {

    public void testCreateRssDocument() {
        final RssDocumentFactory rssDocumentFactory = new Rss20DocumentFactory();
        SimpleChannel channel = new SimpleChannel();
        channel.setTitle("title");
        channel.setLink("link");
        channel.setDescription("description");
        SimpleDublinCoreTerms dublinCoreTerms = new SimpleDublinCoreTerms();
        dublinCoreTerms.setTitle("title");
        dublinCoreTerms.setIssued(new Date());
        DublinCoreItem item = new DublinCoreItem(dublinCoreTerms);
        channel.setItems(Arrays.asList(new Item[]{item}));
        Document rssDocument = rssDocumentFactory.createRssDocument(channel);
        Utility.writeXmlDocument(rssDocument, new StreamResult(System.out));
    }

    private static class SimpleDublinCoreTerms implements DublinCoreTerms {

        private Date created;
        private DublinCoreEntity creator;
        private String description;
        private String identifier;
        private Date issued;
        private Date modified;
        private String title;

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public DublinCoreEntity getCreator() {
            return creator;
        }

        public void setCreator(DublinCoreEntity creator) {
            this.creator = creator;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIdentifer() {
            return identifier;
        }

        public Date getIssued() {
            return issued;
        }

        public void setIssued(Date issued) {
            this.issued = issued;
        }

        public Date getModified() {
            return modified;
        }

        public void setModified(Date modified) {
            this.modified = modified;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
    }

    private static class SimpleChannel implements Channel {

        private String title;
        private String link;
        private String description;
        private Collection<Item> items;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Iterable<Item> getItems() {
            return items;
        }

        public void setItems(Collection<Item> items) {
            this.items = items;
        }

        public Map<NameSpace, Map<String, String>> getNameSpaceStrings() {
            return Collections.EMPTY_MAP;
        }

        public Map<NameSpace, DynaBean> getNameSpaceBeans() {
            return Collections.EMPTY_MAP;
        }
    }
}
