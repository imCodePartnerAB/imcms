package imcode.server.document.index.service;

import imcode.server.document.index.service.impl.IndexRebuildProgress;

import java.util.Optional;
import java.util.concurrent.Future;

public interface IndexRebuildTask {
    Future<?> future();

    Optional<IndexRebuildProgress> progress();
}
