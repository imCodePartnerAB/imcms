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
import com.imcode.imcms.servlet.admin.AdminDoc;

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

    public static final String PARAMETER__RETURNING_FROM_IMAGE_BROWSE = "returningFromImageBrowse";
    public static final String PARAMETER__IMAGE_BROWSE_ORIGINAL_ACTION = "imageBrowse.originalAction";
    public static final String REQUEST_ATTR_OR_PARAM__CATEGORY_SESSION_ATTRIBUTE_NAME = "category.sessionAttributeName";
    private static final String PARAMETER__CATEGORY_TYPE_ADD = "category_type_add";
    private static final String PARAMETER__NAME = "name";
    private static final String PARAMETER__DESCRIPTION = "description";
    private static final String SESSION_ATTRIBUTE__CATEGORY = "category";
    public static final String PARAMETER_BUTTON__SELECT_CATEGORY_TYPE = "button__select_category_type";
    private static final String PARAMETER__ICON = "icon";
    private static final String PARAMETER__CATEGORIES = "categories";
    public static final String PARAMETER_SELECT__SELECT_CATEGORY_TYPE = "select__select_category_type";
    public static final String PARAMETER__ADD_TO_CATEGORY_TYPE = "add_to_category_type";


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

        if (req.getParameter("cancel") != null && req.getParameter("adminMode") != null) {
            if (req.getParameter("adminMode").equals("")) {
                Utility.redirect(req, res, "AdminManager");
                return;
            }
        }

        FormData formData = new FormData();
        String adminMode = req.getParameter("adminMode") == null ? "default" : req.getParameter("adminMode");

        CategoryTypeDomainObject categoryTypeToEdit = null;
        if (req.getParameter(PARAMETER_SELECT__SELECT_CATEGORY_TYPE) != null) {
            categoryTypeToEdit = service.getDocumentMapper().getCategoryTypeById(Integer.parseInt(req.getParameter(PARAMETER_SELECT__SELECT_CATEGORY_TYPE)));
        }

        CategoryDomainObject categoryToEdit = null;
        if (req.getParameter(PARAMETER__CATEGORIES) != null && req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE) == null) {
            categoryToEdit = service.getDocumentMapper().getCategoryById(Integer.parseInt(req.getParameter("categories")));
        }

        // ----  Add category type  ---------
        if (req.getParameter(PARAMETER__CATEGORY_TYPE_ADD) != null && !req.getParameter(PARAMETER__NAME).trim().equals("")) {
            addCategoryType(service, req);
        } else if (req.getParameter("edit_category_type") != null ||
                adminMode.equals("editCategoryTypeMode")) {

            editCategoryType(req, categoryTypeToEdit, service, formData);

        } else if (req.getParameter("delete_category_type") != null ||
                adminMode.equals("deleteCategoryTypeMode")) {

            deleteCategoryType(categoryTypeToEdit, service, formData);
        } else if (req.getParameter("add_category") != null || adminMode.equals("addCategoryMode")) {

            CategoryTypeDomainObject categoryTypeToAddTo = null;

            String categoryTypeIdString = req.getParameter(PARAMETER__ADD_TO_CATEGORY_TYPE);

            boolean gotCategoryTypeId = null != categoryTypeIdString;
            if (gotCategoryTypeId) {
                int categoryTypeId = Integer.parseInt(categoryTypeIdString);
                categoryTypeToAddTo = service.getDocumentMapper().getCategoryTypeById(categoryTypeId);
            }
            String uniqueSessionAttributeName = req.getParameter(REQUEST_ATTR_OR_PARAM__CATEGORY_SESSION_ATTRIBUTE_NAME);

            boolean returningFromImageBrowse = null != uniqueSessionAttributeName;
            if (returningFromImageBrowse) {
                CategoryDomainObject newCategory = (CategoryDomainObject) req.getSession().getAttribute(uniqueSessionAttributeName);
                String imageUrl = AdminDoc.getImageUri( req );
                newCategory.setImage(imageUrl);
                formData.setCategoryToEdit(newCategory);
            } else if (req.getParameter("browseForMenuImage") != null) {

                CategoryDomainObject newCategory = new CategoryDomainObject(0,
                        req.getParameter(PARAMETER__NAME),
                        req.getParameter(PARAMETER__DESCRIPTION),
                        null,
                        categoryTypeToAddTo);

                uniqueSessionAttributeName = SESSION_ATTRIBUTE__CATEGORY + "." + System.currentTimeMillis();
                req.getSession().setAttribute(uniqueSessionAttributeName, newCategory);

                String returningUrl = "AdminCategories?" +
                        "add_category" + "=" + "1" + "&"
                        + REQUEST_ATTR_OR_PARAM__CATEGORY_SESSION_ATTRIBUTE_NAME
                        + "="
                        + uniqueSessionAttributeName;

                req.getRequestDispatcher("ImageBrowse?" + ImageBrowse.PARAMETER__CALLER + "="
                        + java.net.URLEncoder.encode(returningUrl)).forward(req, res);
                return;
            } else {
                addCategory(req, service, categoryTypeToAddTo, formData);
            }
        } else if (req.getParameter("edit_category") != null ||
                adminMode.equals("editCategoryMode")) {

            categoryToEdit = editCategory(req, service, categoryToEdit, categoryTypeToEdit, formData);

        } else if (req.getParameter("delete_category") != null ||
                adminMode.equals("deleteCategoryMode")) {

            categoryToEdit = deleteCategory(categoryToEdit, service, req, categoryTypeToEdit, formData);

        } else if (req.getParameter("view_category") != null ||
                adminMode.equals("showCategoryMode")) {

            viewCategory(categoryTypeToEdit, formData, service, categoryToEdit, req);

        }

        formData.setAdminMode(adminMode);
        req.setAttribute(ATTRIBUTE__FORM_DATA, formData);
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP_TEMPLATE);
        rd.forward(req, res);
    }

    private void addCategory(HttpServletRequest req, IMCServiceInterface service, CategoryTypeDomainObject categoryTypeToAddTo, FormData formBean) {
        if (req.getParameter("category_add") != null && !req.getParameter(PARAMETER__NAME).trim().equals("")) {
            if (isUniqueName(service, categoryTypeToAddTo, req.getParameter(PARAMETER__NAME))) {
                service.getDocumentMapper().addCategoryToDb(categoryTypeToAddTo, req.getParameter(PARAMETER__NAME).trim(), req.getParameter(PARAMETER__DESCRIPTION), req.getParameter(PARAMETER__ICON));
                formBean.setUniqueName(true);
            } else {
                formBean.setUniqueName(false);
                formBean.setCategoryTypeToEdit(categoryTypeToAddTo);
            }
        }
        formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToAddTo));
    }

    private void viewCategory(CategoryTypeDomainObject categoryTypeToEdit, FormData formBean, IMCServiceInterface service, CategoryDomainObject categoryToEdit, HttpServletRequest req) {
        if (categoryTypeToEdit != null) {
            formBean.setCategoriesOptionList(createHtmlOptionListOfCategoriesForOneType(service, categoryTypeToEdit, categoryToEdit != null ? categoryToEdit.getId() + "" : null));
        }

        formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        if (req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE) != null) {
            formBean.setCategoryToEdit(null);
        } else {
            formBean.setCategoryToEdit(categoryToEdit);
        }
    }

    private CategoryDomainObject deleteCategory(CategoryDomainObject categoryToEdit, IMCServiceInterface service, HttpServletRequest req, CategoryTypeDomainObject categoryTypeToEdit, FormData formBean) {
        StringBuffer msg = new StringBuffer("");
        String[] documentsOfOneCategory = null;
        if (categoryToEdit != null) {
            documentsOfOneCategory = service.getDocumentMapper().getAllDocumentsOfOneCategory(categoryToEdit);
            if (req.getParameter("category_delete") != null) {
                DocumentDomainObject document;
                for (int i = 0; i < documentsOfOneCategory.length; i++) {
                    document = service.getDocumentMapper().getDocument(Integer.parseInt(documentsOfOneCategory[i]));
                    service.getDocumentMapper().deleteOneCategoryFromDocument(document, categoryToEdit);
                }
                service.getDocumentMapper().deleteCategoryFromDb(categoryToEdit);
                categoryToEdit = null;
                documentsOfOneCategory = null;
            }
        }

        if (categoryTypeToEdit != null) {
            formBean.setCategoriesOptionList(createHtmlOptionListOfCategoriesForOneType(service, categoryTypeToEdit, categoryToEdit != null ? categoryToEdit.getId() + "" : null));
        }
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        formBean.setCategoryToEdit(categoryToEdit);
        formBean.setDocumentsOfOneCategory(documentsOfOneCategory);
        formBean.setMsgToUser(msg.toString());
        formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
        return categoryToEdit;
    }

    private CategoryDomainObject editCategory(HttpServletRequest req, IMCServiceInterface service, CategoryDomainObject categoryToEdit, CategoryTypeDomainObject categoryTypeToEdit, FormData formBean) {
        boolean nameIsUnique = true;
        if (req.getParameter("category_save") != null) {
            CategoryTypeDomainObject categoryTypeToAddTo = null;
            if (req.getParameter(PARAMETER__ADD_TO_CATEGORY_TYPE) != null) {
                categoryTypeToAddTo = service.getDocumentMapper().getCategoryTypeById(Integer.parseInt(req.getParameter(PARAMETER__ADD_TO_CATEGORY_TYPE)));
            }
            if (!req.getParameter("oldName").toLowerCase().equals(req.getParameter(PARAMETER__NAME).toLowerCase())) {
                if (!isUniqueName(service, categoryTypeToAddTo, req.getParameter(PARAMETER__NAME))) {
                    nameIsUnique = false;
                }
            }
            if (nameIsUnique) {
                categoryToEdit.setName(req.getParameter(PARAMETER__NAME));
                categoryToEdit.setType(categoryTypeToAddTo);
                categoryToEdit.setImage(req.getParameter(PARAMETER__ICON));
                categoryToEdit.setDescription(req.getParameter(PARAMETER__DESCRIPTION));
                service.getDocumentMapper().updateCategory(categoryToEdit);
                categoryToEdit = null;
            }
        }

        if (categoryTypeToEdit != null) {
            formBean.setCategoriesOptionList(createHtmlOptionListOfCategoriesForOneType(service, categoryTypeToEdit, categoryToEdit != null ? categoryToEdit.getId() + "" : null));
        }
        formBean.setUniqueName(nameIsUnique);
        formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        if (req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE) != null) {
            formBean.setCategoryToEdit(null);
        } else {
            formBean.setCategoryToEdit(categoryToEdit);
        }
        return categoryToEdit;
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
        formBean.setMsgToUser(msg.toString());
        formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
    }

    private void editCategoryType(HttpServletRequest req, CategoryTypeDomainObject categoryTypeToEdit, IMCServiceInterface service, FormData formBean) {
        if (req.getParameter("category_type_save") != null) {
            categoryTypeToEdit.setName(req.getParameter(PARAMETER__NAME).trim());
            categoryTypeToEdit.setMaxChoices(Integer.parseInt(req.getParameter("max_choices")));
            service.getDocumentMapper().updateCategoryType(categoryTypeToEdit);
        }

        formBean.setCategoryTypesOptionList(createHtmlOptionListOfCategoryTypes(service, categoryTypeToEdit));
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
    }

    private void addCategoryType(IMCServiceInterface service, HttpServletRequest req) {
        service.getDocumentMapper().addCategoryTypeToDb(req.getParameter(PARAMETER__NAME).trim(), Integer.parseInt(req.getParameter("max_choices")));
    }


    private String createHtmlOptionListOfCategoryTypes(IMCServiceInterface imcref, CategoryTypeDomainObject selectedType) {

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

    private String createHtmlOptionListOfCategoriesForOneType(IMCServiceInterface service, CategoryTypeDomainObject categoryType, String selectedCategory) {

        CategoryDomainObject[] categories = service.getDocumentMapper().getAllCategoriesOfType(categoryType);
        String temps = "";
        for (int i = 0; i < categories.length; i++) {
            boolean selected = selectedCategory != null && selectedCategory.equals("" + categories[i].getId());
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

        public void setUniqueName(boolean bool) {
            uniqueName = bool;
        }
    }

}
