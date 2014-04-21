package imcode.server.document.index.service;

import java.util.Optional;
import java.util.concurrent.Future;
import imcode.server.document.index.service.impl.IndexRebuildProgress;

public interface IndexRebuildTask {
    Future<?> future();
    Optional<IndexRebuildProgress> progress();
}
