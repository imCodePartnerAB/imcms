package com.imcode.imcms.servlet.superadmin;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: lenake
 * Date: 2004-feb-23
 * Time: 13:46:39
 * To change this template use File | Settings | File Templates.
 */
public class AdminCategories extends HttpServlet{

    public static final String ADMIN_CATEGORIES_BEAN = "admincategoriesbean";
    private static final String JSP_TEMPLATE = "category_admin.jsp";

    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        // check params
         // Lets verify that the user who tries to add a new user is an admin
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( service.checkAdminRights( user ) == false ) {
            String header = "Error in AdminCategories. ";
            Properties langproperties = service.getLangProperties( user );
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            this.log( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        if ( req.getParameter("cancel") != null && req.getParameter("adminMode") != null  ) {
            if(req.getParameter("adminMode").equals("") ) {
                Utility.redirect( req, res, "AdminManager" );
                return;
            }
        }

        AdminCategoriesBean formBean = new AdminCategoriesBean();
        String adminMode = req.getParameter("adminMode") == null ? "default" : req.getParameter("adminMode");

        CategoryTypeDomainObject categoryTypeToEdit = null ;
        if(req.getParameter("select_category_type") != null){
                categoryTypeToEdit = service.getDocumentMapper().getCategoryTypeById(Integer.parseInt(req.getParameter("select_category_type")));
        }

        CategoryDomainObject categoryToEdit = null;
        if(req.getParameter("categories") != null && req.getParameter("select_category_type_to_edit") == null ) {
            categoryToEdit = service.getDocumentMapper().getCategoryById(Integer.parseInt(req.getParameter("categories")));
        }

        // ----  Add category type  ---------
        if(req.getParameter("add_category_type") != null || req.getParameter("category_type_add") != null){

            if(req.getParameter("category_type_add") != null && !req.getParameter("name").trim().equals("")){
                service.getDocumentMapper().addCategoryTypeToDb(req.getParameter("name").trim(), Integer.parseInt(req.getParameter("max_choices")));
            }
        }

        // ----  Edit category type  ---------
        if(req.getParameter("edit_category_type") != null ||
                adminMode.equals("editCategoryTypeMode") ) {

            if(req.getParameter("category_type_save") != null ){
                categoryTypeToEdit.setName(req.getParameter("name").trim());
                categoryTypeToEdit.setMaxChoices(Integer.parseInt(req.getParameter("max_choices")));
                service.getDocumentMapper().updateCategoryType(categoryTypeToEdit);
            }

            formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit) );
            formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        }

        // ----  Delete category type  ---------
        if(req.getParameter("delete_category_type") != null ||
                adminMode.equals("deleteCategoryTypeMode")){

            StringBuffer msg = new StringBuffer("");
            int numberOfCategories = 0;
            if(categoryTypeToEdit != null ){
                numberOfCategories = service.getDocumentMapper().getAllCategoriesOfType(categoryTypeToEdit).length;
                if(numberOfCategories == 0 ) {
                    service.getDocumentMapper().deleteCategoryTypeFromDb(categoryTypeToEdit);
                }
            }
            formBean.setCategoryTypeToEdit(categoryTypeToEdit);
            formBean.setNumberOfCategories(numberOfCategories);
            formBean.setMsgToUser(msg.toString());
            formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
        }

