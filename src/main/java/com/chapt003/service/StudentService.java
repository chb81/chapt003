package com.chapt003.service;

import com.chapt003.dto.StudentProfileRequest;
import com.chapt003.dto.StudentProfileResponse;
import com.chapt003.dto.StudentScoreRequest;
import com.chapt003.dto.StudentScoreResponse;
import com.chapt003.entity.StudentProfile;
import com.chapt003.entity.StudentScore;
import com.chapt003.entity.User;
import com.chapt003.exception.BusinessException;
import com.chapt003.repository.StudentProfileRepository;
import com.chapt003.repository.StudentScoreRepository;
import com.chapt003.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    @Autowired
    private UserRepository userRepository;

    public StudentProfileResponse getProfile(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(404, "学生档案不存在"));
        
        return convertToProfileResponse(profile);
    }

    @Transactional
    public StudentProfileResponse createOrUpdateProfile(String username, StudentProfileRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElse(StudentProfile.builder().user(user).build());
        
        profile.setName(request.getName());
        profile.setGender(request.getGender());
        profile.setBirthDate(request.getBirthDate());
        profile.setCity(request.getCity());
        profile.setDistrict(request.getDistrict());
        profile.setSchool(request.getSchool());
        
        StudentProfile savedProfile = studentProfileRepository.save(profile);
        return convertToProfileResponse(savedProfile);
    }

    @Transactional
    public void deleteProfile(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        if (!studentProfileRepository.existsByUserId(user.getId())) {
            throw new BusinessException(404, "学生档案不存在");
        }
        
        studentProfileRepository.deleteByUserId(user.getId());
    }

    public StudentScoreResponse getScore(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        StudentScore score = studentScoreRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(404, "学生成绩不存在"));
        
        return convertToScoreResponse(score);
    }

    @Transactional
    public StudentScoreResponse createOrUpdateScore(String username, StudentScoreRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        StudentScore score = studentScoreRepository.findByUserId(user.getId())
                .orElse(StudentScore.builder().user(user).build());
        
        score.setChinese(request.getChinese());
        score.setMath(request.getMath());
        score.setEnglish(request.getEnglish());
        score.setPhysics(request.getPhysics());
        score.setChemistry(request.getChemistry());
        score.setPolitics(request.getPolitics());
        score.setHistory(request.getHistory());
        score.setGeography(request.getGeography());
        score.setBiology(request.getBiology());
        
        StudentScore savedScore = studentScoreRepository.save(score);
        return convertToScoreResponse(savedScore);
    }

    @Transactional
    public void deleteScore(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        if (!studentScoreRepository.existsByUserId(user.getId())) {
            throw new BusinessException(404, "学生成绩不存在");
        }
        
        studentScoreRepository.deleteByUserId(user.getId());
    }

    private StudentProfileResponse convertToProfileResponse(StudentProfile profile) {
        return StudentProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .name(profile.getName())
                .gender(profile.getGender())
                .birthDate(profile.getBirthDate())
                .city(profile.getCity())
                .district(profile.getDistrict())
                .school(profile.getSchool())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private StudentScoreResponse convertToScoreResponse(StudentScore score) {
        return StudentScoreResponse.builder()
                .id(score.getId())
                .userId(score.getUser().getId())
                .chinese(score.getChinese())
                .math(score.getMath())
                .english(score.getEnglish())
                .physics(score.getPhysics())
                .chemistry(score.getChemistry())
                .politics(score.getPolitics())
                .history(score.getHistory())
                .geography(score.getGeography())
                .biology(score.getBiology())
                .totalScore(score.getTotalScore())
                .averageScore(score.getAverageScore())
                .createdAt(score.getCreatedAt())
                .updatedAt(score.getUpdatedAt())
                .build();
    }
}
