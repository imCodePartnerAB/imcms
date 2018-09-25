package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.persistence.entity.ImageCacheDomainObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ImageCacheRepository extends JpaRepository<ImageCacheDomainObject, String> {

    @Query("SELECT count(ic.id) FROM ImageCache ic")
    Long countEntries();

    @Query("SELECT sum(ic.fileSize) FROM ImageCache ic")
    Long fileSizeTotal();

    @Query("SELECT ic.id FROM ImageCache ic ORDER BY ic.frequency ASC")
    List<String> idsByFrequency();

    List<ImageCacheDomainObject> findAllByResource(String resource);

    @Modifying
    @Query("UPDATE ImageCache ic SET ic.frequency = ic.frequency + 1 WHERE ic.id = ?1 AND ic.frequency < ?2")
    void incFrequency(String id, int maxFreq);

    @Modifying
    @Query("DELETE FROM ImageCache ic WHERE ic.id IN (?1)")
    void deleteAllById(Collection<String> cacheIds);

    @Modifying
    @Query("DELETE FROM ImageCache ic WHERE ic.id = ?1")
    void deleteById(String id);
}
