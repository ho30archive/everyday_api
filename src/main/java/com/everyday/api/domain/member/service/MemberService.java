package com.everyday.api.domain.member.service;

import com.everyday.api.domain.member.dto.MemberInfoDto;
import com.everyday.api.domain.member.dto.MemberSignUpDto;
import com.everyday.api.domain.member.dto.MemberUpdateDto;
import org.springframework.stereotype.Service;



public interface MemberService {

    /**
     * 회원가입
     * 정보수정
     * 회원탈퇴
     * 정보조회
     */

    void signUp(MemberSignUpDto memberSignUpDto) throws Exception;

    void update(MemberUpdateDto memberUpdateDto) throws Exception;

    void updatePassword(String checkPassword, String toBePassword) throws Exception;

    void withdraw(String checkPassword) throws Exception;

    MemberInfoDto getInfo(Long id) throws Exception;

    MemberInfoDto getMyInfo() throws Exception;
}
