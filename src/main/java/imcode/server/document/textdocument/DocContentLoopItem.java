package imcode.server.document.textdocument;


public interface DocContentLoopItem extends DocVersionItem {
    ContentRef getContentRef();

    void setContentRef(ContentRef contentRef);
}
