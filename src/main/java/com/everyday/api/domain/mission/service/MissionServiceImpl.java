package com.everyday.api.domain.mission.service;

import com.everyday.api.domain.member.exception.MemberException;
import com.everyday.api.domain.member.exception.MemberExceptionType;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.cond.MissionSearchCondition;
import com.everyday.api.domain.mission.dto.MissionInfoDto;
import com.everyday.api.domain.mission.dto.MissionPagingDto;
import com.everyday.api.domain.mission.dto.MissionSaveDto;
import com.everyday.api.domain.mission.dto.MissionUpdateDto;
import com.everyday.api.domain.mission.exception.MissionException;
import com.everyday.api.domain.mission.exception.MissionExceptionType;
import com.everyday.api.domain.mission.repository.MissionRepository;
import com.everyday.api.global.file.exception.FileException;
import com.everyday.api.global.file.service.FileService;
import com.everyday.api.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.everyday.api.domain.mission.exception.MissionExceptionType.MISSION_NOT_POUND;



@Service
@RequiredArgsConstructor
@Transactional
public class MissionServiceImpl implements MissionService{

    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;


    /**
     * 게시글 저장
     */
    @Override
    public void save(MissionSaveDto missionSaveDto) throws FileException {
        Mission mission = missionSaveDto.toEntity();

        mission.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        missionSaveDto.uploadFile().ifPresent(
                file ->  mission.updateFilePath(fileService.save(file))
        );
        missionRepository.save(mission);
    }


    /**
     * 게시글 수정
     */
    @Override
    public void update(Long id, MissionUpdateDto missionUpdateDto) {

        Mission mission = missionRepository.findById(id).orElseThrow(() -> new MissionException(MISSION_NOT_POUND));
        checkAuthority(mission, MissionExceptionType.NOT_AUTHORITY_UPDATE_MISSION );


        missionUpdateDto.title().ifPresent(mission::updateTitle);
        missionUpdateDto.content().ifPresent(mission::updateContent);


        if(mission.getFilePath() !=null){
            fileService.delete(mission.getFilePath());//기존에 올린 파일 지우기
        }

        missionUpdateDto.uploadFile().ifPresentOrElse(
                multipartFile ->  mission.updateFilePath(fileService.save(multipartFile)),
                () ->  mission.updateFilePath(null)
        );

    }


    /**
     * 게시글 삭제
     */
    @Override
    public void delete(Long id) {

        Mission mission = missionRepository.findById(id).orElseThrow(() ->
                new MissionException(MISSION_NOT_POUND));

        checkAuthority(mission,MissionExceptionType.NOT_AUTHORITY_DELETE_MISSION);


        if(mission.getFilePath() !=null){
            fileService.delete(mission.getFilePath());//기존에 올린 파일 지우기
        }

        missionRepository.delete(mission);
    }


    private void checkAuthority(Mission mission, MissionExceptionType missionExceptionType) {
        if(!mission.getWriter().getUsername().equals(SecurityUtil.getLoginUsername()))
            throw new MissionException(missionExceptionType);
    }






    /**
     * Mission의 id를 통해 Mission 조회
     */
    @Override
    public MissionInfoDto getMissionInfo(Long id) {


        /**
         * Mission + MEMBER 조회 -> 쿼리 1번 발생
         *
         * 댓글&대댓글 리스트 조회 -> 쿼리 1번 발생(MISSION ID로 찾는 것이므로, IN쿼리가 아닌 일반 where문 발생)
         * (댓글과 대댓글 모두 Comment 클래스이므로, JPA는 구분할 방법이 없어서, 당연히 CommentList에 모두 나오는것이 맞다,
         * 가지고 온 것을 가지고 우리가 구분지어주어야 한다.)
         *
         * 댓글 작성자 정보 조회 -> 배치사이즈를 이용했기때문에 쿼리 1번 발생
         *
         *
         */
        return new MissionInfoDto(missionRepository.findWithWriterById(id)
                .orElseThrow(() -> new MissionException(MISSION_NOT_POUND)));

    }



    /**
     * 게시글 검색
     */
    @Override
    public MissionPagingDto getMissionList(Pageable pageable, MissionSearchCondition missionSearchCondition) {

        return new MissionPagingDto(missionRepository.search(missionSearchCondition, pageable));
    }
}

