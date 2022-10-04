package com.everyday.api.domain.member.dto;

import com.everyday.api.domain.member.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberInfoDto {

    private String name;
    private String email;
    private String username;




    @Builder
    public MemberInfoDto(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.username = member.getUsername();
    }
}