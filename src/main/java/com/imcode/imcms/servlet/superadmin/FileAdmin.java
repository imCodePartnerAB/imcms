package com.imcode.imcms.servlet.superadmin;

import com.imcode.util.HumanReadable;
import com.imcode.util.MultipartHttpServletRequest;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class FileAdmin extends HttpServlet {

    private final static Logger LOG = Logger.getLogger("FileAdmin");
    private static final int BUFFER_SIZE = 65536;
    private static final String ADMIN_TEMPLATE_FILE_ADMIN_COPY_OVERWRIGHT_WARNING = "FileAdminCopyOverwriteWarning.jsp";
    private static final String ADMIN_TEMPLATE_FILE_ADMIN_MOVE_OVERWRITE_WARNING = "FileAdminMoveOverwriteWarning.jsp";

    static File findUniqueFilename(File file) {
        File uniqueFile = file;
        int counter = 1;
        String previousSuffix = "";
        while (uniqueFile.exists()) {
            String filenameWithoutSuffix = StringUtils.substringBeforeLast(uniqueFile.getName(), previousSuffix);
            String suffix = "." + counter;
            counter++;
            uniqueFile = new File(uniqueFile.getParentFile(), filenameWithoutSuffix + suffix);
            previousSuffix = suffix;
        }
        return uniqueFile;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        Utility.setNoCache(res);

        File dir1 = null;
        File dir2 = null;

        File[] roots = getRoots();
        if (roots.length > 0) {
            dir1 = roots[0];
            if (roots.length > 1) {
                dir2 = roots[1];
            }
        }

        outputFileAdmin(req, res, dir1, dir2);
    }

    /**
     * Check to see if the path is a child to one of the rootpaths
     */
    private boolean isUnderRoot(File path, File[] roots) throws IOException {
        for (File root : roots) {
            if (FileUtility.directoryIsAncestorOfOrEqualTo(root, path)) {
                return true;
            }
        }
        return false;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (!user.isSuperAdmin()) {
            Utility.redirectToStartDocument(req, res);
            return;
        }

        Utility.setNoCache(res);

        MultipartHttpServletRequest mp = new MultipartHttpServletRequest(req);

        if (mp.getParameter("cancel") != null) {
            res.sendRedirect("AdminManager");
            return;
        }

        File[] roots = getRoots();

        File dir1 = getContextRelativeDirectoryFromRequest(mp, "dir1");
        File dir2 = getContextRelativeDirectoryFromRequest(mp, "dir2");

        if (!isUnderRoot(dir1, roots) || !isUnderRoot(dir2, roots)) {
            doGet(req, res);
            return;
        }

        String[] files1 = mp.getParameterValues("files1");
        String[] files2 = mp.getParameterValues("files2");
        String name = mp.getParameter("name");

        boolean outputHasBeenHandled = false;

        // holy shit!

        if (mp.getParameter("change1") != null) {    //UserDomainObject wants to change dir1
            dir1 = changeDir(files1, dir1, roots);
        } else if (mp.getParameter("change2") != null) {    //UserDomainObject wants to change dir2
            dir2 = changeDir(files2, dir2, roots);
        } else if (mp.getParameter("mkdir1") != null) {
            outputHasBeenHandled = makeDirectory(name, dir1, dir1, dir2, res, req);
        } else if (mp.getParameter("mkdir2") != null) {
            outputHasBeenHandled = makeDirectory(name, dir2, dir1, dir2, res, req);
        } else if (mp.getParameter("delete1") != null) {
            outputHasBeenHandled = delete(dir1, files1, dir1, dir2, res, req);
        } else if (mp.getParameter("delete2") != null) {
            outputHasBeenHandled = delete(dir2, files2, dir1, dir2, res, req);
        } else if (mp.getParameter("deleteok") != null) {
            deleteOk(mp, roots);
        } else if (mp.getParameter("upload1") != null) {
            outputHasBeenHandled = upload(mp, dir1, dir1, dir2, res, req);
        } else if (mp.getParameter("upload2") != null) {
            outputHasBeenHandled = upload(mp, dir2, dir1, dir2, res, req);
        } else if (mp.getParameter("download1") != null) {
            outputHasBeenHandled = download(files1, dir1, res);
        } else if (mp.getParameter("download2") != null) {
            outputHasBeenHandled = download(files2, dir2, res);
        } else if (mp.getParameter("rename1") != null) {
            outputHasBeenHandled = rename(files1, name, dir1, dir1, dir2, res, req);
        } else if (mp.getParameter("rename2") != null) {
            outputHasBeenHandled = rename(files2, name, dir2, dir1, dir2, res, req);
        } else if (mp.getParameter("copy1") != null) {
            outputHasBeenHandled = copy(files1, dir1, dir2, dir1, dir2, res, req);
        } else if (mp.getParameter("copy2") != null) {
            outputHasBeenHandled = copy(files2, dir2, dir1, dir1, dir2, res, req);
        } else if (mp.getParameter("copyok") != null) {
            copyOk(mp, roots);
        } else if (mp.getParameter("move1") != null) {
            outputHasBeenHandled = move(files1, dir1, dir2, dir1, dir2, res, req);
        } else if (mp.getParameter("move2") != null) {
            outputHasBeenHandled = move(files2, dir2, dir1, dir1, dir2, res, req);
        } else if (mp.getParameter("moveok") != null) {
            moveOk(mp, roots);
        }

        if (!outputHasBeenHandled) {
            outputFileAdmin(req, res, dir1, dir2);
        }
    }

    private File getContextRelativeDirectoryFromRequest(HttpServletRequest request, String parameter) throws IOException {
        File webappPath = Imcms.getPath();
        String dirParameter = request.getParameter(parameter);
        return new File(webappPath, dirParameter).getCanonicalFile();
    }

    private File[] getRoots() {
        String rootpaths = Imcms.getServices().getConfig().getFileAdminRootPaths();
        List rootList = new ArrayList();
        if (rootpaths != null) {
            StringTokenizer st = new StringTokenizer(rootpaths, ":;");
            int tokenCount = st.countTokens();
            for (int i = 0; i < tokenCount; i++) {
                String oneRoot = st.nextToken().trim();
                File oneRootFile = FileUtility.getFileFromWebappRelativePath(oneRoot);
                if (oneRootFile.isDirectory()) {
                    rootList.add(oneRootFile);
                }
            }
        }
        return (File[]) rootList.toArray(new File[rootList.size()]);
    }

    private boolean move(String[] files, File sourceDir, File destDir, File dir1, File dir2, HttpServletResponse res,
                         HttpServletRequest req) throws IOException, ServletException {
        boolean handledOutput = false;
        if (files != null && !sourceDir.equals(destDir)) {
            File[] sourceFileTree = makeFileTreeList(makeAbsoluteFileList(sourceDir, files), false);
            File[] relativeSourceFileTree = makeRelativeFileList(sourceDir, sourceFileTree);
            StringBuffer optionList = new StringBuffer();
            StringBuffer fileList = buildWarningOptions(relativeSourceFileTree, destDir, optionList);
            if (optionList.length() > 0) {
                outputMoveOverwriteWarning(optionList.toString(), sourceDir, destDir, fileList.toString(), dir1, dir2, res, req);
                handledOutput = true;
            } else {
                File[] destFiles = makeAbsoluteFileList(destDir, relativeSourceFileTree);
                for (int i = 0; i < sourceFileTree.length; i++) {
                    File destFile = destFiles[i];
                    destFile.getParentFile().mkdirs();
                    File sourceFile = sourceFileTree[i];
                    if (sourceFile.isFile()) {
                        FileUtils.copyFile(sourceFile, destFile);
                    }
                    if (sourceFile.length() == destFile.length()) {
                        FileUtils.forceDelete(sourceFile);
                    }
                }
            }
        }
        return handledOutput;
    }

    private String createWarningFileOptionString(File destFile) throws IOException {
        File webAppPath = Imcms.getPath();
        return FileUtility.relativizeFile(webAppPath, destFile).getPath() + (destFile.isDirectory()
                ? File.separator
                : " [" + destFile.length() + "]");
    }

    private boolean copy(String[] files, File sourceDir, File destDir, File dir1, File dir2, HttpServletResponse res,
                         HttpServletRequest req) throws IOException, ServletException {
        boolean handledOutput = false;
        if (files != null && !sourceDir.equals(destDir)) {
            File[] sourceFileTree = makeFileTreeList(makeAbsoluteFileList(sourceDir, files), true);
            File[] relativeSourceFileTree = makeRelativeFileList(sourceDir, sourceFileTree);
            StringBuffer optionList = new StringBuffer();
            StringBuffer fileList = buildWarningOptions(relativeSourceFileTree, destDir, optionList);
            if (optionList.length() > 0) {
                outputCopyOverwriteWarning(optionList.toString(), sourceDir, destDir, fileList.toString(), dir1, dir2, res, req);
                handledOutput = true;
            } else {
                File[] destFileTree = makeAbsoluteFileList(destDir, relativeSourceFileTree);
                for (int i = 0; i < sourceFileTree.length; i++) {
                    File sourceFile = sourceFileTree[i];
                    File destFile = destFileTree[i];
                    if (sourceFile.isDirectory()) {
                        destFile.mkdir();
                        continue;
                    }
                    FileUtils.copyFile(sourceFile, destFile);
                }
            }
        }
        return handledOutput;
    }

    private StringBuffer buildWarningOptions(File[] relativeSourceFileTree, File destDir, StringBuffer optionList) throws IOException {
        StringBuffer fileList = new StringBuffer();
        for (int i = 0; i < relativeSourceFileTree.length; i++) {
            File destFile = new File(destDir, relativeSourceFileTree[i].getPath());
            fileList.append(relativeSourceFileTree[i]).append(File.pathSeparator);
            if (destFile.exists()) {
                String optionString = createWarningFileOptionString(destFile);
                optionList.append("<option>").append(optionString).append("</option>");
            }
        }
        return fileList;
    }

    private boolean rename(String[] files, String name, File dir, File dir1, File dir2, HttpServletResponse res,
                           HttpServletRequest req) throws IOException, ServletException {
        boolean handledOutput = false;
        if (files != null && files.length == 1) {    //Has the user chosen just one file?
            if (name != null && name.length() > 0) {
                File oldFilename = new File(dir, files[0]);
                File newFilename = new File(dir, name);
                if (oldFilename.exists()) {
                    oldFilename.renameTo(newFilename);
                }
            } else {
                outputBlankFilenameError(dir1, dir2, res, req);
                handledOutput = true;
            }
        }
        return handledOutput;
    }

    private boolean download(String[] files, File dir, HttpServletResponse res) throws IOException {
        boolean handledOutput = false;
        if (files != null && files.length == 1) {    //Has the user chosen just one file?
            File file = new File(dir, files[0]);
            try {
                BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));
                res.setContentType("application/octet-stream");
                res.setContentLength(fin.available());
                res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + '\"');
                byte[] buffer = new byte[BUFFER_SIZE];
                ServletOutputStream out = res.getOutputStream();
                for (int bytes_read; (bytes_read = fin.read(buffer)) != -1; ) {
                    out.write(buffer, 0, bytes_read);
                }
                handledOutput = true;
            } catch (FileNotFoundException ex) {
                // todo: FIXME: Error dialog?
                LOG.debug("Download failed", ex);
            }
        }
        return handledOutput;
    }

    private boolean delete(File dir, String[] files, File dir1, File dir2, HttpServletResponse res,
                           HttpServletRequest request) throws IOException, ServletException {
        boolean handledOutput = false;
        File[] farray = makeFileTreeList(makeAbsoluteFileList(dir, files), false);
        File[] filelist = makeRelativeFileList(dir, farray);
        if (filelist != null && filelist.length > 0) {
            outputDeleteWarning(filelist, dir1, dir2, dir, res, request);
            handledOutput = true;
        }
        return handledOutput;
    }

    private boolean makeDirectory(String name, File dir, File dir1, File dir2, HttpServletResponse res,
                                  HttpServletRequest req) throws IOException, ServletException {
        boolean handledOutput = false;
        if (name != null && name.length() > 0) {
            File newname = new File(dir, name);
            if (!newname.exists()) {
                newname.mkdir();
            }
        } else {
            outputBlankFilenameError(dir1, dir2, res, req);
            handledOutput = true;
        }
        return handledOutput;
    }

    private boolean upload(MultipartHttpServletRequest mp, File destDir, File dir1, File dir2, HttpServletResponse res,
                           HttpServletRequest req) throws IOException, ServletException {
        boolean handledOutput = false;
        MultipartHttpServletRequest.DataSourceFileItem parameterFileItem = mp.getParameterFileItem("file");
        if (parameterFileItem == null || parameterFileItem.getSize() < 1) {
            outputBlankFileError(dir1, dir2, req, res);
            handledOutput = true;
            return handledOutput;
        }
        String filename = parameterFileItem.getName();
        File file = new File(destDir, filename);
        File uniqueFile = findUniqueFilename(file);
        if (file.equals(uniqueFile) || file.renameTo(uniqueFile)) {
            try {
                parameterFileItem.write(file);
            } catch (Exception e) {
                IOException ioException = new IOException("Failed to write file.", e);
                throw ioException;
            }
            if (!file.equals(uniqueFile)) {
                outputFileExistedAndTheOriginalWasRenamedNotice(dir1, dir2, uniqueFile.getName(), res, req);
                handledOutput = true;
            }
        } else {
            // todo: FIXME: Output failed-to-rename-original-file error dialog
            handledOutput = false;
        }
        return handledOutput;
    }

    private void outputFileAdmin(HttpServletRequest req, HttpServletResponse res, File dir1, File dir2)
            throws IOException, ServletException {
        Utility.setDefaultHtmlContentType(res);
        res.getWriter().print(parseFileAdmin(req, res, dir1, dir2));
    }

    private void outputMoveOverwriteWarning(String option_list, File sourceDir, File destDir,
                                            String file_list, File dir1, File dir2, HttpServletResponse res,
                                            HttpServletRequest req) throws IOException, ServletException {
        outputWarning(option_list, sourceDir, destDir, file_list, dir1, dir2, res, req, ADMIN_TEMPLATE_FILE_ADMIN_MOVE_OVERWRITE_WARNING);
    }

    private void outputCopyOverwriteWarning(String option_list, File sourceDir, File destDir,
                                            String file_list, File dir1, File dir2, HttpServletResponse res,
                                            HttpServletRequest req) throws IOException, ServletException {
        outputWarning(option_list, sourceDir, destDir, file_list, dir1, dir2, res, req, ADMIN_TEMPLATE_FILE_ADMIN_COPY_OVERWRIGHT_WARNING);
    }

    private void outputWarning(String option_list, File sourceDir, File destDir, String file_list, File dir1, File dir2,
                               HttpServletResponse res, HttpServletRequest request, String template) throws IOException, ServletException {

        request.setAttribute("filelist", option_list);
        request.setAttribute("source", getContextRelativeAbsolutePathToDirectory(sourceDir));
        request.setAttribute("dest", getContextRelativeAbsolutePathToDirectory(destDir));
        request.setAttribute("files", file_list);
        request.setAttribute("dir1", getContextRelativeAbsolutePathToDirectory(dir1));
        request.setAttribute("dir2", getContextRelativeAbsolutePathToDirectory(dir2));

        Utility.setDefaultHtmlContentType(res);
        res.getWriter().print(Utility.getAdminContents(template, request, res));
    }

    private void outputFileExistedAndTheOriginalWasRenamedNotice(File dir1, File dir2, String newFilename,
                                                                 HttpServletResponse res, HttpServletRequest request) throws IOException, ServletException {

        request.setAttribute("dir1", getContextRelativeAbsolutePathToDirectory(dir1));
        request.setAttribute("dir2", getContextRelativeAbsolutePathToDirectory(dir2));
        request.setAttribute("filename", newFilename);

        Utility.setDefaultHtmlContentType(res);
        res.getWriter().print(Utility.getAdminContents("FileAdminFileExisted.jsp", request, res));
    }

    private void outputBlankFileError(File dir1, File dir2, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setAttribute("dir1", getContextRelativeAbsolutePathToDirectory(dir1));
        request.setAttribute("dir2", getContextRelativeAbsolutePathToDirectory(dir2));
        Utility.setDefaultHtmlContentType(response);
        response.getWriter().print(Utility.getAdminContents("FileAdminFileBlank.jsp", request, response));
    }

    private void outputDeleteWarning(File[] filelist, File dir1, File dir2, File sourceDir, HttpServletResponse response,
                                     HttpServletRequest request) throws IOException, ServletException {
        StringBuilder files = new StringBuilder();
        StringBuilder options = new StringBuilder();

        for (File file : filelist) {
            File foo = new File(sourceDir, file.getPath());
            String bar = createWarningFileOptionString(foo);
            options.append("<option>").append(bar).append("</option>");
            files.append(file).append(File.pathSeparator);
        }

        request.setAttribute("filelist", options.toString());
        request.setAttribute("files", StringEscapeUtils.escapeHtml4(files.toString()));
        request.setAttribute("source", getContextRelativeAbsolutePathToDirectory(sourceDir));
        request.setAttribute("dir1", getContextRelativeAbsolutePathToDirectory(dir1));
        request.setAttribute("dir2", getContextRelativeAbsolutePathToDirectory(dir2));

        Utility.setDefaultHtmlContentType(response);
        response.getWriter().print(Utility.getAdminContents("FileAdminDeleteWarning.jsp", request, response));
    }

    private void outputBlankFilenameError(File dir1, File dir2, HttpServletResponse response,
                                          HttpServletRequest request) throws IOException, ServletException {
        request.setAttribute("dir1", getContextRelativeAbsolutePathToDirectory(dir1));
        request.setAttribute("dir2", getContextRelativeAbsolutePathToDirectory(dir2));
        Utility.setDefaultHtmlContentType(response);
        response.getWriter().print(Utility.getAdminContents("FileAdminNameBlank.jsp", request, response));
    }

    private void moveOk(HttpServletRequest mp, File[] roots) throws IOException {
        fromSourceToDestination(mp, roots, (source, dest) -> {
            dest.getParentFile().mkdirs();
            if (source.isFile()) {
                FileUtils.copyFile(source, dest);
            }
            if (source.length() == dest.length()) {
                FileUtils.forceDelete(source);
            }
        });
    }

    private void fromSourceToDestination(HttpServletRequest mp, File[] roots,
                                         FromSourceFileToDestinationFileCommand command) throws IOException {
        File srcdir = getContextRelativeDirectoryFromRequest(mp, "source");
        File dstdir = getContextRelativeDirectoryFromRequest(mp, "dest");
        String files = mp.getParameter("files");
        if (isUnderRoot(srcdir, roots) && isUnderRoot(dstdir, roots)) {
            StringTokenizer st = new StringTokenizer(files, ":;");
            while (st.hasMoreTokens()) {
                String foo = st.nextToken();
                File source = new File(srcdir, foo);
                File dest = new File(dstdir, foo);
                command.execute(source, dest);
            }
        }
    }

    private void copyOk(HttpServletRequest mp, File[] roots) throws IOException {
        fromSourceToDestination(mp, roots, (source, destination) -> {
            if (source.isDirectory()) {
                destination.mkdir();
            } else {
                FileUtils.copyFile(source, destination);
            }
        });
    }

    private void deleteOk(HttpServletRequest mp, File[] roots) throws IOException {
        String files = mp.getParameter("files");
        File path = getContextRelativeDirectoryFromRequest(mp, "source");
        if (null != files && null != path) {
            StringTokenizer st = new StringTokenizer(files, ":;");
            while (st.hasMoreTokens()) {
                File foo = new File(path, st.nextToken());
                if (foo.exists() && isUnderRoot(foo.getParentFile(), roots)) {
                    FileUtils.forceDelete(foo);
                }
            }
        }
    }

    private File changeDir(String[] files, File dir, File[] roots) throws IOException {
        File resultDir = dir;
        if (files != null && files.length == 1) {    //Has the user chosen just one dir?
            String filename = files[0];
            if (filename.startsWith(File.separator)) {
                resultDir = new File(Imcms.getPath(), filename);
            } else {                    //Is the dir one of the roots?
                File newDir = new File(dir, filename);        //No? Treat it like a relative path...
                if (newDir.isDirectory()) {            //It IS a directory, i hope?
                    resultDir = newDir;
                }
            }
        }
        if (!isUnderRoot(resultDir, roots)) {
            return dir;
        }
        return resultDir.getCanonicalFile();
    }

    private File[] makeAbsoluteFileList(File parent, String[] filePaths) {
        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            String filePath = filePaths[i];
            files[i] = new File(filePath);
        }
        return makeAbsoluteFileList(parent, files);
    }

    /**
     * Takes a list of files that are supposed to share a common parent, and returns them in an array.
     */
    private File[] makeAbsoluteFileList(File parent, File[] files) {
        if (files == null || parent == null) {
            return null;
        }
        LinkedList list = new LinkedList();
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getPath();
            if (!("..".equals(filename) || new File(filename).isAbsolute())) {
                list.add(new File(parent, filename));
            }
        }
        return (File[]) list.toArray(new File[list.size()]);
    }

    /**
     * Takes a list of files that share a common parent, orphans them, and returns them in an array.
     */
    private File[] makeRelativeFileList(File relativeParentDir, File[] files) throws IOException {
        if (files == null || relativeParentDir == null) {
            return null;
        }
        File[] relativeFileList = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            relativeFileList[i] = FileUtility.relativizeFile(relativeParentDir, files[i]);
        }
        return relativeFileList;
    }

    /**
     * Takes a list of files and dirs in one dir, and recursively adds the files of the subdirs.
     */
    private File[] makeFileTreeList(File[] files, boolean dirfirst) {
        if (files == null) {
            return new File[0];
        }
        LinkedList list = new LinkedList();
        for (int i = 0; i < files.length; i++) {
            if (dirfirst) {
                list.add(files[i]);
            }
            if (files[i].isDirectory()) {
                File[] sub_list = makeFileTreeList(files[i].listFiles(), dirfirst);
                Collections.addAll(list, sub_list);
            }
            if (!dirfirst) {
                list.add(files[i]);
            }
        }
        File[] result = new File[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (File) list.removeFirst();
        }
        return result;
    }

    private String parseFileAdmin(HttpServletRequest request, HttpServletResponse response, File fd1, File fd2) throws IOException, ServletException {

        File[] rootlist = getRoots();

        if (fd1 != null) {
            request.setAttribute("dir1", getContextRelativeAbsolutePathToDirectory(fd1));
            String optionlist = createDirectoryOptionList(rootlist, fd1);
            request.setAttribute("files1", optionlist);
        } else {
            request.setAttribute("dir1", "");
            request.setAttribute("files1", "");
        }
        if (fd2 != null) {
            request.setAttribute("dir2", getContextRelativeAbsolutePathToDirectory(fd2));
            String optionlist = createDirectoryOptionList(rootlist, fd2);
            request.setAttribute("files2", optionlist);
        } else {
            request.setAttribute("dir2", "");
            request.setAttribute("files2", "");
        }

        return Utility.getAdminContents("FileAdmin.jsp", request, response);
    }

    private String getContextRelativeAbsolutePathToDirectory(File dir) throws IOException {
        return File.separator + getPathRelativeTo(Imcms.getPath(), dir) + File.separator;
    }

    private String getPathRelativeTo(File root, File file) throws IOException {
        root = root.getCanonicalFile();
        file = file.getCanonicalFile();
        if (!FileUtility.directoryIsAncestorOfOrEqualTo(root, file)) {
            return file.getAbsolutePath();
        }
        if (file.equals(root)) {
            return "";
        }
        return FileUtility.relativizeFile(root, file).getPath();
    }

    private String createDirectoryOptionList(File[] rootlist, File directory) throws IOException {
        StringBuffer optionlist = new StringBuffer();
        File webappPath = Imcms.getPath();
        for (int i = 0; i < rootlist.length; i++) {
            String dirname = getPathRelativeTo(webappPath, rootlist[i]);
            optionlist.append(getDirectoryOption(File.separator + dirname + File.separator, File.separator + dirname + File.separator));
        }
        File parent = directory.getCanonicalFile().getParentFile();
        if (isUnderRoot(parent, rootlist)) {
            optionlist.append(getDirectoryOption(".." + File.separator, ".." + File.separator));
        }
        File[] dirlist = directory.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        Arrays.sort(dirlist, getFileComparator());
        for (int i = 0; null != dirlist && i < dirlist.length; i++) {
            optionlist.append(getDirectoryOption(dirlist[i].getName() + File.separator, dirlist[i].getName() + File.separator));
        }
        File[] filelist = directory.listFiles((FileFilter) new NotFileFilter(DirectoryFileFilter.INSTANCE));
        Arrays.sort(filelist, getFileComparator());

        for (int i = 0; null != filelist && i < filelist.length; i++) {
            String formatedFileSize = HumanReadable.getHumanReadableByteSize(filelist[i].length());
            String filename = filelist[i].getName();
            String fileNameAndSize = filename + " [" + formatedFileSize + "]";
            optionlist.append("<option value=\"");
            optionlist.append(StringEscapeUtils.escapeHtml4(filename));
            optionlist.append("\">");
            optionlist.append(StringEscapeUtils.escapeHtml4(fileNameAndSize));
            optionlist.append("</option>");
        }
        return optionlist.toString();
    }

    private String getDirectoryOption(String value, String text) {
        return "<option style=\"background-color:#f0f0f0\" value=\""
                + StringEscapeUtils.escapeHtml4(value) + "\">"
                + StringEscapeUtils.escapeHtml4(text) + "</option>";
    }

    private Comparator<File> getFileComparator() {
        return (filea, fileb) -> {
            //--- Sort directories before files,
            //    otherwise alphabetical ignoring case.
            if (filea.isDirectory() && !fileb.isDirectory()) {
                return -1;
            } else if (!filea.isDirectory() && fileb.isDirectory()) {
                return 1;
            } else {
                return filea.getName().compareToIgnoreCase(fileb.getName());
            }

        };
    }

    private interface FromSourceFileToDestinationFileCommand {
        void execute(File source, File destination) throws IOException;
    }

}
