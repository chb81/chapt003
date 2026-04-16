package com.chapt003.repository;

import com.chapt003.entity.DocumentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentFeedbackRepository extends JpaRepository<DocumentFeedback, Long> {

    Optional<DocumentFeedback> findByUserIdAndDocumentId(Long userId, Long documentId);

    @Query("SELECT COUNT(f) FROM DocumentFeedback f WHERE f.document.id = :documentId AND f.isHelpful = true")
    long countHelpfulByDocumentId(@Param("documentId") Long documentId);

    @Query("SELECT COUNT(f) FROM DocumentFeedback f WHERE f.document.id = :documentId AND f.isHelpful = false")
    long countNotHelpfulByDocumentId(@Param("documentId") Long documentId);
}
