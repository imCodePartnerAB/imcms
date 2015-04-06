package com.imcode.imcms.util.directory;

import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 18.03.2015.
 */
public class Directory {
    private final File source;
    private String fullPath = "";
    @JsonIgnore
    private Directory parent;
    private String name;
    private List<Directory> subdirectories;

    public Directory(File file) {
        this(file, null);
    }

    public Directory(@NotNull File file, Directory parent) {
        if (!file.isDirectory()) throw new IllegalArgumentException("File should be directory");
        this.parent = parent;
        if (parent != null)
            fullPath = parent.fullPath + parent.name + "/";
        source = file;
        name = file.getName();
        subdirectories = findSubdirectoriesInFile(source, this);
    }

    private static List<Directory> findSubdirectoriesInFile(File file, Directory parent) {
        return Stream.of(file.listFiles(File::isDirectory)).map((dir) -> new Directory(dir, parent)).collect(Collectors.toList());
    }

    public String getFullPath() {
        return fullPath;
    }

    public List<Directory> getSubdirectories() {
        return subdirectories;
    }

    public File getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public Directory find(String path) {
        return find(Stream.of(path.split("/"))
                .filter((item) -> !item.isEmpty()));
    }

    public Directory find(String... path) {
        return find(Stream.of(path));
    }

    public Directory find(List<String> path) {
        return find(path.stream());
    }

    public Directory find(Stream<String> path) {
        List<String> source = path.collect(Collectors.toList());
        Optional<String> childDir = source.stream().findFirst();
        if (!childDir.isPresent()) return this;
        String childDirName = childDir.get();
        Optional<Directory> foundChild = subdirectories.stream().filter((dir -> dir.name.equals(childDirName))).findFirst();
        if (foundChild.isPresent()) return foundChild.get().find(source.stream().skip(1));
        throw new IllegalArgumentException(String.format("No such directory with name '%s'", childDirName));
    }
}
