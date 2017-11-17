package com.imcode.imcms.components.datainitializer;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class TestDataCleaner {
    private final JpaRepository[] jpaRepositories;

    TestDataCleaner(JpaRepository... jpaRepositories) {
        this.jpaRepositories = jpaRepositories;
    }

    public void cleanRepositories() {
        cleanRepositories(jpaRepositories);
    }

    private void cleanRepositories(JpaRepository... jpaRepositories) {
        for (JpaRepository jpaRepository : jpaRepositories) {
            jpaRepository.deleteAll();
            jpaRepository.flush();
        }
    }

}
