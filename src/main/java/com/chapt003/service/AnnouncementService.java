package com.chapt003.service;

import com.chapt003.dto.AnnouncementDTO;
import com.chapt003.dto.AnnouncementDetailDTO;
import com.chapt003.entity.Announcement;
import com.chapt003.entity.User;
import com.chapt003.entity.UserAnnouncementRead;
import com.chapt003.entity.enums.AnnouncementType;
import com.chapt003.repository.AnnouncementRepository;
import com.chapt003.repository.UserAnnouncementReadRepository;
import com.chapt003.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private UserAnnouncementReadRepository userAnnouncementReadRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "announcements", key = "'all-' + #userId")
    public List<AnnouncementDTO> getAllAnnouncements(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> announcements = announcementRepository
                .findAllActiveAnnouncements(now);

        return announcements.stream()
                .map(announcement -> convertToDTO(announcement, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByType(AnnouncementType type, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> announcements = announcementRepository
                .findByTypeAndActive(type, now);

        return announcements.stream()
                .map(announcement -> convertToDTO(announcement, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getUnreadAnnouncements(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> allAnnouncements = announcementRepository
                .findAllActiveAnnouncements(now);

        List<Long> readAnnouncementIds = userAnnouncementReadRepository
                .findReadAnnouncementsByUserId(userId)
                .stream()
                .map(announcement -> announcement.getId())
                .collect(Collectors.toList());

        return allAnnouncements.stream()
                .filter(announcement -> !readAnnouncementIds.contains(announcement.getId()))
                .map(announcement -> convertToDTO(announcement, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AnnouncementDetailDTO> getAnnouncementDetail(Long announcementId, Long userId) {
        return announcementRepository.findById(announcementId)
                .map(announcement -> {
                    AnnouncementDetailDTO detail = convertToDetailDTO(announcement, userId);
                    
                    if (userId != null && !userAnnouncementReadRepository
                            .existsByUserIdAndAnnouncementId(userId, announcementId)) {
                        
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("用户不存在"));
                        
                        UserAnnouncementRead readRecord = UserAnnouncementRead.builder()
                                .user(user)
                                .announcement(announcement)
                                .build();
                        userAnnouncementReadRepository.save(readRecord);
                        
                        detail.setReadAt(LocalDateTime.now());
                    }
                    
                    return detail;
                });
    }

    @Transactional
    public void markAsRead(Long announcementId, Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!userAnnouncementReadRepository.existsByUserIdAndAnnouncementId(userId, announcementId)) {
            UserAnnouncementRead readRecord = UserAnnouncementRead.builder()
                    .user(user)
                    .announcement(announcement)
                    .build();
            userAnnouncementReadRepository.save(readRecord);
        }
    }

    private AnnouncementDTO convertToDTO(Announcement announcement, Long userId) {
        Boolean isRead = userId != null && 
                userAnnouncementReadRepository.existsByUserIdAndAnnouncementId(userId, announcement.getId());

        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .type(announcement.getType().name())
                .content(announcement.getContent())
                .publisher(announcement.getPublisher())
                .priority(announcement.getPriority())
                .publishedAt(announcement.getPublishedAt())
                .isRead(isRead)
                .build();
    }

    private AnnouncementDetailDTO convertToDetailDTO(Announcement announcement, Long userId) {
        Boolean isRead = userId != null && 
                userAnnouncementReadRepository.existsByUserIdAndAnnouncementId(userId, announcement.getId());

        return AnnouncementDetailDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .type(announcement.getType().name())
                .content(announcement.getContent())
                .publisher(announcement.getPublisher())
                .priority(announcement.getPriority())
                .publishedAt(announcement.getPublishedAt())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .readAt(isRead ? LocalDateTime.now() : null)
                .build();
    }
}
