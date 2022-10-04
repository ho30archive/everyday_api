package com.everyday.api;

import com.everyday.api.domain.comment.Comment;
import com.everyday.api.domain.comment.repository.CommentRepository;
import com.everyday.api.domain.member.Member;
import com.everyday.api.domain.member.Role;
import com.everyday.api.domain.member.repository.MemberRepository;
import com.everyday.api.domain.mission.Mission;
import com.everyday.api.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.String.valueOf;

@RequiredArgsConstructor
@Component
public class InitService {


//    private final Init init;
//
//
//    @PostConstruct
//    public void init(){
//
//        init.save();
//    }
//
//    @RequiredArgsConstructor
//    @Component
//    private static class Init{
//        private final MemberRepository memberRepository;
//
//        private final MissionRepository missionRepository;
//        private final CommentRepository commentRepository;
//
//        @Transactional
//        public void save() {
//            PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//
//
//            //== MEMBER 저장 ==//
//            memberRepository.save(Member.builder().username("username1").password(delegatingPasswordEncoder.encode("1234567890")).name("USER1").email("USER1@abc.com").role(Role.USER).build());
//
//            memberRepository.save(Member.builder().username("username2").password(delegatingPasswordEncoder.encode("1234567890")).name("USER2").email("USER1@abc.com").role(Role.USER).build());
//
//            memberRepository.save(Member.builder().username("username3").password(delegatingPasswordEncoder.encode("1234567890")).name("USER3").email("USER1@abc.com").role(Role.USER).build());
//
//            Member member = memberRepository.findById(1L).orElse(null);
//
//
//            for(int i = 0; i<=50; i++ ){
//                Mission mission = Mission.builder().title(format("게시글 %s", i)).content(format("내용 %s", i)).build();
//                mission.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).orElse(null));
//                missionRepository.save(mission);
//            }
//
//            for(int i = 1; i<=150; i++ ){
//                Comment comment = Comment.builder().content("댓글" + i).build();
//                comment.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).orElse(null));
//
//                comment.confirmMission(missionRepository.findById(parseLong(valueOf(i%50 + 1))).orElse(null));
//                commentRepository.save(comment);
//            }
//
//
//            commentRepository.findAll().stream().forEach(comment -> {
//
//                for(int i = 1; i<=50; i++ ){
//                    Comment recomment = Comment.builder().content("대댓글" + i).build();
//                    recomment.confirmWriter(memberRepository.findById((long) (i % 3 + 1)).orElse(null));
//
//                    recomment.confirmMission(comment.getMission());
//                    recomment.confirmParent(comment);
//                    commentRepository.save(recomment);
//                }
//
//            });
//        }
//    }


}
