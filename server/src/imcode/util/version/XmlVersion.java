package imcode.util.version ;

import java.io.* ;
import java.util.* ;
import java.util.zip.* ;
import java.text.* ;

import javax.xml.parsers.* ;

import org.w3c.dom.* ;
import org.xml.sax.* ;

import imcode.util.DirFileComparator ;

/**
   Creates a DOM-document describing the files of a release.
**/
public class XmlVersion {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    private Document document ;

    public XmlVersion(File fileOrDir) throws ParserConfigurationException, IOException, SAXException {
	init(fileOrDir,"") ;
    }

    public XmlVersion (File fileOrDir, String version) throws ParserConfigurationException, IOException, SAXException {
	version = vcNameToVersion(version) ;
	init(fileOrDir, version) ;
    }

    public String getVersion() {
	return document.getDocumentElement().getAttribute("version") ;
    }

    private void init(File fileOrDir, String version) throws ParserConfigurationException, IOException, SAXException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance() ;
	DocumentBuilder builder = factory.newDocumentBuilder() ;
	if (fileOrDir.isDirectory()) {
	    document = builder.newDocument() ;
	    Element root = createRoot(version) ;
	    createRootContents(root,fileOrDir) ;
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
	    root.setAttribute("generated-at",dateFormat.format(new Date())) ;
	} else {
	    document = builder.parse(fileOrDir) ;
	}
    }

    private String vcNameToVersion(String vcName) {
	if (vcName.startsWith("$Name:") // Line break here to protect us from CVS finding the tag here.
	    && vcName.endsWith("$")) {
	    vcName = vcName.substring(6,vcName.length()-1) ;
	}
	vcName = vcName.trim() ;
	return vcName ;
    }

    private Element createRoot(String version) {
	Element root = (Element) document.createElement("release") ;
	root.setAttribute("version",version) ;
	document.appendChild(root) ;
	return root ;
    }

    private void createRootContents(Element root, File dir) throws IOException {
	File[] files = dir.listFiles() ;
	java.util.Arrays.sort(files, new DirFileComparator()) ;
	appendFileElements(root,files) ;
    }

    private Element getFileElement(File file) throws IOException {
	Element theElement = document.createElement("file") ;
	FileInfo fileInfo = new FileInfo(file) ;
	theElement.setAttribute("name",fileInfo.getName()) ;
	theElement.setAttribute("size",""+fileInfo.getLength()) ;
	if (fileInfo.getVcRevision() != null) {
	    theElement.setAttribute("vc-rev",fileInfo.getVcRevision()) ;
	}
	if (fileInfo.getVcDate() != null) {
	    theElement.setAttribute("vc-date",fileInfo.getVcDate()) ;
	}
	theElement.setAttribute("crc32",""+fileInfo.getCRC32Checksum()) ;
	return theElement ;
    }

    private Element getDirectoryElement(File directory) throws IOException {
	File[] files = directory.listFiles() ;
	java.util.Arrays.sort(files, new DirFileComparator()) ;
	Element	theElement = document.createElement("directory") ;
	theElement.setAttribute("name", directory.getName()) ;
	appendFileElements(theElement,files) ;
	return theElement ;
    }

    private void appendFileElements(Element element, File[] files) throws IOException {
	for (int i = 0; i < files.length; ++i) {
	    Element child = null ;
	    if (files[i].isDirectory()) {
		child = getDirectoryElement(files[i]) ;
	    } else {
		child = getFileElement(files[i]) ;
	    }
	    element.appendChild(child) ;
	}
    }

    public Document getDocument() {
	return document ;
    }

}
