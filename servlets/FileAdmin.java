
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.MultipartFormdataParser;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class FileAdmin extends HttpServlet {

    private final static Logger log = Logger.getLogger( "FileAdmin" );
    private static final int BUFFER_SIZE = 65536;

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        String host = req.getHeader( "Host" );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        // Check if user logged on
        UserDomainObject user;
        if ( ( user = Utility.getLoggedOnUserOrRedirect( req, res, start_url ) ) == null ) {
            return;
        }

        if ( !imcref.checkAdminRights( user ) ) {
            Utility.redirect( req, res, start_url );
            return;
        }

        Utility.setNoCache( res );

        File dir1 = null;
        File dir2 = null;

        File[] roots = getRoots();
        switch ( roots.length ) {
            default:
                dir1 = roots[0];
                dir2 = roots[1];
                break;
            case 1:
                dir1 = roots[0];
                dir2 = dir1;
                break;
            case 0:
        }

        outputFileAdmin( res, user, dir1, dir2 );
    }

    /**
     * Check to see if the path is a child to one of the rootpaths
     */
    private boolean isUnderRoot( File path, File[] roots ) throws IOException {
        for ( int i = 0; i < roots.length; i++ ) {
            if ( path.getCanonicalPath().startsWith( roots[i].getCanonicalPath() ) ) {
                return true;
            }
        }
        return false;
    }

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        String host = req.getHeader( "Host" );
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        String start_url = imcref.getStartUrl();

        UserDomainObject user;
        if ( ( user = Utility.getLoggedOnUserOrRedirect( req, res, start_url ) ) == null ) {
            return;
        }

        if ( !imcref.checkAdminRights( user ) ) {
            Utility.redirect( req, res, start_url );
            return;
        }

        Utility.setNoCache( res );

        MultipartFormdataParser mp = getMultiPartFormDataParserForRequest( req );

        if ( mp.getParameter( "cancel" ) != null ) {
            Utility.redirect( req, res, "AdminManager" );
            return;
        }

        File[] roots = getRoots();

        File dir1 = new File( mp.getParameter( "dir1" ) );
        if ( !isUnderRoot( dir1, roots ) ) {
            doGet( req, res );
        }
        File dir2 = new File( mp.getParameter( "dir2" ) );
        if ( !isUnderRoot( dir2, roots ) ) {
            doGet( req, res );
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
            File destDir = dir1;
            outputHasBeenHandled = makeDirectory( name, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "mkdir2" ) != null ) {
            File destDir = dir2;
            outputHasBeenHandled = makeDirectory( name, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "delete1" ) != null ) {
            File dir = dir1;
            String[] files = files1;
            outputHasBeenHandled = delete( dir, files, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "delete2" ) != null ) {
            File dir = dir2;
            String[] files = files2;
            outputHasBeenHandled = delete( dir, files, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "deleteok" ) != null ) {
            deleteOk( mp, roots );
        } else if ( mp.getParameter( "upload1" ) != null ) {
            File destDir = dir1;
            outputHasBeenHandled = upload( mp, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "upload2" ) != null ) {
            File destDir = dir2;
            outputHasBeenHandled = upload( mp, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "download1" ) != null ) {
            outputHasBeenHandled = download( files1, dir1, res );
        } else if ( mp.getParameter( "download2" ) != null ) {
            outputHasBeenHandled = download( files2, dir2, res );
        } else if ( mp.getParameter( "rename1" ) != null ) {
            File dir = dir1;
            String[] files = files1;
            outputHasBeenHandled = rename( files, name, dir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "rename2" ) != null ) {
            File dir = dir2;
            String[] files = files2;
            outputHasBeenHandled = rename( files, name, dir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "copy1" ) != null ) {
            File sourceDir = dir1;
            File destDir = dir2;
            String[] files = files1;
            outputHasBeenHandled = copy( files, sourceDir, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "copy2" ) != null ) {
            File sourceDir = dir2;
            File destDir = dir1;
            String[] files = files2;
            outputHasBeenHandled = copy( files, sourceDir, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "copyok" ) != null ) {
            copyOk( mp, roots );
        } else if ( mp.getParameter( "move1" ) != null ) {
            File sourceDir = dir1;
            File destDir = dir2;
            String[] files = files1;
            outputHasBeenHandled = move( files, sourceDir, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "move2" ) != null ) {
            File sourceDir = dir2;
            File destDir = dir1;
            String[] files = files2;
            outputHasBeenHandled = move( files, sourceDir, destDir, dir1, dir2, res, user, imcref );
        } else if ( mp.getParameter( "moveok" ) != null ) {
            moveOk( mp, roots );
        }

        if ( !outputHasBeenHandled ) {
            outputFileAdmin( res, user, dir1, dir2 );
        }
    }

    private File[] getRoots() throws IOException {
        String rootpaths = Utility.getDomainPref( "fileadmin_rootpaths" );
        List rootList = new ArrayList();
        if ( rootpaths != null ) {
            StringTokenizer st = new StringTokenizer( rootpaths, ":;" );
            int tokenCount = st.countTokens();
            for ( int i = 0; i < tokenCount; i++ ) {
                String oneRoot = st.nextToken().trim();
                File oneRootFile = Utility.getAbsolutePathFromString( oneRoot );
                if ( oneRootFile.isDirectory() ) {
                    rootList.add( oneRootFile );
                }
            }
        }
        File[] roots = (File[])rootList.toArray( new File[rootList.size()] );
        return roots;
    }

    private boolean move( String[] files, File sourceDir, File destDir, File dir1, File dir2, HttpServletResponse res,
                          UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
        boolean handledOutput = false;
        if ( files != null && !sourceDir.equals( destDir ) ) {
            File[] sourceFileTree = makeFileTreeList( makeAbsoluteFileList( sourceDir, files ), false );
            File[] relativeSourceFileTree = makeRelativeFileList( sourceDir, sourceFileTree );
            StringBuffer optionList = new StringBuffer();
            StringBuffer fileList = new StringBuffer();
            for ( int i = 0; i < relativeSourceFileTree.length; i++ ) {
                File destFile = new File( destDir, relativeSourceFileTree[i].getPath() );
                fileList.append( relativeSourceFileTree[i] ).append( File.pathSeparator );
                if ( destFile.exists() ) {
                    String optionString = destFile.getCanonicalPath() + ( destFile.isDirectory()
                                                                          ? File.separator
                                                                          : " [" + destFile.length() + "]" );
                    optionList.append( "<option>" ).append( optionString ).append( "</option>" );
                }
            }
            if ( optionList.length() > 0 ) {
                outputMoveOverwriteWarning( optionList, sourceDir, destDir, fileList, dir1, dir2, res, user, imcref );
                handledOutput = true;
            } else {
                File[] destFiles = makeAbsoluteFileList( destDir, relativeSourceFileTree );
                for ( int i = 0; i < sourceFileTree.length; i++ ) {
                    destFiles[i].getParentFile().mkdirs();
                    if ( sourceFileTree[i].isFile() ) {
                        FileInputStream fin = new FileInputStream( sourceFileTree[i] );
                        FileOutputStream fout = new FileOutputStream( destFiles[i] );
                        byte[] buffer = new byte[BUFFER_SIZE];
                        for ( int bytes_read; ( bytes_read = fin.read( buffer ) ) != -1; ) {
                            fout.write( buffer, 0, bytes_read );
                        }
                        fout.close();
                        fin.close();
                    }
                    if ( sourceFileTree[i].length() == destFiles[i].length() ) {
                        sourceFileTree[i].delete();
                    }
                }
            }
        }
        return handledOutput;
    }

    private boolean copy( String[] files, File sourceDir, File destDir, File dir1, File dir2, HttpServletResponse res,
                          UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
        boolean handledOutput = false;
        if ( files != null && !sourceDir.equals( destDir ) ) {
            File[] sourceFileTree = makeFileTreeList( makeAbsoluteFileList( sourceDir, files ), true );
            File[] relativeSourceFileTree = makeRelativeFileList( sourceDir, sourceFileTree );
            StringBuffer optionList = new StringBuffer();
            StringBuffer fileList = new StringBuffer();
            for ( int i = 0; i < relativeSourceFileTree.length; i++ ) {
                File destFile = new File( destDir, relativeSourceFileTree[i].getPath() );
                fileList.append( relativeSourceFileTree[i] ).append( File.pathSeparator );
                if ( destFile.exists() ) {
                    String optionString = destFile.getCanonicalPath() + ( destFile.isDirectory()
                                                                          ? File.separator
                                                                          : " [" + destFile.length() + ']' );
                    optionList.append( "<option>" ).append( optionString ).append( "</option>" );
                }
            }
            if ( optionList.length() > 0 ) {
                ouputCopyOverwriteWarning( optionList, sourceDir, destDir, fileList, dir1, dir2, res, user, imcref );
                handledOutput = true;
            } else {
                File[] destFileTree = makeAbsoluteFileList( destDir, relativeSourceFileTree );
                for ( int i = 0; i < sourceFileTree.length; i++ ) {
                    if ( sourceFileTree[i].isDirectory() ) {
                        destFileTree[i].mkdir();
                        continue;
                    }
                    FileInputStream fin = new FileInputStream( sourceFileTree[i] );
                    FileOutputStream fout = new FileOutputStream( destFileTree[i] );
                    byte[] buffer = new byte[BUFFER_SIZE];
                    for ( int bytes_read; ( bytes_read = fin.read( buffer ) ) != -1; ) {
                        fout.write( buffer, 0, bytes_read );
                    }
                }
            }
        }
        return handledOutput;
    }

    private boolean rename( String[] files, String name, File dir, File dir1, File dir2, HttpServletResponse res,
                            UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
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
                byte[] buffer = new byte[BUFFER_SIZE];
                res.setContentLength( fin.available() );
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
                            UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
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
                                   UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
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

    private boolean upload( MultipartFormdataParser mp, File destDir, File dir1, File dir2, HttpServletResponse res,
                            UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
        boolean handledOutput = false;
        String fileContents = mp.getParameter( "file" );
        if ( fileContents == null || fileContents.length() < 1 ) {
            outputBlankFileError( dir1, dir2, res, user, imcref );
            handledOutput = true;
        }
        String filename = ( new File( mp.getFilename( "file" ) ) ).getName();
        File file = new File( destDir, filename );
        File uniqueFile = findUniqueFilename( file );
        if ( file.equals( uniqueFile ) || file.renameTo( uniqueFile ) ) {
            FileOutputStream fout = new FileOutputStream( file );
            fout.write( fileContents.getBytes( "8859_1" ) );
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

    private MultipartFormdataParser getMultiPartFormDataParserForRequest( HttpServletRequest req ) throws IOException {
        int length = req.getContentLength();
        ServletInputStream in = req.getInputStream();
        byte buffer[] = new byte[length];
        int bytes_read = 0;
        while ( bytes_read < length ) {
            bytes_read += in.read( buffer, bytes_read, length - bytes_read );
        }
        String contentType = req.getContentType();
        // Min klass tar emot datan och plockar ut det som är intressant...
        MultipartFormdataParser mp = new MultipartFormdataParser( buffer, contentType );
        return mp;
    }

    private void outputFileAdmin( HttpServletResponse res, UserDomainObject user, File dir1, File dir2 )
            throws IOException {
        res.setContentType( "text/html" );
        res.getOutputStream().print( parseFileAdmin( user, dir1, dir2 ) );
    }

    static File findUniqueFilename( File file ) {
        String filenameWithoutSuffix;
        int counter = 1;
        String previousSuffix = "";
        while ( file.exists() ) {
            filenameWithoutSuffix = StringUtils.substringBeforeLast( file.getName(), previousSuffix );
            String suffix = "." + counter;
            counter++;
            file = new File( file.getParentFile(), filenameWithoutSuffix + suffix );
            previousSuffix = suffix;
        }
        return file;
    }

    private void outputMoveOverwriteWarning( StringBuffer option_list, File sourceDir, File destDir,
                                             StringBuffer file_list, File dir1, File dir2, HttpServletResponse res,
                                             UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#filelist#" );
        vec.add( option_list );
        vec.add( "#source#" );
        vec.add( sourceDir.getCanonicalPath() );
        vec.add( "#dest#" );
        vec.add( destDir.getCanonicalPath() );
        vec.add( "#files#" );
        vec.add( file_list );
        vec.add( "#dir1#" );
        vec.add( dir1.getCanonicalPath() );
        vec.add( "#dir2#" );
        vec.add( dir2.getCanonicalPath() );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        String lang_prefix = user.getLangPrefix();
        out.print( imcref.parseDoc( vec, "FileAdminMoveOverwriteWarning.html", lang_prefix ) );
    }

    private void ouputCopyOverwriteWarning( StringBuffer option_list, File sourceDir, File destDir,
                                            StringBuffer file_list, File dir1, File dir2, HttpServletResponse res,
                                            UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#filelist#" );
        vec.add( option_list );
        vec.add( "#source#" );
        vec.add( sourceDir.getCanonicalPath() );
        vec.add( "#dest#" );
        vec.add( destDir.getCanonicalPath() );
        vec.add( "#files#" );
        vec.add( file_list );
        vec.add( "#dir1#" );
        vec.add( dir1.getCanonicalPath() );
        vec.add( "#dir2#" );
        vec.add( dir2.getCanonicalPath() );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        String lang_prefix = user.getLangPrefix();
        out.print( imcref.parseDoc( vec, "FileAdminCopyOverwriteWarning.html", lang_prefix ) );
    }

    private void outputFileExistedAndTheOriginalWasRenamedNotice( File dir1, File dir2, String newFilename,
                                                                  HttpServletResponse res, UserDomainObject user,
                                                                  IMCServiceInterface imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#dir1#" );
        vec.add( dir1.getCanonicalPath() );
        vec.add( "#dir2#" );
        vec.add( dir2.getCanonicalPath() );
        vec.add( "#filename#" );
        vec.add( newFilename );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        String lang_prefix = user.getLangPrefix();
        out.print( imcref.parseDoc( vec, "FileAdminFileExisted.html", lang_prefix ) );
    }

    private void outputBlankFileError( File dir1, File dir2, HttpServletResponse res, UserDomainObject user,
                                       IMCServiceInterface imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#dir1#" );
        vec.add( dir1.getCanonicalPath() );
        vec.add( "#dir2#" );
        vec.add( dir2.getCanonicalPath() );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        String lang_prefix = user.getLangPrefix();
        out.print( imcref.parseDoc( vec, "FileAdminFileBlank.html", lang_prefix ) );
    }

    private void outputDeleteWarning( File[] filelist, File dir1, File dir2, File sourceDir, HttpServletResponse res,
                                      UserDomainObject user, IMCServiceInterface imcref ) throws IOException {
        StringBuffer files = new StringBuffer();
        StringBuffer optionlist = new StringBuffer();
        for ( int i = 0; i < filelist.length; i++ ) {
            File foo = new File( sourceDir, filelist[i].getPath() );
            String bar = foo.getCanonicalPath() + ( foo.isDirectory()
                                                    ? File.separator
                                                    : " [" + foo.length() + "]" );
            optionlist.append( "<option>" ).append( bar ).append( "</option>" );
            files.append( filelist[i] ).append( File.pathSeparator );
        }
        List vec = new ArrayList();
        vec.add( "#filelist#" );
        vec.add( optionlist.toString() );
        vec.add( "#files#" );
        vec.add( files.toString() );
        vec.add( "#source#" );
        vec.add( sourceDir.getCanonicalPath() );
        vec.add( "#dir1#" );
        vec.add( dir1.getCanonicalPath() );
        vec.add( "#dir2#" );
        vec.add( dir2.getCanonicalPath() );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        String lang_prefix = user.getLangPrefix();
        out.print( imcref.parseDoc( vec, "FileAdminDeleteWarning.html", lang_prefix ) );
        return;
    }

    private void outputBlankFilenameError( File dir1, File dir2, HttpServletResponse res, UserDomainObject user,
                                           IMCServiceInterface imcref ) throws IOException {
        List vec = new ArrayList();
        vec.add( "#dir1#" );
        vec.add( dir1.getCanonicalPath() );
        vec.add( "#dir2#" );
        vec.add( dir2.getCanonicalPath() );
        res.setContentType( "text/html" );
        ServletOutputStream out = res.getOutputStream();
        String lang_prefix = user.getLangPrefix();
        out.print( imcref.parseDoc( vec, "FileAdminNameBlank.html", lang_prefix ) );
        return;
    }

    private void moveOk( MultipartFormdataParser mp, File[] roots ) throws IOException {
        byte[] buffer;
        int bytes_read;
        String src = mp.getParameter( "source" );
        String dst = mp.getParameter( "dest" );
        String files = mp.getParameter( "files" );
        if ( src != null && dst != null && files != null ) {
            File srcdir = new File( src );
            File dstdir = new File( dst );
            if ( isUnderRoot( srcdir, roots ) && isUnderRoot( dstdir, roots ) ) {
                StringTokenizer st = new StringTokenizer( files, ":;" );
                while ( st.hasMoreTokens() ) {
                    String foo = st.nextToken();
                    File source = new File( srcdir, foo );
                    File dest = new File( dstdir, foo );
                    dest.getParentFile().mkdirs();
                    if ( source.isFile() ) {
                        FileInputStream fin = new FileInputStream( source );
                        FileOutputStream fout = new FileOutputStream( dest );
                        buffer = new byte[BUFFER_SIZE];
                        while ( ( bytes_read = fin.read( buffer ) ) != -1 ) {
                            fout.write( buffer, 0, bytes_read );
                        }
                        fout.close();
                        fin.close();
                    }
                    if ( source.length() == dest.length() ) {
                        source.delete();
                    }
                }
            }
        }
    }

    private void copyOk( MultipartFormdataParser mp, File[] roots ) throws IOException {
        byte[] buffer;
        int bytes_read;
        String src = mp.getParameter( "source" );
        String dst = mp.getParameter( "dest" );
        String files = mp.getParameter( "files" );
        if ( src != null && dst != null && files != null ) {
            File srcdir = new File( src );
            File dstdir = new File( dst );
            if ( isUnderRoot( srcdir, roots ) && isUnderRoot( dstdir, roots ) ) {
                StringTokenizer st = new StringTokenizer( files, ":;" );
                while ( st.hasMoreTokens() ) {
                    String foo = st.nextToken();
                    File source = new File( srcdir, foo );
                    File dest = new File( dstdir, foo );
                    if ( source.isDirectory() ) {
                        dest.mkdir();
                        continue;
                    }
                    FileInputStream fin = new FileInputStream( source );
                    FileOutputStream fout = new FileOutputStream( dest );
                    buffer = new byte[BUFFER_SIZE];
                    while ( ( bytes_read = fin.read( buffer ) ) != -1 ) {
                        fout.write( buffer, 0, bytes_read );
                    }
                }
            }
        }
    }

    private void deleteOk( MultipartFormdataParser mp, File[] roots ) throws IOException {
        String files, path;
        if ( ( files = mp.getParameter( "files" ) ) != null && ( path = mp.getParameter( "source" ) ) != null ) {
            StringTokenizer st = new StringTokenizer( files, ":;" );
            while ( st.hasMoreTokens() ) {
                File foo = new File( path, st.nextToken() );
                if ( foo.exists() && isUnderRoot( foo.getParentFile(), roots ) ) {
                    foo.delete();
                }
            }
        }
    }

    private File changeDir( String[] files, File dir, File[] roots ) throws IOException {
        File resultDir = dir;
        if ( files != null && files.length == 1 ) {	//Has the user chosen just one dir?
            File newDir = new File( files[0] );
            if ( !newDir.isAbsolute() ) {					//Is the dir one of the roots?
                newDir = new File( dir, files[0] );		//No? Treat it like a relative path...
                if ( newDir.isDirectory() && isUnderRoot( newDir, roots ) ) {			//It IS a directory, i hope?
                    resultDir = newDir;
                }
            } else if ( isUnderRoot( newDir, roots ) ) {
                resultDir = newDir;
            }
        }
        return resultDir;
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
            File file = files[i];
            relativeFileList[i] = makeRelativeFile( relativeParentDir, file );
        }
        return relativeFileList;
    }

    private File makeRelativeFile( File relativeParentDir, File file ) {
        LinkedList fileParents = new LinkedList();
        File currentParent = file;
        while ( !currentParent.equals( relativeParentDir ) ) {
            fileParents.addFirst( currentParent.getName() );
            currentParent = currentParent.getParentFile();
        }
        File relativeFile = new File( (String)fileParents.removeFirst() );
        for ( Iterator iterator = fileParents.iterator(); iterator.hasNext(); ) {
            relativeFile = new File( relativeFile, (String)iterator.next() );
        }

        return relativeFile;
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
                sub_list = null;
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
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        File[] rootlist = getRoots();
        List vec = new ArrayList();
        DirectoryFilter dirfilt = new DirectoryFilter();
        NotDirectoryFilter notdirfilt = new NotDirectoryFilter();
        if ( fd1 != null ) {
            vec.add( "#dir1#" );
            vec.add( fd1.getCanonicalPath() );
            String optionlist = createDirectoryOptionList( rootlist, fd1, dirfilt, notdirfilt );
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
            vec.add( fd2.getCanonicalPath() );
            String optionlist = createDirectoryOptionList( rootlist, fd2, dirfilt, notdirfilt );
            vec.add( "#files2#" );
            vec.add( optionlist );
        } else {
            vec.add( "#dir2#" );
            vec.add( "" );
            vec.add( "#files2#" );
            vec.add( "" );
        }

        String lang_prefix = user.getLangPrefix();
        return imcref.parseDoc( vec, "FileAdmin.html", lang_prefix );
    }

    private String createDirectoryOptionList( File[] rootlist, File directory, DirectoryFilter dirfilt,
                                              NotDirectoryFilter notdirfilt ) throws IOException {
        StringBuffer optionlist = new StringBuffer();
        for ( int i = 0; i < rootlist.length; i++ ) {
            String root = rootlist[i].getCanonicalPath() + File.separator;
            optionlist.append( "<option value=\"" ).append( root ).append( "\">" ).append( root ).append( "</option>" );
        }
        optionlist.append( "<option value=\"..\">.." ).append( File.separator ).append( "</option>" );
        File[] dirlist = directory.listFiles( dirfilt );
        for ( int i = 0; null != dirlist && i < dirlist.length; i++ ) {
            String directoryName = dirlist[i].getName() + File.separator;
            optionlist.append( "<option value=\"" ).append( directoryName ).append( "\">" ).append( directoryName )
                    .append( "</option>" );
        }
        File[] filelist = directory.listFiles( notdirfilt );
        for ( int i = 0; null != filelist && i < filelist.length; i++ ) {
            String fileNameAndSize = filelist[i].getName() + " [" + filelist[i].length() + "]";
            optionlist.append( "<option value=\"" ).append( filelist[i].getName() ).append( "\">" ).append(
                    fileNameAndSize ).append( "</option>" );
        }
        return optionlist.toString();
    }

    private class DirectoryFilter implements FileFilter {

        public boolean accept( File path ) {
            return path.isDirectory();
        }
    }

    private class NotDirectoryFilter implements FileFilter {

        public boolean accept( File path ) {
            return !path.isDirectory();
        }
    }
}
