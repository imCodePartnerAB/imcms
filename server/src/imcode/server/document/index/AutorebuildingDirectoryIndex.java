package imcode.server.document.index;

import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.DateConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AutorebuildingDirectoryIndex implements DocumentIndex {

    private int indexingSchedulePeriodInMilliseconds ;

    private final static Logger log = Logger.getLogger( AutorebuildingDirectoryIndex.class.getName() );

    private DirectoryIndex index;
    private Set documentsToAddToNewIndex = Collections.synchronizedSet( new HashSet() );
    private Set documentsToRemoveFromNewIndex = Collections.synchronizedSet( new HashSet() );
    private final Object newIndexBuildingLock = new Object();
    private boolean buildingNewIndex;

    private static final String INDEX_SEGMENTS_FILE_NAME = "segments";

    static {
        // FIXME: Set to something lower, like imcmsDocumentCount to prevent slow queries?
        BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
    }

    public AutorebuildingDirectoryIndex( File indexDirectory, int indexingSchedulePeriodInMinutes ) {
        this.indexingSchedulePeriodInMilliseconds = indexingSchedulePeriodInMinutes * DateUtils.MILLIS_IN_MINUTE ;
        this.index = new DirectoryIndex( indexDirectory );
        Timer scheduledIndexBuildingTimer = new Timer( true );
        long scheduledIndexDelay = 0 ;
        if ( IndexReader.indexExists( indexDirectory ) ) {
            try {
                long indexModifiedTime = IndexReader.lastModified(indexDirectory);
                Date nextExecutionTime = new Date( indexModifiedTime + indexingSchedulePeriodInMilliseconds );
                log.info( "First indexing scheduled at " + formatDatetime( nextExecutionTime ) );
                scheduledIndexDelay = nextExecutionTime.getTime() - System.currentTimeMillis();
            } catch ( IOException e ) {
                log.warn("Failed to get last modified time of index.", e) ;
            }
        }
        scheduledIndexBuildingTimer.scheduleAtFixedRate( new ScheduledIndexingTimerTask(), scheduledIndexDelay, indexingSchedulePeriodInMilliseconds );
    }

    private String formatDatetime( Date nextExecutionTime ) {
        return new SimpleDateFormat( DateConstants.DATETIME_FORMAT_STRING ).format( nextExecutionTime );
    }

    public synchronized void indexDocument( DocumentDomainObject document ) {
        if ( buildingNewIndex ) {
            documentsToAddToNewIndex.add( document );
        }
        try {
            index.indexDocument( document );
        } catch ( IndexException e ) {
            log.error( "Failed to index document " + document.getId() + ". Reindexing..." );
            buildNewIndexInBackground();
        }
    }

    public synchronized void removeDocument( DocumentDomainObject document ) {
        if ( buildingNewIndex ) {
            documentsToRemoveFromNewIndex.add( document );
        }
        try {
            index.removeDocument( document );
        } catch ( IndexException e ) {
            log.error( "Failed to remove document " + document.getId() + " from index. Reindexing..." );
            buildNewIndexInBackground();
        }
    }

    public synchronized DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IndexException {
        log.debug( "search - called" );
        try {
            DocumentDomainObject[] documents = index.search( query, searchingUser );
            if (index.isInconsistent()) {
                buildNewIndexInBackground();
            }
            return documents;
        } catch ( IndexException ex ) {
            log.warn( "Search failed", ex );
            buildNewIndexInBackground();
            throw ex;
        }
    }

    private void buildNewIndexInBackground() {
        Thread indexBuildingThread = new Thread( "Background indexing thread" ) {
            public void run() {
                buildNewIndex();
            }
        };
        int callersPriority = Thread.currentThread().getPriority();
        int newPriority = Math.max( callersPriority - 1, Thread.MIN_PRIORITY );
        indexBuildingThread.setPriority( newPriority );

        indexBuildingThread.setDaemon( true );
        indexBuildingThread.start();
    }

    private void buildNewIndex() {
        NDC.push("buildNewIndex");
        try {
            File indexDirectoryFile = this.index.getDirectory();
            File parentFile = indexDirectoryFile.getParentFile();
            String name = indexDirectoryFile.getName();
            buildNewIndex( parentFile, name );
        } catch ( IOException e ) {
            log.fatal( "Failed to index all documents.", e );
        } finally {
            NDC.pop();
        }
    }

    private void buildNewIndex( File parentFile, String name ) throws IOException {
        if ( buildingNewIndex ) {
            log.debug("Ignoring request to build new index. Already in progress.") ;
            return;
        }
        synchronized ( newIndexBuildingLock ) {
            buildingNewIndex = true;
            File newIndexDirectoryFile = new File( parentFile, name + ".new" );
            DirectoryIndex newIndexDirectory = new DirectoryIndex( newIndexDirectoryFile );
            newIndexDirectory.indexAllDocuments();
            replaceIndexWithNewIndex( newIndexDirectory );
            buildingNewIndex = false;
        }
        considerDocumentsForNewIndex();
    }

    private synchronized void considerDocumentsForNewIndex() throws IndexException {
        for ( Iterator iterator = documentsToAddToNewIndex.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject)iterator.next();
            index.indexDocument( document );
            iterator.remove();
        }
        for ( Iterator iterator = documentsToRemoveFromNewIndex.iterator(); iterator.hasNext(); ) {
            DocumentDomainObject document = (DocumentDomainObject)iterator.next();
            index.removeDocument( document );
            iterator.remove();
        }
    }

    private synchronized void replaceIndexWithNewIndex( DirectoryIndex newIndex ) throws IOException {
        File indexDirectory = index.getDirectory();
        File oldIndex = new File( indexDirectory.getParentFile(), indexDirectory.getName()
                                                                  + ".old" );
        if ( oldIndex.exists() ) {
            FileUtils.forceDelete( oldIndex );
        }
        if ( indexDirectory.exists() && !indexDirectory.renameTo( oldIndex ) ) {
            log.error( "Failed to rename \"" + indexDirectory + "\" to \"" + oldIndex + "\"." );
        }
        File newIndexDirectory = newIndex.getDirectory();
        if ( !newIndexDirectory.renameTo( indexDirectory ) ) {
            throw new IOException( "Failed to rename \"" + newIndexDirectory + "\" to \""
                                   + indexDirectory
                                   + "\"." );
        }
        FileUtils.deleteDirectory( oldIndex );
    }

    private class ScheduledIndexingTimerTask extends TimerTask {

        public void run() {
            Date nextExecutionTime = new Date( this.scheduledExecutionTime() + indexingSchedulePeriodInMilliseconds );
            log.info( "Starting scheduled indexing. Next indexing at " + formatDatetime( nextExecutionTime ) );
            buildNewIndexInBackground();
        }
    }

}