        // ----  Add category  ---------
        if(req.getParameter("add_category") != null || adminMode.equals("addCategoryMode")){

            CategoryTypeDomainObject categoryTypeToAddTo = null ;
            if(req.getParameter("category_add") != null && !req.getParameter("name").trim().equals("")){
                if(req.getParameter("add_to_category_type") != null){
                    categoryTypeToAddTo = service.getDocumentMapper().getCategoryTypeById(Integer.parseInt(req.getParameter("add_to_category_type")));
                }

                if( isUniqueName(service, categoryTypeToAddTo, req.getParameter("name") ) ) {
                    service.getDocumentMapper().addCategoryToDb(categoryTypeToAddTo, req.getParameter("name").trim(), req.getParameter("description"), req.getParameter("icon"));
                    formBean.setUniqueName(true);
                }else{
                    formBean.setUniqueName(false);
                    formBean.setCategoryTypeToEdit(categoryTypeToAddTo);
                }
            }
            formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToAddTo));
        }

        // ----  Edit category  ---------
        if(req.getParameter("edit_category") != null ||
                adminMode.equals("editCategoryMode") ) {

            boolean nameIsUnique = true;
            if(req.getParameter("category_save") != null ){
                CategoryTypeDomainObject categoryTypeToAddTo = null ;
                if(req.getParameter("add_to_category_type") != null){
                    categoryTypeToAddTo = service.getDocumentMapper().getCategoryTypeById(Integer.parseInt(req.getParameter("add_to_category_type")));
                }
                if( !req.getParameter("oldName").toLowerCase().equals(req.getParameter("name").toLowerCase())) {
                    if( !isUniqueName(service, categoryTypeToAddTo, req.getParameter("name") ) ) {
                        nameIsUnique = false;
                    }
                }
                if( nameIsUnique ) {
                    categoryToEdit.setName(req.getParameter("name"));
                    categoryToEdit.setType(categoryTypeToAddTo);
                    categoryToEdit.setImage(req.getParameter("icon"));
                    categoryToEdit.setDescription(req.getParameter("description"));
                    service.getDocumentMapper().updateCategory(categoryToEdit);
                    categoryToEdit = null;
                }
            }

            if(categoryTypeToEdit != null ) {
                formBean.setCategoriesOptionList(createHtmlOptionListOfCategoriesForOneType(service, categoryTypeToEdit, categoryToEdit != null ? categoryToEdit.getId()+"" : null));
            }
            formBean.setUniqueName(nameIsUnique);
            formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
            formBean.setCategoryTypeToEdit(categoryTypeToEdit);
            if(req.getParameter("select_category_type_to_edit") != null ) {
                formBean.setCategoryToEdit(null);
            }else{
                formBean.setCategoryToEdit(categoryToEdit);
            }

        }

        // ----  Delete category  ---------
        if(req.getParameter("delete_category") != null ||
                adminMode.equals("deleteCategoryMode")){

            StringBuffer msg = new StringBuffer("");
            String[] documentsOfOneCategory = null;
            if(categoryToEdit != null ){
                documentsOfOneCategory = service.getDocumentMapper().getAllDocumentsOfOneCategory(categoryToEdit);
                if( req.getParameter("category_delete") != null  ) {
                    DocumentDomainObject document;
                    for(int i=0; i<documentsOfOneCategory.length; i++){
                        document = service.getDocumentMapper().getDocument(Integer.parseInt(documentsOfOneCategory[i]));
                        service.getDocumentMapper().deleteOneCategoryFromDocument(document, categoryToEdit);
                    }
                    service.getDocumentMapper().deleteCategoryFromDb(categoryToEdit);
                    categoryToEdit = null;
                    documentsOfOneCategory = null;
                }
            }

            if(categoryTypeToEdit != null ) {
                formBean.setCategoriesOptionList(createHtmlOptionListOfCategoriesForOneType(service, categoryTypeToEdit, categoryToEdit != null ? categoryToEdit.getId()+"" : null));
            }
            formBean.setCategoryTypeToEdit(categoryTypeToEdit);
            formBean.setCategoryToEdit(categoryToEdit);
            formBean.setDocumentsOfOneCategory(documentsOfOneCategory);
            formBean.setMsgToUser(msg.toString());
            formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
        }

        // ----  Show category  ---------
        if(req.getParameter("view_category") != null ||
                adminMode.equals("showCategoryMode") ) {

         /*  if(req.getParameter("category_save") != null ){
                CategoryTypeDomainObject categoryTypeToAddTo = null ;
                if(req.getParameter("add_to_category_type") != null){
                    categoryTypeToAddTo = service.getDocumentMapper().getCategoryTypeById(Integer.parseInt(req.getParameter("add_to_category_type")));
                }
                categoryToEdit.setName(req.getParameter("name"));
                categoryToEdit.setType(categoryTypeToAddTo);
                categoryToEdit.setImage(req.getParameter("icon"));
                categoryToEdit.setDescription(req.getParameter("description"));
                service.getDocumentMapper().updateCategory(categoryToEdit);
                categoryToEdit = null;
            }
        */
            if(categoryTypeToEdit != null ) {
                formBean.setCategoriesOptionList(createHtmlOptionListOfCategoriesForOneType(service, categoryTypeToEdit, categoryToEdit != null ? categoryToEdit.getId()+"" : null));
            }

            formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
            formBean.setCategoryTypeToEdit(categoryTypeToEdit);
            if(req.getParameter("select_category_type_to_edit") != null ) {
                formBean.setCategoryToEdit(null);
            }else{
                formBean.setCategoryToEdit(categoryToEdit);
            }

        }

        formBean.setAdminMode(adminMode);
        req.setAttribute( ADMIN_CATEGORIES_BEAN, formBean );
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/imcms/"+ user.getLanguageIso639_2() + "/jsp/" + JSP_TEMPLATE);
        rd.forward( req, res );
    }


    private String createHtmlOptionListOfCategoryTypes(IMCServiceInterface imcref, CategoryTypeDomainObject selectedType) {

        CategoryTypeDomainObject[] categoryTypes = imcref.getDocumentMapper().getAllCategoryTypes();
        String temps = "";
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            boolean selected = selectedType != null && selectedType.getId() == categoryTypes[i].getId() ;
           temps += "<option value=\""
                     + categoryTypes[i].getId()
                     + "\""
                     + ( selected ? " selected" : "" )
                     + ">"
                     + categoryTypes[i].getName() + "</option>";
        }
        return temps;
    }

    private String createHtmlOptionListOfCategoriesForOneType( IMCServiceInterface service, CategoryTypeDomainObject categoryType,  String selectedCategory ) {

        CategoryDomainObject[] categories = service.getDocumentMapper().getAllCategoriesOfType(categoryType);
        String temps = "";
        for ( int i = 0; i < categories.length; i++ ) {
            boolean selected = selectedCategory != null && selectedCategory.equals(""+ categories[i].getId() );
           temps += "<option value=\""
                     + categories[i].getId()
                     + "\""
                     + ( selected ? " selected" : "" )
                     + ">"
                     + categories[i].getName() + "</option>";
        }
        return temps;
    }

    private boolean isUniqueName( IMCServiceInterface service, CategoryTypeDomainObject categoryType, String categoryName) {

        CategoryDomainObject[] categories = service.getDocumentMapper().getAllCategoriesOfType(categoryType);
        for(int i = 0; i < categories.length; i++) {
            if(categories[i].getName().toLowerCase().equals(categoryName.trim().toLowerCase()) ){
                return false;
            }
        }
        return true;
    }

    public static class AdminCategoriesBean {

        private String adminMode;
        private String categoryTypesOptionList;
        private String categoriesOptionList;
        private CategoryTypeDomainObject categoryTypeToEdit;
        private CategoryDomainObject categoryToEdit;
        private int numberOfCategories;
        private String[] documentsOfOneCategory;
        private String msgToUser;
        private boolean uniqueName;



        public String getAdminMode() {
            return adminMode;
        }

        public void setAdminMode(String adminMode) {
            this.adminMode = adminMode;
        }


         public String getCategoryTypesOptionList() {
            return categoryTypesOptionList;
        }

        public void setCategoryTypesOptionList(String categoryTypesOptionList) {
            this.categoryTypesOptionList = categoryTypesOptionList;
        }


        public String getCategoriesOptionList() {
            return categoriesOptionList;
        }

        public void setCategoriesOptionList(String categoriesOptionList) {
            this.categoriesOptionList = categoriesOptionList;
        }

        public CategoryDomainObject getCategoryToEdit() {
            return categoryToEdit;
        }

        public void setCategoryToEdit(CategoryDomainObject categoryToEdit) {
            this.categoryToEdit = categoryToEdit;
        }

        public CategoryTypeDomainObject getCategoryTypeToEdit() {
            return categoryTypeToEdit;
        }

        public void setCategoryTypeToEdit(CategoryTypeDomainObject categoryTypeToEdit) {
            this.categoryTypeToEdit = categoryTypeToEdit;
        }

        public int getNumberOfCategories() {
            return numberOfCategories;
        }

        public void setNumberOfCategories(int numberOfCategories) {
            this.numberOfCategories = numberOfCategories;
        }

        public String[] getDocumentsOfOneCategory() {
            return documentsOfOneCategory;
        }

        public void setDocumentsOfOneCategory(String[] documentsOfOneCategory) {
            this.documentsOfOneCategory = documentsOfOneCategory;
        }

        public String getMsgToUser() {
            return msgToUser;
        }

        public void setMsgToUser(String msgToUser) {
            this.msgToUser = msgToUser;
        }

        public boolean getUniqueName() {
            return uniqueName;
        }

        public void setUniqueName(boolean bool ) {
            uniqueName = bool;
        }
    }
}
