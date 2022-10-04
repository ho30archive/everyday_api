package com.everyday.api.domain.mission.dto;

import com.everyday.api.domain.mission.Mission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BriefMissionInfo{

    private Long missionId;

    private String title;//제목
    private String content;//내용
    private String writerName;//작성자의 이름
    private String createdDate; //작성일

    public BriefMissionInfo(Mission mission) {
        this.missionId = mission.getId();
        this.title = mission.getTitle();
        this.content = mission.getContent();
        this.writerName = mission.getWriter().getName();
        this.createdDate = mission.getCreatedDate().toString();
    }
}
