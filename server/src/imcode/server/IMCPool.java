package imcode.server;

import java.sql.*;
import java.sql.Date;
import java.io.*;
import java.util.*;

import imcode.server.*;
import imcode.server.user.UserDomainObject;
import imcode.server.db.DBConnect;
import imcode.server.db.sql.ConnectionPool;

/**
 Database connection pool for the Imcode Net Server
 */
public class IMCPool implements IMCPoolInterface {
   private final static String CVS_REV = "$Revision$";
   private final static String CVS_DATE = "$Date$";
   //	ConnectionPool m_conPool ;            // our pool of connections
   ConnectionPool m_conPool; // inet pool of connections

   /**
    Construct a pool object
    */
   public IMCPool( ConnectionPool conPool, Properties props ) {
      super();
      m_conPool = conPool;
   }

   /**
    Send a sqlQuery to the database and return a string array
    */
   public String[] sqlQuery( String sqlQuery ) {

      Vector data = new Vector();

      DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
      dbc.getConnection();
      dbc.createStatement();
      data = (Vector)dbc.executeQuery();

      if( data != null ) {
         String result[] = new String[data.size()];
         for( int i = 0; i < data.size(); i++ )
            result[i] = data.elementAt( i ).toString();

         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         return result;
      } else {
         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         return null;
      }
   }

   /**
    Send a sqlquery to the database and return a string array
    */
   public String[] sqlQuery( String sqlQuery, String catalog ) {

      Vector data = new Vector();

      DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
      dbc.getConnection();
      dbc.createStatement();
      data = (Vector)dbc.executeQuery( catalog );

      if( data != null ) {
         String result[] = new String[data.size()];
         for( int i = 0; i < data.size(); i++ )
            result[i] = data.elementAt( i ).toString();

         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         return result;
      } else {
         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         return null;
      }
   }


   /**
    Send a sqlquery to the database and return a string
    */
   public String sqlQueryStr( String sqlQuery ) {
      Vector data = new Vector();

      DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
      dbc.getConnection();
      dbc.createStatement();
      data = (Vector)dbc.executeQuery();

      dbc.clearResultSet();
      dbc.closeConnection();
      dbc = null;

      if( data != null )
         return data.elementAt( 0 ).toString();
      else
         return null;
   }


   /**
    Send a sql update query to the database
    */
   public void sqlUpdateQuery( String sqlStr ) {
      DBConnect dbc = new DBConnect( m_conPool, sqlStr );
      dbc.getConnection();
      dbc.createStatement();
      dbc.executeUpdateQuery();
      dbc.closeConnection();
      dbc = null;
   }


   /**
    Send a procedure to the database and return a string array
    */
   public String[] sqlProcedure( String procedure ) {

      Vector data = new Vector();

      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setProcedure( procedure );


      data = (Vector)dbc.executeProcedure();

      if( data != null ) {
         String result[] = new String[data.size()];
         for( int i = 0; i < data.size(); i++ )
            result[i] = data.elementAt( i ).toString();

         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         return result;
      } else {
         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         return null;

      }
   }


   /**
    Send a procedure to the database and return a string
    */
   public String sqlProcedureStr( String procedure ) {
      Vector data = new Vector();

      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setProcedure( procedure );
      data = (Vector)dbc.executeProcedure().clone();


      dbc.clearResultSet();
      dbc.closeConnection();
      dbc = null;

      if( data != null ) {
         if( data.size() > 0 ) {
            return data.elementAt( 0 ).toString();
         } else {
            return null;
         }
      } else {
         throw new java.lang.NullPointerException( "Null in IMCPool.sqlProcedureStr(String)" );
      }
   }


   /**
    Send a update procedure to the database
    */
   public void sqlUpdateProcedure( String procedure ) {
      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setProcedure( procedure );
      dbc.executeUpdateProcedure();
      dbc.closeConnection();
      dbc = null;
   }


