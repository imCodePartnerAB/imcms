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
            System.out.println("Visiting directory "+file.getPath()) ;
            return true;
        }
        String outFileSuffix = ".out";
        File destFile = new File( file.getPath() + outFileSuffix );
        String fileName = file.getName();
        if ( fileName.startsWith( "." ) || fileName.endsWith( outFileSuffix ) ) {
            return false;
        }
        try {
            translateFile( file, destFile );
            if ( !destFile.renameTo( file ) ) {
                throw new IOException( "Failed to rename " + destFile + " to " + file );
            }
        } catch ( IOException ioe ) {
            System.err.println( ioe.toString() );
            System.exit(1) ;
        }
        return true;
    }

    private void translateFile( File sourceFile, File destFile ) throws IOException {
        InputStream sourceStream = new FileInputStream( sourceFile );
        FileOutputStream destStream = new FileOutputStream( destFile );
        LineReader lineReader = new LineReader(new BufferedReader( new InputStreamReader( sourceStream ) ) );
        BufferedWriter destWriter = new BufferedWriter( new OutputStreamWriter( destStream ) );
        for ( String line; null != ( line = lineReader.readLine() ); ) {
            String translatedLine = translateLine( sourceFile, line );
            destWriter.write( translatedLine );
        }
        destWriter.flush();
        destWriter.close();
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
