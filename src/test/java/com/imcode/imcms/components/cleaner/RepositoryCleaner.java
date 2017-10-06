package com.imcode.imcms.components.cleaner;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class RepositoryCleaner {

    public abstract void cleanRepositories();

    public void cleanRepositories(JpaRepository... jpaRepositories) {
        for (JpaRepository jpaRepository : jpaRepositories) {
            jpaRepository.deleteAll();
            jpaRepository.flush();
        }
    }

}
