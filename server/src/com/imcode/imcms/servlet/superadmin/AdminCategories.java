package com.imcode.imcms.servlet.superadmin;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.CategoryDomainObject;
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

import com.imcode.imcms.servlet.admin.ImageBrowse;

/**
 * Created by IntelliJ IDEA.
 * User: lenake
 * Date: 2004-feb-23
 * Time: 13:46:39
 * To change this template use File | Settings | File Templates.
 */
public class AdminCategories extends HttpServlet {

    public static final String ATTRIBUTE__FORM_DATA = "admincategoriesbean";
    private static final String JSP_TEMPLATE = "category_admin.jsp";

    private static final String SESSION_ATTRIBUTE__FORM_DATA = "formData";
    private static final String REQUEST_ATTR_OR_PARAM__FORM_DATA_SESSION_ATTRIBUTE_NAME = SESSION_ATTRIBUTE__FORM_DATA+".sessionAttributeName";
    private static final String PARAMETER__CATEGORY_TYPE_ADD = "category_type_add";
    public static final String PARAMETER__NAME = "name";
    private static final String PARAMETER__DESCRIPTION = "description";
    public static final String PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE = "button__select_category_type";
    private static final String PARAMETER__ICON = "icon";
    private static final String PARAMETER__CATEGORIES = "categories";
    public static final String PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW = "select__select_category_type";
    public static final String PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO = "add_to_category_type";
    public static final String PARAMETER__BROWSE_FOR_IMAGE = "browseForImage";
    private static final String PARAMETER__OLD_NAME = "oldName";
    private static final String PARAMETER__CATEGORY_SAVE = "category_save";
    public static final String PARAMETER_MODE__EDIT_CATEGORY_TYPE = "edit_category_type";
    public static final String PARAMETER_MODE__DELETE_CATEGORY_TYPE = "delete_category_type";
    public static final String PARAMETER_MODE__ADD_CATEGORY_TYPE = "add_category_type";
    public static final String PARAMETER_MODE__EDIT_CATEGORY = "edit_category";
    public static final String PARAMETER_MODE__ADD_CATEGORY = "add_category";
    public static final String PARAMETER_MODE__DELETE_CATEGORY = "delete_category";
    public static final String PARAMETER_MODE__VIEW_CATEGORY = "view_category";
    public static final String PARAMETER_MODE__DEFAULT = "default_mode";
    public static final String PARAMETER_BUTTON__CANCEL = "cancel";


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // check params
        // Lets verify that the user who tries to add a new user is an admin
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        if (service.checkAdminRights(user) == false) {
            String header = "Error in AdminCategories. ";
            Properties langproperties = service.getLangProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            this.log(header + "- user is not an administrator");
            new AdminError(req, res, header, msg);
            return;
        }

        if (null != req.getParameter(PARAMETER_BUTTON__CANCEL) && null != req.getParameter(PARAMETER_MODE__DEFAULT)) {
            res.sendRedirect( "AdminManager" );
            return;
        }

        FormData formData = new FormData();

        CategoryTypeDomainObject categoryType = getCategoryTypeFromRequest(req, service, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW);
        CategoryDomainObject category = getCategoryFromIdInRequest(req, service);

        if (req.getParameter(PARAMETER__CATEGORY_TYPE_ADD) != null && !req.getParameter(PARAMETER__NAME).trim().equals("")) {
            addCategoryType(service, req);
        } else if (null != req.getParameter(PARAMETER_MODE__EDIT_CATEGORY_TYPE)) {
            editCategoryType(req, categoryType, service, formData);
        } else if (null != req.getParameter(PARAMETER_MODE__DELETE_CATEGORY_TYPE)) {
            deleteCategoryType(categoryType, service, formData);
        } else if (null != req.getParameter(PARAMETER_MODE__ADD_CATEGORY)) {
            formData = addCategory(req, service, formData, res);
        } else if (null != req.getParameter(PARAMETER_MODE__EDIT_CATEGORY)) {
            formData = editCategory(req, service, res, formData);
        } else if (req.getParameter(PARAMETER_MODE__DELETE_CATEGORY) != null) {
            deleteCategory(category, service, req, categoryType, formData);
        } else if (null != req.getParameter(PARAMETER_MODE__VIEW_CATEGORY)) {
            viewCategory(categoryType, formData, category, req);
        }

