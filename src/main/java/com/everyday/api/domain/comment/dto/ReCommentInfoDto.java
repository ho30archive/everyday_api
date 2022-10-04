package com.everyday.api.domain.comment.dto;

import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.member.dto.MemberInfoDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReCommentInfoDto  {


    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다";

    private Long missionId;
    private Long parentId;


    private Long reCommentId;
    private String content;
    private boolean isRemoved;


    private MemberInfoDto writerDto;

    public ReCommentInfoDto(Comment reComment) {
        this.missionId = reComment.getMission().getId();
        this.parentId = reComment.getParent().getId();
        this.reCommentId = reComment.getId();
        this.content = reComment.getContent();

        if(reComment.isRemoved()){
            this.content = DEFAULT_DELETE_MESSAGE;
        }

        this.isRemoved = reComment.isRemoved();
        this.writerDto = new MemberInfoDto(reComment.getWriter());
    }
}
