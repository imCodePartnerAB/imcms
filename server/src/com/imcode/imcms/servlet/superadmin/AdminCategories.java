package com.imcode.imcms.servlet.superadmin;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.Properties;

import com.imcode.imcms.servlet.admin.ImageBrowse;

/**
 * Created by IntelliJ IDEA.
 * User: lenake
 * Date: 2004-feb-23
 * Time: 13:46:39
 * To change this template use File | Settings | File Templates.
 */
public class AdminCategories extends HttpServlet {

    private static final String JSP_TEMPLATE = "category_admin.jsp";
    private static final String SESSION_ATTRIBUTE__FORM_DATA = "formData";
    private static final String REQUEST_ATTR_OR_PARAM__FORM_DATA_SESSION_ATTRIBUTE_NAME = SESSION_ATTRIBUTE__FORM_DATA + ".sessionAttributeName";
    private static final String PARAMETER__DESCRIPTION = "description";
    private static final String PARAMETER__ICON = "icon";
    private static final String PARAMETER__CATEGORIES = "categories";
    private static final String PARAMETER__OLD_NAME = "oldName";
    private static final String PARAMETER__CATEGORY_SAVE = "category_save";

    public static final String ATTRIBUTE__FORM_DATA = "admincategoriesbean";
    public static final String PARAMETER__NAME = "name";
    public static final String PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE = "button__select_category_type";
    public static final String PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW = "select__select_category_type";
    public static final String PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO = "add_to_category_type";
    public static final String PARAMETER__BROWSE_FOR_IMAGE = "browseForImage";
    public static final String PARAMETER_MODE__EDIT_CATEGORY_TYPE = "edit_category_type";
    public static final String PARAMETER_MODE__DELETE_CATEGORY_TYPE = "delete_category_type";
    public static final String PARAMETER_MODE__ADD_CATEGORY_TYPE = "add_category_type";
    public static final String PARAMETER_MODE__EDIT_CATEGORY = "edit_category";
    public static final String PARAMETER_MODE__ADD_CATEGORY = "add_category";
    public static final String PARAMETER_MODE__DELETE_CATEGORY = "delete_category";
    public static final String PARAMETER_MODE__VIEW_CATEGORY = "view_category";
    public static final String PARAMETER_MODE__DEFAULT = "default_mode";
    public static final String PARAMETER_BUTTON__CANCEL = "cancel";
    public static final String PARAMETER_MAX_CHOICES = "max_choices";
    public static final String PARAMETER_CATEGORY_TYPE_SAVE = "category_type_save";
    public static final String PARAMETER_CATEGORY_TYPE_ADD = "category_type_add";

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    protected void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        // Lets verify that the user who tries to add a new user is an admin
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if (service.checkAdminRights( user ) == false) {
            String header = "Error in AdminCategories. ";
            Properties langproperties = service.getLangProperties( user );
            String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
            this.log( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        if (null != req.getParameter( PARAMETER_BUTTON__CANCEL ) && null != req.getParameter( PARAMETER_MODE__DEFAULT )) {
            res.sendRedirect( "AdminManager" );
            return;
        }

        DocumentMapper documentMapper = service.getDocumentMapper();

        FormData formBean = new FormData();

        CategoryTypeDomainObject categoryType = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW, documentMapper );
        CategoryDomainObject category = getCategoryFromIdInRequest( req, documentMapper );

        if (req.getParameter( PARAMETER_CATEGORY_TYPE_ADD ) != null && !req.getParameter( PARAMETER__NAME ).trim().equals( "" )) {
            addCategoryType( req, formBean, documentMapper );
        } else if (null != req.getParameter( PARAMETER_MODE__EDIT_CATEGORY_TYPE )) {
            editCategoryType( categoryType, req, formBean, documentMapper );
        } else if (null != req.getParameter( PARAMETER_MODE__DELETE_CATEGORY_TYPE )) {
            deleteCategoryType( categoryType, formBean, documentMapper );
        } else if (null != req.getParameter( PARAMETER_MODE__ADD_CATEGORY )) {
            formBean = addCategory( req, res, formBean, documentMapper );
        } else if (null != req.getParameter( PARAMETER_MODE__EDIT_CATEGORY )) {
            formBean = editCategory( req, res, formBean, documentMapper );
        } else if (req.getParameter( PARAMETER_MODE__DELETE_CATEGORY ) != null) {
            deleteCategory( category, categoryType, req, formBean, documentMapper );
        } else if (null != req.getParameter( PARAMETER_MODE__VIEW_CATEGORY )) {
            viewCategory( categoryType, category, req, formBean );
        }

        if (!res.isCommitted()) {
            req.setAttribute( ATTRIBUTE__FORM_DATA, formBean );
            RequestDispatcher rd = this.getServletContext().getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP_TEMPLATE );
            rd.forward( req, res );
        }
    }

    private FormData editCategory( HttpServletRequest req, HttpServletResponse res, FormData formBean, DocumentMapper documentMapper ) throws ServletException, IOException {

        CategoryDomainObject category = null;
        CategoryTypeDomainObject categoryTypeToEdit = null;
        String formDataSessionAttributeName = getFormDataSessionAttributeNameFromRequest( req );
        boolean returningFromImageBrowse = null != formDataSessionAttributeName;
        if (returningFromImageBrowse) {
            formBean = getFormDataFromSession( req, formDataSessionAttributeName );
            category = formBean.getCategoryToEdit();
            categoryTypeToEdit = formBean.getCategoryTypeToEdit();
            String imageUrl = ImageBrowse.getImageUri( req );
            if (null != imageUrl) {
                category.setImageUrl( imageUrl );
            }
        } else {
            category = getCategoryFromIdInRequest( req, documentMapper );
            categoryTypeToEdit = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW, documentMapper );
        }

        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
        if (req.getParameter( PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE ) != null) {
            formBean.setCategoryToEdit( null );
        } else {
            formBean.setCategoryToEdit( category );
        }

        boolean nameIsUnique = true;

        if (req.getParameter( PARAMETER__BROWSE_FOR_IMAGE ) != null) {
            setCategoryFromRequest( category, req, documentMapper );
            putFormDataInSessionAndForwardToImageBrowse( formBean, req, res, PARAMETER_MODE__EDIT_CATEGORY );
        } else if (req.getParameter( PARAMETER__CATEGORY_SAVE ) != null) {
            boolean nameWasChanged = !req.getParameter( PARAMETER__OLD_NAME ).toLowerCase().equals( req.getParameter( PARAMETER__NAME ).toLowerCase() );
            CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, documentMapper );
            if (nameWasChanged) {
                nameIsUnique = !categoryTypeToAddTo.hasCategoryWithName( categoryTypeToAddTo, req.getParameter( PARAMETER__NAME ) );
            }
            if (nameIsUnique) {
                setCategoryFromRequest( category, req, documentMapper );
                documentMapper.updateCategory( category );
                formBean.setCategoryToEdit( null );

            }
        }

        formBean.setUniqueCategoryName( nameIsUnique );
        return formBean;
    }

    private void setCategoryFromRequest( CategoryDomainObject category, HttpServletRequest req, DocumentMapper documentMapper ) {
        category.setName( req.getParameter( PARAMETER__NAME ) );
        category.setDescription( req.getParameter( PARAMETER__DESCRIPTION ) );
        category.setImageUrl( req.getParameter( PARAMETER__ICON ) );
        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, documentMapper );
        category.setType( categoryTypeToAddTo );
    }

    private FormData addCategory( HttpServletRequest req, HttpServletResponse res, FormData formData, DocumentMapper documentMapper ) throws ServletException, IOException {
        CategoryDomainObject newCategory = null;
        String formDataSessionAttributeName = getFormDataSessionAttributeNameFromRequest( req );
        boolean returningFromImageBrowse = null != formDataSessionAttributeName;
        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, documentMapper );
        if (returningFromImageBrowse) {
            formData = getFormDataFromSession( req, formDataSessionAttributeName );
            newCategory = formData.getCategoryToEdit();
            String imageUrl = ImageBrowse.getImageUri( req );
            if (null != imageUrl) {
                newCategory.setImageUrl( imageUrl );
            }
        } else {

            newCategory = new CategoryDomainObject( 0,
                    req.getParameter( PARAMETER__NAME ),
                    req.getParameter( PARAMETER__DESCRIPTION ),
                    req.getParameter( PARAMETER__ICON ),
                    categoryTypeToAddTo );
        }
        formData.setCategoryToEdit( newCategory );

        if (req.getParameter( PARAMETER__BROWSE_FOR_IMAGE ) != null) {
            putFormDataInSessionAndForwardToImageBrowse( formData, req, res, PARAMETER_MODE__ADD_CATEGORY );
        } else if (req.getParameter( "category_add" ) != null && !newCategory.getName().trim().equals( "" )) {
            if (!categoryTypeToAddTo.hasCategoryWithName( newCategory.getType(), newCategory.getName() )) {
                documentMapper.addCategoryToDb( newCategory.getType().getId(), newCategory.getName(), newCategory.getDescription(), newCategory.getImageUrl() );
                formData.setCategoryToEdit( new CategoryDomainObject( 0, "", "", "", null ) );
                formData.setUniqueCategoryName( true );
            } else {
                formData.setUniqueCategoryName( false );
                formData.setCategoryTypeToEdit( newCategory.getType() );
            }
        }
        return formData;
    }

    private FormData getFormDataFromSession( HttpServletRequest req, String formDataSessionAttributeName ) {
        FormData formData;
        formData = (FormData) req.getSession().getAttribute( formDataSessionAttributeName );
        return formData;
    }

    private String getFormDataSessionAttributeNameFromRequest( HttpServletRequest req ) {
        String formDataSessionAttributeName = req.getParameter( REQUEST_ATTR_OR_PARAM__FORM_DATA_SESSION_ATTRIBUTE_NAME );
        return formDataSessionAttributeName;
    }

    private CategoryDomainObject getCategoryFromIdInRequest( HttpServletRequest req, DocumentMapper documentMapper ) {
        CategoryDomainObject categoryToEdit = null;
        String categoryIdString = req.getParameter( PARAMETER__CATEGORIES );
        boolean selectCategoryButtonPressed = req.getParameter( PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE ) == null;
        boolean aCategoryWasSelected = categoryIdString != null && selectCategoryButtonPressed;
        if (aCategoryWasSelected) {
            int categoryId = Integer.parseInt( categoryIdString );
            categoryToEdit = documentMapper.getCategoryById( categoryId );
        }
        return categoryToEdit;
    }

    private CategoryTypeDomainObject getCategoryTypeFromRequest( HttpServletRequest req, String requestParameter, DocumentMapper documentMapper ) {
        CategoryTypeDomainObject categoryType = null;
        String categoryTypeIdString = req.getParameter( requestParameter );
        boolean gotCategoryTypeId = null != categoryTypeIdString;
        if (gotCategoryTypeId) {
            int categoryTypeId = Integer.parseInt( categoryTypeIdString );
            categoryType = documentMapper.getCategoryTypeById( categoryTypeId );
        }
        return categoryType;
    }

    private void deleteCategory( CategoryDomainObject categoryToEdit, CategoryTypeDomainObject categoryTypeToEdit, HttpServletRequest req, FormData formData, DocumentMapper documentMapper ) {
        CategoryDomainObject categoryToEdit1 = categoryToEdit;
        String[] documentsOfOneCategory = null;
        if (categoryToEdit1 != null) {
            documentsOfOneCategory = documentMapper.getAllDocumentsOfOneCategory( categoryToEdit1 );
            if (req.getParameter( "category_delete" ) != null) {
                DocumentDomainObject document;
                for (int i = 0; i < documentsOfOneCategory.length; i++) {
                    document = documentMapper.getDocument( Integer.parseInt( documentsOfOneCategory[i] ) );
                    documentMapper.deleteOneCategoryFromDocument( document, categoryToEdit1 );
                }
                documentMapper.deleteCategoryFromDb( categoryToEdit1 );
                categoryToEdit1 = null;
                documentsOfOneCategory = null;
            }
        }

        formData.setCategoryTypeToEdit( categoryTypeToEdit );
        formData.setCategoryToEdit( categoryToEdit1 );
        formData.setDocumentsOfOneCategory( documentsOfOneCategory );
        categoryToEdit = categoryToEdit1;
    }

    private void putFormDataInSessionAndForwardToImageBrowse( FormData formData, HttpServletRequest req, HttpServletResponse res, String callerMode ) throws ServletException, IOException {
        String uniqueSessionAttributeName;
        uniqueSessionAttributeName = SESSION_ATTRIBUTE__FORM_DATA + "." + System.currentTimeMillis();
        req.getSession().setAttribute( uniqueSessionAttributeName, formData );

        String returningUrl = "AdminCategories?" +
                callerMode + "=" + "1" + "&"
                + REQUEST_ATTR_OR_PARAM__FORM_DATA_SESSION_ATTRIBUTE_NAME
                + "="
                + uniqueSessionAttributeName;

        req.getRequestDispatcher( "ImageBrowse?" + ImageBrowse.PARAMETER__CALLER + "="
                + java.net.URLEncoder.encode( returningUrl ) ).forward( req, res );
    }

    private void viewCategory( CategoryTypeDomainObject categoryTypeToEdit, CategoryDomainObject categoryToEdit, HttpServletRequest req, FormData formBean ) {
        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
        if (req.getParameter( PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE ) != null) {
            formBean.setCategoryToEdit( null );
        } else {
            formBean.setCategoryToEdit( categoryToEdit );
        }
    }

    private void deleteCategoryType( CategoryTypeDomainObject categoryTypeToEdit, FormData formBean, DocumentMapper documentMapper ) {
        int numberOfCategories = 0;
        if (categoryTypeToEdit != null) {
            numberOfCategories = documentMapper.getAllCategoriesOfType( categoryTypeToEdit ).length;
            if (numberOfCategories == 0) {
                documentMapper.deleteCategoryTypeFromDb( categoryTypeToEdit );
            }
        }
        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
        formBean.setNumberOfCategories( numberOfCategories );
    }

    private void editCategoryType( CategoryTypeDomainObject categoryTypeToEdit, HttpServletRequest req, FormData formBean, DocumentMapper documentMapper ) {
        if (req.getParameter( PARAMETER_CATEGORY_TYPE_SAVE ) != null) {
            String name = req.getParameter( PARAMETER__NAME ).trim();
            if( documentMapper.isUniqueCategoryTypeName( name ) ) {
                formBean.setUniqueCategoryTypeName( true );
                int maxChoices = Integer.parseInt( req.getParameter( "max_choices" ) );
                categoryTypeToEdit.setName( name );
                categoryTypeToEdit.setMaxChoices( maxChoices );
                documentMapper.updateCategoryType( categoryTypeToEdit );
            } else {
                formBean.setUniqueCategoryTypeName( false );
            }
        }

        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
    }

    private void addCategoryType( HttpServletRequest req, FormData formBean, DocumentMapper documentMapper ) {
        String categoryTypeName = req.getParameter( PARAMETER__NAME ).trim();
        int maxChoices = Integer.parseInt( req.getParameter( PARAMETER_MAX_CHOICES ) );

        if (documentMapper.isUniqueCategoryTypeName( categoryTypeName )) {
            formBean.setUniqueCategoryTypeName( true );
            documentMapper.addCategoryTypeToDb( categoryTypeName, maxChoices );
        } else {
            formBean.setUniqueCategoryTypeName( false );
        }
    }


    public static String createHtmlOptionListOfCategoryTypes( CategoryTypeDomainObject selectedType ) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();
        CategoryTypeDomainObject[] categoryTypes = imcref.getDocumentMapper().getAllCategoryTypes();
        String temps = "";
        for (int i = 0; i < categoryTypes.length; i++) {
            boolean selected = selectedType != null && selectedType.getId() == categoryTypes[i].getId();
            temps += "<option value=\""
                    + categoryTypes[i].getId()
                    + "\""
                    + (selected ? " selected" : "")
                    + ">"
                    + categoryTypes[i].getName() + "</option>";
        }
        return temps;
    }

    public static String createHtmlOptionListOfCategoriesForOneType( CategoryTypeDomainObject categoryType, CategoryDomainObject selectedCategory ) {
        DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();

        CategoryDomainObject[] categories = documentMapper.getAllCategoriesOfType( categoryType );
        String temps = "";
        for (int i = 0; i < categories.length; i++) {
            boolean selected = selectedCategory != null && selectedCategory.equals( categories[i] );
            temps += "<option value=\""
                    + categories[i].getId()
                    + "\""
                    + (selected ? " selected" : "")
                    + ">"
                    + categories[i].getName() + "</option>";
        }
        return temps;
    }

    public static class FormData {

        private CategoryTypeDomainObject categoryTypeToEdit;
        private CategoryDomainObject categoryToEdit;
        private int numberOfCategories;
        private String[] documentsOfOneCategory;
        private boolean uniqueCategoryName;

        public boolean isUniqueCategoryTypeName() {
            return uniqueCategoryTypeName;
        }

        public void setUniqueCategoryTypeName( boolean uniqueCategoryTypeName ) {
            this.uniqueCategoryTypeName = uniqueCategoryTypeName;
        }

        private boolean uniqueCategoryTypeName;


        public CategoryDomainObject getCategoryToEdit() {
            return categoryToEdit;
        }

        private void setCategoryToEdit( CategoryDomainObject categoryToEdit ) {
            this.categoryToEdit = categoryToEdit;
        }

        public CategoryTypeDomainObject getCategoryTypeToEdit() {
            return categoryTypeToEdit;
        }

        private void setCategoryTypeToEdit( CategoryTypeDomainObject categoryTypeToEdit ) {
            this.categoryTypeToEdit = categoryTypeToEdit;
        }

        public int getNumberOfCategories() {
            return numberOfCategories;
        }

        private void setNumberOfCategories( int numberOfCategories ) {
            this.numberOfCategories = numberOfCategories;
        }

        public String[] getDocumentsOfOneCategory() {
            return documentsOfOneCategory;
        }

        private void setDocumentsOfOneCategory( String[] documentsOfOneCategory ) {
            this.documentsOfOneCategory = documentsOfOneCategory;
        }

        public boolean getUniqueCategoryName() {
            return uniqueCategoryName;
        }

        private void setUniqueCategoryName( boolean bool ) {
            uniqueCategoryName = bool;
        }
    }

}
