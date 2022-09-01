package imcode.server.document.index.service.impl;

import com.google.common.base.MoreObjects;

public final class IndexRebuildProgress {

    private final long startTime;
    private final long currentTime;
    private final int totalDocsCount;
    private final int indexedDocsCount;

    public IndexRebuildProgress(long startTime, long currentTime, int totalDocsCount, int indexedDocsCount) {
        this.startTime = startTime;
        this.currentTime = currentTime;
        this.totalDocsCount = totalDocsCount;
        this.indexedDocsCount = indexedDocsCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public int getTotalDocsCount() {
        return totalDocsCount;
    }

    public int getIndexedDocsCount() {
        return indexedDocsCount;
    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("startTime", startTime)
			    .add("currentTime", currentTime)
			    .add("totalDocsCount", totalDocsCount)
			    .add("indexedDocsCount", indexedDocsCount)
			    .toString();
    }
}
