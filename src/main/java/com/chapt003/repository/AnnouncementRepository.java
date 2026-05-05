package com.chapt003.repository;

import com.chapt003.entity.Announcement;
import com.chapt003.entity.enums.AnnouncementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByTypeAndPublishedAtLessThanEqualAndDeletedFalseOrderByPublishedAtDesc(
            AnnouncementType type, LocalDateTime now);

    List<Announcement> findByPublishedAtLessThanEqualAndDeletedFalseOrderByPublishedAtDesc(LocalDateTime now);

    @Query("SELECT a FROM Announcement a WHERE a.publishedAt <= :now AND a.deleted = false ORDER BY a.publishedAt DESC")
    List<Announcement> findAllActiveAnnouncements(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Announcement a WHERE a.publishedAt <= :now AND a.deleted = false AND a.type = :type ORDER BY a.publishedAt DESC")
    List<Announcement> findByTypeAndActive(@Param("type") AnnouncementType type, @Param("now") LocalDateTime now);

    Page<Announcement> findByTypeOrderByCreatedAtDesc(AnnouncementType type, Pageable pageable);
}
