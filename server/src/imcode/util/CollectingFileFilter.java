package imcode.util;

import org.apache.commons.io.filefilter.IOFileFilter;

import java.util.Collection;
import java.io.File;

public class CollectingFileFilter implements IOFileFilter {

    private Collection collection;

    public CollectingFileFilter( Collection collection ) {
        this.collection = collection;
    }

    public boolean accept( File file ) {
        collection.add( file ) ;
        return true ;
    }

    public boolean accept( File file, String s ) {
        return accept(new File(file, s)) ;
    }
}
