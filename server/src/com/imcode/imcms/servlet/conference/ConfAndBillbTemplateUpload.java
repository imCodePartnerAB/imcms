package com.imcode.imcms.servlet.conference;

import imcode.external.diverse.RmiConf;
import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.MultipartFormdataParser;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//former class GenericUpload

public class ConfAndBillbTemplateUpload extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );

        Utility.setDefaultHtmlContentType( res );
        res.setHeader( "Cache-Control", "no-cache; must-revalidate;" );
        res.setHeader( "Pragma", "no-cache;" );

        int length = req.getContentLength();

        ServletInputStream in = req.getInputStream();
        byte[] buffer = new byte[length];
        int bytes_read = 0;
        while ( bytes_read < length ) {
            bytes_read += in.read( buffer, bytes_read, length - bytes_read );
        }

        String contentType = req.getContentType();
        MultipartFormdataParser mp = new MultipartFormdataParser( buffer, contentType );
        String file = mp.getParameter( "file" );
        //log ("Filesize: "+file.length()) ;
        String filename = mp.getFilename( "file" );
        //vet inte om det behövs men borde vi inte oxå kolla att det verkligen är en bild eller image fil

        //ok lets get some info
        String metaId = mp.getParameter( "metaId" );
        File externalPath;

        //lets get the templatefolder to save the file in
        String uploadType = mp.getParameter( "uploadType" );//IMAGE or TEMPLATE
        String folderName = mp.getParameter( "folderName" );
        if ( uploadType.equalsIgnoreCase( "TEMPLATE" ) ) {
            externalPath = new File( imcref.getExternalTemplateFolder( Integer.parseInt( metaId ), user), folderName );
        } else if ( uploadType.equalsIgnoreCase( "IMAGE" ) ) {
            externalPath = new File( RmiConf.getImagePathForExternalDocument( imcref, Integer.parseInt( metaId ), user), folderName );
        } else {
            return;
        }

        File fp = new File( filename );
        filename = fp.getName();

        fp = new File( externalPath.toString() );
        if ( !fp.exists() && !fp.isDirectory() ) {
            return;
        }

        fp = new File( fp, filename );
        FileOutputStream fw = new FileOutputStream( fp );
        fw.write( file.getBytes( "8859_1" ) );
        fw.close();
        res.sendRedirect( mp.getParameter( "target" ) );
        return;
    }
}
