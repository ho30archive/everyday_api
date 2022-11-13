package com.everyday.api.domain.comment.service;


import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.comment.dto.CommentSaveDto;
import com.everyday.api.domain.comment.dto.CommentUpdateDto;
import com.everyday.api.domain.comment.exception.CommentException;
import com.everyday.api.domain.comment.exception.CommentExceptionType;
import com.everyday.api.domain.comment.repository.CommentRepository;
import com.everyday.api.domain.member.exception.MemberException;
import com.everyday.api.domain.member.exception.MemberExceptionType;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.everyday.api.domain.mission.exception.MissionException;
import com.everyday.api.domain.mission.exception.MissionExceptionType;
import com.everyday.api.domain.mission.repository.MissionRepository;
import com.everyday.api.global.file.exception.FileException;
import com.everyday.api.global.file.service.FileService;
import com.everyday.api.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;

    private final FileService fileService;

    @Override
    public void save(Long missionId, CommentSaveDto commentSaveDto) throws FileException {
        Comment comment = commentSaveDto.toEntity();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        comment.confirmMission(missionRepository.findById(missionId).orElseThrow(() -> new MissionException(MissionExceptionType.MISSION_NOT_POUND)));

        commentSaveDto.uploadFile().ifPresent(
                file -> comment.updateFilePath(fileService.save(file))
        );


        commentRepository.save(comment);

    }

    @Override
    public void saveReComment(Long missionId, Long parentId, CommentSaveDto commentSaveDto) {
        Comment comment = commentSaveDto.toEntity();

        comment.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)));

        comment.confirmMission(missionRepository.findById(missionId).orElseThrow(() -> new MissionException(MissionExceptionType.MISSION_NOT_POUND)));

        comment.confirmParent(commentRepository.findById(parentId).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_POUND_COMMENT)));

        commentRepository.save(comment);

    }



    @Override
    public void update(Long id, CommentUpdateDto commentUpdateDto) {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentException(CommentExceptionType.NOT_POUND_COMMENT));
        if(!comment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())){
            throw new CommentException(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);
        }

        if(comment.getFilePath() !=null){
            fileService.delete(comment.getFilePath());//기존에 올린 파일 지우기
        }

        commentUpdateDto.uploadFile().ifPresentOrElse(
                multipartFile ->  comment.updateFilePath(fileService.save(multipartFile)),
                () ->  comment.updateFilePath(null)
        );

        commentUpdateDto.content().ifPresent(comment::updateContent);
    }



    @Override
    public void remove(Long id) throws CommentException {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new CommentException(CommentExceptionType.NOT_POUND_COMMENT));

        if(!comment.getWriter().getUsername().equals(SecurityUtil.getLoginUsername())){
            throw new CommentException(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
        }

        if(comment.getFilePath() !=null){
            fileService.delete(comment.getFilePath());//기존에 올린 파일 지우기
        }

        comment.remove();
        List<Comment> removableCommentList = comment.findRemovableList();
        commentRepository.deleteAll(removableCommentList);
    }
}