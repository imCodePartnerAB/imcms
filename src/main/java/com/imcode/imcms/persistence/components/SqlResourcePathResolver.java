package com.imcode.imcms.persistence.components;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Collections.singletonMap;

/**
 * Provides possibility to dynamically resolve SQL resources path no matter
 * is it in jar file or not.
 */
public class SqlResourcePathResolver implements AutoCloseable {

    private final URI sqlResourceDirectoryURI;

    private FileSystem jarFileSystem;

    public SqlResourcePathResolver(URI sqlResourceDirectoryURI) {
        this.sqlResourceDirectoryURI = sqlResourceDirectoryURI;
    }

    public Path resolveSqlResourceSubPath(String subPath) throws IOException {
        return resolveSqlResourcePath().resolve(subPath);
    }

    public Path resolveSqlResourcePath() throws IOException {
        final String protocol = sqlResourceDirectoryURI.getScheme();
        switch (protocol) {
            case "file":
                return Paths.get(sqlResourceDirectoryURI);
            case "jar":
                try {
                    jarFileSystem = FileSystems.newFileSystem(sqlResourceDirectoryURI, singletonMap("create", true));
                } catch (FileSystemAlreadyExistsException e) {
                    jarFileSystem = FileSystems.getFileSystem(sqlResourceDirectoryURI);
                }
                return jarFileSystem.getPath("sql");
            default:
                throw new ProtocolException("Unsupported protocol - " + protocol);
        }
    }

    @Override
    public void close() throws IOException {
        if (jarFileSystem != null) {
            jarFileSystem.close();
        }
    }

}
