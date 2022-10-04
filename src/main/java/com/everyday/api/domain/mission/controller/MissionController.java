package com.everyday.api.domain.mission.controller;

import com.everyday.api.domain.mission.cond.MissionSearchCondition;
import com.everyday.api.domain.mission.dto.MissionSaveDto;
import com.everyday.api.domain.mission.dto.MissionUpdateDto;
import com.everyday.api.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;


    /**
     * 게시글 저장
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/mission")
    public void save(@Valid @ModelAttribute MissionSaveDto missionSaveDto){
        missionService.save(missionSaveDto);
    }

    /**
     * 게시글 수정
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/mission/{missionId}")
    public void update(@PathVariable("missionId") Long missionId,
                       @ModelAttribute MissionUpdateDto missionUpdateDto){


        missionService.update(missionId, missionUpdateDto);
    }

    /**
     * 게시글 삭제
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/mission/{missionId}")
    public void delete(@PathVariable("missionId") Long missionId){
        missionService.delete(missionId);
    }


    /**
     * 게시글 조회
     */
    @GetMapping("/mission/{missionId}")
    public ResponseEntity getInfo(@PathVariable("missionId") Long missionId){
        return ResponseEntity.ok(missionService.getMissionInfo(missionId));
    }

    /**
     * 게시글 검색
     */
    @GetMapping("/mission")
    public ResponseEntity search(Pageable pageable,
                                 @ModelAttribute MissionSearchCondition missionSearchCondition){

        return ResponseEntity.ok(missionService.getMissionList(pageable,missionSearchCondition));
    }
}

