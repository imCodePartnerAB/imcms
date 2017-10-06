package com.imcode.imcms.components.cleaner;

import org.springframework.data.jpa.repository.JpaRepository;

public class RepositoryTestDataCleaner extends RepositoryCleaner {
    private final JpaRepository[] jpaRepositories;

    public RepositoryTestDataCleaner(JpaRepository... jpaRepositories) {
        this.jpaRepositories = jpaRepositories;
    }

    public void cleanRepositories() {
        cleanRepositories(jpaRepositories);
    }
}
