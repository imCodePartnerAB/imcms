package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.RoleDTO;

import java.util.List;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 07.12.17.
 */
public interface RoleService {
    RoleDTO getById(int id);

    List<RoleDTO> getAll();

    RoleDTO save(RoleDTO saveMe);
}
