package com.imcode.imcms.tools;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;
import org.apache.log4j.Logger;

public class Issue9755Fixer {

    private Logger logger = Logger.getLogger(Issue9755Fixer.class);

    private static final String NEW_LINE = System.getProperty("line.separator");

    private Pattern localizationPattern = Pattern.compile("<\\?(.+?)\\?>",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private Pattern pagePattern = Pattern.compile("<%@.*page.+?%>",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private String source;
    private String destination;

    private static final String[] structureToBeChecked = {
        "web/imcms/lang/",
        "web/WEB-INF/templates/lang/admin/",
    };

    public Issue9755Fixer (String source, String destination){
        this.source = source;
        this.destination = destination;
    }

    public void initialize() throws IllegalArgumentException {
        for(String path : structureToBeChecked) {
            File f = new File(source, path);
            if (!f.exists()) {
                throw new IllegalArgumentException(
                    String.format ("The required path '%s' not found. Please, specify the correct source directory.",
                            f.getAbsolutePath()));
            }
        }
    }

    private void listFilesRecursively(File directory, FileFilter filter, List<File> result) {
        File[] jspFiles = directory.listFiles(filter);
        result.addAll(Arrays.asList(jspFiles));

        File[] dirs = directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();  
            }
        });
        for (File d: dirs)
            listFilesRecursively(d, filter, result);
    }

    private String getFileContent(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuffer buffer = new StringBuffer();
        String line;
        do {
            line = reader.readLine();
            buffer.append(line).append(NEW_LINE);
        }
        while (line != null);

        reader.close();

        return buffer.toString();
    }

    private void saveToDestination(String path, String postDirs, String filename, String content) throws IOException {
        File target = new File(destination, path);
        target = new File(target, postDirs);
        target.mkdirs();
        File targetFile = new File(target, filename);
        logger.info("Saving destination '" + targetFile.getAbsolutePath() + "' ...");
        FileWriter writer = new FileWriter(targetFile);
        writer.append(content);
        writer.close();
    }

    private void processListOfFiles(List<File> list, String root, boolean isJsp) throws IOException {
        for (File file: list) {
            String content = getFileContent(file);

            logger.info("Processing file '" + file.getAbsolutePath() + "' ...");
            Matcher m = localizationPattern.matcher(content);

            StringBuffer contentBuffer = new StringBuffer(content);
            while (m.find()) {
                String key = m.group(1).trim();
                String message = String.format("<fmt:message key=\"%s\"/>", key);
                logger.debug("Replace '" + contentBuffer.substring(m.start(), m.end()) + "' with '" + message + "'");
                contentBuffer.replace(m.start(), m.end(), message);
                m.reset(contentBuffer.toString());
            }

            m = pagePattern.matcher(contentBuffer.toString());
            int pageEndIndex = 0;
            while (m.find()) {
                pageEndIndex = m.end();
            }

            logger.debug("Insert <%@taglib prefix=\"fmt\" uri=\"http://java.sun.com/jsp/jstl/fmt\" %>");

            contentBuffer.insert(pageEndIndex,
                    NEW_LINE + "<%@taglib prefix=\"fmt\" uri=\"http://java.sun.com/jsp/jstl/fmt\" %>" + NEW_LINE);

            if (!isJsp) {
                contentBuffer.insert(0,
                        "<%@ page contentType=\"text/html; charset=UTF-8\"%>" + NEW_LINE +
                        "<%@taglib prefix=\"fmt\" uri=\"http://java.sun.com/jsp/jstl/fmt\" %>" + NEW_LINE);
            }

            String absolutePath = file.getAbsolutePath().replace('\\', '/');
            int index = absolutePath.indexOf(root);
            String filename = absolutePath.substring(index + root.length());
            String postDirs = filename.replace(file.getName(), "");
            String fn = file.getName();
            if (!isJsp) {
                int dotIndex = fn.lastIndexOf(".");
                fn = fn.substring(0, dotIndex) + ".jsp";
            }
            saveToDestination(root, postDirs, fn, contentBuffer.toString());
        }
    }

    public void fixJspPages() throws IOException {
         List<File> result = new ArrayList<File>();
         listFilesRecursively(new File (source, structureToBeChecked[0]), new FileFilter() {
            public boolean accept(File file) {
                return !file.isDirectory() && file.getName().toLowerCase().endsWith(".jsp");
            }
        }, result);

        
        processListOfFiles(result, structureToBeChecked[0], true);
    }

    public void fixHtmlTemplates() throws IOException {
        List<File> result = new ArrayList<File>();
        listFilesRecursively(new File (source, structureToBeChecked[1]), new FileFilter() {
            public boolean accept(File file) {
                String filename = file.getName().toLowerCase();
                return !file.isDirectory() && (filename.endsWith(".html") ||
                        filename.endsWith(".htm") || filename.endsWith(".txt"));
            }
        }, result);

        processListOfFiles(result, structureToBeChecked[1], false);
    }

    public void fixReferences() {

    }
}
