package com.imcode.imcms.addon.imagearchive.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;

public class ArchiveSession implements Serializable {
    private static final long serialVersionUID = -389907761386813667L;

    private static final String ARCHIVE_SESSION_KEY = Utils.makeKey(ArchiveSession.class, "archiveSession");
    
    
    private Map<String, Object> sessionMap = new HashMap<String, Object>();
    private int belongsToUserId;

    
    private ArchiveSession(int belongsToUserId) {
        this.belongsToUserId = belongsToUserId;
    }
    
    
    public static ArchiveSession getSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = ContentManagementSystem.fromRequest(request).getCurrentUser();
        
        ArchiveSession archiveSession = (ArchiveSession) session.getAttribute(ARCHIVE_SESSION_KEY);
        
        if (archiveSession == null || archiveSession.getBelongsToUserId() != user.getId()) {
            archiveSession = new ArchiveSession(user.getId());
            session.setAttribute(ARCHIVE_SESSION_KEY, archiveSession);
        }
        
        return archiveSession;
    }
    
    public void put(String key, Object value) {
        sessionMap.put(key, value);
    }
    
    public Object get(String key) {
        return sessionMap.get(key);
    }
    
    public void remove(String key) {
        sessionMap.remove(key);
    }

    private int getBelongsToUserId() {
        return belongsToUserId;
    }
}
