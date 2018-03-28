package imcode.server.document.index.service;

import imcode.server.document.index.service.impl.IndexRebuildProgress;

import java.util.Optional;
import java.util.concurrent.FutureTask;

public interface IndexRebuildTask {
    FutureTask<?> future();

    Optional<IndexRebuildProgress> progress();
}
