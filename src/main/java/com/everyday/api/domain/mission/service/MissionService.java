package com.everyday.api.domain.mission.service;
import com.everyday.api.domain.mission.cond.MissionSearchCondition;
import com.everyday.api.domain.mission.dto.MissionInfoDto;
import com.everyday.api.domain.mission.dto.MissionPagingDto;
import com.everyday.api.domain.mission.dto.MissionSaveDto;
import com.everyday.api.domain.mission.dto.MissionUpdateDto;
import com.everyday.api.global.file.exception.FileException;
import org.springframework.data.domain.Pageable;

import java.io.IOException;


public interface MissionService {

    /**
     * 게시글 등록
     */
    void save(MissionSaveDto missionSaveDto) throws FileException;

    /**
     * 게시글 수정
     */
    void update(Long id, MissionUpdateDto missionUpdateDto);

    /**
     * 게시글 삭제
     */
    void delete(Long id);

    /**
     * 게시글 1개 조회
     */
    MissionInfoDto getMissionInfo(Long id);

    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     */
    MissionPagingDto getMissionList(Pageable pageable, MissionSearchCondition missionSearchCondition);
}