        if (!res.isCommitted()) {
            req.setAttribute(ATTRIBUTE__FORM_DATA, formData);
            RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP_TEMPLATE);
            rd.forward(req, res);
        }
    }

    private FormData editCategory(HttpServletRequest req, IMCServiceInterface service, HttpServletResponse res, FormData formData) throws ServletException, IOException {

        CategoryDomainObject category = null ;
        CategoryTypeDomainObject categoryTypeToEdit = null ;
        String formDataSessionAttributeName = getFormDataSessionAttributeNameFromRequest(req);
        boolean returningFromImageBrowse = null != formDataSessionAttributeName;
        if (returningFromImageBrowse) {
            formData = getFormDataFromSession(req, formDataSessionAttributeName);
            category = formData.getCategoryToEdit();
            categoryTypeToEdit = formData.getCategoryTypeToEdit() ;
            String imageUrl = ImageBrowse.getImageUri(req);
            if (null != imageUrl) {
                category.setImage(imageUrl);
            }
        } else {
            category = getCategoryFromIdInRequest(req, service);
            categoryTypeToEdit = getCategoryTypeFromRequest(req, service, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW);
        }

        formData.setCategoryTypeToEdit(categoryTypeToEdit);
        if (req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE) != null) {
            formData.setCategoryToEdit(null);
        } else {
            formData.setCategoryToEdit(category);
        }

        boolean nameIsUnique = true;

        if (req.getParameter(PARAMETER__BROWSE_FOR_IMAGE) != null) {
            setCategoryFromRequest(category, req, service);
            putFormDataInSessionAndForwardToImageBrowse(formData, req, res, PARAMETER_MODE__EDIT_CATEGORY);
        } else if (req.getParameter(PARAMETER__CATEGORY_SAVE) != null) {
            boolean nameWasChanged = !req.getParameter(PARAMETER__OLD_NAME).toLowerCase().equals(req.getParameter(PARAMETER__NAME).toLowerCase());
            CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest(req, service, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO);
            if (nameWasChanged) {
                nameIsUnique = isUniqueName(service, categoryTypeToAddTo, req.getParameter(PARAMETER__NAME));
            }
            if (nameIsUnique) {
                setCategoryFromRequest(category, req, service);
                service.getDocumentMapper().updateCategory(category);
                formData.setCategoryToEdit(null);

            }
        }

        formData.setUniqueName(nameIsUnique);
        return formData ;
    }

    private void setCategoryFromRequest(CategoryDomainObject category, HttpServletRequest req, IMCServiceInterface service) {
        category.setName(req.getParameter(PARAMETER__NAME));
        category.setDescription(req.getParameter(PARAMETER__DESCRIPTION));
        category.setImage(req.getParameter(PARAMETER__ICON));
        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest(req, service, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO);
        category.setType(categoryTypeToAddTo);
    }

    private FormData addCategory(HttpServletRequest req, IMCServiceInterface service, FormData formData, HttpServletResponse res) throws ServletException, IOException {
        CategoryDomainObject newCategory = null;
        String formDataSessionAttributeName = getFormDataSessionAttributeNameFromRequest(req);
        boolean returningFromImageBrowse = null != formDataSessionAttributeName;
        if (returningFromImageBrowse) {
            formData = getFormDataFromSession(req, formDataSessionAttributeName);
            newCategory = formData.getCategoryToEdit();
            String imageUrl = ImageBrowse.getImageUri(req);
            if (null != imageUrl) {
                newCategory.setImage(imageUrl);
            }
        } else {
            CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest(req, service, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO);

            newCategory = new CategoryDomainObject(0,
                    req.getParameter(PARAMETER__NAME),
                    req.getParameter(PARAMETER__DESCRIPTION),
                    req.getParameter(PARAMETER__ICON),
                    categoryTypeToAddTo);
        }
        formData.setCategoryToEdit(newCategory);

        if (req.getParameter(PARAMETER__BROWSE_FOR_IMAGE) != null) {
            putFormDataInSessionAndForwardToImageBrowse(formData, req, res, PARAMETER_MODE__ADD_CATEGORY);
        } else if (req.getParameter("category_add") != null && !newCategory.getName().trim().equals("")) {
            if (isUniqueName(service, newCategory.getType(), newCategory.getName())) {
                service.getDocumentMapper().addCategoryToDb(newCategory);
                formData.setCategoryToEdit(new CategoryDomainObject(0, "", "", "", null));
                formData.setUniqueName(true);
            } else {
                formData.setUniqueName(false);
                formData.setCategoryTypeToEdit(newCategory.getType());
            }
        }
        return formData ;
    }

    private FormData getFormDataFromSession(HttpServletRequest req, String formDataSessionAttributeName) {
        FormData formData;
        formData = (FormData) req.getSession().getAttribute(formDataSessionAttributeName) ;
        return formData;
    }

    private String getFormDataSessionAttributeNameFromRequest(HttpServletRequest req) {
        String formDataSessionAttributeName = req.getParameter(REQUEST_ATTR_OR_PARAM__FORM_DATA_SESSION_ATTRIBUTE_NAME);
        return formDataSessionAttributeName;
    }

    private CategoryDomainObject getCategoryFromIdInRequest(HttpServletRequest req, IMCServiceInterface service) {
        CategoryDomainObject categoryToEdit = null;
        String categoryIdString = req.getParameter(PARAMETER__CATEGORIES);
        boolean selectCategoryButtonPressed = req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE) == null;
        boolean aCategoryWasSelected = categoryIdString != null && selectCategoryButtonPressed;
        if (aCategoryWasSelected) {
            int categoryId = Integer.parseInt(categoryIdString);
            categoryToEdit = service.getDocumentMapper().getCategoryById(categoryId);
        }
        return categoryToEdit;
    }

    private CategoryTypeDomainObject getCategoryTypeFromRequest(HttpServletRequest req, IMCServiceInterface service, String requestParameter) {
        CategoryTypeDomainObject categoryType = null;
        String categoryTypeIdString = req.getParameter(requestParameter);
        boolean gotCategoryTypeId = null != categoryTypeIdString;
        if (gotCategoryTypeId) {
            int categoryTypeId = Integer.parseInt(categoryTypeIdString);
            categoryType = service.getDocumentMapper().getCategoryTypeById(categoryTypeId);
        }
        return categoryType;
    }

    private void deleteCategory(CategoryDomainObject categoryToEdit, IMCServiceInterface service, HttpServletRequest req, CategoryTypeDomainObject categoryTypeToEdit, FormData formData) {
        CategoryDomainObject categoryToEdit1 = categoryToEdit;
        StringBuffer msg = new StringBuffer("");
        String[] documentsOfOneCategory = null;
        if (categoryToEdit1 != null) {
            documentsOfOneCategory = service.getDocumentMapper().getAllDocumentsOfOneCategory(categoryToEdit1);
            if (req.getParameter("category_delete") != null) {
                DocumentDomainObject document;
                for (int i = 0; i < documentsOfOneCategory.length; i++) {
                    document = service.getDocumentMapper().getDocument(Integer.parseInt(documentsOfOneCategory[i]));
                    service.getDocumentMapper().deleteOneCategoryFromDocument(document, categoryToEdit1);
                }
                service.getDocumentMapper().deleteCategoryFromDb(categoryToEdit1);
                categoryToEdit1 = null;
                documentsOfOneCategory = null;
            }
        }

        formData.setCategoryTypeToEdit(categoryTypeToEdit);
        formData.setCategoryToEdit(categoryToEdit1);
        formData.setDocumentsOfOneCategory(documentsOfOneCategory);
        categoryToEdit = categoryToEdit1;
    }

    private void putFormDataInSessionAndForwardToImageBrowse(FormData formData, HttpServletRequest req, HttpServletResponse res, String callerMode) throws ServletException, IOException {
        String uniqueSessionAttributeName;
        uniqueSessionAttributeName = SESSION_ATTRIBUTE__FORM_DATA + "." + System.currentTimeMillis();
        req.getSession().setAttribute(uniqueSessionAttributeName, formData);

        String returningUrl = "AdminCategories?" +
                callerMode + "=" + "1" + "&"
                + REQUEST_ATTR_OR_PARAM__FORM_DATA_SESSION_ATTRIBUTE_NAME
                + "="
                + uniqueSessionAttributeName;

        req.getRequestDispatcher("ImageBrowse?" + ImageBrowse.PARAMETER__CALLER + "="
                + java.net.URLEncoder.encode(returningUrl)).forward(req, res);
    }

    private void viewCategory(CategoryTypeDomainObject categoryTypeToEdit, FormData formBean, CategoryDomainObject categoryToEdit, HttpServletRequest req) {
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        if (req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE) != null) {
            formBean.setCategoryToEdit(null);
        } else {
            formBean.setCategoryToEdit(categoryToEdit);
        }
    }

    private void deleteCategoryType(CategoryTypeDomainObject categoryTypeToEdit, IMCServiceInterface service, FormData formBean) {
        StringBuffer msg = new StringBuffer("");
        int numberOfCategories = 0;
        if (categoryTypeToEdit != null) {
            numberOfCategories = service.getDocumentMapper().getAllCategoriesOfType(categoryTypeToEdit).length;
            if (numberOfCategories == 0) {
                service.getDocumentMapper().deleteCategoryTypeFromDb(categoryTypeToEdit);
            }
        }
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        formBean.setNumberOfCategories(numberOfCategories);
    }

    private void editCategoryType(HttpServletRequest req, CategoryTypeDomainObject categoryTypeToEdit, IMCServiceInterface service, FormData formBean) {
        if (req.getParameter("category_type_save") != null) {
            categoryTypeToEdit.setName(req.getParameter(PARAMETER__NAME).trim());
            categoryTypeToEdit.setMaxChoices(Integer.parseInt(req.getParameter("max_choices")));
            service.getDocumentMapper().updateCategoryType(categoryTypeToEdit);
        }

        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
    }

    private void addCategoryType(IMCServiceInterface service, HttpServletRequest req) {
        service.getDocumentMapper().addCategoryTypeToDb(req.getParameter(PARAMETER__NAME).trim(), Integer.parseInt(req.getParameter("max_choices")));
    }


    public static String createHtmlOptionListOfCategoryTypes(CategoryTypeDomainObject selectedType) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
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

    public static String createHtmlOptionListOfCategoriesForOneType(CategoryTypeDomainObject categoryType, CategoryDomainObject selectedCategory) {
        IMCServiceInterface service = ApplicationServer.getIMCServiceInterface() ;

        CategoryDomainObject[] categories = service.getDocumentMapper().getAllCategoriesOfType(categoryType);
        String temps = "";
        for (int i = 0; i < categories.length; i++) {
            boolean selected = selectedCategory != null && selectedCategory.equals(categories[i]);
            temps += "<option value=\""
                    + categories[i].getId()
                    + "\""
                    + (selected ? " selected" : "")
                    + ">"
                    + categories[i].getName() + "</option>";
        }
        return temps;
    }

    private boolean isUniqueName(IMCServiceInterface service, CategoryTypeDomainObject categoryType, String categoryName) {

        CategoryDomainObject[] categories = service.getDocumentMapper().getAllCategoriesOfType(categoryType);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].getName().toLowerCase().equals(categoryName.trim().toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public static class FormData {

        private CategoryTypeDomainObject categoryTypeToEdit;
        private CategoryDomainObject categoryToEdit;
        private int numberOfCategories;
        private String[] documentsOfOneCategory;
        private boolean uniqueName;


        public CategoryDomainObject getCategoryToEdit() {
            return categoryToEdit;
        }

        private void setCategoryToEdit(CategoryDomainObject categoryToEdit) {
            this.categoryToEdit = categoryToEdit;
        }

        public CategoryTypeDomainObject getCategoryTypeToEdit() {
            return categoryTypeToEdit;
        }

        private void setCategoryTypeToEdit(CategoryTypeDomainObject categoryTypeToEdit) {
            this.categoryTypeToEdit = categoryTypeToEdit;
        }

        public int getNumberOfCategories() {
            return numberOfCategories;
        }

        private void setNumberOfCategories(int numberOfCategories) {
            this.numberOfCategories = numberOfCategories;
        }

        public String[] getDocumentsOfOneCategory() {
            return documentsOfOneCategory;
        }

        private void setDocumentsOfOneCategory(String[] documentsOfOneCategory) {
            this.documentsOfOneCategory = documentsOfOneCategory;
        }

        public boolean getUniqueName() {
            return uniqueName;
        }

        private void setUniqueName(boolean bool) {
            uniqueName = bool;
        }
    }

}
