package com.imcode.imcms.mapping.jpa;

import java.util.List;

interface UserRepositoryCustom {

    List<User> findAll(boolean includeExternal, boolean includeInactive);

    List<User> findByNamePrefix(String prefix, boolean includeInactive);
}
