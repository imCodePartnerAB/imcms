package com.imcode.imcms.servlet.apis;

import com.imcode.imcms.util.JSONUtils;
import imcode.server.document.DocumentPermissionSetTypeDomainObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shadowgun on 14.04.2015.
 */
public class PermissionApiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONUtils.defaultJSONAnswer(resp, Stream
                .of(DocumentPermissionSetTypeDomainObject.values())
                .collect(
                        Collectors.toMap(
                                DocumentPermissionSetTypeDomainObject::getId,
                                DocumentPermissionSetTypeDomainObject::getName
                        )
                ));
    }
}
