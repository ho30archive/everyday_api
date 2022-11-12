package com.everyday.api.domain.mission.dto;

import com.everyday.api.domain.mission.Mission;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Optional;

public record MissionSaveDto(@NotBlank(message = "제목을 입력해주세요") String title,
                            @NotBlank(message = "내용을 입력해주세요") String content,
                            Optional<MultipartFile> uploadFile,
                             @NotNull(message = "종료날짜을 입력해주세요") Date endDate
) {
    public Mission toEntity() {
        return Mission.builder().title(title).content(content).endDate(endDate).build();
    }
}
