package imcode.util;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import imcode.server.*;
import imcode.server.user.UserDomainObject;
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

    public static IMCPoolInterface getChatIMCPoolInterface( HttpServletRequest req ) throws IOException {
       // FIXME: Should return some useful database interface for the plugins
       return null ;
   }

   public static IMCPoolInterface getConfIMCPoolInterface( HttpServletRequest req ) throws IOException {
       // FIXME: Should return some useful database interface for the plugins
       return null ;
   }

   public static IMCPoolInterface getBillboardIMCPoolInterface( HttpServletRequest req ) throws IOException {
       // FIXME: Should return some useful database interface for the plugins
       return null ;
   }

   public static IMCPoolInterface getCalendarIMCPoolInterface( HttpServletRequest req ) throws IOException {
       // FIXME: Should return some useful database interface for the plugins
       return null ;
   }

}
