package com.imcode.imcms.servlet.admin;

import com.imcode.imcms.servlet.WebComponent;
import imcode.server.user.UserDomainObject;
import imcode.util.HttpSessionUtils;
import com.imcode.imcms.util.l10n.LocalizedMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public class UserFinder extends WebComponent {

    private boolean usersAddable;
    private boolean nullSelectable ;
    private LocalizedMessage selectButtonText ;
    private LocalizedMessage headline ;

    private SelectUserCommand selectUserCommand;

    public static UserFinder getInstance( HttpServletRequest request ) {
        UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null == userFinder ) {
            userFinder = new UserFinder();
        }
        return userFinder;
    }

    public void setUsersAddable( boolean usersAddable ) {
        this.usersAddable = usersAddable;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        HttpSessionUtils.setSessionAttributeAndSetNameInRequestAttribute( this, request, UserBrowser.REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        UserBrowser.UserBrowserPage userBrowserPage = new UserBrowser.UserBrowserPage();
        userBrowserPage.forward( request, response );
    }

    public void setSelectButtonText( LocalizedMessage buttonText ) {
        this.selectButtonText = buttonText;
    }

    public LocalizedMessage getSelectButtonText() {
        return selectButtonText;
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

    public LocalizedMessage getHeadline() {
        return headline;
    }

    public void setHeadline( LocalizedMessage headline ) {
        this.headline = headline;
    }

    public static interface SelectUserCommand extends Serializable {

        void selectUser( UserDomainObject selectedUser, HttpServletRequest request,
                                HttpServletResponse response ) throws ServletException, IOException ;
    }
}
