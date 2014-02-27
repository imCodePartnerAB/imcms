package com.imcode
package imcms
package mapping

import _root_.imcode.server.document.DocumentDomainObject
import _root_.net.sf.ehcache.config.CacheConfiguration
import _root_.net.sf.ehcache.CacheManager

import com.imcode.imcms.mapping.container.DocRef
import scala.collection.JavaConverters._
import com.imcode.imcms.api._


class DocLoaderCachingProxy(docLoader: DocumentLoader, docLanguageSupport: DocumentLanguageSupport, size: Int) {

  val cacheManager = CacheManager.create()

  case class DocCacheKey(docId: DocId, languageCode: String)

  def cacheConfiguration(name: String) = new CacheConfiguration |>> { cc =>
    cc.setMaxEntriesLocalHeap(size)
    cc.setOverflowToDisk(false)
    cc.setEternal(true)
    cc.setName(classOf[DocLoaderCachingProxy].getCanonicalName + "." + name)
  }

  val metas = CacheWrapper[DocId, MetaVO](cacheConfiguration("meats"))
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
  def getMeta(docId: DocId): MetaVO = metas.getOrPut(docId) { docLoader.loadMeta(docId) }

  /**
   * @return doc's version info or null if doc does not exists
   */
  def getDocVersionInfo(docId: DocId): DocumentVersionInfo = versionInfos.getOrPut(docId) {
    docLoader.getDocVersionRepository.findByDocId(docId) match {
      case versions if versions.size == 0 => null
      case versions =>
        val workingVersion = versions.get(0)
        val defaultVersion = docLoader.getDocVersionRepository.findDefault(docId)
        new DocumentVersionInfo(docId, versions.asScala.map(EntityConverter.toApi).asJava, EntityConverter.toApi(workingVersion),
          EntityConverter.toApi(defaultVersion))
    }
  }

  /**
   * @return doc's id or null if doc does not exists or alias is not set
   */
  def getDocId(docAlias: String): DocId = aliasesToIds.getOrPut(docAlias) {
    docLoader.getDocRepository.getDocIdByAlias(docAlias) |>> {
      case null =>
      case docId => idsToAliases.put(docId, docAlias)
    }
  }

  /**
   * @return working doc or null if doc does not exists
   */
  def getWorkingDoc[A <: DocumentDomainObject](docId: DocId, docLanguageCode: String): A =
    workingDocs.getOrPut(DocCacheKey(docId, docLanguageCode)) {
      getMeta(docId) match {
        case null => null
        case meta =>
          val versionInfo = getDocVersionInfo(docId)
          val version = versionInfo.getWorkingVersion
          val doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType).asInstanceOf[DocumentDomainObject]

          doc.setMeta(meta.clone())
          doc.setVersionNo(version.getNo)
          doc.setLanguage(docLanguageSupport.getByCode(docLanguageCode))

          docLoader.loadAndInitContent(doc)
      }
    }.asInstanceOf[A]

  /**
   * @return default doc or null if doc does not exists
   */
  def getDefaultDoc[A <: DocumentDomainObject](docId: DocId, docLanguageCode: String): A =
    defaultDocs.getOrPut(DocCacheKey(docId, docLanguageCode)) {
      getMeta(docId) match {
        case meta =>
          val versionInfo = getDocVersionInfo(docId)
          val version = versionInfo.getDefaultVersion
          val doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType).asInstanceOf[DocumentDomainObject]

          doc.setMeta(meta.clone())
          doc.setVersionNo(version.getNo)
          doc.setLanguage(docLanguageSupport.getByCode(docLanguageCode))

          docLoader.loadAndInitContent(doc)
      }
    }.asInstanceOf[A]

  /**
   * @return custom doc or null if doc does not exists
   */
  def getCustomDoc[A <: DocumentDomainObject](ref: DocRef): A = {
    getMeta(ref.getDocId) match {
      case null => null
      case meta =>
        val versionInfo = getDocVersionInfo(ref.getDocId)
        val version = versionInfo.getVersion(ref.getDocVersionNo)
        val doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType).asInstanceOf[DocumentDomainObject]

        doc.setMeta(meta.clone())
        doc.setVersionNo(version.getNo)
        doc.setLanguage(docLanguageSupport.getByCode(ref.getDocLanguageCode))

        docLoader.loadAndInitContent(doc)
    }
  }.asInstanceOf[A]



  def removeDocFromCache(docId: DocId) {
    metas.remove(docId)
    versionInfos.remove(docId)

    for {
      code <- docLanguageSupport.getCodes.asScala
      key = DocCacheKey(docId, code)
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


