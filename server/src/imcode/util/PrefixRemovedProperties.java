package imcode.util;

import java.util.*;

public class PrefixRemovedProperties extends Properties {

   public PrefixRemovedProperties( Properties p, String prefix ) {

      // Loop over the key-value-pairs of p,
      // removing the prefix from each key and
      // putting the pair in this Properties.
      Enumeration names = p.propertyNames();
      while( names.hasMoreElements() ) {
         String name = (String)names.nextElement();
         if( name.startsWith( prefix ) ) {
            String value = p.getProperty( name );
            this.setProperty( name.substring( prefix.length() ), value );
         }
         ;
      }
   }
}
