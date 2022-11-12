package com.everyday.api.domain.mission.dto;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.Optional;

public record MissionUpdateDto(
        Optional<String> title,
        Optional<String> content,

        Optional<Date> endDate,

        Optional<MultipartFile> uploadFile
) {
}
