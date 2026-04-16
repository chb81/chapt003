package com.chapt003.repository;

import com.chapt003.entity.Announcement;
import com.chapt003.entity.UserAnnouncementRead;
import com.chapt003.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnnouncementReadRepository extends JpaRepository<UserAnnouncementRead, Long> {

    Optional<UserAnnouncementRead> findByUserIdAndAnnouncementId(Long userId, Long announcementId);

    boolean existsByUserIdAndAnnouncementId(Long userId, Long announcementId);

    @Query("SELECT r.announcement FROM UserAnnouncementRead r WHERE r.user.id = :userId")
    List<Announcement> findReadAnnouncementsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) > 0 FROM Announcement a LEFT JOIN UserAnnouncementRead r ON r.announcement = a WHERE a.id = :announcementId AND r.user.id = :userId")
    boolean hasUserReadAnnouncement(@Param("userId") Long userId, @Param("announcementId") Long announcementId);
}
