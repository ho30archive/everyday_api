package com.everyday.api.domain.comment.service;

import static org.junit.jupiter.api.Assertions.*;


import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.comment.dto.CommentSaveDto;
import com.everyday.api.domain.comment.dto.CommentUpdateDto;
import com.everyday.api.domain.comment.exception.CommentException;
import com.everyday.api.domain.comment.exception.CommentExceptionType;
import com.everyday.api.domain.comment.repository.CommentRepository;
import com.everyday.api.domain.member.Role;
import com.everyday.api.domain.member.dto.MemberSignUpDto;
import com.everyday.api.domain.member.service.MemberService;
import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.dto.MissionSaveDto;
import com.everyday.api.domain.mission.exception.MissionException;
import com.everyday.api.domain.mission.exception.MissionExceptionType;
import com.everyday.api.domain.mission.repository.MissionRepository;
import com.everyday.api.global.exception.BaseExceptionType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {


    @Autowired
    CommentService commentService;

    @Autowired CommentRepository commentRepository;

    @Autowired
    MissionRepository missionRepository;
    @Autowired
    MemberService memberService;

    @Autowired EntityManager em;

    private void clear(){
        em.flush();
        em.clear();
    }

    private void deleteFile(String filePath) {
        File files = new File(filePath);
        files.delete();
    }

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg", new FileInputStream("/Users/hoyun/Downloads/022.jpg"));
    }

    @BeforeEach
    private void signUpAndSetAuthentication() throws Exception {
        memberService.signUp(new MemberSignUpDto("USERNAME","PASSWORD","name","email@abc.com"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username("USERNAME")
                                .password("PASSWORD")
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void anotherSignUpAndSetAuthentication() throws Exception {
        memberService.signUp(new MemberSignUpDto("USERNAME1","PASSWORD123","name","email@abc.com"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username("USERNAME1")
                                .password("PASSWORD123")
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }


    private Long saveMission(){
        String title = "??????";
        String content = "??????";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title, content, Optional.empty());


        //when
        Mission save = missionRepository.save(missionSaveDto.toEntity());
        clear();
        return save.getId();
    }


    private Long saveComment(){
        CommentSaveDto commentSaveDto = new CommentSaveDto("??????", Optional.empty());
        commentService.save(saveMission(),commentSaveDto);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }


    private Long saveReComment(Long parentId){

        CommentSaveDto commentSaveDto = new CommentSaveDto("?????????", Optional.empty());
        commentService.saveReComment(saveMission(),parentId,commentSaveDto);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    //?????? ?????? ???????????? ?????????
    private Comment findComment() {
        return em.createQuery("select c from Comment c", Comment.class).getSingleResult();
    }





    @Test
    public void ????????????_??????_?????????_??????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        CommentSaveDto commentSaveDto = new CommentSaveDto("??????", Optional.empty());

        //when

        commentService.save(missionId,commentSaveDto);
        clear();

        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);


        //?????? ?????? ???????????? ?????????
        Comment findComment = findComment();
        Comment comment = em.find(Comment.class, findComment.getId());
        assertThat(comment.getFilePath()).isNull();
    }

    @Test
    public void ????????????_??????_?????????_??????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        CommentSaveDto commentSaveDto = new CommentSaveDto("??????", Optional.ofNullable(getMockUploadFile()));

        //when

        commentService.save(missionId,commentSaveDto);
        clear();

        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);

        //?????? ?????? ???????????? ?????????
        Comment findComment = findComment();
        Comment comment = em.find(Comment.class, findComment.getId());
        assertThat(comment.getFilePath()).isNotNull();

        //?????? ?????? ??????
        deleteFile(comment.getFilePath());

    }



    @Test
    public void ???????????????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("?????????", Optional.empty());

        //when
        commentService.saveReComment(missionId,parentId,commentSaveDto);
        clear();


        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(2);

    }


    @Test
    public void ????????????_??????_????????????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        CommentSaveDto commentSaveDto = new CommentSaveDto("??????", Optional.empty());

        //when, then

        assertThat(assertThrows(MissionException.class, () -> commentService.save(missionId+1,commentSaveDto)).getExceptionType()).isEqualTo(MissionExceptionType.MISSION_NOT_POUND);



    }

    @Test
    public void ???????????????_??????_????????????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("??????", Optional.empty());

        //when, then

        assertThat(assertThrows(MissionException.class, () -> commentService.saveReComment(missionId+123, parentId,commentSaveDto)).getExceptionType()).isEqualTo(MissionExceptionType.MISSION_NOT_POUND);

    }

    @Test
    public void ???????????????_??????_?????????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("??????", Optional.empty());

        //when, then

        assertThat(assertThrows(CommentException.class, () -> commentService.saveReComment(missionId, parentId+1,commentSaveDto)).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT);

    }

    @Test
    public void ????????????_??????_???????????????_??????TO??????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        //when
