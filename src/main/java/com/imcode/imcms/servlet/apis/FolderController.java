package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.util.directory.Directory;
import imcode.server.Imcms;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 18.03.2015.
 */
@RestController
@RequestMapping("/content/folders")
public class FolderController {
    private static final String FILE_FILTER_PATTERN = "^(%s)+(\\.(%s))$";

    private static Pattern filterOf(String filename, String extension) {
        return Pattern
                .compile(
                        String.format(FILE_FILTER_PATTERN,
                                filename.replace("*", ".*"),
                                extension.replace("*", ".*")
                        )
                );
    }

    public static String folderFromRequest(HttpServletRequest request) {
        String path = request.getPathInfo();
        path = path.replaceFirst("^/content/(.*?)/", "/");
        int lastDelimiter = path.lastIndexOf('/');
        path = path.substring(0, lastDelimiter);

        return path;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/**/"})
    public Directory read(HttpServletRequest request) {
        String path = folderFromRequest(request);

        return new Directory(Imcms.getServices().getConfig().getImagePath()).find(path);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/**/{name}"})
    public boolean create(HttpServletRequest request, @PathVariable("name") String name) throws IOException {
        String path = folderFromRequest(request);

        return new File(new Directory(Imcms.getServices().getConfig().getImagePath())
                .find(path)
                .getSource(), name).mkdir();
    }

    @RequestMapping(method = RequestMethod.PATCH, value = {"/**/{name}"})
    public boolean update(HttpServletRequest request,
                          @PathVariable("name") String name,
                          @RequestBody MultiValueMap<String, String> body) throws IOException {
        String path = folderFromRequest(request);
        Directory base = new Directory(Imcms.getServices().getConfig().getImagePath());
        File folderTo = base.find(URLDecoder.decode(body.get("to").get(0), "UTF-8")).getSource();

        return new File(base
                .find(path)
                .getSource(), name).renameTo(new File(folderTo, name));
    }

    @RequestMapping(method = RequestMethod.DELETE, value = {"/**/{name}"})
    public boolean delete(HttpServletRequest request, @PathVariable("name") String name) {
        String path = folderFromRequest(request);
        Pattern namePattern = Pattern.compile(String.format("^(%s)$", name.replace("*", ".*")));
        Stream.of(new Directory(Imcms.getServices().getConfig().getImagePath())
                .find(path)
                .getSource()
                .listFiles(file -> file.isDirectory() && namePattern.matcher(file.getName()).matches()))
                .forEach(File::delete);

        return true;
    }

}