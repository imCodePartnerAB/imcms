package imcode.external.diverse;

import java.io.*;

import imcode.server.*;

public class RmiConf implements IMCConstants {


    /**
     * Get the url-path for the images for an external document.
     * @param imcref The IMCServiceInterface instance.
     * @param metaId The id for the external document.
     * @param lang_prefix
     * @return The url-path to the images for an external document.
     */
    public static String getExternalImageFolder(IMCServiceInterface imcref, int metaId, String lang_prefix) {
	return imcref.getImcmsUrl() + lang_prefix + "/images/"
	    + imcref.getDocType(metaId) + '/' ;
    } // end getExternalImageHomeFolder


    /**
     * Get the path to the images for an external internalDocument.
     * @param imcref The IMCServiceInterface instance.
     * @param metaId The id for the external internalDocument.
     * @param lang_prefix
     * @return The path to the images for an external internalDocument. Example :D:\apache\
     */
    public static File getImagePathForExternalDocument(IMCServiceInterface imcref, int metaId, String lang_prefix) {

        File imageFolder = imcref.getImcmsPath();
        imageFolder = new File( imageFolder, lang_prefix );
        imageFolder = new File( imageFolder, "images/" + imcref.getDocType( metaId ) );

        return imageFolder;
    }


} // End class
