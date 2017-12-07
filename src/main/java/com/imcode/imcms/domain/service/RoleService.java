package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.RoleDTO;

import java.util.List;

public interface RoleService {

    RoleDTO getById(int id);

    List<RoleDTO> getAll();

    RoleDTO save(RoleDTO saveMe);

}
