package imcode.util.version ;

import java.io.* ;
import java.util.* ;
import java.util.zip.* ;

public class FileInfo {

    protected String name ;
    protected long length ;
    protected Date lastModified ;
    protected String vcRevision ;
    protected String vcDate ;
    protected int lineCount ;
    protected long crc32Checksum ;

    public FileInfo(File file) throws IOException {
	this.name = file.getName() ;
	this.length = file.length() ;
	this.lastModified = new Date(file.lastModified()) ;
	Checksum checksum = new CRC32() ;
	CheckedInputStream checkedStream = new CheckedInputStream(new FileInputStream(file),checksum) ;
	LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(checkedStream)) ;
	for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
	    if (vcRevision == null) {
		int revisionIndex = line.indexOf("$Revision:") ;
		if (revisionIndex != -1) {
		    int revisionEndIndex = line.indexOf("$",revisionIndex+1) ;
		    if (revisionEndIndex != -1 && line.charAt(revisionIndex+10) == ' ' && line.charAt(revisionEndIndex-1) == ' ') {
			vcRevision = line.substring(revisionIndex+11,revisionEndIndex-1) ;
		    }
		}
	    }
	    if (vcDate == null) {
		int dateIndex = line.indexOf("$Date:") ;
		if (dateIndex != -1) {
		    int dateEndIndex = line.indexOf("$",dateIndex+1) ;
		    if (dateEndIndex != -1 && line.charAt(dateIndex+6) == ' ' && line.charAt(dateEndIndex-1) == ' ') {
			vcDate = line.substring(dateIndex+7,dateEndIndex-1) ;
		    }
		}
	    }
	}
	this.lineCount = lineReader.getLineNumber() ;
	this.crc32Checksum = checksum.getValue() ;
	lineReader.close() ;
    }

    public String getName() {
	return name ;
    }

    public long getLength() {
	return length ;
    }

    public Date getLastModified() {
	return lastModified ;
    }

    public String getVcRevision() {
	return vcRevision ;
    }

    public String getVcDate() {
	return vcDate ;
    }

    public int getLineCount() {
	return lineCount ;
    }

    public long getCRC32Checksum() {
	return crc32Checksum ;
    }


}
