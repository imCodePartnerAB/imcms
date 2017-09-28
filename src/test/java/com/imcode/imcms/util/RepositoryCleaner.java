package com.imcode.imcms.util;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryCleaner {

    void cleanRepositories();

    default void cleanRepositories(JpaRepository... jpaRepositories) {
        for (JpaRepository jpaRepository : jpaRepositories) {
            jpaRepository.deleteAll();
            jpaRepository.flush();
        }
    }

}