   /**
    Parse doc replace variables with data
    */
   public String parseDoc( String htmlStr, java.util.Vector variables ) {
      int v_start;
      String temp_str1 = "";
      String temp_str2 = "";
      boolean all_not_found = true;


      for( int i = 0; i < variables.size(); i += 2 ) {
         v_start = htmlStr.indexOf( variables.elementAt( i ).toString() );
         while( v_start != -1 ) {

            temp_str1 = htmlStr.substring( 0, v_start ) + variables.elementAt( i + 1 ).toString();
            temp_str2 = htmlStr.substring( v_start + (variables.elementAt( i ).toString()).length(), htmlStr.length() );
            htmlStr = temp_str1 + temp_str2;
            v_start = htmlStr.indexOf( variables.elementAt( i ).toString() );
         }
      }

      return htmlStr;
   }


   /**
    Parse doc replace variables with data, uses two vectors
    */
   public String parseDoc( String htmlStr, java.util.Vector variables, java.util.Vector data ) {
      int v_start;
      String temp_str1 = "";
      String temp_str2 = "";


      for( int i = 0; i < variables.size(); i++ ) {
         v_start = htmlStr.indexOf( variables.elementAt( i ).toString() );
         while( v_start != -1 ) {
            temp_str1 = htmlStr.substring( 0, v_start ) + data.elementAt( i ).toString();
            temp_str2 = htmlStr.substring( v_start + (variables.elementAt( i ).toString()).length(), htmlStr.length() );
            htmlStr = temp_str1 + temp_str2;
            v_start = htmlStr.indexOf( variables.elementAt( i ).toString() );
         }
      }

      return htmlStr;
   }


   /**
    Send a sqlQuery to the database and return a string array
    Array[0]                 = number of field in the record
    Array[1]   - array[n]    = metadata
    Array[n+1] - array[size] = data
    */
   public String[] sqlQueryExt( String sqlQuery ) {

      Vector data = new Vector();
      String[] meta = new String[0];

      DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
      dbc.getConnection();
      dbc.createStatement();
      data = (Vector)dbc.executeQuery();
      meta = dbc.getMetaData();

      if( data.size() > 0 ) {
         String result[] = new String[data.size() + dbc.getColumnCount() + 1];

         // no of fields
         result[0] = dbc.getColumnCount() + "";

         // meta
         int i = 0;
         for( i = 0; i < dbc.getColumnCount(); i++ )
            result[i + 1] = meta[i];

         // data
         for( int j = 0; j < data.size(); j++ )
            result[j + i + 1] = data.elementAt( j ).toString();

         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         meta = null;
         return result;
      } else {
         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         meta = null;
         return null;
      }
   }

   /**
    Send a procedure to the database and return a string array
    Array[0]                 = number of field in the record
    Array[1]   - array[n]    = metadata
    Array[n+1] - array[size] = data
    */
   public String[] sqlProcedureExt( String procedure ) {

      Vector data = new Vector();
      String[] meta = new String[0];
      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setProcedure( procedure );


      data = (Vector)dbc.executeProcedure();
      meta = dbc.getMetaData();

      if( data != null && data.size() > 0 ) {


         String result[] = new String[data.size() + dbc.getColumnCount() + 1];

         // no of fields
         result[0] = dbc.getColumnCount() + "";

         // meta
         int i = 0;
         for( i = 0; i < dbc.getColumnCount(); i++ )
            result[i + 1] = meta[i];

         // data
         for( int j = 0; j < data.size(); j++ )
            result[j + i + 1] = data.elementAt( j ).toString();


         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         meta = null;
         return result;
      } else {
         dbc.clearResultSet();
         dbc.closeConnection();
         dbc = null;
         data = null;
         meta = null;
         return null;
      }


   }


