package com.imcode.imcms.servlet.superadmin;

import com.imcode.util.HumanReadable;
import com.imcode.util.MultipartHttpServletRequest;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import imcode.util.io.FileUtility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class FileAdmin extends HttpServlet {

    private final static Logger log = Logger.getLogger( "FileAdmin" );
    private static final int BUFFER_SIZE = 65536;
    private static final String ADMIN_TEMPLATE_FILE_ADMIN_COPY_OVERWRIGHT_WARNING = "FileAdminCopyOverwriteWarning.html";
    private static final String ADMIN_TEMPLATE_FILE_ADMIN_MOVE_OVERWRITE_WARNING = "FileAdminMoveOverwriteWarning.html";

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        Utility.setNoCache( res );

        File dir1 = null;
        File dir2 = null;

        File[] roots = getRoots();
        if (roots.length > 0) {
            dir1 = roots[0];
            if (roots.length > 1) {
                dir2 = roots[1];
            }
        }

        outputFileAdmin( res, user, dir1, dir2 );
    }

    /**
     * Check to see if the path is a child to one of the rootpaths
     */
    private boolean isUnderRoot( File path, File[] roots ) {
        for ( int i = 0; i < roots.length; i++ ) {
            if ( FileUtility.directoryIsAncestorOfOrEqualTo( roots[i], path ) ) {
                return true;
            }
        }
        return false;
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        ImcmsServices imcref = Imcms.getServices();

        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res );
            return;
        }

        Utility.setNoCache( res );

        MultipartHttpServletRequest mp = new MultipartHttpServletRequest(req);

        if ( mp.getParameter( "cancel" ) != null ) {
            res.sendRedirect( "AdminManager" );
            return;
        }

        File[] roots = getRoots();

        File dir1 = getContextRelativeDirectoryFromRequest(mp, "dir1");
        File dir2 = getContextRelativeDirectoryFromRequest(mp, "dir2");

        if ( !isUnderRoot( dir1, roots ) || !isUnderRoot( dir2, roots )) {
            doGet( req, res );
            return ;
        }
        
        String[] files1 = mp.getParameterValues( "files1" );
        String[] files2 = mp.getParameterValues( "files2" );
        String name = mp.getParameter( "name" );

        boolean outputHasBeenHandled = false;

        if ( mp.getParameter( "change1" ) != null ) {	//UserDomainObject wants to change dir1
            dir1 = changeDir( files1, dir1, roots );
        } else if ( mp.getParameter( "change2" ) != null ) {	//UserDomainObject wants to change dir2
            dir2 = changeDir( files2, dir2, roots );
        } else if ( mp.getParameter( "mkdir1" ) != null ) {
            outputHasBeenHandled = makeDirectory( name, dir1, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "mkdir2" ) != null ) {
            outputHasBeenHandled = makeDirectory( name, dir2, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "delete1" ) != null ) {
            outputHasBeenHandled = delete( dir1, files1, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "delete2" ) != null ) {
            outputHasBeenHandled = delete( dir2, files2, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "deleteok" ) != null ) {
            deleteOk( mp, roots );
        } else if ( mp.getParameter( "upload1" ) != null ) {
            outputHasBeenHandled = upload( mp, dir1, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "upload2" ) != null ) {
            outputHasBeenHandled = upload( mp, dir2, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "download1" ) != null ) {
            outputHasBeenHandled = download( files1, dir1, res );
        } else if ( mp.getParameter( "download2" ) != null ) {
            outputHasBeenHandled = download( files2, dir2, res );
        } else if ( mp.getParameter( "rename1" ) != null ) {
            outputHasBeenHandled = rename( files1, name, dir1, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "rename2" ) != null ) {
            outputHasBeenHandled = rename( files2, name, dir2, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "copy1" ) != null ) {
            outputHasBeenHandled = copy( files1, dir1, dir2, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "copy2" ) != null ) {
            outputHasBeenHandled = copy( files2, dir2, dir1, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "copyok" ) != null ) {
            copyOk( mp, roots );
        } else if ( mp.getParameter( "move1" ) != null ) {
            outputHasBeenHandled = move( files1, dir1, dir2, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "move2" ) != null ) {
            outputHasBeenHandled = move( files2, dir2, dir1, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "moveok" ) != null ) {
            moveOk( mp, roots );
        }

        if ( !outputHasBeenHandled ) {
            outputFileAdmin( res, user, dir1, dir2 );
        }
    }

    private File getContextRelativeDirectoryFromRequest(HttpServletRequest request, String parameter) throws IOException {
        File webappPath = Imcms.getPath() ;
        String dirParameter = request.getParameter( parameter );
        return new File( webappPath, dirParameter ).getAbsoluteFile();
    }

    private File[] getRoots() {
        String rootpaths = Imcms.getServices().getConfig().getFileAdminRootPaths();
        List rootList = new ArrayList();
        if ( rootpaths != null ) {
            StringTokenizer st = new StringTokenizer( rootpaths, ":;" );
            int tokenCount = st.countTokens();
            for ( int i = 0; i < tokenCount; i++ ) {
                String oneRoot = st.nextToken().trim();
                File oneRootFile = FileUtility.getFileFromWebappRelativePath( oneRoot );
                if ( oneRootFile.isDirectory() ) {
                    rootList.add( oneRootFile );
                }
            }
        }
        return (File[])rootList.toArray( new File[rootList.size()] );
    }

    private boolean move( String[] files, File sourceDir, File destDir, File dir1, File dir2, HttpServletResponse res,
                          UserDomainObject user, ImcmsServices imcref ) throws IOException {
        boolean handledOutput = false;
        if ( files != null && !sourceDir.equals( destDir ) ) {
            File[] sourceFileTree = makeFileTreeList( makeAbsoluteFileList( sourceDir, files ), false );
            File[] relativeSourceFileTree = makeRelativeFileList( sourceDir, sourceFileTree );
            StringBuffer optionList = new StringBuffer();
            StringBuffer fileList = buildWarningOptions(relativeSourceFileTree, destDir, optionList);
            if ( optionList.length() > 0 ) {
                outputMoveOverwriteWarning( optionList.toString(), sourceDir, destDir, fileList.toString(), dir1, dir2, res, user, imcref );
                handledOutput = true;
            } else {
                File[] destFiles = makeAbsoluteFileList( destDir, relativeSourceFileTree );
                for ( int i = 0; i < sourceFileTree.length; i++ ) {
                    File destFile = destFiles[i];
                    destFile.getParentFile().mkdirs();
                    File sourceFile = sourceFileTree[i];
                    if ( sourceFile.isFile() ) {
                        FileUtils.copyFile( sourceFile, destFile );
                    }
                    if ( sourceFile.length() == destFile.length() ) {
                        FileUtils.forceDelete(sourceFile);
                    }
                }
            }
        }
        return handledOutput;
    }

    private String createWarningFileOptionString(File destFile) {
        File webAppPath = Imcms.getPath();
        return FileUtility.relativizeFile(webAppPath, destFile).getPath() + ( destFile.isDirectory()
                                                                              ? File.separator
                                                                              : " [" + destFile.length() + "]" );
    }

    private boolean copy( String[] files, File sourceDir, File destDir, File dir1, File dir2, HttpServletResponse res,
                          UserDomainObject user, ImcmsServices imcref ) throws IOException {
        boolean handledOutput = false;
        if ( files != null && !sourceDir.equals( destDir ) ) {
            File[] sourceFileTree = makeFileTreeList( makeAbsoluteFileList( sourceDir, files ), true );
            File[] relativeSourceFileTree = makeRelativeFileList( sourceDir, sourceFileTree );
            StringBuffer optionList = new StringBuffer();
            StringBuffer fileList = buildWarningOptions(relativeSourceFileTree, destDir, optionList);
            if ( optionList.length() > 0 ) {
                ouputCopyOverwriteWarning( optionList.toString(), sourceDir, destDir, fileList.toString(), dir1, dir2, res, user, imcref );
                handledOutput = true;
            } else {
                File[] destFileTree = makeAbsoluteFileList( destDir, relativeSourceFileTree );
                for ( int i = 0; i < sourceFileTree.length; i++ ) {
                    File sourceFile = sourceFileTree[i];
                    File destFile = destFileTree[i];
                    if ( sourceFile.isDirectory() ) {
                        destFile.mkdir();
                        continue;
                    }
                    FileUtils.copyFile( sourceFile, destFile );
                }
            }
        }
        return handledOutput;
    }

    private StringBuffer buildWarningOptions(File[] relativeSourceFileTree, File destDir, StringBuffer optionList) {
        StringBuffer fileList = new StringBuffer();
        for ( int i = 0; i < relativeSourceFileTree.length; i++ ) {
            File destFile = new File( destDir, relativeSourceFileTree[i].getPath() );
            fileList.append( relativeSourceFileTree[i] ).append( File.pathSeparator );
            if ( destFile.exists() ) {
                String optionString = createWarningFileOptionString(destFile);
                optionList.append( "<option>" ).append( optionString ).append( "</option>" );
            }
        }
        return fileList;
    }

    private boolean rename( String[] files, String name, File dir, File dir1, File dir2, HttpServletResponse res,
                            UserDomainObject user, ImcmsServices imcref ) throws IOException {
        boolean handledOutput = false;
        if ( files != null && files.length == 1 ) {	//Has the user chosen just one file?
            if ( name != null && name.length() > 0 ) {
                File oldFilename = new File( dir, files[0] );
                File newFilename = new File( dir, name );
                if ( oldFilename.exists() ) {
                    oldFilename.renameTo( newFilename );
                }
            } else {
                outputBlankFilenameError( dir1, dir2, res, user, imcref );
                handledOutput = true;
            }
        }
        return handledOutput;
    }

    private boolean download( String[] files, File dir, HttpServletResponse res ) throws IOException {
        boolean handledOutput = false;
        if ( files != null && files.length == 1 ) {	//Has the user chosen just one file?
            File file = new File( dir, files[0] );
            try {
                res.setContentType( "application/octet-stream" );
                res.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getName() + '\"' );
                BufferedInputStream fin = new BufferedInputStream( new FileInputStream( file ) );
                res.setContentLength( fin.available() );
                byte[] buffer = new byte[BUFFER_SIZE];
                ServletOutputStream out = res.getOutputStream();
                for ( int bytes_read; ( bytes_read = fin.read( buffer ) ) != -1; ) {
                    out.write( buffer, 0, bytes_read );
                }
                handledOutput = true;
            } catch ( FileNotFoundException ex ) {
                // FIXME: Error dialog?
                log.debug( "Download failed", ex );
            }
        }
        return handledOutput;
    }

    private boolean delete( File dir, String[] files, File dir1, File dir2, HttpServletResponse res,
                            UserDomainObject user, ImcmsServices imcref ) throws IOException {
        boolean handledOutput = false;
        File[] farray = makeFileTreeList( makeAbsoluteFileList( dir, files ), false );
        File[] filelist = makeRelativeFileList( dir, farray );
        if ( filelist != null && filelist.length > 0 ) {
            outputDeleteWarning( filelist, dir1, dir2, dir, res, user, imcref );
            handledOutput = true;
        }
        return handledOutput;
    }

    private boolean makeDirectory( String name, File dir, File dir1, File dir2, HttpServletResponse res,
                                   UserDomainObject user, ImcmsServices imcref ) throws IOException {
        boolean handledOutput = false;
        if ( name != null && name.length() > 0 ) {
            File newname = new File( dir, name );
            if ( !newname.exists() ) {
                newname.mkdir();
            }
        } else {
            outputBlankFilenameError( dir1, dir2, res, user, imcref );
            handledOutput = true;
        }
        return handledOutput;
    }

    private boolean upload( MultipartHttpServletRequest mp, File destDir, File dir1, File dir2, HttpServletResponse res,
                            UserDomainObject user, ImcmsServices imcref ) throws IOException {
        boolean handledOutput = false;
        String fileContents = mp.getParameter( "file" );
        if ( fileContents == null || fileContents.length() < 1 ) {
            outputBlankFileError( dir1, dir2, res, user, imcref );
            handledOutput = true;
            return handledOutput;
        }
        String filename = mp.getParameterFileItem( "file" ).getName() ;
        File file = new File( destDir, filename );
        File uniqueFile = findUniqueFilename( file );
        if ( file.equals( uniqueFile ) || file.renameTo( uniqueFile ) ) {
            FileOutputStream fout = new FileOutputStream( file );
            byte[] bytes = fileContents.getBytes( "8859_1" );
            fout.write( bytes );
            fout.flush();
            fout.close();
            if ( !file.equals( uniqueFile ) ) {
                outputFileExistedAndTheOriginalWasRenamedNotice( dir1, dir2, uniqueFile.getName(), res, user, imcref );
                handledOutput = true;
            }
        } else {
            // FIXME: Output failed-to-rename-original-file error dialog
            handledOutput = false;
        }
        return handledOutput;
    }

    private void outputFileAdmin( HttpServletResponse res, UserDomainObject user, File dir1, File dir2 )
            throws IOException {
        Utility.setDefaultHtmlContentType( res );
        res.getOutputStream().print( parseFileAdmin( user, dir1, dir2 ) );
    }

    static File findUniqueFilename(File file) {
        File uniqueFile = file;
        int counter = 1;
        String previousSuffix = "";
        while ( uniqueFile.exists() ) {
            String filenameWithoutSuffix = StringUtils.substringBeforeLast(uniqueFile.getName(), previousSuffix);
            String suffix = "." + counter;
            counter++;
            uniqueFile = new File(uniqueFile.getParentFile(), filenameWithoutSuffix + suffix);
            previousSuffix = suffix;
        }
        return uniqueFile;
    }

    private void outputMoveOverwriteWarning( String option_list, File sourceDir, File destDir,
                                             String file_list, File dir1, File dir2, HttpServletResponse res,
                                             UserDomainObject user, ImcmsServices imcref ) throws IOException {
        outputWarning(option_list, sourceDir, destDir, file_list, dir1, dir2, res, imcref, ADMIN_TEMPLATE_FILE_ADMIN_MOVE_OVERWRITE_WARNING, user);
    }

    private void ouputCopyOverwriteWarning( String option_list, File sourceDir, File destDir,
                                            String file_list, File dir1, File dir2, HttpServletResponse res,
                                            UserDomainObject user, ImcmsServices imcref ) throws IOException {
        outputWarning(option_list, sourceDir, destDir, file_list, dir1, dir2, res, imcref, ADMIN_TEMPLATE_FILE_ADMIN_COPY_OVERWRIGHT_WARNING, user);
    }

    private void outputWarning(String option_list, File sourceDir, File destDir, String file_list, File dir1, File dir2,
                               HttpServletResponse res, ImcmsServices imcref, String template,
                               UserDomainObject user) throws IOException {
        List vec = new ArrayList();
        vec.add( "#filelist#" );
        vec.add( option_list );
        vec.add( "#source#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(sourceDir) );
        vec.add( "#dest#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(destDir) );
        vec.add( "#files#" );
        vec.add( file_list );
        vec.add( "#dir1#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir1) );
        vec.add( "#dir2#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir2) );
        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        out.print( imcref.getAdminTemplate( template, user, vec ) );
    }

    private void outputFileExistedAndTheOriginalWasRenamedNotice( File dir1, File dir2, String newFilename,
                                                                  HttpServletResponse res, UserDomainObject user,
                                                                  ImcmsServices imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#dir1#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir1) );
        vec.add( "#dir2#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir2) );
        vec.add( "#filename#" );
        vec.add( newFilename );
        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        out.print( imcref.getAdminTemplate( "FileAdminFileExisted.html", user, vec ) );
    }

    private void outputBlankFileError( File dir1, File dir2, HttpServletResponse res, UserDomainObject user,
                                       ImcmsServices imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#dir1#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir1) );
        vec.add( "#dir2#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir2) );
        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        out.print( imcref.getAdminTemplate( "FileAdminFileBlank.html", user, vec ) );
    }

    private void outputDeleteWarning( File[] filelist, File dir1, File dir2, File sourceDir, HttpServletResponse res,
                                      UserDomainObject user, ImcmsServices imcref ) throws IOException {
        StringBuffer files = new StringBuffer();
        StringBuffer optionlist = new StringBuffer();
        for ( int i = 0; i < filelist.length; i++ ) {
            File foo = new File( sourceDir, filelist[i].getPath() );
            String bar = createWarningFileOptionString(foo);
            optionlist.append( "<option>" ).append( bar ).append( "</option>" );
            files.append( filelist[i] ).append( File.pathSeparator );
        }
        List vec = new ArrayList();
        vec.add( "#filelist#" );
        vec.add( optionlist.toString() );
        vec.add( "#files#" );
        vec.add( StringEscapeUtils.escapeHtml(files.toString()) );
        vec.add( "#source#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(sourceDir) );
        vec.add( "#dir1#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir1) );
        vec.add( "#dir2#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir2) );
        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        out.print( imcref.getAdminTemplate( "FileAdminDeleteWarning.html", user, vec ) );
    }

    private void outputBlankFilenameError( File dir1, File dir2, HttpServletResponse res, UserDomainObject user,
                                           ImcmsServices imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#dir1#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir1) );
        vec.add( "#dir2#" );
        vec.add( getContextRelativeAbsolutePathToDirectory(dir2) );
        Utility.setDefaultHtmlContentType( res );
        ServletOutputStream out = res.getOutputStream();
        out.print( imcref.getAdminTemplate( "FileAdminNameBlank.html", user, vec ) );
    }

    private interface FromSourceFileToDestinationFileCommand {
        void execute(File source, File destination) throws IOException;
    }

    private void moveOk( HttpServletRequest mp, File[] roots ) throws IOException {
        fromSourceToDestination(mp, roots, new FromSourceFileToDestinationFileCommand() {
            public void execute(File source, File dest) throws IOException {
                dest.getParentFile().mkdirs();
                if ( source.isFile() ) {
                    FileUtils.copyFile( source, dest );
                }
                if ( source.length() == dest.length() ) {
                    FileUtils.forceDelete( source );
                }
            }
        });
    }

    private void fromSourceToDestination(HttpServletRequest mp, File[] roots,
                                         FromSourceFileToDestinationFileCommand command) throws IOException {
        File srcdir = getContextRelativeDirectoryFromRequest(mp, "source" );
        File dstdir = getContextRelativeDirectoryFromRequest(mp, "dest" );
        String files = mp.getParameter( "files" );
        if ( isUnderRoot( srcdir, roots ) && isUnderRoot( dstdir, roots ) ) {
            StringTokenizer st = new StringTokenizer( files, ":;" );
            while ( st.hasMoreTokens() ) {
                String foo = st.nextToken();
                File source = new File( srcdir, foo );
                File dest = new File( dstdir, foo );
                command.execute(source, dest);
            }
        }
    }

    private void copyOk( HttpServletRequest mp, File[] roots ) throws IOException {
        fromSourceToDestination(mp, roots, new FromSourceFileToDestinationFileCommand() {
            public void execute(File source, File destination) throws IOException {
                if ( source.isDirectory() ) {
                    destination.mkdir();
                } else {
                    FileUtils.copyFile( source, destination );
                }
            }
        });
    }

    private void deleteOk( HttpServletRequest mp, File[] roots ) throws IOException {
        String files = mp.getParameter( "files" ) ;
        File path = getContextRelativeDirectoryFromRequest(mp, "source") ;
        if ( null != files && null != path ) {
            StringTokenizer st = new StringTokenizer( files, ":;" );
            while ( st.hasMoreTokens() ) {
                File foo = new File( path, st.nextToken() );
                if ( foo.exists() && isUnderRoot( foo.getParentFile(), roots ) ) {
                    FileUtils.forceDelete(foo);
                }
            }
        }
    }

    private File changeDir( String[] files, File dir, File[] roots ) throws IOException {
        File resultDir = dir;
        if ( files != null && files.length == 1 ) {	//Has the user chosen just one dir?
            String filename = files[0];
            if ( filename.startsWith(File.separator) ) {
                File newDir = new File( Imcms.getPath(), filename) ;
                if ( isUnderRoot( newDir, roots ) ) {
                    resultDir = newDir;
                }
            } else {					//Is the dir one of the roots?
                File newDir = new File( dir, filename );		//No? Treat it like a relative path...
                if ( newDir.isDirectory() && isUnderRoot( newDir, roots ) ) {			//It IS a directory, i hope?
                    resultDir = newDir;
                }
            }
        }
        return resultDir.getAbsoluteFile();
    }

    private File[] makeAbsoluteFileList( File parent, String[] filePaths ) {
        File[] files = new File[filePaths.length];
        for ( int i = 0; i < filePaths.length; i++ ) {
            String filePath = filePaths[i];
            files[i] = new File( filePath );
        }
        return makeAbsoluteFileList( parent, files );
    }

    /**
     * Takes a list of files that are supposed to share a common parent, and returns them in an array.
     */
    private File[] makeAbsoluteFileList( File parent, File[] files ) {
        if ( files == null || parent == null ) {
            return null;
        }
        LinkedList list = new LinkedList();
        for ( int i = 0; i < files.length; i++ ) {
            String filename = files[i].getPath();
            if ( !( "..".equals( filename ) || new File( filename ).isAbsolute() ) ) {
                list.add( new File( parent, filename ) );
            }
        }
        return (File[])list.toArray( new File[list.size()] );
    }

    /**
     * Takes a list of files that share a common parent, orphans them, and returns them in an array.
     */
    private File[] makeRelativeFileList( File relativeParentDir, File[] files ) {
        if ( files == null || relativeParentDir == null ) {
            return null;
        }
        File[] relativeFileList = new File[files.length];
        for ( int i = 0; i < files.length; i++ ) {
            relativeFileList[i] = FileUtility.relativizeFile( relativeParentDir, files[i] );
        }
        return relativeFileList;
    }

    /**
     * Takes a list of files and dirs in one dir, and recursively adds the files of the subdirs.
     */
    private File[] makeFileTreeList( File[] files, boolean dirfirst ) {
        if ( files == null ) {
            return new File[0];
        }
        LinkedList list = new LinkedList();
        for ( int i = 0; i < files.length; i++ ) {
            if ( dirfirst ) {
                list.add( files[i] );
            }
            if ( files[i].isDirectory() ) {
                File[] sub_list = makeFileTreeList( files[i].listFiles(), dirfirst );
                for ( int j = 0; j < sub_list.length; j++ ) {
                    list.add( sub_list[j] );
                }
            }
            if ( !dirfirst ) {
                list.add( files[i] );
            }
        }
        File[] result = new File[list.size()];
        for ( int i = 0; i < result.length; i++ ) {
            result[i] = (File)list.removeFirst();
        }
        return result;
    }

    private String parseFileAdmin( UserDomainObject user, File fd1, File fd2 ) throws IOException {
        ImcmsServices imcref = Imcms.getServices();

        File[] rootlist = getRoots();
        List vec = new ArrayList();
        if ( fd1 != null ) {
            vec.add( "#dir1#" );
            vec.add( getContextRelativeAbsolutePathToDirectory(fd1) );
            String optionlist = createDirectoryOptionList( rootlist, fd1 );
            vec.add( "#files1#" );
            vec.add( optionlist );
        } else {
            vec.add( "#dir1#" );
            vec.add( "" );
            vec.add( "#files1#" );
            vec.add( "" );
        }
        if ( fd2 != null ) {
            vec.add( "#dir2#" );
            vec.add( getContextRelativeAbsolutePathToDirectory(fd2) );
            String optionlist = createDirectoryOptionList( rootlist, fd2 );
            vec.add( "#files2#" );
            vec.add( optionlist );
        } else {
            vec.add( "#dir2#" );
            vec.add( "" );
            vec.add( "#files2#" );
            vec.add( "" );
        }

        return imcref.getAdminTemplate( "FileAdmin.html", user, vec );
    }

    private String getContextRelativeAbsolutePathToDirectory(File dir) throws IOException {
        File webappPath = Imcms.getPath();
        return File.separator + getPathRelativeTo( dir, webappPath ) + File.separator;
    }

    private String getPathRelativeTo( File file, File root ) {
        if ( !FileUtility.directoryIsAncestorOfOrEqualTo( root, file ) ) {
            return file.getAbsolutePath();
        }
        if ( file.equals( root ) ) {
            return "";
        }
        return FileUtility.relativizeFile( root, file ).getPath() ;
    }

    private String createDirectoryOptionList( File[] rootlist, File directory ) throws IOException {
        StringBuffer optionlist = new StringBuffer();
        File webappPath = Imcms.getPath();
        for ( int i = 0; i < rootlist.length; i++ ) {
            String dirname = getPathRelativeTo( rootlist[i], webappPath ) ;
            optionlist.append(getDirectoryOption(File.separator + dirname + File.separator, File.separator + dirname + File.separator) );
        }
        File parent = directory.getParentFile();
        if ( isUnderRoot( parent, rootlist )) {
            optionlist.append(getDirectoryOption(".." + File.separator, ".." + File.separator) );
        }
        File[] dirlist = directory.listFiles( (FileFilter)DirectoryFileFilter.INSTANCE );
        Arrays.sort(dirlist, getFileComparator());
        for ( int i = 0; null != dirlist && i < dirlist.length; i++ ) {
            optionlist.append(getDirectoryOption(dirlist[i].getName() + File.separator, dirlist[i].getName() + File.separator));
        }
        File[] filelist = directory.listFiles( (FileFilter)new NotFileFilter( DirectoryFileFilter.INSTANCE ) );
        Arrays.sort(filelist, getFileComparator());

        for ( int i = 0; null != filelist && i < filelist.length; i++ ) {
            String formatedFileSize = HumanReadable.getHumanReadableByteSize( filelist[i].length() );
            String filename = filelist[i].getName();
            String fileNameAndSize = filename + " [" + formatedFileSize + "]";
            optionlist.append("<option value=\"");
            optionlist.append(StringEscapeUtils.escapeHtml(filename));
            optionlist.append("\">");
            optionlist.append(StringEscapeUtils.escapeHtml(fileNameAndSize));
            optionlist.append("</option>");
        }
        return optionlist.toString();
    }

    private String getDirectoryOption(String value, String text) {
        return "<option style=\"background-color:#f0f0f0\" value=\""
               + StringEscapeUtils.escapeHtml(value) + "\">"
               + StringEscapeUtils.escapeHtml(text) + "</option>";
    }

    private Comparator getFileComparator() {
        return new Comparator() {
            public int compare(Object a, Object b) {
                File filea = (File)a;
                File fileb = (File)b;
                //--- Sort directories before files,
                //    otherwise alphabetical ignoring case.
                if (filea.isDirectory() && !fileb.isDirectory()) {
                    return -1;
                } else if (!filea.isDirectory() && fileb.isDirectory()) {
                    return 1;
                } else {
                    return filea.getName().compareToIgnoreCase(fileb.getName());
                }

            }
        };
    }

}