//        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("????????????")));
        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("????????????"), Optional.empty()));
        clear();

        //then
        Comment comment = commentRepository.findById(reCommentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("????????????");



        assertThat(comment.getFilePath()).isNull();

    }

    @Test
    public void ????????????_??????_???????????????_??????TO??????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        //when
//        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("????????????")));
        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("????????????"), Optional.ofNullable(getMockUploadFile())));
        clear();

        //then
        Comment comment = commentRepository.findById(reCommentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("????????????");



        assertThat(comment.getFilePath()).isNotNull();
        //?????? ?????? ??????
        deleteFile(comment.getFilePath());

    }






    @Test
    public void ????????????_??????_???????????????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        anotherSignUpAndSetAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("????????????"), Optional.empty()))).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);


    }

    @Test
    public void ????????????_??????_?????????_??????() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        anotherSignUpAndSetAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.remove(reCommentId)).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
    }




    // ????????? ???????????? ??????
    // ???????????? ???????????? ??????
    // DB??? ??????????????? ???????????? ??????, "????????? ???????????????"?????? ??????
    @Test
    public void ????????????_????????????_????????????_??????() throws Exception {

        //given
        Long commentId = saveComment();
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);

        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(4);

        //when
        commentService.remove(commentId);
        clear();


        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT));
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(4);
    }




    // ????????? ???????????? ??????
    //???????????? ?????? ???????????? ?????? ?????? : ????????? DB?????? ??????
    @Test
    public void ????????????_????????????_??????_??????() throws Exception {
        //given
        Long commentId = saveComment();

        //when
        commentService.remove(commentId);
        clear();

        //then
        Assertions.assertThat(commentRepository.findAll().size()).isSameAs(0);
        assertThat(assertThrows(CommentException.class, () ->commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT);
    }




    // ????????? ???????????? ??????
    // ???????????? ???????????? ?????? ????????? ??????
    //?????????, ???????????? ????????? ?????? DB?????? ?????? ??????, ??????????????? ???????????? ??????
    @Test
    public void ????????????_????????????_????????????_??????_?????????_????????????_??????() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);
        Long reCommend2Id = saveReComment(commentId);
        Long reCommend3Id = saveReComment(commentId);
        Long reCommend4Id = saveReComment(commentId);


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(4);
        clear();

        commentService.remove(reCommend1Id);
        clear();

        commentService.remove(reCommend2Id);
        clear();

        commentService.remove(reCommend3Id);
        clear();

        commentService.remove(reCommend4Id);
        clear();


        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend3Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend4Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        clear();


        //when
        commentService.remove(commentId);
        clear();


        //then
        LongStream.rangeClosed(commentId, reCommend4Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT)
        );

    }





    // ???????????? ???????????? ??????
    // ?????? ????????? ???????????? ?????? ??????
    // ????????? ??????, DB????????? ?????? X
    @Test
    public void ???????????????_???????????????_????????????_??????() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);


        //when
        commentService.remove(reCommend1Id);
        clear();


        //then
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isFalse();
        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
    }



    // ???????????? ???????????? ??????
    // ?????? ????????? ??????????????????, ??????????????? ?????? ????????? ??????
    // ????????? ????????? ?????? ???????????? DB?????? ?????? ??????, ?????????????????? ??????
    @Test
    public void ???????????????_???????????????_?????????_??????_??????_????????????_?????????_??????() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);
        Long reCommend2Id = saveReComment(commentId);
        Long reCommend3Id = saveReComment(commentId);


        commentService.remove(reCommend2Id);
        clear();
        commentService.remove(commentId);
        clear();
        commentService.remove(reCommend3Id);
        clear();


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(3);

        //when
        commentService.remove(reCommend1Id);



        //then
        LongStream.rangeClosed(commentId, reCommend3Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT)
        );



    }


    // ???????????? ???????????? ??????
    // ?????? ????????? ??????????????????, ?????? ???????????? ?????? ???????????? ?????? ???????????? ??????
    //?????? ???????????? ??????, ????????? DB?????? ??????????????? ??????, ??????????????? "????????? ???????????????"?????? ??????
    @Test
    public void ???????????????_???????????????_?????????_??????_??????_????????????_????????????_??????() throws Exception {
        //given
        Long commentId = saveComment();
        Long reCommend1Id = saveReComment(commentId);
        Long reCommend2Id = saveReComment(commentId);
        Long reCommend3Id = saveReComment(commentId);


        commentService.remove(reCommend3Id);
        commentService.remove(commentId);
        clear();

        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getChildList().size()).isEqualTo(3);


        //when
        commentService.remove(reCommend2Id);
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();


        //then
        Assertions.assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(reCommend2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reCommend1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getId()).isNotNull();
        Assertions.assertThat(commentRepository.findById(reCommend3Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getId()).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)).getId()).isNotNull();

    }

}