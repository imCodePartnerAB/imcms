package com.imcode.imcms.addon.imagearchive.service;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imcode.imcms.addon.imagearchive.dto.LibrariesDto;
import com.imcode.imcms.addon.imagearchive.dto.LibraryRolesDto;
import com.imcode.imcms.addon.imagearchive.entity.Libraries;
import com.imcode.imcms.addon.imagearchive.entity.LibraryRoles;
import com.imcode.imcms.addon.imagearchive.entity.Roles;
import com.imcode.imcms.api.User;


@Transactional
public class LibraryService {
    @Autowired
    private Facade facade;
    
    @Autowired
    @Qualifier("hibernateTemplate")
    private HibernateTemplate template;
    
    
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public Libraries findLibraryById(final int libraryId) {
        return (Libraries) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "SELECT lib.id AS id, lib.folderNm AS folderNm, lib.libraryNm AS libraryNm, " +
                        "lib.filepath AS filepath, lib.libraryType AS libraryType " +
                        "FROM Libraries lib WHERE lib.id = :libraryId")
                        .setInteger("libraryId", libraryId)
                        .setResultTransformer(Transformers.aliasToBean(Libraries.class))
                        .uniqueResult();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public void syncLibraryFolders() {
        template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                syncOldLibraryFolders(session);
                
                Set<String> folders = facade.getFileService().listLibraryFolders();
                
                if (folders.isEmpty()) {
                    session.getNamedQuery("deleteLibraryRoles")
                            .setShort("type", Libraries.TYPE_STANDARD)
                            .executeUpdate();
                    session.getNamedQuery("deleteLibraries")
                            .setShort("type", Libraries.TYPE_STANDARD)
                            .executeUpdate();
                    
                    return null;
                }
                
                List<Libraries> existingLibraries = session.createQuery(
                        "SELECT lib.id AS id, lib.folderNm AS folderNm FROM Libraries lib WHERE lib.libraryType = :typeStandard")
                        .setShort("typeStandard", Libraries.TYPE_STANDARD)
                        .setResultTransformer(Transformers.aliasToBean(Libraries.class))
                        .list();
                
                List<Integer> toDelete = new ArrayList<Integer>();
                
                for (Libraries lib : existingLibraries) {
                    String folderNm = lib.getFolderNm();
                    
                    if (!folders.contains(folderNm)) {
                        toDelete.add(lib.getId());
                    } else {
                        folders.remove(folderNm);
                    }
                }
                
                if (!toDelete.isEmpty()) {
                    session.createQuery(
                            "DELETE FROM LibraryRoles lr WHERE lr.libraryId IN (:libraryIds) ")
                            .setParameterList("libraryIds", toDelete)
                            .executeUpdate();
                    
                    session.createQuery("DELETE FROM Libraries lib WHERE lib.id IN (:libraryIds) ")
                            .setParameterList("libraryIds", toDelete)
                            .executeUpdate();
                }
                
                for (String folderNm : folders) {
                    Libraries lib = new Libraries();
                    lib.setFolderNm(folderNm);
                    lib.setLibraryNm(StringUtils.substring(folderNm, 0, 120));
                    lib.setLibraryType(Libraries.TYPE_STANDARD);
                    
                    session.persist(lib);
                }
                session.flush();
                
                return null;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private void syncOldLibraryFolders(Session session) {
        File[] oldLibraryFiles = facade.getConfig().getOldLibraryPaths();
        Set<File> files = new HashSet<File>(oldLibraryFiles.length);
        
        CollectionUtils.addAll(files, oldLibraryFiles);

        if (files.isEmpty()) {
            session.getNamedQuery("deleteLibraryRoles")
                    .setShort("type", Libraries.TYPE_OLD_LIBRARY)
                    .executeUpdate();
            session.getNamedQuery("deleteLibraries")
                    .setShort("type", Libraries.TYPE_OLD_LIBRARY)
                    .executeUpdate();
        }

        List<Libraries> existingLibraries = session.createQuery(
                "SELECT lib.id AS id, lib.folderNm AS folderNm, lib.filepath AS filepath " +
                "FROM Libraries lib WHERE lib.libraryType = :typeOld")
                .setShort("typeOld", Libraries.TYPE_OLD_LIBRARY)
                .setResultTransformer(Transformers.aliasToBean(Libraries.class))
                .list();

        List<Integer> toDelete = new ArrayList<Integer>();

        for (Libraries lib : existingLibraries) {
            String folderNm = lib.getFolderNm();
            String filepath = lib.getFilepath();
            File file = new File(filepath, folderNm);

            if (!files.contains(file)) {
                toDelete.add(lib.getId());
            } else {
                files.remove(file);
            }
        }

        if (!toDelete.isEmpty()) {
            session.createQuery(
                    "DELETE LibraryRoles lr WHERE lr.libraryId IN (:libraryIds) ")
                    .setParameterList("libraryIds", toDelete)
                    .executeUpdate();

            session.createQuery(
                    "DELETE Libraries lib WHERE lib.id IN (:libraryIds) ")
                    .setParameterList("libraryIds", toDelete)
                    .executeUpdate();
        }

        for (File file : files) {
            String folderNm = file.getName();

            Libraries lib = new Libraries();
            lib.setFolderNm(folderNm);
            lib.setLibraryNm(StringUtils.substring(folderNm, 0, 120));
            lib.setFilepath(file.getParent());
            lib.setLibraryType(Libraries.TYPE_OLD_LIBRARY);

            session.persist(lib);
        }
        session.flush();
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Libraries> findLibraries() {
        return (List<Libraries>) template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "SELECT lib.id AS id, lib.folderNm AS folderNm, lib.libraryNm AS libraryNm, " +
                        "lib.filepath AS filepath, lib.libraryType AS libraryType FROM Libraries lib " +
                        "ORDER BY lib.folderNm")
                        .setResultTransformer(Transformers.aliasToBean(Libraries.class))
                        .list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<Roles> findAvailableRoles(final int libraryId) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "SELECT r.id AS id, r.roleName AS roleName FROM Roles r " +
                        "WHERE NOT EXISTS (FROM LibraryRoles lr WHERE lr.roleId = r.id AND lr.libraryId = :libraryId) " +
                        "ORDER BY r.roleName")
                        .setInteger("libraryId", libraryId)
                        .setResultTransformer(Transformers.aliasToBean(Roles.class))
                        .list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<LibraryRolesDto> findLibraryRoles(final int libraryId) {
        return (List<LibraryRolesDto>) template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.getNamedQuery("libraryRoles")
                        .setInteger("libraryId", libraryId)
                        .setResultTransformer(Transformers.aliasToBean(LibraryRolesDto.class))
                        .list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public void updateLibraryRoles(final int libraryId, final String libraryNm, 
            final List<LibraryRolesDto> libraryRoles) {
        template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.createQuery(
                        "UPDATE Libraries lib SET lib.libraryNm = :libraryNm, lib.updatedDt = current_timestamp() WHERE lib.id = :libraryId")
                        .setString("libraryNm", libraryNm)
                        .setInteger("libraryId", libraryId)
                        .executeUpdate();
                
                if (libraryRoles.isEmpty()) {
                    session.createQuery(
                            "DELETE FROM LibraryRoles lr WHERE lr.libraryId = :libraryId")
                            .setInteger("libraryId", libraryId)
                            .executeUpdate();
                    
                    return null;
                }
                
                Map<Integer, LibraryRolesDto> roleMap = new HashMap<Integer, LibraryRolesDto>(libraryRoles.size());
                for (LibraryRolesDto libraryRole : libraryRoles) {
                    roleMap.put(libraryRole.getRoleId(), libraryRole);
                }
                
                List<Integer> existingRoleIds = session.createQuery(
                        "SELECT lr.roleId FROM LibraryRoles lr WHERE lr.libraryId = :libraryId")
                        .setInteger("libraryId", libraryId)
                        .list();
                
                List<Integer> toDelete = new ArrayList<Integer>();
                List<LibraryRolesDto> toUpdate = new ArrayList<LibraryRolesDto>();
                for (Integer roleId : existingRoleIds) {
                    if (roleMap.containsKey(roleId)) {
                        toUpdate.add(roleMap.get(roleId));
                        roleMap.remove(roleId);
                    } else {
                        toDelete.add(roleId);
                    }
                }
                
                if (!toDelete.isEmpty()) {
                    session.createQuery("DELETE FROM LibraryRoles lr WHERE lr.libraryId = :libraryId AND lr.roleId IN (:roleIds)")
                            .setInteger("libraryId", libraryId)
                            .setParameterList("roleIds", toDelete)
                            .executeUpdate();
                }
                
                
                Collection<LibraryRolesDto> toCreate = roleMap.values();
                for (LibraryRolesDto libraryRoleDto : toCreate) {
                    LibraryRoles libraryRole = new LibraryRoles();
                    libraryRole.setLibraryId(libraryId);
                    libraryRole.setRoleId(libraryRoleDto.getRoleId());
                    libraryRole.setPermissions(libraryRoleDto.getPermissions());

                    session.persist(libraryRole);
                }
                
                Query updateQuery = session.createQuery(
                        "UPDATE LibraryRoles lr SET lr.permissions = :permissions, lr.updatedDt = current_timestamp() " +
                        "WHERE lr.libraryId = :libraryId AND lr.roleId = :roleId")
                        .setInteger("libraryId", libraryId);
                for (LibraryRolesDto libraryRoleDto : toUpdate) {
                    updateQuery.setInteger("roleId", libraryRoleDto.getRoleId())
                            .setInteger("permissions", libraryRoleDto.getPermissions())
                            .executeUpdate();
                }
                
                session.flush();
                
                return null;
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public List<LibrariesDto> findLibraries(final User user) {
        return template.executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                if (user.isSuperAdmin()) {
                    return session.createQuery(
                            "SELECT lib.id AS id, lib.libraryNm AS libraryNm FROM Libraries lib ORDER BY lib.libraryNm")
                            .setResultTransformer(Transformers.aliasToBean(LibrariesDto.class))
                            .list();
                }
                
                List<Integer> roleIds = UserService.getRoleIds(user);
                if (roleIds.isEmpty()) {
                    return Collections.EMPTY_LIST;
                }
                
                return session.createQuery(
                        "SELECT DISTINCT lib.id AS id, lib.libraryNm AS libraryNm FROM LibraryRoles lr INNER JOIN lr.library lib " +
                        "WHERE lr.roleId IN (:roleIds) ORDER BY lib.libraryNm ")
                        .setParameterList("roleIds", roleIds)
                        .setResultTransformer(Transformers.aliasToBean(LibrariesDto.class))
                        .list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    public LibrariesDto findLibraryById(final User user, final int libraryId) {
        return (LibrariesDto) template.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                LibrariesDto library = (LibrariesDto) session.createQuery(
                        "SELECT lib.id AS id, lib.libraryNm AS libraryNm, lib.folderNm AS folderNm, " +
                        "lib.filepath AS filepath, lib.libraryType AS libraryType " +
                        "FROM Libraries lib WHERE lib.id = :libraryId")
                        .setInteger("libraryId", libraryId)
                        .setResultTransformer(Transformers.aliasToBean(LibrariesDto.class))
                        .uniqueResult();
                
                if (library == null) {
                    return null;
                } else if (user.isSuperAdmin()) {
                    library.setCanUse(true);
                    library.setCanChange(true);
                    
                    return library;
                }
                
                List<Integer> roleIds = UserService.getRoleIds(user);
                if (roleIds.isEmpty()) {
                    return library;
                }
                
                List<Integer> permissions = session.createQuery(
                        "SELECT DISTINCT lr.permissions FROM LibraryRoles lr WHERE lr.libraryId = :libraryId AND lr.roleId IN (:roleIds)")
                        .setInteger("libraryId", libraryId)
                        .setParameterList("roleIds", roleIds)
                        .list();
                
                boolean canUse = false;
                boolean canChange = false;
                for (int permission : permissions) {
                    if ((permission & LibraryRoles.PERMISSION_USE) == LibraryRoles.PERMISSION_USE) {
                        canUse = true;
                    }
                    if ((permission & LibraryRoles.PERMISSION_CHANGE) == LibraryRoles.PERMISSION_CHANGE) {
                        canChange = true;
                    }
                }
                
                library.setCanUse(canUse);
                library.setCanChange(canChange);
                
                return library;
            }
        });
    }
}
