package com.imcode.imcms.components.datainitializer;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractTestDataInitializer<IN, R> {
    private final JpaRepository[] jpaRepositories;

    AbstractTestDataInitializer(JpaRepository... jpaRepositories) {
        this.jpaRepositories = jpaRepositories;
    }

    public void cleanRepositories() {
        cleanRepositories(jpaRepositories);
    }

    protected R createData(IN in) {
        throw new UnsupportedOperationException();
    }

    protected R createData(IN in1, IN in2) {
        throw new UnsupportedOperationException();
    }

    protected R createData(IN in1, IN in2, IN in3) {
        throw new UnsupportedOperationException();
    }

    protected R createData() {
        throw new UnsupportedOperationException();
    }

    private void cleanRepositories(JpaRepository... jpaRepositories) {
        for (JpaRepository jpaRepository : jpaRepositories) {
            jpaRepository.deleteAll();
            jpaRepository.flush();
        }
    }

}
