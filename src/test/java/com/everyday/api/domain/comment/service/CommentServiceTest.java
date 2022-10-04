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
        String title = "제목";
        String content = "내용";
        MissionSaveDto missionSaveDto = new MissionSaveDto(title, content, Optional.empty());


        //when
        Mission save = missionRepository.save(missionSaveDto.toEntity());
        clear();
        return save.getId();
    }


    private Long saveComment(){
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글", Optional.empty());
        commentService.save(saveMission(),commentSaveDto);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }


    private Long saveReComment(Long parentId){

        CommentSaveDto commentSaveDto = new CommentSaveDto("대댓글", Optional.empty());
        commentService.saveReComment(saveMission(),parentId,commentSaveDto);
        clear();

        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    //파일 저장 테스트를 위한것
    private Comment findComment() {
        return em.createQuery("select c from Comment c", Comment.class).getSingleResult();
    }





    @Test
    public void 댓글저장_성공_업로드_파일_없음() throws Exception {
        //given
        Long missionId = saveMission();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글", Optional.empty());

        //when

        commentService.save(missionId,commentSaveDto);
        clear();

        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);


        //파일 저장 테스트를 위한것
        Comment findComment = findComment();
        Comment comment = em.find(Comment.class, findComment.getId());
        assertThat(comment.getFilePath()).isNull();
    }

    @Test
    public void 댓글저장_성공_업로드_파일_있음() throws Exception {
        //given
        Long missionId = saveMission();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글", Optional.ofNullable(getMockUploadFile()));

        //when

        commentService.save(missionId,commentSaveDto);
        clear();

        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(1);

        //파일 저장 테스트를 위한것
        Comment findComment = findComment();
        Comment comment = em.find(Comment.class, findComment.getId());
        assertThat(comment.getFilePath()).isNotNull();

        //올린 파일 삭제
        deleteFile(comment.getFilePath());

    }



    @Test
    public void 대댓글저장_성공() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("대댓글", Optional.empty());

        //when
        commentService.saveReComment(missionId,parentId,commentSaveDto);
        clear();


        //then
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        assertThat(resultList.size()).isEqualTo(2);

    }


    @Test
    public void 댓글저장_실패_게시물이_없음() throws Exception {
        //given
        Long missionId = saveMission();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글", Optional.empty());

        //when, then

        assertThat(assertThrows(MissionException.class, () -> commentService.save(missionId+1,commentSaveDto)).getExceptionType()).isEqualTo(MissionExceptionType.MISSION_NOT_POUND);



    }

    @Test
    public void 대댓글저장_실패_게시물이_없음() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글", Optional.empty());

        //when, then

        assertThat(assertThrows(MissionException.class, () -> commentService.saveReComment(missionId+123, parentId,commentSaveDto)).getExceptionType()).isEqualTo(MissionExceptionType.MISSION_NOT_POUND);

    }

    @Test
    public void 대댓글저장_실패_댓글이_없음() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글", Optional.empty());

        //when, then

        assertThat(assertThrows(CommentException.class, () -> commentService.saveReComment(missionId, parentId+1,commentSaveDto)).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT);

    }

    @Test
    public void 업데이트_성공_업로드파일_없음TO없음() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        //when
//        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("업데이트")));
        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("업데이트"), Optional.empty()));
        clear();

        //then
        Comment comment = commentRepository.findById(reCommentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("업데이트");



        assertThat(comment.getFilePath()).isNull();

    }

    @Test
    public void 업데이트_성공_업로드파일_없음TO있음() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        //when
//        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("업데이트")));
        commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("업데이트"), Optional.ofNullable(getMockUploadFile())));
        clear();

        //then
        Comment comment = commentRepository.findById(reCommentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("업데이트");



        assertThat(comment.getFilePath()).isNotNull();
        //올린 파일 삭제
        deleteFile(comment.getFilePath());

    }






    @Test
    public void 업데이트_실패_권한이없음() throws Exception {
        //given
        Long missionId = saveMission();
        Long parentId = saveComment();
        Long reCommentId = saveReComment(parentId);
        clear();

        anotherSignUpAndSetAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.update(reCommentId, new CommentUpdateDto(Optional.ofNullable("업데이트"), Optional.empty()))).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);


    }

    @Test
    public void 댓글삭제_실패_권한이_없음() throws Exception {
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




    // 댓글을 삭제하는 경우
    // 대댓글이 남아있는 경우
    // DB와 화면에서는 지워지지 않고, "삭제된 댓글입니다"라고 표시
    @Test
    public void 댓글삭제_대댓글이_남아있는_경우() throws Exception {

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




    // 댓글을 삭제하는 경우
    //대댓글이 아예 존재하지 않는 경우 : 곧바로 DB에서 삭제
    @Test
    public void 댓글삭제_대댓글이_없는_경우() throws Exception {
        //given
        Long commentId = saveComment();

        //when
        commentService.remove(commentId);
        clear();

        //then
        Assertions.assertThat(commentRepository.findAll().size()).isSameAs(0);
        assertThat(assertThrows(CommentException.class, () ->commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_POUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_POUND_COMMENT);
    }




    // 댓글을 삭제하는 경우
    // 대댓글이 존재하나 모두 삭제된 경우
    //댓글과, 달려있는 대댓글 모두 DB에서 일괄 삭제, 화면상에도 표시되지 않음
    @Test
    public void 댓글삭제_대댓글이_존재하나_모두_삭제된_대댓글인_경우() throws Exception {
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





    // 대댓글을 삭제하는 경우
    // 부모 댓글이 삭제되지 않은 경우
    // 내용만 삭제, DB에서는 삭제 X
    @Test
    public void 대댓글삭제_부모댓글이_남아있는_경우() throws Exception {
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



    // 대댓글을 삭제하는 경우
    // 부모 댓글이 삭제되어있고, 대댓글들도 모두 삭제된 경우
    // 부모를 포함한 모든 대댓글을 DB에서 일괄 삭제, 화면상에서도 지움
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_모든_대댓글이_삭제된_경우() throws Exception {
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


    // 대댓글을 삭제하는 경우
    // 부모 댓글이 삭제되어있고, 다른 대댓글이 아직 삭제되지 않고 남아있는 경우
    //해당 대댓글만 삭제, 그러나 DB에서 삭제되지는 않고, 화면상에는 "삭제된 댓글입니다"라고 표시
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_다른_대댓글이_남아있는_경우() throws Exception {
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