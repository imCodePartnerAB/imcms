package imcode.external.diverse;

import java.io.*;

import imcode.server.*;

public class RmiConf implements IMCConstants {


    /**
     * Get the url-path for the images for an external document.
     * @param imcref The IMCServiceInterface instance.
     * @param metaId The id for the external document.
     * @return The url-path to the images for an external document. Ex: ../imcmsimages/se/
     */
    public static String getExternalImageFolder(IMCServiceInterface imcref, int metaId ) {
	return imcref.getImageUrl() + '/' + imcref.getDefaultLanguageAsIso639_1() + '/'
	    + imcref.getDocType(metaId) + '/' ;
    } // end getExternalImageHomeFolder


    /**
     * Get the path to the images for an external internalDocument.
     * @param imcref The IMCServiceInterface instance.
     * @param metaId The id for the external internalDocument.
     * @return The path to the images for an external internalDocument. Example :D:\apache\
     */
    public static File getImagePathForExternalDocument( IMCServiceInterface imcref, int metaId ) {

        File imageFolder = imcref.getImcmsImagePath();
        imageFolder = new File( imageFolder, imcref.getDefaultLanguageAsIso639_1() );
        imageFolder = new File( imageFolder, "" + imcref.getDocType( metaId ) );

        return imageFolder;
    }


} // End class
