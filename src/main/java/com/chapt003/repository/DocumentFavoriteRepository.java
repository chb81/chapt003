package com.chapt003.repository;

import com.chapt003.entity.DocumentFavorite;
import com.chapt003.entity.HelpDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentFavoriteRepository extends JpaRepository<DocumentFavorite, Long> {

    Optional<DocumentFavorite> findByUserIdAndDocumentId(Long userId, Long documentId);

    @Query("SELECT d FROM DocumentFavorite f JOIN f.document d WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<HelpDocument> findFavoriteDocumentsByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndDocumentId(Long userId, Long documentId);

    @Query("SELECT COUNT(f) > 0 FROM DocumentFavorite f WHERE f.user.id = :userId AND f.document.id = :documentId")
    boolean existsByUserIdAndDocumentId(@Param("userId") Long userId, @Param("documentId") Long documentId);
}
