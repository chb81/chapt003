package com.chapt003.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareResponse {

    private Long id;
    private String shareCode;
    private String shareType;
    private String title;
    private String description;
    private String shareUrl;
    private String miniProgramPath;
}
