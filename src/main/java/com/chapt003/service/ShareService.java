package com.chapt003.service;

import com.chapt003.dto.ShareResponse;
import com.chapt003.entity.ShareRecord;
import com.chapt003.entity.VolunteerApplication;
import com.chapt003.repository.ShareRecordRepository;
import com.chapt003.repository.VolunteerApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShareService {

    @Autowired
    private ShareRecordRepository shareRecordRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    public ShareResponse createShare(Long userId, String shareType, Long targetId, String title, String description) {
        String shareCode = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        ShareRecord record = ShareRecord.builder()
                .userId(userId)
                .shareType(shareType)
                .targetId(targetId)
                .shareCode(shareCode)
                .title(title)
                .description(description)
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        shareRecordRepository.save(record);

        String shareUrl = "/share/" + shareCode;
        String miniPath = "";

        if ("PLAN".equals(shareType) && targetId != null) {
            miniPath = "pages/applications/applications?shareCode=" + shareCode;
        } else if ("SCHOOL".equals(shareType) && targetId != null) {
            miniPath = "pages/projects/projects?shareCode=" + shareCode;
        }

        return ShareResponse.builder()
                .id(record.getId())
                .shareCode(shareCode)
                .shareType(shareType)
                .title(title)
                .description(description)
                .shareUrl(shareUrl)
                .miniProgramPath(miniPath)
                .build();
    }

    public ShareResponse getShareByCode(String shareCode) {
        ShareRecord record = shareRecordRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new RuntimeException("分享不存在或已过期"));

        if (record.getExpiresAt() != null && record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("分享已过期");
        }

        record.setViewCount(record.getViewCount() + 1);
        shareRecordRepository.save(record);

        String detailInfo = "";
        if ("PLAN".equals(record.getShareType()) && record.getTargetId() != null) {
            detailInfo = volunteerApplicationRepository.findById(record.getTargetId())
                    .map(VolunteerApplication::getSimulationName)
                    .orElse("志愿方案");
        }

        return ShareResponse.builder()
                .id(record.getId())
                .shareCode(record.getShareCode())
                .shareType(record.getShareType())
                .title(record.getTitle())
                .description(record.getDescription())
                .shareUrl("/share/" + shareCode)
                .build();
    }
}
