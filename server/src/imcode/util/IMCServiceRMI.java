package imcode.util;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import imcode.server.*;
import imcode.server.user.User;
import imcode.server.Table;
import imcode.server.ExternalDocType;
import imcode.server.Image;
import imcode.server.SystemData;

import imcode.server.parser.ParserParameters;

import imcode.server.IMCServiceInterface;

/**
 Class to keep track on the RMI-interface to the ImCode netserver.
 All calls are identical to those in IMCServiceInterface, with the exception that you add one parameter in front of each.
 That parameter is a string to indicate the server to use, defined in netservers.cfg.
 */

public class IMCServiceRMI {
   private final static String CVS_REV = "$Revision$";
   private final static String CVS_DATE = "$Date$";

   private static Hashtable interfaces = new Hashtable();	// Keeps track of servers. "ip:port"=interface
   private final static imcode.server.ApplicationServer appServer = new imcode.server.ApplicationServer();

   private static IMCServiceInterface renewInterface( String server ) {
      if( server == null ) {
         throw new IllegalArgumentException( "Server == null" );
      }

      IMCServiceInterface imc = (IMCServiceInterface)appServer.getServerObject( server );

      if( imc == null ) {
         throw new IllegalArgumentException( "Server '" + server + "' not found." );
      }

      interfaces.put( server, imc );
      return imc;
   }

   private static IMCServiceInterface getInterface( String server ) {
      if( server == null ) {
         throw new IllegalArgumentException( "Server == null" );
      }
      IMCServiceInterface imc = (IMCServiceInterface)interfaces.get( server );
      if( imc == null ) {
         imc = renewInterface( server );
      }
      return imc;
   }

   public static IMCServiceInterface getIMCServiceInterfaceByHost( String host ) throws IOException {
      String imcserver = Utility.getDomainPref( "userserver", host );
      return getInterface( imcserver );
   }

   public static IMCServiceInterface getIMCServiceInterface( HttpServletRequest req ) throws IOException {
      String host = req.getHeader( "Host" );
      String imcserver = Utility.getDomainPref( "userserver", host );
      return getInterface( imcserver );
   }

   public static IMCPoolInterface getChatIMCPoolInterface( HttpServletRequest req ) throws IOException {
      String host = req.getHeader( "Host" );
      String imcserver = Utility.getDomainPref( "chat_server", host );
      return getPoolInterface( imcserver );
   }

   public static IMCPoolInterface getConfIMCPoolInterface( HttpServletRequest req ) throws IOException {
      String host = req.getHeader( "Host" );
      String imcserver = Utility.getDomainPref( "conference_server", host );
      return getPoolInterface( imcserver );
   }

   public static IMCPoolInterface getBillboardIMCPoolInterface( HttpServletRequest req ) throws IOException {
      String host = req.getHeader( "Host" );
      String imcserver = Utility.getDomainPref( "billboard_server", host );
      return getPoolInterface( imcserver );
   }

   public static IMCPoolInterface getCalendarIMCPoolInterface( HttpServletRequest req ) throws IOException {
      String host = req.getHeader( "Host" );
      String imcserver = Utility.getDomainPref( "calender_server", host ); // Observe, intentional misspelling of calendar.
      return getPoolInterface( imcserver );
   }

   /**
    * GetInterface. Returns an interface to the host db.
    */
   private static imcode.server.IMCPoolInterface getPoolInterface( String server ) {
      if( server == null ) {
         throw new IllegalArgumentException( "Server == null" );
      }
      imcode.server.IMCPoolInterface imc = (imcode.server.IMCPoolInterface)interfaces.get( server );
      if( imc == null ) {
         imc = renewPoolInterface( server );
      }
      return imc;
   }


   /**
    * RenewInterface. Returns a renewed interface towards the host DB
    */

   private static imcode.server.IMCPoolInterface renewPoolInterface( String server ) {
      if( server == null ) {
         throw new IllegalArgumentException( "Server == null" );
      }

      IMCPoolInterface imc = (IMCPoolInterface)appServer.getServerObject( server );

      if( imc == null ) {
         throw new IllegalArgumentException( "Server '" + server + "' not found." );
      }

      interfaces.put( server, imc );
      return imc;
   }


   /**
    Flushes the interface cache, essentially resetting everything.
    */
   public static void flush() {
      interfaces = new Hashtable();
   }

}
