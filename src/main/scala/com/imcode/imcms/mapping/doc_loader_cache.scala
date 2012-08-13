package com.imcode
package imcms
package mapping

import _root_.imcode.server.document.DocumentDomainObject
import _root_.net.sf.ehcache.config.CacheConfiguration
import _root_.net.sf.ehcache.{Ehcache, CacheManager, Element, Cache}
import scala.collection.JavaConverters._
import com.imcode.imcms.api.{DocumentVersionInfo, Meta, I18nLanguage}


class DocLoaderCachingProxy(docLoader: DocumentLoader, languages: JList[I18nLanguage], size: Int) {

  val cacheManager = CacheManager.create()

  case class DocCacheKey(docId: DocId, languageId: LanguageId)

  def cacheConfiguration(name: String) = new CacheConfiguration |>> { cc =>
    cc.setMaxEntriesLocalHeap(size)
    cc.setOverflowToDisk(false)
    cc.setEternal(true)
    cc.setName(classOf[DocLoaderCachingProxy].getCanonicalName + "." + name)
  }

  val metas = CacheWrapper[DocId, Meta](cacheConfiguration("meats"))
  val versionInfos = CacheWrapper[DocId, DocumentVersionInfo](cacheConfiguration("versionInfos"))
  val workingDocs = CacheWrapper[DocCacheKey, DocumentDomainObject](cacheConfiguration("workingDocs"))
  val defaultDocs = CacheWrapper[DocCacheKey, DocumentDomainObject](cacheConfiguration("defaultDocs"))
  val aliasesToIds = CacheWrapper[String, DocId](cacheConfiguration("aliasesToIds"))
  val idsToAliases = CacheWrapper[DocId, String](cacheConfiguration("idsToAliases"))

  for (CacheWrapper(cache) <- Seq(metas, versionInfos, workingDocs, defaultDocs, aliasesToIds, idsToAliases)) {
    cacheManager.addCache(cache)
  }

  /**
   * @return doc's meta or null if doc does not exists
   */
  def getMeta(docId: DocId) = metas.getOrPut(docId) { docLoader.loadMeta(docId) }

  /**
   * @return doc's version info or null if doc does not exists
   */
  def getDocVersionInfo(docId: DocId) = versionInfos.getOrPut(docId) {
    docLoader.getDocumentVersionDao.getAllVersions(docId) match {
      case versions if versions.size == 0 => null
      case versions =>
        val workingVersion = versions.get(0)
        val defaultVersion = docLoader.getDocumentVersionDao.getDefaultVersion(docId)
        new DocumentVersionInfo(docId, versions, workingVersion, defaultVersion)
    }
  }

  /**
   * @return doc's id or null if doc does not exists or alias is not set
   */
  def getDocId(docAlias: String): DocId = aliasesToIds.getOrPut(docAlias) {
    docLoader.getMetaDao.getDocumentIdByAlias(docAlias) |>> {
      case null =>
      case docId => idsToAliases.put(docId, docAlias)
    }
  }

  /**
   * @return working doc or null if doc does not exists
   */
  def getWorkingDoc(docId: DocId, language: I18nLanguage): DocumentDomainObject =
    workingDocs.getOrPut(DocCacheKey(docId, language.getId)) {
      getMeta(docId) match {
        case null => null
        case meta =>
          val versionInfo = getDocVersionInfo(docId)
          val version = versionInfo.getWorkingVersion

          docLoader.loadAndInitDocument(meta.clone, version.clone, language)
      }
    }

  /**
   * @return default doc or null if doc does not exists
   */
  def getDefaultDoc(docId: DocId, language: I18nLanguage): DocumentDomainObject =
    defaultDocs.getOrPut(DocCacheKey(docId, language.getId)) {
      getMeta(docId) match {
        case null => null
        case meta =>
          val versionInfo = getDocVersionInfo(docId)
          val version = versionInfo.getDefaultVersion

          docLoader.loadAndInitDocument(meta.clone, version.clone, language)
      }
    }

  /**
   * @return custom doc or null if doc does not exists
   */
  def getCustomDoc(docId: DocId, docVersionNo: Int, language: I18nLanguage) = {
    getMeta(docId) match {
      case null => null
      case meta =>
        val versionInfo = getDocVersionInfo(docId)
        val version = versionInfo.getVersion(docVersionNo)

        docLoader.loadAndInitDocument(meta.clone, version.clone, language)
    }
  }

  def removeDocFromCache(docId: DocId) {
    metas.remove(docId)
    versionInfos.remove(docId)

    for {
      language <- languages.asScala
      key = DocCacheKey(docId, language.getId)
    } {
      workingDocs.remove(key)
      defaultDocs.remove(key)
    }

    for (alias: String <- Option(idsToAliases.get(docId))) {
      idsToAliases.remove(docId)
      aliasesToIds.remove(alias)
    }
  }
}


case class CacheWrapper[K >: Null, V >: Null](cache: Ehcache) {

  // Compiles, but Intellij can't infer
  // Option(cache.get(key)).map(_.getObjectValue).orNull.asInstanceOf[V]
  def get(key: K): V =
    cache.get(key) |> {
      case null => null
      case element => element.getObjectValue
    } |> { _.asInstanceOf[V] }

  def put(key: K, value: V): Unit = cache.put(new Element(key, value))

  def remove(key: K): Boolean = cache.remove(key)

  def getOrPut(key: K)(compute: => V): V =
    get(key) match {
      case value if value != null => value
      case _ => compute |>> { value => put(key, value) }
    }
}


object CacheWrapper {
  def apply[K >: Null, V >: Null](cacheConfiguration: CacheConfiguration) =
    new CacheWrapper[K, V](new Cache(cacheConfiguration))
}