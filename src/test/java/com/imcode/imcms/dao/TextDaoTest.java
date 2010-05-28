package com.imcode.imcms.dao;

import com.imcode.imcms.Script;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.util.Factory;
import imcode.server.document.textdocument.TextDomainObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class TextDaoTest {
	
	static TextDao textDao;

    static DataSourceDatabaseTester dbTester;

    static I18nLanguage ENGLISH = Factory.createLanguage(1, "en", "English");

    static I18nLanguage SWEDISH = Factory.createLanguage(2, "sv", "Swedish");

    
	@BeforeClass
    public static void init() {
        Script.recreateDB();
//        dbTester = new DataSourceDatabaseTester(Script.createDBDataSource(true));
//        dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
//
//        try {
//            dbTester.setDataSet(new XmlDataSet(FileUtils.openInputStream(new File(""))));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
	}
    

    @Before
    public void resetDBData() {

        SessionFactory sf = Script.createHibernateSessionFactory(
                new Class[] {I18nLanguage.class, TextDomainObject.class},
                "src/main/resources/I18nLanguage.hbm.xml",
                "src/main/resources/Text.hbm.xml");

        textDao = new TextDao();
        textDao.setSessionFactory(sf);

        Script.runDBScripts("text_dao.sql");
    }
    


    @Test
    public void saveText() {
        TextDomainObject text = Factory.createText(1001, 0, 0, ENGLISH);

        textDao.saveText(text);
    }


    @Test
    public void updateText() {
        TextDomainObject text = Factory.createText(1001, 0, 0, ENGLISH);

        textDao.saveText(text);

        TextDomainObject updatedText = Factory.createText(1001, 0, 0, ENGLISH);
        updatedText.setId(text.getId());

        updatedText.setText("updated text");
        textDao.save(updatedText);
    }
    

//    @Transactional
//    public int deleteTexts(Integer docId, Integer docVersionNo, Integer languageId) {
//        return getSession().getNamedQuery("Text.deleteTexts")
//            .setParameter("docId", docId)
//            .setParameter("docVersionNo", docVersionNo)
//            .setParameter("languageId", languageId)
//            .executeUpdate();
//    }
//
//
//    /**
//     * Saves text history.
//     */
//    @Transactional
//    public void saveTextHistory(TextHistory textHistory) {
//        save(textHistory);
//    }
//
//
//    /**
//     * @param docId
//     * @param docVersionNo
//     *
//     * @return all texts in a doc.
//     */
//    @Transactional
//    public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo) {
//        return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNo",
//                new String[] {"docId", "docVersionNo"},
//                new Object[] {docId, docVersionNo}
//        );
//    }
//
//
//    /**
//     * Returns text fields for the same doc, version and language.
//     */
//    @Transactional
//    public List<TextDomainObject> getTexts(Integer docId, Integer docVersionNo, Integer languageId) {
//        return findByNamedQueryAndNamedParam("Text.getByDocIdAndDocVersionNoAndLanguageId",
//                new String[] {"docId", "docVersionNo", "languageId"},
//                new Object[] {docId, docVersionNo, languageId}
//        );
//    }

}