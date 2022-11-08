package com.everyday.api.domain.mission.dto;


import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.comment.dto.CommentInfoDto;
import com.everyday.api.domain.member.dto.MemberInfoDto;
import com.everyday.api.domain.mission.Mission;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor

public class MissionInfoDto{


    private Long missionId; //MISSION의 ID
    private String title;//제목
    private String content;//내용
    private String filePath;//업로드 파일 경로

    private MemberInfoDto writerDto;//작성자에 대한 정보


    private List<CommentInfoDto> commentInfoDtoList;//댓글 정보들




    public MissionInfoDto(Mission mission) {

        this.missionId = mission.getId();
        this.title = mission.getTitle();
        this.content = mission.getContent();
//        this.filePath = mission.getFilePath();


        this.writerDto = new MemberInfoDto(mission.getWriter());




        /**
         * 댓글과 대댓글을 그룹짓기
         * post.getCommentList()는 댓글과 대댓글이 모두 조회된다.
         */

        Map<Comment, List<Comment>> commentListMap = mission.getCommentList().stream()

                .filter(comment -> comment.getParent() != null)

                .collect(Collectors.groupingBy(Comment::getParent));






        /**
         * 댓글과 대댓글을 통해 CommentInfoDto 생성
         */

        commentInfoDtoList = commentListMap.keySet().stream()

                .map(comment -> new CommentInfoDto(comment, commentListMap.get(comment)))
                .toList();

    }
}