   /**
    Send a sqlQuery to the database and return a Hastable
    */
   public Hashtable sqlQueryHash( String sqlQuery ) {

      Vector data = new Vector();
      String[] meta = new String[0];

      DBConnect dbc = new DBConnect( m_conPool, sqlQuery );
      dbc.getConnection();
      dbc.createStatement();
      data = (Vector)dbc.executeQuery().clone();

      meta = dbc.getMetaData();
      int columns = dbc.getColumnCount();

      Hashtable result = new Hashtable( columns, 0.5f );


      dbc.clearResultSet();
      dbc.closeConnection();


      if( data.size() > 0 ) {

         for( int i = 0; i < columns; i++ ) {
            String temp_str[] = new String[data.size() / columns];
            int counter = 0;

            for( int j = i; j < data.size(); j += columns )
               temp_str[counter++] = data.elementAt( j ).toString();
            ;

            result.put( meta[i], temp_str );
         }


         return result;
      } else {
         return new Hashtable( 1, 0.5f );
      }

   }


   /**
    Send a procedure to the database and return a Hashtable
    */
   public Hashtable sqlProcedureHash( String procedure ) {

      Vector data = new Vector();
      String[] meta = new String[0];

      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setProcedure( procedure );

      data = (Vector)dbc.executeProcedure().clone();
      meta = dbc.getMetaData();
      int columns = dbc.getColumnCount();


      Hashtable result = new Hashtable( columns, 0.5f );

      dbc.clearResultSet();
      dbc.closeConnection();


      if( data.size() > 0 ) {

         for( int i = 0; i < columns; i++ ) {
            String temp_str[] = new String[data.size() / columns];
            int counter = 0;


            for( int j = i; j < data.size(); j += columns )
               temp_str[counter++] = data.elementAt( j ).toString();


            result.put( meta[i], temp_str );
         }


         return result;
      } else {
         return new Hashtable( 1, 0.5f );
      }


   }


   /**
    checkDocAdminRights
    */
   public boolean checkDocAdminRights( int meta_id, UserDomainObject user ) {
      String sqlStr = "";
      // is user superadmin?
      sqlStr = "select role_id from users,user_roles_crossref\n";
      sqlStr += "where users.user_id = user_roles_crossref.user_id\n";
      sqlStr += "and user_roles_crossref.role_id = 0\n";
      sqlStr += "and users.user_id = " + user.getUserId();
      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setSQLString( sqlStr );
      dbc.createStatement();
      Vector super_admin_vec = (Vector)dbc.executeQuery().clone();
      dbc.clearResultSet();

      if( super_admin_vec.size() > 0 ) {
         dbc.closeConnection();
         dbc = null;
         return true;
      }

      // ROLES RIGHTS
      sqlStr = "select meta.meta_id from meta,roles_rights,user_roles_crossref ";
      sqlStr += "where meta.meta_id = " + meta_id;
      sqlStr += " and roles_rights.meta_id = meta.to_meta_id";
      sqlStr += " and roles_rights.permission_id = 3";
      sqlStr += " and roles_rights.role_id = user_roles_crossref.role_id";
      sqlStr += " and user_roles_crossref.user_id =" + user.getUserId();

      dbc.setSQLString( sqlStr );
      dbc.createStatement();
      Vector hasAdminRights = (Vector)dbc.executeQuery().clone();
      dbc.clearResultSet();
      dbc.closeConnection();
      dbc = null;

      if( hasAdminRights.size() > 0 ) {
         return true;
      } else {
         return false;
      }
   }


   /**
    Send a procedure to the database and return a multi string array
    */
   public String[][] sqlProcedureMulti( String procedure ) {
      Vector data = new Vector();

      DBConnect dbc = new DBConnect( m_conPool );
      dbc.getConnection();
      dbc.setProcedure( procedure );

      data = (Vector)dbc.executeProcedure().clone();
      int columns = dbc.getColumnCount();
      int rows = data.size() / columns;
      dbc.clearResultSet();
      dbc.closeConnection();


      String result[][] = new String[rows][columns];
      for( int i = 0; i < rows; i++ ) {
         for( int j = 0; j < columns; j++ ) {
            result[i][j] = data.elementAt( i * columns + j ).toString();
         }

      }


      return result;
   }
} // END CLASS IMCPool
