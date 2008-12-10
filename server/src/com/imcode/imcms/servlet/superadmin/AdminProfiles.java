package com.imcode.imcms.servlet.superadmin;

import com.imcode.db.Database;
import com.imcode.imcms.mapping.ProfileMapper;
import com.imcode.imcms.api.ContentManagementSystem;
import imcode.server.Imcms;
import imcode.server.document.Profile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class AdminProfiles extends HttpServlet {

    public enum Parameter {
        EDIT_PREFIX,
        DELETE_PREFIX,
        PROFILE_ID,
        PROFILE_NAME,
        PROFILE_DOCUMENT_NAME,
        NEW_PROFILE,
        BACK;

        String from(HttpServletRequest request) {
            return request.getParameter(toString());
        }

        public String suffixFrom(HttpServletRequest request) {
            Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
            for ( String parameterName : parameterMap.keySet() ) {
                String prefix = toString();
                if (parameterName.startsWith(prefix)) {
                    return parameterName.substring(prefix.length());
                }
            }
            return null;
        }

        public boolean in(HttpServletRequest request) {
            return null != from(request);
        }
    }

    private Database getDatabase() {
        return Imcms.getServices().getDatabase();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if ("/list".equals(pathInfo)) {
            list(request,response);
        } else if ("/edit".equals(pathInfo)) {
            edit(request, response, new ProfileMapper.SimpleProfile("", "", ""));
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        ProfileMapper profileMapper = getProfileMapper();
        if ("/list".equals(pathInfo)) {
            if (Parameter.BACK.in(request)) {
                response.sendRedirect(request.getContextPath()+"/servlet/AdminManager");
                return;
            }
            String editId = Parameter.EDIT_PREFIX.suffixFrom(request);
            if (null != editId) {
                edit(request, response, profileMapper.get(editId));
                return;
            }
            String deleteId = Parameter.DELETE_PREFIX.suffixFrom(request);
            if (null != deleteId) {
                profileMapper.delete(deleteId);
            }
            if (Parameter.NEW_PROFILE.in(request)) {
                edit(request, response, new ProfileMapper.SimpleProfile("", "", ""));
                return;
            }
            list(request, response);
        } else if ("/edit".equals(pathInfo)) {
            if (Parameter.BACK.in(request)) {
                response.sendRedirect(request.getContextPath()+"/imcms/admin/profile/list");
                return;
            }
            String id = Parameter.PROFILE_ID.from(request);
            String name = Parameter.PROFILE_NAME.from(request);
            String documentName = Parameter.PROFILE_DOCUMENT_NAME.from(request);
            if ( null == ContentManagementSystem.fromRequest(request).getDocumentService().getDocument(documentName) ) {
                edit(request, response, new ProfileMapper.SimpleProfile(id, name, ""));
                return;
            }
            if ( StringUtils.isNotBlank(id) ) {
                profileMapper.update(new ProfileMapper.SimpleProfile(id, name, documentName));
            } else {
                profileMapper.create(new ProfileMapper.SimpleProfile(null, name, documentName));
            }
            list(request, response);
        }
    }



    private void edit(HttpServletRequest request, HttpServletResponse response,
                      Profile profile) throws ServletException, IOException {
        request.setAttribute("profile", profile);
        request.getRequestDispatcher("/WEB-INF/jsp/imcms/profile/edit.jsp").forward(request, response);
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List profiles = getProfileMapper().getAll();
        request.setAttribute("profiles", profiles);
        request.getRequestDispatcher("/WEB-INF/jsp/imcms/profile/list.jsp").forward(request, response);
    }

    private ProfileMapper getProfileMapper() {
        return new ProfileMapper(getDatabase());
    }

}
