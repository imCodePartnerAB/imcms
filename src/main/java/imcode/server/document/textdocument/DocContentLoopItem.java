package imcode.server.document.textdocument;


public interface DocContentLoopItem extends DocVersionItem {
    ContentLoopRef getContentLoopRef();

    void setContentLoopRef(ContentLoopRef contentRef);
}
