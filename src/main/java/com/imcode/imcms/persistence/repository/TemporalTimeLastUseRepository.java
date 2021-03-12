package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.DataOfTimeLastUseJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporalTimeLastUseRepository extends JpaRepository<DataOfTimeLastUseJPA, Integer> {


}
