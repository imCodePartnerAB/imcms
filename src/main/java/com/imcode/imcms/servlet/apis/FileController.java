package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.util.directory.Directory;
import com.imcode.util.ImageSize;
import imcode.server.Imcms;
import imcode.server.document.textdocument.AbstractFileSource;
import imcode.server.document.textdocument.DocumentImageSource;
import imcode.server.document.textdocument.FileSource;
import imcode.util.image.Format;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Current class provide access to file system inside ImCMS
 */
@RestController
@RequestMapping("/content/files")
public class FileController {
    private static final String FILE_FILTER_PATTERN = "^%s\\.(%s)$";

    /**
     * Create RegExp {@link Pattern} file filter based on special file name and extension
     *
     * @param filename File name
     * @param extension File extension
     *
     * @return Return created {@link Pattern}
     */
    private static Pattern filterOf(String filename, String extension) {
        return Pattern
                .compile(
                        String.format(FILE_FILTER_PATTERN,
                                filename.replace("*", ".*"),
                                extension.replace("*", ".*")
                        )
                );
    }

    /**
     * List files from special folder, that presented in request.
     * Path to folder presented as a part of url, for example:
     *
     *  http:localhost:8080/content/files/images/some/folder
     * {         |         }{      |     }{        |        }
     * {  server  address  }{  API part  }{   folder path   }
     *
     * @param request Request object {@link HttpServletRequest}
     * @param extension File extension. Using for find files, with specify extension.
     *                  If extension equals to `*` then all files extensions are valid
     * @param filename File name. Using for find files, with specify name.
     *                 If file name equals to `*` then all files names are valid
     *
     * @return Array of {@link File}
     **/
    File[] readFiles(HttpServletRequest request, String extension, String filename) {
        final Pattern fileFilterPattern = filterOf(filename, extension);
        String path = FolderController.folderFromRequest(request);
        return new Directory(Imcms.getServices().getConfig().getImagePath())
                .find(path)
                .getSource()
                .listFiles((dir, item) -> fileFilterPattern.matcher(item).matches());
    }


    @RequestMapping(method = RequestMethod.GET, value = {"/**/{filename}.{extension}"})
    public AbstractFileSource[] read(HttpServletRequest request,
                                     @PathVariable("extension") String extension,
                                     @PathVariable("filename") String filename) {
        File[] files = readFiles(request, extension, filename);
        String prefix = Imcms.getServices().getConfig().getImagePath().getAbsolutePath();
        List<AbstractFileSource> collect = Stream.of(files).map(file -> {
            if (Format.isImage(FilenameUtils.getExtension(file.getName()).toLowerCase())) {
                return new DocumentImageSource(
                        file.getAbsolutePath().replace(prefix + File.separator, ""),
                        new ImageSize(0, 0)
                );
            }
            return new FileSource(file);
        }).collect(Collectors.toList());
        return collect.toArray(new AbstractFileSource[collect.size()]);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/**/{filename}.{extension}"})
    public boolean create(HttpServletRequest request, @PathVariable("extension") String extension,
                          @PathVariable("filename") String filename)
            throws IOException {
        MultipartFile multipartFile = null;
        String path = FolderController.folderFromRequest(request);
        Directory dir = new Directory(Imcms.getServices().getConfig().getImagePath()).find(path);
        File createdFile;

        if (!(createdFile = new File(
                dir.getSource(),
                String.format("%s.%s", filename, extension))).createNewFile()
                ) {
            throw new FileAlreadyExistsException(String.format("File '%s.%s' has already exists", filename, extension));
        }

       /* @RequestParam(value = "file", required = false)*/
        if (request instanceof MultipartHttpServletRequest) {
            multipartFile = ((MultipartHttpServletRequest) request).getFile("file");
        }

        FileUtils.copyInputStreamToFile(multipartFile != null ? multipartFile.getInputStream() : request.getInputStream(), createdFile);
        return true;
    }

    @RequestMapping(method = RequestMethod.PATCH, value = {"/**/{filename}.{extension}"})
    public boolean update(HttpServletRequest request, @PathVariable("extension") String extension,
                          @PathVariable("filename") String filename, @RequestBody MultiValueMap<String, String> body) throws IOException {
        final Pattern fileFilterPattern = filterOf(filename, extension);
        String path = FolderController.folderFromRequest(request);
        Directory base = new Directory(Imcms.getServices().getConfig().getImagePath());
        File folderTo = base.find(URLDecoder.decode(body.get("to").get(0), "UTF-8")).getSource();
        return Stream.of(base
                .find(path)
                .getSource()
                .listFiles((dir, item) -> fileFilterPattern.matcher(item).matches()))
                .allMatch(file -> file.renameTo(new File(folderTo, file.getName())));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = {"/**/{filename}.{extension}"})
    public boolean delete(HttpServletRequest request, @PathVariable("extension") String extension,
                          @PathVariable("filename") String filename) {
        final Pattern fileFilterPattern = filterOf(filename, extension);
        String path = FolderController.folderFromRequest(request);
        return Stream.of(new Directory(Imcms.getServices().getConfig().getImagePath())
                .find(path)
                .getSource()
                .listFiles((dir, item) -> fileFilterPattern.matcher(item).matches())).allMatch(File::delete);

    }
}
