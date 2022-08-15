package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.MetaTagJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaTagRepository extends JpaRepository<MetaTagJPA, Integer> {

	@Query("SELECT m FROM MetaTagJPA m ORDER BY m.id")
	List<MetaTagJPA> findAllOrderById();

	boolean existsByName(String name);
}
