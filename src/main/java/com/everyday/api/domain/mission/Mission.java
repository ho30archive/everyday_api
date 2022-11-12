package com.everyday.api.domain.mission;

import com.everyday.api.domain.BaseTimeEntity;
import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Table(name = "MISSION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Mission extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "mission_id")
    private Long id;


    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;


    @Column(length = 40, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private String filePath;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;


    @Builder
    public Mission(String title, String content, Date endDate) {
        this.title = title;
        this.content = content;
        this.endDate = endDate;
    }


    //== 게시글을 삭제하면 달려있는 댓글 모두 삭제 ==//
    @OneToMany(mappedBy = "mission", cascade = ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();




    //== 연관관계 편의 메서드 ==//

    public void confirmWriter(Member writer) {
        //writer는 변경이 불가능하므로 이렇게만 해주어도 될듯
        this.writer = writer;
        writer.addMission(this);
    }

    public void addComment(Comment comment){
        //comment의 Mission 설정은 comment에서 함
        commentList.add(comment);
    }



    //== 내용 수정 ==//
    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }
}