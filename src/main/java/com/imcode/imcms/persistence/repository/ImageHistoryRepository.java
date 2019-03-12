package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImageHistoryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageHistoryRepository extends JpaRepository<ImageHistoryJPA, Integer> {
}
