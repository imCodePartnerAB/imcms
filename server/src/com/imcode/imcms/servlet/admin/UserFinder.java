package com.imcode.imcms.servlet.admin;

import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.imcode.imcms.servlet.WebComponent;

public class UserFinder extends WebComponent {

    private boolean userSelected;
    private UserDomainObject selectedUser;
    private boolean usersAddable;
    private String searchString;
    private int selectButton;
    private boolean nullSelectable ;

    public static final int SELECT_BUTTON__SELECT_USER = UserBrowser.SELECT_BUTTON__SELECT_USER;
    public static final int SELECT_BUTTON__EDIT_USER = UserBrowser.SELECT_BUTTON__EDIT_USER;
    private SelectUserCommand selectUserCommand;

    public static UserFinder getInstance( HttpServletRequest request ) {
        UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null == userFinder ) {
            userFinder = new UserFinder();
        }
        return userFinder;
    }

    public UserDomainObject getSelectedUser() {
        return selectedUser;
    }

    public void setUsersAddable( boolean usersAddable ) {
        this.usersAddable = usersAddable;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        UserBrowser.forwardToJsp( request, response, new UserBrowser.Page() );
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

    public void selectUser( UserDomainObject selectedUser, HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        this.selectUserCommand.selectUser(selectedUser,request,response) ;
    }

    public void setSelectUserCommand( SelectUserCommand selectUserCommand ) {
        this.selectUserCommand = selectUserCommand;
    }

    public static abstract class SelectUserCommand {

        public abstract void selectUser( UserDomainObject selectedUser, HttpServletRequest request,
                                HttpServletResponse response ) throws ServletException, IOException ;
    }
}
