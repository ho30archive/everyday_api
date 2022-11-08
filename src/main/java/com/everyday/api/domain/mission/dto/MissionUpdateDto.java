package com.everyday.api.domain.mission.dto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record MissionUpdateDto(
        Optional<String> title,
        Optional<String> content
//        , Optional<MultipartFile> uploadFile
) {
}
