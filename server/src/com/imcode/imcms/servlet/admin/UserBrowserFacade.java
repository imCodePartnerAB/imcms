package com.imcode.imcms.servlet.admin;

import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.imcode.imcms.servlet.ServletFacade;

public class UserBrowserFacade extends ServletFacade {

    private boolean userSelected;
    private UserDomainObject selectedUser;
    private boolean usersAddable;
    private String searchString;
    private int selectButton;
    private boolean nullSelectable = true ;

    public static final int SELECT_BUTTON__SELECT_USER = UserBrowser.SELECT_BUTTON__SELECT_USER;
    public static final int SELECT_BUTTON__EDIT_USER = UserBrowser.SELECT_BUTTON__EDIT_USER;

    public static UserBrowserFacade getInstance( HttpServletRequest request ) {
        UserBrowserFacade userBrowserFacade = (UserBrowserFacade)HttpSessionUtils.getObjectFromSessionWithKeyInRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null == userBrowserFacade ) {
            userBrowserFacade = new UserBrowserFacade();
        }
        return userBrowserFacade;
    }

    public boolean isUserSelected() {
        return userSelected;
    }

    public UserDomainObject getSelectedUser() {
        return selectedUser;
    }

    public void setUsersAddable( boolean usersAddable ) {
        this.usersAddable = usersAddable;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.addObjectToSessionAndSetSessionAttributeNameInRequest( this, request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        UserBrowser.forwardToJsp( request, response, new UserBrowser.FormData() );
    }

    public void setSelectedUser( UserDomainObject selectedUser ) {
        userSelected = true;
        this.selectedUser = selectedUser;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString( String searchString ) {
        this.searchString = searchString;
    }

    public void setSelectButton( int selectButton ) {
        this.selectButton = selectButton;
    }

    public int getSelectButton() {
        return selectButton;
    }

    public boolean isUsersAddable() {
        return usersAddable;
    }

    public boolean isNullSelectable() {
        return nullSelectable;
    }

    public void setNullSelectable( boolean nullSelectable ) {
        this.nullSelectable = nullSelectable;
    }
}
