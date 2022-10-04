package com.everyday.api.domain.member;

import com.everyday.api.domain.BaseTimeEntity;
import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.mission.Mission;
import lombok.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.*;
import static javax.persistence.GenerationType.*;

@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id; //primary Key

    @Column(nullable = false, length = 30, unique = true)
    private String username;//아이디

    private String password;//비밀번호

    @Column(nullable = false, length = 30)
    private String name;//이름(실명)

    @Column(nullable = false, length = 30)
    private String email;//이메일


    @Enumerated(STRING)
    @Column(nullable = false, length = 30)
    private Role role;//권한 -> USER, ADMIN


    @Column(length = 1000)
    private String refreshToken;//RefreshToken


    //== 회원탈퇴 -> 작성한 게시물, 댓글 모두 삭제 ==//
    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Mission> missionList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();






    //== 연관관계 메서드 ==//
    public void addMission(Mission mission){
        //post의 writer 설정은 post에서 함
        missionList.add(mission);
    }

    public void addComment(Comment comment){
        //comment의 writer 설정은 comment에서 함
        commentList.add(comment);
    }









    //== 정보 수정 ==//
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateEmail(String email){
        this.email = email;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public void destroyRefreshToken(){
        this.refreshToken = null;
    }



    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }


    //비밀번호 변경, 회원 탈퇴 시, 비밀번호를 확인하며, 이때 비밀번호의 일치여부를 판단하는 메서드입니다.
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
        return passwordEncoder.matches(checkPassword, getPassword());
    }


    //회원가입시, USER의 권한을 부여하는 메서드입니다.
    public void addUserAuthority() {
        this.role = Role.USER;
    }
}