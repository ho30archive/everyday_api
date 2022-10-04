package com.everyday.api.domain.mission.service;

import static org.junit.jupiter.api.Assertions.*;

import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.comment.dto.CommentInfoDto;
import com.everyday.api.domain.comment.repository.CommentRepository;
import com.everyday.api.domain.member.Member;
import com.everyday.api.domain.member.Role;
import com.everyday.api.domain.member.dto.MemberSignUpDto;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.everyday.api.domain.member.service.MemberService;
import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.cond.MissionSearchCondition;
import com.everyday.api.domain.mission.dto.MissionInfoDto;
import com.everyday.api.domain.mission.dto.MissionPagingDto;
import com.everyday.api.domain.mission.dto.MissionSaveDto;
import com.everyday.api.domain.mission.dto.MissionUpdateDto;
import com.everyday.api.domain.mission.exception.MissionException;
import com.everyday.api.domain.mission.repository.MissionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class MissionServiceImplTest {


    @Autowired private EntityManager em;

    @Autowired
    private MissionService missionService;

    @Autowired
    private MemberService memberService;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "PASSWORD123@@@";


    private String title = "제목";
    private String content = "내용";


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private CommentRepository commentRepository;




    private void clear(){
        em.flush();
        em.clear();
    }

    private void deleteFile(String filePath) {
        File files = new File(filePath);
        files.delete();
    }

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg", new FileInputStream("/Users/hoyun/Downloads/022.jpeg"));
    }



    @BeforeEach
    private void signUpAndSetAuthentication() throws Exception {
        memberService.signUp(new MemberSignUpDto(USERNAME,PASSWORD,"name","email@abc.com"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }




    @Test
    public void 미션_저장_성공_업로드_파일_없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title, content, Optional.empty());


        //when
        missionService.save(missionSaveDto);
        clear();


        //then
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        Mission mission = em.find(Mission.class, findMission.getId());
        assertThat(mission.getContent()).isEqualTo(content);
        assertThat(mission.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(mission.getFilePath()).isNull();
    }


    @Test
    public void 미션_저장_성공_업로드_파일_있음() throws Exception {
        //given

        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title,content, Optional.ofNullable(getMockUploadFile()));

        //when
        missionService.save(missionSaveDto);
        clear();

        //then
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        Mission mission = em.find(Mission.class, findMission.getId());
        assertThat(mission.getContent()).isEqualTo(content);
        assertThat(mission.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(mission.getFilePath()).isNotNull();

        deleteFile(mission.getFilePath());
        //올린 파일 삭제
    }



    @Test
    public void 미션_저장_실패_제목이나_내용이_없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";

        MissionSaveDto missionSaveDto = new MissionSaveDto(null,content, Optional.empty());
        MissionSaveDto missionSaveDto2 = new MissionSaveDto(title,null, Optional.empty());


        //when,then
        assertThrows(Exception.class, () -> missionService.save(missionSaveDto));
        assertThrows(Exception.class, () -> missionService.save(missionSaveDto2));

    }



    @Test
    public void 미션_업데이트_성공_업로드파일_없음TO없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title,content, Optional.empty());
        missionService.save(missionSaveDto);
        clear();

        //when
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        MissionUpdateDto missionUpdateDto = new MissionUpdateDto(Optional.ofNullable("바꾼제목"),Optional.ofNullable("바꾼내용"), Optional.empty());
        missionService.update(findMission.getId(),missionUpdateDto);
        clear();

        //then
        Mission mission = em.find(Mission.class, findMission.getId());
        assertThat(mission.getContent()).isEqualTo("바꾼내용");
        assertThat(mission.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(mission.getFilePath()).isNull();

    }

    @Test
    public void 미션_업데이트_성공_업로드파일_없음TO있음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title,content, Optional.empty());
        missionService.save(missionSaveDto);
        clear();


        //when
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();

        MissionUpdateDto missionUpdateDto = new MissionUpdateDto(Optional.ofNullable("바꾼제목"),Optional.ofNullable("바꾼내용"), Optional.ofNullable(getMockUploadFile()));
        missionService.update(findMission.getId(),missionUpdateDto);
        clear();


        //then
        Mission mission = em.find(Mission.class, findMission.getId());
        assertThat(mission.getContent()).isEqualTo("바꾼내용");
        assertThat(mission.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(mission.getFilePath()).isNotNull();

        deleteFile(mission.getFilePath());
        //올린 파일 삭제
    }


    @Test
    public void 미션_업데이트_성공_업로드파일_있음TO없음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title,content, Optional.ofNullable(getMockUploadFile()));
        missionService.save(missionSaveDto);

        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        assertThat(findMission.getFilePath()).isNotNull();
        clear();

        //when
        MissionUpdateDto missionUpdateDto = new MissionUpdateDto(Optional.ofNullable("바꾼제목"),Optional.ofNullable("바꾼내용"), Optional.empty());
        missionService.update(findMission.getId(),missionUpdateDto);
        clear();

        //then
        findMission = em.find(Mission.class, findMission.getId());
        assertThat(findMission.getContent()).isEqualTo("바꾼내용");
        assertThat(findMission.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(findMission.getFilePath()).isNull();
    }


    @Test
    public void 미션_업데이트_성공_업로드파일_있음TO있음() throws Exception {
        //given
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title,content, Optional.empty());
        missionService.save(missionSaveDto);

        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        Mission mission = em.find(Mission.class, findMission.getId());
        String filePath = mission.getFilePath();
        clear();

        //when
        MissionUpdateDto missionUpdateDto = new MissionUpdateDto(Optional.ofNullable("바꾼제목"),Optional.ofNullable("바꾼내용"), Optional.ofNullable(getMockUploadFile()));
        missionService.update(findMission.getId(),missionUpdateDto);
        clear();

        //then
        mission = em.find(Mission.class, findMission.getId());
        assertThat(mission.getContent()).isEqualTo("바꾼내용");
        assertThat(mission.getWriter().getUsername()).isEqualTo(USERNAME);
        assertThat(mission.getFilePath()).isNotEqualTo(filePath);
        deleteFile(mission.getFilePath());
        //올린 파일 삭제
    }




    private void setAnotherAuthentication() throws Exception {
        memberService.signUp(new MemberSignUpDto(USERNAME+"123",PASSWORD,"name","email@abc.com"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME+"123")
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }


    @Test
    public void 미션_업데이트_실패_권한이없음() throws Exception {
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title, content, Optional.empty());

        missionService.save(missionSaveDto);
        clear();


        //when, then
        setAnotherAuthentication();
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        MissionUpdateDto missionUpdateDto = new MissionUpdateDto(Optional.ofNullable("바꾼제목"),Optional.ofNullable("바꾼내용"), Optional.empty());


        assertThrows(MissionException.class, ()-> missionService.update(findMission.getId(),missionUpdateDto));

    }


    @Test
    public void 미션삭제_성공() throws Exception {
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title, content, Optional.empty());
        missionService.save(missionSaveDto);
        clear();

        //when
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        missionService.delete(findMission.getId());

        //then
        List<Mission> findMissions = em.createQuery("select m from Mission m", Mission.class).getResultList();
        assertThat(findMissions.size()).isEqualTo(0);
    }


    @Test
    public void 미션삭제_실패() throws Exception {
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title, content, Optional.empty());

        missionService.save(missionSaveDto);
        clear();

        //when, then
        setAnotherAuthentication();
        Mission findMission = em.createQuery("select m from Mission m", Mission.class).getSingleResult();
        assertThrows(MissionException.class, ()-> missionService.delete(findMission.getId()));
    }


    @Test
    public void 포스트_조회() throws Exception {
        Member member1 = memberRepository.save(Member.builder().username("username1").password("1234567890").name("USER1").email("USER1@abc.com").role(Role.USER).build());
        Member member2 = memberRepository.save(Member.builder().username("username2").password("1234567890").name("USER1").email("USER2@abc.com").role(Role.USER).build());
        Member member3 = memberRepository.save(Member.builder().username("username3").password("1234567890").name("USER1").email("USER3@abc.com").role(Role.USER).build());
        Member member4 = memberRepository.save(Member.builder().username("username4").password("1234567890").name("USER1").email("USER4@abc.com").role(Role.USER).build());
        Member member5 = memberRepository.save(Member.builder().username("username5").password("1234567890").name("USER1").email("USER5@abc.com").role(Role.USER).build());

        Map<Integer, Long> memberIdMap = new HashMap<>();
        memberIdMap.put(1,member1.getId());
        memberIdMap.put(2,member2.getId());
        memberIdMap.put(3,member4.getId());
        memberIdMap.put(4,member4.getId());
        memberIdMap.put(5,member5.getId());



        /**
         * Post 생성
         */

        Mission mission = Mission.builder().title("게시글").content("내용").build();
        mission.confirmWriter(member1);
        missionRepository.save(mission);
        em.flush();


        /**
         * Comment 생성(댓글)
         */

        final int COMMENT_COUNT = 10;

        for(int i = 1; i<=COMMENT_COUNT; i++ ){
            Comment comment = Comment.builder().content("댓글" + i).build();
            comment.confirmWriter(memberRepository.findById(memberIdMap.get(i % 3 + 1)).orElse(null));
            comment.confirmMission(mission);
            commentRepository.save(comment);
        }






        /**
         * ReComment 생성(대댓글)
         */
        final int COMMENT_PER_RECOMMENT_COUNT = 20;
        commentRepository.findAll().stream().forEach(comment -> {

            for(int i = 1; i<=20; i++ ){
                Comment recomment = Comment.builder().content("대댓글" + i).build();
                recomment.confirmWriter(memberRepository.findById(memberIdMap.get(i % 3 + 1)).orElse(null));

                recomment.confirmMission(comment.getMission());
                recomment.confirmParent(comment);
                commentRepository.save(recomment);
            }

        });



        clear();




        //when
        MissionInfoDto missionInfo = missionService.getMissionInfo(mission.getId());



        //then
        assertThat(missionInfo.getMissionId()).isEqualTo(mission.getId());
        assertThat(missionInfo.getContent()).isEqualTo(mission.getContent());
        assertThat(missionInfo.getWriterDto().getUsername()).isEqualTo(mission.getWriter().getUsername());


        int recommentCount = 0;
        for (CommentInfoDto commentInfoDto : missionInfo.getCommentInfoDtoList()) {
            recommentCount += commentInfoDto.getReCommentListDtoList().size();
        }

        assertThat(missionInfo.getCommentInfoDtoList().size()).isEqualTo(COMMENT_COUNT);
        assertThat(recommentCount).isEqualTo(COMMENT_PER_RECOMMENT_COUNT * COMMENT_COUNT);

    }


    @Test
    public void 미션_검색_조건없음() throws Exception {
        //given

        /**
         * MEMBER 저장
         */

        Member member1 = memberRepository.save(Member.builder().username("username1").password("1234567890").name("USER1").email("USER1@abc.com").role(Role.USER).build());



        /**
         * Mission 생성
         */
        final int MISSION_COUNT = 50;
        for(int i = 1; i<= MISSION_COUNT; i++ ){
            Mission mission = Mission.builder().title("게시글"+ i).content("내용"+i).build();
            mission.confirmWriter(member1);
            missionRepository.save(mission);
        }

        clear();



        //when
        final int PAGE = 0;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        MissionSearchCondition missionSearchCondition = new MissionSearchCondition();

        MissionPagingDto missionList = missionService.getMissionList(pageRequest, missionSearchCondition);


        //then
        assertThat(missionList.getTotalElementCount()).isEqualTo(MISSION_COUNT);

        assertThat(missionList.getTotalPageCount()).isEqualTo((MISSION_COUNT % SIZE == 0)
                ? MISSION_COUNT/SIZE
                : MISSION_COUNT/SIZE + 1);

        assertThat(missionList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(missionList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }




}