package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.servlet.admin.ImageBrowser;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.CategoryDomainObject;
import imcode.server.document.CategoryTypeDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class AdminCategories extends HttpServlet {

    private static final String JSP_TEMPLATE = "category_admin.jsp";
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
    private static final String PARAMETER__ADD_CATEGORY_BUTTON = "category_add";

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doPost( request, response );
    }

    protected void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
        // Lets verify that the user who tries to add a new user is an admin
        ImcmsServices service = Imcms.getServices();
        UserDomainObject user = Utility.getLoggedOnUser( req );
        if ( !user.isSuperAdmin() ) {
            String header = "Error in AdminCategories. ";
            Properties langproperties = service.getLanguageProperties( user );
            String msg = langproperties.getProperty( "error/servlet/global/no_administrator" ) + "<br>";
            this.log( header + "- user is not an administrator" );
            new AdminError( req, res, header, msg );
            return;
        }

        if ( null != req.getParameter( PARAMETER_BUTTON__CANCEL )
             && null != req.getParameter( PARAMETER_MODE__DEFAULT ) ) {
            res.sendRedirect( "AdminManager" );
            return;
        }

        DocumentMapper documentMapper = service.getDocumentMapper();

        Page page = new Page();

        CategoryTypeDomainObject categoryType = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW, documentMapper );
        CategoryDomainObject category = getCategoryFromIdInRequest( req, documentMapper );

        if ( req.getParameter( PARAMETER_CATEGORY_TYPE_ADD ) != null
             && !req.getParameter( PARAMETER__NAME ).trim().equals( "" ) ) {
            addCategoryType( req, page, documentMapper );
        } else if ( null != req.getParameter( PARAMETER_MODE__EDIT_CATEGORY_TYPE ) ) {
            editCategoryType( categoryType, req, page, documentMapper );
        } else if ( null != req.getParameter( PARAMETER_MODE__DELETE_CATEGORY_TYPE ) ) {
            deleteCategoryType( categoryType, page, documentMapper );
        } else if ( null != req.getParameter( PARAMETER_MODE__ADD_CATEGORY ) ) {
            page = addCategory( req, res, page, documentMapper );
        } else if ( null != req.getParameter( PARAMETER_MODE__EDIT_CATEGORY ) ) {
            page = editCategory( req, res, page, documentMapper );
        } else if ( req.getParameter( PARAMETER_MODE__DELETE_CATEGORY ) != null ) {
            deleteCategory( category, categoryType, req, page, documentMapper );
        } else if ( null != req.getParameter( PARAMETER_MODE__VIEW_CATEGORY ) ) {
            viewCategory( categoryType, category, req, page );
        }

        if ( !res.isCommitted() ) {
            forward( page, user, req, res );
        }
    }

    private void forward( Page formBean, UserDomainObject user, HttpServletRequest req,
                          HttpServletResponse res ) throws ServletException, IOException {
        req.setAttribute( ATTRIBUTE__FORM_DATA, formBean );
        req.getRequestDispatcher( "/imcms/" + user.getLanguageIso639_2() + "/jsp/" + JSP_TEMPLATE ).forward( req, res );
    }

    private Page editCategory( HttpServletRequest req, HttpServletResponse res, Page formBean,
                                   DocumentMapper documentMapper ) throws ServletException, IOException {
        formBean.setMode(PARAMETER_MODE__EDIT_CATEGORY) ;

        CategoryDomainObject category = null;
        CategoryTypeDomainObject categoryTypeToEdit = null;
        category = getCategoryFromIdInRequest( req, documentMapper );
        categoryTypeToEdit = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_SHOW, documentMapper );

        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
        if ( req.getParameter( PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE ) != null ) {
            formBean.setCategoryToEdit( null );
        } else {
            formBean.setCategoryToEdit( category );
        }

        boolean nameIsUnique = true;

        if ( req.getParameter( PARAMETER__BROWSE_FOR_IMAGE ) != null ) {
            setCategoryFromRequest( category, req, documentMapper );
            forwardToImageBrowse( formBean, req, res );
        } else if ( req.getParameter( PARAMETER__CATEGORY_SAVE ) != null ) {
            boolean nameWasChanged = !req.getParameter( PARAMETER__OLD_NAME ).toLowerCase().equals( req.getParameter( PARAMETER__NAME ).toLowerCase() );
            CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, documentMapper );
            if ( nameWasChanged ) {
                nameIsUnique = !categoryTypeToAddTo.hasCategoryWithName( req.getParameter( PARAMETER__NAME ) );
            }
            if ( nameIsUnique ) {
                setCategoryFromRequest( category, req, documentMapper );
                documentMapper.updateCategory( category );
                formBean.setCategoryToEdit( null );
            }
        }

        formBean.setUniqueCategoryName( nameIsUnique );
        return formBean;
    }

    private void setCategoryFromRequest( CategoryDomainObject category, HttpServletRequest req,
                                         DocumentMapper documentMapper ) {
        category.setName( req.getParameter( PARAMETER__NAME ) );
        category.setDescription( req.getParameter( PARAMETER__DESCRIPTION ) );
        category.setImageUrl( req.getParameter( PARAMETER__ICON ) );
        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, documentMapper );
        category.setType( categoryTypeToAddTo );
    }

    private Page addCategory( HttpServletRequest req, HttpServletResponse res, Page page,
                                  DocumentMapper documentMapper ) throws ServletException, IOException {
        page.setMode(PARAMETER_MODE__ADD_CATEGORY) ;

        CategoryDomainObject newCategory = null;
        CategoryTypeDomainObject categoryTypeToAddTo = getCategoryTypeFromRequest( req, PARAMETER_SELECT__CATEGORY_TYPE_TO_ADD_TO, documentMapper );

        newCategory = new CategoryDomainObject( 0,
                                                req.getParameter( PARAMETER__NAME ),
                                                req.getParameter( PARAMETER__DESCRIPTION ),
                                                req.getParameter( PARAMETER__ICON ),
                                                categoryTypeToAddTo );
        page.setCategoryToEdit( newCategory );
        page.setCategoryTypeToEdit( categoryTypeToAddTo );

        if ( req.getParameter( PARAMETER__BROWSE_FOR_IMAGE ) != null ) {
            forwardToImageBrowse( page, req, res );
        } else if ( null != req.getParameter( PARAMETER__ADD_CATEGORY_BUTTON ) && StringUtils.isNotBlank( newCategory.getName() ) ) {
            if ( !categoryTypeToAddTo.hasCategoryWithName( newCategory.getName() ) ) {
                documentMapper.addCategoryToDb( newCategory.getType().getId(), newCategory.getName(), newCategory.getDescription(), newCategory.getImageUrl() );
                page.setCategoryToEdit( new CategoryDomainObject( 0, "", "", "", null ) );
                page.setUniqueCategoryName( true );
            }
        }
        return page;
    }

    private CategoryDomainObject getCategoryFromIdInRequest( HttpServletRequest req, DocumentMapper documentMapper ) {
        CategoryDomainObject categoryToEdit = null;
        String categoryIdString = req.getParameter( PARAMETER__CATEGORIES );
        boolean selectCategoryButtonPressed = req.getParameter( PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE )
                                              == null;
        boolean aCategoryWasSelected = categoryIdString != null && selectCategoryButtonPressed;
        if ( aCategoryWasSelected ) {
            int categoryId = Integer.parseInt( categoryIdString );
            categoryToEdit = documentMapper.getCategoryById( categoryId );
        }
        return categoryToEdit;
    }

    private CategoryTypeDomainObject getCategoryTypeFromRequest( HttpServletRequest req, String requestParameter,
                                                                 DocumentMapper documentMapper ) {
        CategoryTypeDomainObject categoryType = null;
        String categoryTypeIdString = req.getParameter( requestParameter );
        boolean gotCategoryTypeId = null != categoryTypeIdString;
        if ( gotCategoryTypeId ) {
            int categoryTypeId = Integer.parseInt( categoryTypeIdString );
            categoryType = documentMapper.getCategoryTypeById( categoryTypeId );
        }
        return categoryType;
    }

    private void deleteCategory( CategoryDomainObject categoryToEdit, CategoryTypeDomainObject categoryTypeToEdit,
                                 HttpServletRequest req, Page page, DocumentMapper documentMapper ) {
        page.setMode(PARAMETER_MODE__DELETE_CATEGORY) ;
        String[] documentsOfOneCategory = null;
        if ( categoryToEdit != null ) {
            documentsOfOneCategory = documentMapper.getAllDocumentsOfOneCategory( categoryToEdit );
            if ( req.getParameter( "category_delete" ) != null ) {
                DocumentDomainObject document;
                for ( int i = 0; i < documentsOfOneCategory.length; i++ ) {
                    document = documentMapper.getDocument( Integer.parseInt( documentsOfOneCategory[i] ) );
                    documentMapper.deleteOneCategoryFromDocument( document, categoryToEdit );
                }
                documentMapper.deleteCategoryFromDb( categoryToEdit );
                categoryToEdit = null;
                documentsOfOneCategory = null;
            }
        }

        page.setCategoryTypeToEdit( categoryTypeToEdit );
        page.setCategoryToEdit( categoryToEdit );
        page.setDocumentsOfOneCategory( documentsOfOneCategory );
    }

    private void forwardToImageBrowse( final Page page, HttpServletRequest request,
                                       HttpServletResponse response ) throws ServletException, IOException {
        ImageBrowser imageBrowser = new ImageBrowser();
        imageBrowser.setSelectImageUrlCommand( new ImageBrowser.SelectImageUrlCommand() {
            public void selectImageUrl( String imageUrl, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
                if (null != imageUrl) {
                    imageUrl = "../images/"+imageUrl ;
                }
                page.getCategoryToEdit().setImageUrl( imageUrl );
                forward( page, Utility.getLoggedOnUser( request ), request, response );
            }
        } );
        imageBrowser.forward( request, response );
    }

    private void viewCategory( CategoryTypeDomainObject categoryTypeToEdit, CategoryDomainObject categoryToEdit,
                               HttpServletRequest req, Page formBean ) {
        formBean.setMode(PARAMETER_MODE__VIEW_CATEGORY) ;
        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
        if ( req.getParameter( PARAMETER_BUTTON__SELECT_CATEGORY_TYPE_TO_SHOW_OR_REMOVE ) != null ) {
            formBean.setCategoryToEdit( null );
        } else {
            formBean.setCategoryToEdit( categoryToEdit );
        }
    }

    private void deleteCategoryType( CategoryTypeDomainObject categoryTypeToEdit, Page formBean,
                                     DocumentMapper documentMapper ) {
        formBean.setMode(PARAMETER_MODE__DELETE_CATEGORY_TYPE) ;
        int numberOfCategories = 0;
        if ( categoryTypeToEdit != null ) {
            numberOfCategories = documentMapper.getAllCategoriesOfType( categoryTypeToEdit ).length;
            if ( numberOfCategories == 0 ) {
                documentMapper.deleteCategoryTypeFromDb( categoryTypeToEdit );
            }
        }
        formBean.setCategoryTypeToEdit( categoryTypeToEdit );
        formBean.setNumberOfCategories( numberOfCategories );
    }

    private void editCategoryType( CategoryTypeDomainObject categoryTypeToEdit, HttpServletRequest req,
                                   Page formBean, DocumentMapper documentMapper ) {
        formBean.setMode(PARAMETER_MODE__EDIT_CATEGORY_TYPE) ;
        if ( req.getParameter( PARAMETER_CATEGORY_TYPE_SAVE ) != null ) {
            String name = req.getParameter( PARAMETER__NAME ).trim();
            if ( documentMapper.isUniqueCategoryTypeName( name ) ) {
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

    private void addCategoryType( HttpServletRequest req, Page formBean, DocumentMapper documentMapper ) {
        formBean.setMode(PARAMETER_MODE__ADD_CATEGORY_TYPE) ;
        String categoryTypeName = req.getParameter( PARAMETER__NAME ).trim();
        int maxChoices = Integer.parseInt( req.getParameter( PARAMETER_MAX_CHOICES ) );

        if ( documentMapper.isUniqueCategoryTypeName( categoryTypeName ) ) {
            formBean.setUniqueCategoryTypeName( true );
            documentMapper.addCategoryTypeToDb( categoryTypeName, maxChoices );
        } else {
            formBean.setUniqueCategoryTypeName( false );
        }
    }

    public static String createHtmlOptionListOfCategoryTypes( CategoryTypeDomainObject selectedType ) {
        ImcmsServices imcref = Imcms.getServices();
        CategoryTypeDomainObject[] categoryTypes = imcref.getDocumentMapper().getAllCategoryTypes();
        String temps = "";
        for ( int i = 0; i < categoryTypes.length; i++ ) {
            boolean selected = selectedType != null && selectedType.getId() == categoryTypes[i].getId();
            temps += "<option value=\""
                     + categoryTypes[i].getId()
                     + "\""
                     + ( selected ? " selected" : "" )
                     + ">"
                     + categoryTypes[i].getName() + "</option>";
        }
        return temps;
    }

    public static String createHtmlOptionListOfCategoriesForOneType( CategoryTypeDomainObject categoryType,
                                                                     CategoryDomainObject selectedCategory ) {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        CategoryDomainObject[] categories = documentMapper.getAllCategoriesOfType( categoryType );
        String temps = "";
        for ( int i = 0; i < categories.length; i++ ) {
            boolean selected = selectedCategory != null && selectedCategory.equals( categories[i] );
            temps += "<option value=\""
                     + categories[i].getId()
                     + "\""
                     + ( selected ? " selected" : "" )
                     + ">"
                     + categories[i].getName() + "</option>";
        }
        return temps;
    }

    public static class Page {

        private CategoryTypeDomainObject categoryTypeToEdit;
        private CategoryDomainObject categoryToEdit;
        private int numberOfCategories;
        private String[] documentsOfOneCategory;
        private boolean uniqueCategoryName;
        private String mode;

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

        public void setMode( String mode ) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }
    }

}
