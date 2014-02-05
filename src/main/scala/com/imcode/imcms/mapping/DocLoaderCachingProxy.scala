package com.imcode
package imcms
package mapping

import _root_.imcode.server.document.DocumentDomainObject
import _root_.net.sf.ehcache.config.CacheConfiguration
import _root_.net.sf.ehcache.CacheManager
import com.imcode.imcms.mapping.orm.{DocLanguage, Meta, I18nDocRef}
import scala.collection.JavaConverters._
import com.imcode.imcms.api._


class DocLoaderCachingProxy(docLoader: DocumentLoader, languages: JList[DocLanguage], size: Int) {

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
  def getWorkingDoc[A <: DocumentDomainObject](docId: DocId, language: DocLanguage): A =
    workingDocs.getOrPut(DocCacheKey(docId, language.getId)) {
      getMeta(docId) match {
        case null => null
        case meta =>
          val versionInfo = getDocVersionInfo(docId)
          val version = versionInfo.getWorkingVersion

          docLoader.loadAndInitDocument(meta.clone, version.clone, language)
      }
    }.asInstanceOf[A]

  /**
   * @return default doc or null if doc does not exists
   */
  def getDefaultDoc[A <: DocumentDomainObject](docId: DocId, language: DocLanguage): A =
    defaultDocs.getOrPut(DocCacheKey(docId, language.getId)) {
      getMeta(docId) match {
        case null => null
        case meta =>
          val versionInfo = getDocVersionInfo(docId)
          val version = versionInfo.getDefaultVersion

          docLoader.loadAndInitDocument(meta.clone, version.clone, language)
      }
    }.asInstanceOf[A]

  /**
   * @return custom doc or null if doc does not exists
   */
  def getCustomDoc[A <: DocumentDomainObject](ref: I18nDocRef): A = {
    getMeta(ref.docId) match {
      case null => null.asInstanceOf[A]
      case meta =>
        val versionInfo = getDocVersionInfo(ref.docId)
        val version = versionInfo.getVersion(ref.versionNo)

        docLoader.loadAndInitDocument(meta.clone, version.clone, ref.getLanguage)
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

    for (alias: String <- idsToAliases.get(docId).asOption) {
      idsToAliases.remove(docId)
      aliasesToIds.remove(alias)
    }
  }
}


