/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-27
 * Time: 16:03:00
 */
package imcode.util;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.fileupload.*;
import org.apache.oro.text.perl.Perl5Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

public class MultipartHttpServletRequest extends HttpServletRequestWrapper {
    private static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    MultiMap fileItemMap;

    public MultipartHttpServletRequest( HttpServletRequest request ) throws IOException {
        super( request );
        if ( FileUploadBase.isMultipartContent( request ) ) {
            FileUpload fileUpload = new FileUpload( new DefaultFileItemFactory() );
            List fileItems = null;
            try {
                fileItems = fileUpload.parseRequest( request );
            } catch ( FileUploadException e ) {
                throw new IOException( e.getMessage() );
            }
            this.fileItemMap = new MultiHashMap();
            for ( Iterator iterator = fileItems.iterator(); iterator.hasNext(); ) {
                FileItem fileItem = new BaseNameFileItem( (FileItem)iterator.next() );
                this.fileItemMap.put( fileItem.getFieldName(), fileItem );
            }
        }
    }

    public String getParameter( String key ) {
        String[] parameterValues = getParameterValues( key );
        if ( null != parameterValues && parameterValues.length > 0 ) {
            return parameterValues[0];
        }
        return null;
    }

    public FileItem getParameterFileItem( String key ) {
        FileItem[] parameterValues = getParameterFileItems( key );
        if ( null != parameterValues && parameterValues.length > 0 ) {
            return parameterValues[0];
        }
        return null;
    }

    public FileItem[] getParameterFileItems( String key ) {
        if ( null == fileItemMap ) {
            return null;
        }
        final Collection parameterFileItems = (Collection)fileItemMap.get( key );
        if ( null == parameterFileItems ) {
            return null;
        }
        return (FileItem[])parameterFileItems.toArray( new FileItem[parameterFileItems.size()] );
    }

    public Map getParameterMap() {
        Map map = new HashMap( super.getParameterMap() );
        Set fileItemKeys = fileItemMap.keySet();
        for ( Iterator iterator = fileItemKeys.iterator(); iterator.hasNext(); ) {
            String key = (String)iterator.next();
            map.put( key, getParameterValues( key ) );
        }
        return map;
    }

    public Enumeration getParameterNames() {
        Enumeration superParameterNames = super.getParameterNames();
        Set parameterNames = new HashSet();
        while ( superParameterNames.hasMoreElements() ) {
            parameterNames.add( superParameterNames.nextElement() );
        }
        parameterNames.addAll( fileItemMap.keySet() );
        return new IteratorEnumeration( parameterNames.iterator() );
    }

    public String[] getParameterValues( String key ) {
        if ( null != fileItemMap ) {
            List parameterValues = new ArrayList();
            Collection fileItems = (Collection)fileItemMap.get( key );
            if ( null == fileItems ) {
                return null;
            }
            for ( Iterator iterator = fileItems.iterator(); iterator.hasNext(); ) {
                FileItem fileItem = (FileItem)iterator.next();
                String contentType = fileItem.getContentType();
                parameterValues.add( getStringFromBytesWithContentType( fileItem.get(), contentType ) );
            }
            return (String[])parameterValues.toArray( new String[parameterValues.size()] );
        }
        return super.getParameterValues( key );
    }

    private String getStringFromBytesWithContentType( byte[] bytes, String contentType ) {
        String charset = CHARSET_ISO_8859_1;
        if ( null != contentType ) {
            Perl5Util perl5Util = new Perl5Util();
            perl5Util.match( "charset=\"?(\\S+?)\"?", contentType );
            String contentTypeCharset = perl5Util.group( 1 );
            try {
                return new String( bytes, contentTypeCharset );
            } catch ( UnsupportedEncodingException uee ) {
            }
        }
        try {
            return new String( bytes, charset );
        } catch ( UnsupportedEncodingException never ) {
            return null;
        }
    }

    private class BaseNameFileItem extends FileItemWrapper {

        public BaseNameFileItem( FileItem fileItem ) {
            super( fileItem );
        }

        public String getName() {
            String filename = fileItem.getName();
            if ( null != filename ) {
                filename = filename.substring( filename.lastIndexOf( '/' ) + 1 );
                filename = filename.substring( filename.lastIndexOf( '\\' ) + 1 );
            }
            return filename;
        }
    }

    private class FileItemWrapper implements FileItem {

        protected FileItem fileItem;

        public FileItemWrapper( FileItem fileItem ) {
            this.fileItem = fileItem;
        }

        public String getContentType() {
            return fileItem.getContentType();
        }

        public String getFieldName() {
            return fileItem.getFieldName();
        }

        public void setFieldName( String s ) {
            fileItem.setFieldName( s );
        }

        public InputStream getInputStream() throws IOException {
            return fileItem.getInputStream();
        }

        public String getName() {
            return fileItem.getName();
        }

        public OutputStream getOutputStream() throws IOException {
            return fileItem.getOutputStream();
        }

        public long getSize() {
            return fileItem.getSize();
        }

        public String getString() {
            return fileItem.getString();
        }

        public boolean isFormField() {
            return fileItem.isFormField();
        }

        public void setFormField( boolean b ) {
            fileItem.setFormField( b );
        }

        public boolean isInMemory() {
            return fileItem.isInMemory();
        }

        public byte[] get() {
            return fileItem.get();
        }

        public void delete() {
            fileItem.delete();
        }

        public String getString( String s ) throws UnsupportedEncodingException {
            return fileItem.getString( s );
        }

        public void write( File file ) throws Exception {
            fileItem.write( file );
        }
    }

}