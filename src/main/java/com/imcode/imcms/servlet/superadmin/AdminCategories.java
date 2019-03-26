package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.mapping.CategoryMapper;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.CategoryType;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class AdminCategories extends HttpServlet {

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
    public static final String PARAMETER__MULTI_SELECT = "is_multi_select";
    public static final String PARAMETER_CATEGORY_TYPE_SAVE = "category_type_save";
    public static final String PARAMETER_CATEGORY_TYPE_ADD = "category_type_add";
    public static final String PARAMETER__INHERITED = "inherited";
    public static final String PARAMETER__CATEGORY_DELETE = "category_delete";
    private static final String JSP_TEMPLATE = "category_admin.jsp";
    private static final String PARAMETER__DESCRIPTION = "description";
    private static final String PARAMETER__CATEGORIES = "categories";
    private static final String PARAMETER__OLD_NAME = "oldName";
    private static final String PARAMETER__CATEGORY_SAVE = "category_save";
    private static final String PARAMETER__ADD_CATEGORY_BUTTON = "category_add";

    public static String createHtmlOptionListOfCategoryTypes(CategoryType selectedType) {
        final ImcmsServices imcref = Imcms.getServices();
        final CategoryTypeDomainObject[] categoryTypes = imcref.getCategoryMapper().getAllCategoryTypes();
        final StringBuilder temps = new StringBuilder();

        for (CategoryTypeDomainObject categoryType : categoryTypes) {
            boolean selected = (selectedType != null) && (Objects.equals(selectedType.getId(), categoryType.getId()));
            temps.append("<option value=\"")
                    .append(categoryType.getId())
                    .append("\"")
                    .append(selected ? " selected" : "")
                    .append(">")
                    .append(categoryType.getName())
                    .append("</option>");
        }
        return temps.toString();
    }

    public static String createHtmlOptionListOfCategoriesForOneType(CategoryTypeDomainObject categoryType,
                                                                    CategoryDomainObject selectedCategory) {
        final CategoryMapper categoryMapper = Imcms.getServices().getCategoryMapper();
        final CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType(categoryType);
        final StringBuilder temps = new StringBuilder();

        for (CategoryDomainObject category : categories) {
            final boolean selected = selectedCategory != null && selectedCategory.equals(category);
            temps.append("<option value=\"")
                    .append(category.getId())
                    .append("\"")
                    .append(selected ? " selected" : "")
                    .append(">")
                    .append(category.getName())
                    .append("</option>");
        }
        return temps.toString();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Lets verify that the user who tries to add a new user is an admin
        ImcmsServices service = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser(req);
        Utility.setDefaultHtmlContentType(res);
        if (!user.isSuperAdmin()) {
            String header = "Error in AdminCategories. ";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            AdminRoles.printErrorMessage(req, res, header, msg);
            return;
        }

        if (null != req.getParameter(PARAMETER_BUTTON__CANCEL)
                && null != req.getParameter(PARAMETER_MODE__DEFAULT))
        {
            res.sendRedirect("AdminManager");
            return;
        }

        CategoryMapper categoryMapper = service.getCategoryMapper();

        AdminCategoriesPage adminCategoriesPage = new AdminCategoriesPage();

        CategoryTypeDomainObject categoryType = getCategoryTypeFromIdParameterInRequest(req, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW, categoryMapper);
        CategoryDomainObject category = getCategoryFromIdInRequest(req, categoryMapper);

        if (null != req.getParameter(PARAMETER_MODE__ADD_CATEGORY_TYPE)) {
            addCategoryType(req, adminCategoriesPage, categoryMapper);
        } else if (null != req.getParameter(PARAMETER_MODE__EDIT_CATEGORY_TYPE)) {
            editCategoryType(categoryType, req, adminCategoriesPage, categoryMapper);
        } else if (null != req.getParameter(PARAMETER_MODE__DELETE_CATEGORY_TYPE)) {
            deleteCategoryType(categoryType, adminCategoriesPage, categoryMapper);
        } else if (null != req.getParameter(PARAMETER_MODE__ADD_CATEGORY)) {
            adminCategoriesPage = addCategory(req, res, adminCategoriesPage, categoryMapper);
        } else if (null != req.getParameter(PARAMETER_MODE__EDIT_CATEGORY)) {
            adminCategoriesPage = editCategory(req, res, adminCategoriesPage, categoryMapper);
        } else if (req.getParameter(PARAMETER_MODE__DELETE_CATEGORY) != null) {
            deleteCategory(category, categoryType, req, adminCategoriesPage, categoryMapper, service.getDocumentMapper());
        } else if (null != req.getParameter(PARAMETER_MODE__VIEW_CATEGORY)) {
            viewCategory(categoryType, category, req, adminCategoriesPage);
        }

        if (!res.isCommitted()) {
            forward(adminCategoriesPage, user, req, res);
        }
    }

    private void forward(AdminCategoriesPage formBean, UserDomainObject user, HttpServletRequest req,
                         HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute(ATTRIBUTE__FORM_DATA, formBean);
        req.getRequestDispatcher("/imcms/" + user.getLanguage() + "/jsp/" + JSP_TEMPLATE).forward(req, res);
    }

    private AdminCategoriesPage editCategory(HttpServletRequest req, HttpServletResponse res, AdminCategoriesPage formBean,
                                             CategoryMapper categoryMapper) {
        formBean.setMode(PARAMETER_MODE__EDIT_CATEGORY);

        CategoryDomainObject category = getCategoryFromIdInRequest(req, categoryMapper);
        CategoryTypeDomainObject categoryTypeToEdit = getCategoryTypeFromIdParameterInRequest(req, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW, categoryMapper);

        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        if (req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE) != null) {
            formBean.setCategoryToEdit(null);
        } else {
            formBean.setCategoryToEdit(category);
        }

        boolean nameIsUnique = true;

        if (req.getParameter(PARAMETER__CATEGORY_SAVE) != null) {
            boolean nameWasChanged = !req.getParameter(PARAMETER__OLD_NAME).toLowerCase().equals(req.getParameter(PARAMETER__NAME).toLowerCase());
            CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromIdParameterInRequest(req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, categoryMapper);
            if (nameWasChanged) {
                nameIsUnique = null == categoryMapper.getCategoryByTypeAndName(categoryTypeToAddTo, req.getParameter(PARAMETER__NAME));
            }
            if (nameIsUnique) {
                setCategoryFromRequest(category, req, categoryMapper);
                categoryMapper.updateCategory(category);
                formBean.setCategoryToEdit(null);
            }
        }

        formBean.setUniqueCategoryName(nameIsUnique);
        return formBean;
    }

    private void setCategoryFromRequest(CategoryDomainObject category, HttpServletRequest req,
                                        CategoryMapper categoryMapper) {
        category.setName(req.getParameter(PARAMETER__NAME));
        category.setDescription(req.getParameter(PARAMETER__DESCRIPTION));
        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromIdParameterInRequest(req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, categoryMapper);
        category.setType(categoryTypeToAddTo);
    }

    private AdminCategoriesPage addCategory(HttpServletRequest req, HttpServletResponse res, AdminCategoriesPage adminCategoriesPage,
                                            CategoryMapper categoryMapper) {
        adminCategoriesPage.setMode(PARAMETER_MODE__ADD_CATEGORY);

        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromIdParameterInRequest(req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, categoryMapper);

        CategoryDomainObject newCategory = new CategoryDomainObject(0,
                req.getParameter(PARAMETER__NAME),
                req.getParameter(PARAMETER__DESCRIPTION),
                categoryTypeToAddTo);
        adminCategoriesPage.setCategoryToEdit(newCategory);
        adminCategoriesPage.setCategoryTypeToEdit(categoryTypeToAddTo);

        if (null != req.getParameter(PARAMETER__ADD_CATEGORY_BUTTON) && StringUtils.isNotBlank(newCategory.getName())) {
            categoryMapper.addCategory(newCategory);
            adminCategoriesPage.setCategoryToEdit(new CategoryDomainObject(0, null, "", null));
            adminCategoriesPage.setUniqueCategoryName(true);
        }
        return adminCategoriesPage;
    }

    private CategoryDomainObject getCategoryFromIdInRequest(HttpServletRequest req, CategoryMapper categoryMapper) {
        CategoryDomainObject categoryToEdit = null;
        String categoryIdString = req.getParameter(PARAMETER__CATEGORIES);
        boolean selectCategoryButtonPressed = req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE)
                == null;
        boolean aCategoryWasSelected = categoryIdString != null && selectCategoryButtonPressed;
        if (aCategoryWasSelected) {
            int categoryId = Integer.parseInt(categoryIdString);
            categoryToEdit = categoryMapper.getCategoryById(categoryId);
        }
        return categoryToEdit;
    }

    private CategoryTypeDomainObject getCategoryTypeFromIdParameterInRequest(HttpServletRequest req, String requestParameter,
                                                                             CategoryMapper categoryMapper) {
        CategoryTypeDomainObject categoryType = null;
        String categoryTypeIdString = req.getParameter(requestParameter);
        boolean gotCategoryTypeId = null != categoryTypeIdString;
        if (gotCategoryTypeId) {
            int categoryTypeId = Integer.parseInt(categoryTypeIdString);
            categoryType = categoryMapper.getCategoryTypeById(categoryTypeId);
        }
        return categoryType;
    }

    private void deleteCategory(CategoryDomainObject categoryToEdit, CategoryTypeDomainObject categoryTypeToEdit,
                                HttpServletRequest req, AdminCategoriesPage adminCategoriesPage,
                                CategoryMapper categoryMapper,
                                DocumentMapper documentMapper) {

        adminCategoriesPage.setMode(PARAMETER_MODE__DELETE_CATEGORY);
        List<Integer> documentsOfOneCategory = null;

        if (categoryToEdit != null) {
            documentsOfOneCategory = categoryMapper.getAllDocumentsOfOneCategory(categoryToEdit);

            if (req.getParameter(PARAMETER__CATEGORY_DELETE) != null) {
                DocumentDomainObject document;

                for (Integer documentsCategoryId : documentsOfOneCategory) {
                    document = documentMapper.getDocument(documentsCategoryId);
                    categoryMapper.deleteOneCategoryFromDocument(document, categoryToEdit);
                }

                categoryMapper.deleteCategoryFromDb(categoryToEdit);
                categoryToEdit = null;
                documentsOfOneCategory = null;
            }
        }

        adminCategoriesPage.setCategoryTypeToEdit(categoryTypeToEdit);
        adminCategoriesPage.setCategoryToEdit(categoryToEdit);
        adminCategoriesPage.setDocumentsOfOneCategory(documentsOfOneCategory);
    }

    private void viewCategory(CategoryTypeDomainObject categoryTypeToEdit, CategoryDomainObject categoryToEdit,
                              HttpServletRequest req, AdminCategoriesPage formBean) {
        formBean.setMode(PARAMETER_MODE__VIEW_CATEGORY);
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        if (req.getParameter(PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE) != null) {
            formBean.setCategoryToEdit(null);
        } else {
            formBean.setCategoryToEdit(categoryToEdit);
        }
    }

    private void deleteCategoryType(CategoryTypeDomainObject categoryTypeToEdit, AdminCategoriesPage formBean,
                                    CategoryMapper categoryMapper) {
        formBean.setMode(PARAMETER_MODE__DELETE_CATEGORY_TYPE);
        int numberOfCategories = 0;
        if (categoryTypeToEdit != null) {
            numberOfCategories = categoryMapper.getAllCategoriesOfType(categoryTypeToEdit).length;
            if (numberOfCategories == 0) {
                categoryMapper.deleteCategoryTypeFromDb(categoryTypeToEdit);
            }
        }
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
        formBean.setNumberOfCategories(numberOfCategories);
    }

    private void editCategoryType(CategoryTypeDomainObject categoryTypeToEdit, HttpServletRequest req,
                                  AdminCategoriesPage formBean, CategoryMapper categoryMapper) {
        formBean.setMode(PARAMETER_MODE__EDIT_CATEGORY_TYPE);
        formBean.setUniqueCategoryTypeName(true);

        if (req.getParameter(PARAMETER_CATEGORY_TYPE_SAVE) != null) {
            String newName = req.getParameter(PARAMETER__NAME).trim();
            if (!newName.equals(categoryTypeToEdit.getName())) {
                formBean.setUniqueCategoryTypeName(categoryMapper.isUniqueCategoryTypeName(newName));
            }
            if (formBean.isUniqueCategoryTypeName()) {
                int maxChoices = Integer.parseInt(req.getParameter(PARAMETER__MULTI_SELECT));
                categoryTypeToEdit.setName(newName);
                categoryTypeToEdit.setMultiSelect(maxChoices == 0);
                boolean inherited = getInheritedParameterFromRequest(req);
                categoryTypeToEdit.setInherited(inherited);
                categoryMapper.updateCategoryType(categoryTypeToEdit);
            }
        }
        formBean.setCategoryTypeToEdit(categoryTypeToEdit);
    }

    private void addCategoryType(HttpServletRequest req, AdminCategoriesPage formBean, CategoryMapper categoryMapper) {
        formBean.setMode(PARAMETER_MODE__ADD_CATEGORY_TYPE);
        if (req.getParameter(PARAMETER_CATEGORY_TYPE_ADD) != null
                && !req.getParameter(PARAMETER__NAME).trim().equals(""))
        {

            CategoryTypeDomainObject categoryType = createCategoryTypeFromRequest(req);

            if (categoryMapper.isUniqueCategoryTypeName(categoryType.getName())) {
                formBean.setUniqueCategoryTypeName(true);
                categoryMapper.addCategoryTypeToDb(categoryType);
            } else {
                formBean.setUniqueCategoryTypeName(false);
            }
        }
    }

    private CategoryTypeDomainObject createCategoryTypeFromRequest(HttpServletRequest req) {
        String categoryTypeName = req.getParameter(PARAMETER__NAME).trim();
        boolean multiselect = Integer.parseInt(req.getParameter(PARAMETER__MULTI_SELECT)) == 0;
        boolean inherited = getInheritedParameterFromRequest(req);
        CategoryTypeDomainObject categoryType = new CategoryTypeDomainObject(0, categoryTypeName, multiselect, inherited);
        return categoryType;
    }

    private boolean getInheritedParameterFromRequest(HttpServletRequest request) {
        return null != request.getParameter(PARAMETER__INHERITED);
    }

    public static class AdminCategoriesPage {

        private CategoryTypeDomainObject categoryTypeToEdit;
        private CategoryDomainObject categoryToEdit;
        private int numberOfCategories;
        private List<Integer> documentsOfOneCategory;
        private boolean uniqueCategoryName;
        private String mode;
        private boolean uniqueCategoryTypeName;

        public boolean isUniqueCategoryTypeName() {
            return uniqueCategoryTypeName;
        }

        public void setUniqueCategoryTypeName(boolean uniqueCategoryTypeName) {
            this.uniqueCategoryTypeName = uniqueCategoryTypeName;
        }

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

        public List<Integer> getDocumentsOfOneCategory() {
            return documentsOfOneCategory;
        }

        private void setDocumentsOfOneCategory(List<Integer> documentsOfOneCategory) {
            this.documentsOfOneCategory = documentsOfOneCategory;
        }

        public boolean getUniqueCategoryName() {
            return uniqueCategoryName;
        }

        private void setUniqueCategoryName(boolean bool) {
            uniqueCategoryName = bool;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }

}
