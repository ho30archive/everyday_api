package com.everyday.api.domain.mission.dto;

import com.everyday.api.domain.mission.Mission;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public record MissionSaveDto(@NotBlank(message = "제목을 입력해주세요") String title,
                            @NotBlank(message = "내용을 입력해주세요") String content
//                            ,Optional<MultipartFile> uploadFile
) {
    public Mission toEntity() {

        return Mission.builder().title(title).content(content).build();
    }
}
