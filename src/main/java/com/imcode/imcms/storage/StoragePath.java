package com.imcode.imcms.storage;

import com.imcode.imcms.api.SourceFile.FileType;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;

import java.util.StringJoiner;

import static com.imcode.imcms.api.SourceFile.FileType.DIRECTORY;
import static com.imcode.imcms.api.SourceFile.FileType.FILE;

/**
 * Provides the path of the file in a storage.
 *
 * Directory - directory1/directory2/
 * File      - directory1/file.txt
 */

@EqualsAndHashCode
public class StoragePath {

    public static final String PATH_SEPARATOR = Path.SEPARATOR;     // Path.SEPARATOR slashes use in everywhere, for different OS

    private final String path;
    private final FileType type;

    private StoragePath(FileType type, String path){
        this.type = type;
        this.path = path;
    }

    public static StoragePath get(FileType type, String... paths){
        final StringJoiner resultPathJoiner;

        if(type == DIRECTORY){
            resultPathJoiner = new StringJoiner(PATH_SEPARATOR, "", PATH_SEPARATOR);
        }else {
            resultPathJoiner = new StringJoiner(PATH_SEPARATOR);
        }

        for(String path: paths){
            path = path.replaceAll("/+", "/");
            path = StringUtils.removeStart(path, PATH_SEPARATOR);
            path = StringUtils.removeEnd(path, PATH_SEPARATOR);

            if(!path.isEmpty()) resultPathJoiner.add(path);
        }

        return new StoragePath(type, resultPathJoiner.toString());
    }

    /**
     * Appends the given paths to the current path.
     */
    public StoragePath resolve(FileType type, String... paths) {
        final String[] fullPaths = new String[paths.length + 1];

        fullPaths[0] = this.path;
        for (int i = 0; i < paths.length; i++){
            fullPaths[i+1] = paths[i];
        }

        return get(type, fullPaths);
    }

    /**
     * Appends the given paths to the current path.
     */
    public StoragePath resolve(FileType type, StoragePath... paths) {
        final String[] strPaths = new String[paths.length];

        for (int i = 0; i < paths.length; i++){
            strPaths[i] = paths[i].toString();
        }

        return resolve(type, strPaths);
    }

    /**
     * Removes the current path from the given path.
     */
    public StoragePath relativize(StoragePath path){
        String resultPath = path.toString().replaceFirst(this.path, "");
        return new StoragePath(path.getType(), resultPath);
    }

    public String getName() {
        String tempPath = (type == FILE) ? path : StringUtils.removeEnd(path, PATH_SEPARATOR);
        return tempPath.contains(PATH_SEPARATOR) ?
                StringUtils.substringAfterLast(tempPath, PATH_SEPARATOR) : tempPath;
    }

    public StoragePath getParentPath(){
        String tempPath = (type == FILE) ? path : StringUtils.removeEnd(path, PATH_SEPARATOR);

        return tempPath.contains(PATH_SEPARATOR) ?
                get(DIRECTORY, StringUtils.substringBeforeLast(tempPath, PATH_SEPARATOR)) : null;
    }

    public FileType getType(){
        return type;
    }

    @Override
    public String toString() {
        return path;
    }
}
