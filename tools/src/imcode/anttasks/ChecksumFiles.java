/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-apr-01
 * Time: 18:01:16
 */
package imcode.anttasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.math.BigInteger;

public class ChecksumFiles extends Task {

    private List fileSets = new ArrayList();
    private File destFile;
    private boolean append = false ;
    private String digest;

    public void addFileset( FileSet fileSet ) {
        fileSets.add( fileSet );
    }

    public void setDestFile( File destFile ) {
        this.destFile = destFile;
    }

    public void setAppend( boolean append ) {
        this.append = append ;
    }

    public void setDigest(String digest) {
        this.digest = digest ;
    }

    public void execute() {
        validateAttributes();
        BufferedWriter fileWriter = openDestFile( destFile );
        DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        for ( Iterator iterator = fileSets.iterator(); iterator.hasNext(); ) {
            FileSet fileSet = (FileSet)iterator.next();
            outputDataAboutFileSet( fileSet, fileWriter, dateFormat );

        }
        closeDestFile( fileWriter );
    }

    private void closeDestFile( BufferedWriter fileWriter ) {
        try {
            fileWriter.close();
        } catch ( IOException e ) {
            throw new BuildException( e ) ;
        }
    }

    private BufferedWriter openDestFile( File destFile ) {
        try {
            return new BufferedWriter( new FileWriter( destFile, append ) );
        } catch ( IOException e ) {
            throw new BuildException( e );
        }
    }

    private void outputDataAboutFileSet( FileSet fileSet, BufferedWriter destWriter, DateFormat dateFormat ) {
        DirectoryScanner directoryScanner = fileSet.getDirectoryScanner( fileSet.getProject() );
        File baseDir = directoryScanner.getBasedir();
        String[] includedRelativeFilenames = directoryScanner.getIncludedFiles();
        outputDataAboutFilesRelativeToBaseDir( baseDir, includedRelativeFilenames, dateFormat, destWriter );
    }

    private void outputDataAboutFilesRelativeToBaseDir( File baseDir, String[] relativeFilenames,
                                                        DateFormat dateFormat, BufferedWriter destWriter ) {
        for ( int i = 0; i < relativeFilenames.length; i++ ) {
            String relativeFilename = relativeFilenames[i];
            outputDataAboutFileRelativeToBaseDir( baseDir, relativeFilename, dateFormat, destWriter );
        }
    }

    private void outputDataAboutFileRelativeToBaseDir( File baseDir, String relativeFilename, DateFormat dateFormat,
                                      BufferedWriter destWriter ) {
        File file = new File( baseDir, relativeFilename );
        String digestString = digestFile( file );
        digestString = StringUtils.leftPad( digestString, 32 ) ;
        String fileLength = ""+file.length() ;
        fileLength = StringUtils.leftPad(fileLength, 8) ;

        String datetime = dateFormat.format( new Date( file.lastModified() ) );
        try {
            destWriter.write( datetime + " " + fileLength + " " + digestString +" "+relativeFilename);
            destWriter.newLine();
        } catch ( IOException e ) {
            throw new BuildException( e );
        }
    }

    private String digestFile( File includedFile ) {
        MessageDigest messageDigest = getMessageDigest();
        FileInputStream inputStream = openFile( includedFile );
        try {
            byte[] buffer = new byte[16384];
            for ( int bytesRead = 0; -1 != ( bytesRead = inputStream.read( buffer ) ); ) {
                messageDigest.update( buffer, 0, bytesRead );
            }
        } catch ( IOException e ) {
            throw new BuildException( e );
        }
        byte[] digestBytes = messageDigest.digest();
        BigInteger digestInteger = new BigInteger( 1, digestBytes ) ;
        return digestInteger.toString(16) ;
    }

    private FileInputStream openFile( File includedFile ) {
        try {
            return new FileInputStream( includedFile );
        } catch ( FileNotFoundException e ) {
            throw new BuildException( e );
        }
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance( digest );
        } catch ( NoSuchAlgorithmException e ) {
            throw new BuildException( e );
        }
    }

    private void validateAttributes() {
        if ( fileSets.isEmpty() ) {
            throw new BuildException( "Specify at least one fileset." );
        }
        if ( null == destFile ) {
            throw new BuildException( "Specify a destfile." );
        }
    }

}