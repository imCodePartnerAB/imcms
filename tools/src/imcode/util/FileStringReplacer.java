package imcode.util;

import java.io.*;
import java.util.Properties;

/**
 * @author kreiger
 */
public class FileStringReplacer implements FileFilter {

    private String string;
    private String replacement;

    public FileStringReplacer( String string, String replacement ) {

        this.string = string;
        this.replacement = replacement;
    }

    public boolean accept( File file ) {
        if ( file.isDirectory() ) {
            return true;
        }
        String outFileSuffix = ".out";
        File destFile = new File( file.getPath() + outFileSuffix );
        String fileName = file.getName();
        if ( fileName.startsWith( "." ) || fileName.endsWith( outFileSuffix ) ) {
            return false;
        }
        try {
            if (translateFile( file, destFile )) {
                if ( !destFile.renameTo( file ) ) {
                    throw new IOException( "Failed to rename " + destFile + " to " + file );
                }
            } else {
                if ( !destFile.delete() ) {
                    throw new IOException( "Failed to delete " + destFile );
                }
            }
        } catch ( IOException ioe ) {
            System.err.println( ioe.toString() );
            System.exit(1) ;
        }
        return true;
    }

    private boolean translateFile( File sourceFile, File destFile ) throws IOException {
        InputStream sourceStream = new FileInputStream( sourceFile );
        FileOutputStream destStream = new FileOutputStream( destFile );
        LineReader lineReader = new LineReader(new BufferedReader( new InputStreamReader( sourceStream ) ) );
        BufferedWriter destWriter = new BufferedWriter( new OutputStreamWriter( destStream ) );
        boolean linesTranslated = false ;
        for ( String line; null != ( line = lineReader.readLine() ); ) {
            String translatedLine = translateLine( sourceFile, line );
            if (!translatedLine.equals(line)) {
                linesTranslated = true ;
            }
            destWriter.write( translatedLine );
        }
        destWriter.flush();
        destWriter.close();
        return linesTranslated ;
    }

    private String translateLine( File sourceFile, String line ) {
        String translatedLine = line;
        for (int index = 0; -1 != (index = translatedLine.indexOf(string,index)); ) {
            translatedLine = translatedLine.substring(0,index) + replacement + translatedLine.substring(index+string.length()) ;
            index += replacement.length() ;
            System.out.println("Replaced '"+string+"' in file "+sourceFile.getPath()) ;
        }
        return translatedLine;
    }

}
