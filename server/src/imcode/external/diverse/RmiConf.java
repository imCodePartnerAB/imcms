package imcode.external.diverse;

import java.io.*;

import imcode.server.*;
import imcode.server.user.UserDomainObject;

public class RmiConf implements IMCConstants {

    /**
     * Get the path to the images for an external document.
     * @param imcref The IMCServiceInterface instance.
     * @param metaId The id for the external document.
     * @param user
     * @return The path to the images for an external document. Example :D:\apache\
     */
    public static File getImagePathForExternalDocument(IMCServiceInterface imcref, int metaId, UserDomainObject user) {

        String langPrefix = imcref.getUserLangPrefixOrDefaultLanguage(user) ;
        File imageFolder = imcref.getImcmsPath();
        imageFolder = new File( imageFolder, langPrefix );
        imageFolder = new File( imageFolder, "images/" + imcref.getDocType( metaId ) );

        return imageFolder;
    }


} // End class
