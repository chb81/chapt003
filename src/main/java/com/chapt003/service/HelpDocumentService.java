package com.chapt003.service;

import com.chapt003.dto.HelpDocumentDTO;
import com.chapt003.dto.HelpDocumentDetailDTO;
import com.chapt003.entity.DocumentFeedback;
import com.chapt003.entity.DocumentFavorite;
import com.chapt003.entity.HelpDocument;
import com.chapt003.entity.User;
import com.chapt003.entity.enums.HelpDocumentCategory;
import com.chapt003.repository.DocumentFeedbackRepository;
import com.chapt003.repository.DocumentFavoriteRepository;
import com.chapt003.repository.HelpDocumentRepository;
import com.chapt003.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HelpDocumentService {

    @Autowired
    private HelpDocumentRepository helpDocumentRepository;

    @Autowired
    private DocumentFavoriteRepository documentFavoriteRepository;

    @Autowired
    private DocumentFeedbackRepository documentFeedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<HelpDocumentDTO> getDocumentsByCategory(HelpDocumentCategory category, Long userId) {
        List<HelpDocument> documents = helpDocumentRepository
                .findByCategoryAndPublishedAndDeletedFalse(category, true);

        return documents.stream()
                .map(doc -> convertToDTO(doc, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HelpDocumentDTO> searchDocuments(String keyword, Long userId) {
        List<HelpDocument> documents;
        if (keyword == null || keyword.trim().isEmpty()) {
            documents = helpDocumentRepository.findByPublishedAndDeletedFalseOrderByCreatedAtDesc(true);
        } else {
            documents = helpDocumentRepository.searchDocuments(keyword.trim());
        }

        return documents.stream()
                .map(doc -> convertToDTO(doc, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<HelpDocumentDetailDTO> getDocumentDetail(Long documentId, Long userId) {
        Optional<HelpDocument> documentOpt = helpDocumentRepository.findById(documentId);
        if (!documentOpt.isPresent()) {
            return Optional.empty();
        }

        HelpDocument document = documentOpt.get();
        if (!document.getPublished() || document.getDeleted()) {
            return Optional.empty();
        }

        document.setViewCount(document.getViewCount() + 1);
        helpDocumentRepository.save(document);

        Boolean isFavorite = userId != null ?
                documentFavoriteRepository.existsByUserIdAndDocumentId(userId, documentId) : false;

        HelpDocumentDetailDTO dto = HelpDocumentDetailDTO.builder()
                .id(document.getId())
                .title(document.getTitle())
                .category(document.getCategory().name())
                .description(document.getDescription())
                .content(document.getContent())
                .readingTime(document.getReadingTime())
                .viewCount(document.getViewCount())
                .helpfulCount(document.getHelpfulCount())
                .notHelpfulCount(document.getNotHelpfulCount())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .isFavorite(isFavorite)
                .build();

        return Optional.of(dto);
    }

    @Transactional
    public void toggleFavorite(Long documentId, Long userId) {
        Optional<HelpDocument> documentOpt = helpDocumentRepository.findById(documentId);
        if (!documentOpt.isPresent()) {
            throw new RuntimeException("文档不存在");
        }

        Optional<DocumentFavorite> existingOpt = documentFavoriteRepository
                .findByUserIdAndDocumentId(userId, documentId);

        if (existingOpt.isPresent()) {
            documentFavoriteRepository.delete(existingOpt.get());
        } else {
            DocumentFavorite favorite = DocumentFavorite.builder()
                    .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")))
                    .document(documentOpt.get())
                    .build();
            documentFavoriteRepository.save(favorite);
        }
    }

    @Transactional(readOnly = true)
    public List<HelpDocumentDTO> getFavoriteDocuments(Long userId) {
        List<HelpDocument> documents = documentFavoriteRepository
                .findFavoriteDocumentsByUserId(userId);

        return documents.stream()
                .map(doc -> convertToDTO(doc, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void submitFeedback(Long documentId, Long userId, Boolean isHelpful) {
        Optional<HelpDocument> documentOpt = helpDocumentRepository.findById(documentId);
        if (!documentOpt.isPresent()) {
            throw new RuntimeException("文档不存在");
        }

        Optional<DocumentFeedback> existingOpt = documentFeedbackRepository
                .findByUserIdAndDocumentId(userId, documentId);

        if (existingOpt.isPresent()) {
            DocumentFeedback existing = existingOpt.get();
            if (!existing.getIsHelpful().equals(isHelpful)) {
                existing.setIsHelpful(isHelpful);
                documentFeedbackRepository.save(existing);
                updateDocumentFeedbackCounts(documentId);
            }
        } else {
            DocumentFeedback feedback = DocumentFeedback.builder()
                    .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                    .document(documentOpt.get())
                    .isHelpful(isHelpful)
                    .build();
            documentFeedbackRepository.save(feedback);
            updateDocumentFeedbackCounts(documentId);
        }
    }

    private void updateDocumentFeedbackCounts(Long documentId) {
        long helpfulCount = documentFeedbackRepository.countHelpfulByDocumentId(documentId);
        long notHelpfulCount = documentFeedbackRepository.countNotHelpfulByDocumentId(documentId);

        HelpDocument document = helpDocumentRepository.findById(documentId).orElse(null);
        if (document != null) {
            document.setHelpfulCount((int) helpfulCount);
            document.setNotHelpfulCount((int) notHelpfulCount);
            helpDocumentRepository.save(document);
        }
    }

    private HelpDocumentDTO convertToDTO(HelpDocument document, Long userId) {
        Boolean isFavorite = userId != null ?
                documentFavoriteRepository.existsByUserIdAndDocumentId(userId, document.getId()) : false;

        return HelpDocumentDTO.builder()
                .id(document.getId())
                .title(document.getTitle())
                .category(document.getCategory().name())
                .description(document.getDescription())
                .readingTime(document.getReadingTime())
                .viewCount(document.getViewCount())
                .helpfulCount(document.getHelpfulCount())
                .notHelpfulCount(document.getNotHelpfulCount())
                .createdAt(document.getCreatedAt())
                .isFavorite(isFavorite)
                .build();
    }
}
