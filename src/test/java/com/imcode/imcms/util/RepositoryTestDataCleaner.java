package com.imcode.imcms.util;

import org.springframework.data.jpa.repository.JpaRepository;

public class RepositoryTestDataCleaner implements RepositoryCleaner {
    private final JpaRepository[] jpaRepositories;

    public RepositoryTestDataCleaner(JpaRepository... jpaRepositories) {
        this.jpaRepositories = jpaRepositories;
    }

    public void cleanRepositories() {
        cleanRepositories(jpaRepositories);
    }
}
