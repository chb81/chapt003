package com.chapt003.repository;

import com.chapt003.entity.HelpDocument;
import com.chapt003.entity.enums.HelpDocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpDocumentRepository extends JpaRepository<HelpDocument, Long> {

    List<HelpDocument> findByCategoryAndPublishedAndDeletedFalse(HelpDocumentCategory category, Boolean published);

    @Query("SELECT d FROM HelpDocument d WHERE d.published = true AND d.deleted = false " +
           "AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<HelpDocument> searchDocuments(@Param("keyword") String keyword);

    @Query("SELECT d FROM HelpDocument d WHERE d.published = true AND d.deleted = false " +
           "AND d.category = :category " +
           "AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<HelpDocument> searchDocumentsByCategory(@Param("category") HelpDocumentCategory category,
                                                  @Param("keyword") String keyword);

    List<HelpDocument> findByPublishedAndDeletedFalseOrderByCreatedAtDesc(Boolean published);
}
