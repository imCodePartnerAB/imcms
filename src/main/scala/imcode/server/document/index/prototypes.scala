//package imcode.server.document.index.prototypes
//
//import com.imcode._
//import scala.reflect.BeanProperty
//import scala.collection.JavaConverters._
//import com.imcode.imcms.mapping.DocumentMapper
//import com.imcode.Log4jLoggerSupport
//import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
//import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
//import org.apache.solr.core.CoreContainer
//import java.io.File
//import imcode.server.document.DocumentDomainObject
//import imcode.server.user.UserDomainObject
//import org.apache.solr.common.SolrInputDocument
//import scala.actors.{DaemonActor, Actor, TIMEOUT}
//import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
//import java.util.concurrent.atomic.AtomicReference
//import java.util.concurrent.{BlockingQueue, Callable, Executors, LinkedBlockingQueue, Future => JFuture}
//import java.util.concurrent.locks.ReentrantLock
//
//class SolrIndexServiceEventHandler {
//
//  var documentMapper: DocumentMapper = _
//  var documentIndexer: DocumentIndexer = _
//
//  // case class Rebuild(force: Boolean = false)
//  case object Rebuild
//  case class IndexDoc(doc: DocumentDomainObject)
//  case class IndexDocs(docId: Int)
//  case class RemoveDoc(doc: DocumentDomainObject)
//  case class RemoveDocs(docId: Int)
//
//  // todo: limit to ~1000
//  private val indexedDocs = new LinkedBlockingQueue[SolrInputDocument] // events - PUBLISHED, consumed by SOLr' writers
//  private val indexerExecutor = Executors.newFixedThreadPool(10)
//
//  object IndexedDocsWriter {
//    // while not interrupted
//
//    indexedDocs.drainTo(null, 10) |> { count =>
//      if (count > 0) {
//        //solrServer.add(null)
//        //solrServer.commit()
//      }
//
//      // onError
//      //   log
//      //   embedded: recreate data dir, force rebuild
//    }
//  }
//
//  type SolrTask[T] = SolrServer => T
//
//  // type SolrReadedTask[T] = SolrServer => T
//  // type SolrWriterTask[T] = SolrServer => T
//
//  // everything inside Actor, pass indexedDocs queue for exchange as constructor param?
//  private val eventDispatcher = new Actor with DaemonActor {
////    private val docRebuildService = Executors.newSingleThreadExecutor()
////    private val docRebuildTaskRef = new AtomicReference[JFuture[_]]
////
////    docRebuildTaskRef.get() match {
////      case future if future == null || future.isDone =>
////        docRebuildService.submit(new Runnable {
////          def run() {
////            //docIndex.rebuild()
////          }
////        }) |> docRebuildTaskRef.set
////
////      case _ => // ignore
////    }
//
//    def act() {
//      loop {
//        reactWithin(0) {
//          case Rebuild =>
//            // submit rebuild task
//            // shutdown indexers? remove all from indexed queue, start rebuild task, issue solr query - delete old?
//            indexerExecutor.shutdownNow()
//            indexedDocs.clear()
//
//          case TIMEOUT =>
//            react {
//              case Rebuild =>
//              case IndexDoc(doc) => submitIndexDoc(doc)
//              case IndexDocs(docId) => submitIndexDocs(docId)
//              case RemoveDoc(doc) => submitRemoveDoc(doc)
//              case RemoveDocs(docId) => submitRemoveDocs(docId)
//            }
//        }
//      }
//    }
//  }
//
//
//  def indexDocument(docId: Int) {
//    eventDispatcher ! IndexDocs(docId)
//  }
//
//  def indexDocument(document: DocumentDomainObject) {
//    eventDispatcher ! IndexDoc(document)
//  }
//
//  def removeDocument(docId: Int) {
//    eventDispatcher ! RemoveDocs(docId)
//  }
//
//  def removeDocument(document: DocumentDomainObject) {
//    eventDispatcher ! RemoveDoc(document)
//  }
//
//  def rebuild() {
//    eventDispatcher ! Rebuild
//  }
//
//
//  def submitIndexDocs(docId: Int) {
//    indexerExecutor.submit(new Runnable {
//      def run() {
//        for (language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala) {
//          submitIndexDoc(documentMapper.getDefaultDocument(docId, language))
//        }
//      }
//    })
//  }
//
//
//  def submitIndexDoc(doc: DocumentDomainObject) {
//    indexerExecutor.submit(new Runnable {
//      def run() {
//        try {
//          documentIndexer.index(doc) |> indexedDocs.put // | writeDoc; commit
//        } catch {
//          case e => throw new IndexException(e)
//        }
//      }
//    })
//  }
//
//  def submitRemoveDocs(docId: Int) {
//    indexerExecutor.submit(new Runnable {
//      def run() {
//        for (language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala) {
//           submitRemoveDoc(documentMapper.getDefaultDocument(docId, language))
//        }
//      }
//    })
//  }
//
//  def submitRemoveDoc(doc: DocumentDomainObject) {
//    indexerExecutor.submit(new Runnable {
//      def run() {
//        try {
//          //docsToRemove.add() ... solr doc id?
//        } catch {
//          case e => throw new IndexException(e)
//        }
//      }
//    })
//  }
//
//
//  // return task
//  def submitRebuild() {
//    indexerExecutor.submit(new Runnable {
//      def run() {
//        val languages = documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala
//        for {
//          docId <- documentMapper.getAllDocumentIds.asScala
//          language <- languages
//          doc <- Option(documentMapper.getDefaultDocument(docId, language))
//        } {
//          val solrIndexDoc = documentIndexer.index(doc)
//          val solrServer = getWriter()
//
//          solrServer.add(solrIndexDoc)
//          solrServer.commit()
//        }
//
//        // solrServer.delete all with ts < rebuildStartTime
//      }
//    })
//  }
//
//  def getWriter(): SolrServer = null
//}
//
//
//
//// ALSO TO SEPARATE imCMS exceptions from Solr
//// how to observer progress?
//// rebuild task .wait. outside?
//
//// SINGLE Thread Index Wirter
//// Multi Thread Index Writer
//trait DocIndexer {
//
//  @BeanProperty var documentMapper: DocumentMapper = _
//  @BeanProperty var documentIndexer: DocumentIndexer = _
//
////  val docBuildIndexTasksQueue = null
////  val docRemoveIndexQueryTasksQueue = null
//  val rebuildTaskRef = new AtomicReference[JFuture[_]]
//  val rebuildTaskExecutor = Executors.newSingleThreadExecutor()
//
//  val indexerExecutors = Executors.newFixedThreadPool(10)
//
////  def index(solrServer: SolrServer, docId: Int, callback: Any): JFuture[_]
////  def index(solrServer: SolrServer, doc: DocumentDomainObject, callback: Any): JFuture[_]
////  def remove(solrServer: SolrServer, docId: Int, callback: Any): JFuture[_]
////
////  def remove(solrServer: SolrServer, doc: DocumentDomainObject, callback: Any): JFuture[_]
////
////  //def next(): Option[SolrInputDocument]
////  //def next(n:Int): Seq[SolrInputDocument]
////
////  def rebuild(solrServer: SolrServer, callback: Any): JFuture[_]
//
//
//  def indexDocument(doc: DocumentDomainObject): JFuture[SolrInputDocument] =
//    indexerExecutors.submit(new Callable[SolrInputDocument] {
//      def call() {
//        documentIndexer.index(doc)
//      }
//    })
//
//  def indexDocument(docId: Int): JFuture[Seq[SolrInputDocument]] =
//    indexerExecutors.submit(new Callable[Seq[SolrInputDocument]] {
//      def call() = {
//        for {
//          language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala
//          doc <- Option(documentMapper.getDefaultDocument(docId, language))
//        } yield indexDocument(doc).get()
//      }
//    })
//
//
//  // new or existing Future?
//  def rebuild(solrServer: SolrServer): JFuture[_] =
//    indexerExecutors.submit(new Runnable {
//      def run() {
//        for {
//          docId <- documentMapper.getAllDocumentIds.asScala
//          solrInputDocument <- indexDocument(docId).get()
//        } {
//          //solrServer.add(solrIndexDoc)
//          //solrServer.commit()
//        }
//      }
//    })
//}
//
//
//
//
//
//trait SolrDocumentIndexService extends DocumentIndex {
//  @BeanProperty var documentMapper: DocumentMapper = _
//  @BeanProperty var documentIndexer: DocumentIndexer = _
//
//  private val rebuildTaskRef = new AtomicReference[JFuture[_]]
//
////  private val docsToIndex = new LinkedBlockingQueue[_]
////  private val docsToRemove = new LinkedBlockingQueue[_]
////  private val indexesToWrite = new LinkedBlockingQueue[SolrInputDocument]
//
//
//  def indexDocument(docId: Int) {
//    for (language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala) {
//      try {
//        indexDocument(documentMapper.getDefaultDocument(docId, language))
//      } catch {
//        case e: IndexException => throw e
//        case e => throw new IndexException(e)
//      }
//    }
//  }
//
//  def indexDocument(document: DocumentDomainObject) {
//    try {
//        documentIndexer.index(document) |> { di =>
//          //solrServer.add(di)
//          //solrServer.commit()
//        }
//    } catch {
//      case e => throw new IndexException(e)
//    }
//  }
//
//  def removeDocument(docId: Int) {
//    for (language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala) {
//      try {
//        removeDocument(documentMapper.getDefaultDocument(docId, language))
//      } catch {
//        case e: IndexException => throw e
//        case e => throw new IndexException(e)
//      }
//    }
//  }
//
//  def removeDocument(document: DocumentDomainObject) {
//    try {
//        //solrServer.deleteByQuery("meta_id:" + document.getId)
//        //solrServer.commit()
//    } catch {
//      case e => throw new IndexException(e)
//    }
//  }
//
//  def rebuild() {
//    for (docId <- documentMapper.getAllDocumentIds.asScala) {
//      indexDocument(docId)
//    }
//  }
//
//  def shutdown() //shutdown solr, background indexing
//}
//
//
//
//
//
//
//
//
//class EmbeddedSolrDocumentIndexService(solrHome: File) extends SolrDocumentIndexService {
//  private val solrServerReaderRef = new AtomicReference(Option.empty[EmbeddedSolrServer])
//  private val solrServerWriterRef = new AtomicReference(Option.empty[EmbeddedSolrServer])
//
//  private var restartFuture: JFuture[_] = null
//  private var reindexFuture: JFuture[_] = null
//  private val restartExecutor = Executors.newSingleThreadExecutor()
//  private val reindexExecutor = Executors.newSingleThreadExecutor()
//
//
//  // case class Rebuild(force: Boolean = false)
//  case object Rebuild
//  case class IndexDoc(doc: DocumentDomainObject)
//  case class IndexDocs(docId: Int)
//  case class RemoveDoc(doc: DocumentDomainObject)
//  case class RemoveDocs(docId: Int)
//
//  private val eventDispatcher = new Actor with DaemonActor {
//    private val docRebuildService = Executors.newSingleThreadExecutor()
//    private val docRebuildTaskRef = new AtomicReference[JFuture[_]]
//
//    def act() {
//      loop {
//        reactWithin(0) {
//          case Rebuild =>
//            docRebuildTaskRef.get() match {
//              case future if future == null || future.isDone =>
//                docRebuildService.submit(new Runnable {
//                  def run() {
//                    try {
//
//                    } catch {
//                      case e => // eventDispatcher ! Rebuild
//                    }
//                  }
//                }) |> docRebuildTaskRef.set
//
//              case _ => // ignore
//            }
//
//            // submit rebuild task
//            // shutdown indexers? remove all from indexed queue, start rebuild task, issue solr query - delete old?
//            //indexerExecutor.shutdownNow()
//            //indexedDocs.clear()
//
//          case TIMEOUT =>
//            react {
//              case Rebuild =>
//              case IndexDoc(doc) => submitIndexDoc(doc)
//              case IndexDocs(docId) => submitIndexDocs(docId)
//              case RemoveDoc(doc) => submitRemoveDoc(doc)
//              case RemoveDocs(docId) => submitRemoveDocs(docId)
//            }
//        }
//      }
//    }
//  }
//
//
//
//  def indexDocument(docId: Int) {
//    eventDispatcher ! IndexDocs(docId)
//  }
//
//  def indexDocument(document: DocumentDomainObject) {
//    eventDispatcher ! IndexDoc(document)
//  }
//
//  def removeDocument(docId: Int) {
//    eventDispatcher ! RemoveDocs(docId)
//  }
//
//  def removeDocument(document: DocumentDomainObject) {
//    eventDispatcher ! RemoveDoc(document)
//  }
//
//  def rebuild() {
//    eventDispatcher ! Rebuild
//  }
//
//  private def submitRestart(): Unit = synchronized {
//    if (restartFuture == null || restartFuture.isDone) {
//      restartFuture = restartExecutor.submit(new Runnable {
//        def run() {
//          shutdownReaderAndWriter()
//
//          solrServerReaderRef.set(Some(SolrServerFactory.createEmbeddedSolrServer(solrHome, recreateDataDir = true)))
//          solrServerWriterRef.set(Some(SolrServerFactory.createEmbeddedSolrServer(solrHome, recreateDataDir = false)))
//
//          submitReindex()
//        }
//      })
//    }
//  }
//
//  // merge with restart
//  private def submitReindex(): Unit = synchronized {
//    if (reindexFuture == null || reindexFuture.isDone) {
//      reindexFuture = reindexExecutor.submit(new Runnable {
//        def run() {
//          // pause processing index/remove
//          // finally - continue processing index/remove
//        }
//      })
//    }
//  }
//
//
//  private def shutdownReaderAndWriter() {
//    Seq(solrServerReaderRef, solrServerWriterRef).flatMap(_.getAndSet(None).toSeq).foreach { solrServer =>
//      try {
//        solrServer.shutdown()
//      } catch {
//        case e => // log error shutdown embedded solr server
//      }
//    }
//  }
//
//
//  def start() {
//    submitRestart()
//  }
//
//  def shutdown() {
//    // stop tasks, stop listening
//    shutdownReaderAndWriter()
//  }
//
//  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = solrServerReaderRef.get() match {
//    case None => java.util.Collections.emptyList()
//    case Some(solrServer) =>
//      try {
//        //solrServer.query(null)
//        java.util.Collections.emptyList()
//      } catch {
//        case e =>
//          submitRestart() // rebuild?
//          java.util.Collections.emptyList()
//      }
//  }
//
//  def withReader(f: SolrServer => Any) {
//    try {
//      solrServerReaderRef.get().foreach(f)
//    } catch {
//      case e => submitRestart()
//    }
//  }
//
//  def withWriter(f: SolrServer => Any) {
//    try {
//      solrServerWriterRef.get().foreach(f)
//    } catch {
//      case e => submitRestart()
//    }
//  }
//}
//
//
//
//
//
//
//
//
//
//
//
//class HttpSolrDocumentIndexService(solrUrl: String) extends SolrDocumentIndexService {
//  private val solrServerReader = SolrServerFactory.createHttpSolrServer(solrUrl)
//  private val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrUrl)
//
//  private val indexDocExecutor = Executors.newSingleThreadExecutor()
//  private val removeDocExecutor = Executors.newSingleThreadExecutor()
//
//  val indexDocRequests = new LinkedBlockingQueue[Runnable](1000)
//  val removeDocRequests = new LinkedBlockingQueue[Runnable](1000)
//
//  @volatile
//  private var writingIsPaused = false
//  private val writingLock = new Object
//
//  case object Rebuild
//  case class IndexDoc(doc: DocumentDomainObject)
//  case class IndexDocs(docId: Int)
//  case class RemoveDoc(doc: DocumentDomainObject)
//  case class RemoveDocs(docId: Int)
//
//  private val eventDispatcher = new Actor with DaemonActor {
//    // can cancel outside? via msg?
//    val docRebuildTaskRef = new AtomicReference[JFuture[_]]
//    val docRebuildService = Executors.newSingleThreadExecutor()
//
//    def act() {
//      loop {
//        reactWithin(0) {
//          case 'Add => submitAddDocToIndex()
//          case 'Del =>
//          case 'Rebuild =>
//            docRebuildTaskRef.get() match {
//              case future if future == null || future.isDone =>
//                docRebuildService.submit(new Runnable {
//                  def run() {
//                    writingIsPaused = true
//
//                    try {
//                      indexDocRequests.clear()
//                      removeDocRequests.clear()
//
//                      // await addAoc
//                      // await delDoc
//
//                    } catch {
//                      case e => // eventDispatcher ! Rebuild
//                    } finally {
//                      writingIsPaused = false
//                      writingLock.synchronized {
//                        writingLock.notify()
//                      }
//                    }
//                  }
//                }) |> docRebuildTaskRef.set
//
//              case _ => // ignore
//            }
//        }
//      }
//    }
//  }
//
//  def submitAddDocToIndex() {
//    indexDocRequests.offer(new Runnable() {
//      def run() {
//
//      }
//    }) |> {
//      case false =>
//      case _ =>
//    }
//  }
//
//
//
//
//
//
//
//
//
//  def indexDocument(doc: DocumentDomainObject): JFuture[SolrInputDocument] =
//    indexerExecutors.submit(new Callable[SolrInputDocument] {
//      def call() {
//        documentIndexer.index(doc)
//      }
//    })
//
//  def indexDocument(docId: Int): JFuture[Seq[SolrInputDocument]] =
//    indexerExecutors.submit(new Callable[Seq[SolrInputDocument]] {
//      def call() = {
//        for {
//          language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala
//          doc <- Option(documentMapper.getDefaultDocument(docId, language))
//        } yield indexDocument(doc).get()
//      }
//    })
//
//
//  // new or existing Future?
//  def rebuild(solrServer: SolrServer): JFuture[_] =
//    indexerExecutors.submit(new Runnable {
//      def run() {
//        for {
//          docId <- documentMapper.getAllDocumentIds.asScala
//          solrInputDocument <- indexDocument(docId).get()
//        } {
//          //solrServer.add(solrIndexDoc)
//          //solrServer.commit()
//        }
//      }
//    })
//
//
//
//  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
//    try {
//      val queryResponse = solrServerReader.query(new SolrQuery(query.getQuery.toString))
//
//      java.util.Collections.emptyList()
//    } catch {
//      case e =>
//        // log error
//        java.util.Collections.emptyList()
//      }
//  }
//
//  def shutdown() {
//    solrServerReader.shutdown()
//    solrServerWriter.shutdown()
//  }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//object SolrServerFactory extends Log4jLoggerSupport {
//
//  def createHttpSolrServer(solrUrl: String) = new HttpSolrServer(solrUrl) with SolrServerShutdown |>> { solr =>
//    solr.setRequestWriter(new BinaryRequestWriter())
//  }
//
//
//  def createEmbeddedSolrServer(solrHome: File, recreateDataDir: Boolean = false): EmbeddedSolrServer with SolrServerShutdown = {
//    if (recreateDataDir) {
//      new File(solrHome, "core/data") |> { dataDir =>
//        if (dataDir.exists() && !dataDir.delete()) sys.error("Unable to delete SOLr data dir %s.".format(dataDir))
//      }
//    }
//
//    new CoreContainer(solrHome.getAbsolutePath, new File(solrHome, "solr.xml")) |> { coreContainer =>
//      new EmbeddedSolrServer(coreContainer, "core") with SolrServerShutdown
//    }
//  }
//}
//
//
//trait SolrServerShutdown { this: SolrServer { def shutdown() } =>
//}