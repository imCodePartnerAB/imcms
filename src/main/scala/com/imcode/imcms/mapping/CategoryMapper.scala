package com.imcode
package imcms.mapping

import scala.collection.JavaConverters._
import com.imcode.imcms.dao.HibernateSupport
import com.imcode.imcms.api.CategoryAlreadyExistsException
import org.springframework.transaction.annotation.Transactional
import _root_.imcode.server.document.{MaxCategoryDomainObjectsOfTypeExceededException, DocumentDomainObject, CategoryDomainObject, CategoryTypeDomainObject}

@Transactional(rollbackFor = Array(classOf[Throwable]))
class CategoryMapper extends HibernateSupport {
  private val UNLIMITED_MAX_CATEGORY_CHOICES = 0

  /*
  static final String SQL__GET_DOCUMENT_CATEGORIES = "SELECT meta_id, category_id"
                                                     + " FROM document_categories"
                                                     + " WHERE meta_id ";
*/

  def getAllCategoriesOfType(categoryType: CategoryTypeDomainObject): Array[CategoryDomainObject] =
    hibernate.listByNamedQuery[CategoryDomainObject]("Category.getByType", categoryType) |> { _.toArray(Array.empty[CategoryDomainObject]) }

  def isUniqueCategoryTypeName(categoryTypeName: String): Boolean =
    !getAllCategoryTypes().exists(_.getName.equalsIgnoreCase(categoryTypeName))

  def getAllCategoryTypes(): Array[CategoryTypeDomainObject] =
    hibernate.listByNamedQuery[CategoryTypeDomainObject]("CategoryType.getAllTypes") |> { _.toArray(Array.empty[CategoryTypeDomainObject]) }

  def getCategoryByTypeAndName(categoryType: CategoryTypeDomainObject, categoryName: String): CategoryDomainObject =
    hibernate.getByNamedQueryAndNamedParams("Category.getByNameAndType",
      "name" -> categoryName,
      "type" -> categoryType
    )

  def getCategoryById(categoryId: Int): CategoryDomainObject = hibernate.get[CategoryDomainObject](categoryId)

  def getCategoryTypeByName(categoryTypeName: String): CategoryTypeDomainObject =
    hibernate.getByNamedQueryAndNamedParams("CategoryType.getByName", "name" -> categoryTypeName)

  def getCategoryTypeById(categoryTypeId: Int): CategoryTypeDomainObject = hibernate.get[CategoryTypeDomainObject](categoryTypeId)

  def deleteCategoryTypeFromDb(categoryType: CategoryTypeDomainObject): Unit = hibernate.delete(categoryType)

  def addCategoryTypeToDb(categoryType: CategoryTypeDomainObject ): CategoryTypeDomainObject =  hibernate.save(categoryType)

  // just update
  def updateCategoryType(categoryType: CategoryTypeDomainObject): Unit = hibernate.saveOrUpdate(categoryType)

  def saveCategoryType(categoryType: CategoryTypeDomainObject): Unit = hibernate.saveOrUpdate(categoryType)

  @throws(classOf[CategoryAlreadyExistsException])
  def addCategory(category: CategoryDomainObject): CategoryDomainObject = hibernate.save(category)

  // just update
  def updateCategory(category: CategoryDomainObject): Unit = hibernate.saveOrUpdate(category)

  def deleteCategoryFromDb(category: CategoryDomainObject): Unit = hibernate.delete(category)

  def getAllDocumentsOfOneCategory(category: CategoryDomainObject): Array[String] =
    hibernate.listBySqlQuery[JInteger]("select meta_id from document_categories where category_id = ?", category.getId).asScala.map(_.toString).toArray

  def deleteOneCategoryFromDocument(document: DocumentDomainObject, category: CategoryDomainObject): Unit =
    hibernate.bulkUpdateBySqlQuery("DELETE FROM document_categories WHERE meta_id = ? and category_id = ?", document.getId, category.getId)

  @throws(classOf[MaxCategoryDomainObjectsOfTypeExceededException])
  def checkMaxDocumentCategoriesOfType(document: DocumentDomainObject): Unit =
    for {
      categoryType <- getAllCategoryTypes()
      maxChoices = categoryType.getMaxChoices
      if maxChoices != UNLIMITED_MAX_CATEGORY_CHOICES
    } {
      val documentCategoriesOfType = getCategoriesOfType(categoryType, document.getCategoryIds)
      if (documentCategoriesOfType.size() > maxChoices) {
        throw new MaxCategoryDomainObjectsOfTypeExceededException("Document may have at most " + maxChoices
                                                                  + " categories of type '"
                                                                  + categoryType.getName()
                                                                  + "'")
      }
    }

  @throws(classOf[CategoryAlreadyExistsException])
  def saveCategory(category: CategoryDomainObject): CategoryDomainObject = {
    if (category.getId == 0) {
      getCategoryByTypeAndName(category.getType, category.getName) |> opt foreach { categoryInDb =>
        throw new CategoryAlreadyExistsException("A category with name \"" + category.getName
                                                 + "\" already exists in category type \""
                                                 + category.getType.getName
                                                 + "\".")
      }
    }

    hibernate.saveOrUpdate(category)
  }

  def getCategories(categoryIds: JCollection[JInteger]): JSet[CategoryDomainObject] = {
    val categories = for {
      categoryId <- categoryIds.asScala
      category <- Option(getCategoryById(categoryId))
    } yield category

    categories.toSet.asJava
  }

  def getAllCategories(): JList[CategoryDomainObject] = hibernate.listByNamedQuery("Category.getAll")

  def getCategoriesOfType(categoryType: CategoryTypeDomainObject, categoryIds: JSet[JInteger]): JSet[CategoryDomainObject] =
    getCategories(categoryIds).asScala.filter(_.getType == categoryType).asJava
}