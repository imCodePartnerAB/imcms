package com.imcode
package imcms.dao

import com.imcode.imcms.api.I18nLanguage
import com.imcode.imcms.api.TextHistory
import imcode.server.document.textdocument.TextDomainObject

import org.springframework.transaction.annotation.Transactional

class TextDao extends SpringHibernateTemplate {

  /**Inserts or updates text. */
  @Transactional
  def saveText(text: TextDomainObject) = letret(text) {
    hibernateTemplate.saveOrUpdate(_)
  }


  @Transactional
  def getTextById(id: JLong) = hibernateTemplate.get(classOf[TextDomainObject], id)


  @Transactional
  def deleteTexts(docId: JInteger, docVersionNo: JInteger, language: I18nLanguage): Int =
    deleteTexts(docId, docVersionNo, language.getId)


  @Transactional
  def deleteTexts(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) = withSession{
    _.getNamedQuery("Text.deleteTexts")
      .setParameter("docId", docId)
      .setParameter("docVersionNo", docVersionNo)
      .setParameter("languageId", languageId)
      .executeUpdate()
  }


  @Transactional
  def saveTextHistory(textHistory: TextHistory) = hibernateTemplate.save(textHistory)


  /**
   * @param docId
   * @param docVersionNo
   *
   * @return all texts in a doc.
   */
  @Transactional
  def getTexts(docId: JInteger, docVersionNo: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
      Array("docId", "docVersionNo"),
      Array[AnyRef](docId, docVersionNo)).asInstanceOf[java.util.List[TextDomainObject]]


  /**
   * Returns text fields for the same doc, version and language.
   */
  @Transactional
  def getTexts(docId: JInteger, docVersionNo: JInteger, languageId: JInteger) =
    hibernateTemplate.findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNoAndLanguageId",
      Array("docId", "docVersionNo", "languageId"),
      Array[AnyRef](docId, docVersionNo, languageId)).asInstanceOf[java.util.List[TextDomainObject]]


  /**
   * Returns text fields for the same doc, version and language.
   */
  @Transactional
  def getTexts(docId: JInteger, docVersionNo: JInteger, language: I18nLanguage): java.util.List[TextDomainObject] =
    getTexts(docId, docVersionNo, language.getId)
}


//package com.imcode.imcms.dao;
//
//import com.imcode.imcms.api.I18nLanguage;
//import com.imcode.imcms.api.TextHistory;
//import imcode.server.document.textdocument.TextDomainObject;
//import imcode.server.user.UserDomainObject;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.orm.hibernate3.HibernateTemplate;
//import org.springframework.transaction.annotation.Transactional;
//
//public class TextDao extends HibernateTemplate {
//
//	/**
//	 * Inserts or updates text.
//	 */
//	@Transactional
//	public TextDomainObject saveText(TextDomainObject text) {
//		saveOrUpdate(text);
//
//		return text;
//
//	}
//
//    @Transactional
//    public TextDomainObject getTextById(Integer id) {
//        return get(TextDomainObject.class, id);
//    }
//
//
//    @Transactional
//    public int deleteTexts(Integer docId, Integer docVersionNo, I18nLanguage language) {
//        return deleteTexts(docId, docVersionNo, language.getId());
//    }
//
//
//	@Transactional
//	public int deleteTexts(Integer docId, Integer docVersionNo, Integer languageId) {
//		return getSession().getNamedQuery("Text.deleteTexts")
//			.setParameter("docId", docId)
//			.setParameter("docVersionNo", docVersionNo)
//            .setParameter("languageId", languageId)
//			.executeUpdate();
//	}
//
//
//	/**
//	 * Saves text history.
//	 */
//	@Transactional
//	public void saveTextHistory(TextHistory textHistory) {
//        save(textHistory);
//	}
//
//
//    /**
//     * @param docId
//     * @param docVersionNo
//     *
//     * @return all texts in a doc.
//     */
//	@Transactional
//	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo) {
//		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
//				new String[] {"docId", "docVersionNo"},
//				new Object[] {docId, docVersionNo}
//		);
//	}
//
//
//	/**
//	 * Returns text fields for the same doc, version and language.
//	 */
//	@Transactional
//	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo, Integer languageId) {
//		return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNoAndLanguageId",
//				new String[] {"docId", "docVersionNo", "languageId"},
//				new Object[] {docId, docVersionNo, languageId}
//		);
//	}
//
//	/**
//	 * Returns text fields for the same doc, version and language.
//	 */
//	@Transactional
//	public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo, I18nLanguage language) {
//		return getTexts(docId, docVersionNo, language.getId());
//	}
//}